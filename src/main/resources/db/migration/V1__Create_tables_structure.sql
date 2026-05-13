
CREATE TABLE public.departments (
                                    id integer NOT NULL,
                                    name character varying(255) NOT NULL,
                                    head_id integer
);


CREATE SEQUENCE public.departments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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


CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.user_project (
                                     id integer NOT NULL,
                                     user_id integer NOT NULL,
                                     project_id integer NOT NULL,
                                     role character varying(255),
                                     start_date date NOT NULL,
                                     end_date date
);

CREATE SEQUENCE public.employees_projects_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.niches (
                               id integer NOT NULL,
                               name character varying(255) NOT NULL
);


CREATE SEQUENCE public.niches_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.positions (
                                  id integer NOT NULL,
                                  name character varying(255) NOT NULL,
                                  department_id integer NOT NULL
);


CREATE SEQUENCE public.positions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.project_niche (
                                      project_id integer NOT NULL,
                                      niche_id integer NOT NULL
);


CREATE TABLE public.projects (
                                 id integer NOT NULL,
                                 name character varying(255) NOT NULL,
                                 start_date date NOT NULL,
                                 end_date date,
                                 status character varying(50) NOT NULL,
                                 description text,
                                 isbillable boolean DEFAULT false NOT NULL
);


CREATE SEQUENCE public.projects_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.stacks (
                               id integer NOT NULL,
                               name character varying(255) NOT NULL
);


CREATE SEQUENCE public.stacks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE public.user_position (
                                      user_id integer NOT NULL,
                                      position_id integer NOT NULL,
                                      start_date date NOT NULL,
                                      end_date date
);


CREATE TABLE public.user_stack (
                                   stack_id integer NOT NULL,
                                   user_id integer NOT NULL,
                                   isprimary boolean NOT NULL
);


ALTER TABLE ONLY public.niches ALTER COLUMN id SET DEFAULT nextval('public.niches_id_seq'::regclass);


ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);


ALTER TABLE ONLY public.projects ALTER COLUMN id SET DEFAULT nextval('public.projects_id_seq'::regclass);


ALTER TABLE ONLY public.stacks ALTER COLUMN id SET DEFAULT nextval('public.stacks_id_seq'::regclass);


ALTER TABLE ONLY public.user_project ALTER COLUMN id SET DEFAULT nextval('public.employees_projects_id_seq'::regclass);


ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);


ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT employees_email_key UNIQUE (email);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.niches
    ADD CONSTRAINT niches_name_key UNIQUE (name);


ALTER TABLE ONLY public.niches
    ADD CONSTRAINT niches_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_pkey PRIMARY KEY (user_id, position_id, start_date);


ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_name_key UNIQUE (name);


ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_pkey PRIMARY KEY (project_id, niche_id);


ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_pkey PRIMARY KEY (stack_id, user_id);


ALTER TABLE ONLY public.stacks
    ADD CONSTRAINT stacks_name_key UNIQUE (name);


ALTER TABLE ONLY public.stacks
    ADD CONSTRAINT stacks_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_head_id_fkey FOREIGN KEY (head_id) REFERENCES public.users(id) ON DELETE SET NULL;


ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.user_project
    ADD CONSTRAINT employees_projects_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.user_position
    ADD CONSTRAINT positions_employees_position_id_fkey FOREIGN KEY (position_id) REFERENCES public.positions(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_niche_id_fkey FOREIGN KEY (niche_id) REFERENCES public.niches(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.project_niche
    ADD CONSTRAINT projects_niches_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_employee_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


ALTER TABLE ONLY public.user_stack
    ADD CONSTRAINT stacks_employees_stack_id_fkey FOREIGN KEY (stack_id) REFERENCES public.stacks(id) ON DELETE CASCADE;
