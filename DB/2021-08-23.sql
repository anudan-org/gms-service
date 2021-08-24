update disbursements set disabled_by_amendment=false where id in (select a.id from disbursements a
inner join workflow_statuses b on a.status_id=b.id
where b.internal_status!='CLOSED' and a.disabled_by_amendment=true);