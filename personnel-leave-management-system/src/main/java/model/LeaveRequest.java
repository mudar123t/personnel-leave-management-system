/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


import java.util.Date;

public class LeaveRequest {

    private int leaveRequestId;
    private int employeeId;
    private int leaveTypeId;
    private Date requestDate;
    private Date startDate;
    private Date endDate;
    private int totalDays;
    private String status;                 // Pending, Approved, Rejected, Canceled
    private String reason;                 // text
    private Integer approvedByEmployeeId;  // nullable
    private Date decisionDate;             // nullable

    public LeaveRequest() {}

    public LeaveRequest(int leaveRequestId, int employeeId, int leaveTypeId,
                        Date requestDate, Date startDate, Date endDate,
                        int totalDays, String status, String reason,
                        Integer approvedByEmployeeId, Date decisionDate) {
        this.leaveRequestId = leaveRequestId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.requestDate = requestDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.status = status;
        this.reason = reason;
        this.approvedByEmployeeId = approvedByEmployeeId;
        this.decisionDate = decisionDate;
    }

    public int getLeaveRequestId() { return leaveRequestId; }
    public void setLeaveRequestId(int leaveRequestId) { this.leaveRequestId = leaveRequestId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Integer getApprovedByEmployeeId() { return approvedByEmployeeId; }
    public void setApprovedByEmployeeId(Integer approvedByEmployeeId) {
        this.approvedByEmployeeId = approvedByEmployeeId;
    }

    public Date getDecisionDate() { return decisionDate; }
    public void setDecisionDate(Date decisionDate) { this.decisionDate = decisionDate; }
}
