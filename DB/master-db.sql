--
-- PostgreSQL database dump
--

-- Dumped from database version 11.7
-- Dumped by pg_dump version 11.7

-- Started on 2020-05-06 19:19:55 IST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 313 (class 1255 OID 306459)
-- Name: process_grant_state_change(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.process_grant_state_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        IF (TG_OP = 'UPDATE' AND OLD.grant_status_id!=NEW.grant_status_id) THEN
            INSERT INTO grant_history (
id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added,note_added_by,moved_on,reference_no) select OLD.*;

            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
$$;


--
-- TOC entry 311 (class 1255 OID 306460)
-- Name: process_report_state_change(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.process_report_state_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.status_id!=NEW.status_id) THEN
INSERT INTO report_history(
id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id,type, note, note_added,note_added_by,template_id,moved_on,linked_approved_reports,report_detail)
select OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;

$$;


--
-- TOC entry 312 (class 1255 OID 306461)
-- Name: refresh_mat_views(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.refresh_mat_views() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
    refresh materialized view granter_count_and_amount_totals;
 refresh materialized view granter_grantees;
end
$$;


SET default_with_oids = false;

--
-- TOC entry 196 (class 1259 OID 306462)
-- Name: app_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.app_config (
    id bigint NOT NULL,
    config_name character varying(255),
    config_value text,
    description text,
    configurable boolean DEFAULT false,
    key bigint,
    type character varying(10)
);


--
-- TOC entry 197 (class 1259 OID 306469)
-- Name: app_config_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.app_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3890 (class 0 OID 0)
-- Dependencies: 197
-- Name: app_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.app_config_id_seq OWNED BY public.app_config.id;


--
-- TOC entry 198 (class 1259 OID 306471)
-- Name: doc_kpi_data_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.doc_kpi_data_document (
    id bigint NOT NULL,
    file_name character varying(255),
    file_type character varying(255),
    version integer,
    doc_kpi_data_id bigint
);


--
-- TOC entry 199 (class 1259 OID 306477)
-- Name: doc_kpi_data_document_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.doc_kpi_data_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3891 (class 0 OID 0)
-- Dependencies: 199
-- Name: doc_kpi_data_document_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.doc_kpi_data_document_id_seq OWNED BY public.doc_kpi_data_document.id;


--
-- TOC entry 200 (class 1259 OID 306479)
-- Name: document_kpi_notes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.document_kpi_notes (
    id bigint NOT NULL,
    message character varying(255),
    posted_on timestamp without time zone,
    kpi_data_id bigint,
    posted_by_id bigint
);


--
-- TOC entry 201 (class 1259 OID 306482)
-- Name: document_kpi_notes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.document_kpi_notes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3892 (class 0 OID 0)
-- Dependencies: 201
-- Name: document_kpi_notes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.document_kpi_notes_id_seq OWNED BY public.document_kpi_notes.id;


--
-- TOC entry 202 (class 1259 OID 306484)
-- Name: grant_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_assignments (
    id bigint NOT NULL,
    anchor boolean,
    assignments bigint,
    grant_id bigint,
    state_id bigint
);


--
-- TOC entry 203 (class 1259 OID 306487)
-- Name: grant_assignments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_assignments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3893 (class 0 OID 0)
-- Dependencies: 203
-- Name: grant_assignments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_assignments_id_seq OWNED BY public.grant_assignments.id;


--
-- TOC entry 204 (class 1259 OID 306489)
-- Name: grant_attrib_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_attrib_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 205 (class 1259 OID 306491)
-- Name: grant_document_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_document_attributes (
    id bigint NOT NULL,
    file_type character varying(255),
    location character varying(255),
    name character varying(255),
    version integer,
    grant_id bigint,
    section_id bigint,
    section_attribute_id bigint
);


--
-- TOC entry 206 (class 1259 OID 306497)
-- Name: grant_document_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_document_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3894 (class 0 OID 0)
-- Dependencies: 206
-- Name: grant_document_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_document_attributes_id_seq OWNED BY public.grant_document_attributes.id;


--
-- TOC entry 207 (class 1259 OID 306499)
-- Name: grant_document_kpi_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_document_kpi_data (
    id bigint NOT NULL,
    actuals character varying(255),
    goal character varying(255),
    note character varying(255),
    to_report boolean,
    type character varying(255),
    grant_kpi_id bigint,
    submission_id bigint
);


--
-- TOC entry 208 (class 1259 OID 306505)
-- Name: grant_document_kpi_data_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_document_kpi_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3895 (class 0 OID 0)
-- Dependencies: 208
-- Name: grant_document_kpi_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_document_kpi_data_id_seq OWNED BY public.grant_document_kpi_data.id;


--
-- TOC entry 209 (class 1259 OID 306507)
-- Name: grant_history_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 210 (class 1259 OID 306509)
-- Name: grant_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_history (
    seqid bigint DEFAULT nextval('public.grant_history_id_seq'::regclass) NOT NULL,
    id bigint,
    amount double precision,
    created_at timestamp without time zone,
    created_by character varying(255),
    description text,
    end_date timestamp without time zone,
    name text,
    representative character varying(255),
    start_date timestamp without time zone,
    status_name character varying(255),
    template_id bigint,
    updated_at timestamp without time zone,
    updated_by character varying(255),
    grant_status_id bigint,
    grantor_org_id bigint,
    organization_id bigint,
    substatus_id bigint,
    note text,
    note_added timestamp without time zone,
    note_added_by text,
    moved_on timestamp without time zone,
    reference_no text
);


--
-- TOC entry 211 (class 1259 OID 306516)
-- Name: grant_kpis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_kpis (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    description character varying(255),
    periodicity_unit character varying(255),
    kpi_reporting_type character varying(255),
    kpi_type character varying(255),
    periodicity integer,
    is_scheduled boolean,
    title character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    grant_id bigint
);


--
-- TOC entry 212 (class 1259 OID 306522)
-- Name: grant_kpis_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_kpis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3896 (class 0 OID 0)
-- Dependencies: 212
-- Name: grant_kpis_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_kpis_id_seq OWNED BY public.grant_kpis.id;


--
-- TOC entry 213 (class 1259 OID 306524)
-- Name: grant_qualitative_kpi_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_qualitative_kpi_data (
    id bigint NOT NULL,
    actuals character varying(255),
    goal character varying(255),
    note character varying(255),
    to_report boolean,
    grant_kpi_id bigint,
    submission_id bigint
);


--
-- TOC entry 214 (class 1259 OID 306530)
-- Name: grant_qualitative_kpi_data_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_qualitative_kpi_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3897 (class 0 OID 0)
-- Dependencies: 214
-- Name: grant_qualitative_kpi_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_qualitative_kpi_data_id_seq OWNED BY public.grant_qualitative_kpi_data.id;


--
-- TOC entry 215 (class 1259 OID 306532)
-- Name: grant_quantitative_kpi_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_quantitative_kpi_data (
    id bigint NOT NULL,
    actuals integer,
    goal integer,
    note character varying(255),
    to_report boolean,
    grant_kpi_id bigint,
    submission_id bigint
);


--
-- TOC entry 216 (class 1259 OID 306535)
-- Name: grant_quantitative_kpi_data_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_quantitative_kpi_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3898 (class 0 OID 0)
-- Dependencies: 216
-- Name: grant_quantitative_kpi_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_quantitative_kpi_data_id_seq OWNED BY public.grant_quantitative_kpi_data.id;


--
-- TOC entry 217 (class 1259 OID 306537)
-- Name: grant_section_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_section_attributes (
    id bigint NOT NULL,
    deletable boolean,
    field_name character varying(255),
    field_type character varying(255),
    required boolean,
    type character varying(255),
    section_id bigint
);


--
-- TOC entry 218 (class 1259 OID 306543)
-- Name: grant_section_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_section_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3899 (class 0 OID 0)
-- Dependencies: 218
-- Name: grant_section_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_section_attributes_id_seq OWNED BY public.grant_section_attributes.id;


--
-- TOC entry 219 (class 1259 OID 306545)
-- Name: grant_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_sections (
    id bigint NOT NULL,
    deletable boolean,
    section_name character varying(255)
);


--
-- TOC entry 220 (class 1259 OID 306548)
-- Name: grant_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3900 (class 0 OID 0)
-- Dependencies: 220
-- Name: grant_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_sections_id_seq OWNED BY public.grant_sections.id;


--
-- TOC entry 221 (class 1259 OID 306550)
-- Name: grant_snapshot_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_snapshot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 222 (class 1259 OID 306552)
-- Name: grant_snapshot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_snapshot (
    id bigint DEFAULT nextval('public.grant_snapshot_id_seq'::regclass) NOT NULL,
    assigned_to_id bigint,
    grant_id bigint,
    grantee text,
    string_attributes text,
    name text,
    description text,
    amount double precision,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    representative character varying(255),
    grant_status_id bigint
);


--
-- TOC entry 223 (class 1259 OID 306559)
-- Name: grant_specific_section_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_specific_section_attributes (
    id bigint NOT NULL,
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name text,
    field_type text,
    required boolean,
    granter_id bigint,
    section_id bigint
);


--
-- TOC entry 224 (class 1259 OID 306565)
-- Name: grant_specific_section_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_specific_section_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3901 (class 0 OID 0)
-- Dependencies: 224
-- Name: grant_specific_section_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_specific_section_attributes_id_seq OWNED BY public.grant_specific_section_attributes.id;


--
-- TOC entry 225 (class 1259 OID 306567)
-- Name: grant_specific_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_specific_sections (
    id bigint NOT NULL,
    deletable boolean,
    grant_id bigint,
    grant_template_id bigint,
    section_name character varying(255),
    section_order integer,
    granter_id bigint
);


--
-- TOC entry 226 (class 1259 OID 306570)
-- Name: grant_specific_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_specific_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3902 (class 0 OID 0)
-- Dependencies: 226
-- Name: grant_specific_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_specific_sections_id_seq OWNED BY public.grant_specific_sections.id;


--
-- TOC entry 227 (class 1259 OID 306572)
-- Name: grant_string_attribute_attachments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_string_attribute_attachments (
    id bigint DEFAULT nextval('public.grant_attrib_attachments_id_seq'::regclass) NOT NULL,
    name text,
    description text,
    location text,
    version integer DEFAULT 1,
    title text,
    type text,
    created_on date,
    created_by text,
    updated_on date,
    updated_by text,
    grant_string_attribute_id bigint
);


--
-- TOC entry 228 (class 1259 OID 306580)
-- Name: grant_string_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grant_string_attributes (
    id bigint NOT NULL,
    frequency character varying(255),
    target character varying(255),
    value text,
    grant_id bigint,
    section_id bigint,
    section_attribute_id bigint
);


--
-- TOC entry 229 (class 1259 OID 306586)
-- Name: grant_string_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grant_string_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3903 (class 0 OID 0)
-- Dependencies: 229
-- Name: grant_string_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grant_string_attributes_id_seq OWNED BY public.grant_string_attributes.id;


--
-- TOC entry 230 (class 1259 OID 306588)
-- Name: grantees; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grantees (
    id bigint NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 306591)
-- Name: organizations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.organizations (
    organization_type character varying(31) NOT NULL,
    id bigint NOT NULL,
    code character varying(255),
    created_at timestamp without time zone,
    created_by character varying(255),
    name character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255)
);


--
-- TOC entry 232 (class 1259 OID 306597)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    email_id character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    organization_id bigint,
    active boolean DEFAULT true,
    user_profile text
);

alter table users add column plain boolean default false;
--
-- TOC entry 233 (class 1259 OID 306604)
-- Name: granter_active_users; Type: MATERIALIZED VIEW; Schema: public; Owner: -
--

CREATE MATERIALIZED VIEW public.granter_active_users AS
 SELECT b.id AS granter_id,
    count(*) AS active_users
   FROM (public.users a
     JOIN public.organizations b ON ((b.id = a.organization_id)))
  WHERE ((a.active = true) AND ((b.organization_type)::text = 'GRANTER'::text))
  GROUP BY b.id
  WITH NO DATA;


--
-- TOC entry 234 (class 1259 OID 306609)
-- Name: grants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grants (
    id bigint NOT NULL,
    amount double precision,
    created_at timestamp without time zone,
    created_by character varying(255),
    description text,
    end_date timestamp without time zone,
    name text,
    representative character varying(255),
    start_date timestamp without time zone,
    status_name character varying(255),
    template_id bigint,
    updated_at timestamp without time zone,
    updated_by character varying(255),
    grant_status_id bigint,
    grantor_org_id bigint,
    organization_id bigint,
    substatus_id bigint,
    note text,
    note_added timestamp without time zone,
    note_added_by text,
    moved_on timestamp without time zone,
    reference_no text
);


--
-- TOC entry 235 (class 1259 OID 306615)
-- Name: workflow_statuses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_statuses (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    display_name character varying(255),
    initial boolean,
    internal_status character varying(255),
    name character varying(255),
    terminal boolean,
    updated_at timestamp without time zone,
    updated_by character varying(255),
    verb character varying(255),
    workflow_id bigint
);


--
-- TOC entry 236 (class 1259 OID 306621)
-- Name: granter_count_and_amount_totals; Type: MATERIALIZED VIEW; Schema: public; Owner: -
--

CREATE MATERIALIZED VIEW public.granter_count_and_amount_totals AS
 SELECT a.grantor_org_id AS granter_id,
    sum(a.amount) AS total_grant_amount,
    count(*) AS total_grants
   FROM (public.grants a
     JOIN public.workflow_statuses b ON ((a.grant_status_id = b.id)))
  WHERE (((b.internal_status)::text = 'ACTIVE'::text) OR ((b.internal_status)::text = 'CLOSED'::text))
  GROUP BY a.grantor_org_id
  WITH NO DATA;


--
-- TOC entry 237 (class 1259 OID 306626)
-- Name: granter_grant_section_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_grant_section_attributes (
    id bigint NOT NULL,
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name character varying(255),
    field_type character varying(255),
    required boolean,
    granter_id bigint,
    section_id bigint
);


--
-- TOC entry 238 (class 1259 OID 306632)
-- Name: granter_grant_section_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_grant_section_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3904 (class 0 OID 0)
-- Dependencies: 238
-- Name: granter_grant_section_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.granter_grant_section_attributes_id_seq OWNED BY public.granter_grant_section_attributes.id;


--
-- TOC entry 239 (class 1259 OID 306634)
-- Name: granter_grant_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_grant_sections (
    id bigint NOT NULL,
    deletable boolean,
    section_name character varying(255),
    section_order integer,
    grant_template_id bigint,
    granter_id bigint
);


--
-- TOC entry 240 (class 1259 OID 306637)
-- Name: granter_grant_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_grant_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3905 (class 0 OID 0)
-- Dependencies: 240
-- Name: granter_grant_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.granter_grant_sections_id_seq OWNED BY public.granter_grant_sections.id;


--
-- TOC entry 241 (class 1259 OID 306639)
-- Name: granter_grant_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_grant_templates (
    id bigint NOT NULL,
    description text,
    granter_id bigint,
    name text,
    published boolean,
    private_to_grant boolean DEFAULT true,
    default_template boolean DEFAULT false
);


--
-- TOC entry 242 (class 1259 OID 306647)
-- Name: granter_grant_templates_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_grant_templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3906 (class 0 OID 0)
-- Dependencies: 242
-- Name: granter_grant_templates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.granter_grant_templates_id_seq OWNED BY public.granter_grant_templates.id;


--
-- TOC entry 243 (class 1259 OID 306649)
-- Name: granter_grantees; Type: MATERIALIZED VIEW; Schema: public; Owner: -
--

CREATE MATERIALIZED VIEW public.granter_grantees AS
 SELECT DISTINCT a.grantor_org_id AS granter_id,
    count(DISTINCT a.organization_id) AS grantee_totals
   FROM (public.grants a
     JOIN public.workflow_statuses b ON ((a.grant_status_id = b.id)))
  WHERE (((b.internal_status)::text = 'ACTIVE'::text) OR ((b.internal_status)::text = 'CLOSED'::text))
  GROUP BY a.grantor_org_id
  WITH NO DATA;


--
-- TOC entry 244 (class 1259 OID 306654)
-- Name: granter_report_section_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_report_section_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 245 (class 1259 OID 306656)
-- Name: granter_report_section_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_report_section_attributes (
    id bigint DEFAULT nextval('public.granter_report_section_attributes_id_seq'::regclass) NOT NULL,
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name character varying(255),
    field_type character varying(255),
    required boolean,
    granter_id bigint,
    section_id bigint
);


--
-- TOC entry 246 (class 1259 OID 306663)
-- Name: granter_report_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_report_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 247 (class 1259 OID 306665)
-- Name: granter_report_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_report_sections (
    id bigint DEFAULT nextval('public.granter_report_sections_id_seq'::regclass) NOT NULL,
    deletable boolean,
    section_name character varying(255),
    section_order integer,
    report_template_id bigint,
    granter_id bigint
);


--
-- TOC entry 248 (class 1259 OID 306669)
-- Name: granter_report_templates_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granter_report_templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 249 (class 1259 OID 306671)
-- Name: granter_report_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granter_report_templates (
    id bigint DEFAULT nextval('public.granter_report_templates_id_seq'::regclass) NOT NULL,
    description text,
    granter_id bigint,
    name text,
    published boolean,
    private_to_report boolean DEFAULT true,
    default_template boolean DEFAULT false
);


--
-- TOC entry 250 (class 1259 OID 306680)
-- Name: granters; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granters (
    host_url character varying(255),
    image_name character varying(255),
    navbar_color character varying(255),
    navbar_text_color character varying(255),
    id bigint NOT NULL
);


--
-- TOC entry 251 (class 1259 OID 306686)
-- Name: grants_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3907 (class 0 OID 0)
-- Dependencies: 251
-- Name: grants_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grants_id_seq OWNED BY public.grants.id;


--
-- TOC entry 252 (class 1259 OID 306688)
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    message text,
    posted_on timestamp without time zone,
    read boolean,
    user_id bigint,
    grant_id bigint,
    title text,
    report_id bigint,
    notification_for character varying(25)
);


--
-- TOC entry 253 (class 1259 OID 306694)
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3908 (class 0 OID 0)
-- Dependencies: 253
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- TOC entry 254 (class 1259 OID 306696)
-- Name: org_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.org_config (
    id bigint NOT NULL,
    config_name character varying(255),
    config_value text,
    granter_id bigint,
    description text,
    configurable boolean DEFAULT false,
    key bigint,
    type character varying(10)
);


--
-- TOC entry 255 (class 1259 OID 306703)
-- Name: org_config_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.org_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3909 (class 0 OID 0)
-- Dependencies: 255
-- Name: org_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.org_config_id_seq OWNED BY public.org_config.id;


--
-- TOC entry 256 (class 1259 OID 306705)
-- Name: organizations_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.organizations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3910 (class 0 OID 0)
-- Dependencies: 256
-- Name: organizations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.organizations_id_seq OWNED BY public.organizations.id;


--
-- TOC entry 257 (class 1259 OID 306707)
-- Name: platform; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.platform (
    host_url character varying(255),
    image_name character varying(255),
    navbar_color character varying(255),
    id bigint NOT NULL
);


--
-- TOC entry 258 (class 1259 OID 306713)
-- Name: qual_kpi_data_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qual_kpi_data_document (
    id bigint NOT NULL,
    file_name character varying(255),
    file_type character varying(255),
    version integer,
    qual_kpi_data_id bigint
);


--
-- TOC entry 259 (class 1259 OID 306719)
-- Name: qual_kpi_data_document_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.qual_kpi_data_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3911 (class 0 OID 0)
-- Dependencies: 259
-- Name: qual_kpi_data_document_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.qual_kpi_data_document_id_seq OWNED BY public.qual_kpi_data_document.id;


--
-- TOC entry 260 (class 1259 OID 306721)
-- Name: qualitative_kpi_notes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qualitative_kpi_notes (
    id bigint NOT NULL,
    message character varying(255),
    posted_on timestamp without time zone,
    kpi_data_id bigint,
    posted_by_id bigint
);


--
-- TOC entry 261 (class 1259 OID 306724)
-- Name: qualitative_kpi_notes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.qualitative_kpi_notes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3912 (class 0 OID 0)
-- Dependencies: 261
-- Name: qualitative_kpi_notes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.qualitative_kpi_notes_id_seq OWNED BY public.qualitative_kpi_notes.id;


--
-- TOC entry 262 (class 1259 OID 306726)
-- Name: quant_kpi_data_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quant_kpi_data_document (
    id bigint NOT NULL,
    file_name character varying(255),
    file_type character varying(255),
    version integer,
    quant_kpi_data_id bigint
);


--
-- TOC entry 263 (class 1259 OID 306732)
-- Name: quant_kpi_data_document_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quant_kpi_data_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3913 (class 0 OID 0)
-- Dependencies: 263
-- Name: quant_kpi_data_document_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quant_kpi_data_document_id_seq OWNED BY public.quant_kpi_data_document.id;


--
-- TOC entry 264 (class 1259 OID 306734)
-- Name: quantitative_kpi_notes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quantitative_kpi_notes (
    id bigint NOT NULL,
    message character varying(255),
    posted_on timestamp without time zone,
    kpi_data_id bigint,
    posted_by_id bigint
);


--
-- TOC entry 265 (class 1259 OID 306737)
-- Name: quantitative_kpi_notes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quantitative_kpi_notes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3914 (class 0 OID 0)
-- Dependencies: 265
-- Name: quantitative_kpi_notes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quantitative_kpi_notes_id_seq OWNED BY public.quantitative_kpi_notes.id;


--
-- TOC entry 266 (class 1259 OID 306739)
-- Name: release_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.release_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 267 (class 1259 OID 306741)
-- Name: release; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.release (
    id bigint DEFAULT nextval('public.release_id_seq'::regclass) NOT NULL,
    version character varying(255)
);


--
-- TOC entry 268 (class 1259 OID 306745)
-- Name: report_assignments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_assignments_id_seq
    START WITH 768
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 269 (class 1259 OID 306747)
-- Name: report_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_assignments (
    id bigint DEFAULT nextval('public.report_assignments_id_seq'::regclass) NOT NULL,
    report_id bigint,
    state_id bigint,
    assignment bigint,
    anchor boolean
);


--
-- TOC entry 270 (class 1259 OID 306751)
-- Name: report_history_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 271 (class 1259 OID 306753)
-- Name: report_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_history (
    seqid bigint DEFAULT nextval('public.report_history_id_seq'::regclass) NOT NULL,
    id bigint,
    name text,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    due_date timestamp without time zone,
    status_id bigint,
    created_at timestamp without time zone,
    created_by bigint,
    updated_at timestamp without time zone,
    updated_by bigint,
    grant_id bigint,
    note text,
    note_added timestamp without time zone,
    note_added_by bigint,
    template_id bigint,
    type text,
    moved_on timestamp without time zone,
    linked_approved_reports text,
    report_detail text
);


--
-- TOC entry 272 (class 1259 OID 306760)
-- Name: report_snapshot_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_snapshot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 273 (class 1259 OID 306762)
-- Name: report_snapshot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_snapshot (
    id bigint DEFAULT nextval('public.report_snapshot_id_seq'::regclass) NOT NULL,
    assigned_to_id bigint,
    report_id bigint,
    string_attributes text,
    name text,
    description text,
    status_id bigint,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    due_date timestamp without time zone
);


--
-- TOC entry 274 (class 1259 OID 306769)
-- Name: report_specific_section_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_specific_section_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 275 (class 1259 OID 306771)
-- Name: report_specific_section_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_specific_section_attributes (
    id bigint DEFAULT nextval('public.report_specific_section_attributes_id_seq'::regclass) NOT NULL,
    attribute_order integer,
    deletable boolean,
    extras text,
    field_name text,
    field_type text,
    required boolean,
    granter_id bigint,
    section_id bigint,
    can_edit boolean DEFAULT true
);


--
-- TOC entry 276 (class 1259 OID 306779)
-- Name: report_specific_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_specific_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 277 (class 1259 OID 306781)
-- Name: report_specific_sections; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_specific_sections (
    id bigint DEFAULT nextval('public.report_specific_sections_id_seq'::regclass) NOT NULL,
    deletable boolean,
    report_id bigint,
    report_template_id bigint,
    section_name character varying(255),
    section_order integer,
    granter_id bigint
);


--
-- TOC entry 278 (class 1259 OID 306785)
-- Name: report_string_attribute_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_string_attribute_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 279 (class 1259 OID 306787)
-- Name: report_string_attribute_attachments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_string_attribute_attachments (
    id bigint DEFAULT nextval('public.report_string_attribute_attachments_id_seq'::regclass) NOT NULL,
    created_by character varying(255),
    created_on timestamp without time zone,
    description text,
    location character varying(255),
    name text,
    title character varying(255),
    type character varying(255),
    updated_by character varying(255),
    updated_on timestamp without time zone,
    version integer,
    report_string_attribute_id bigint
);


--
-- TOC entry 280 (class 1259 OID 306794)
-- Name: report_string_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_string_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 281 (class 1259 OID 306796)
-- Name: report_string_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.report_string_attributes (
    id bigint DEFAULT nextval('public.report_string_attributes_id_seq'::regclass) NOT NULL,
    frequency character varying(255),
    target character varying(255),
    value text,
    report_id bigint,
    section_id bigint,
    section_attribute_id bigint,
    grant_level_target text DEFAULT ''::text,
    actual_target text
);


--
-- TOC entry 282 (class 1259 OID 306804)
-- Name: reports_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 283 (class 1259 OID 306806)
-- Name: reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reports (
    id bigint DEFAULT nextval('public.reports_id_seq'::regclass) NOT NULL,
    name text,
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    due_date timestamp without time zone,
    status_id bigint,
    created_at timestamp without time zone,
    created_by bigint,
    updated_at timestamp without time zone,
    updated_by bigint,
    grant_id bigint,
    type text,
    note text,
    note_added timestamp without time zone,
    note_added_by bigint,
    template_id bigint,
    moved_on timestamp without time zone,
    linked_approved_reports text,
    report_detail text
);


--
-- TOC entry 284 (class 1259 OID 306813)
-- Name: rfps; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rfps (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    description character varying(255),
    title character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    granter_id bigint
);


--
-- TOC entry 285 (class 1259 OID 306819)
-- Name: rfps_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.rfps_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3915 (class 0 OID 0)
-- Dependencies: 285
-- Name: rfps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.rfps_id_seq OWNED BY public.rfps.id;


--
-- TOC entry 286 (class 1259 OID 306821)
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    name character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    organization_id bigint,
    description text,
    internal boolean DEFAULT false
);


--
-- TOC entry 287 (class 1259 OID 306828)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3916 (class 0 OID 0)
-- Dependencies: 287
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- TOC entry 288 (class 1259 OID 306830)
-- Name: roles_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles_permission (
    id bigint NOT NULL,
    permission character varying(255),
    role_id bigint
);


--
-- TOC entry 289 (class 1259 OID 306833)
-- Name: roles_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.roles_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3917 (class 0 OID 0)
-- Dependencies: 289
-- Name: roles_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.roles_permission_id_seq OWNED BY public.roles_permission.id;


--
-- TOC entry 290 (class 1259 OID 306835)
-- Name: submission_note; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.submission_note (
    id bigint NOT NULL,
    message character varying(255),
    posted_on timestamp without time zone,
    posted_by_id bigint,
    submission_id bigint
);


--
-- TOC entry 291 (class 1259 OID 306838)
-- Name: submission_note_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.submission_note_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3918 (class 0 OID 0)
-- Dependencies: 291
-- Name: submission_note_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.submission_note_id_seq OWNED BY public.submission_note.id;


--
-- TOC entry 292 (class 1259 OID 306840)
-- Name: submissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.submissions (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    submit_by timestamp without time zone,
    submitted_on timestamp without time zone,
    title character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    grant_id bigint,
    submission_status_id bigint
);


--
-- TOC entry 293 (class 1259 OID 306846)
-- Name: submissions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.submissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3919 (class 0 OID 0)
-- Dependencies: 293
-- Name: submissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.submissions_id_seq OWNED BY public.submissions.id;


--
-- TOC entry 294 (class 1259 OID 306848)
-- Name: template_library; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.template_library (
    id bigint NOT NULL,
    description text,
    file_type character varying(255),
    granter_id bigint,
    location character varying(255),
    name text,
    type character varying(255),
    version integer
);


--
-- TOC entry 295 (class 1259 OID 306854)
-- Name: template_library_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.template_library_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3920 (class 0 OID 0)
-- Dependencies: 295
-- Name: template_library_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.template_library_id_seq OWNED BY public.template_library.id;


--
-- TOC entry 296 (class 1259 OID 306856)
-- Name: templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.templates (
    id bigint NOT NULL,
    description character varying(255),
    file_type character varying(255),
    location character varying(255),
    name character varying(255),
    type character varying(255),
    version integer,
    kpi_id bigint
);


--
-- TOC entry 297 (class 1259 OID 306862)
-- Name: templates_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3921 (class 0 OID 0)
-- Dependencies: 297
-- Name: templates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.templates_id_seq OWNED BY public.templates.id;


--
-- TOC entry 298 (class 1259 OID 306864)
-- Name: user_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_roles (
    id bigint NOT NULL,
    role_id bigint,
    user_id bigint
);


--
-- TOC entry 299 (class 1259 OID 306867)
-- Name: user_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3922 (class 0 OID 0)
-- Dependencies: 299
-- Name: user_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.user_roles_id_seq OWNED BY public.user_roles.id;


--
-- TOC entry 300 (class 1259 OID 306869)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3923 (class 0 OID 0)
-- Dependencies: 300
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 301 (class 1259 OID 306871)
-- Name: work_flow_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.work_flow_permission (
    id bigint NOT NULL,
    action character varying(255),
    from_name character varying(255),
    from_state_id bigint,
    note_required boolean,
    to_name character varying(255),
    to_state_id bigint
);


