/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personnel.leave.management.system.model;


public class EmploymentType {

    private int employmentTypeId;
    private String name;
    private String description;

    public EmploymentType() {}

    public EmploymentType(int employmentTypeId, String name, String description) {
        this.employmentTypeId = employmentTypeId;
        this.name = name;
        this.description = description;
    }

    public int getEmploymentTypeId() { return employmentTypeId; }
    public void setEmploymentTypeId(int employmentTypeId) { this.employmentTypeId = employmentTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}
