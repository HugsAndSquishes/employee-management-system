# employee-management-system

This project uses MAVEN to organize dependencies. It uses Flyway to manage database organization.

I want to note that I use Optional<T> alot to avoid null errors

1. First, you might have to adjust the url, user, name, and password values in the following files:

   - config.properties
   - test_config.properties

2. In the terminal, type
   mvn clean install

3. Run the following file normally (Right click and select Run Java):
   employee-management-system/src/main/java/com/group02/App.java

Useful Commands:

mvn flyway:clean
This deletes all tables, data, and the schema history Flyway tracks. It will only wipe the database
configured in config.properties.
