INSERT INTO GENRE(ID, NAME)
VALUES (1, 'fiction');
INSERT INTO GENRE(ID, NAME)
VALUES (2, 'novel');
INSERT INTO GENRE(ID, NAME)
VALUES (3, 'mystery');

INSERT INTO AUTHOR(ID, NAME, SURNAME)
VALUES (nextval('public.author_id_seq'), 'Stephen', 'King');
INSERT INTO AUTHOR(ID, NAME, SURNAME)
VALUES (nextval('public.author_id_seq'), 'Nicolas', 'Sparks');
INSERT INTO AUTHOR(ID, NAME, SURNAME)
VALUES (nextval('public.author_id_seq'), 'Dan', 'Brown');
INSERT INTO AUTHOR(ID, NAME, SURNAME)
VALUES (nextval('public.author_id_seq'), 'Lewis', null);

INSERT INTO BOOK(ID, TITLE, DESCRIPTION, PRICE, YEAR, CREATION_DATE)
VALUES (nextval('public.book_id_seq'), 'The Da Vinci Code', null, 98.9, 2003, NOW());
INSERT INTO BOOK_GENRE(BOOK_ID, GENRE_ID) VALUES (1, 2),(1, 3);
INSERT INTO BOOK_AUTHOR(BOOK_ID, AUTHOR_ID)VALUES (1, 3);

INSERT INTO BOOK(ID, TITLE, DESCRIPTION, PRICE, YEAR, CREATION_DATE)
VALUES (nextval('public.book_id_seq'), 'It', null, 95.9, 1986, NOW());
INSERT INTO BOOK_GENRE(BOOK_ID, GENRE_ID) VALUES (2, 2);
INSERT INTO BOOK_AUTHOR(BOOK_ID, AUTHOR_ID) VALUES (2, 1);

INSERT INTO BOOK(ID, TITLE, DESCRIPTION, PRICE, YEAR, CREATION_DATE)
VALUES (nextval('public.book_id_seq'), 'unknown', null, null, null, NOW());

INSERT INTO USERS(ID, USERNAME, PASSWORD, ROLE) VALUES(1, 'admin' ,'$2y$10$588re1Ynf1OkVjsoyDbKMOAifc0txhx7omh87jDuf8BirWlnmOoGi', 'ADMIN');
INSERT INTO USERS(ID, USERNAME, PASSWORD, ROLE) VALUES(2, 'user', '$2y$10$thvBolpbhUcdVqbU0/rDN.RWYrO1zz4c9fqD0fzKM8.8wKyq9ElCS', 'USER')

