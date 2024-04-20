INSERT INTO GENRE(ID, NAME) VALUES (1, 'fiction');
INSERT INTO GENRE(ID, NAME) VALUES (2, 'novel');
INSERT INTO GENRE(ID, NAME) VALUES (3, 'mystery');

INSERT INTO AUTHOR(ID, NAME, SURNAME) VALUES (nextval('public.author_id_seq'), 'Stephen', 'King');
INSERT INTO AUTHOR(ID, NAME, SURNAME) VALUES (nextval('public.author_id_seq'), 'Nicolas', 'Sparks');
INSERT INTO AUTHOR(ID, NAME, SURNAME) VALUES (nextval('public.author_id_seq'), 'Dan', 'Brown');
INSERT INTO AUTHOR(ID, NAME, SURNAME) VALUES (nextval('public.author_id_seq'), 'Lewis', null);
