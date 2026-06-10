--
-- PostgreSQL database dump
--

\restrict NTOHZ0JHVHm671VqlFWvg58c7nHjbrVeFWgaA48cjzXb8z5BYYWzAJzUde6VmvE

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: eventos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventos (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone NOT NULL,
    tipo character varying(255),
    sessao_id uuid,
    CONSTRAINT eventos_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['NENHUM'::character varying, 'ALERTA_ENTRADA'::character varying, 'ALERTA_SAIDA'::character varying, 'CRITICO_ENTRADA'::character varying, 'CRITICO_SAIDA'::character varying, 'ERRO_SENSOR_ENTRADA'::character varying, 'ERRO_SENSOR_SAIDA'::character varying])::text[])))
);


ALTER TABLE public.eventos OWNER TO postgres;

--
-- Name: sessao; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sessao (
    id uuid NOT NULL,
    duracao_segundos bigint,
    estado_forno_final character varying(255),
    fim_sessao timestamp(6) without time zone,
    inicio_sessao timestamp(6) without time zone NOT NULL,
    estado_sistema character varying(255),
    CONSTRAINT sessao_estado_forno_final_check CHECK (((estado_forno_final)::text = ANY ((ARRAY['FORNO_DESLIGADO'::character varying, 'FORNO_AQUECENDO'::character varying, 'FORNO_ATIVO'::character varying, 'FORNO_ESFRIANDO'::character varying])::text[]))),
    CONSTRAINT sessao_estado_sistema_check CHECK (((estado_sistema)::text = ANY ((ARRAY['INICIANDO'::character varying, 'SEGURO'::character varying, 'ALERTA'::character varying, 'CRITICO'::character varying, 'ERRO_SENSOR'::character varying])::text[])))
);


ALTER TABLE public.sessao OWNER TO postgres;

--
-- Name: telemetrias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.telemetrias (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone,
    estado_forno character varying(255),
    estado_sistema character varying(255),
    temperatura_atual double precision NOT NULL,
    temperatura_ultima double precision NOT NULL,
    tempo_ligado_minutos bigint,
    CONSTRAINT telemetrias_estado_forno_check CHECK (((estado_forno)::text = ANY ((ARRAY['FORNO_DESLIGADO'::character varying, 'FORNO_AQUECENDO'::character varying, 'FORNO_ATIVO'::character varying, 'FORNO_ESFRIANDO'::character varying])::text[]))),
    CONSTRAINT telemetrias_estado_sistema_check CHECK (((estado_sistema)::text = ANY ((ARRAY['INICIANDO'::character varying, 'SEGURO'::character varying, 'ALERTA'::character varying, 'CRITICO'::character varying, 'ERRO_SENSOR'::character varying])::text[])))
);


ALTER TABLE public.telemetrias OWNER TO postgres;

--
-- Name: temperatura; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.temperatura (
    id uuid NOT NULL,
    registrado_em timestamp(6) without time zone NOT NULL,
    temperatura_atual double precision NOT NULL,
    temperatura_ultima double precision NOT NULL,
    sessao_id uuid
);


ALTER TABLE public.temperatura OWNER TO postgres;

--
-- Name: temporizadores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.temporizadores (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone NOT NULL,
    executado boolean NOT NULL,
    horario_fim timestamp(6) without time zone NOT NULL,
    usuario_id uuid
);


ALTER TABLE public.temporizadores OWNER TO postgres;

--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id uuid NOT NULL,
    email character varying(255) NOT NULL,
    nascimento date NOT NULL,
    nome character varying(255) NOT NULL,
    senha character varying(255) NOT NULL
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- Data for Name: eventos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eventos (id, criado_em, tipo, sessao_id) FROM stdin;
63d26c56-0b94-4a84-badc-e91dad1bff30	2026-06-02 15:24:52.983302	CRITICO_SAIDA	\N
0b1fe120-6a88-4cdf-9688-6d17de30730a	2026-06-09 14:18:31.118317	ALERTA_ENTRADA	dad7870f-9afe-44be-887e-08c373559a6e
2fc31cc7-cadf-4c0e-88a2-81079c67787e	2026-06-09 14:32:22.537326	ALERTA_ENTRADA	494795a1-7685-42c3-b510-c1a004d9e9d6
\.


