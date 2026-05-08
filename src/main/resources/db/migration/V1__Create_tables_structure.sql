
CREATE TABLE public.departments (
                                    id integer NOT NULL,
                                    name character varying(255) NOT NULL,
                                    head_id integer
);


--
-- Name: departments_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.departments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: departments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.departments_id_seq OWNED BY public.departments.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.users (
                              id integer NOT NULL,
                              password text NOT NULL,
                              email character varying(255) NOT NULL,
                              role character varying(50) NOT NULL,
                              status character varying(50) NOT NULL,
                              first_name character varying(100) NOT NULL,
                              last_name character varying(100) NOT NULL,
                              avatar text,
                              phone jsonb NOT NULL,
                              birth_date date NOT NULL,
                              gender character varying(10) NOT NULL,
                              grade character varying(50) NOT NULL
);


--
-- Name: employees_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: employees_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.employees_id_seq OWNED BY public.users.id;


--
-- Name: user_project; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.user_project (
                                     id integer NOT NULL,
                                     user_id integer NOT NULL,
                                     project_id integer NOT NULL,
                                     role character varying(255),
                                     start_date date NOT NULL,
                                     end_date date
);

--
-- Name: employees_projects_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.employees_projects_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: employees_projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.employees_projects_id_seq OWNED BY public.user_project.id;


--
-- Name: niches; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.niches (
                               id integer NOT NULL,
                               name character varying(255) NOT NULL
);


--
-- Name: niches_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.niches_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: niches_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.niches_id_seq OWNED BY public.niches.id;


--
-- Name: positions; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.positions (
                                  id integer NOT NULL,
                                  name character varying(255) NOT NULL,
                                  department_id integer NOT NULL
);


--
-- Name: positions_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.positions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.positions_id_seq OWNED BY public.positions.id;


--
-- Name: project_niche; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.project_niche (
                                      project_id integer NOT NULL,
                                      niche_id integer NOT NULL
);


--
-- Name: projects; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.projects (
                                 id integer NOT NULL,
                                 name character varying(255) NOT NULL,
                                 start_date date NOT NULL,
                                 end_date date,
                                 status character varying(50) NOT NULL,
                                 description text,
                                 isbillable boolean DEFAULT false NOT NULL
);


--
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.projects_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.projects_id_seq OWNED BY public.projects.id;


--
-- Name: stacks; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.stacks (
                               id integer NOT NULL,
                               name character varying(255) NOT NULL
);


--
-- Name: stacks_id_seq; Type: SEQUENCE; Schema: public; Owner: teamConnect
--

CREATE SEQUENCE public.stacks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: stacks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: teamConnect
--

ALTER SEQUENCE public.stacks_id_seq OWNED BY public.stacks.id;


--
-- Name: user_position; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.user_position (
                                      user_id integer NOT NULL,
                                      position_id integer NOT NULL,
                                      start_date date NOT NULL,
                                      end_date date
);


--
-- Name: user_hire_date_view; Type: VIEW; Schema: public; Owner: teamConnect
--

CREATE VIEW public.user_hire_date_view AS
SELECT user_id,
       min(start_date) AS hire_date
FROM public.user_position
GROUP BY user_id;


--
-- Name: user_stack; Type: TABLE; Schema: public; Owner: teamConnect
--

CREATE TABLE public.user_stack (
                                   stack_id integer NOT NULL,
                                   user_id integer NOT NULL,
                                   isprimary boolean NOT NULL
);


--
-- Name: departments id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.departments ALTER COLUMN id SET DEFAULT nextval('public.departments_id_seq'::regclass);


--
-- Name: niches id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.niches ALTER COLUMN id SET DEFAULT nextval('public.niches_id_seq'::regclass);


--
-- Name: positions id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);


--
-- Name: projects id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.projects ALTER COLUMN id SET DEFAULT nextval('public.projects_id_seq'::regclass);


--
-- Name: stacks id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.stacks ALTER COLUMN id SET DEFAULT nextval('public.stacks_id_seq'::regclass);


--
-- Name: user_project id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_project ALTER COLUMN id SET DEFAULT nextval('public.employees_projects_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);


--
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- Name: users employees_email_key; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT employees_email_key UNIQUE (email);


--
-- Name: users employees_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


--
-- Name: user_project employees_projects_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_pkey PRIMARY KEY (id);


--
-- Name: niches niches_name_key; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.niches
    ADD CONSTRAINT niches_name_key UNIQUE (name);


--
-- Name: niches niches_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.niches
    ADD CONSTRAINT niches_pkey PRIMARY KEY (id);


--
-- Name: user_position positions_employees_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_pkey PRIMARY KEY (user_id, position_id, start_date);


--
-- Name: positions positions_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (id);


--
-- Name: projects projects_name_key; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_name_key UNIQUE (name);


--
-- Name: project_niche projects_niches_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_pkey PRIMARY KEY (project_id, niche_id);


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: user_stack stacks_employees_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_pkey PRIMARY KEY (stack_id, user_id);


--
-- Name: stacks stacks_name_key; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.stacks
    ADD CONSTRAINT stacks_name_key UNIQUE (name);


--
-- Name: stacks stacks_pkey; Type: CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.stacks
    ADD CONSTRAINT stacks_pkey PRIMARY KEY (id);


--
-- Name: departments departments_head_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_head_id_fkey FOREIGN KEY (head_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: user_project employees_projects_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_project employees_projects_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: positions positions_department_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id) ON DELETE CASCADE;


--
-- Name: user_position positions_employees_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_position positions_employees_position_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_position_id_fkey FOREIGN KEY (position_id) REFERENCES public.positions(id) ON DELETE CASCADE;


--
-- Name: project_niche projects_niches_niche_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_niche_id_fkey FOREIGN KEY (niche_id) REFERENCES public.niches(id) ON DELETE CASCADE;


--
-- Name: project_niche projects_niches_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: user_stack stacks_employees_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: user_stack stacks_employees_stack_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: teamConnect
--

ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_stack_id_fkey FOREIGN KEY (stack_id) REFERENCES public.stacks(id) ON DELETE CASCADE;



