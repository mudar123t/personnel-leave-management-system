package dao;

import model.LeaveBalance;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveBalanceDAO {

    private LeaveBalance mapRow(ResultSet rs) throws SQLException {
        LeaveBalance b = new LeaveBalance();
        b.setLeaveBalanceId(rs.getInt("leave_balance_id"));
        b.setEmployeeId(rs.getInt("employee_id"));
        b.setLeaveTypeId(rs.getInt("leave_type_id"));
        b.setYear(rs.getInt("year"));
        b.setTotalAllocatedDays(rs.getInt("total_allocated_days"));
        b.setUsedDays(rs.getInt("used_days"));
        b.setRemainingDays(rs.getInt("remaining_days"));
        return b;
    }

    public List<LeaveBalance> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveBalance ORDER BY leave_balance_id";
        List<LeaveBalance> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public LeaveBalance getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveBalance WHERE leave_balance_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public LeaveBalance getByEmpTypeYear(int employeeId, int leaveTypeId, int year) throws SQLException {
        String sql = "SELECT TOP 1 * FROM dbo.LeaveBalance WHERE employee_id=? AND leave_type_id=? AND [year]=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(LeaveBalance b) throws SQLException {
        String sql = "INSERT INTO dbo.LeaveBalance(employee_id, leave_type_id, [year], total_allocated_days, used_days, remaining_days) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getEmployeeId());
            ps.setInt(2, b.getLeaveTypeId());
            ps.setInt(3, b.getYear());
            ps.setInt(4, b.getTotalAllocatedDays());
            ps.setInt(5, b.getUsedDays());
            ps.setInt(6, b.getRemainingDays());
            ps.executeUpdate();
        }
    }

    public void update(LeaveBalance b) throws SQLException {
        String sql = "UPDATE dbo.LeaveBalance SET employee_id=?, leave_type_id=?, [year]=?, total_allocated_days=?, used_days=?, remaining_days=? " +
                     "WHERE leave_balance_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getEmployeeId());
            ps.setInt(2, b.getLeaveTypeId());
            ps.setInt(3, b.getYear());
            ps.setInt(4, b.getTotalAllocatedDays());
            ps.setInt(5, b.getUsedDays());
            ps.setInt(6, b.getRemainingDays());
            ps.setInt(7, b.getLeaveBalanceId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.LeaveBalance WHERE leave_balance_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Handy helper for approvals later
    public void addUsedDays(int employeeId, int leaveTypeId, int year, int daysToAdd) throws SQLException {
        String sql = "UPDATE dbo.LeaveBalance " +
                     "SET used_days = used_days + ?, remaining_days = remaining_days - ? " +
                     "WHERE employee_id=? AND leave_type_id=? AND [year]=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, daysToAdd);
            ps.setInt(2, daysToAdd);
            ps.setInt(3, employeeId);
            ps.setInt(4, leaveTypeId);
            ps.setInt(5, year);
            ps.executeUpdate();
        }
    }
}