--
-- Data for Name: sessao; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sessao (id, duracao_segundos, estado_forno_final, fim_sessao, inicio_sessao, estado_sistema) FROM stdin;
35156a9c-f548-4294-a686-dcafe1e13a8f	42	\N	2026-06-01 16:04:18.266751	2026-06-01 16:03:35.592026	\N
bca5fe38-09d6-494b-871e-a666b222b71a	172	\N	2026-06-01 16:05:06.853633	2026-06-01 16:02:14.721588	\N
95f3b520-8e3d-439b-9014-01490e004226	\N	\N	\N	2026-06-03 13:32:13.050066	\N
f83e3c5a-c6fa-4bdb-9226-e949e8c28a5d	\N	\N	\N	2026-06-09 14:17:38.000523	\N
dad7870f-9afe-44be-887e-08c373559a6e	\N	\N	\N	2026-06-09 14:18:11.952718	\N
65a6f518-003e-4b47-be31-2db789b24ad5	\N	\N	\N	2026-06-09 14:19:30.860429	\N
0933acf3-dedf-4ba6-9f98-50a4bd946500	\N	\N	\N	2026-06-09 14:28:03.714634	\N
6043de5b-e7bc-4a65-9e90-96d5cf4f9fc5	\N	\N	\N	2026-06-09 14:28:16.400825	\N
9e1b6324-35a3-4e5d-87bf-5fdf83c9e97d	\N	\N	\N	2026-06-09 14:31:51.463901	\N
494795a1-7685-42c3-b510-c1a004d9e9d6	\N	\N	\N	2026-06-09 14:31:57.719059	\N
\.


--
-- Data for Name: telemetrias; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.telemetrias (id, criado_em, estado_forno, estado_sistema, temperatura_atual, temperatura_ultima, tempo_ligado_minutos) FROM stdin;
\.


--
-- Data for Name: temperatura; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.temperatura (id, registrado_em, temperatura_atual, temperatura_ultima, sessao_id) FROM stdin;
02e6edb5-67f9-429d-a8d6-aaaaf05d2eea	2026-06-02 13:55:51.672589	215	200	\N
b8bbde18-a7c5-4996-af91-876bf566e3f0	2026-06-02 14:26:30.146438	215	200	\N
\.


--
-- Data for Name: temporizadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.temporizadores (id, criado_em, executado, horario_fim, usuario_id) FROM stdin;
c3ef5e5e-1d5e-4903-a984-907321961425	2026-06-03 16:43:40.14898	f	2026-06-03 18:00:00	2c66fecb-0363-421f-9544-93aaaf75f358
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuarios (id, email, nascimento, nome, senha) FROM stdin;
2c66fecb-0363-421f-9544-93aaaf75f358	rafa@gmail.com	2009-02-13	Rafael	$2a$10$Kt8/8PLav6Poa7bhujdShe0EcoOcaT..85MQUzGc2uh6vJAzwEJve
d04da8fc-9c8d-46db-9499-f581d0787597	rafinhafiorioofc@gmail.com	2005-01-01	Rafael	$2a$10$ubN2XRtdn0yVAF3uyLl.hOV3sknUO29Nw04.5yiPtka6BGOy.jJ3C
60cca86c-9fc1-4dab-9f53-32cd641058ce	rafael@email.com	2000-01-01	Rafael	$2a$10$mBgxQcFlEdOkTtKxFHEgzeGZgaBc2z1vPfVHe.K3F8JjPaL9PDMsK
\.


--
-- Name: eventos eventos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventos
    ADD CONSTRAINT eventos_pkey PRIMARY KEY (id);


--
-- Name: sessao sessao_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sessao
    ADD CONSTRAINT sessao_pkey PRIMARY KEY (id);


--
-- Name: telemetrias telemetrias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.telemetrias
    ADD CONSTRAINT telemetrias_pkey PRIMARY KEY (id);


--
-- Name: temperatura temperatura_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.temperatura
    ADD CONSTRAINT temperatura_pkey PRIMARY KEY (id);


--
-- Name: temporizadores temporizadores_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.temporizadores
    ADD CONSTRAINT temporizadores_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: temperatura fkk3rgqj0py7n8ojcwon4cwkbiu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.temperatura
    ADD CONSTRAINT fkk3rgqj0py7n8ojcwon4cwkbiu FOREIGN KEY (sessao_id) REFERENCES public.sessao(id);


--
-- Name: eventos fkl8vtys26bofgxxqrjkxd0q4g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventos
    ADD CONSTRAINT fkl8vtys26bofgxxqrjkxd0q4g FOREIGN KEY (sessao_id) REFERENCES public.sessao(id);


--
-- Name: temporizadores fkokcwt25rrknalsx1bxtfwy4ix; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.temporizadores
    ADD CONSTRAINT fkokcwt25rrknalsx1bxtfwy4ix FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- PostgreSQL database dump complete
--

\unrestrict NTOHZ0JHVHm671VqlFWvg58c7nHjbrVeFWgaA48cjzXb8z5BYYWzAJzUde6VmvE

