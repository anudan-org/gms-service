alter table disbursements alter column requested_amount type double precision; 
alter table disbursement_history alter column requested_amount type double precision; 
alter table actual_disbursements alter column actual_amount type double precision; 
alter table actual_disbursements alter column other_sources type double precision; 
alter table disbursement_snapshot alter column requested_amount type double precision; 