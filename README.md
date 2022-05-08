# BudLib REST API

REST API for BudLib - a library management tool

## Dependencies

- JDK version 11.0.11 or above
- Maven version 3.6.3 or above
- MySQL server version 8.0.26 or above

## How to run

1. Clone the repository on your machine

2. Start the MySQL server on your local machine. If you are accessing the MySQL server over a network, edit the property `spring.datasource.url` in the file `application.properties` and replace `localhost` with the server IP address.

3. Connect to your MySQL server using an admin user like `root`.

4. Run the below scripts on the MySQL server in the given order using `root`. These scripts will create a schema `buddb`, a user `budapp` with password `budpassword`, create all necessary tables and load them with some dummy data.

   1. [01_init.sql](sql/01_init.sql)
   2. [02_tables.sql](sql/02_dummy_data.sql)

5. Running the below command from the repository's root directory will get the backend server running.
   ```bash
   $ mvn clean spring-boot:run
   ```

## Contributors

- [Bhavyai Gupta](https://github.com/zbhavyai)
- [Mike Lee](https://github.com/mikeePy)
