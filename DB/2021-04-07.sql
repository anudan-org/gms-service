CREATE SEQUENCE data_extract_logs_id_seq START 1;
CREATE TABLE data_extract_logs
(
    id bigint NOT NULL DEFAULT nextval('data_extract_logs_id_seq'::regclass),
    summary_for text,
    extract_request_by text,
    extract_requested_on timestamp,
    records_retrieved integer DEFAULT 0,
    PRIMARY KEY (id)
);

