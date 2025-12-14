/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class Department {

    private int departmentId;
    private String name;
    private String code;
    private Integer managerEmployeeId; // nullable

    public Department() {}

    public Department(int departmentId, String name, String code, Integer managerEmployeeId) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.managerEmployeeId = managerEmployeeId;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getManagerEmployeeId() { return managerEmployeeId; }
    public void setManagerEmployeeId(Integer managerEmployeeId) { this.managerEmployeeId = managerEmployeeId; }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}

