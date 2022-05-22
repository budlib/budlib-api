# BudLib REST API

REST API for BudLib - a library management tool

## How to deploy

Check the [wiki](https://github.com/budlib/budlib-api/wiki) for deployment instructions.

## How to build and run

1. The following are required to build and run this project:

   - MySQL server version 8.0.26 or above
   - JDK version 11.0.11 or above
   - Maven version 3.6.3 or above

2. Clone the repository on your machine, or download the zip file

   ```bash
   $ git clone git@github.com:budlib/budlib-api.git
   ```

3. Start the MySQL server on your local machine. If you are accessing the MySQL server over a network, edit the property `spring.datasource.url` in the file `application.properties` and replace `localhost` with the server IP address.

4. Connect to your MySQL server using an admin user like `root`.

5. Run the below scripts on the MySQL server in the given order using `root`. These scripts will create a schema `buddb`, a user `budapp` with password `budpassword`, create all necessary tables and load them with some dummy data.

   1. [01_init.sql](sql/01_init.sql)
   2. [02_tables.sql](sql/02_dummy_data.sql)

6. Running the below command from the repository's root directory will get the backend server running.

   ```bash
   $ mvn clean spring-boot:run
   ```

## Contributors

- [Bhavyai Gupta](https://github.com/zbhavyai)
- [Mike Lee](https://github.com/mikeePy)
