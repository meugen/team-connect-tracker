CREATE TABLE public.media_files (
							  id bigserial primary key,
							  dropbox_path character varying(500) NOT NULL,
							  url text NOT NULL,
							  content_type character varying(100),
							  size bigint,
							  created_at timestamp NOT NULL default now(),
							  updated_at timestamp NOT NULL default now()
);

ALTER TABLE public.media_files
ADD CONSTRAINT uq_media_files_url UNIQUE (url);

ALTER TABLE public.users
ADD CONSTRAINT uq_users_avatar UNIQUE (avatar);