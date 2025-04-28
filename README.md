# employee-management-system

This project uses MAVEN to organize dependencies. It uses Flyway to manage database organization.

I want to note that I use Optional<T> alot to avoid null errors

1. First, you might have to adjust the url, user, password, and database name values in the following files:

   - src/main/resources/config.properties
   - src/test/resources/config.properties

2. In the terminal, type
   ```bash
   mvn clean install
   ```

3. To run the app, type
   ```bash
   mvn clean compile exec:java
   ```

Useful Commands:

mvn flyway:clean
This deletes all tables, data, and the schema history Flyway tracks. It will only wipe the database
configured in config.properties.
