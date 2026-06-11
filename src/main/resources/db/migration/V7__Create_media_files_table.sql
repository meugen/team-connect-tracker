CREATE TABLE public.media_files (
							  id serial primary key,
							  dropbox_path character varying(500) NOT NULL,
							  url text UNIQUE NOT NULL,
							  content_type character varying(100),
							  size bigint,
							  created_at timestamp NOT NULL default now(),
							  updated_at timestamp NOT NULL default now()
);

ALTER TABLE public.users
ADD CONSTRAINT uq_users_avatar UNIQUE (avatar);