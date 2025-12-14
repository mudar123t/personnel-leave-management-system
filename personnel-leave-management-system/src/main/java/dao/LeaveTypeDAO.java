package dao;

import model.LeaveType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveTypeDAO {

    public List<LeaveType> getAll() throws SQLException {
        String sql = "SELECT leave_type_id, name, description, is_paid FROM dbo.LeaveType ORDER BY name";
        List<LeaveType> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveType t = new LeaveType();
                t.setLeaveTypeId(rs.getInt("leave_type_id"));
                t.setName(rs.getString("name"));
                t.setDescription(rs.getString("description"));
                t.setPaid(rs.getBoolean("is_paid"));
                list.add(t);
            }
        }
        return list;
    }

    public void insert(LeaveType t) throws SQLException {
        String sql = "INSERT INTO dbo.LeaveType (name, description, is_paid) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getName());
            ps.setString(2, t.getDescription());
            ps.setBoolean(3, t.isPaid());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.LeaveType WHERE leave_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- safety checks ----
    public boolean hasRequests(int leaveTypeId) throws SQLException {
        return exists("SELECT 1 FROM dbo.LeaveRequest WHERE leave_type_id=?", leaveTypeId);
    }

    public boolean hasPolicies(int leaveTypeId) throws SQLException {
        return exists("SELECT 1 FROM dbo.LeavePolicy WHERE leave_type_id=?", leaveTypeId);
    }

    public boolean hasBalances(int leaveTypeId) throws SQLException {
        return exists("SELECT 1 FROM dbo.LeaveBalance WHERE leave_type_id=?", leaveTypeId);
    }

    private boolean exists(String sql, int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
