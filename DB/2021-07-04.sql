create sequence disbursement_documents_seq start 1;
create table disbursement_documents(
	id bigint not null default nextval('disbursement_documents_seq'::regclass),
	location text,
	uploaded_on timestamp,
	uploaded_by bigint,
	name text,
	extension text,
    disbursement_id bigint,
	constraint disbursement_document_pk primary key(id)
);