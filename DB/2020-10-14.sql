alter table report_snapshot add column from_state_id bigint, add column from_note text,add column moved_by bigint,add column from_string_attributes text;
alter table report_snapshot add column to_state_id bigint;
alter table disbursement_snapshot add column from_state_id bigint, add column from_note text,add column moved_by bigint,add column from_string_attributes text;
alter table disbursement_snapshot add column to_state_id bigint;
alter table report_snapshot add column moved_on timestamp;
alter table disbursement_snapshot add column moved_on timestamp;