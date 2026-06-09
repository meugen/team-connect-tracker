CREATE TABLE public.media_files (
							  id bigserial primary key,
							  dropbox_path character varying(500) NOT NULL,
							  url text UNIQUE NOT NULL,
							  content_type character varying(100),
							  size bigint,
							  created_at timestamp NOT NULL default now(),
							  updated_at timestamp NOT NULL default now(),
							  pending_delete boolean NOT NULL default false,
							  delete_attempts integer NOT NULL default 0
);

ALTER TABLE public.users
ADD CONSTRAINT uq_users_avatar UNIQUE (avatar);