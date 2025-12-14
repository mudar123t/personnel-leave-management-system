/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class LeaveType {

    private int leaveTypeId;
    private String name;
    private String description;
    private boolean isPaid;

    public LeaveType() {}

    public LeaveType(int leaveTypeId, String name, String description, boolean isPaid) {
        this.leaveTypeId = leaveTypeId;
        this.name = name;
        this.description = description;
        this.isPaid = isPaid;
    }

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    @Override
    public String toString() {
        return name;
    }
}

