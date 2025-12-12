/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personnel.leave.management.system.model;


public class LeaveBalance {

    private int leaveBalanceId;
    private int employeeId;
    private int leaveTypeId;
    private int year;
    private int totalAllocatedDays;
    private int usedDays;
    private int remainingDays;

    public LeaveBalance() {}

    public LeaveBalance(int leaveBalanceId, int employeeId, int leaveTypeId,
                        int year, int totalAllocatedDays,
                        int usedDays, int remainingDays) {
        this.leaveBalanceId = leaveBalanceId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.totalAllocatedDays = totalAllocatedDays;
        this.usedDays = usedDays;
        this.remainingDays = remainingDays;
    }

    public int getLeaveBalanceId() { return leaveBalanceId; }
    public void setLeaveBalanceId(int leaveBalanceId) { this.leaveBalanceId = leaveBalanceId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getTotalAllocatedDays() { return totalAllocatedDays; }
    public void setTotalAllocatedDays(int totalAllocatedDays) {
        this.totalAllocatedDays = totalAllocatedDays;
    }

    public int getUsedDays() { return usedDays; }
    public void setUsedDays(int usedDays) { this.usedDays = usedDays; }

    public int getRemainingDays() { return remainingDays; }
    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
}
