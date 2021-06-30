alter table workflows drop column _default;
alter table workflows drop column internal;
alter table grant_type_workflow_mapping drop column internal;
alter table grant_type_workflow_mapping add column _default boolean default true;


--FOR CINI MIGRATION
CREATE OR REPLACE FUNCTION public.cini_disbursement_status(
	oldid bigint,wfIdOld bigint,wfIdNew bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare newId bigint;
begin
 select (select id from workflow_statuses where name=a.name and workflow_id=wfIdNew) into newId
from workflow_statuses a inner join workflows b on b.id=a.workflow_id where b.id=wfIdOld
and a.id = oldId;

 return newId;
end;
$BODY$;

-------------------
CREATE OR REPLACE FUNCTION public.cini_grant_status(
	oldid bigint, wfIdOld bigint,wfIdNew bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare newId bigint;
begin
 select (select id from workflow_statuses where name=a.name and workflow_id=wfIdNew) into newId
from workflow_statuses a inner join workflows b on b.id=a.workflow_id where b.id=wfIdOld
and a.id = oldId;

 return newId;
end;
$BODY$;

-------------------
CREATE OR REPLACE FUNCTION public.cini_report_status(
	oldid bigint, wfIdOld bigint,wfIdNew bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
declare newId bigint;
begin
 select (select id from workflow_statuses where name=a.name and workflow_id=wfIdNew) into newId
from workflow_statuses a inner join workflows b on b.id=a.workflow_id where b.id=wfIdOld
and a.id = oldId;

 return newId;
end;
$BODY$;

-------------------

--GRANTS
insert into workflows(created_at,created_by,description,name,object,granter_id)
select now(),created_by,'Sustainplus Internal Grant Workflow','Sustainplus Internal Grant Workflow',object,granter_id from workflows where id=6;

insert into workflow_statuses(created_at,created_by,display_name,name,initial,terminal,internal_status,verb,workflow_id) select created_at,created_by,display_name,name,initial,terminal,internal_status,verb,17 from workflow_statuses where workflow_id=6;

insert into workflow_status_transitions (action,created_at,created_by,note_required,from_state_id,to_state_id,workflow_id,seq_order)
select action,created_at,created_by,note_required,cini_grant_status(from_state_id,6,17),cini_grant_status(to_state_id,6,17),17,seq_order from workflow_status_transitions where workflow_id=6;

--DISBURSEMENTS
insert into workflows(created_at,created_by,description,name,object,granter_id)
select now(),created_by,'Sustainplus Internal Disbursement Workflow','Sustainplus Internal Disbursement Workflow',object,granter_id from workflows where id=9;

insert into workflow_statuses(created_at,created_by,display_name,name,initial,terminal,internal_status,verb,workflow_id) select created_at,created_by,display_name,name,initial,terminal,internal_status,verb,18 from workflow_statuses where workflow_id=9;

insert into workflow_status_transitions (action,created_at,created_by,note_required,from_state_id,to_state_id,workflow_id,seq_order)
select action,created_at,created_by,note_required,cini_disbursement_status(from_state_id,9,18),cini_disbursement_status(to_state_id,9,18),18,seq_order from workflow_status_transitions where workflow_id=9;


-------Grant type workflow mapping
update grant_type_workflow_mapping set workflow_id=17 where id=11;
update grant_type_workflow_mapping set workflow_id=18 where id=12;
update grant_type_workflow_mapping set _default=true;

---------Functions to get current owners----------
CREATE FUNCTION get_owner_grant(IN grantId bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

AS $BODY$
declare _owner bigint;
begin
select a.assignments into _owner from grant_assignments a
inner join grants b on b.id=a.grant_id where b.grant_status_id=a.state_id
and b.id=grantId;
return _owner;
end;
$BODY$;
---------------
CREATE OR REPLACE FUNCTION get_owner_report(IN reportId bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

AS $BODY$
declare _owner bigint;
begin
select a.assignment into _owner from report_assignments a
inner join reports b on b.id=a.report_id where b.status_id=a.state_id
and b.id=reportId;
return _owner;
end;
$BODY$;

--------------------
CREATE OR REPLACE FUNCTION get_owner_disbursement(IN disbId bigint)
    RETURNS bigint
    LANGUAGE 'plpgsql'

AS $BODY$
declare _owner bigint;
begin
select a.owner into _owner from disbursement_assignments a
inner join disbursements b on b.id=a.disbursement_id where b.status_id=a.state_id
and b.id=disbId;
return _owner;
end;
$BODY$;

--------------------

-------Dummy Statuses and System user
insert into workflows (name) values('ANUDAN SYSTEM');
insert into workflow_statuses(name,display_name,internal_status,initial,terminal,workflow_id) values('SYSTEM','SYSTEM','SYSTEM',true,true,19);
insert into users(email_id,first_name,last_name,active,deleted,plain) values('system@anudan.org','Anudan','Admin',true,false,false);

---MIGRATION
CREATE OR REPLACE PROCEDURE public.migrate_cini(
	)
LANGUAGE 'plpgsql'
AS $BODY$declare grantRow RECORD;
declare grantAssignmentsRow RECORD;
declare reportAssignmentsRow RECORD;
declare disbursementAssignmentsRow RECORD;
declare reportRow RECORD;
declare disbursementRow RECORD;
declare systemUserId BIGINT;
declare systemStatusId BIGINT;
declare oldType VARCHAR(50);
declare newType VARCHAR(50);
declare publishedReportsCount BIGINT;
--STATS
declare grantsStat BIGINT;
declare grantsDraftStat BIGINT;
declare grantsInprogressStat BIGINT;
declare grantsActiveStat BIGINT;
declare reportsStat BIGINT;
declare reportsStatDraftNM BIGINT;
declare reportsStatDraft BIGINT;
declare reportsStatReview BIGINT;
declare reportsStatApproved BIGINT;
declare disbursementsStat BIGINT;
declare disbursementsStatDraft BIGINT;
declare disbursementsStatReview BIGINT;
declare disbursementsStatApproved BIGINT;
declare disbursementsStatClosed BIGINT;

declare reportsProcessedCount BIGINT DEFAULT 0;
declare disbursementsProcessedCount BIGINT DEFAULT 0;

--ENDSTATS
begin

--START: Check if reports exists in PUBLISHED state. Abort if they exist
select count(*) into publishedReportsCount from grants a inner join reports b on b.grant_id=a.id inner join workflow_statuses c on c.id=b.status_id where a.organization_id=69 and c.internal_status='ACTIVE';
IF publishedReportsCount>0 THEN
	RAISE NOTICE 'Found % reports in Published status...Aborting',publishedReportsCount;
	RETURN;
END IF;
--END

--PREP
SELECT id into systemUserId FROM users where email_id='system@anudan.org';
SELECT id into systemStatusId FROM workflow_statuses WHERE workflow_id=19;
SELECT name into oldType from grant_types WHERE id=1;
SELECT name into newType from grant_types WHERE id=4;
--ENDPREP

--INITIAL STATISTICS
select count(*) into grantsStat from grants a inner join grant_types b on b.id=a.grant_type_id where (b.internal=true or (a.organization_id=69)) and b.granter_id=11 and deleted=false;
select count(*) into grantsDraftStat from grants a inner join workflow_statuses b on b.id=a.grant_status_id where (a.organization_id=69 or grant_type_id=4 ) and b.internal_status='DRAFT' and (select count(*) from grant_history where id=a.id)=0 and a.deleted=false;
select count(*) into grantsInprogressStat from grants a inner join workflow_statuses b on b.id=a.grant_status_id where (a.organization_id=69 or grant_type_id=4 ) and ((b.internal_status='DRAFT' and (select count(*) from grant_history where id=a.id)>0) OR (b.internal_status='REVIEW')) and a.deleted=false;
select count(*) into grantsActiveStat from grants a inner join workflow_statuses b on b.id=a.grant_status_id where (a.organization_id=69 or grant_type_id=4 ) and b.internal_status='ACTIVE' and a.deleted=false;
select count(*) into reportsStat from reports a where a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into reportsStatDraftNM from reports a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='DRAFT' and (select count(*) from report_history where id=a.id)=0 and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into reportsStatDraft from reports a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='DRAFT' and (select count(*) from report_history where id=a.id)>0 and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into reportsStatReview from reports a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='REVIEW' and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into reportsStatApproved from reports a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='CLOSED' and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into disbursementsStat from disbursements a where a.grant_id in (select id from grants where organization_id=69 and deleted=false);
select count(*) into disbursementsStatDraft from disbursements a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='DRAFT' and (select count(*) from disbursement_history where id=a.id)=0 and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into disbursementsStatReview from disbursements a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='REVIEW' and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into disbursementsStatApproved from disbursements a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='ACTIVE' and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);
select count(*) into disbursementsStatClosed from disbursements a inner join workflow_statuses b on b.id=a.status_id where b.internal_status='CLOSED' and a.grant_id in (select id from grants where (organization_id=69 or grant_type_id=4 ) and deleted=false);

RAISE NOTICE 'INITIAL STATS';
RAISE NOTICE 'Total Grants: [%], Draft Grants: [%], In-progress Grants: [%], Active Grants: [%]', grantsStat,grantsDraftStat,grantsInprogressStat,grantsActiveStat;
RAISE NOTICE 'Total Reports: [%], Draft Reports(Never Moved): [%], Draft Reports:%, Reports in Review: [%], Approved Reports: [%]', reportsStat,reportsStatDraftNM,reportsStatDraft,reportsStatReview,reportsStatApproved;
RAISE NOTICE 'Total Disbursement Requests: [%], Draft Disbursement Requests: [%], In-progress Disbursement Requests:%, Approved Disbursement Requests: [%], Disbursed Disbursement Requests: [%]', disbursementsStat,disbursementsStatDraft,disbursementsStatReview,disbursementsStatApproved,disbursementsStatClosed;

--ENDSTATISTICS

-- DISABLE TRIGGERS FOR Grants, Reports, Disbursements, Grant Assignments, Report Assignments, Disbursement Assignments
ALTER TABLE grants DISABLE TRIGGER ALL;
ALTER TABLE reports DISABLE TRIGGER ALL;
ALTER TABLE disbursements DISABLE TRIGGER ALL;
ALTER TABLE grant_assignments DISABLE TRIGGER ALL;
ALTER TABLE report_assignments DISABLE TRIGGER ALL;
ALTER TABLE disbursement_assignments DISABLE TRIGGER ALL;
--END


FOR grantRow IN (select a.* from grants a inner join grant_types b on b.id=a.grant_type_id where (b.internal=true or (a.organization_id=69)) and b.granter_id=11 and deleted=false) LOOP
    RAISE NOTICE '-------------------------------------------------------';
    RAISE NOTICE 'PROCESSING GRANT %',grantRow.id;
    RAISE NOTICE '-------------------------------------------------------';
	--START: Update grant type for each grant
	update grants set grant_type_id=4,grant_status_id=cini_grant_status(grant_status_id,6,17),organization_id=11 where id=grantRow.id;
	--[LOG] GrantId, Old Type, New Type
	RAISE NOTICE '[DONE: Grant Type & Status updated] Grant Id: [%], Status Change: %, Type Change: %',grantRow.id,concat(concat((select name from workflow_statuses where id=grantRow.grant_status_id),'-',grantRow.grant_status_id), ' to ', concat((select name from workflow_statuses where id=cini_grant_status(grantRow.grant_status_id,6,17)),'-',cini_grant_status(grantRow.grant_status_id,6,17))),(concat(oldType,' to ',newType));
	--END

	--LOOP: Switch Old Status > New Status for Grant Assignments
	RAISE NOTICE 'PROCESSING GRANT ASSIGNMENTS...';
	FOR grantAssignmentsRow IN (select * from grant_assignments where grant_id=grantRow.id) LOOP
		update grant_assignments set state_id=cini_grant_status(grantAssignmentsRow.state_id,6,17) where id=grantAssignmentsRow.id;
		RAISE NOTICE '		[DONE: Grant Assignment status updated] Grant Id: (%), Assignment Id: (%), Old Status: (%), New Status: (%)',grantRow.id,grantAssignmentsRow.id,concat((select name from workflow_statuses where id=grantAssignmentsRow.state_id),'-',grantAssignmentsRow.state_id),concat((select name from workflow_statuses where id=cini_grant_status(grantAssignmentsRow.state_id,6,17)),'-',cini_grant_status(grantAssignmentsRow.state_id,6,17));
	END LOOP;
	RAISE NOTICE 'GRANT ASSIGNMENTS PROCESSED...';
	--ENDLOOP

	--START: Make 2 entries for Grant History and Snaphot System Receipt + Switch to New Status
	RAISE NOTICE 'PROCESSING GRANT HISTORY...';
	insert into grant_snapshot (grant_id,from_state_id,from_note,moved_by,to_state_id,assigned_by,moved_on) values(grantRow.id,grantRow.grant_status_id,'Control taken by System to perform Grant Type switch for Direct Implementation (As per SustainPlus request)',get_owner_grant(grantRow.id),systemStatusId,get_owner_grant(grantRow.id),clock_timestamp());
	insert into grant_snapshot (grant_id,from_state_id,from_note,moved_by,to_state_id,assigned_by,moved_on) values(grantRow.id,systemStatusId,'Control returned back to user after Grant type switch for Direct Implementation (As per SustainPlus request)',systemUserId,cini_grant_status(grantRow.grant_status_id,6,17),systemUserId,clock_timestamp());
	RAISE NOTICE '[DONE: Grant History updated] Grant Id: % | Moved to System (%) and back to %(%) ',grantRow.id,systemStatusId,(select name from workflow_statuses where id=cini_grant_status(grantRow.grant_status_id,6,17)),cini_grant_status(grantRow.grant_status_id,6,17);
	RAISE NOTICE 'GRANT HISTORY PROCESSED...';
	--END

	--LOOP: Loop through each report of grant
	RAISE NOTICE 'PROCESSING REPORTS...';
	FOR reportRow in (select * from reports where grant_id=grantRow.id) LOOP
	    reportsProcessedCount := reportsProcessedCount +1;
		--START: Switch Old Status to New Status
		RAISE NOTICE 'PROCESSING REPORT STATUS...';
		update reports set status_id=cini_report_status(status_id,7,16) where id=reportRow.id;
		RAISE NOTICE '		[DONE: Report Status Updated] Grant Id: [%], Report Id: [%], Status Change: %',grantRow.id,reportRow.id,concat(concat((select name from workflow_statuses where id=reportRow.status_id),'-',reportRow.status_id), ' to ', concat((select name from workflow_statuses where id=cini_report_status(reportRow.status_id,7,16)),'-',cini_report_status(reportRow.status_id,7,16)));
		RAISE NOTICE 'PROCESSED REPORT STATUS...';
		--END

		--LOOP: Loop through each report assignment
			--START: Switch Old Status to New Status
			RAISE NOTICE 'PROCESSING REPORT ASSIGNMENTS...';
			FOR reportAssignmentsRow IN (select * from report_assignments where report_id=reportRow.id) LOOP
			    IF (cini_report_status(reportAssignmentsRow.state_id,7,16) is null) THEN
			        delete from reports where id=reportAssignmentsRow.id;
			        RAISE NOTICE '			[DONE: External Report Assignment deleted] Grant Id: (%), Report Id: %',grantRow.id,reportRow.id;
			    ELSE
			        update report_assignments set state_id=cini_report_status(reportAssignmentsRow.state_id,7,16) where id=reportAssignmentsRow.id;
                    RAISE NOTICE '			[DONE: Report Assignment Status Updated] Grant Id: (%), Report Id: %, Assignment Id: (%), Old Status: (%), New Status: (%)',grantRow.id,reportRow.id,reportAssignmentsRow.id,concat((select name from workflow_statuses where id=reportAssignmentsRow.state_id),'-',reportAssignmentsRow.state_id),concat((select name from workflow_statuses where id=cini_report_status(reportAssignmentsRow.state_id,7,16)),'-',cini_report_status(reportAssignmentsRow.state_id,7,16));
			    END IF;
			END LOOP;
			RAISE NOTICE 'PROCESSED REPORT ASSIGNMENTS...';
			--END
		--ENDLOOP
		--START: Make 2 entries for Report History and Snaphot System Receipt + Switch to New Status
		    RAISE NOTICE 'PROCESSING REPORT HISTORY...';
			insert into report_snapshot (report_id,from_state_id,from_note,moved_by,to_state_id,assigned_to_id,moved_on) values(reportRow.id,reportRow.status_id,'Control taken by system to perform Grant Type switch for Direct Implementation (As per SustainPlus request)',get_owner_report(reportRow.id),systemStatusId,get_owner_report(reportRow.id),clock_timestamp());
			insert into report_snapshot (report_id,from_state_id,from_note,moved_by,to_state_id,assigned_to_id,moved_on) values(reportRow.id,systemStatusId,'Control returned back to user after Grant type switch for Direct Implementation (As per SustainPlus request)',systemUserId,cini_report_status(reportRow.status_id,7,16),systemUserId,clock_timestamp());
			RAISE NOTICE '		[Report History updated for Grant Type switch] Report Id: % | Moved to System (%) and back to %(%) ',reportRow.id,systemStatusId,(select name from workflow_statuses where id=cini_report_status(reportRow.status_id,7,16)),cini_report_status(reportRow.status_id,7,16);
			RAISE NOTICE 'PROCESSED REPORT HISTORY...';
		--END
	END LOOP;
	RAISE NOTICE 'PROCESSED ALL REPORTS...';
	--ENDLOOP

	----PROCESS DISBURSEMENTS----
	--LOOP: Loop through each report of grant
	    RAISE NOTICE 'PROCESSING DISBURSEMENTS...';
    	FOR disbursementRow in (select * from disbursements where grant_id=grantRow.id) LOOP
    	    disbursementsProcessedCount := disbursementsProcessedCount+1;
    		--START: Switch Old Status to New Status
    		RAISE NOTICE 'PROCESSING DISBURSEMENT STATUS...';
    		update disbursements set status_id=cini_disbursement_status(status_id,9,18) where id=disbursementRow.id;
    		RAISE NOTICE '		[DONE: Disbursement Status Updated] Grant Id: [%], Disbursement Id: [%], Status Change: %',grantRow.id,disbursementRow.id,concat(concat((select name from workflow_statuses where id=disbursementRow.status_id),'-',disbursementRow.status_id), ' to ', concat((select name from workflow_statuses where id=cini_disbursement_status(disbursementRow.status_id,9,18)),'-',cini_disbursement_status(disbursementRow.status_id,9,18)));
    		RAISE NOTICE 'PROCESSED DISBURSEMENT STATUS...';
    		--END

    		--LOOP: Loop through each report assignment
    			--START: Switch Old Status to New Status
    			RAISE NOTICE 'PROCESSING DISBURSEMENT ASSIGNMENTS...';
    			FOR disbursementAssignmentsRow IN (select * from disbursement_assignments where disbursement_id=disbursementRow.id) LOOP
                    update disbursement_assignments set state_id=cini_disbursement_status(disbursementAssignmentsRow.state_id,9,18) where id=disbursementAssignmentsRow.id;
                    RAISE NOTICE '			[DONE: Disbursement Assignment Status Updated] Grant Id: (%), Disbursement Id: %, Assignment Id: (%), Old Status: (%), New Status: (%)',grantRow.id,disbursementRow.id,disbursementAssignmentsRow.id,concat((select name from workflow_statuses where id=disbursementAssignmentsRow.state_id),'-',disbursementAssignmentsRow.state_id),concat((select name from workflow_statuses where id=cini_disbursement_status(disbursementAssignmentsRow.state_id,9,18)),'-',cini_disbursement_status(disbursementAssignmentsRow.state_id,9,18));
    			END LOOP;
    			RAISE NOTICE 'PROCESSED DISBURSEMENT ASSIGNMENTS...';
    			--END
    		--ENDLOOP
    		--START: Make 2 entries for Report History and Snaphot System Receipt + Switch to New Status
    			RAISE NOTICE 'PROCESSING DISBURSEMENT HISTORY...';
    			insert into disbursement_snapshot (disbursement_id,from_state_id,from_note,moved_by,to_state_id,assigned_to_id,moved_on) values(disbursementRow.id,disbursementRow.status_id,'Control taken by System to perform Grant Type switch for Direct Implementation (As per SustainPlus request)',get_owner_disbursement(disbursementRow.id),systemStatusId,get_owner_disbursement(disbursementRow.id),clock_timestamp());
    			insert into disbursement_snapshot (disbursement_id,from_state_id,from_note,moved_by,to_state_id,assigned_to_id,moved_on) values(disbursementRow.id,systemStatusId,'Control returned back to user after Grant type switch for Direct Implementation (As per SustainPlus request)',systemUserId,cini_disbursement_status(disbursementRow.status_id,9,18),systemUserId,clock_timestamp());
    			RAISE NOTICE '		[DONE: Disbursement History updated for Grant Type switch] Disbursement Id: %', disbursementRow.id;
    			RAISE NOTICE '		[DONE: Disbursement History updated for Grant Type switch] Disbursement Id: % | Moved to System (%) and back to %(%) ',disbursementRow.id,systemStatusId,(select name from workflow_statuses where id=cini_disbursement_status(disbursementRow.status_id,9,18)),cini_disbursement_status(disbursementRow.status_id,9,18);

    			RAISE NOTICE 'PROCESSED DISBURSEMENT HISTORY...';
    		--END
    	END LOOP;
    	RAISE NOTICE 'PROCESSED ALL DISBURSEMENTS...';
    	--ENDLOOP
    	RAISE NOTICE '-------------------------------------------------------';
        RAISE NOTICE 'PROCESSED GRANT %, Reports Processed: %, Disbursements Processed:%',grantRow.id,reportsProcessedCount,disbursementsProcessedCount;
        RAISE NOTICE '-------------------------------------------------------';
        reportsProcessedCount:=0;
        disbursementsProcessedCount:=0;
END LOOP;

-- ENABLE TRIGGERS FOR Grants, Reports, Disbursements, Grant Assignments, Report Assignments, Disbursement Assignments
ALTER TABLE grants ENABLE TRIGGER ALL;
ALTER TABLE reports ENABLE TRIGGER ALL;
ALTER TABLE disbursements ENABLE TRIGGER ALL;
ALTER TABLE grant_assignments ENABLE TRIGGER ALL;
ALTER TABLE report_assignments ENABLE TRIGGER ALL;
ALTER TABLE disbursement_assignments ENABLE TRIGGER ALL;
--END
end;
$BODY$;