--
-- TOC entry 302 (class 1259 OID 306877)
-- Name: workflow_action_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_action_permission (
    id bigint NOT NULL,
    permissions_string character varying(255)
);


--
-- TOC entry 303 (class 1259 OID 306880)
-- Name: workflow_state_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_state_permissions (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    permission character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    role_id bigint,
    workflow_status_id bigint
);


--
-- TOC entry 304 (class 1259 OID 306886)
-- Name: workflow_state_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.workflow_state_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3924 (class 0 OID 0)
-- Dependencies: 304
-- Name: workflow_state_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.workflow_state_permissions_id_seq OWNED BY public.workflow_state_permissions.id;


--
-- TOC entry 305 (class 1259 OID 306888)
-- Name: workflow_status_transitions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_status_transitions (
    id bigint NOT NULL,
    action character varying(255),
    created_at timestamp without time zone,
    created_by character varying(255),
    note_required boolean,
    updated_at timestamp without time zone,
    updated_by character varying(255),
    from_state_id bigint,
    role_id bigint,
    to_state_id bigint,
    workflow_id bigint,
    seq_order integer
);


--
-- TOC entry 306 (class 1259 OID 306894)
-- Name: workflow_status_transitions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.workflow_status_transitions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3925 (class 0 OID 0)
-- Dependencies: 306
-- Name: workflow_status_transitions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.workflow_status_transitions_id_seq OWNED BY public.workflow_status_transitions.id;


--
-- TOC entry 307 (class 1259 OID 306896)
-- Name: workflow_statuses_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.workflow_statuses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3926 (class 0 OID 0)
-- Dependencies: 307
-- Name: workflow_statuses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.workflow_statuses_id_seq OWNED BY public.workflow_statuses.id;


--
-- TOC entry 308 (class 1259 OID 306898)
-- Name: workflow_transition_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflow_transition_model (
    id bigint NOT NULL,
    _from character varying(255),
    _performedby character varying(255),
    _to character varying(255),
    action character varying(255),
    from_state_id bigint,
    role_id bigint,
    to_state_id bigint,
    seq_order integer
);


--
-- TOC entry 309 (class 1259 OID 306904)
-- Name: workflows; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workflows (
    id bigint NOT NULL,
    created_at timestamp without time zone,
    created_by character varying(255),
    description character varying(255),
    name character varying(255),
    object character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    granter_id bigint
);


--
-- TOC entry 310 (class 1259 OID 306910)
-- Name: workflows_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.workflows_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3927 (class 0 OID 0)
-- Dependencies: 310
-- Name: workflows_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.workflows_id_seq OWNED BY public.workflows.id;


--
-- TOC entry 3396 (class 2604 OID 306912)
-- Name: app_config id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.app_config ALTER COLUMN id SET DEFAULT nextval('public.app_config_id_seq'::regclass);


--
-- TOC entry 3397 (class 2604 OID 306913)
-- Name: doc_kpi_data_document id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doc_kpi_data_document ALTER COLUMN id SET DEFAULT nextval('public.doc_kpi_data_document_id_seq'::regclass);


--
-- TOC entry 3398 (class 2604 OID 306914)
-- Name: document_kpi_notes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.document_kpi_notes ALTER COLUMN id SET DEFAULT nextval('public.document_kpi_notes_id_seq'::regclass);


--
-- TOC entry 3399 (class 2604 OID 306915)
-- Name: grant_assignments id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_assignments ALTER COLUMN id SET DEFAULT nextval('public.grant_assignments_id_seq'::regclass);


--
-- TOC entry 3400 (class 2604 OID 306916)
-- Name: grant_document_attributes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_attributes ALTER COLUMN id SET DEFAULT nextval('public.grant_document_attributes_id_seq'::regclass);


--
-- TOC entry 3401 (class 2604 OID 306917)
-- Name: grant_document_kpi_data id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_kpi_data ALTER COLUMN id SET DEFAULT nextval('public.grant_document_kpi_data_id_seq'::regclass);


--
-- TOC entry 3403 (class 2604 OID 306918)
-- Name: grant_kpis id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_kpis ALTER COLUMN id SET DEFAULT nextval('public.grant_kpis_id_seq'::regclass);


--
-- TOC entry 3404 (class 2604 OID 306919)
-- Name: grant_qualitative_kpi_data id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_qualitative_kpi_data ALTER COLUMN id SET DEFAULT nextval('public.grant_qualitative_kpi_data_id_seq'::regclass);


--
-- TOC entry 3405 (class 2604 OID 306920)
-- Name: grant_quantitative_kpi_data id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_quantitative_kpi_data ALTER COLUMN id SET DEFAULT nextval('public.grant_quantitative_kpi_data_id_seq'::regclass);


--
-- TOC entry 3406 (class 2604 OID 306921)
-- Name: grant_section_attributes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_section_attributes ALTER COLUMN id SET DEFAULT nextval('public.grant_section_attributes_id_seq'::regclass);


--
-- TOC entry 3407 (class 2604 OID 306922)
-- Name: grant_sections id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_sections ALTER COLUMN id SET DEFAULT nextval('public.grant_sections_id_seq'::regclass);


--
-- TOC entry 3409 (class 2604 OID 306923)
-- Name: grant_specific_section_attributes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_section_attributes ALTER COLUMN id SET DEFAULT nextval('public.grant_specific_section_attributes_id_seq'::regclass);


--
-- TOC entry 3410 (class 2604 OID 306924)
-- Name: grant_specific_sections id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_sections ALTER COLUMN id SET DEFAULT nextval('public.grant_specific_sections_id_seq'::regclass);


--
-- TOC entry 3413 (class 2604 OID 306925)
-- Name: grant_string_attributes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attributes ALTER COLUMN id SET DEFAULT nextval('public.grant_string_attributes_id_seq'::regclass);


--
-- TOC entry 3419 (class 2604 OID 306926)
-- Name: granter_grant_section_attributes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_section_attributes ALTER COLUMN id SET DEFAULT nextval('public.granter_grant_section_attributes_id_seq'::regclass);


--
-- TOC entry 3420 (class 2604 OID 306927)
-- Name: granter_grant_sections id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_sections ALTER COLUMN id SET DEFAULT nextval('public.granter_grant_sections_id_seq'::regclass);


--
-- TOC entry 3423 (class 2604 OID 306928)
-- Name: granter_grant_templates id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_templates ALTER COLUMN id SET DEFAULT nextval('public.granter_grant_templates_id_seq'::regclass);


--
-- TOC entry 3417 (class 2604 OID 306929)
-- Name: grants id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants ALTER COLUMN id SET DEFAULT nextval('public.grants_id_seq'::regclass);


--
-- TOC entry 3429 (class 2604 OID 306930)
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- TOC entry 3431 (class 2604 OID 306931)
-- Name: org_config id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.org_config ALTER COLUMN id SET DEFAULT nextval('public.org_config_id_seq'::regclass);


--
-- TOC entry 3414 (class 2604 OID 306932)
-- Name: organizations id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organizations ALTER COLUMN id SET DEFAULT nextval('public.organizations_id_seq'::regclass);


--
-- TOC entry 3432 (class 2604 OID 306933)
-- Name: qual_kpi_data_document id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qual_kpi_data_document ALTER COLUMN id SET DEFAULT nextval('public.qual_kpi_data_document_id_seq'::regclass);


--
-- TOC entry 3433 (class 2604 OID 306934)
-- Name: qualitative_kpi_notes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qualitative_kpi_notes ALTER COLUMN id SET DEFAULT nextval('public.qualitative_kpi_notes_id_seq'::regclass);


--
-- TOC entry 3434 (class 2604 OID 306935)
-- Name: quant_kpi_data_document id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quant_kpi_data_document ALTER COLUMN id SET DEFAULT nextval('public.quant_kpi_data_document_id_seq'::regclass);


--
-- TOC entry 3435 (class 2604 OID 306936)
-- Name: quantitative_kpi_notes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quantitative_kpi_notes ALTER COLUMN id SET DEFAULT nextval('public.quantitative_kpi_notes_id_seq'::regclass);


--
-- TOC entry 3447 (class 2604 OID 306937)
-- Name: rfps id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rfps ALTER COLUMN id SET DEFAULT nextval('public.rfps_id_seq'::regclass);


--
-- TOC entry 3449 (class 2604 OID 306938)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- TOC entry 3450 (class 2604 OID 306939)
-- Name: roles_permission id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permission ALTER COLUMN id SET DEFAULT nextval('public.roles_permission_id_seq'::regclass);


--
-- TOC entry 3451 (class 2604 OID 306940)
-- Name: submission_note id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_note ALTER COLUMN id SET DEFAULT nextval('public.submission_note_id_seq'::regclass);


--
-- TOC entry 3452 (class 2604 OID 306941)
-- Name: submissions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submissions ALTER COLUMN id SET DEFAULT nextval('public.submissions_id_seq'::regclass);


--
-- TOC entry 3453 (class 2604 OID 306942)
-- Name: template_library id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.template_library ALTER COLUMN id SET DEFAULT nextval('public.template_library_id_seq'::regclass);


--
-- TOC entry 3454 (class 2604 OID 306943)
-- Name: templates id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.templates ALTER COLUMN id SET DEFAULT nextval('public.templates_id_seq'::regclass);


--
-- TOC entry 3455 (class 2604 OID 306944)
-- Name: user_roles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles ALTER COLUMN id SET DEFAULT nextval('public.user_roles_id_seq'::regclass);


--
-- TOC entry 3416 (class 2604 OID 306945)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3456 (class 2604 OID 306946)
-- Name: workflow_state_permissions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_state_permissions ALTER COLUMN id SET DEFAULT nextval('public.workflow_state_permissions_id_seq'::regclass);


--
-- TOC entry 3457 (class 2604 OID 306947)
-- Name: workflow_status_transitions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions ALTER COLUMN id SET DEFAULT nextval('public.workflow_status_transitions_id_seq'::regclass);


--
-- TOC entry 3418 (class 2604 OID 306948)
-- Name: workflow_statuses id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_statuses ALTER COLUMN id SET DEFAULT nextval('public.workflow_statuses_id_seq'::regclass);


--
-- TOC entry 3458 (class 2604 OID 306949)
-- Name: workflows id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflows ALTER COLUMN id SET DEFAULT nextval('public.workflows_id_seq'::regclass);


--
-- TOC entry 3770 (class 0 OID 306462)
-- Dependencies: 196
-- Data for Name: app_config; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.app_config (id, config_name, config_value, description, configurable, key, type) FROM stdin;
1	KPI_REMINDER_NOTIFICATION_DAYS	30	\N	f	\N	\N
2	KPI_SUBMISSION_WINDOW_DAYS	20	\N	f	\N	\N
3	SUBMISSION_ALTER_MAIL_SUBJECT	Submission Alert	\N	f	\N	\N
4	SUBMISSION_ALTER_MAIL_CONTENT	Submission for %SUBMISSION_TITLE% has been recently updated to %SUBMISSION_STATUS%. Your action is required.	\N	f	\N	\N
5	GRANT_ALERT_NOTIFICATION_MESSAGE	Grant %GRANT_NAME% is %GRANT_STATUS%	\N	f	\N	\N
13	REPORT_STATE_CHANGED_MAIL_SUBJECT	Workflow Alert | Status of %REPORT_NAME% has changed.	\N	f	\N	\N
20	GRANT_STATE_CHANGED_NOTIFICATION_SUBJECT	Workflow Alert | Status of %GRANT_NAME% has changed.	\N	f	\N	\N
9	REPORT_DUE_DATE_INTERVAL	15	Days after end date	f	\N	\N
10	REPORT_SETUP_INTERVAL	30	Days before end date when report needs to be setup	f	\N	\N
11	REPORT_PERIOD_INTERVAL	30	Calculated start date of report	f	\N	\N
14	GRANT_INVITE_SUBJECT	Intivation to Grant: %GRANT_NAME%	\N	f	\N	\N
15	GRANT_INVITE_MESSAGE	You have been invited to view access Grant: %GRANT_NAME% from %TENANT_NAME%.<br><br>Please sign up or login to view the grant ply clicking on the link below.<br><br>%LINK%	\N	f	\N	\N
16	REPORT_INVITE_SUBJECT	Intivation to Report: %REPORT_NAME% for Grant: %GRANT_NAME%	\N	f	\N	\N
17	REPORT_INVITE_MESSAGE	You have been invited to access Report: %REPORT_NAME% for Grant: %GRANT_NAME% from %TENANT_NAME%.<br><br>Please sign up or login to view the grant ply clicking on the link below.<br><br>%LINK%	\N	f	\N	\N
18	INVITE_SUBJECT	Invitation to join %ORG_NAME%	\N	f	\N	\N
19	INVITE_MESSAGE	You have been invited to join %ORG_NAME% as %ROLE_NAME%. This invite has been sent on behalf of %INVITE_FROM%<br><br>Please complete your registration by clicking on the link below.<br><br>%LINK%	\N	f	\N	\N
12	REPORT_STATE_CHANGED_MAIL_MESSAGE	<p style="color: #000;">Hi!</p> <p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a href="%REPORT_LINK%">%REPORT_NAME%</a> to review.</p> <p>&nbsp;</p> <p style="color: #000;"><strong>Change Summary: </strong></p> <hr /> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Name of the Report:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;">%REPORT_NAME% <span style="font-size: 14px; color: #000; font-weight: normal;">for Grant "%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000;margin:0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000;margin:0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <br> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <hr /> <p style="text-align: center; color: #000;">This is an automatically generated email. Please do not reply to this message.</p>	\N	f	\N	\N
6	GRANT_STATE_CHANGED_MAIL_MESSAGE	<p style="color: #000;">Hi!</p> <p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a href="%GRANT_LINK%">%GRANT_NAME%</a> to review.</p> <p>&nbsp;</p> <p style="color: #000;"><strong>Change Summary: </strong></p> <hr /> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Name of the Grant:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;">%GRANT_NAME%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000;margin:0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000;margin:0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <br> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <hr /> <p style="text-align: center; color: #000;">This is an automatically generated email. Please do not reply to this message.</p>	\N	f	\N	\N
7	GRANT_STATE_CHANGED_MAIL_SUBJECT	Workflow Alert | Status of %GRANT_NAME% has changed.	\N	f	\N	\N
21	GRANT_STATE_CHANGED_NOTIFICATION_MESSAGE	\n<p style="color: #000;"><strong>Change Summary: </strong></p>\n<hr />\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Name of the Grant:</p>\n</td>\n<td><span style="font-size: 14px; color: #000; font-weight: bold;">%GRANT_NAME%</span></td>\n</tr>\n<tr>\n<td>\n<p style="font-size: 11px; color: #000;margin:0;">Current State:</p>\n</td>\n<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td>\n</tr>\n<tr>\n<td>\n<p style="font-size: 11px; color: #000;margin:0;">State Owner:</p>\n</td>\n<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td>\n</tr>\n</tbody>\n</table>\n<br>\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous State:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td>\n</tr>\n</tbody>\n</table>\n<p>&nbsp;</p>\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p>\n</td>\n<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p>\n</td>\n<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td>\n</tr>\n</tbody>\n</table>	\N	f	\N	\N
22	REPORT_STATE_CHANGED_NOTIFICATION_SUBJECT	Workflow Alert | Status of %REPORT_NAME% has changed.	\N	f	\N	\N
23	REPORT_STATE_CHANGED_NOTIFICATION_MESSAGE	\n<p style="color: #000;"><strong>Change Summary: </strong></p>\n<hr />\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Name of the Report:</p>\n</td>\n  <td><span style="font-size: 14px; color: #000; font-weight: bold;">%REPORT_NAME% <span style="font-size: 14px; color: #000; font-weight: normal;">for Grant "%GRANT_NAME%"</span> </span></td>\n</tr>\n<tr>\n<td>\n<p style="font-size: 11px; color: #000;margin:0;">Current State:</p>\n</td>\n<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td>\n</tr>\n<tr>\n<td>\n<p style="font-size: 11px; color: #000;margin:0;">State Owner:</p>\n</td>\n<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td>\n</tr>\n</tbody>\n</table>\n<br>\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous State:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p>\n</td>\n<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td>\n</tr>\n</tbody>\n</table>\n<p>&nbsp;</p>\n<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">\n<tbody>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p>\n</td>\n<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td>\n</tr>\n<tr>\n<td width="25%">\n<p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p>\n</td>\n<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td>\n</tr>\n</tbody>\n</table>	\N	f	\N	\N
8	PLATFORM_EMAIL_FOOTER	<hr /> <p style="text-align: center; color: #000;"><strong>Anudan &ndash; A simple Grant Management tool.</strong></p> <p style="text-align: center; color: #000;">&copy; 2020 Foundation for Innovation and Social Entrepreneurship. All rights reserved.</p> <p style="text-align: center; color: #000;">Social Alpha | India | <a href="https://www.socialalpha.org">www.socialalpha.org</a></p> <hr /> <span style="color: #808080;"><em>The content of this message is confidential. If you have received it by mistake, please inform us by an email reply and then delete the message. It is forbidden to copy, forward, or in any way reveal the contents of this message to anyone. The integrity and security of this email cannot be guaranteed over the Internet. Therefore, the sender will not be held liable for any damage caused by the message.</em></span></p> <hr /> <p>&nbsp;</p>	\N	f	\N	\N
24	DUE_REPORTS_REMINDER_SETTINGS	{ "messageDescription": "Description for message", "time": "19:43", "timeDescription": "Description for time", "configuration": { "daysBefore": [ 5, 4, 3, 2 ], "afterNoOfHours": [ 0 ] }, "configurationDescription": "Description for configuration", "sql": "", "subjectDescription": "Description for reminder notification subject", "messageReport": "<p>The report <b>%REPORT_NAME%</b> is due on %DUE_DATE%. <p>Please log on to Anudan to submit the report.</p><p>In case you have any questions or need clarifications while submitting the report please reach out to <b>%OWNER_NAME%</b> at <b>%OWNER_EMAIL%</b>.</p><p>This is a system generated reminder for a report submission against <b>%GRANT_NAME%</b> from <b>%TENANT%</b>. Please ignore this reminder if you have already submitted the report.</p>", "subjectReport": "Report Submission Reminder | Action Required" }	<p>Report due reminder configuration for Grantee organizations<p><br><small>Applicable to unsubmitted reports</small>	t	\N	\N
25	ACTION_DUE_REPORTS_REMINDER_SETTINGS	{ "messageDescription": "Description for message", "time": "08:35", "timeDescription": "Description for time", "configuration": { "daysBefore": [ 0 ], "afterNoOfHours": [ 1440, 5760 ] }, "configurationDescription": "Description for configuration", "sql": "", "subjectDescription": "Description for reminder notification subject", "messageGrant": "<p>The Grant workflow for <b>%GRANT_NAME%</b> requires your action.</p><p>This has been in your queue for %NO_DAYS% number of days</p><p> Please log on to Anudan to progress the workflow. </p><p>This is a system generated reminder for <b>%TENANT%</b>. Please ignore this reminder if you have already actioned the workflow.</p>", "subjectReport": "Workflow delays | Your action required", "subjectGrant": "Workflow delays | Your action required", "messageReport": "<p>The Report workflow for <b>%REPORT_NAME%</b> requires your action.</p><p>This has been in your queue for %NO_DAYS% number of days</p><p> Please log on to Anudan to progress the workflow. </p><p>This is a system generated reminder for <b>%TENANT%</b>. Please ignore this reminder if you have already actioned the workflow.</p>" }	Action pending reminder configuration for Granter users	t	\N	\N
26	GENERATE_GRANT_REFERENCE	true	\N	t	\N	\N
\.


--
-- TOC entry 3772 (class 0 OID 306471)
-- Dependencies: 198
-- Data for Name: doc_kpi_data_document; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.doc_kpi_data_document (id, file_name, file_type, version, doc_kpi_data_id) FROM stdin;
\.


--
-- TOC entry 3774 (class 0 OID 306479)
-- Dependencies: 200
-- Data for Name: document_kpi_notes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.document_kpi_notes (id, message, posted_on, kpi_data_id, posted_by_id) FROM stdin;
\.


--
-- TOC entry 3776 (class 0 OID 306484)
-- Dependencies: 202
-- Data for Name: grant_assignments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_assignments (id, anchor, assignments, grant_id, state_id) FROM stdin;
\.


--
-- TOC entry 3779 (class 0 OID 306491)
-- Dependencies: 205
-- Data for Name: grant_document_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_document_attributes (id, file_type, location, name, version, grant_id, section_id, section_attribute_id) FROM stdin;
\.


--
-- TOC entry 3781 (class 0 OID 306499)
-- Dependencies: 207
-- Data for Name: grant_document_kpi_data; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_document_kpi_data (id, actuals, goal, note, to_report, type, grant_kpi_id, submission_id) FROM stdin;
\.


--
-- TOC entry 3784 (class 0 OID 306509)
-- Dependencies: 210
-- Data for Name: grant_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_history (seqid, id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added, note_added_by, moved_on, reference_no) FROM stdin;
\.


--
-- TOC entry 3785 (class 0 OID 306516)
-- Dependencies: 211
-- Data for Name: grant_kpis; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_kpis (id, created_at, created_by, description, periodicity_unit, kpi_reporting_type, kpi_type, periodicity, is_scheduled, title, updated_at, updated_by, grant_id) FROM stdin;
\.


--
-- TOC entry 3787 (class 0 OID 306524)
-- Dependencies: 213
-- Data for Name: grant_qualitative_kpi_data; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_qualitative_kpi_data (id, actuals, goal, note, to_report, grant_kpi_id, submission_id) FROM stdin;
\.


--
-- TOC entry 3789 (class 0 OID 306532)
-- Dependencies: 215
-- Data for Name: grant_quantitative_kpi_data; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_quantitative_kpi_data (id, actuals, goal, note, to_report, grant_kpi_id, submission_id) FROM stdin;
\.


--
-- TOC entry 3791 (class 0 OID 306537)
-- Dependencies: 217
-- Data for Name: grant_section_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_section_attributes (id, deletable, field_name, field_type, required, type, section_id) FROM stdin;
1	f	What is need being addressed?	multiline	t	\N	1
2	f	Why do this?	multiline	t	\N	1
3	f	What is the proposed intervention?	multiline	t	\N	2
4	f	Describe the intervention	multiline	t	\N	2
5	f	Describe the main activities of the project	multiline	t	\N	2
6	f	Describe the key assumptions	multiline	t	\N	2
7	f	Describe the success factors	multiline	t	\N	2
8	f	Describe the risks to the project & mitigation plans	multiline	t	\N	3
9	f	Describe the impact outcomes	multiline	t	\N	4
10	f	Annual budget	multiline	t	\N	5
11	f	Disbursement schedule	multiline	t	\N	5
12	f	Bank Name	multiline	t	\N	5
13	f	Bank A/C No.	multiline	t	\N	5
14	f	Bank IFSC Code	multiline	t	\N	5
15	f	Severability	multiline	t	\N	6
16	f	Governing Law and Jursidiction	multiline	t	\N	6
17	f	Binding terms	multiline	t	\N	6
18	f	Insurance	multiline	t	\N	6
19	f	Notices	multiline	t	\N	6
20	f	Indemnification	multiline	t	\N	6
21	f	Assignment	multiline	t	\N	6
22	f	Amendment	multiline	t	\N	6
\.


--
-- TOC entry 3793 (class 0 OID 306545)
-- Dependencies: 219
-- Data for Name: grant_sections; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_sections (id, deletable, section_name) FROM stdin;
1	f	Purpose
2	f	Project Approach
3	t	Project Risks/challenges
4	t	Project Outcome Measurement & Evaluation
5	t	Budget & Finance Details
6	t	Grant Terms & Conditions
\.


--
-- TOC entry 3796 (class 0 OID 306552)
-- Dependencies: 222
-- Data for Name: grant_snapshot; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_snapshot (id, assigned_to_id, grant_id, grantee, string_attributes, name, description, amount, start_date, end_date, representative, grant_status_id) FROM stdin;
\.


--
-- TOC entry 3797 (class 0 OID 306559)
-- Dependencies: 223
-- Data for Name: grant_specific_section_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_specific_section_attributes (id, attribute_order, deletable, extras, field_name, field_type, required, granter_id, section_id) FROM stdin;
\.


--
-- TOC entry 3799 (class 0 OID 306567)
-- Dependencies: 225
-- Data for Name: grant_specific_sections; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_specific_sections (id, deletable, grant_id, grant_template_id, section_name, section_order, granter_id) FROM stdin;
\.


--
-- TOC entry 3801 (class 0 OID 306572)
-- Dependencies: 227
-- Data for Name: grant_string_attribute_attachments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_string_attribute_attachments (id, name, description, location, version, title, type, created_on, created_by, updated_on, updated_by, grant_string_attribute_id) FROM stdin;
\.


--
-- TOC entry 3802 (class 0 OID 306580)
-- Dependencies: 228
-- Data for Name: grant_string_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grant_string_attributes (id, frequency, target, value, grant_id, section_id, section_attribute_id) FROM stdin;
\.


--
-- TOC entry 3804 (class 0 OID 306588)
-- Dependencies: 230
-- Data for Name: grantees; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grantees (id) FROM stdin;
\.


