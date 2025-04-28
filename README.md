# employee-management-system

This project uses MAVEN to organize dependencies. It uses Flyway to manage database organization.

I use `Optional<T>` extensively to avoid null errors.

---

## Configuration

1. Adjust the DB connection settings in:
   - `src/main/resources/config.properties`
   - `src/test/resources/config.properties`

---

## Build

```bash
mvn clean install
```

---

## Running

By default the **console UI** will launch. If you’d rather run the JavaFX GUI, open `src/main/java/com/group02/App.java` and:

1. **Comment out** the console UI line:
   ```java
   // new ConsoleUI(new EmployeeServiceImpl()).run();
   ```
2. **Uncomment** the JavaFX launcher line:
   ```java
   Application.launch(EmployeeApp.class, args);
   ```

Then you can start the app in one of two ways:

### Console UI

```bash
mvn clean compile exec:java
```

### JavaFX GUI

```bash
mvn clean javafx:run
```

---

## Useful Commands

- **Clean all Flyway migrations and data**  
  ```bash
  mvn flyway:clean
  ```  
  Deletes all tables, data, and the Flyway schema history in the database configured in `config.properties`.

---
