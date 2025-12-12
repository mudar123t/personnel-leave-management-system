/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personnel.leave.management.system.model;


import java.util.Date;

public class EmployeeDepartmentHistory {

    private int empDeptHistId;
    private int employeeId;
    private int departmentId;
    private Date startDate;
    private Date endDate; // null = current department

    public EmployeeDepartmentHistory() {}

    public EmployeeDepartmentHistory(int empDeptHistId, int employeeId,
                                     int departmentId, Date startDate, Date endDate) {
        this.empDeptHistId = empDeptHistId;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getEmpDeptHistId() { return empDeptHistId; }
    public void setEmpDeptHistId(int empDeptHistId) { this.empDeptHistId = empDeptHistId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}
