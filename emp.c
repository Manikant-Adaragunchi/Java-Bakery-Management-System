#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_EMPLOYEES 100

// Structure to store employee data
typedef struct {
    int id;
    char name[50];
    char position[50];
    float salary;
} Employee;

// Global array to store employee records
Employee employees[MAX_EMPLOYEES];
int employee_count = 0;

// Function to add a new employee
void addEmployee() {
    if (employee_count < MAX_EMPLOYEES) {
        Employee emp;

        printf("Enter Employee ID: ");
        scanf("%d", &emp.id);
        getchar(); // Consume the newline character left by scanf
        printf("Enter Employee Name: ");
        fgets(emp.name, sizeof(emp.name), stdin);
        emp.name[strcspn(emp.name, "\n")] = 0;  // Remove trailing newline
        printf("Enter Employee Position: ");
        fgets(emp.position, sizeof(emp.position), stdin);
        emp.position[strcspn(emp.position, "\n")] = 0;  // Remove trailing newline
        printf("Enter Employee Salary: ");
        scanf("%f", &emp.salary);

        employees[employee_count] = emp;
        employee_count++;
        printf("Employee added successfully!\n");
    } else {
        printf("Employee limit reached! Cannot add more employees.\n");
    }
}

// Function to display all employees
void displayEmployees() {
    if (employee_count == 0) {
        printf("No employees to display.\n");
        return;
    }

    printf("\nEmployee List:\n");
    printf("ID\tName\t\tPosition\t\tSalary\n");
    printf("-----------------------------------------------\n");
    for (int i = 0; i < employee_count; i++) {
        printf("%d\t%s\t\t%s\t\t%.2f\n", employees[i].id, employees[i].name, employees[i].position, employees[i].salary);
    }
}

// Function to delete an employee by ID
void deleteEmployee() {
    int id;
    printf("Enter Employee ID to delete: ");
    scanf("%d", &id);

    int found = 0;
    for (int i = 0; i < employee_count; i++) {
        if (employees[i].id == id) {
            // Shift all employees after the deleted one to the left
            for (int j = i; j < employee_count - 1; j++) {
                employees[j] = employees[j + 1];
            }
            employee_count--;
            printf("Employee with ID %d has been deleted.\n", id);
            found = 1;
            break;
        }
    }

    if (!found) {
        printf("Employee with ID %d not found.\n", id);
    }
}

// Main menu
void showMenu() {
    int choice;
    do {
        printf("\nEmployee Management System\n");
        printf("1. Add Employee\n");
        printf("2. Display Employees\n");
        printf("3. Delete Employee\n");
        printf("4. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1:
                addEmployee();
                break;
            case 2:
                displayEmployees();
                break;
            case 3:
                deleteEmployee();
                break;
            case 4:
                printf("Exiting program...\n");
                break;
            default:
                printf("Invalid choice! Please try again.\n");
        }
    } while (choice != 4);
}

int main() {
    showMenu();
    return 0;
}
