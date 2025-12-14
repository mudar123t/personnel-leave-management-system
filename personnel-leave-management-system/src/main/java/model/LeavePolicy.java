/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class LeavePolicy {

    private int leavePolicyId;
    private int leaveTypeId;
    private int employmentTypeId;
    private int annualQuotaDays;
    private Integer maxConsecutiveDays;
    private Integer minServiceMonthsRequired;

    public LeavePolicy() {}

    public LeavePolicy(int leavePolicyId, int leaveTypeId, int employmentTypeId,
                       int annualQuotaDays, Integer maxConsecutiveDays,
                       Integer minServiceMonthsRequired) {
        this.leavePolicyId = leavePolicyId;
        this.leaveTypeId = leaveTypeId;
        this.employmentTypeId = employmentTypeId;
        this.annualQuotaDays = annualQuotaDays;
        this.maxConsecutiveDays = maxConsecutiveDays;
        this.minServiceMonthsRequired = minServiceMonthsRequired;
    }

    public int getLeavePolicyId() { return leavePolicyId; }
    public void setLeavePolicyId(int leavePolicyId) { this.leavePolicyId = leavePolicyId; }

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public int getEmploymentTypeId() { return employmentTypeId; }
    public void setEmploymentTypeId(int employmentTypeId) { this.employmentTypeId = employmentTypeId; }

    public int getAnnualQuotaDays() { return annualQuotaDays; }
    public void setAnnualQuotaDays(int annualQuotaDays) { this.annualQuotaDays = annualQuotaDays; }

    public Integer getMaxConsecutiveDays() { return maxConsecutiveDays; }
    public void setMaxConsecutiveDays(Integer maxConsecutiveDays) {
        this.maxConsecutiveDays = maxConsecutiveDays;
    }

    public Integer getMinServiceMonthsRequired() { return minServiceMonthsRequired; }
    public void setMinServiceMonthsRequired(Integer minServiceMonthsRequired) {
        this.minServiceMonthsRequired = minServiceMonthsRequired;
    }
}
