package model;

public class EmployeeContact {
    private Integer employeeContactId; // nullable for new
    private int employeeId;

    private String phone;
    private String email;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;

    public Integer getEmployeeContactId() { return employeeContactId; }
    public void setEmployeeContactId(Integer employeeContactId) { this.employeeContactId = employeeContactId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
}