--
-- TOC entry 3811 (class 0 OID 306626)
-- Dependencies: 237
-- Data for Name: granter_grant_section_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_grant_section_attributes (id, attribute_order, deletable, extras, field_name, field_type, required, granter_id, section_id) FROM stdin;
1	1	t	\N	What is the need being addressed?	multiline	t	2	1
2	2	t	\N	Why do this?	multiline	t	2	1
3	1	t	\N	What is the proposed intervention?	multiline	t	2	2
4	2	t	\N	Describe the intervention	multiline	t	2	2
5	3	t	\N	Describe the main activities of the project	multiline	t	2	2
6	4	t	\N	Describe the key assumptions	multiline	t	2	2
7	5	t	\N	Describe the success factors	multiline	t	2	2
8	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	3
9	1	t	\N	Describe the impact outcomes	multiline	t	2	4
10	1	t	\N	Annual budget	multiline	t	2	5
11	2	t	\N	Disbursement schedule	multiline	t	2	5
12	3	t	\N	Bank Name	multiline	t	2	5
13	4	t	\N	Bank A/C No.	multiline	t	2	5
14	5	t	\N	Bank IFSC Code	multiline	t	2	5
15	1	t	\N	Severability	multiline	t	2	6
16	2	t	\N	Governing Law and Jursidiction	multiline	t	2	6
17	3	t	\N	Binding terms	multiline	t	2	6
18	4	t	\N	Insurance	multiline	t	2	6
19	5	t	\N	Notices	multiline	t	2	6
20	6	t	\N	Indemnification	multiline	t	2	6
21	7	t	\N	Assignment	multiline	t	2	6
22	8	t	\N	Amendment	multiline	t	2	6
687	1	t	\N	Describe the impact outcomes	multiline	t	2	187
688	2	t	\N	Number of States	kpi	t	2	187
689	3	t	\N	Number of Districts	kpi	t	2	187
690	4	t	\N	Number of Villages	kpi	t	2	187
691	5	t	\N	Number of Households	kpi	t	2	187
692	1	t	\N	Annual budget	multiline	t	2	188
693	2	t	[{"name":"Amount (INR)","columns":[{"name":"Quarter 1","value":""},{"name":"Quarter 2","value":""},{"name":"Quarter 3","value":""},{"name":"Quarter 4","value":""},{"name":"","value":""}]},{"name":"Amount (INR) from other sources","columns":[{"name":"Quarter 1","value":""},{"name":"Quarter 2","value":""},{"name":"Quarter 3","value":""},{"name":"Quarter 4","value":""},{"name":"","value":""}]}]	Disbursement schedule	table	t	2	188
694	3	t	\N	Bank Name :	multiline	t	2	188
695	4	t	\N	Bank A/C No.	multiline	t	2	188
696	5	t	\N	Bank IFSC Code	multiline	t	2	188
697	6	t	\N	Branch Name & Address :	multiline	t	2	188
698	1	t	\N	Reports	document	t	2	189
699	3	t	\N	Plan	document	t	2	189
700	4	t	\N	Guidelines	document	t	2	189
701	1	t	\N	Name of Grantee Organization & Stamp :	multiline	t	2	190
702	2	t	\N		multiline	t	2	190
703	3	t	\N	 Authorized Signatory :	multiline	t	2	190
704	4	t	\N	Name & Designation :	multiline	t	2	190
45	2	t	NULL	Number of States	kpi	t	2	13
46	3	t	NULL	Number of Districts	kpi	t	2	13
47	4	t	NULL	Number of Villages	kpi	t	2	13
48	1	t	NULL	Describe the impact outcomes	multiline	t	2	13
49	5	t	NULL	Number of Households	kpi	f	2	13
50	1	t	NULL	Annual budget	multiline	t	2	14
51	2	t	[{"name":"Amount (INR)","columns":[{"name":"Quarter 1","value":""},{"name":"Quarter 2","value":""},{"name":"Quarter 3","value":""},{"name":"Quarter 4","value":""},{"name":"","value":""}]},{"name":"Amount (INR) from other sources","columns":[{"name":"Quarter 1","value":""},{"name":"Quarter 2","value":""},{"name":"Quarter 3","value":""},{"name":"Quarter 4","value":""},{"name":"","value":""}]}]	Disbursement schedule	table	t	2	14
52	3	t	NULL	Bank Name :	multiline	t	2	14
53	4	t	NULL	Bank A/C No.	multiline	t	2	14
54	5	t	NULL	Bank IFSC Code	multiline	t	2	14
55	6	t	NULL	Branch Name & Address :	multiline	t	2	14
56	1	t	NULL	Reports	document	t	2	15
57	3	t	NULL	Plan	document	t	2	15
58	4	t	NULL	Guidelines	document	t	2	15
59	3	t	NULL	 Authorized Signatory :	multiline	t	2	16
60	4	t	NULL	Name & Designation :	multiline	t	2	16
61	5	t	NULL	Date :	multiline	t	2	16
62	2	t	NULL		multiline	t	2	16
63	1	t	NULL	Name of Grantee Organization & Stamp :	multiline	t	2	16
64	1	t	NULL	What is the need being addressed?	multiline	t	2	17
65	2	t	NULL	Why do this?	multiline	t	2	17
66	4	t	NULL	Describe the key assumptions	multiline	t	2	18
67	5	t	NULL	Describe the success factors	multiline	t	2	18
68	1	t	NULL	What is the proposed intervention?	multiline	t	2	18
69	2	t	NULL	Describe the intervention	multiline	t	2	18
70	3	t	NULL	Describe the main activities of the project	multiline	t	2	18
71	1	t	NULL	Describe the risks to the project & mitigation plans	multiline	t	2	19
72	1	t	NULL	Severability	multiline	t	2	20
73	2	t	NULL	Governing Law and Jursidiction	multiline	t	2	20
74	3	t	NULL	Binding terms	multiline	t	2	20
75	4	t	NULL	Insurance	multiline	t	2	20
76	5	t	NULL	Notices	multiline	t	2	20
77	6	t	NULL	Indemnification	multiline	t	2	20
78	7	t	NULL	Assignment	multiline	t	2	20
79	8	t	NULL	Amendment	multiline	t	2	20
705	5	t	\N	Date :	multiline	t	2	190
706	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	191
707	1	t	\N	Severability	multiline	t	2	192
708	2	t	\N	Governing Law and Jursidiction	multiline	t	2	192
709	3	t	\N	Binding terms	multiline	t	2	192
710	4	t	\N	Insurance	multiline	t	2	192
711	5	t	\N	Notices	multiline	t	2	192
712	6	t	\N	Indemnification	multiline	t	2	192
713	7	t	\N	Assignment	multiline	t	2	192
714	8	t	\N	Amendment	multiline	t	2	192
715	1	t	\N	What is the need being addressed?	multiline	t	2	193
716	2	t	\N	Why do this?	multiline	t	2	193
717	2	t	\N	Describe the intervention	multiline	t	2	194
718	3	t	\N	Describe the main activities of the project	multiline	t	2	194
719	4	t	\N	Describe the key assumptions	multiline	t	2	194
720	5	t	\N	Describe the success factors	multiline	t	2	194
721	1	t	\N	What is the proposed intervention?	kpi	t	2	194
2068	1	t	\N	What is need being addressed?	multiline	f	11	591
2069	2	t	\N	Why do this?	multiline	f	11	591
2070	1	t	\N	What is the proposed intervention?	multiline	f	11	592
2071	2	t	\N	Describe the intervention	multiline	f	11	592
2072	3	t	\N	Describe the main activities of the project	multiline	f	11	592
2073	4	t	\N	Describe the key assumptions	multiline	f	11	592
2074	5	t	\N	Describe the success factors	multiline	f	11	592
2075	1	t	\N	Describe the risks to the project & mitigation plans	multiline	f	11	593
2076	1	t	\N	Describe the impact outcomes	multiline	f	11	594
2077	1	t	\N	Annual budget	multiline	f	11	595
2078	2	t	\N	Disbursement schedule	multiline	f	11	595
2079	3	t	\N	Bank Name	multiline	f	11	595
2080	4	t	\N	Bank A/C No.	multiline	f	11	595
2081	5	t	\N	Bank IFSC Code	multiline	f	11	595
2082	1	t	\N	Severability	multiline	f	11	596
2083	2	t	\N	Governing Law and Jursidiction	multiline	f	11	596
2084	3	t	\N	Binding terms	multiline	f	11	596
2085	4	t	\N	Insurance	multiline	f	11	596
2086	5	t	\N	Notices	multiline	f	11	596
2087	6	t	\N	Indemnification	multiline	f	11	596
2088	7	t	\N	Assignment	multiline	f	11	596
2089	8	t	\N	Amendment	multiline	f	11	596
2346	1	t	\N	What is need being addressed?	multiline	t	11	665
2347	2	t	\N	Why do this?	multiline	t	11	665
2348	1	t	\N	What is the proposed intervention?	multiline	t	11	666
2349	2	t	\N	Describe the intervention	multiline	t	11	666
2350	3	t	\N	Describe the main activities of the project	multiline	t	11	666
2351	4	t	\N	Describe the key assumptions	multiline	t	11	666
2352	5	t	\N	Describe the success factors	multiline	t	11	666
2353	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	667
2354	1	t	\N	Annual budget	multiline	t	11	668
2355	2	t	\N	Disbursement schedule	multiline	t	11	668
2356	3	t	\N	Bank Name	multiline	t	11	668
2357	4	t	\N	Bank A/C No.	multiline	t	11	668
2358	5	t	\N	Bank IFSC Code	multiline	t	11	668
2359	1	t	\N	Severability	multiline	t	11	669
2360	2	t	\N	Governing Law and Jursidiction	multiline	t	11	669
2361	3	t	\N	Binding terms	multiline	t	11	669
2362	4	t	\N	Insurance	multiline	t	11	669
2363	5	t	\N	Notices	multiline	t	11	669
2364	6	t	\N	Indemnification	multiline	t	11	669
2365	7	t	\N	Assignment	multiline	t	11	669
2366	8	t	\N	Amendment	multiline	t	11	669
2367	1	t	\N	Describe the impact outcomes	multiline	t	11	670
2368	2	t	\N	Number of homes visited	kpi	t	11	670
2369	3	t	\N	Number of individuals impacted	kpi	t	11	670
2370	1	t	\N		document	t	11	671
6377	1	t	\N	What is the proposed intervention?	table	t	11	1852
6378	2	t	\N	Describe the intervention	multiline	t	11	1852
6379	3	t	\N	Describe the main activities of the project	multiline	t	11	1852
6380	4	t	\N	Describe the key assumptions	multiline	t	11	1852
6381	5	t	\N	Describe the success factors	multiline	t	11	1852
6382	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1853
6383	1	t	\N	Describe the impact outcomes	multiline	t	11	1854
6384	2	t	\N	Number of homes visited	kpi	t	11	1854
6385	3	t	\N	Number of individuals impacted	kpi	t	11	1854
6386	1	t	\N	Annual budget	multiline	t	11	1855
6387	2	t	\N	Disbursement schedule	multiline	t	11	1855
6388	3	t	\N	Bank Name	multiline	t	11	1855
6389	4	t	\N	Bank A/C No.	multiline	t	11	1855
6390	5	t	\N	Bank IFSC Code	multiline	t	11	1855
6391	1	t	\N	Severability	multiline	t	11	1856
6392	2	t	\N	Governing Law and Jursidiction	multiline	t	11	1856
6393	3	t	\N	Binding terms	multiline	t	11	1856
6394	4	t	\N	Insurance	multiline	t	11	1856
5431	1	t	\N	Utilisation of funds 	multiline	t	11	1601
5432	1	t	\N	Objevtive of program 	multiline	t	11	1602
5433	2	t	\N	Disbursement schedule	multiline	t	11	1603
5434	3	t	\N	Bank Name	multiline	t	11	1603
2510	1	t	\N	Describe the impact outcomes	multiline	t	11	708
2511	6	t	\N	Disbursement	table	t	11	709
2512	1	t	\N	Annual budget	multiline	t	11	709
2513	2	t	\N	Disbursement schedule	multiline	t	11	709
2514	3	t	\N	Bank Name	multiline	t	11	709
2515	4	t	\N	Bank A/C No.	multiline	t	11	709
2516	5	t	\N	Bank IFSC Code	multiline	t	11	709
2517	6	t	\N	Indemnification	multiline	t	11	710
2518	7	t	\N	Assignment	multiline	t	11	710
2519	8	t	\N	Amendment	multiline	t	11	710
2520	1	t	\N	Governing Law and Jursidiction	multiline	t	11	710
2521	2	t	\N	Binding terms	multiline	t	11	710
2522	3	t	\N	Insurance	multiline	t	11	710
2523	4	t	\N	Notices	multiline	t	11	710
2394	1	t	\N	What is need being addressed?	multiline	t	11	678
2395	2	t	\N	Why do this?	multiline	t	11	678
2396	2	t	\N	Describe the intervention	multiline	t	11	679
2397	5	t	\N	Describe the success factors	multiline	t	11	679
2398	1	t	\N	What is the proposed intervention?	multiline	t	11	679
2399	3	t	\N	Describe the main activities of the project	multiline	t	11	679
2400	4	t	\N	Describe the key assumptions	multiline	t	11	679
2401	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	680
2402	1	t	\N	Describe the impact outcomes	multiline	t	11	681
2403	2	t	\N		kpi	t	11	681
2404	4	t	\N	Bank A/C No.	multiline	t	11	682
2057	2	t	\N	Objectives	multiline	t	2	588
2058	3	t	\N	Project specifics-I	kpi	t	2	588
2059	4	t	\N	Project specifics-II	kpi	t	2	588
2060	1	t	\N	Project deliverables	multiline	t	2	588
2061	5	t	\N	Name of the device	multiline	t	2	589
2062	1	t	\N	Targeted gap area	multiline	t	2	589
2063	2	t	\N	Targeted disease	multiline	t	2	589
2064	3	t	\N	Type of innovation	multiline	t	2	589
2065	4	t	\N	Scope of work	multiline	t	2	589
2066	2	t	[{"name":"Annual work plan","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Baseline survey report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Utilization Certificate","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Utilization Certificate","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Utilization Certificate","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Utilization Certificate","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Project end report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Disbursement timeline	table	t	2	590
2067	1	t	[{"name":"Total Budget","columns":[{"name":"Tranche Amount","value":""},{"name":"GST","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Tranche 1 (Preparatory tranche)","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Tranche 2","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Tranche 3","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Tranche 4","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Disbursement schedule	table	t	2	590
2405	1	t	\N	Annual budget	multiline	t	11	682
2406	2	t	\N	Disbursement schedule	multiline	t	11	682
2407	5	t	\N	Bank IFSC Code	multiline	t	11	682
2408	3	t	\N	Bank Name	multiline	t	11	682
2409	3	t	\N	Binding terms	multiline	t	11	683
2410	4	t	\N	Insurance	multiline	t	11	683
2411	1	t	\N	Severability	multiline	t	11	683
2412	2	t	\N	Governing Law and Jursidiction	multiline	t	11	683
2413	7	t	\N	Assignment	multiline	t	11	683
2414	5	t	\N	Notices	multiline	t	11	683
2415	8	t	\N	Amendment	multiline	t	11	683
2416	6	t	\N	Indemnification	multiline	t	11	683
2524	5	t	\N	Guideline	document	t	11	710
2525	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	711
2526	3	t	\N	Purpose new field	multiline	t	11	712
2527	2	t	\N	Why do this?	multiline	t	11	712
2528	1	t	\N	Number of homes visited	kpi	t	11	712
2529	1	t	\N	What is the proposed intervention?	multiline	t	11	713
2530	2	t	\N	Describe the intervention	multiline	t	11	713
2531	3	t	\N	Describe the main activities of the project	multiline	t	11	713
2532	4	t	\N	Describe the key assumptions	multiline	t	11	713
2533	5	t	\N	Describe the success factors	multiline	t	11	713
5435	4	t	\N	Bank A/C No.	multiline	t	11	1603
5436	5	t	\N	Bank IFSC Code	multiline	t	11	1603
5437	1	t	\N	Annual budget	document	t	11	1603
5438	1	t	\N	Describe the impact outcomes	multiline	t	11	1604
5439	2	t	\N	Binding Terms	multiline	t	11	1604
5440	1	t	\N	Email ID	multiline	t	11	1605
5441	2	t	\N	Main office Address 	multiline	t	11	1605
5442	3	t	\N	Contact Number 	multiline	t	11	1605
5443	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1606
5444	3	t	\N	7.\tChanges in Budget	multiline	t	11	1606
5445	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1606
2814	5	t	\N	Bank IFSC Code	multiline	t	11	789
2815	1	t	\N	Protective irrigation	kpi	t	11	790
2816	2	t	\N		multiline	t	11	790
2817	1	t	\N	Optimum utilization of the  water  for irrigation	kpi	t	11	791
2818	2	t	\N	Why do this?	multiline	t	11	792
2819	1	t	\N	Mobile Energy for Draught  prone farmers	multiline	t	11	792
2820	2	t	\N	Describe the intervention	multiline	t	11	793
2821	4	t	\N	Describe the key assumptions	multiline	t	11	793
2822	5	t	\N	Describe the success factors	multiline	t	11	793
2823	1	t	[{"name":"Value of bullock cart","columns":[{"name":"1 in Q1","value":""},{"name":"1 in Q2","value":""},{"name":"1 in Q3","value":""},{"name":"1 in Q4","value":""}]}]	Mobile energy cart - bullock cart	table	t	11	793
2824	3	t	\N	Describe the main activities of the project	multiline	t	11	793
2825	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	794
2826	1	t	\N	Describe the impact outcomes	multiline	t	11	795
2827	2	t	\N	Disbursement schedule	table	t	11	796
2828	3	t	\N	Bank Name	multiline	t	11	796
2829	4	t	\N	Bank A/C No.	multiline	t	11	796
2830	5	t	\N	Bank IFSC Code	multiline	t	11	796
2831	1	t	[{"name":"1 Project Manager","columns":[{"name":"For 12 Months","value":""}]},{"name":"2 Advisors","columns":[{"name":"For 12 Months","value":""}]},{"name":"2 Project team Manager","columns":[{"name":"For 12 Months","value":""}]}]	Program Personnel cost	table	t	11	796
2832	1	t	\N	Cash Payment	multiline	t	11	797
2833	2	t	\N	Governing Law and Jursidiction	multiline	t	11	797
2834	3	t	\N	Binding terms	multiline	t	11	797
2835	4	t	\N	Insurance	multiline	t	11	797
2836	5	t	\N	Notices	multiline	t	11	797
2837	6	t	\N	Indemnification	multiline	t	11	797
2838	7	t	\N	Assignment	multiline	t	11	797
2839	8	t	\N	Amendment	multiline	t	11	797
2840	2	t	\N	Why do this?	multiline	t	11	798
2841	1	t	\N	Mobile Energy for Draught  prone farmers	multiline	t	11	798
2842	2	t	\N	Describe the intervention	multiline	t	11	799
2843	4	t	\N	Describe the key assumptions	multiline	t	11	799
2844	5	t	\N	Describe the success factors	multiline	t	11	799
2845	1	t	[{"name":"Value of bullock cart","columns":[{"name":"1 in Q1","value":""},{"name":"1 in Q2","value":""},{"name":"1 in Q3","value":""},{"name":"1 in Q4","value":""}]}]	Mobile energy cart - bullock cart	table	t	11	799
2846	3	t	\N	Describe the main activities of the project	multiline	t	11	799
2847	2	t	\N		multiline	f	11	800
2848	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	800
5446	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2	table	t	11	1606
5447	3	t	\N	3.\tQuarterly Reports: 	multiline	t	11	1607
5448	4	t	\N	4.Project End Report: 	multiline	t	11	1607
5449	5	t	\N	5.\tAudited Statement of Accounts	multiline	t	11	1607
5450	2	t	\N	2.Baseline Survey Report	multiline	t	11	1607
5451	1	t	\N	1.\tAnnual Work Plan (AWP)	multiline	t	11	1607
2849	1	t	\N	Describe the impact outcomes	multiline	t	11	801
2850	2	t	[{"name":"CINI","columns":[{"name":"Aug-19","value":""},{"name":"Nov-19","value":""},{"name":"Feb-2020","value":""},{"name":"May-2020","value":""}]},{"name":"Other Source","columns":[{"name":"Aug-19","value":""},{"name":"Nov-19","value":""},{"name":"Feb-2020","value":""},{"name":"May-2020","value":""}]}]	Disbursement schedule	table	t	11	802
2851	3	t	\N	Bank Name	multiline	t	11	802
2852	4	t	\N	Bank A/C No.	multiline	t	11	802
2463	1	t	\N	Describe the impact outcomes	multiline	t	11	696
2464	1	t	\N	Annual budget	multiline	t	11	697
2465	2	t	\N	Disbursement schedule	multiline	t	11	697
2466	3	t	\N	Bank Name	multiline	t	11	697
2467	4	t	\N	Bank A/C No.	multiline	t	11	697
2468	5	t	\N	Bank IFSC Code	multiline	t	11	697
2469	2	t	\N	Governing Law and Jursidiction	multiline	t	11	698
2470	3	t	\N	Binding terms	multiline	t	11	698
2471	4	t	\N	Insurance	multiline	t	11	698
2472	5	t	\N	Notices	multiline	t	11	698
2473	6	t	\N	Indemnification	multiline	t	11	698
2136	1	t	\N	What is the need being addressed?	multiline	t	2	609
2137	2	t	\N	Why do this?	multiline	t	2	609
2138	1	t	\N	Annual budget	multiline	t	2	610
2139	2	t	\N	Disbursement schedule	multiline	t	2	610
2140	3	t	\N	Bank Name	multiline	t	2	610
2141	4	t	\N	Bank A/C No.	multiline	t	2	610
2142	5	t	\N	Bank IFSC Code	multiline	t	2	610
2143	1	t	\N	Severability	multiline	t	2	611
1986	2	t	\N	Objectives	multiline	t	2	570
1987	3	t	\N	Project specifics-I	kpi	t	2	570
1988	1	t	\N	Project deliverables	multiline	t	2	570
1989	4	t	\N	Project specifics-II	kpi	t	2	570
1990	5	t	\N	Project specifics-III	kpi	t	2	570
1991	5	t	\N	Name of the device	multiline	t	2	571
1992	6	t	\N		multiline	t	2	571
1993	3	t	\N	Type of innovation	multiline	t	2	571
1994	2	t	\N	Targeted disease	multiline	t	2	571
1995	1	t	\N	Targeted gap area	multiline	t	2	571
1996	4	t	\N	Scope of work	multiline	t	2	571
1997	1	t	[{"name":"Payment Schedule","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Total budget","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"At the time of project initiation subject to production of proforma invoice","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"The following amounts will be based upon deliverables and submission of Proforma invoice. The final tranche will be transfered based upon original invoice upon completion of the project.","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Disbursement schedule	table	t	2	572
1998	2	t	[{"name":"Annual work plan","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Baseline survey report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Quarterly progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Utilisation certificate","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Annual progress report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Audited statement of accounts","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Project end report","columns":[{"name":"Frequency","value":""},{"name":"Submission due date","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Disbursement timeline	table	t	2	572
2144	2	t	\N	Governing Law and Jursidiction	multiline	t	2	611
2145	3	t	\N	Binding terms	multiline	t	2	611
2146	4	t	\N	Insurance	multiline	t	2	611
2147	5	t	\N	Notices	multiline	t	2	611
2148	6	t	\N	Indemnification	multiline	t	2	611
2149	7	t	\N	Assignment	multiline	t	2	611
2150	8	t	\N	Amendment	multiline	t	2	611
2151	1	t	\N	What is the proposed intervention?	multiline	t	2	612
2152	2	t	\N	Describe the intervention	multiline	t	2	612
2153	3	t	\N	Describe the main activities of the project	multiline	t	2	612
2154	4	t	\N	Describe the key assumptions	multiline	t	2	612
2155	5	t	\N	Describe the success factors	multiline	t	2	612
2156	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	613
2157	1	t	\N	Describe the impact outcomes	kpi	t	2	614
2158	2	t	\N	Consequatur nam eveniet esse.	document	t	2	614
2474	7	t	\N	Assignment	multiline	t	11	698
2475	8	t	\N	Amendment	multiline	t	11	698
2476	1	t	\N	Guideline	document	t	11	698
2477	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	699
2478	3	t	\N	Purpose new field	multiline	t	11	700
2479	2	t	\N	Why do this?	multiline	t	11	700
2480	1	t	\N	Number of homes visited	kpi	t	11	700
2481	1	t	\N	What is the proposed intervention?	multiline	t	11	701
2482	2	t	\N	Describe the intervention	multiline	t	11	701
2483	3	t	\N	Describe the main activities of the project	multiline	t	11	701
2484	4	t	\N	Describe the key assumptions	multiline	t	11	701
2485	5	t	\N	Describe the success factors	multiline	t	11	701
2797	1	t	\N	To be completed within the period and  periodical reporting to be submitted to Sustain Plus	kpi	t	11	786
2798	3	t	\N	Binding terms	multiline	t	11	786
2799	4	t	\N	Insurance	multiline	t	11	786
2800	5	t	\N	Notices	multiline	t	11	786
2801	6	t	\N	Indemnification	multiline	t	11	786
2802	7	t	\N	Assignment	multiline	t	11	786
2803	8	t	\N	Amendment	multiline	t	11	786
2804	2	t	\N	Describe the intervention	multiline	t	11	787
2805	3	t	\N	Describe the main activities of the project	multiline	t	11	787
2806	4	t	\N	Describe the key assumptions	multiline	t	11	787
2807	5	t	\N	Describe the success factors	multiline	t	11	787
2808	1	t	\N	Draught Mitigation	kpi	t	11	787
2809	1	t	\N	Identifying the drought  effected areas and educating the farmers on protective irrigation	kpi	t	11	788
2810	1	t	[{"name":"Program cost for 1 year","columns":[{"name":"No of projects","value":""},{"name":"No of Household covered","value":""},{"name":"Cost for each Project","value":""},{"name":"Total cost","value":""},{"name":"","value":""}]}]	Program Cost	table	t	11	789
2811	2	t	\N	Disbursement schedule	multiline	t	11	789
2812	3	t	\N	Bank Name	multiline	t	11	789
2813	4	t	\N	Bank A/C No.	multiline	t	11	789
2853	5	t	\N	Bank IFSC Code	multiline	t	11	802
2854	1	t	[{"name":"1 Project Manager","columns":[{"name":"For 12 Months","value":""}]},{"name":"2 Advisors","columns":[{"name":"For 12 Months","value":""}]},{"name":"2 Project team Manager","columns":[{"name":"For 12 Months","value":""}]}]	Program Personnel cost	table	t	11	802
2855	1	t	\N	Cash Payment	multiline	t	11	803
2856	2	t	\N	Governing Law and Jursidiction	multiline	t	11	803
2857	3	t	\N	Binding terms	multiline	t	11	803
2858	4	t	\N	Insurance	multiline	t	11	803
2859	5	t	\N	Notices	multiline	t	11	803
2860	6	t	\N	Indemnification	multiline	t	11	803
2861	7	t	\N	Assignment	multiline	t	11	803
2862	8	t	\N	Amendment	multiline	t	11	803
5452	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1608
5453	1	t	\N	4.\tDisbursement of the grant	table	t	11	1609
5454	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1610
5455	2	t	\N	 10.\tInternal Systems	multiline	t	11	1610
5456	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1610
6395	5	t	\N	Notices	multiline	t	11	1856
4604	2	t	\N		multiline	t	11	1324
4605	1	t	[{"name":"1","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"2","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"3","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]		table	t	11	1324
4606	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1325
4607	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2: Report Submission Due dates	table	t	11	1325
4608	3	t	\N	7.\tChanges in Budget	multiline	t	11	1325
4609	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1325
4610	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1326
4611	2	t	\N	 10.\tInternal Systems	multiline	t	11	1326
4612	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1326
4613	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1326
4614	5	t	\N	13.\tCash payments	multiline	t	11	1326
6396	6	t	\N	Indemnification	multiline	t	11	1856
6397	7	t	\N	Assignment	multiline	t	11	1856
6398	8	t	\N	Amendment	multiline	t	11	1856
6399	1	t	\N	Guidelines	document	t	11	1857
3052	1	t	[{"name":"Program cost for 1 year","columns":[{"name":"No of projects","value":""},{"name":"No of Household covered","value":""},{"name":"Cost for each Project","value":""},{"name":"Total cost","value":""}]}]	Program Cost	table	t	11	859
3053	2	t	[{"name":"Disbursement","columns":[{"name":"April 20","value":""},{"name":"July 20","value":""},{"name":"Oct 20","value":""},{"name":"Jan 21","value":""},{"name":"","value":""}]}]	Disbursement schedule	table	t	11	859
3054	3	t	\N	Bank Name	multiline	t	11	859
3055	4	t	\N	Bank A/C No.	multiline	t	11	859
3056	5	t	\N	Bank IFSC Code	multiline	t	11	859
3057	1	t	\N	To be completed within the period and  periodical reporting to be submitted to Sustain Plus	kpi	t	11	860
3058	3	t	\N	Binding terms	multiline	t	11	860
3059	4	t	\N	Insurance	multiline	t	11	860
3060	5	t	\N	Notices	multiline	t	11	860
3061	6	t	\N	Indemnification	multiline	t	11	860
3062	7	t	\N	Assignment	multiline	t	11	860
3063	8	t	\N	Amendment	multiline	t	11	860
3064	1	t	\N		multiline	t	11	861
3065	1	t	\N		multiline	t	11	862
3066	4	t	\N		multiline	t	11	862
3067	3	t	\N		multiline	t	11	862
4240	1	t	[{"name":"1","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"2","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"3","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]		table	t	11	1217
4241	1	t	\N		multiline	t	11	1218
4242	3	t	\N		multiline	t	11	1218
4243	2	t	\N		multiline	t	11	1218
4244	1	t	\N	Annual budget	table	t	11	1219
4245	2	t	\N	Disbursement schedule	multiline	t	11	1219
4246	3	t	\N	Bank Name	multiline	t	11	1219
4247	4	t	\N	Bank A/C No.	multiline	t	11	1219
4248	5	t	\N	Bank IFSC Code	multiline	t	11	1219
4249	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	 Report Submission Due dates	table	t	11	1220
4250	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1220
4251	3	t	\N	7.\tChanges in Budget	multiline	t	11	1220
4252	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1220
4253	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1221
4254	2	t	\N	 10.\tInternal Systems	multiline	t	11	1221
4255	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1221
4256	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1221
4257	5	t	\N	13.\tCash payments	multiline	t	11	1221
4258	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1221
4259	7	t	\N	15.\tAudit by CInI	multiline	t	11	1221
4260	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1221
4261	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1221
4262	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1221
4263	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1221
4264	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1221
4265	13	t	\N	21.\tIndemnification	multiline	t	11	1221
4266	3	t	\N	3.\tQuarterly Reports: 	multiline	t	11	1222
4267	2	t	\N	2.Baseline Survey Report	multiline	t	11	1222
4268	1	t	\N	1.\tAnnual Work Plan (AWP)	multiline	t	11	1222
4269	4	t	\N	4.Project End Report: 	multiline	t	11	1222
4270	5	t	\N	5.\tAudited Statement of Accounts	multiline	t	11	1222
4271	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1223
4272	1	t	\N	Describe the impact outcomes	multiline	t	11	1224
4273	2	t	\N	Binding Terms	multiline	t	11	1224
4274	1	t	\N		multiline	t	11	1225
4275	1	t	\N		multiline	t	11	1226
4615	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1326
4616	7	t	\N	15.\tAudit by CInI	multiline	t	11	1326
4617	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1326
4618	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1326
4619	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1326
4620	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1326
4621	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1326
4622	13	t	\N	21.\tIndemnification	multiline	t	11	1326
4623	4	t	\N	4. Project End Report: 	multiline	t	11	1327
4624	5	t	\N	5. Audited Statement of Accounts	multiline	t	11	1327
4625	1	t	\N	1. Annual Work Plan (AWP)	multiline	t	11	1327
4626	2	t	\N	2. Baseline Survey Report	multiline	t	11	1327
4627	3	t	\N	3. Quarterly Reports: 	multiline	t	11	1327
4628	1	t	\N		multiline	t	11	1328
4629	3	t	\N		multiline	t	11	1328
4630	2	t	\N		multiline	t	11	1328
4631	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1329
4632	1	t	\N	Describe the impact outcomes	multiline	t	11	1330
4633	2	t	\N	Binding Terms	multiline	t	11	1330
4634	3	t	\N		multiline	t	11	1330
4635	1	t	[{"name":"Budget Head","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Personnel","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Program Cost","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Annual budget	table	t	11	1331
4636	1	t	\N		multiline	t	11	1332
4637	1	t	\N		multiline	t	11	1333
4638	1	t	\N		multiline	t	11	1334
5457	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1610
5458	5	t	\N	13.\tCash payments	multiline	t	11	1610
5459	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1610
5460	7	t	\N	15.\tAudit by CInI	multiline	t	11	1610
5461	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1610
5462	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1610
5463	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1610
5464	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1610
5465	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1610
5466	13	t	\N	21.\tIndemnification	multiline	t	11	1610
7302	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	2103
7303	3	t	\N	7.\tChanges in Budget	multiline	t	11	2103
7304	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	2103
7305	1	t	\N	1. Annual Work Plan (AWP)	multiline	t	11	2104
4710	1	t	\N		multiline	t	11	1359
4711	3	t	\N		multiline	t	11	1359
4712	2	t	\N		multiline	t	11	1359
4713	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1360
4714	1	t	\N	Describe the impact outcomes	multiline	t	11	1361
4715	2	t	\N	Binding Terms	multiline	t	11	1361
4716	3	t	\N		multiline	t	11	1361
4717	1	t	[{"name":"Budget Head","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Personnel","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Program Cost","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Annual budget	table	t	11	1362
4718	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1363
4719	2	t	\N	 10.\tInternal Systems	multiline	t	11	1363
4720	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1363
4721	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1363
4722	5	t	\N	13.\tCash payments	multiline	t	11	1363
4723	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1363
4724	7	t	\N	15.\tAudit by CInI	multiline	t	11	1363
4725	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1363
4726	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1363
4727	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1363
4728	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1363
4729	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1363
4730	13	t	\N	21.\tIndemnification	multiline	t	11	1363
4731	2	t	\N		multiline	t	11	1364
4732	1	t	[{"name":"1","columns":[{"name":"Period","value":""},{"name":"Amount (in Rs)","value":""},{"name":"Fund raised from other sources (in Rs)","value":""}]},{"name":"2","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"3","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total Amounts (in Rs)","columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]		table	t	11	1364
4733	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1365
4734	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2: Report Submission Due dates	table	t	11	1365
4735	3	t	\N	7.\tChanges in Budget	multiline	t	11	1365
4736	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1365
4737	4	t	\N	4. Project End Report: 	multiline	t	11	1366
4738	5	t	\N	5. Audited Statement of Accounts	multiline	t	11	1366
4739	1	t	\N	1. Annual Work Plan (AWP)	multiline	t	11	1366
4740	2	t	\N	2. Baseline Survey Report	multiline	t	11	1366
4741	3	t	\N	3. Quarterly Reports: 	multiline	t	11	1366
4742	1	t	\N		multiline	t	11	1367
4743	1	t	\N		multiline	t	11	1368
4744	1	t	\N		multiline	t	11	1369
4745	1	t	\N		document	t	11	1370
7306	2	t	\N	2. Baseline Survey Report	multiline	t	11	2104
7307	3	t	\N	3. Quarterly Reports: 	multiline	t	11	2104
7308	4	t	\N	4. Project End Report: 	multiline	t	11	2104
7309	5	t	\N	5. Audited Statement of Accounts	multiline	t	11	2104
7310	1	t	\N		multiline	t	11	2105
9568	1	t	\N	What is the proposed intervention?	multiline	t	11	2593
9569	2	t	\N	Describe the intervention	multiline	t	11	2593
9570	2	t	\N	Aut sit consequatur qui provident sit hic voluptatem minus voluptatum.	kpi	t	11	2594
9571	3	t	\N	Documents	document	t	11	2594
9572	1	t	\N	Describe the risks to the project & mitigation plans	kpi	t	11	2594
9573	1	t	\N	Describe the impact outcomes	multiline	t	11	2595
9574	1	t	\N	Annual budget	multiline	t	11	2596
9575	3	t	\N	Bank Name	multiline	t	11	2596
9576	4	t	\N	Bank A/C No.	multiline	t	11	2596
9577	5	t	\N	Bank IFSC Code	multiline	t	11	2596
9578	2	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false},{"name":"2","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false},{"name":"3","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false}]	Disbursement schedule	disbursement	t	11	2596
9579	1	t	\N	What is need being addressed?	multiline	t	11	2597
9580	2	t	\N	Why do this?	multiline	t	11	2597
5956	3	t	\N		multiline	t	11	1747
5957	2	t	\N		multiline	t	11	1747
5958	4	t	\N		multiline	t	11	1747
5959	1	t	\N		multiline	t	11	1748
5960	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1749
5961	1	t	\N		multiline	t	11	1750
5962	2	t	\N	Binding Terms	multiline	t	11	1750
5963	3	t	\N		multiline	t	11	1750
5964	4	t	\N		multiline	t	11	1750
5965	5	t	\N	ACCEPTED & AGREED	multiline	t	11	1750
5966	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1751
5967	2	t	\N	 10.\tInternal Systems	multiline	t	11	1751
5968	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1751
5969	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1751
5970	5	t	\N	13.\tCash payments	multiline	t	11	1751
5971	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1751
5972	7	t	\N	15.\tAudit by CInI	multiline	t	11	1751
5973	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1751
5974	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1751
5975	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1751
5976	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1751
5977	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1751
5978	13	t	\N	21.\tIndemnification	multiline	t	11	1751
5979	3	t	\N	3.\tQuarterly Reports: 	multiline	t	11	1752
5980	4	t	\N	4.Project End Report: 	multiline	t	11	1752
5981	5	t	\N	5.\tAudited Statement of Accounts	multiline	t	11	1752
5982	2	t	\N	2.Baseline Survey Report	multiline	t	11	1752
5983	1	t	\N	1.\tAnnual Work Plan (AWP)	multiline	t	11	1752
5254	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1549
5255	1	t	\N	26th Floor, Tata Trusts, Centre 1, World Trade Centre 	multiline	t	11	1550
5256	2	t	\N		multiline	t	11	1550
5257	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1551
5258	2	t	\N	 10.\tInternal Systems	multiline	t	11	1551
5259	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1551
5260	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1551
5261	5	t	\N	13.\tCash payments	multiline	t	11	1551
5262	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1551
5263	7	t	\N	15.\tAudit by CInI	multiline	t	11	1551
5264	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1551
5265	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1551
5266	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1551
5267	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1551
5268	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1551
5269	13	t	\N	21.\tIndemnification	multiline	t	11	1551
5270	1	t	[{"name":"Annual Work Plan","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Quarterly Reports","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Quarterly Reports","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Quarterly Reports","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Project End Report","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]},{"name":"Audited statement of accounts","columns":[{"name":"Year","value":""},{"name":"Month","value":""}]}]	Reports should be submitted as per below mentioned timelines	table	t	11	1552
5271	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2: Report Submission Due dates	table	t	11	1553
5272	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1553
5273	3	t	\N	7.\tChanges in Budget	multiline	t	11	1553
5274	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1553
5275	1	t	\N	Providing solar pumps to Agariyas	multiline	t	11	1554
5276	1	t	\N		multiline	t	11	1555
5277	1	t	[{"name":"January","columns":[{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"March","columns":[{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"December","columns":[{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total","columns":[{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]	Disbursement schedule	table	t	11	1556
5278	2	t	\N		multiline	t	11	1556
5279	1	t	\N	Governing Law and Jurisdication	multiline	t	11	1557
5280	2	t	\N	Binding Terms	multiline	t	11	1557
5281	3	t	\N		multiline	t	11	1557
5282	1	t	[{"name":"Personnel","columns":[{"name":"Sustain+ Year 1","value":""},{"name":"Sustain+ Year 2","value":""},{"name":"Community Contribution","value":""},{"name":"Other sources","value":""},{"name":"Total","value":""}]},{"name":"Program Cost","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Overhead Cost","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Grand Total","columns":[{"name":"Sustain+ Year 1","value":""},{"name":"Sustain+ Year 2","value":""},{"name":"Community Contribution","value":""},{"name":"Other sources","value":""},{"name":"Total","value":""}]}]	Annual budget	table	t	11	1558
5283	1	t	\N	Utilization of Funds	multiline	t	11	1559
5284	2	t	\N	Learning events	multiline	t	11	1560
5285	4	t	\N		kpi	t	11	1560
5286	1	t	\N	Increase In area under irrigation	kpi	t	11	1560
7449	1	t	\N	Describe the impact outcomes	multiline	t	2	2147
7450	2	t	\N	Number of States	kpi	t	2	2147
7451	3	t	\N	Number of Districts	kpi	t	2	2147
7452	4	t	\N	Number of Villages	kpi	t	2	2147
7453	5	t	\N	Number of Households	kpi	t	2	2147
7454	1	t	\N	Annual budget	multiline	t	2	2148
7455	3	t	\N	Bank Name :	multiline	t	2	2148
7456	4	t	\N	Bank A/C No.	multiline	t	2	2148
7457	5	t	\N	Bank IFSC Code	multiline	t	2	2148
7458	6	t	\N	Branch Name & Address :	multiline	t	2	2148
7459	2	t	\N	Disbursement schedule	disbursement	t	2	2148
7460	1	t	\N	Reports	document	t	2	2149
7461	3	t	\N	Plan	document	t	2	2149
7462	4	t	\N	Guidelines	document	t	2	2149
7463	1	t	\N	Name of Grantee Organization & Stamp :	multiline	t	2	2150
7464	2	t	\N		multiline	t	2	2150
7465	3	t	\N	 Authorized Signatory :	multiline	t	2	2150
7466	4	t	\N	Name & Designation :	multiline	t	2	2150
7467	5	t	\N	Date :	multiline	t	2	2150
7468	1	t	\N	What is the need being addressed?	multiline	t	2	2151
7469	2	t	\N	Why do this?	multiline	t	2	2151
7470	1	t	\N	What is the proposed intervention?	multiline	t	2	2152
7471	2	t	\N	Describe the intervention	multiline	t	2	2152
7472	3	t	\N	Describe the main activities of the project	multiline	t	2	2152
7473	4	t	\N	Describe the key assumptions	multiline	t	2	2152
7474	5	t	\N	Describe the success factors	multiline	t	2	2152
7475	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	2153
7476	1	t	\N	Severability	multiline	t	2	2154
7477	2	t	\N	Governing Law and Jursidiction	multiline	t	2	2154
7478	3	t	\N	Binding terms	multiline	t	2	2154
7479	4	t	\N	Insurance	multiline	t	2	2154
7480	5	t	\N	Notices	multiline	t	2	2154
7481	6	t	\N	Indemnification	multiline	t	2	2154
7482	7	t	\N	Assignment	multiline	t	2	2154
7483	8	t	\N	Amendment	multiline	t	2	2154
7484	3	t	\N	Bank Name :	multiline	t	2	2155
7485	4	t	\N	Bank A/C No.	multiline	t	2	2155
7486	5	t	\N	Bank IFSC Code	multiline	t	2	2155
7487	6	t	\N	Branch Name & Address :	multiline	t	2	2155
7488	1	t	\N	Annual budget	disbursement	t	2	2155
7489	2	t	[{"name":"Amount (INR)","header":null,"columns":[{"name":"Quarter 1","value":"","dataType":null},{"name":"Quarter 2","value":"","dataType":null},{"name":"Quarter 3","value":"","dataType":null},{"name":"Quarter 4","value":"","dataType":null},{"name":"","value":"","dataType":null}]},{"name":"Amount (INR) from other sources","header":null,"columns":[{"name":"Quarter 1","value":"","dataType":null},{"name":"Quarter 2","value":"","dataType":null},{"name":"Quarter 3","value":"","dataType":null},{"name":"Quarter 4","value":"","dataType":null},{"name":"","value":"","dataType":null}]}]	Disbursement schedule	table	t	2	2155
5950	1	t	\N	Annual budget	document	t	11	1743
5951	2	t	\N		multiline	t	11	1743
5952	1	t	\N		document	t	11	1744
5953	1	t	[{"name":"Installment No 1","columns":[{"name":" Period","value":""},{"name":"Amount (In Rs.)","value":""},{"name":" Funds raised from Other  Sources (In Rs. )","value":""}]},{"name":"Installment No 2","columns":[{"name":" Period","value":""},{"name":"Amount (In Rs.)","value":""},{"name":" Funds raised from Other  Sources (In Rs. )","value":""}]},{"name":"Total ","columns":[{"name":" Period","value":""},{"name":"Amount (In Rs.)","value":""},{"name":" Funds raised from Other  Sources (In Rs. )","value":""}]}]		table	t	11	1745
5954	2	t	\N		multiline	t	11	1745
7490	1	t	\N	Reports	document	t	2	2156
7491	3	t	\N	Plan	document	t	2	2156
7492	4	t	\N	Guidelines	document	t	2	2156
7493	2	t	\N	Number of States	kpi	t	2	2157
7494	3	t	\N	Number of Districts	kpi	t	2	2157
7495	4	t	\N	Number of Villages	kpi	t	2	2157
7496	5	t	\N	Number of Households	kpi	t	2	2157
7497	1	t	\N	Describe the impact outcomes	multiline	t	2	2157
7498	1	t	\N	Name of Grantee Organization & Stamp :	multiline	t	2	2158
7499	2	t	\N		multiline	t	2	2158
7500	3	t	\N	 Authorized Signatory :	multiline	t	2	2158
7501	4	t	\N	Name & Designation :	multiline	t	2	2158
7502	5	t	\N	Date :	multiline	t	2	2158
7503	1	t	\N	What is the need being addressed?	multiline	t	2	2159
7504	2	t	\N	Why do this?	multiline	t	2	2159
7505	1	t	\N	What is the proposed intervention?	multiline	t	2	2160
7506	2	t	\N	Describe the intervention	multiline	t	2	2160
7507	3	t	\N	Describe the main activities of the project	multiline	t	2	2160
7508	4	t	\N	Describe the key assumptions	multiline	t	2	2160
7509	5	t	\N	Describe the success factors	multiline	t	2	2160
7510	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	2161
7511	1	t	\N	Severability	multiline	t	2	2162
7512	2	t	\N	Governing Law and Jursidiction	multiline	t	2	2162
7513	3	t	\N	Binding terms	multiline	t	2	2162
7514	4	t	\N	Insurance	multiline	t	2	2162
7515	5	t	\N	Notices	multiline	t	2	2162
7516	6	t	\N	Indemnification	multiline	t	2	2162
7517	7	t	\N	Assignment	multiline	t	2	2162
7518	8	t	\N	Amendment	multiline	t	2	2162
5955	1	t	\N		multiline	t	11	1746
5984	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	1753
5985	3	t	\N	7.\tChanges in Budget	multiline	t	11	1753
5986	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	1753
5987	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Utilisation Certificate (UC) Quarterly unaudited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Utilisation Certificate (UC) Six monthly audited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"FCRA Quarterly Intimation","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Project End Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Audited Statement of Accounts","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"FCRA Annual Return FC-4","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2	table	t	11	1753
7519	1	t	\N	Describe the impact outcomes	multiline	t	2	2163
7520	2	t	\N	Number of States	kpi	t	2	2163
7521	3	t	\N	Number of Districts	kpi	t	2	2163
7522	4	t	\N	Number of Villages	kpi	t	2	2163
7523	5	t	\N	Number of Households	kpi	t	2	2163
7524	2	t	[{"name":"Amount (INR)","header":null,"columns":[{"name":"Quarter 1","value":"","dataType":null},{"name":"Quarter 2","value":"","dataType":null},{"name":"Quarter 3","value":"","dataType":null},{"name":"Quarter 4","value":"","dataType":null},{"name":"","value":"","dataType":null}]},{"name":"Amount (INR) from other sources","header":null,"columns":[{"name":"Quarter 1","value":"","dataType":null},{"name":"Quarter 2","value":"","dataType":null},{"name":"Quarter 3","value":"","dataType":null},{"name":"Quarter 4","value":"","dataType":null},{"name":"","value":"","dataType":null}]}]	Disbursement schedule	table	t	2	2164
7525	3	t	\N	Bank Name :	multiline	t	2	2164
7526	4	t	\N	Bank A/C No.	multiline	t	2	2164
7527	5	t	\N	Bank IFSC Code	multiline	t	2	2164
7528	6	t	\N	Branch Name & Address :	multiline	t	2	2164
7529	1	t	\N	Annual budget	disbursement	t	2	2164
7530	1	t	\N	Reports	document	t	2	2165
7531	3	t	\N	Plan	document	t	2	2165
7532	4	t	\N	Guidelines	document	t	2	2165
7533	1	t	\N	Name of Grantee Organization & Stamp :	multiline	t	2	2166
7534	2	t	\N		multiline	t	2	2166
7535	3	t	\N	 Authorized Signatory :	multiline	t	2	2166
7536	4	t	\N	Name & Designation :	multiline	t	2	2166
7537	5	t	\N	Date :	multiline	t	2	2166
7538	1	t	\N	What is the need being addressed?	multiline	t	2	2167
7539	2	t	\N	Why do this?	multiline	t	2	2167
7540	1	t	\N	What is the proposed intervention?	multiline	t	2	2168
7541	2	t	\N	Describe the intervention	multiline	t	2	2168
7542	3	t	\N	Describe the main activities of the project	multiline	t	2	2168
7543	4	t	\N	Describe the key assumptions	multiline	t	2	2168
7544	5	t	\N	Describe the success factors	multiline	t	2	2168
7545	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	2169
7546	1	t	\N	Severability	multiline	t	2	2170
7547	2	t	\N	Governing Law and Jursidiction	multiline	t	2	2170
7548	3	t	\N	Binding terms	multiline	t	2	2170
7549	4	t	\N	Insurance	multiline	t	2	2170
7550	5	t	\N	Notices	multiline	t	2	2170
7551	6	t	\N	Indemnification	multiline	t	2	2170
7552	7	t	\N	Assignment	multiline	t	2	2170
7553	8	t	\N	Amendment	multiline	t	2	2170
8813	6	t	\N	4.   Quarterly Reports: 	multiline	t	11	2406
8814	9	t	\N	6.   Audited Statement of Accounts	multiline	t	11	2406
8815	10	t	\N	7.   Contributions from Other Sources brought into the Accounts of TLMTI	multiline	t	11	2406
8816	11	t	\N	8.   Approvals for Budget Changes	multiline	t	11	2406
8817	2	t	\N	See attached guideline (Annexure 8) and template (Annexure 4) documents	document	t	11	2406
8818	3	t	\N	Books of Accounts	multiline	t	11	2407
8819	4	t	\N	Procedure for Booking of Expenses	multiline	t	11	2407
8820	6	t	\N	Interest earned on grant funds	multiline	t	11	2407
8821	7	t	\N	Audit by CInI	multiline	t	11	2407
7332	2	t	\N	Why do this?	multiline	t	11	2112
7333	1	t	\N	What is need being addressed?	multiline	t	11	2112
7334	1	t	\N	What is the proposed intervention?	multiline	t	11	2113
7335	2	t	\N	Describe the intervention	multiline	t	11	2113
7336	3	t	\N	Describe the main activities of the project	multiline	t	11	2113
7337	4	t	\N	Describe the key assumptions	multiline	t	11	2113
7338	5	t	\N	Describe the success factors	multiline	t	11	2113
7339	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2114
7340	1	t	\N	Annual budget	multiline	t	11	2115
7341	2	t	\N	Disbursement schedule	multiline	t	11	2115
7342	3	t	\N	Bank Name	multiline	t	11	2115
7343	4	t	\N	Bank A/C No.	multiline	t	11	2115
7344	5	t	\N	Bank IFSC Code	multiline	t	11	2115
7345	1	t	\N	Severability	multiline	t	11	2116
7346	2	t	\N	Governing Law and Jursidiction	multiline	t	11	2116
7347	3	t	\N	Binding terms	multiline	t	11	2116
7348	4	t	\N	Insurance	multiline	t	11	2116
7349	5	t	\N	Notices	multiline	t	11	2116
7350	6	t	\N	Indemnification	multiline	t	11	2116
7351	7	t	\N	Assignment	multiline	t	11	2116
7352	8	t	\N	Amendment	multiline	t	11	2116
8039	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	2	2279
8040	1	t	\N	Severability	multiline	t	2	2280
8041	2	t	\N	Governing Law and Jursidiction	multiline	t	2	2280
8042	3	t	\N	Binding terms	multiline	t	2	2280
8043	4	t	\N	Insurance	multiline	t	2	2280
8044	5	t	\N	Notices	multiline	t	2	2280
8045	6	t	\N	Indemnification	multiline	t	2	2280
8046	7	t	\N	Assignment	multiline	t	2	2280
6011	2	t	\N	Why do this?	multiline	t	11	1760
6296	1	t	\N	Annual budget	multiline	t	11	1832
6012	1	t	\N	What is need being addressed?	multiline	t	11	1760
6013	1	t	\N	What is the proposed intervention?	multiline	t	11	1761
6014	2	t	\N	Describe the intervention	multiline	t	11	1761
6015	3	t	\N	Describe the main activities of the project	multiline	t	11	1761
6016	4	t	\N	Describe the key assumptions	multiline	t	11	1761
6017	5	t	\N	Describe the success factors	multiline	t	11	1761
6018	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1762
6019	1	t	\N	Describe the impact outcomes	multiline	t	11	1763
6020	2	t	\N	New KPI	kpi	t	11	1763
6021	1	t	\N	Annual budget	multiline	t	11	1764
6022	2	t	\N	Disbursement schedule	multiline	t	11	1764
6023	3	t	\N	Bank Name	multiline	t	11	1764
6024	4	t	\N	Bank A/C No.	multiline	t	11	1764
6025	5	t	\N	Bank IFSC Code	multiline	t	11	1764
6026	1	t	\N	Severability	multiline	t	11	1765
6027	2	t	\N	Governing Law and Jursidiction	multiline	t	11	1765
6028	3	t	\N	Binding terms	multiline	t	11	1765
6029	4	t	\N	Insurance	multiline	t	11	1765
6030	5	t	\N	Notices	multiline	t	11	1765
6031	6	t	\N	Indemnification	multiline	t	11	1765
6032	7	t	\N	Assignment	multiline	t	11	1765
6033	8	t	\N	Amendment	multiline	t	11	1765
6034	1	t	[{"name":"Budget Head","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Personnel","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Program Cost","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Overhead Cost","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Annual budget	table	t	11	1766
6035	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	1767
6036	2	t	\N	 10.\tInternal Systems	multiline	t	11	1767
6037	3	t	\N	11.\tBooks of Accounts	multiline	t	11	1767
6038	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	1767
6039	5	t	\N	13.\tCash payments	multiline	t	11	1767
6040	7	t	\N	15.\tAudit by CInI	multiline	t	11	1767
6041	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	1767
6042	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	1767
6043	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	1767
6044	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	1767
6045	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	1767
6046	13	t	\N	21.\tIndemnification	multiline	t	11	1767
6047	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	1767
6048	2	t	\N		multiline	t	11	1768
8047	8	t	\N	Amendment	multiline	t	2	2280
8912	1	t	\N	What is need being addressed?	multiline	t	11	2427
8913	2	t	\N	Why do this?	multiline	t	11	2427
8914	1	t	\N	What is the proposed intervention?	multiline	t	11	2428
8915	2	t	\N	Describe the intervention	multiline	t	11	2428
8916	3	t	\N	Describe the main activities of the project	multiline	t	11	2428
8917	4	t	\N	Describe the key assumptions	multiline	t	11	2428
8918	5	t	\N	Describe the success factors	multiline	t	11	2428
8919	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2429
8920	1	t	\N	Describe the impact outcomes	kpi	t	11	2430
8921	2	t	\N	No. of stations setup	kpi	t	11	2430
8922	1	t	\N	Annual budget	multiline	t	11	2431
8923	3	t	\N	Bank Name	multiline	t	11	2431
8924	4	t	\N	Bank A/C No.	multiline	t	11	2431
8925	5	t	\N	Bank IFSC Code	multiline	t	11	2431
8926	2	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]},{"name":"2","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]}]	Disbursement schedule	disbursement	t	11	2431
8927	1	t	\N	Severability	multiline	t	11	2432
8928	2	t	\N	Governing Law and Jursidiction	multiline	t	11	2432
8929	3	t	\N	Binding terms	multiline	t	11	2432
8930	4	t	\N	Insurance	multiline	t	11	2432
8931	5	t	\N	Notices	multiline	t	11	2432
6287	1	t	\N	What is the proposed intervention?	multiline	t	11	1829
6288	2	t	\N	Describe the intervention	multiline	t	11	1829
6289	3	t	\N	Describe the main activities of the project	multiline	t	11	1829
6290	4	t	\N	Describe the key assumptions	multiline	t	11	1829
6291	5	t	\N	Describe the success factors	multiline	t	11	1829
6292	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1830
6293	1	t	\N	Describe the impact outcomes	multiline	t	11	1831
6294	2	t	\N	Number of homes visited	kpi	t	11	1831
6295	3	t	\N	Number of individuals impacted	kpi	t	11	1831
6297	2	t	\N	Disbursement schedule	multiline	t	11	1832
6298	3	t	\N	Bank Name	multiline	t	11	1832
6299	4	t	\N	Bank A/C No.	multiline	t	11	1832
6300	5	t	\N	Bank IFSC Code	multiline	t	11	1832
6301	1	t	\N	Severability	multiline	t	11	1833
6302	2	t	\N	Governing Law and Jursidiction	multiline	t	11	1833
6303	3	t	\N	Binding terms	multiline	t	11	1833
6304	4	t	\N	Insurance	multiline	t	11	1833
6305	5	t	\N	Notices	multiline	t	11	1833
6306	6	t	\N	Indemnification	multiline	t	11	1833
6307	7	t	\N	Assignment	multiline	t	11	1833
6308	8	t	\N	Amendment	multiline	t	11	1833
7576	1	t	\N	What is need being addressed?	multiline	t	11	2177
7577	2	t	\N	Why do this?	multiline	t	11	2177
7578	1	t	\N	What is the proposed intervention?	multiline	t	11	2178
7579	2	t	\N	Describe the intervention	multiline	t	11	2178
7580	3	t	\N	Describe the main activities of the project	multiline	t	11	2178
7581	4	t	\N	Describe the key assumptions	multiline	t	11	2178
7582	5	t	\N	Describe the success factors	multiline	t	11	2178
7274	1	t	\N		multiline	t	11	2093
7275	1	t	\N		multiline	t	11	2094
7276	1	t	\N		document	t	11	2095
7277	1	t	\N	Number of Instalations	kpi	t	11	2096
7278	2	t	\N		kpi	t	11	2096
7279	3	t	\N		multiline	t	11	2097
7280	1	t	\N	Address	multiline	t	11	2097
7281	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2098
7282	1	t	\N	Describe the impact outcomes	multiline	t	11	2099
7283	2	t	\N	Binding Terms	multiline	t	11	2099
7284	3	t	\N		multiline	t	11	2099
7583	1	t	\N	Describe the impact outcomes	kpi	t	11	2179
7584	2	t	\N	Disbursement schedule	disbursement	t	11	2180
7585	1	t	\N	Annual budget	multiline	t	11	2180
6120	1	t	\N	Describe the impact outcomes	multiline	t	11	1787
6121	2	t	\N	Number of homes visited	kpi	t	11	1787
6122	3	t	\N	Number of individuals impacted	kpi	t	11	1787
6123	1	t	\N	Annual budget	multiline	t	11	1788
6124	2	t	\N	Disbursement schedule	multiline	t	11	1788
6125	3	t	\N	Bank Name	multiline	t	11	1788
6126	4	t	\N	Bank A/C No.	multiline	t	11	1788
6127	5	t	[{"name":"","header":null,"columns":[{"name":"1","value":""},{"name":"2","value":""},{"name":"3","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Bank IFSC Code	table	t	11	1788
6128	1	t	\N	Severability	multiline	t	11	1789
6129	2	t	\N	Governing Law and Jursidiction	multiline	t	11	1789
6130	3	t	\N	Binding terms	multiline	t	11	1789
6131	4	t	\N	Insurance	multiline	t	11	1789
6132	5	t	\N	Notices	multiline	t	11	1789
6133	6	t	\N	Indemnification	multiline	t	11	1789
6134	7	t	\N	Assignment	multiline	t	11	1789
6135	8	t	\N	Amendment	multiline	t	11	1789
6136	1	t	\N	What is need being addressed?	multiline	t	11	1790
6137	2	t	\N	Why do this?	multiline	t	11	1790
7586	3	t	\N	Bank Name	multiline	t	11	2180
7587	4	t	\N	Bank A/C No.	multiline	t	11	2180
7588	5	t	\N	Bank IFSC Code	multiline	t	11	2180
6138	1	t	\N	What is the proposed intervention?	multiline	t	11	1791
6139	2	t	\N	Describe the intervention	multiline	t	11	1791
6140	3	t	\N	Describe the main activities of the project	multiline	t	11	1791
6141	4	t	\N	Describe the key assumptions	multiline	t	11	1791
6142	5	t	\N	Describe the success factors	multiline	t	11	1791
6143	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1792
7589	1	t	\N	Severability	multiline	t	11	2181
7590	2	t	\N	Governing Law and Jursidiction	multiline	t	11	2181
7591	3	t	\N	Binding terms	multiline	t	11	2181
7592	4	t	\N	Insurance	multiline	t	11	2181
7593	5	t	\N	Notices	multiline	t	11	2181
7594	6	t	\N	Indemnification	multiline	t	11	2181
7595	7	t	\N	Assignment	multiline	t	11	2181
7596	8	t	\N	Amendment	multiline	t	11	2181
7597	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2182
6354	1	t	\N	What is the proposed intervention?	multiline	t	11	1846
6355	2	t	\N	Describe the intervention	multiline	t	11	1846
6356	3	t	\N	Describe the main activities of the project	multiline	t	11	1846
6357	4	t	\N	Describe the key assumptions	multiline	t	11	1846
6358	5	t	\N	Describe the success factors	multiline	t	11	1846
6359	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	1847
6360	1	t	\N	Describe the impact outcomes	multiline	t	11	1848
6361	2	t	\N	Number of homes visited	kpi	t	11	1848
6362	3	t	\N	Number of individuals impacted	kpi	t	11	1848
6363	1	t	\N	Annual budget	multiline	t	11	1849
6364	2	t	\N	Disbursement schedule	multiline	t	11	1849
6365	3	t	\N	Bank Name	multiline	t	11	1849
6366	4	t	\N	Bank A/C No.	multiline	t	11	1849
6367	5	t	\N	Bank IFSC Code	multiline	t	11	1849
6368	1	t	\N	Severability	multiline	t	11	1850
6369	2	t	\N	Governing Law and Jursidiction	multiline	t	11	1850
6370	3	t	\N	Binding terms	multiline	t	11	1850
6371	4	t	\N	Insurance	multiline	t	11	1850
6372	5	t	\N	Notices	multiline	t	11	1850
6373	6	t	\N	Indemnification	multiline	t	11	1850
6374	7	t	\N	Assignment	multiline	t	11	1850
6375	8	t	\N	Amendment	multiline	t	11	1850
6376	1	t	\N	Guidelines	document	t	11	1851
9181	2	t	\N	Why do this?	multiline	t	11	2475
9182	1	t	\N	What is need being addressed?	multiline	t	11	2475
9183	1	t	\N	What is the proposed intervention?	multiline	t	11	2476
9184	2	t	\N	Describe the intervention	multiline	t	11	2476
9185	3	t	\N	Describe the main activities of the project	multiline	t	11	2476
9186	4	t	\N	Describe the key assumptions	multiline	t	11	2476
9187	5	t	\N	Describe the success factors	multiline	t	11	2476
9188	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2477
9189	1	t	\N	Describe the impact outcomes	kpi	t	11	2478
9190	1	t	\N	Annual budget	multiline	t	11	2479
9191	3	t	\N	Bank Name	multiline	t	11	2479
8070	1	t	\N	What is need being addressed?	multiline	t	11	2287
8071	2	t	\N	Why do this?	multiline	t	11	2287
8072	1	t	\N	What is the proposed intervention?	multiline	t	11	2288
8073	2	t	\N	Describe the intervention	multiline	t	11	2288
8074	3	t	\N	Describe the main activities of the project	multiline	t	11	2288
8075	4	t	\N	Describe the key assumptions	multiline	t	11	2288
8076	5	t	\N	Describe the success factors	multiline	t	11	2288
8077	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2289
8078	1	t	\N	Describe the impact outcomes	kpi	t	11	2290
8079	2	t	\N	Disbursement schedule	disbursement	t	11	2291
8080	1	t	\N	Annual budget	multiline	t	11	2291
8081	3	t	\N	Bank Name	multiline	t	11	2291
8082	4	t	\N	Bank A/C No.	multiline	t	11	2291
8083	5	t	\N	Bank IFSC Code	multiline	t	11	2291
8084	1	t	\N	Severability	multiline	t	11	2292
8085	2	t	\N	Governing Law and Jursidiction	multiline	t	11	2292
8086	3	t	\N	Binding terms	multiline	t	11	2292
8087	4	t	\N	Insurance	multiline	t	11	2292
8088	5	t	\N	Notices	multiline	t	11	2292
8932	6	t	\N	Indemnification	multiline	t	11	2432
8933	7	t	\N	Assignment	multiline	t	11	2432
8934	8	t	\N	Amendment	multiline	t	11	2432
9192	4	t	\N	Bank A/C No.	multiline	t	11	2479
9193	5	t	\N	Bank IFSC Code	multiline	t	11	2479
9194	2	t	\N	Disbursement schedule	disbursement	t	11	2479
9195	1	t	\N	Severability	multiline	t	11	2480
7285	1	t	[{"name":"Budget Head","header":null,"columns":[{"name":"Sustain+ Year 1","value":""},{"name":"Sustain+ Year 2","value":""},{"name":"Community ","value":""},{"name":"Partner","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Personnel","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Program Cost","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Overheads","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Annual budget	table	t	11	2100
7286	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	2101
7287	2	t	\N	 10.\tInternal Systems	multiline	t	11	2101
7288	3	t	\N	11.\tBooks of Accounts	multiline	t	11	2101
7289	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	2101
7290	5	t	\N	13.\tCash payments	multiline	t	11	2101
7291	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	2101
7292	7	t	\N	15.\tAudit by CInI	multiline	t	11	2101
7293	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	2101
7294	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	2101
7295	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	2101
7296	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	2101
7297	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	2101
7298	13	t	\N	21.\tIndemnification	multiline	t	11	2101
7299	2	t	\N		multiline	t	11	2102
7300	1	t	[{"name":"Instalment 1","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount (in Rs)","value":""},{"name":"Fund raised from other sources (in Rs)","value":""}]},{"name":"2","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"3","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total Amounts (in Rs)","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]	Disbursement Schedule	table	t	11	2102
8832	15	t	\N	Governing Law and Jurisdiction	multiline	t	11	2407
7301	1	t	[{"name":"FCRA Declaration","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2: Report Submission Due dates	table	t	11	2103
9196	2	t	\N	Governing Law and Jursidiction	multiline	t	11	2480
9197	3	t	\N	Binding terms	multiline	t	11	2480
9198	4	t	\N	Insurance	multiline	t	11	2480
9199	5	t	\N	Notices	multiline	t	11	2480
9200	6	t	\N	Indemnification	multiline	t	11	2480
9201	7	t	\N	Assignment	multiline	t	11	2480
9202	8	t	\N	Amendment	multiline	t	11	2480
8089	6	t	\N	Indemnification	multiline	t	11	2292
8090	7	t	\N	Assignment	multiline	t	11	2292
8091	8	t	\N	Amendment	multiline	t	11	2292
8822	8	t	\N	Information and Publicity	multiline	t	11	2407
8823	9	t	\N	Quality, Health, Safety and Environment	multiline	t	11	2407
8824	10	t	\N	Obligations of the Grantee	multiline	t	11	2407
8825	11	t	\N	Repayment of Grant Funds	multiline	t	11	2407
8826	12	t	\N	Amendment of the Grant terms	multiline	t	11	2407
8827	13	t	\N	Indemnification	multiline	t	11	2407
7390	2	t	\N		multiline	t	11	2129
7391	3	t	\N		multiline	t	11	2129
7392	1	t	\N		multiline	t	11	2129
7393	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2130
7394	1	t	\N	Describe the impact outcomes	multiline	t	11	2131
7395	2	t	\N	Binding Terms	multiline	t	11	2131
7396	3	t	\N		multiline	t	11	2131
7397	1	t	[{"name":"Budget Head","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Personnel","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]},{"name":"Program Cost","header":null,"columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}]}]	Annual budget	table	t	11	2132
7398	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	2133
7399	2	t	\N	 10.\tInternal Systems	multiline	t	11	2133
7400	3	t	\N	11.\tBooks of Accounts	multiline	t	11	2133
7401	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	2133
7402	5	t	\N	13.\tCash payments	multiline	t	11	2133
7403	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	2133
7404	7	t	\N	15.\tAudit by CInI	multiline	t	11	2133
7405	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	2133
7406	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	2133
7407	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	2133
7408	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	2133
7409	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	2133
7410	13	t	\N	21.\tIndemnification	multiline	t	11	2133
7411	1	t	[{"name":"1","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount (in Rs)","value":""},{"name":"Fund raised from other sources (in Rs)","value":""}]},{"name":"2","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"3","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]},{"name":"Total Amounts (in Rs)","header":null,"columns":[{"name":"Period","value":""},{"name":"Amount in Rs","value":""},{"name":"Fund raised from other source","value":""}]}]		table	t	11	2134
7412	2	t	\N		multiline	t	11	2134
7413	1	t	[{"name":"FCRA Declaration","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Annual Work Plan(AWP)","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Baseline Survey Report","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]},{"name":"Quarterly Progress Report and Utilisation report","header":null,"columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}]}]	Table 2: Report Submission Due dates	table	t	11	2135
7414	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	2135
7415	3	t	\N	7.\tChanges in Budget	multiline	t	11	2135
7416	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	2135
7417	1	t	\N	1. Annual Work Plan (AWP)	multiline	t	11	2136
7418	2	t	\N	2. Baseline Survey Report	multiline	t	11	2136
7419	3	t	\N	3. Quarterly Reports: 	multiline	t	11	2136
7420	4	t	\N	4. Project End Report: 	multiline	t	11	2136
7421	5	t	\N	5. Audited Statement of Accounts	multiline	t	11	2136
7422	1	t	\N		multiline	t	11	2137
7423	1	t	\N		multiline	t	11	2138
7424	1	t	\N		multiline	t	11	2139
7425	1	t	\N		document	t	11	2140
8828	1	t	\N	Separate Bank Account	multiline	t	11	2407
8829	2	t	\N	Internal Accounting/Financial Systems	multiline	t	11	2407
8830	5	t	\N	Cash payments	multiline	t	11	2407
8831	14	t	\N	Termination of the Contract	multiline	t	11	2407
8833	16	t	\N	Binding Terms	multiline	t	11	2407
8834	17	t	\N		multiline	t	11	2407
8835	18	t	\N	ACCEPTED & AGREED	multiline	t	11	2407
8836	19	t	\N		multiline	f	11	2407
8837	1	t	\N		multiline	t	11	2408
8838	2	t	\N		multiline	t	11	2408
8839	3	t	\N	Increase in additional training hours	kpi	t	11	2408
8840	4	t	\N	New skills trainings added 	kpi	t	11	2408
8841	5	t	\N	Increase in job placements	kpi	t	11	2408
8013	1	t	\N	Describe the impact outcomes	multiline	t	2	2273
9279	2	t	\N		multiline	t	11	2505
8842	6	t	\N	DISBURSEMENT OF THE GRANT	multiline	t	11	2408
8843	7	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":"","dataType":null},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]},{"name":"2","header":"Planned Installment #","columns":[{"name":"Date/Period","value":"","dataType":null},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]},{"name":"3","header":"Planned Installment #","columns":[{"name":"Date/Period","value":"","dataType":null},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]}]	Table 1	disbursement	t	11	2408
8844	8	t	\N	Utilization of Funds	multiline	t	11	2408
9280	3	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false},{"name":"2","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false},{"name":"3","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false}]		disbursement	t	11	2505
9281	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	2506
9282	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false}]	Table 2: Report Submission Due dates	table	t	11	2506
9283	3	t	\N	7.\tChanges in Budget	multiline	t	11	2506
9284	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	2506
9285	4	t	\N	4. Project End Report: 	multiline	t	11	2507
9286	5	t	\N	5. Audited Statement of Accounts	multiline	t	11	2507
9287	1	t	\N	1. Annual Work Plan (AWP)	multiline	t	11	2507
9288	2	t	\N	2. Baseline Survey Report	multiline	t	11	2507
9289	3	t	\N	3. Quarterly Reports: 	multiline	t	11	2507
9290	6	t	\N	Grant level Outcome measures (Number of homes electrified)	kpi	t	11	2507
9291	1	t	\N		multiline	t	11	2508
9292	3	t	\N		multiline	t	11	2508
9293	2	t	\N		multiline	t	11	2508
9294	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2509
9295	1	t	\N	Describe the impact outcomes	multiline	t	11	2510
9296	2	t	\N	Binding Terms	multiline	t	11	2510
9297	3	t	\N		multiline	t	11	2510
9298	1	t	[{"name":"Budget Head","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}],"enteredByGrantee":false},{"name":"Personnel","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}],"enteredByGrantee":false},{"name":"Program Cost","columns":[{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""},{"name":"","value":""}],"enteredByGrantee":false}]	Annual budget	table	t	11	2511
9299	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	2512
8014	2	t	\N	Number of States	kpi	t	2	2273
8015	3	t	\N	Number of Districts	kpi	t	2	2273
8016	4	t	\N	Number of Villages	kpi	t	2	2273
8017	5	t	\N	Number of Households	kpi	t	2	2273
8018	3	t	\N	Bank Name :	multiline	t	2	2274
8019	4	t	\N	Bank A/C No.	multiline	t	2	2274
8020	5	t	\N	Bank IFSC Code	multiline	t	2	2274
8021	6	t	\N	Branch Name & Address :	multiline	t	2	2274
8022	1	t	\N	Annual budget	multiline	t	2	2274
8023	2	t	\N	Disbursement schedule	disbursement	t	2	2274
8024	1	t	\N	Reports	document	t	2	2275
8025	3	t	\N	Plan	document	t	2	2275
8026	4	t	\N	Guidelines	document	t	2	2275
8027	1	t	\N	Name of Grantee Organization & Stamp :	multiline	t	2	2276
8028	2	t	\N		multiline	t	2	2276
8029	3	t	\N	 Authorized Signatory :	multiline	t	2	2276
8030	4	t	\N	Name & Designation :	multiline	t	2	2276
8031	5	t	\N	Date :	multiline	t	2	2276
8032	1	t	\N	What is the need being addressed?	multiline	t	2	2277
8033	2	t	\N	Why do this?	multiline	t	2	2277
8034	1	t	\N	What is the proposed intervention?	multiline	t	2	2278
8035	2	t	\N	Describe the intervention	multiline	t	2	2278
8036	3	t	\N	Describe the main activities of the project	multiline	t	2	2278
8037	4	t	\N	Describe the key assumptions	multiline	t	2	2278
8038	5	t	\N	Describe the success factors	multiline	t	2	2278
9300	2	t	\N	 10.\tInternal Systems	multiline	t	11	2512
9301	3	t	\N	11.\tBooks of Accounts	multiline	t	11	2512
9302	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	2512
9303	5	t	\N	13.\tCash payments	multiline	t	11	2512
9304	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	2512
9305	7	t	\N	15.\tAudit by CInI	multiline	t	11	2512
9022	1	t	\N		multiline	t	11	2441
9023	2	t	\N		multiline	t	11	2441
9024	3	t	\N	Increase in additional training hours	kpi	t	11	2441
9025	4	t	\N	New skills trainings added 	kpi	t	11	2441
9026	5	t	\N	Increase in job placements	kpi	t	11	2441
9027	6	t	\N	DISBURSEMENT OF THE GRANT	multiline	t	11	2441
9028	7	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]},{"name":"2","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]},{"name":"3","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]}]	Table 1	disbursement	t	11	2441
9029	8	t	\N	Utilization of Funds	multiline	t	11	2441
9030	3	t	\N		multiline	t	11	2442
9031	2	t	\N		multiline	t	11	2442
9032	4	t	\N		multiline	t	11	2442
9033	2	t	\N	See attached Annual Work Plan guideline (Annexure 8) and template (Annexure 4) documents	document	t	11	2443
9034	3	t	\N	2.   Submissions to establish adherence to the Foreign Contribution (Regulation) Act, 2010 (FCRA)	multiline	t	11	2443
9035	4	t	\N	FCRA Declaration (Annexure 9) 	document	t	11	2443
9036	5	t	\N	3.   Baseline Survey Report	multiline	t	11	2443
9037	1	t	\N	1.   Annual Work Plan (AWP)	multiline	t	11	2443
9038	6	t	\N	4.   Quarterly Reports: 	multiline	t	11	2443
9039	7	t	\N	See attached Quarterly Progress Report template	document	t	11	2443
9040	10	t	\N	5.   Project End Report 	multiline	t	11	2443
9041	11	t	\N	6.   Audited Statement of Accounts	multiline	t	11	2443
9042	12	t	\N	7.   Contributions from Other Sources brought into the Accounts of TLMTI	multiline	t	11	2443
9043	13	t	\N	8.   Approvals for Budget Changes	multiline	t	11	2443
9044	14	t	[{"name":"1","header":"","columns":[{"name":"Submissions","value":""},{"name":"Due by milestone","value":""},{"name":"Notes","value":""}]},{"name":"2.a","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"2.b","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"2.c","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"3","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"4.a","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"4.b","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"5","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"6","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"7","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]},{"name":"8","header":"","columns":[{"name":"Submissions","value":""},{"name":"Milestone Due","value":""},{"name":"Notes","value":""}]}]	Note: All submissions must be made within 30 days of milestone unless specified otherwise.	table	t	11	2443
9045	9	t	\N	See attached Utilisation Certificate template	document	t	11	2443
9046	8	t	\N		multiline	t	11	2443
9047	3	t	\N	Books of Accounts	multiline	t	11	2444
9048	4	t	\N	Procedure for Booking of Expenses	multiline	t	11	2444
9049	6	t	\N	Interest earned on grant funds	multiline	t	11	2444
9050	7	t	\N	Audit by CInI	multiline	t	11	2444
9051	8	t	\N	Information and Publicity	multiline	t	11	2444
9052	9	t	\N	Quality, Health, Safety and Environment	multiline	t	11	2444
9053	10	t	\N	Obligations of the Grantee	multiline	t	11	2444
9054	11	t	\N	Repayment of Grant Funds	multiline	t	11	2444
9055	12	t	\N	Amendment of the Grant terms	multiline	t	11	2444
9056	13	t	\N	Indemnification	multiline	t	11	2444
9057	1	t	\N	Separate Bank Account	multiline	t	11	2444
9058	2	t	\N	Internal Accounting/Financial Systems	multiline	t	11	2444
9059	5	t	\N	Cash payments	multiline	t	11	2444
9060	14	t	\N	Termination of the Contract	multiline	t	11	2444
9061	15	t	\N	Governing Law and Jurisdiction	multiline	t	11	2444
9062	16	t	\N	Binding Terms	multiline	t	11	2444
9063	17	t	\N		multiline	t	11	2444
9306	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	2512
9307	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	2512
9308	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	2512
9309	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	2512
9310	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	2512
9311	13	t	\N	21.\tIndemnification	multiline	t	11	2512
9064	18	t	\N	ACCEPTED & AGREED	multiline	t	11	2444
9065	19	t	\N	Bank Details	multiline	t	11	2444
9066	20	t	\N		multiline	f	11	2444
9312	1	t	\N		multiline	t	11	2513
9313	1	t	\N		multiline	t	11	2514
9314	1	t	\N		document	t	11	2515
9315	1	t	\N		multiline	t	11	2516
9656	1	t	\N	Annual budget	document	t	11	2620
9657	2	t	\N		multiline	t	11	2620
9658	1	t	\N		document	t	11	2621
9659	2	t	\N		disbursement	t	11	2622
9660	1	t	\N		multiline	t	11	2623
9661	2	t	\N		multiline	t	11	2624
9662	3	t	\N		multiline	t	11	2624
9663	4	t	\N		multiline	t	11	2624
9664	1	t	\N		multiline	t	11	2625
9665	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2626
9666	1	t	\N		multiline	t	11	2627
9667	2	t	\N	Binding Terms	multiline	t	11	2627
9668	3	t	\N		multiline	t	11	2627
9669	4	t	\N		multiline	t	11	2627
9670	5	t	\N	ACCEPTED & AGREED	multiline	t	11	2627
9671	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	2628
9672	2	t	\N	 10.\tInternal Systems	multiline	t	11	2628
9673	3	t	\N	11.\tBooks of Accounts	multiline	t	11	2628
9674	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	2628
9675	5	t	\N	13.\tCash payments	multiline	t	11	2628
9676	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	2628
9677	7	t	\N	15.\tAudit by CInI	multiline	t	11	2628
9678	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	2628
9679	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	2628
9680	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	2628
9681	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	2628
9682	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	2628
9683	13	t	\N	21.\tIndemnification	multiline	t	11	2628
9684	1	t	\N	1.\tAnnual Work Plan (AWP)	multiline	t	11	2629
9685	2	t	\N	2.Baseline Survey Report	multiline	t	11	2629
9686	3	t	\N	3.\tQuarterly Reports: 	multiline	t	11	2629
9687	4	t	\N	4.Project End Report: 	multiline	t	11	2629
9688	5	t	\N	5.\tAudited Statement of Accounts	multiline	t	11	2629
9689	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Utilisation Certificate (UC) Quarterly unaudited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Utilisation Certificate (UC) Six monthly audited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"FCRA Quarterly Intimation","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Project End Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Audited Statement of Accounts","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"FCRA Annual Return FC-4","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false}]	Table 2	table	t	11	2630
9690	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	2630
9691	3	t	\N	7.\tChanges in Budget	multiline	t	11	2630
9692	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	2630
8803	3	t	\N		multiline	t	11	2405
8804	2	t	\N		multiline	t	11	2405
8805	4	t	\N		multiline	t	11	2405
8806	3	t	\N	2.   Submissions to establish adherence to the Foreign Contribution (Regulation) Act, 2010 (FCRA)	multiline	t	11	2406
8807	4	t	\N	FCRA Declaration (Annexure 9) 	document	t	11	2406
8808	12	t	[{"name":"1","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Due by milestone","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"2.a","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"2.b","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"2.c","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"3","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"4.a","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"4.b","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"5","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"6","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"7","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]},{"name":"8","header":"","columns":[{"name":"Submissions","value":"","dataType":null},{"name":"Milestone Due","value":"","dataType":null},{"name":"Notes","value":"","dataType":null}]}]	Note: All submissions must be made within 30 days of milestone unless specified otherwise.	table	t	11	2406
8809	5	t	\N	3.   Baseline Survey Report	multiline	t	11	2406
8810	7	t	\N	Annexure 2a	document	t	11	2406
8811	8	t	\N	5.   Project End Report 	multiline	t	11	2406
8812	1	t	\N	1.   Annual Work Plan (AWP)	multiline	t	11	2406
9693	1	t	\N	1.\tAnnual Work Plan (AWP)	multiline	t	11	2631
9694	2	t	\N	2.Baseline Survey Report	multiline	t	11	2631
9695	3	t	\N	3.\tQuarterly Reports: 	multiline	t	11	2631
9696	4	t	\N	4.Project End Report: 	multiline	t	11	2631
9697	5	t	\N	5.\tAudited Statement of Accounts	multiline	t	11	2631
9698	1	t	[{"name":"FCRA Declaration","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Annual Work Plan(AWP)","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Baseline Survey Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Quarterly Progress Report and Utilisation report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Utilisation Certificate (UC) Quarterly unaudited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Utilisation Certificate (UC) Six monthly audited","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"FCRA Quarterly Intimation","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Project End Report","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"Audited Statement of Accounts","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"FCRA Annual Return FC-4","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false},{"name":"","columns":[{"name":"Frequency","value":""},{"name":"Submission Due Dates","value":""}],"enteredByGrantee":false}]	Table 2	table	t	11	2632
9699	2	t	\N	6.\tContributions from Other Sources not Brought into the Accounts of the Grantee	multiline	t	11	2632
9700	3	t	\N	7.\tChanges in Budget	multiline	t	11	2632
9701	4	t	\N	8.\tMonitoring and Evaluation	multiline	t	11	2632
9702	2	t	\N		multiline	t	11	2633
9703	1	t	\N	Annual budget	table	t	11	2633
9704	1	t	\N		document	t	11	2634
9705	2	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Date/Period","value":""},{"name":"Amount","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false}]	Payment Schedule	disbursement	t	11	2635
9706	1	t	\N		multiline	t	11	2636
9707	2	t	\N		multiline	t	11	2637
9708	3	t	\N		multiline	t	11	2637
9709	4	t	\N		multiline	t	11	2637
9710	1	t	\N		multiline	t	11	2638
9711	1	t	\N	Describe the risks to the project & mitigation plans	multiline	t	11	2639
9712	1	t	\N		multiline	t	11	2640
9713	2	t	\N	Binding Terms	multiline	t	11	2640
9714	3	t	\N		multiline	t	11	2640
9715	4	t	\N		multiline	t	11	2640
9716	5	t	\N	ACCEPTED & AGREED	multiline	t	11	2640
9717	1	t	\N	9.\tSeparate Bank Account	multiline	t	11	2641
9718	2	t	\N	 10.\tInternal Systems	multiline	t	11	2641
9719	3	t	\N	11.\tBooks of Accounts	multiline	t	11	2641
9720	4	t	\N	12.\tProcedure for Booking of Expenses	multiline	t	11	2641
9721	5	t	\N	13.\tCash payments	multiline	t	11	2641
9722	6	t	\N	14.\tInterest earned on grant funds	multiline	t	11	2641
9723	7	t	\N	15.\tAudit by CInI	multiline	t	11	2641
9724	8	t	\N	 16.\tInformation and Publicity	multiline	t	11	2641
9725	9	t	\N	17.\tQuality, Health, Safety and Environment	multiline	t	11	2641
9726	10	t	\N	18.\tObligations of the Grantee	multiline	t	11	2641
9727	11	t	\N	19.\tRepayment of Grant Funds	multiline	t	11	2641
9728	12	t	\N	20.\tAmendment of the Grant terms	multiline	t	11	2641
9729	13	t	\N	21.\tIndemnification	multiline	t	11	2641
\.


--
-- TOC entry 3813 (class 0 OID 306634)
-- Dependencies: 239
-- Data for Name: granter_grant_sections; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_grant_sections (id, deletable, section_name, section_order, grant_template_id, granter_id) FROM stdin;
1	t	Purpose	1	1	2
2	t	Project Approach	2	1	2
3	t	Project Risks/challenges	3	1	2
4	t	Project Outcome Measurement & Evaluation	4	1	2
5	t	Budget & Finance Details	5	1	2
6	t	Grant Terms & Conditions	6	1	2
696	t	Project Outcome Measurement & Evaluation	4	116	11
697	t	Budget & Finance Details	5	116	11
698	t	Grant Terms & Conditions	6	116	11
699	t	Project Risks/challenges	3	116	11
700	t	Purpose	1	116	11
701	t	Project Approach	2	116	11
13	t	Project Outcomes	4	3	2
14	t	Budget & Finance Details	5	3	2
15	t	Documents	8	3	2
16	t	Signatures	7	3	2
17	t	Purpose	1	3	2
18	t	Project Approach	2	3	2
19	t	Project Risks/challenges	3	3	2
20	t	Terms & Conditions	6	3	2
2129	t	Address	1	276	11
2130	t	Termination of the Contract 	8	276	11
2131	t	Governing Law and Jurisdiction	9	276	11
708	t	Project Outcome Measurement & Evaluation	4	118	11
709	t	Budget & Finance Details	5	118	11
710	t	Grant Terms & Conditions	6	118	11
588	t	Proposed Plan & Project Specifics	2	98	2
187	t	Project Outcomes	4	27	2
188	t	Budget & Finance Details	5	27	2
189	t	Documents	8	27	2
711	t	Project Risks/challenges	3	118	11
712	t	Purpose	1	118	11
713	t	Project Approach	2	118	11
1760	t	Purpose	1	233	11
1761	t	Project Approach	2	233	11
1762	t	Project Risks/challenges	3	233	11
1763	t	Project Outcome Measurement & Evaluation	4	233	11
1764	t	Budget & Finance Details	5	233	11
1765	t	Grant Terms & Conditions	6	233	11
786	t	Grant Terms & Conditions	6	131	11
787	t	Project Approach	2	131	11
788	t	Project Risks/challenges	3	131	11
789	t	Budget & Finance Details	5	131	11
790	t	Purpose	1	131	11
589	t	Purpose & Background	1	98	2
590	t	Budget & Finance Details	5	98	2
591	t	Purpose	1	99	11
592	t	Project Approach	2	99	11
593	t	Project Risks/challenges	3	99	11
594	t	Project Outcome Measurement & Evaluation	4	99	11
595	t	Budget & Finance Details	5	99	11
190	t	Signatures	7	27	2
191	t	Project Risks/challenges	3	27	2
192	t	Terms & Conditions	6	27	2
193	t		1	27	2
194	t		2	27	2
596	t	Grant Terms & Conditions	6	99	11
791	t	Project Outcome Measurement & Evaluation	4	131	11
792	t	Purpose	1	132	11
793	t	Project Approach	2	132	11
794	t	Project Risks/challenges	3	132	11
795	t	Project Outcome Measurement & Evaluation	4	132	11
796	t	Budget & Finance Details	5	132	11
797	t	Grant Terms & Conditions	6	132	11
798	t	Purpose	1	133	11
799	t	Project Approach	2	133	11
800	t	Project Risks/challenges	3	133	11
801	t	Project Outcome Measurement & Evaluation	4	133	11
802	t	Budget & Finance Details	5	133	11
609	t	Purpose	1	102	2
610	t	Budget & Finance Details	5	102	2
611	t	Grant Terms & Conditions	6	102	2
612	t	Project Approach	2	102	2
613	t	Project Risks/challenges	3	102	2
614	t	Project Outcome Measurement & Evaluation	4	102	2
803	t	Grant Terms & Conditions	6	133	11
665	t	Purpose	1	111	11
666	t	Project Approach	2	111	11
667	t	Project Risks/challenges	3	111	11
668	t	Budget & Finance Details	5	111	11
669	t	Grant Terms & Conditions	6	111	11
670	t	Project Outcomes	4	111	11
671	t	Annexures	7	111	11
678	t	Purpose	1	113	11
679	t	Project Approach	2	113	11
680	t	Project Risks/challenges	3	113	11
681	t	Project Outcome Measurement & Evaluation	4	113	11
570	t	Proposed Plan & Project Specifics	2	94	2
571	t	Purpose & Background	1	94	2
572	t	Budget & Finance Details	5	94	2
682	t	Budget & Finance Details	5	113	11
683	t	Grant Terms & Conditions	6	113	11
1787	t	Project Outcome Measurement & Evaluation	4	238	11
1788	t	Budget & Finance Details	5	238	11
1789	t	Grant Terms & Conditions	6	238	11
1790	t	Purpose	1	238	11
1791	t	Project Approach	2	238	11
1792	t	Project Risks/challenges	3	238	11
1766	t	Budget & Finance Details	10	234	11
1767	t	General Conditions of Contract (GCC)	7	234	11
1768	t	Disbursement of the Grant	3	234	11
2505	t	Disbursement of the Grant	3	328	11
2506	t	Report Submission due dates	6	328	11
2507	t	Submission of Reports	5	328	11
2112	t	Purpose	1	274	11
2113	t	Project Approach	2	274	11
2114	t	Project Risks/challenges	3	274	11
2115	t	Budget & Finance Details	5	274	11
2116	t	Grant Terms & Conditions	6	274	11
2132	t	Budget & Finance Details	10	276	11
2133	t	General Conditions of Contract (GCC)	7	276	11
2134	t	Disbursement of the Grant	3	276	11
2135	t	Report Submission due dates	6	276	11
2136	t	Submission of Reports	5	276	11
2137	t	Utilization Of funds	4	276	11
2138	t	Purpose	2	276	11
1829	t	Project Approach	2	245	11
1830	t	Project Risks/challenges	3	245	11
1831	t	Project Outcome Measurement & Evaluation	4	245	11
1832	t	Budget & Finance Details	5	245	11
1833	t	Grant Terms & Conditions	6	245	11
2139	t	Accepted and Agreed	11	276	11
2140	t	Annexures	12	276	11
2147	t	Project Outcomes	4	278	2
2148	t	Budget & Finance Details	5	278	2
2149	t	Documents	8	278	2
2150	t	Signatures	7	278	2
1846	t	Project Approach	2	248	11
1847	t	Project Risks/challenges	3	248	11
1848	t	Project Outcome Measurement & Evaluation	4	248	11
1849	t	Budget & Finance Details	5	248	11
1850	t	Grant Terms & Conditions	6	248	11
1851	t	Documents	7	248	11
1852	t	Project Approach	2	249	11
1743	t	Budget & Finance Details	10	231	11
1744	t	Annxures	11	231	11
1745	t	Disbursement of the Grant	3	231	11
1746	t	Purpose	2	231	11
1747	t	Address	1	231	11
1748	t	Utilization Of funds	4	231	11
1749	t	Termination of the Contract 	8	231	11
1549	t	Termination of the Contract 	10	212	11
1550	t	Address	1	212	11
1551	t	General Conditions of Contract (GCC	9	212	11
1324	t	Disbursement of the Grant	3	192	11
1325	t	Report Submission due dates	6	192	11
1326	t	General Conditions of Contract (GCC	7	192	11
1327	t	Submission of Reports	5	192	11
1328	t	Address	1	192	11
1329	t	Termination of the Contract 	8	192	11
1330	t	Governing Law and Jurisdiction	9	192	11
1331	t	Budget & Finance Details	10	192	11
1332	t	Utilization Of funds	4	192	11
1333	t	Purpose	2	192	11
1334	t	Accepted and Agreed	11	192	11
1552	t	Submission of Reports	7	212	11
1553	t	Report Submission due dates	8	212	11
1554	t	Purpose	2	212	11
1555	t	Accepted and Agreed	12	212	11
1556	t	Disbursement of the Grant	4	212	11
1557	t	Governing Law and Jurisdiction	11	212	11
1750	t	Governing Law and Jurisdiction	9	231	11
1751	t	General Conditions of Contract (GCC	7	231	11
1752	t	Submission of Reports	5	231	11
1753	t	Report Submission due dates	6	231	11
1853	t	Project Risks/challenges	3	249	11
1854	t	Project Outcome Measurement & Evaluation	4	249	11
1855	t	Budget & Finance Details	5	249	11
1856	t	Grant Terms & Conditions	6	249	11
1857	t	Documents	7	249	11
2151	t	Purpose	1	278	2
2152	t	Project Approach	2	278	2
1558	t	Budget & Finance Details	3	212	11
1559	t	Utilization Of funds	6	212	11
1560	t	Indicators	5	212	11
2153	t	Project Risks/challenges	3	278	2
2154	t	Terms & Conditions	6	278	2
2155	t	Budget & Finance Details	5	279	2
2156	t	Documents	8	279	2
2157	t	Project Outcomes	4	279	2
2158	t	Signatures	7	279	2
2159	t	Purpose	1	279	2
2160	t	Project Approach	2	279	2
2161	t	Project Risks/challenges	3	279	2
2162	t	Terms & Conditions	6	279	2
2405	t	Address	1	312	11
2406	t	Submission of Reports	5	312	11
2407	t	General Conditions of Contract (GCC)	7	312	11
2408	t	Particular Conditions of Contract (PCC)	2	312	11
2427	t	Purpose	1	316	11
2428	t	Project Approach	2	316	11
2429	t	Project Risks/challenges	3	316	11
2430	t	Project Outcome Measurement & Evaluation	4	316	11
2431	t	Budget & Finance Details	5	316	11
2432	t	Grant Terms & Conditions	6	316	11
1359	t	Address	1	195	11
1360	t	Termination of the Contract 	8	195	11
1361	t	Governing Law and Jurisdiction	9	195	11
1362	t	Budget & Finance Details	10	195	11
1363	t	General Conditions of Contract (GCC)	7	195	11
1364	t	Disbursement of the Grant	3	195	11
1365	t	Report Submission due dates	6	195	11
1366	t	Submission of Reports	5	195	11
1367	t	Utilization Of funds	4	195	11
1368	t	Purpose	2	195	11
1369	t	Accepted and Agreed	11	195	11
1370	t	Annexures	12	195	11
2273	t	Project Outcomes	4	293	2
2274	t	Budget & Finance Details	5	293	2
2275	t	Documents	8	293	2
2276	t	Signatures	7	293	2
2277	t	Purpose	1	293	2
2278	t	Project Approach	2	293	2
2279	t	Project Risks/challenges	3	293	2
2280	t	Terms & Conditions	6	293	2
1217	t	Disbursement of the Grant	3	182	11
1218	t	Address	1	182	11
1219	t	Budget & Finance Details	10	182	11
1220	t	Report Submission due dates	6	182	11
1221	t	General Conditions of Contract (GCC	7	182	11
1222	t	Submission of Reports	5	182	11
1223	t	Termination of the Contract 	8	182	11
1224	t	Governing Law and Jurisdiction	9	182	11
1225	t	Utilization Of funds	4	182	11
1226	t	Purpose	2	182	11
2508	t	Address	1	328	11
2287	t	Purpose	1	295	11
1601	t	Utilization Of funds	4	217	11
1602	t	Purpose	2	217	11
2509	t	Termination of the Contract 	8	328	11
2510	t	Governing Law and Jurisdiction	9	328	11
2511	t	Budget & Finance Details	10	328	11
859	t	Budget & Finance Details	6	143	11
860	t	Grant Terms & Conditions	7	143	11
861	t	Particulars of the Grant	2	143	11
862	t	Atten:	1	143	11
2512	t	General Conditions of Contract (GCC)	7	328	11
2513	t	Purpose	2	328	11
2514	t	Accepted and Agreed	11	328	11
2515	t	Annexures	12	328	11
2516	t	Utilization Of funds	4	328	11
2631	t	Submission of Reports	5	346	11
2632	t	Report Submission due dates	6	346	11
2633	t	Budget & Finance Details	10	346	11
2634	t	Annxures	11	346	11
1603	t	Budget & Finance Details	10	217	11
1604	t	Governing Law and Jurisdiction	9	217	11
1605	t	Address	1	217	11
1606	t	Report Submission due dates	6	217	11
1607	t	Submission of Reports	5	217	11
1608	t	Termination of the Contract 	8	217	11
1609	t	Disbursement of the Grant	3	217	11
1610	t	General Conditions of Contract (GCC	7	217	11
2288	t	Project Approach	2	295	11
2289	t	Project Risks/challenges	3	295	11
2635	t	Disbursement of the Grant	3	346	11
2636	t	Purpose	2	346	11
2637	t	Address	1	346	11
2638	t	Utilization Of funds	4	346	11
2639	t	Termination of the Contract 	8	346	11
2640	t	Governing Law and Jurisdiction	9	346	11
2163	t	Project Outcomes	4	280	2
2164	t	Budget & Finance Details	5	280	2
2165	t	Documents	8	280	2
2166	t	Signatures	7	280	2
2167	t	Purpose	1	280	2
2168	t	Project Approach	2	280	2
2169	t	Project Risks/challenges	3	280	2
2170	t	Terms & Conditions	6	280	2
2290	t	Project Outcome Measurement & Evaluation	4	295	11
2177	t	Purpose	1	282	11
2178	t	Project Approach	2	282	11
2179	t	Project Outcome Measurement & Evaluation	4	282	11
2180	t	Budget & Finance Details	5	282	11
2181	t	Grant Terms & Conditions	6	282	11
2182	t	Project Risks/challenges	3	282	11
2291	t	Budget & Finance Details	5	295	11
2292	t	Grant Terms & Conditions	6	295	11
2093	t	Purpose	2	272	11
2094	t	Accepted and Agreed	11	272	11
2095	t	Annexures	12	272	11
2096	t	Key Performance Indicators	13	272	11
2097	t	Address	1	272	11
2098	t	Termination of the Contract 	8	272	11
2099	t	Governing Law and Jurisdiction	9	272	11
2100	t	Budget & Finance Details	10	272	11
2101	t	General Conditions of Contract (GCC)	7	272	11
2102	t	Disbursement of the Grant	3	272	11
2103	t	Report Submission due dates	6	272	11
2104	t	Submission of Reports	5	272	11
2105	t	Utilization Of funds	4	272	11
2641	t	General Conditions of Contract (GCC	7	346	11
2441	t	Particular Conditions of Contract (PCC)	2	319	11
2442	t	Address	1	319	11
2443	t	Submission of Reports	5	319	11
2444	t	General Conditions of Contract (GCC)	7	319	11
2475	t	Purpose	1	325	11
2476	t	Project Approach	2	325	11
2477	t	Project Risks/challenges	3	325	11
2478	t	Project Outcome Measurement & Evaluation	4	325	11
2479	t	Budget & Finance Details	5	325	11
2480	t	Grant Terms & Conditions	6	325	11
2593	t	Project Approach	2	342	11
2594	t	Project Risks/challenges	3	342	11
2595	t	Project Outcome Measurement & Evaluation	4	342	11
2596	t	Budget & Finance Details	5	342	11
2597	t	Purpose	1	342	11
2620	t	Budget & Finance Details	10	345	11
2621	t	Annxures	11	345	11
2622	t	Disbursement of the Grant	3	345	11
2623	t	Purpose	2	345	11
2624	t	Address	1	345	11
2625	t	Utilization Of funds	4	345	11
2626	t	Termination of the Contract 	8	345	11
2627	t	Governing Law and Jurisdiction	9	345	11
2628	t	General Conditions of Contract (GCC	7	345	11
2629	t	Submission of Reports	5	345	11
2630	t	Report Submission due dates	6	345	11
\.


--
-- TOC entry 3815 (class 0 OID 306639)
-- Dependencies: 241
-- Data for Name: granter_grant_templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_grant_templates (id, description, granter_id, name, published, private_to_grant, default_template) FROM stdin;
27	\N	2	Custom Template	f	t	f
94	\N	2	Hemex template	t	f	f
98	\N	2	TMEAD Template	t	f	f
99	Default Anudan template	11	Default Anudan template	t	f	t
1	This is the default template for IHF Grants	2	Default Grant Template	t	f	f
3	\N	2	Anudan Template	t	f	t
102	\N	2	undefined	t	t	f
278	\N	2	undefined	t	t	f
279	\N	2	undefined	t	t	f
280	\N	2	undefined	t	t	f
293	\N	2	undefined	t	t	f
345	Created by  Sukesh Mahesh & Veenith	11	Sustain+ Grant letter Template V4	t	f	f
238	\N	11	undefined	t	t	f
111	\N	11	undefined	t	t	f
113	\N	11	Custom Template	f	t	f
245	\N	11	undefined	t	t	f
116	Based on CMF grant	11	Sustain Plus templaes	t	t	f
118	\N	11	undefined	t	t	f
248	\N	11	undefined	t	t	f
249	\N	11	undefined	t	t	f
312	Improved and simplified to enable faster grant onboarding.  Includes outcome measurements and disbursements	11	Sustain Plus Grant Letter	t	t	f
182		11	Sustain Plus template V1	t	t	f
131	\N	11	undefined	t	t	f
132	\N	11	undefined	t	t	f
133	\N	11	undefined	t	t	f
316	\N	11	undefined	t	t	f
319	\N	11	undefined	t	t	f
272	\N	11	undefined	t	t	f
143	In progress	11	S+ Grant  Letter Template	t	t	f
192		11	Sustain Plus Template V2	t	t	f
195	Created by Mahesh, Sukesh, Vineet!	11	Grant Letter template	t	t	f
325	\N	11	undefined	t	t	f
274	\N	11	Custom Template	f	t	f
276	\N	11	Custom Template	f	t	f
212	Have moved status budget and indicators on top	11	Demo Version 3	t	t	f
328	\N	11	undefined	t	t	f
282	\N	11	undefined	t	t	f
217		11	Sustain+	t	t	f
231	prepared for leprosy grant letter	11	Sustain+ Grant Templat V3	t	t	f
295	\N	11	undefined	t	t	f
233	\N	11	undefined	t	t	f
234	\N	11	Custom Template	f	t	f
342	\N	11	undefined	t	t	f
346	\N	11	Custom Template	f	t	f
\.


--
-- TOC entry 3819 (class 0 OID 306656)
-- Dependencies: 245
-- Data for Name: granter_report_section_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_report_section_attributes (id, attribute_order, deletable, extras, field_name, field_type, required, granter_id, section_id) FROM stdin;
1	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	f	2	1
73	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	67
74	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	68
4	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	f	2	6
5	1	t	\N	Who is this project for? (List beneficiaries)	multiline	f	2	3
6	1	t	\N		document	f	2	3
7	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	f	11	8
8	1	t	\N	Describe reasons for deviations  of planned vs actuals spends on the project (if any)	multiline	f	11	12
9	2	t	[{"name":"Amounts (in INR)","columns":[{"name":"Approved (Grant Level)","value":""},{"name":"Spent (Grant Level)","value":""},{"name":"Planned (Report period)","value":""},{"name":"Spent (Report period)","value":""},{"name":"","value":""}]}]	Planned v/s Actuals Spends	table	f	11	12
10	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	f	11	11
11	1	t	\N	Who is this project for? (List beneficiaries)	multiline	f	11	7
12	1	t	\N		document	f	11	10
75	1	f	\N	New KPI	kpi	t	11	69
76	1	t	\N		document	t	11	70
77	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	71
78	2	t	[{"name":"Amounts (in INR)","header":null,"columns":[{"name":"Approved (Grant Level)","value":""},{"name":"Spent (Grant Level)","value":""},{"name":"Planned (Report period)","value":""},{"name":"Spent (Report period)","value":""},{"name":"","value":""}]}]	Planned v/s Actuals Spends	table	t	11	72
79	1	t	\N	Describe reasons for deviations  of planned vs actuals spends on the project (if any)	document	t	11	72
92	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	2	84
93	1	f	\N	Number of States	kpi	t	2	85
94	2	t	\N	Who is this project for? (List beneficiaries)	multiline	t	2	85
95	3	t	\N		document	t	2	85
96	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	2	86
97	1	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]}]	Disbursement Details	disbursement	t	2	87
128	1	t	\N		document	t	11	112
129	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	113
130	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	114
131	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	115
132	2	t	[{"name":"1","header":"Actual Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]}]	Instalment	disbursement	t	11	115
133	1	t	\N	New KPI	kpi	t	11	116
134	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	117
135	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	118
136	1	f	\N	Increase in additional training hours	kpi	t	11	119
137	1	t	\N		document	t	11	120
138	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	121
139	1	t	[{"name":"2","header":"Planned Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":"","dataType":null}]}]	Disbursement Details	disbursement	t	11	122
140	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	123
141	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	124
142	1	f	\N	Increase in additional training hours	kpi	t	11	125
143	1	t	\N		document	t	11	126
144	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	127
66	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	61
67	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	62
68	1	t	\N	Otuput	multiline	t	11	63
69	1	t	\N	Learning 	multiline	t	11	64
70	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	65
71	1	t	\N	Describe reasons for deviations  of planned vs actuals spends on the project (if any)	multiline	t	11	66
72	2	t	\N	Planned v/s Actuals Spends	multiline	t	11	66
145	1	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}]}]	Disbursement Details	disbursement	t	11	128
161	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	143
162	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	144
163	1	f	\N	Describe the risks to the project & mitigation plans	kpi	t	11	145
164	2	f	\N	Aut sit consequatur qui provident sit hic voluptatem minus voluptatum.	kpi	t	11	145
165	1	t	\N		document	t	11	146
166	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	147
167	1	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false}]	Disbursement Details	disbursement	t	11	148
183	1	f	\N	Increase in additional training hours	kpi	t	11	163
184	2	t	\N		document	t	11	163
185	1	t	\N		document	t	11	164
186	1	t	\N	Describe the project challenges encountered and mitigations for this reporting period (List as many as possible).	multiline	t	11	165
187	1	t	\N	Describe reasons for deviations  of planned vs actuals spends on the project (if any)	multiline	t	11	166
188	1	t	\N	Describe the major activities of the project achieved during this reporting period ( List as many as possible.)	multiline	t	11	167
189	1	t	\N	Who is this project for? (List beneficiaries)	multiline	t	11	168
190	1	t	[{"name":"1","header":"Planned Installment #","columns":[{"name":"Disbursement Date","value":"","dataType":"date"},{"name":"Actual Disbursement","value":"","dataType":"currency"},{"name":"Funds from other Sources","value":"","dataType":"currency"},{"name":"Notes","value":""}],"enteredByGrantee":false}]	Disbursement Details	disbursement	t	11	169
\.


--
-- TOC entry 3821 (class 0 OID 306665)
-- Dependencies: 247
-- Data for Name: granter_report_sections; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_report_sections (id, deletable, section_name, section_order, report_template_id, granter_id) FROM stdin;
1	t	Project Summary	1	1	2
2	t	Project Activities & Highlights	2	1	2
3	t	Project Indicators	3	1	2
4	t	Learnings and best practices	4	1	2
5	t	Project Challenges	5	1	2
6	t	Financials	6	1	2
7	t	Project Summary	1	2	11
8	t	Project Activities & Highlights	2	2	11
9	t	Project Indicators	3	2	11
10	t	Learnings and best practices	4	2	11
11	t	Project Challenges	5	2	11
12	t	Financials	6	2	11
112	t	Learnings and best practices	4	20	11
113	t	Project Challenges	5	20	11
114	t	Project Summary	1	20	11
115	t	Project Activities & Highlights	2	20	11
116	t	Project Indicators	3	20	11
117	t	Project Summary	1	21	11
118	t	Project Activities & Highlights	2	21	11
119	t	Project Indicators	3	21	11
120	t	Learnings and best practices	4	21	11
121	t	Project Challenges	5	21	11
122	t	Disbursement Details	7	21	11
123	t	Project Summary	1	22	11
124	t	Project Activities & Highlights	2	22	11
125	t	Project Indicators	3	22	11
126	t	Learnings and best practices	4	22	11
127	t	Project Challenges	5	22	11
128	t	Disbursement Details	7	22	11
61	t	Project Summary	1	11	11
62	t	Project Activities & Highlights	2	11	11
63	t	Project Indicators	3	11	11
64	t	Learnings and best practices	4	11	11
65	t	Project Challenges	5	11	11
66	t	Financials	6	11	11
67	t	Project Summary	1	12	11
68	t	Project Activities & Highlights	2	12	11
69	t	Project Indicators	3	12	11
70	t	Learnings and best practices	4	12	11
71	t	Project Challenges	5	12	11
72	t	Financials	6	12	11
143	t	Project Summary	1	25	11
144	t	Project Activities & Highlights	2	25	11
145	t	Project Indicators	3	25	11
146	t	Learnings and best practices	4	25	11
84	t	Project Summary	1	15	2
85	t	Project Indicators	3	15	2
86	t	Financials	6	15	2
87	t	Disbursement Details	7	15	2
147	t	Project Challenges	5	25	11
148	t	Disbursement Details	7	25	11
163	t	Project Indicators	3	28	11
164	t	Learnings and best practices	4	28	11
165	t	Project Challenges	5	28	11
166	t	Financials	6	28	11
167	t	Project Activities & Highlights	2	28	11
168	t	Project Summary	1	28	11
169	t	Disbursement Details	7	28	11
\.


--
-- TOC entry 3823 (class 0 OID 306671)
-- Dependencies: 249
-- Data for Name: granter_report_templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granter_report_templates (id, description, granter_id, name, published, private_to_report, default_template) FROM stdin;
1	Default Anudan report template	2	Default Anudan Report Template	t	f	t
2	Default Anudan template	11	Default Anudan template	t	f	t
12	\N	11	undefined	t	t	\N
11	\N	11	undefined	t	t	\N
15	\N	2	Custom Template	f	f	\N
20	\N	11	undefined	t	t	\N
21	\N	11	undefined	t	t	\N
22	\N	11	undefined	t	t	\N
25	\N	11	undefined	t	t	\N
28	\N	11	undefined	t	t	\N
\.


--
-- TOC entry 3824 (class 0 OID 306680)
-- Dependencies: 250
-- Data for Name: granters; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.granters (host_url, image_name, navbar_color, navbar_text_color, id) FROM stdin;
ihf	indian_health_fund.png	#232323	#fff	2
susplus	susplus.png	#232323	#fff	11
\.


--
-- TOC entry 3808 (class 0 OID 306609)
-- Dependencies: 234
-- Data for Name: grants; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grants (id, amount, created_at, created_by, description, end_date, name, representative, start_date, status_name, template_id, updated_at, updated_by, grant_status_id, grantor_org_id, organization_id, substatus_id, note, note_added, note_added_by, moved_on, reference_no) FROM stdin;
\.


--
-- TOC entry 3826 (class 0 OID 306688)
-- Dependencies: 252
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notifications (id, message, posted_on, read, user_id, grant_id, title, report_id, notification_for) FROM stdin;
\.


--
-- TOC entry 3828 (class 0 OID 306696)
-- Dependencies: 254
-- Data for Name: org_config; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.org_config (id, config_name, config_value, granter_id, description, configurable, key, type) FROM stdin;
\.


--
-- TOC entry 3805 (class 0 OID 306591)
-- Dependencies: 231
-- Data for Name: organizations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.organizations (organization_type, id, code, created_at, created_by, name, updated_at, updated_by) FROM stdin;
GRANTER	2	IHF	2019-04-08 03:02:02.431	System	India Health Fund	\N	\N
PLATFORM	3	ANUDAN	2019-04-08 03:02:02.431	System	Anudan	\N	\N
GRANTER	11	SUSPLUS	2020-02-17 08:12:16.057644	System	Sustain Plus Energy Foundation	\N	\N
\.


--
-- TOC entry 3831 (class 0 OID 306707)
-- Dependencies: 257
-- Data for Name: platform; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.platform (host_url, image_name, navbar_color, id) FROM stdin;
anudan	anudan.png	#a41029	3
\.


--
-- TOC entry 3832 (class 0 OID 306713)
-- Dependencies: 258
-- Data for Name: qual_kpi_data_document; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qual_kpi_data_document (id, file_name, file_type, version, qual_kpi_data_id) FROM stdin;
\.


--
-- TOC entry 3834 (class 0 OID 306721)
-- Dependencies: 260
-- Data for Name: qualitative_kpi_notes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.qualitative_kpi_notes (id, message, posted_on, kpi_data_id, posted_by_id) FROM stdin;
\.


--
-- TOC entry 3836 (class 0 OID 306726)
-- Dependencies: 262
-- Data for Name: quant_kpi_data_document; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quant_kpi_data_document (id, file_name, file_type, version, quant_kpi_data_id) FROM stdin;
\.


--
-- TOC entry 3838 (class 0 OID 306734)
-- Dependencies: 264
-- Data for Name: quantitative_kpi_notes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quantitative_kpi_notes (id, message, posted_on, kpi_data_id, posted_by_id) FROM stdin;
\.


--
-- TOC entry 3841 (class 0 OID 306741)
-- Dependencies: 267
-- Data for Name: release; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.release (id, version) FROM stdin;
20330	Anudan Release 1.2.10
\.


--
-- TOC entry 3843 (class 0 OID 306747)
-- Dependencies: 269
-- Data for Name: report_assignments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_assignments (id, report_id, state_id, assignment, anchor) FROM stdin;
\.


--
-- TOC entry 3845 (class 0 OID 306753)
-- Dependencies: 271
-- Data for Name: report_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_history (seqid, id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id, note, note_added, note_added_by, template_id, type, moved_on, linked_approved_reports, report_detail) FROM stdin;
\.


--
-- TOC entry 3847 (class 0 OID 306762)
-- Dependencies: 273
-- Data for Name: report_snapshot; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_snapshot (id, assigned_to_id, report_id, string_attributes, name, description, status_id, start_date, end_date, due_date) FROM stdin;
\.


--
-- TOC entry 3849 (class 0 OID 306771)
-- Dependencies: 275
-- Data for Name: report_specific_section_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_specific_section_attributes (id, attribute_order, deletable, extras, field_name, field_type, required, granter_id, section_id, can_edit) FROM stdin;
\.


--
-- TOC entry 3851 (class 0 OID 306781)
-- Dependencies: 277
-- Data for Name: report_specific_sections; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_specific_sections (id, deletable, report_id, report_template_id, section_name, section_order, granter_id) FROM stdin;
\.


--
-- TOC entry 3853 (class 0 OID 306787)
-- Dependencies: 279
-- Data for Name: report_string_attribute_attachments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_string_attribute_attachments (id, created_by, created_on, description, location, name, title, type, updated_by, updated_on, version, report_string_attribute_id) FROM stdin;
\.


--
-- TOC entry 3855 (class 0 OID 306796)
-- Dependencies: 281
-- Data for Name: report_string_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.report_string_attributes (id, frequency, target, value, report_id, section_id, section_attribute_id, grant_level_target, actual_target) FROM stdin;
\.


--
-- TOC entry 3857 (class 0 OID 306806)
-- Dependencies: 283
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reports (id, name, start_date, end_date, due_date, status_id, created_at, created_by, updated_at, updated_by, grant_id, type, note, note_added, note_added_by, template_id, moved_on, linked_approved_reports, report_detail) FROM stdin;
\.


--
-- TOC entry 3858 (class 0 OID 306813)
-- Dependencies: 284
-- Data for Name: rfps; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.rfps (id, created_at, created_by, description, title, updated_at, updated_by, granter_id) FROM stdin;
\.


--
-- TOC entry 3860 (class 0 OID 306821)
-- Dependencies: 286
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.roles (id, created_at, created_by, name, updated_at, updated_by, organization_id, description, internal) FROM stdin;
2	2019-09-17 08:39:04.642281	System	Program Lead	\N	\N	2	\N	f
3	2019-09-17 08:39:04.643356	System	Program Manager	\N	\N	2	\N	f
8	2019-09-17 08:39:04.652747	System	Finance Lead	\N	\N	2	\N	f
7	2019-09-17 08:39:04.649453	System	Finance Manager	\N	\N	2	\N	f
1	2019-09-17 08:39:04.640568	System	Admin	\N	\N	2	\N	t
5	2019-09-17 08:39:04.645742	System	Admin	\N	\N	3	\N	t
11	2020-02-17 08:15:00.874181	System	Program Officer (Central & West)	\N	\N	11	Program Officer (Central & West)	f
10	2020-02-17 08:15:00.862358	System	Admin	\N	\N	11	Admin	t
18	2020-02-24 09:37:31.04	vineet@socialalpha.org	Monitoring Learning Evaluation (Central)	2020-02-24 09:38:57.789	vineet@socialalpha.org	11	Monitoring Learning Evaluation	f
12	2020-02-17 08:15:00.885821	System	Finance (Central)	2020-04-29 15:22:20.691	vineet@socialalpha.org	11	Accounts & Finance (Central)	f
20	2020-02-24 09:38:33.279	vineet@socialalpha.org	Grant Management Team (Central)	2020-04-29 15:25:37.6	vineet@socialalpha.org	11	Pre-approval checks for the Grant and physical Grant Letter printing.	f
13	2020-02-17 08:15:00.89634	System	CEO/ED	2020-04-29 15:32:46.073	vineet@socialalpha.org	11	Managing Committee	f
32	2020-04-29 15:36:15.264	vineet@socialalpha.org	Program Officer (North East)	\N	\N	11	Program Officer for North east	f
14	2020-02-17 08:15:00.91161	System	Hub Manager (Central & West)	2020-04-29 15:37:19.832	vineet@socialalpha.org	11	Hub / Program Manager (Central & West)	f
33	2020-04-29 15:38:12.644	vineet@socialalpha.org	Hub Manager (North East)	\N	\N	11	Hub / Program Manager (North East)	f
34	2020-04-29 15:39:34.837	vineet@socialalpha.org	Hub Manager (North)	\N	\N	11	Hub / Program Manager (North)	f
35	2020-04-29 15:40:40.965	vineet@socialalpha.org	Program Officer (North)	\N	\N	11	Program Officer (North)	f
36	2020-04-29 15:41:39.132	vineet@socialalpha.org	Hub Manager (South)	\N	\N	11	Hub / Program Manager (South)	f
37	2020-04-29 15:42:03.031	vineet@socialalpha.org	Program Officer (South)	\N	\N	11	Program Officer (South)	f
\.


--
-- TOC entry 3862 (class 0 OID 306830)
-- Dependencies: 288
-- Data for Name: roles_permission; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.roles_permission (id, permission, role_id) FROM stdin;
1	Create Grant	3
2	Delete Grant	3
3	Manage Workflows	3
\.


--
-- TOC entry 3864 (class 0 OID 306835)
-- Dependencies: 290
-- Data for Name: submission_note; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.submission_note (id, message, posted_on, posted_by_id, submission_id) FROM stdin;
\.


--
-- TOC entry 3866 (class 0 OID 306840)
-- Dependencies: 292
-- Data for Name: submissions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.submissions (id, created_at, created_by, submit_by, submitted_on, title, updated_at, updated_by, grant_id, submission_status_id) FROM stdin;
\.


--
-- TOC entry 3868 (class 0 OID 306848)
-- Dependencies: 294
-- Data for Name: template_library; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.template_library (id, description, file_type, granter_id, location, name, type, version) FROM stdin;
1	Annexure 1- Project Budget Sheet	\N	2	IHF/template-library/Annexure 1- Project Budget Sheet.xlsx	Annexure 1- Project Budget Sheet	xlsx	1
2	Annexure 2a- Quarterly Progress Report	\N	2	IHF/template-library/Annexure 2a- Quarterly Progress Report.docx	Annexure 2a- Quarterly Progress Report	docx	1
3	Annexure 2b- Utilisation Certificate (UC)	\N	2	IHF/template-library/Annexure 2b- Utilisation Certificate (UC).xls	Annexure 2b- Utilisation Certificate (UC)	xls	1
4	Annexure 3- Annual Progress Report	\N	2	IHF/template-library/Annexure 3- Annual Progress Report.docx	Annexure 3- Annual Progress Report	docx	1
5	Annexure 4- Approved AWP Template	\N	2	IHF/template-library/Annexure 4- Approved AWP Template.xlsx	Annexure 4- Approved AWP Template	xlsx	1
6	Annexure 5- LFA Template	\N	2	IHF/template-library/Annexure 5- LFA Template.xlsx	Annexure 5- LFA Template	xlsx	1
7	Annexure 6- Guideline for Budget Template	\N	2	IHF/template-library/Annexure 6- Guideline for Budget Template.docx	Annexure 6- Guideline for Budget Template	docx	1
8	Annexure 7- Guideline for Utilisation Certificate (UC)	\N	2	IHF/template-library/Annexure 7- Guideline for Utilisation Certificate (UC).docx	Annexure 7- Guideline for Utilisation Certificate (UC)	docx	1
9	Annexure 8- Guideline for Annual Work Plan	\N	2	IHF/template-library/Annexure 8- Guideline for Annual Work Plan.docx	Annexure 8- Guideline for Annual Work Plan	docx	1
14	Annexure 1- Project Budget Sheet.xlsx	xlsx	11	SUSPLUS/template-library/Annexure 1- Project Budget Sheet.xlsx	Annexure 1- Project Budget Sheet	xlsx	1
15	Annexure 2a- Quarterly Progress Report.docx	docx	11	SUSPLUS/template-library/Annexure 2a- Quarterly Progress Report.docx	Annexure 2a- Quarterly Progress Report	docx	1
16	Annexure 2b- Utilisation Certificate (UC).xls	xls	11	SUSPLUS/template-library/Annexure 2b- Utilisation Certificate (UC).xls	Annexure 2b- Utilisation Certificate (UC)	xls	1
17	Annexure 3- Annual Progress Report.docx	docx	11	SUSPLUS/template-library/Annexure 3- Annual Progress Report.docx	Annexure 3- Annual Progress Report	docx	1
18	Annexure 4- Approved AWP Template.xlsx	xlsx	11	SUSPLUS/template-library/Annexure 4- Approved AWP Template.xlsx	Annexure 4- Approved AWP Template	xlsx	1
19	Annexure 5- LFA Template.xlsx	xlsx	11	SUSPLUS/template-library/Annexure 5- LFA Template.xlsx	Annexure 5- LFA Template	xlsx	1
20	Annexure 6- Guideline for Budget Template.docx	docx	11	SUSPLUS/template-library/Annexure 6- Guideline for Budget Template.docx	Annexure 6- Guideline for Budget Template	docx	1
21	Annexure 7- Guideline for Utilisation Certificate (UC).docx	docx	11	SUSPLUS/template-library/Annexure 7- Guideline for Utilisation Certificate (UC).docx	Annexure 7- Guideline for Utilisation Certificate (UC)	docx	1
22	Annexure 8- Guideline for Annual Work Plan.docx	docx	11	SUSPLUS/template-library/Annexure 8- Guideline for Annual Work Plan.docx	Annexure 8- Guideline for Annual Work Plan	docx	1
23	Annexure 9 - Declaration of FCRA.docx	docx	11	SUSPLUS/template-library/Annexure 9 - Declaration of FCRA.docx	Annexure 9 - Declaration of FCRA	docx	1
\.


--
-- TOC entry 3870 (class 0 OID 306856)
-- Dependencies: 296
-- Data for Name: templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.templates (id, description, file_type, location, name, type, version, kpi_id) FROM stdin;
\.


--
-- TOC entry 3872 (class 0 OID 306864)
-- Dependencies: 298
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.user_roles (id, role_id, user_id) FROM stdin;
2	1	2
3	2	2
6	1	4
7	3	4
8	3	5
9	3	6
10	5	7
13	2	10
14	7	11
15	8	12
16	7	14
17	3	15
18	3	16
19	3	17
12	2	9
20	11	29
21	11	31
22	12	26
23	12	32
24	14	33
25	14	27
26	13	28
27	13	30
28	10	30
29	10	28
34	20	38
36	11	40
42	3	46
44	32	48
45	35	49
46	34	50
47	33	51
48	37	52
49	37	53
50	36	54
32	12	36
33	18	37
35	12	39
51	32	55
53	32	57
\.


--
-- TOC entry 3806 (class 0 OID 306597)
-- Dependencies: 232
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, created_at, created_by, email_id, first_name, last_name, password, updated_at, updated_by, organization_id, active, user_profile) FROM stdin;
15	2019-09-17 13:14:48.550708	System	himanshu@indiahealthfund.org	Himanshu	Sharma	password	\N	\N	2	t	\N
16	2019-09-17 13:14:48.596141	System	nishant@indiahealthfund.org	Nishant	Chavan	password	\N	\N	2	t	\N
17	2019-09-17 13:14:48.620551	System	sagar@indiahealthfund.org	Sagar	Atre	password	\N	\N	2	t	\N
37	2020-02-24 09:40:55.229	vineet@socialalpha.org	maneesha.s@sustain-plus.org	Maneesha	Sarin	password	\N	\N	11	t	\N
11	2019-04-08 03:00:16.545	System	shreyas@indiahealthfund.org	Shreyas	Dwaja	password	\N	\N	2	t	\N
9	2019-04-08 03:00:16.545	System	jayeeta@indiahealthfund.org	Jayeeta	Chowdhury	password	\N	\N	2	t	\N
29	2020-02-17 08:14:28.352364	System	vineet.prasani@gmail.com	Vineet	Program Officer	password	\N	\N	11	t	\N
6	2019-04-08 03:00:16.545	System	manasi.arora@enstratify.com	Manasi	PL	password	\N	\N	2	f	\N
7	2019-04-08 03:00:16.545	System	anudan-admin@ihf.com	Anudan	Admin	password	\N	\N	3	f	\N
10	2019-04-08 03:00:16.545	System	jchowdhury@tatatrusts.org	Chowdhury	PL	password	\N	\N	2	f	\N
2	2019-04-08 03:00:16.545	System	vineet_prasani@email.com	Vineet	Prasani	password	\N	\N	2	t	\N
5	2019-04-08 03:00:16.545	System	vineet.prasani@gmail.com	Vineet	Prasani	password	\N	\N	2	t	\N
14	2019-04-08 03:00:16.545	System	vineet@socialalpha.org	Vineet	Finance	password	\N	\N	2	t	\N
4	2019-04-08 03:00:16.545	System	ranjitvictor@gmail.com	Ranjit	Victor	password	\N	\N	2	t	\N
30	2020-02-17 08:14:28.368378	System	gneelam@tatatrusts.org	Ganesh	Neelam	password	\N	\N	11	t	\N
46	2020-04-29 15:46:32.632	vineet.prasani@gmail.com	ranjitvictor+apr29@gmail.com	Ranjit	Victor One	password	\N	\N	2	t	\N
31	2020-02-17 08:14:28.410636	System	kkhera@tatatrusts.org	Karan	Khera	password	\N	\N	11	t	\N
33	2020-02-17 08:14:28.442034	System	adeb@tatatrusts.org	Ayan	Deb	password	\N	\N	11	t	\N
27	2020-02-17 08:14:28.320377	System	vineet_prasani@email.com	Vineet 	Program Leader	password	\N	\N	11	t	\N
28	2020-02-17 08:14:28.336297	System	vineet@socialalpha.org	Approval	Authority	password	\N	\N	11	t	\N
26	2020-02-17 08:14:28.302502	System	tbd@gmail.com	Vineet	Finance Manager	password	\N	\N	11	t	\N
57	2020-05-06 13:12:35.83	vineet@socialalpha.org	barsha.d@sustain-plus.org	Barsha	Bas	sustainguwahati	\N	\N	11	t	\N
39	2020-02-24 09:41:34.139	vineet@socialalpha.org	mahesh.h@cinicell.org	Mahesh 	Hegde	mahesh251	\N	\N	11	t	\N
48	2020-04-29 18:17:04.962	vineet@socialalpha.org	anirban.d@sustain-plus.org	Anirban	Dutta	nokian72#	\N	\N	11	t	\N
38	2020-02-24 09:41:15.903	vineet@socialalpha.org	ksharma@tatatrusts.org	Kavita	Sharma	CInI2013	\N	\N	11	t	\N
32	2020-02-17 08:14:28.425773	System	sukesh.j@sustain-plus.org	Sukesh	Bhat	sustain@2019	\N	\N	11	t	\N
36	2020-02-24 09:40:27.249	vineet@socialalpha.org	sanjna.a@sustain-plus.org	Sanjana	Aggarwal	password	\N	\N	11	t	\N
12	2019-04-08 03:00:16.545	System	archana@indiahealthfund.org	Archana	Challa	password	\N	\N	2	t	\N
40	2020-03-10 04:35:18.35	vineet@socialalpha.org	sgahoi@tatatrusts.org	Siddharth	Gahoi	password	\N	\N	11	t	\N
53	2020-04-29 18:21:05.116	vineet@socialalpha.org	archana.r@sustain-plus.org	Archana	Rao	rejuvenation	\N	\N	11	t	\N
54	2020-04-29 18:21:55.372	vineet@socialalpha.org	jalaluddin.b@sustain-plus.org	Jalaluddin	Basha Shaik	Jalaal@1974	\N	\N	11	t	\N
49	2020-04-29 18:17:59.535	vineet@socialalpha.org	ganesh.p@sustain-plus.org	Ganesh	Pillai	anudan@1234	\N	\N	11	t	\N
50	2020-04-29 18:18:44.121	vineet@socialalpha.org	bigsna.g@sustain-plus.org	Bigsna	Gill	sustain@1983	\N	\N	11	t	\N
55	2020-05-01 15:09:03.557	vineet@socialalpha.org	ranjitvictor@gmail.com	Program	Officer	password	\N	\N	11	t	\N
51	2020-04-29 18:19:31.99	vineet@socialalpha.org	deepak.c@sustain-plus.org	Deepak	Chetia	password	\N	\N	11	t	\N
52	2020-04-29 18:20:47.059	vineet@socialalpha.org	supriya.g@sustain-plus.org	SUPRIYA GOWDA	K R	P@zzw0rd1	\N	\N	11	t	\N
\.


--
-- TOC entry 3875 (class 0 OID 306871)
-- Dependencies: 301
-- Data for Name: work_flow_permission; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.work_flow_permission (id, action, from_name, from_state_id, note_required, to_name, to_state_id) FROM stdin;
\.


--
-- TOC entry 3876 (class 0 OID 306877)
-- Dependencies: 302
-- Data for Name: workflow_action_permission; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflow_action_permission (id, permissions_string) FROM stdin;
\.


--
-- TOC entry 3877 (class 0 OID 306880)
-- Dependencies: 303
-- Data for Name: workflow_state_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflow_state_permissions (id, created_at, created_by, permission, updated_at, updated_by, role_id, workflow_status_id) FROM stdin;
1	2019-09-17 08:39:04.729672	System	MANAGE	\N	\N	3	1
2	2019-09-17 08:39:04.73203	System	MANAGE	\N	\N	2	1
3	2019-09-17 08:39:04.733513	System	VIEW	\N	\N	3	2
5	2019-09-17 08:39:04.735989	System	MANAGE	\N	\N	2	3
6	2019-09-17 08:39:04.737641	System	VIEW	\N	\N	3	3
7	2019-09-17 08:39:04.739303	System	VIEW	\N	\N	7	3
8	2019-09-17 08:39:04.740619	System	VIEW	\N	\N	2	4
9	2019-09-17 08:39:04.741971	System	VIEW	\N	\N	3	4
10	2019-09-17 08:39:04.743411	System	VIEW	\N	\N	7	4
11	2019-09-17 08:39:04.74466	System	VIEW	\N	\N	2	5
12	2019-09-17 08:39:04.74615	System	VIEW	\N	\N	3	5
13	2019-09-17 08:39:04.747402	System	VIEW	\N	\N	7	5
14	2019-09-17 13:14:55.699117	System	MANAGE	\N	\N	8	2
4	2019-09-17 08:39:04.734665	System	MANAGE	\N	\N	7	2
\.


--
-- TOC entry 3879 (class 0 OID 306888)
-- Dependencies: 305
-- Data for Name: workflow_status_transitions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflow_status_transitions (id, action, created_at, created_by, note_required, updated_at, updated_by, from_state_id, role_id, to_state_id, workflow_id, seq_order) FROM stdin;
35	Submit	2020-04-23 03:50:43.30356	System	t	\N	\N	1	\N	2	1	0
36	Approve	2020-04-23 03:50:43.31056	System	t	\N	\N	2	\N	3	1	0
37	Approve	2020-04-23 03:50:43.324951	System	t	\N	\N	3	\N	4	1	0
38	Close	2020-04-23 03:50:43.342295	System	t	\N	\N	4	\N	5	1	0
39	Return	2020-04-23 03:50:43.361067	System	t	\N	\N	2	\N	1	1	1
40	Return	2020-04-23 03:50:44.056997	System	t	\N	\N	3	\N	2	1	1
41	Publish	2020-04-23 05:29:44.486814	System	t	\N	\N	8	\N	6	5	0
42	Submit	2020-04-23 05:29:44.498229	System	t	\N	\N	6	\N	9	5	0
43	Approve	2020-04-23 05:29:44.509633	System	t	\N	\N	9	\N	10	5	0
44	Request Modifications	2020-04-23 05:29:45.202207	System	t	\N	\N	9	\N	6	5	1
49	Submit for Review	2020-04-29 10:28:12.898617	System	t	\N	\N	11	\N	12	6	0
50	Submit for GMT Review	2020-04-29 10:28:12.908447	System	t	\N	\N	12	\N	22	6	0
51	Submit for Final Approval	2020-04-29 10:28:12.918545	System	t	\N	\N	22	\N	14	6	0
52	Approve for Release	2020-04-29 10:28:12.926843	System	t	\N	\N	14	\N	15	6	0
53	Close	2020-04-29 10:28:12.980134	System	t	\N	\N	15	\N	16	6	0
54	Request Modifications	2020-04-29 10:28:12.987694	System	t	\N	\N	12	\N	11	6	1
55	Request Modifications	2020-04-29 10:28:12.994181	System	t	\N	\N	22	\N	12	6	1
56	Request Modifications	2020-04-29 10:28:13.647737	System	t	\N	\N	14	\N	22	6	1
57	Publish	2020-04-29 11:22:51.61334	System	t	\N	\N	17	\N	18	7	0
58	Submit for Approval	2020-04-29 11:22:51.632294	System	t	\N	\N	18	\N	19	7	0
59	Submit for Finance Approval	2020-04-29 11:22:51.654104	System	t	\N	\N	19	\N	23	7	0
60	Approve for Final Approval	2020-04-29 11:22:51.665589	System	t	\N	\N	23	\N	24	7	0
61	Approve	2020-04-29 11:22:51.704601	System	t	\N	\N	24	\N	20	7	0
62	Request Modifications	2020-04-29 11:22:51.71428	System	t	\N	\N	19	\N	18	7	1
63	Request Modifications	2020-04-29 11:22:51.731509	System	t	\N	\N	23	\N	19	7	1
64	Request Modifications	2020-04-29 11:22:52.518888	System	t	\N	\N	24	\N	23	7	1
\.


--
-- TOC entry 3809 (class 0 OID 306615)
-- Dependencies: 235
-- Data for Name: workflow_statuses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflow_statuses (id, created_at, created_by, display_name, initial, internal_status, name, terminal, updated_at, updated_by, verb, workflow_id) FROM stdin;
5	2019-09-17 08:39:04.71808	System	CLOSED	f	CLOSED	CLOSED	t	\N	\N	Closed	1
1	2019-09-17 08:39:04.710916	System	DRAFT	t	DRAFT	DRAFT	f	\N	\N	Re-submission	1
2	2019-09-17 08:39:04.713053	System	Submitted (Pending Review)	f	REVIEW	SUBMITTED (Pending Review)	f	\N	\N	Review	1
3	2019-09-17 08:39:04.715114	System	Submitted (Pending Review)	f	REVIEW	APPROVED (Pending Review)	f	\N	\N	Review	1
8	2019-09-17 08:39:04.710916	System	DRAFT	t	DRAFT	DRAFT	f	\N	\N	Re-submission	5
9	2019-09-17 08:39:04.713053	System	Submitted (Pending Review)	f	REVIEW	SUBMITTED (Pending Review)	f	\N	\N	Review	5
6	2019-09-17 08:39:04.716713	System	Published	f	ACTIVE	Published	f	\N	\N	ACTIVE	5
11	2020-02-17 08:21:19.43246	System	Draft	t	DRAFT	Draft	f	\N	\N	Draft	6
16	2020-02-17 08:21:21.2552	System	Closed	f	CLOSED	Closed	t	\N	\N	Closed	6
17	2020-02-17 08:22:17.901802	System	Draft	t	DRAFT	Draft	f	\N	\N	Draft	7
18	2020-02-17 08:22:17.9183	System	Published	f	ACTIVE	Published	f	\N	\N	Published	7
21	2020-04-22 17:02:23.587645	System	MLE Review	f	REVIEW	MLE Review	f	\N	\N	\N	6
22	2020-04-22 17:02:24.848523	System	GMT Review	f	REVIEW	GMT Review	f	\N	\N	\N	6
13	2020-02-17 08:21:19.473821	System	Finance Review	f	REVIEW	Finance Review	f	\N	\N	In Finance Review	6
4	2019-09-17 08:39:04.716713	System	Active	f	ACTIVE	Active	f	\N	\N	ACTIVE	1
15	2020-02-17 08:21:19.539954	System	Active	f	ACTIVE	Active	f	\N	\N	Approved	6
10	2019-09-17 08:39:04.715114	System	Approved	f	CLOSED	Approved	t	\N	\N	Review	5
20	2020-02-17 08:22:19.255933	System	Approved	f	CLOSED	Approved	t	\N	\N	Approved	7
12	2020-02-17 08:21:19.454955	System	Program Review (Hub)	f	REVIEW	Program Review (Hub)	f	\N	\N	In Program Review (1)	6
14	2020-02-17 08:21:19.492619	System	CEO/ED Approval	f	REVIEW	CEO/ED Approval	f	\N	\N	In Program Review (2)	6
19	2020-02-17 08:22:17.937497	System	Program Review	f	REVIEW	Program Review	f	\N	\N	Submitted	7
23	2020-04-29 11:16:20.105153	System	Finance Review	f	REVIEW	Finance Review	f	\N	\N	\N	7
24	2020-04-29 11:18:55.242587	System	Hub Review	f	REVIEW	Hub Review	f	\N	\N	\N	7
\.


--
-- TOC entry 3882 (class 0 OID 306898)
-- Dependencies: 308
-- Data for Name: workflow_transition_model; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflow_transition_model (id, _from, _performedby, _to, action, from_state_id, role_id, to_state_id, seq_order) FROM stdin;
\.


--
-- TOC entry 3883 (class 0 OID 306904)
-- Dependencies: 309
-- Data for Name: workflows; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.workflows (id, created_at, created_by, description, name, object, updated_at, updated_by, granter_id) FROM stdin;
1	2019-09-17 08:39:04.704526	System	\N	IHF - Grants Workflow	GRANT	\N	\N	2
2	2019-09-17 08:39:04.706585	System	\N	IHF - KPI Submissions Workflow	SUBMISSION	\N	\N	2
5	2020-02-17 08:09:10.740015	System	Reports flow	Report flow	REPORT	\N	\N	2
6	2020-02-17 08:20:53.752926	System	Default Sustain Plus Grant workflow	Default Sustain Plus Grant workflow	GRANT	\N	\N	11
7	2020-02-17 08:20:55.368175	System	Default Sustain Plus Report workflow	Default Sustain Plus Report workflow	REPORT	\N	\N	11
\.


--
-- TOC entry 3928 (class 0 OID 0)
-- Dependencies: 197
-- Name: app_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.app_config_id_seq', 26, true);


--
-- TOC entry 3929 (class 0 OID 0)
-- Dependencies: 199
-- Name: doc_kpi_data_document_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.doc_kpi_data_document_id_seq', 1, false);


--
-- TOC entry 3930 (class 0 OID 0)
-- Dependencies: 201
-- Name: document_kpi_notes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.document_kpi_notes_id_seq', 1, false);


--
-- TOC entry 3931 (class 0 OID 0)
-- Dependencies: 203
-- Name: grant_assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_assignments_id_seq', 457, true);


--
-- TOC entry 3932 (class 0 OID 0)
-- Dependencies: 204
-- Name: grant_attrib_attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_attrib_attachments_id_seq', 95, true);


--
-- TOC entry 3933 (class 0 OID 0)
-- Dependencies: 206
-- Name: grant_document_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_document_attributes_id_seq', 1, false);


--
-- TOC entry 3934 (class 0 OID 0)
-- Dependencies: 208
-- Name: grant_document_kpi_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_document_kpi_data_id_seq', 1, false);


--
-- TOC entry 3935 (class 0 OID 0)
-- Dependencies: 209
-- Name: grant_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_history_id_seq', 132, true);


--
-- TOC entry 3936 (class 0 OID 0)
-- Dependencies: 212
-- Name: grant_kpis_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_kpis_id_seq', 1, false);


--
-- TOC entry 3937 (class 0 OID 0)
-- Dependencies: 214
-- Name: grant_qualitative_kpi_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_qualitative_kpi_data_id_seq', 1, false);


--
-- TOC entry 3938 (class 0 OID 0)
-- Dependencies: 216
-- Name: grant_quantitative_kpi_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_quantitative_kpi_data_id_seq', 1, false);


--
-- TOC entry 3939 (class 0 OID 0)
-- Dependencies: 218
-- Name: grant_section_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_section_attributes_id_seq', 22, true);


--
-- TOC entry 3940 (class 0 OID 0)
-- Dependencies: 220
-- Name: grant_sections_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_sections_id_seq', 6, true);


--
-- TOC entry 3941 (class 0 OID 0)
-- Dependencies: 221
-- Name: grant_snapshot_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_snapshot_id_seq', 189, true);


--
-- TOC entry 3942 (class 0 OID 0)
-- Dependencies: 224
-- Name: grant_specific_section_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_specific_section_attributes_id_seq', 2843, true);


--
-- TOC entry 3943 (class 0 OID 0)
-- Dependencies: 226
-- Name: grant_specific_sections_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_specific_sections_id_seq', 718, true);


--
-- TOC entry 3944 (class 0 OID 0)
-- Dependencies: 229
-- Name: grant_string_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grant_string_attributes_id_seq', 2883, true);


--
-- TOC entry 3945 (class 0 OID 0)
-- Dependencies: 238
-- Name: granter_grant_section_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_grant_section_attributes_id_seq', 9729, true);


--
-- TOC entry 3946 (class 0 OID 0)
-- Dependencies: 240
-- Name: granter_grant_sections_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_grant_sections_id_seq', 2641, true);


--
-- TOC entry 3947 (class 0 OID 0)
-- Dependencies: 242
-- Name: granter_grant_templates_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_grant_templates_id_seq', 346, true);


--
-- TOC entry 3948 (class 0 OID 0)
-- Dependencies: 244
-- Name: granter_report_section_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_report_section_attributes_id_seq', 190, true);


--
-- TOC entry 3949 (class 0 OID 0)
-- Dependencies: 246
-- Name: granter_report_sections_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_report_sections_id_seq', 169, true);


--
-- TOC entry 3950 (class 0 OID 0)
-- Dependencies: 248
-- Name: granter_report_templates_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.granter_report_templates_id_seq', 28, true);


--
-- TOC entry 3951 (class 0 OID 0)
-- Dependencies: 251
-- Name: grants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grants_id_seq', 95, true);


--
-- TOC entry 3952 (class 0 OID 0)
-- Dependencies: 253
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notifications_id_seq', 444, true);


--
-- TOC entry 3953 (class 0 OID 0)
-- Dependencies: 255
-- Name: org_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.org_config_id_seq', 2, true);


--
-- TOC entry 3954 (class 0 OID 0)
-- Dependencies: 256
-- Name: organizations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.organizations_id_seq', 26, true);


--
-- TOC entry 3955 (class 0 OID 0)
-- Dependencies: 259
-- Name: qual_kpi_data_document_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.qual_kpi_data_document_id_seq', 1, false);


--
-- TOC entry 3956 (class 0 OID 0)
-- Dependencies: 261
-- Name: qualitative_kpi_notes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.qualitative_kpi_notes_id_seq', 1, false);


--
-- TOC entry 3957 (class 0 OID 0)
-- Dependencies: 263
-- Name: quant_kpi_data_document_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quant_kpi_data_document_id_seq', 1, false);


--
-- TOC entry 3958 (class 0 OID 0)
-- Dependencies: 265
-- Name: quantitative_kpi_notes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quantitative_kpi_notes_id_seq', 1, false);


--
-- TOC entry 3959 (class 0 OID 0)
-- Dependencies: 266
-- Name: release_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.release_id_seq', 20330, true);


--
-- TOC entry 3960 (class 0 OID 0)
-- Dependencies: 268
-- Name: report_assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_assignments_id_seq', 1357, true);


--
-- TOC entry 3961 (class 0 OID 0)
-- Dependencies: 270
-- Name: report_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_history_id_seq', 45, true);


--
-- TOC entry 3962 (class 0 OID 0)
-- Dependencies: 272
-- Name: report_snapshot_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_snapshot_id_seq', 149, true);


--
-- TOC entry 3963 (class 0 OID 0)
-- Dependencies: 274
-- Name: report_specific_section_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_specific_section_attributes_id_seq', 1381, true);


--
-- TOC entry 3964 (class 0 OID 0)
-- Dependencies: 276
-- Name: report_specific_sections_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_specific_sections_id_seq', 1199, true);


--
-- TOC entry 3965 (class 0 OID 0)
-- Dependencies: 278
-- Name: report_string_attribute_attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_string_attribute_attachments_id_seq', 6, true);


--
-- TOC entry 3966 (class 0 OID 0)
-- Dependencies: 280
-- Name: report_string_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.report_string_attributes_id_seq', 1381, true);


--
-- TOC entry 3967 (class 0 OID 0)
-- Dependencies: 282
-- Name: reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reports_id_seq', 182, true);


--
-- TOC entry 3968 (class 0 OID 0)
-- Dependencies: 285
-- Name: rfps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.rfps_id_seq', 1, false);


--
-- TOC entry 3969 (class 0 OID 0)
-- Dependencies: 287
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_id_seq', 40, true);


--
-- TOC entry 3970 (class 0 OID 0)
-- Dependencies: 289
-- Name: roles_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_permission_id_seq', 3, true);


--
-- TOC entry 3971 (class 0 OID 0)
-- Dependencies: 291
-- Name: submission_note_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.submission_note_id_seq', 1, false);


--
-- TOC entry 3972 (class 0 OID 0)
-- Dependencies: 293
-- Name: submissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.submissions_id_seq', 1, false);


--
-- TOC entry 3973 (class 0 OID 0)
-- Dependencies: 295
-- Name: template_library_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.template_library_id_seq', 23, true);


--
-- TOC entry 3974 (class 0 OID 0)
-- Dependencies: 297
-- Name: templates_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.templates_id_seq', 1, false);


--
-- TOC entry 3975 (class 0 OID 0)
-- Dependencies: 299
-- Name: user_roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.user_roles_id_seq', 53, true);


--
-- TOC entry 3976 (class 0 OID 0)
-- Dependencies: 300
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 57, true);


--
-- TOC entry 3977 (class 0 OID 0)
-- Dependencies: 304
-- Name: workflow_state_permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.workflow_state_permissions_id_seq', 14, true);


--
-- TOC entry 3978 (class 0 OID 0)
-- Dependencies: 306
-- Name: workflow_status_transitions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.workflow_status_transitions_id_seq', 64, true);


--
-- TOC entry 3979 (class 0 OID 0)
-- Dependencies: 307
-- Name: workflow_statuses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.workflow_statuses_id_seq', 24, true);


--
-- TOC entry 3980 (class 0 OID 0)
-- Dependencies: 310
-- Name: workflows_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.workflows_id_seq', 7, true);


--
-- TOC entry 3460 (class 2606 OID 306951)
-- Name: app_config app_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.app_config
    ADD CONSTRAINT app_config_pkey PRIMARY KEY (id);


--
-- TOC entry 3462 (class 2606 OID 306953)
-- Name: doc_kpi_data_document doc_kpi_data_document_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doc_kpi_data_document
    ADD CONSTRAINT doc_kpi_data_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3464 (class 2606 OID 306955)
-- Name: document_kpi_notes document_kpi_notes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.document_kpi_notes
    ADD CONSTRAINT document_kpi_notes_pkey PRIMARY KEY (id);


--
-- TOC entry 3466 (class 2606 OID 306957)
-- Name: grant_assignments grant_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_assignments
    ADD CONSTRAINT grant_assignments_pkey PRIMARY KEY (id);


--
-- TOC entry 3468 (class 2606 OID 306959)
-- Name: grant_document_attributes grant_document_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_attributes
    ADD CONSTRAINT grant_document_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3470 (class 2606 OID 306961)
-- Name: grant_document_kpi_data grant_document_kpi_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_kpi_data
    ADD CONSTRAINT grant_document_kpi_data_pkey PRIMARY KEY (id);


--
-- TOC entry 3472 (class 2606 OID 306963)
-- Name: grant_history grant_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_history
    ADD CONSTRAINT grant_history_pkey PRIMARY KEY (seqid);


--
-- TOC entry 3474 (class 2606 OID 306965)
-- Name: grant_kpis grant_kpis_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_kpis
    ADD CONSTRAINT grant_kpis_pkey PRIMARY KEY (id);


--
-- TOC entry 3476 (class 2606 OID 306967)
-- Name: grant_qualitative_kpi_data grant_qualitative_kpi_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_qualitative_kpi_data
    ADD CONSTRAINT grant_qualitative_kpi_data_pkey PRIMARY KEY (id);


--
-- TOC entry 3478 (class 2606 OID 306969)
-- Name: grant_quantitative_kpi_data grant_quantitative_kpi_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_quantitative_kpi_data
    ADD CONSTRAINT grant_quantitative_kpi_data_pkey PRIMARY KEY (id);


--
-- TOC entry 3480 (class 2606 OID 306971)
-- Name: grant_section_attributes grant_section_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_section_attributes
    ADD CONSTRAINT grant_section_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3482 (class 2606 OID 306973)
-- Name: grant_sections grant_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_sections
    ADD CONSTRAINT grant_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 3484 (class 2606 OID 306975)
-- Name: grant_snapshot grant_snapshot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_snapshot
    ADD CONSTRAINT grant_snapshot_pkey PRIMARY KEY (id);


--
-- TOC entry 3486 (class 2606 OID 306977)
-- Name: grant_specific_section_attributes grant_specific_section_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_section_attributes
    ADD CONSTRAINT grant_specific_section_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3488 (class 2606 OID 306979)
-- Name: grant_specific_sections grant_specific_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_sections
    ADD CONSTRAINT grant_specific_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 3490 (class 2606 OID 306981)
-- Name: grant_string_attribute_attachments grant_string_attribute_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attribute_attachments
    ADD CONSTRAINT grant_string_attribute_attachments_pkey PRIMARY KEY (id);


--
-- TOC entry 3492 (class 2606 OID 306983)
-- Name: grant_string_attributes grant_string_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attributes
    ADD CONSTRAINT grant_string_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3494 (class 2606 OID 306985)
-- Name: grantees grantees_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grantees
    ADD CONSTRAINT grantees_pkey PRIMARY KEY (id);


--
-- TOC entry 3504 (class 2606 OID 306987)
-- Name: granter_grant_section_attributes granter_grant_section_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_section_attributes
    ADD CONSTRAINT granter_grant_section_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3506 (class 2606 OID 306989)
-- Name: granter_grant_sections granter_grant_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_sections
    ADD CONSTRAINT granter_grant_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 3508 (class 2606 OID 306991)
-- Name: granter_grant_templates granter_grant_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_templates
    ADD CONSTRAINT granter_grant_templates_pkey PRIMARY KEY (id);


--
-- TOC entry 3510 (class 2606 OID 306993)
-- Name: granter_report_section_attributes granter_report_section_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_section_attributes
    ADD CONSTRAINT granter_report_section_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3512 (class 2606 OID 306995)
-- Name: granter_report_sections granter_report_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_sections
    ADD CONSTRAINT granter_report_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 3514 (class 2606 OID 306997)
-- Name: granter_report_templates granter_report_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_templates
    ADD CONSTRAINT granter_report_templates_pkey PRIMARY KEY (id);


--
-- TOC entry 3516 (class 2606 OID 306999)
-- Name: granters granters_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granters
    ADD CONSTRAINT granters_pkey PRIMARY KEY (id);


--
-- TOC entry 3500 (class 2606 OID 307001)
-- Name: grants grants_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants
    ADD CONSTRAINT grants_pkey PRIMARY KEY (id);


--
-- TOC entry 3518 (class 2606 OID 307003)
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 3520 (class 2606 OID 307005)
-- Name: org_config org_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.org_config
    ADD CONSTRAINT org_config_pkey PRIMARY KEY (id);


--
-- TOC entry 3496 (class 2606 OID 307007)
-- Name: organizations organizations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_pkey PRIMARY KEY (id);


--
-- TOC entry 3522 (class 2606 OID 307009)
-- Name: platform platform_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT platform_pkey PRIMARY KEY (id);


--
-- TOC entry 3524 (class 2606 OID 307011)
-- Name: qual_kpi_data_document qual_kpi_data_document_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qual_kpi_data_document
    ADD CONSTRAINT qual_kpi_data_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3526 (class 2606 OID 307013)
-- Name: qualitative_kpi_notes qualitative_kpi_notes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qualitative_kpi_notes
    ADD CONSTRAINT qualitative_kpi_notes_pkey PRIMARY KEY (id);


--
-- TOC entry 3528 (class 2606 OID 307015)
-- Name: quant_kpi_data_document quant_kpi_data_document_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quant_kpi_data_document
    ADD CONSTRAINT quant_kpi_data_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3530 (class 2606 OID 307017)
-- Name: quantitative_kpi_notes quantitative_kpi_notes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quantitative_kpi_notes
    ADD CONSTRAINT quantitative_kpi_notes_pkey PRIMARY KEY (id);


--
-- TOC entry 3532 (class 2606 OID 307019)
-- Name: release release_id_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.release
    ADD CONSTRAINT release_id_pk PRIMARY KEY (id);


--
-- TOC entry 3534 (class 2606 OID 307021)
-- Name: report_history report_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_history
    ADD CONSTRAINT report_history_pkey PRIMARY KEY (seqid);


--
-- TOC entry 3536 (class 2606 OID 307023)
-- Name: report_specific_section_attributes report_specific_section_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_specific_section_attributes
    ADD CONSTRAINT report_specific_section_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3538 (class 2606 OID 307025)
-- Name: report_specific_sections report_specific_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_specific_sections
    ADD CONSTRAINT report_specific_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 3540 (class 2606 OID 307027)
-- Name: report_string_attribute_attachments report_string_attribute_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attribute_attachments
    ADD CONSTRAINT report_string_attribute_attachments_pkey PRIMARY KEY (id);


--
-- TOC entry 3542 (class 2606 OID 307029)
-- Name: report_string_attributes report_string_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attributes
    ADD CONSTRAINT report_string_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 3544 (class 2606 OID 307031)
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- TOC entry 3546 (class 2606 OID 307033)
-- Name: rfps rfps_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rfps
    ADD CONSTRAINT rfps_pkey PRIMARY KEY (id);


--
-- TOC entry 3550 (class 2606 OID 307035)
-- Name: roles_permission roles_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permission
    ADD CONSTRAINT roles_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3548 (class 2606 OID 307037)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 3552 (class 2606 OID 307039)
-- Name: submission_note submission_note_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_note
    ADD CONSTRAINT submission_note_pkey PRIMARY KEY (id);


--
-- TOC entry 3554 (class 2606 OID 307041)
-- Name: submissions submissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT submissions_pkey PRIMARY KEY (id);


--
-- TOC entry 3556 (class 2606 OID 307043)
-- Name: template_library template_library_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.template_library
    ADD CONSTRAINT template_library_pkey PRIMARY KEY (id);


--
-- TOC entry 3558 (class 2606 OID 307045)
-- Name: templates templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.templates
    ADD CONSTRAINT templates_pkey PRIMARY KEY (id);


--
-- TOC entry 3560 (class 2606 OID 307047)
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);


--
-- TOC entry 3498 (class 2606 OID 307049)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3562 (class 2606 OID 307051)
-- Name: work_flow_permission work_flow_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.work_flow_permission
    ADD CONSTRAINT work_flow_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3564 (class 2606 OID 307053)
-- Name: workflow_action_permission workflow_action_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_action_permission
    ADD CONSTRAINT workflow_action_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3566 (class 2606 OID 307055)
-- Name: workflow_state_permissions workflow_state_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_state_permissions
    ADD CONSTRAINT workflow_state_permissions_pkey PRIMARY KEY (id);


--
-- TOC entry 3568 (class 2606 OID 307057)
-- Name: workflow_status_transitions workflow_status_transitions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions
    ADD CONSTRAINT workflow_status_transitions_pkey PRIMARY KEY (id);


--
-- TOC entry 3502 (class 2606 OID 307059)
-- Name: workflow_statuses workflow_statuses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_statuses
    ADD CONSTRAINT workflow_statuses_pkey PRIMARY KEY (id);


--
-- TOC entry 3570 (class 2606 OID 307061)
-- Name: workflow_transition_model workflow_transition_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_transition_model
    ADD CONSTRAINT workflow_transition_model_pkey PRIMARY KEY (id);


--
-- TOC entry 3572 (class 2606 OID 307063)
-- Name: workflows workflows_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflows
    ADD CONSTRAINT workflows_pkey PRIMARY KEY (id);


--
-- TOC entry 3644 (class 2620 OID 307064)
-- Name: grants grant_audit; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER grant_audit AFTER UPDATE ON public.grants FOR EACH ROW EXECUTE PROCEDURE public.process_grant_state_change();


--
-- TOC entry 3645 (class 2620 OID 307065)
-- Name: reports report_audit; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER report_audit AFTER UPDATE ON public.reports FOR EACH ROW EXECUTE PROCEDURE public.process_report_state_change();


--
-- TOC entry 3639 (class 2606 OID 307066)
-- Name: workflow_status_transitions fk27a376l9dly50yhv4dyqprgqv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions
    ADD CONSTRAINT fk27a376l9dly50yhv4dyqprgqv FOREIGN KEY (workflow_id) REFERENCES public.workflows(id);


--
-- TOC entry 3586 (class 2606 OID 307071)
-- Name: grant_qualitative_kpi_data fk2s6ithk1xy0ig8jowyiqmo8rx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_qualitative_kpi_data
    ADD CONSTRAINT fk2s6ithk1xy0ig8jowyiqmo8rx FOREIGN KEY (grant_kpi_id) REFERENCES public.grant_kpis(id);


--
-- TOC entry 3606 (class 2606 OID 307076)
-- Name: granter_grant_sections fk3lr5sgkm2s6bx4filctn6gagm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_sections
    ADD CONSTRAINT fk3lr5sgkm2s6bx4filctn6gagm FOREIGN KEY (grant_template_id) REFERENCES public.granter_grant_templates(id);


--
-- TOC entry 3613 (class 2606 OID 307081)
-- Name: platform fk3xre58624noycrrgchsgyef5e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.platform
    ADD CONSTRAINT fk3xre58624noycrrgchsgyef5e FOREIGN KEY (id) REFERENCES public.organizations(id);


--
-- TOC entry 3634 (class 2606 OID 307086)
-- Name: templates fk4op755x1d71aebjj4e8018cc7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.templates
    ADD CONSTRAINT fk4op755x1d71aebjj4e8018cc7 FOREIGN KEY (kpi_id) REFERENCES public.grant_kpis(id);


--
-- TOC entry 3615 (class 2606 OID 307091)
-- Name: qualitative_kpi_notes fk52xwacoieu7isbdcgqy07ekmx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qualitative_kpi_notes
    ADD CONSTRAINT fk52xwacoieu7isbdcgqy07ekmx FOREIGN KEY (posted_by_id) REFERENCES public.users(id);


--
-- TOC entry 3585 (class 2606 OID 307096)
-- Name: grant_kpis fk6qxmldadt1rprdf8v1oesx106; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_kpis
    ADD CONSTRAINT fk6qxmldadt1rprdf8v1oesx106 FOREIGN KEY (grant_id) REFERENCES public.grants(id);


--
-- TOC entry 3637 (class 2606 OID 307101)
-- Name: workflow_state_permissions fk7cek33ktgssedr520yohxkse5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_state_permissions
    ADD CONSTRAINT fk7cek33ktgssedr520yohxkse5 FOREIGN KEY (workflow_status_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3597 (class 2606 OID 307106)
-- Name: grantees fk82ngyn089fjkpmbjg79v75ctx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grantees
    ADD CONSTRAINT fk82ngyn089fjkpmbjg79v75ctx FOREIGN KEY (id) REFERENCES public.organizations(id);


--
-- TOC entry 3599 (class 2606 OID 307111)
-- Name: grants fk881g56ucqjflq4o7hyyrlx2a2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants
    ADD CONSTRAINT fk881g56ucqjflq4o7hyyrlx2a2 FOREIGN KEY (substatus_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3581 (class 2606 OID 307116)
-- Name: grant_history fk881g56ucqjflq4o7hyyrlx2a2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_history
    ADD CONSTRAINT fk881g56ucqjflq4o7hyyrlx2a2 FOREIGN KEY (substatus_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3643 (class 2606 OID 307121)
-- Name: workflows fk8kjwa8ecy2djhc3mmbhvbj7hb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflows
    ADD CONSTRAINT fk8kjwa8ecy2djhc3mmbhvbj7hb FOREIGN KEY (granter_id) REFERENCES public.organizations(id);


--
-- TOC entry 3574 (class 2606 OID 307126)
-- Name: document_kpi_notes fk94ty5yy9jgvquojr9albjxe7r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.document_kpi_notes
    ADD CONSTRAINT fk94ty5yy9jgvquojr9albjxe7r FOREIGN KEY (kpi_data_id) REFERENCES public.grant_document_kpi_data(id);


--
-- TOC entry 3630 (class 2606 OID 307131)
-- Name: submission_note fk9ev4x0qwvnowkyu0ev1539sse; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_note
    ADD CONSTRAINT fk9ev4x0qwvnowkyu0ev1539sse FOREIGN KEY (posted_by_id) REFERENCES public.users(id);


--
-- TOC entry 3631 (class 2606 OID 307136)
-- Name: submission_note fkaqjedpauphanpxfbn87v3tfld; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_note
    ADD CONSTRAINT fkaqjedpauphanpxfbn87v3tfld FOREIGN KEY (submission_id) REFERENCES public.submissions(id);


--
-- TOC entry 3618 (class 2606 OID 307141)
-- Name: quantitative_kpi_notes fkb84h34g0dy0hpf0i8rhkcpma1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quantitative_kpi_notes
    ADD CONSTRAINT fkb84h34g0dy0hpf0i8rhkcpma1 FOREIGN KEY (kpi_data_id) REFERENCES public.grant_quantitative_kpi_data(id);


--
-- TOC entry 3612 (class 2606 OID 307146)
-- Name: granters fkbfopvr9uc3vt0tqg1kom5yy6h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granters
    ADD CONSTRAINT fkbfopvr9uc3vt0tqg1kom5yy6h FOREIGN KEY (id) REFERENCES public.organizations(id);


--
-- TOC entry 3600 (class 2606 OID 307151)
-- Name: grants fkcmlj43405rmsfqlm0x4gs1cli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants
    ADD CONSTRAINT fkcmlj43405rmsfqlm0x4gs1cli FOREIGN KEY (grantor_org_id) REFERENCES public.granters(id);


--
-- TOC entry 3582 (class 2606 OID 307156)
-- Name: grant_history fkcmlj43405rmsfqlm0x4gs1cli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_history
    ADD CONSTRAINT fkcmlj43405rmsfqlm0x4gs1cli FOREIGN KEY (grantor_org_id) REFERENCES public.granters(id);


--
-- TOC entry 3579 (class 2606 OID 307161)
-- Name: grant_document_kpi_data fkd1swedg4q3c9wfo8llld3lacn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_kpi_data
    ADD CONSTRAINT fkd1swedg4q3c9wfo8llld3lacn FOREIGN KEY (submission_id) REFERENCES public.submissions(id);


--
-- TOC entry 3591 (class 2606 OID 307166)
-- Name: grant_specific_section_attributes fkdbh8rbyec5r524690vo6csgle; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_section_attributes
    ADD CONSTRAINT fkdbh8rbyec5r524690vo6csgle FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3607 (class 2606 OID 307171)
-- Name: granter_grant_sections fkdv7se7knwl9xooqukbghcl261; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_sections
    ADD CONSTRAINT fkdv7se7knwl9xooqukbghcl261 FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3594 (class 2606 OID 307176)
-- Name: grant_string_attributes fke0oju6e6wfkn6a8edf6v5sag9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attributes
    ADD CONSTRAINT fke0oju6e6wfkn6a8edf6v5sag9 FOREIGN KEY (section_attribute_id) REFERENCES public.grant_specific_section_attributes(id);


--
-- TOC entry 3595 (class 2606 OID 307181)
-- Name: grant_string_attributes fke7k71toqd2cibb1p6ct63wc1w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attributes
    ADD CONSTRAINT fke7k71toqd2cibb1p6ct63wc1w FOREIGN KEY (grant_id) REFERENCES public.grants(id);


--
-- TOC entry 3608 (class 2606 OID 307186)
-- Name: granter_report_section_attributes fkey_report_section_attr_report_section; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_section_attributes
    ADD CONSTRAINT fkey_report_section_attr_report_section FOREIGN KEY (section_id) REFERENCES public.granter_report_sections(id);


--
-- TOC entry 3609 (class 2606 OID 307191)
-- Name: granter_report_section_attributes fkey_report_section_attrib_granter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_section_attributes
    ADD CONSTRAINT fkey_report_section_attrib_granter FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3610 (class 2606 OID 307196)
-- Name: granter_report_sections fkey_report_section_granter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_sections
    ADD CONSTRAINT fkey_report_section_granter FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3611 (class 2606 OID 307201)
-- Name: granter_report_sections fkey_report_section_report_template; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_report_sections
    ADD CONSTRAINT fkey_report_section_report_template FOREIGN KEY (report_template_id) REFERENCES public.granter_report_templates(id);


--
-- TOC entry 3620 (class 2606 OID 307206)
-- Name: report_specific_section_attributes fkey_report_sp_sec_attr_granter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_specific_section_attributes
    ADD CONSTRAINT fkey_report_sp_sec_attr_granter FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3621 (class 2606 OID 307211)
-- Name: report_specific_section_attributes fkey_report_sp_sec_attr_section; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_specific_section_attributes
    ADD CONSTRAINT fkey_report_sp_sec_attr_section FOREIGN KEY (section_id) REFERENCES public.report_specific_sections(id);


--
-- TOC entry 3622 (class 2606 OID 307216)
-- Name: report_specific_sections fkey_report_specific_section_granter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_specific_sections
    ADD CONSTRAINT fkey_report_specific_section_granter FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3624 (class 2606 OID 307221)
-- Name: report_string_attributes fkey_report_string_report; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attributes
    ADD CONSTRAINT fkey_report_string_report FOREIGN KEY (report_id) REFERENCES public.reports(id);


--
-- TOC entry 3623 (class 2606 OID 307226)
-- Name: report_string_attribute_attachments fkey_string_attach_attrib; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attribute_attachments
    ADD CONSTRAINT fkey_string_attach_attrib FOREIGN KEY (report_string_attribute_id) REFERENCES public.report_string_attributes(id);


--
-- TOC entry 3625 (class 2606 OID 307231)
-- Name: report_string_attributes fkey_string_section; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attributes
    ADD CONSTRAINT fkey_string_section FOREIGN KEY (section_id) REFERENCES public.report_specific_sections(id);


--
-- TOC entry 3626 (class 2606 OID 307236)
-- Name: report_string_attributes fkey_string_section_attribs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_string_attributes
    ADD CONSTRAINT fkey_string_section_attribs FOREIGN KEY (section_attribute_id) REFERENCES public.report_specific_section_attributes(id);


--
-- TOC entry 3580 (class 2606 OID 307241)
-- Name: grant_document_kpi_data fkfeth01qga8x88vu374surppqo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_kpi_data
    ADD CONSTRAINT fkfeth01qga8x88vu374surppqo FOREIGN KEY (grant_kpi_id) REFERENCES public.grant_kpis(id);


--
-- TOC entry 3601 (class 2606 OID 307246)
-- Name: grants fkfxhc0yhlrne4obtxvc11skonn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants
    ADD CONSTRAINT fkfxhc0yhlrne4obtxvc11skonn FOREIGN KEY (organization_id) REFERENCES public.grantees(id);


--
-- TOC entry 3583 (class 2606 OID 307251)
-- Name: grant_history fkfxhc0yhlrne4obtxvc11skonn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_history
    ADD CONSTRAINT fkfxhc0yhlrne4obtxvc11skonn FOREIGN KEY (organization_id) REFERENCES public.grantees(id);


--
-- TOC entry 3617 (class 2606 OID 307256)
-- Name: quant_kpi_data_document fkg1oub7mx2plcgg2rcmoo1x5lf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quant_kpi_data_document
    ADD CONSTRAINT fkg1oub7mx2plcgg2rcmoo1x5lf FOREIGN KEY (quant_kpi_data_id) REFERENCES public.grant_quantitative_kpi_data(id);


--
-- TOC entry 3588 (class 2606 OID 307261)
-- Name: grant_quantitative_kpi_data fkg6kxfcc72cocm6wqomsc8vu8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_quantitative_kpi_data
    ADD CONSTRAINT fkg6kxfcc72cocm6wqomsc8vu8 FOREIGN KEY (submission_id) REFERENCES public.submissions(id);


--
-- TOC entry 3635 (class 2606 OID 307266)
-- Name: user_roles fkh8ciramu9cc9q3qcqiv4ue8a6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 3619 (class 2606 OID 307271)
-- Name: quantitative_kpi_notes fkhbitfsvff7a9lvilmsqg6j589; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quantitative_kpi_notes
    ADD CONSTRAINT fkhbitfsvff7a9lvilmsqg6j589 FOREIGN KEY (posted_by_id) REFERENCES public.users(id);


--
-- TOC entry 3636 (class 2606 OID 307276)
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3604 (class 2606 OID 307281)
-- Name: granter_grant_section_attributes fkhj6nvncasmgr56s0t840loa8g; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_section_attributes
    ADD CONSTRAINT fkhj6nvncasmgr56s0t840loa8g FOREIGN KEY (section_id) REFERENCES public.granter_grant_sections(id);


--
-- TOC entry 3640 (class 2606 OID 307286)
-- Name: workflow_status_transitions fkhq3p8mrvunploh7713igtaowk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions
    ADD CONSTRAINT fkhq3p8mrvunploh7713igtaowk FOREIGN KEY (from_state_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3629 (class 2606 OID 307291)
-- Name: roles_permission fkigkyo0gp095cm55sjfgy0i4lg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permission
    ADD CONSTRAINT fkigkyo0gp095cm55sjfgy0i4lg FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 3590 (class 2606 OID 307296)
-- Name: grant_section_attributes fkj9i8la8732x3w55fujra47bv3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_section_attributes
    ADD CONSTRAINT fkj9i8la8732x3w55fujra47bv3 FOREIGN KEY (section_id) REFERENCES public.grant_sections(id);


--
-- TOC entry 3641 (class 2606 OID 307301)
-- Name: workflow_status_transitions fkjg8davo6hmqd2ailb3ysnwt8j; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions
    ADD CONSTRAINT fkjg8davo6hmqd2ailb3ysnwt8j FOREIGN KEY (to_state_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3632 (class 2606 OID 307306)
-- Name: submissions fkjkfxsyttwtbnpuitanuceacvh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT fkjkfxsyttwtbnpuitanuceacvh FOREIGN KEY (submission_status_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3603 (class 2606 OID 307311)
-- Name: workflow_statuses fkjo7ovfj3t6h7u3nqbflbk2s2n; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_statuses
    ADD CONSTRAINT fkjo7ovfj3t6h7u3nqbflbk2s2n FOREIGN KEY (workflow_id) REFERENCES public.workflows(id);


--
-- TOC entry 3575 (class 2606 OID 307316)
-- Name: document_kpi_notes fkjymhkyqqmvrsj6rjgmxl8rid7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.document_kpi_notes
    ADD CONSTRAINT fkjymhkyqqmvrsj6rjgmxl8rid7 FOREIGN KEY (posted_by_id) REFERENCES public.users(id);


--
-- TOC entry 3602 (class 2606 OID 307321)
-- Name: grants fkldpdqi1vkhahlhaxn5o25vdfa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grants
    ADD CONSTRAINT fkldpdqi1vkhahlhaxn5o25vdfa FOREIGN KEY (grant_status_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3584 (class 2606 OID 307326)
-- Name: grant_history fkldpdqi1vkhahlhaxn5o25vdfa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_history
    ADD CONSTRAINT fkldpdqi1vkhahlhaxn5o25vdfa FOREIGN KEY (grant_status_id) REFERENCES public.workflow_statuses(id);


--
-- TOC entry 3593 (class 2606 OID 307331)
-- Name: grant_specific_sections fklvg23wp6dijbxa3v3kjdu9nfx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_sections
    ADD CONSTRAINT fklvg23wp6dijbxa3v3kjdu9nfx FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3587 (class 2606 OID 307336)
-- Name: grant_qualitative_kpi_data fkn34tany3kjr076gbk9otq63y; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_qualitative_kpi_data
    ADD CONSTRAINT fkn34tany3kjr076gbk9otq63y FOREIGN KEY (submission_id) REFERENCES public.submissions(id);


--
-- TOC entry 3576 (class 2606 OID 307341)
-- Name: grant_document_attributes fknuwf3wvq93wu1phuao51v1dje; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_attributes
    ADD CONSTRAINT fknuwf3wvq93wu1phuao51v1dje FOREIGN KEY (section_attribute_id) REFERENCES public.grant_specific_section_attributes(id);


--
-- TOC entry 3592 (class 2606 OID 307346)
-- Name: grant_specific_section_attributes fkog7mxdfqa5ngcywqhqj7yub4v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_specific_section_attributes
    ADD CONSTRAINT fkog7mxdfqa5ngcywqhqj7yub4v FOREIGN KEY (section_id) REFERENCES public.grant_specific_sections(id);


--
-- TOC entry 3633 (class 2606 OID 307351)
-- Name: submissions fkps9tdguurx5s3f5hc8j5s3bwl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT fkps9tdguurx5s3f5hc8j5s3bwl FOREIGN KEY (grant_id) REFERENCES public.grants(id);


--
-- TOC entry 3642 (class 2606 OID 307356)
-- Name: workflow_status_transitions fkpu2gecbcrsvf2ofw7uaye2nme; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_status_transitions
    ADD CONSTRAINT fkpu2gecbcrsvf2ofw7uaye2nme FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 3616 (class 2606 OID 307361)
-- Name: qualitative_kpi_notes fkq01w3ym97a2c2sjwbssxc7n6p; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qualitative_kpi_notes
    ADD CONSTRAINT fkq01w3ym97a2c2sjwbssxc7n6p FOREIGN KEY (kpi_data_id) REFERENCES public.grant_qualitative_kpi_data(id);


--
-- TOC entry 3577 (class 2606 OID 307366)
-- Name: grant_document_attributes fkqe5w0pys59b28wcs78qkvgq6i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_attributes
    ADD CONSTRAINT fkqe5w0pys59b28wcs78qkvgq6i FOREIGN KEY (grant_id) REFERENCES public.grants(id);


--
-- TOC entry 3628 (class 2606 OID 307371)
-- Name: roles fkqjj9a6xa11cu9ch24cjo4a7lc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT fkqjj9a6xa11cu9ch24cjo4a7lc FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- TOC entry 3598 (class 2606 OID 307376)
-- Name: users fkqpugllwvyv37klq7ft9m8aqxk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkqpugllwvyv37klq7ft9m8aqxk FOREIGN KEY (organization_id) REFERENCES public.organizations(id);


--
-- TOC entry 3573 (class 2606 OID 307381)
-- Name: doc_kpi_data_document fkqq0q2blu7i17tf5cvbbwn2tga; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doc_kpi_data_document
    ADD CONSTRAINT fkqq0q2blu7i17tf5cvbbwn2tga FOREIGN KEY (doc_kpi_data_id) REFERENCES public.grant_document_kpi_data(id);


--
-- TOC entry 3589 (class 2606 OID 307386)
-- Name: grant_quantitative_kpi_data fkqug9yc5krkhldtu4mn6b1yjys; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_quantitative_kpi_data
    ADD CONSTRAINT fkqug9yc5krkhldtu4mn6b1yjys FOREIGN KEY (grant_kpi_id) REFERENCES public.grant_kpis(id);


--
-- TOC entry 3596 (class 2606 OID 307391)
-- Name: grant_string_attributes fkqwys16hap4lvhe2uucqhsvmo7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_string_attributes
    ADD CONSTRAINT fkqwys16hap4lvhe2uucqhsvmo7 FOREIGN KEY (section_id) REFERENCES public.grant_specific_sections(id);


--
-- TOC entry 3614 (class 2606 OID 307396)
-- Name: qual_kpi_data_document fkr8y48i4gsqpjk9984traqneg8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qual_kpi_data_document
    ADD CONSTRAINT fkr8y48i4gsqpjk9984traqneg8 FOREIGN KEY (qual_kpi_data_id) REFERENCES public.grant_qualitative_kpi_data(id);


--
-- TOC entry 3627 (class 2606 OID 307401)
-- Name: rfps fks7co3jiv2plm3i63rrk2kht4a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rfps
    ADD CONSTRAINT fks7co3jiv2plm3i63rrk2kht4a FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3578 (class 2606 OID 307406)
-- Name: grant_document_attributes fksidslnyudy16lf7tkrrxlb75i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grant_document_attributes
    ADD CONSTRAINT fksidslnyudy16lf7tkrrxlb75i FOREIGN KEY (section_id) REFERENCES public.grant_specific_sections(id);


--
-- TOC entry 3638 (class 2606 OID 307411)
-- Name: workflow_state_permissions fksnxqpq7xuci1fd5muwwl75pss; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workflow_state_permissions
    ADD CONSTRAINT fksnxqpq7xuci1fd5muwwl75pss FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 3605 (class 2606 OID 307416)
-- Name: granter_grant_section_attributes fkt0fnvb7pgysqnrlb92r2pafxh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granter_grant_section_attributes
    ADD CONSTRAINT fkt0fnvb7pgysqnrlb92r2pafxh FOREIGN KEY (granter_id) REFERENCES public.granters(id);


--
-- TOC entry 3807 (class 0 OID 306604)
-- Dependencies: 233 3886
-- Name: granter_active_users; Type: MATERIALIZED VIEW DATA; Schema: public; Owner: -
--

REFRESH MATERIALIZED VIEW public.granter_active_users;


--
-- TOC entry 3810 (class 0 OID 306621)
-- Dependencies: 236 3886
-- Name: granter_count_and_amount_totals; Type: MATERIALIZED VIEW DATA; Schema: public; Owner: -
--

REFRESH MATERIALIZED VIEW public.granter_count_and_amount_totals;


--
-- TOC entry 3817 (class 0 OID 306649)
-- Dependencies: 243 3886
-- Name: granter_grantees; Type: MATERIALIZED VIEW DATA; Schema: public; Owner: -
--

REFRESH MATERIALIZED VIEW public.granter_grantees;


-- Completed on 2020-05-06 19:19:59 IST

--
-- PostgreSQL database dump complete
--

