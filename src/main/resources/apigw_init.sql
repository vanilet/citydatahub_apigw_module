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

CREATE DATABASE apigw WITH TEMPLATE = template0 ENCODING = 'UTF8'

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

CREATE SCHEMA apigw;

SET default_with_oids = false;

CREATE TABLE apigw.routing_rule (
    routing_rule_id character varying(30) NOT NULL,
    name character varying(50),
    description character varying(500),
    target_service_id character varying(50) NOT NULL,
    use_yn character varying(1),
    rule_order integer,
    creation_time timestamp without time zone,
    modification_time timestamp without time zone,
    creator_id character varying(30),
    modifier_id character varying(30),
    authorization_yn character varying(1) DEFAULT true,
    authorization_role_ids character varying[],
    new_path character varying(100),
    method character varying(30)
);

CREATE TABLE apigw.routing_rule_detail (
    routing_rule_id character varying(30) NOT NULL,
    routing_rule_detail_sequence integer NOT NULL,
    type character varying(3) NOT NULL,
    attribute_name character varying(50),
    attribute_operation character varying(3),
    attribute_value character varying(100),
    logical_operation character varying(3)
);

CREATE TABLE apigw.service (
    service_id character varying(50) NOT NULL,
    name character varying(50),
    description character varying(500),
    communication_method character varying(3),
    address character varying(100) NOT NULL,
    use_yn character varying(1),
    creation_time timestamp without time zone,
    modification_time timestamp without time zone,
    creator_id character varying(30),
    modifier_id character varying(30),
    fallback_path character varying(100)
);

ALTER TABLE ONLY apigw.routing_rule_detail
    ADD CONSTRAINT routing_rule_detail_pkey PRIMARY KEY (routing_rule_id, routing_rule_detail_sequence);

ALTER TABLE ONLY apigw.routing_rule
    ADD CONSTRAINT routing_rule_routing_rule_id_pkey PRIMARY KEY (routing_rule_id);

ALTER TABLE ONLY apigw.service
    ADD CONSTRAINT service_service_id_pkey PRIMARY KEY (service_id);

ALTER TABLE ONLY apigw.routing_rule_detail
    ADD CONSTRAINT routing_rule_fkey FOREIGN KEY (routing_rule_id) REFERENCES apigw.routing_rule(routing_rule_id) ON DELETE CASCADE;

ALTER TABLE ONLY apigw.routing_rule
    ADD CONSTRAINT routing_rule_target_service_id_fkey FOREIGN KEY (target_service_id) REFERENCES apigw.service(service_id);