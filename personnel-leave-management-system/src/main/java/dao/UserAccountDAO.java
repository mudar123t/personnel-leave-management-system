package dao;

import model.UserAccount;
import util.DBConnection;

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
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public UserAccount getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.UserAccount WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public UserAccount getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM dbo.UserAccount WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(UserAccount u) throws SQLException {
        String sql = "INSERT INTO dbo.UserAccount(username, password_hash, employee_id, role_id, is_active, last_login) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getEmployeeId());
            ps.setInt(4, u.getRoleId());
            ps.setBoolean(5, u.isActive());

            if (u.getLastLogin() == null) ps.setNull(6, Types.TIMESTAMP);
            else ps.setTimestamp(6, new Timestamp(u.getLastLogin().getTime()));

            ps.executeUpdate();
        }
    }

    public void update(UserAccount u) throws SQLException {
        String sql = "UPDATE dbo.UserAccount SET username=?, password_hash=?, employee_id=?, role_id=?, is_active=?, last_login=? " +
                     "WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getEmployeeId());
            ps.setInt(4, u.getRoleId());
            ps.setBoolean(5, u.isActive());

            if (u.getLastLogin() == null) ps.setNull(6, Types.TIMESTAMP);
            else ps.setTimestamp(6, new Timestamp(u.getLastLogin().getTime()));

            ps.setInt(7, u.getUserId());
            ps.executeUpdate();
        }
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE dbo.UserAccount SET last_login = SYSUTCDATETIME() WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.UserAccount WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
