// src/main/java/com/group02/ui/EmployeeApp.java
package com.group02.ui;

import com.group02.model.Employee;
import com.group02.service.EmployeeService;
import com.group02.service.EmployeeServiceImpl;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * JavaFX GUI for the Employee Management System.
 */
public class EmployeeApp extends Application {
    private final EmployeeService service = new EmployeeServiceImpl();
    private final TableView<Employee> table = new TableView<>();
    private final ObservableList<Employee> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Manager (JavaFX)");

        // Table columns
        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("empID"));
        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Employee, String> ssnCol = new TableColumn<>("SSN");
        ssnCol.setCellValueFactory(new PropertyValueFactory<>("SSN"));
        TableColumn<Employee, String> jobCol = new TableColumn<>("Job Title");
        jobCol.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        TableColumn<Employee, String> divCol = new TableColumn<>("Division");
        divCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        TableColumn<Employee, Double> salCol = new TableColumn<>("Salary");
        salCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        table.getColumns().addAll(idCol, nameCol, ssnCol, jobCol, divCol, salCol);
        refreshTable();

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Filter by name...");
        searchField.textProperty().addListener((obs, oldVal, newVal) ->
            data.setAll(service.searchByName(newVal))
        );

        // Buttons
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showEmployeeDialog(null));
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) showEmployeeDialog(sel);
        });
        Button delBtn = new Button("Delete");
        delBtn.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && confirm("Delete " + sel.getName() + "?")) {
                service.delete(sel.getEmpID());
                refreshTable();
            }
        });

        HBox toolbar = new HBox(10, addBtn, editBtn, delBtn);
        toolbar.setPadding(new Insets(10));

        VBox root = new VBox(10, searchField, table, toolbar);
        root.setPadding(new Insets(10));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void refreshTable() {
        data.setAll(service.findAll());
        table.setItems(data);
    }

    private void showEmployeeDialog(Employee employee) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(employee == null ? "Add Employee" : "Edit Employee");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Name");
        TextField ssnField = new TextField();  ssnField.setPromptText("SSN");
        TextField jobField = new TextField();  jobField.setPromptText("Job Title");
        TextField divField = new TextField();  divField.setPromptText("Division");
        TextField salField = new TextField();  salField.setPromptText("Salary");
        TextField payField = new TextField();  payField.setPromptText("Pay Info");

        if (employee != null) {
            nameField.setText(employee.getName());
            ssnField.setText(employee.getSSN());
            jobField.setText(employee.getJobTitle());
            divField.setText(employee.getDivision());
            salField.setText(String.valueOf(employee.getSalary()));
            payField.setText(employee.getPayInfo());
        }

        grid.add(new Label("Name:"), 0, 0);      grid.add(nameField, 1, 0);
        grid.add(new Label("SSN:"), 0, 1);       grid.add(ssnField, 1, 1);
        grid.add(new Label("Job Title:"), 0, 2); grid.add(jobField, 1, 2);
        grid.add(new Label("Division:"), 0, 3);  grid.add(divField, 1, 3);
        grid.add(new Label("Salary:"), 0, 4);    grid.add(salField, 1, 4);
        grid.add(new Label("Pay Info:"), 0, 5);  grid.add(payField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Employee e = employee == null ? new Employee() : employee;
                e.setName(nameField.getText());
                e.setSSN(ssnField.getText());
                e.setJobTitle(jobField.getText());
                e.setDivision(divField.getText());
                try {
                    e.setSalary(Double.parseDouble(salField.getText()));
                } catch (NumberFormatException ex) {
                    e.setSalary(0);
                }
                e.setPayInfo(payField.getText());
                return e;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        result.ifPresent(e -> {
            if (employee == null) service.add(e);
            else service.update(e);
            refreshTable();
        });
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() == ButtonType.YES;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
