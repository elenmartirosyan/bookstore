# Bookstore

## Pre Requisitions
- Java 17+
- PostgreSQL

## How to Run on local machine?
- Create `bookstore` and `bookstore-test` dbs in PostgreSQL.
- To create schema for `bookstore` db run the `src/main/resources/schema.sql` sql script.
- To populate data run `src/main/resources/data.sql` sql script.
- Two users will be created. Here are the credentials:
    - Admin: username: `admin`, password: `admin_pass`
    - User: username: `user`, password: `user_pass`
- Make sure database configs in `application.properties` and `application-test.properties` are correct.
- Run `BookstoreApplication`.
- The application will run on `localhost:8080`.

## Postman Collection
- Import `postman` environment and collection to Postman.