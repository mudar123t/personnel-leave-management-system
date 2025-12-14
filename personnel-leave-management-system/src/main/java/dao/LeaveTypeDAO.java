package dao;

import model.LeaveType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveTypeDAO {

    public List<LeaveType> getAll() throws SQLException {
        String sql = "SELECT leave_type_id, name, description, is_paid FROM dbo.LeaveType ORDER BY leave_type_id";
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

    public LeaveType getById(int id) throws SQLException {
        String sql = "SELECT leave_type_id, name, description, is_paid FROM dbo.LeaveType WHERE leave_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                LeaveType t = new LeaveType();
                t.setLeaveTypeId(rs.getInt("leave_type_id"));
                t.setName(rs.getString("name"));
                t.setDescription(rs.getString("description"));
                t.setPaid(rs.getBoolean("is_paid"));
                return t;
            }
        }
    }

    public void insert(LeaveType t) throws SQLException {
        String sql = "INSERT INTO dbo.LeaveType(name, description, is_paid) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getDescription());
            ps.setBoolean(3, t.isPaid());
            ps.executeUpdate();
        }
    }

    public void update(LeaveType t) throws SQLException {
        String sql = "UPDATE dbo.LeaveType SET name=?, description=?, is_paid=? WHERE leave_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getDescription());
            ps.setBoolean(3, t.isPaid());
            ps.setInt(4, t.getLeaveTypeId());
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
}
