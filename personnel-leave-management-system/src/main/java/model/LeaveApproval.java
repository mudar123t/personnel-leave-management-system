/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


import java.util.Date;

public class LeaveApproval {

    private int leaveApprovalId;
    private int leaveRequestId;
    private int approverEmployeeId;
    private String action;   // Approved, Rejected
    private Date actionDate;
    private String comment;  // text

    public LeaveApproval() {}

    public LeaveApproval(int leaveApprovalId, int leaveRequestId, int approverEmployeeId,
                         String action, Date actionDate, String comment) {
        this.leaveApprovalId = leaveApprovalId;
        this.leaveRequestId = leaveRequestId;
        this.approverEmployeeId = approverEmployeeId;
        this.action = action;
        this.actionDate = actionDate;
        this.comment = comment;
    }

    public int getLeaveApprovalId() { return leaveApprovalId; }
    public void setLeaveApprovalId(int leaveApprovalId) { this.leaveApprovalId = leaveApprovalId; }

    public int getLeaveRequestId() { return leaveRequestId; }
    public void setLeaveRequestId(int leaveRequestId) { this.leaveRequestId = leaveRequestId; }

    public int getApproverEmployeeId() { return approverEmployeeId; }
    public void setApproverEmployeeId(int approverEmployeeId) { this.approverEmployeeId = approverEmployeeId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Date getActionDate() { return actionDate; }
    public void setActionDate(Date actionDate) { this.actionDate = actionDate; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
