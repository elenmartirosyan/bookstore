DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

--
-- Name: author; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.author
(
    id      bigint                 NOT NULL,
    name    character varying(256) NOT NULL,
    surname character varying(256)
);


ALTER TABLE public.author OWNER TO postgres;

--
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.author_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.author_id_seq OWNER TO postgres;

--
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.author_id_seq OWNED BY public.author.id;


--
-- Name: book; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book
(
    id            bigint                                 NOT NULL,
    title         character varying(256)                 NOT NULL,
    description   character varying,
    price         double precision,
    creation_date timestamp with time zone DEFAULT now() NOT NULL,
    year          integer
);


ALTER TABLE public.book OWNER TO postgres;

--
-- Name: book_author; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book_author
(
    book_id   bigint NOT NULL,
    author_id bigint NOT NULL
);


ALTER TABLE public.book_author OWNER TO postgres;

--
-- Name: book_genre; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book_genre
(
    book_id  bigint  NOT NULL,
    genre_id integer NOT NULL
);


ALTER TABLE public.book_genre OWNER TO postgres;

--
-- Name: book_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.book_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.book_id_seq OWNER TO postgres;

--
-- Name: book_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;


--
-- Name: genre; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genre
(
    id   integer                NOT NULL,
    name character varying(200) NOT NULL
);


ALTER TABLE public.genre OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users
(
    id       integer           NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    role     character varying NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;


--
-- Name: author pk_author; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.author
    ADD CONSTRAINT pk_author PRIMARY KEY (id);


--
-- Name: book pk_book; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book
    ADD CONSTRAINT pk_book PRIMARY KEY (id);


--
-- Name: genre pk_genre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genre
    ADD CONSTRAINT pk_genre PRIMARY KEY (id);


--
-- Name: users pk_users; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


--
-- Name: users unique_username; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- Name: fki_fk_book_author_book; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_fk_book_author_book ON public.book_author USING btree (book_id);


--
-- Name: fki_fk_bookauthor_author; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_fk_bookauthor_author ON public.book_author USING btree (author_id);


--
-- Name: fki_fk_bookgenre_book; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_fk_bookgenre_book ON public.book_genre USING btree (book_id);


--
-- Name: fki_fk_bookgenre_genre; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fki_fk_bookgenre_genre ON public.book_genre USING btree (genre_id);


--
-- Name: idx_title; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_title ON public.book USING btree (title);


--
-- Name: idx_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_username ON public.users USING btree (username);


--
-- Name: book_author fk_bookauthor_author; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fk_bookauthor_author FOREIGN KEY (author_id) REFERENCES public.author(id) NOT VALID;


--
-- Name: book_author fk_bookauthor_book; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_author
    ADD CONSTRAINT fk_bookauthor_book FOREIGN KEY (book_id) REFERENCES public.book(id) NOT VALID;


--
-- Name: book_genre fk_bookgenre_book; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_genre
    ADD CONSTRAINT fk_bookgenre_book FOREIGN KEY (book_id) REFERENCES public.book(id) NOT VALID;


--
-- Name: book_genre fk_bookgenre_genre; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book_genre
    ADD CONSTRAINT fk_bookgenre_genre FOREIGN KEY (genre_id) REFERENCES public.genre(id) NOT VALID;--