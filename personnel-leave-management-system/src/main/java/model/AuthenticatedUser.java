package model;
public class AuthenticatedUser {
    

    private int userId;
    private Integer employeeId;
    private int roleId;
    private String roleName;
    private String username;

    public AuthenticatedUser() {}

    public int getUserId() { return userId; }
    public Integer getEmployeeId() { return employeeId; }
    public int getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public String getUsername() { return username; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setUsername(String username) { this.username = username; }

    // convenience
    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(roleName);
    }

    public boolean isHR() {
        return "HR".equalsIgnoreCase(roleName);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equalsIgnoreCase(roleName);
    }
}
