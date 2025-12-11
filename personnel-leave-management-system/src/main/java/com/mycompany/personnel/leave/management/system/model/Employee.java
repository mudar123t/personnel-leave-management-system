package com.mycompany.personnel.leave.management.system.model;

import java.util.Date;

public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String nationalId;
    private Date birthDate;
    private String gender;
    private Date hireDate;
    private double salary;
    private int locationId;
    private int departmentId;
    private int positionId;
    private int employmentTypeId;
    private String status;

    // -------------------------
    // Constructors
    // -------------------------

    public Employee() {
    }

    public Employee(int employeeId, String firstName, String lastName, String nationalId,
                    Date birthDate, String gender, Date hireDate, double salary,
                    int locationId, int departmentId, int positionId,
                    int employmentTypeId, String status) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
        this.birthDate = birthDate;
        this.gender = gender;
        this.hireDate = hireDate;
        this.salary = salary;
        this.locationId = locationId;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.employmentTypeId = employmentTypeId;
        this.status = status;
    }

    // -------------------------
    // Getters and Setters
    // -------------------------

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    //

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(int employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // -------------------------
    // toString
    // -------------------------

    @Override
    public String toString() {
        return firstName + " " + lastName + " (ID: " + employeeId + ")";
    }
}
