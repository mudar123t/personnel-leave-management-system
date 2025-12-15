package dao;

import model.UserAccount;
import util.DBConnection;
import model.AuthenticatedUser;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDAO {

    private UserAccount mapRow(ResultSet rs) throws SQLException {
        UserAccount u = new UserAccount();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setEmployeeId(rs.getInt("employee_id"));
        u.setRoleId(rs.getInt("role_id"));
        u.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("last_login");
        u.setLastLogin(ts == null ? null : new java.util.Date(ts.getTime()));
        return u;
    }

    public List<UserAccount> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.UserAccount ORDER BY user_id";
        List<UserAccount> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public UserAccount getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.UserAccount WHERE user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public UserAccount getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM dbo.UserAccount WHERE username=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(UserAccount u) throws SQLException {
        String sql = "INSERT INTO dbo.UserAccount(username, password_hash, employee_id, role_id, is_active, last_login) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getEmployeeId());
            ps.setInt(4, u.getRoleId());
            ps.setBoolean(5, u.isActive());

            if (u.getLastLogin() == null) {
                ps.setNull(6, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, new Timestamp(u.getLastLogin().getTime()));
            }

            ps.executeUpdate();
        }
    }

    public void update(UserAccount u) throws SQLException {
        String sql = "UPDATE dbo.UserAccount SET username=?, password_hash=?, employee_id=?, role_id=?, is_active=?, last_login=? "
                + "WHERE user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getEmployeeId());
            ps.setInt(4, u.getRoleId());
            ps.setBoolean(5, u.isActive());

            if (u.getLastLogin() == null) {
                ps.setNull(6, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, new Timestamp(u.getLastLogin().getTime()));
            }

            ps.setInt(7, u.getUserId());
            ps.executeUpdate();
        }
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE dbo.UserAccount SET last_login = SYSUTCDATETIME() WHERE user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.UserAccount WHERE user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

public AuthenticatedUser authenticate(String username, String passwordPlain) throws SQLException {

    String passwordHash = PasswordUtil.hash(passwordPlain);

    String sql =
        "SELECT ua.user_id, ua.username, ua.employee_id, ua.role_id, r.role_name " +
        "FROM dbo.UserAccount ua " +
        "JOIN dbo.Role r ON r.role_id = ua.role_id " +
        "WHERE ua.username=? AND ua.password_hash=? AND ua.is_active=1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, passwordHash);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return null;

            AuthenticatedUser au = new AuthenticatedUser();
            au.setUserId(rs.getInt("user_id"));
            au.setUsername(rs.getString("username"));

            int empId = rs.getInt("employee_id");
            au.setEmployeeId(rs.wasNull() ? null : empId);

            au.setRoleId(rs.getInt("role_id"));
            au.setRoleName(rs.getString("role_name"));

            updateLastLogin(au.getUserId());

            return au;
        }
    }
    
    
}

public Integer getUserIdByEmployee(int employeeId) throws SQLException {
    String sql = "SELECT user_id FROM dbo.UserAccount WHERE employee_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, employeeId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return null;
        }
    }
}

public void updateRole(int userId, int roleId) throws SQLException {
    String sql = "UPDATE dbo.UserAccount SET role_id=? WHERE user_id=?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, roleId);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }
}

// create account if missing (username = national_id, temp password = 1234)
public void createAccountForEmployee(int employeeId, String username, String rawPassword, int roleId) throws SQLException {
    String sql = "INSERT INTO dbo.UserAccount(username, password_hash, employee_id, role_id, is_active) VALUES (?, ?, ?, ?, 1)";
    String hash = util.PasswordUtil.hash(rawPassword);

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, hash);
        ps.setInt(3, employeeId);
        ps.setInt(4, roleId);

        ps.executeUpdate();
    }
}

public static class EmployeeAccountRow {
    public int employeeId;
    public String fullName;
    public String nationalId;

    public Integer userId;       // null if no account
    public String username;      // treat as email/login
    public Integer roleId;
    public String roleName;
    public boolean active;
}

public java.util.List<EmployeeAccountRow> getEmployeesWithAccounts() throws SQLException {
    String sql =
        "SELECT e.employee_id, (e.first_name + ' ' + e.last_name) AS full_name, e.national_id, " +
        "       ua.user_id, ua.username, ua.role_id, r.role_name, ua.is_active " +
        "FROM dbo.Employee e " +
        "LEFT JOIN dbo.UserAccount ua ON ua.employee_id = e.employee_id " +
        "LEFT JOIN dbo.Role r ON r.role_id = ua.role_id " +
        "ORDER BY e.employee_id DESC";

    java.util.List<EmployeeAccountRow> list = new java.util.ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            EmployeeAccountRow row = new EmployeeAccountRow();
            row.employeeId = rs.getInt("employee_id");
            row.fullName = rs.getString("full_name");
            row.nationalId = rs.getString("national_id");

            int uid = rs.getInt("user_id");
            row.userId = rs.wasNull() ? null : uid;

            row.username = rs.getString("username");

            int rid = rs.getInt("role_id");
            row.roleId = rs.wasNull() ? null : rid;

            row.roleName = rs.getString("role_name");
            row.active = rs.getBoolean("is_active");

            list.add(row);
        }
    }
    return list;
}

public boolean usernameExistsForOther(String username, Integer currentUserId) throws SQLException {
    String sql = "SELECT 1 FROM dbo.UserAccount WHERE username=? AND (? IS NULL OR user_id<>?)";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, username);
        if (currentUserId == null) {
            ps.setNull(2, Types.INTEGER);
            ps.setNull(3, Types.INTEGER);
        } else {
            ps.setInt(2, currentUserId);
            ps.setInt(3, currentUserId);
        }
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}

public void createAccount(int employeeId, String username, String rawPassword, int roleId, boolean active) throws SQLException {
    String sql =
        "INSERT INTO dbo.UserAccount(username, password_hash, employee_id, role_id, is_active) " +
        "VALUES (?, ?, ?, ?, ?)";

    String hash = util.PasswordUtil.hash(rawPassword);

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, hash);
        ps.setInt(3, employeeId);
        ps.setInt(4, roleId);
        ps.setBoolean(5, active);

        ps.executeUpdate();
    }
}

public void updateAccount(int userId, String username, Integer roleId, boolean active) throws SQLException {
    String sql =
        "UPDATE dbo.UserAccount SET username=?, role_id=?, is_active=? WHERE user_id=?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setInt(2, roleId);
        ps.setBoolean(3, active);
        ps.setInt(4, userId);

        ps.executeUpdate();
    }
}

public void resetPassword(int userId, String rawPassword) throws SQLException {
    String sql = "UPDATE dbo.UserAccount SET password_hash=? WHERE user_id=?";
    String hash = util.PasswordUtil.hash(rawPassword);

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, hash);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }
}


}


