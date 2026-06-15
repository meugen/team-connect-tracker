--
-- Data for Name: media_files; Type: TABLE DATA; Schema: public; Owner: teamConnect
--

INSERT INTO public.media_files
(id, dropbox_path, url, content_type, size)
VALUES
(1, '/avatars/avatar1.jpg', 'https://example.com/avatar1.jpg', 'image/jpeg', 102400),
(2, '/avatars/avatar2.jpg', 'https://example.com/avatar2.jpg', 'image/jpeg', 104500),
(3, '/avatars/avatar3.jpg', 'https://example.com/avatar3.jpg', 'image/jpeg', 101200),
(4, '/avatars/avatar4.jpg', 'https://example.com/avatar4.jpg', 'image/jpeg', 110000),
(5, '/avatars/avatar5.jpg', 'https://example.com/avatar5.jpg', 'image/jpeg', 98000),
(6, '/avatars/avatar6.jpg', 'https://example.com/avatar6.jpg', 'image/jpeg', 105000),
(7, '/avatars/avatar7.jpg', 'https://example.com/avatar7.jpg', 'image/jpeg', 112000),
(8, '/avatars/avatar8.jpg', 'https://example.com/avatar8.jpg', 'image/jpeg', 107000),
(9, '/avatars/avatar9.jpg', 'https://example.com/avatar9.jpg', 'image/jpeg', 109000),
(10, '/avatars/avatar10.jpg', 'https://example.com/avatar10.jpg', 'image/jpeg', 100000),
(11, '/avatars/avatar11.jpg', 'https://example.com/avatar11.jpg', 'image/jpeg', 103000),
(12, '/avatars/avatar12.jpg', 'https://example.com/avatar12.jpg', 'image/jpeg', 111000),
(13, '/avatars/avatar13.jpg', 'https://example.com/avatar13.jpg', 'image/jpeg', 106000),
(14, '/avatars/avatar14.jpg', 'https://example.com/avatar14.jpg', 'image/jpeg', 108000),
(15, '/avatars/avatar15.jpg', 'https://example.com/avatar15.jpg', 'image/jpeg', 99000),
(16, '/avatars/avatar16.jpg', 'https://example.com/avatar16.jpg', 'image/jpeg', 115000),
(17, '/avatars/avatar17.jpg', 'https://example.com/avatar17.jpg', 'image/jpeg', 102000),
(18, '/avatars/avatar18.jpg', 'https://example.com/avatar18.jpg', 'image/jpeg', 104000),
(19, '/avatars/avatar19.jpg', 'https://example.com/avatar19.jpg', 'image/jpeg', 113000),
(20, '/avatars/avatar20.jpg', 'https://example.com/avatar20.jpg', 'image/jpeg', 101000);


--
-- Name: media_files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: teamConnect
--

SELECT pg_catalog.setval('public.media_files_id_seq', (SELECT MAX(id) FROM media_files), true);