package dao;

import model.LeaveApproval;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveApprovalDAO {

    private LeaveApproval mapRow(ResultSet rs) throws SQLException {
        LeaveApproval a = new LeaveApproval();
        a.setLeaveApprovalId(rs.getInt("leave_approval_id"));
        a.setLeaveRequestId(rs.getInt("leave_request_id"));
        a.setApproverEmployeeId(rs.getInt("approver_employee_id"));
        a.setAction(rs.getString("action"));
        a.setActionDate(rs.getTimestamp("action_date"));
        a.setComment(rs.getString("comment"));
        return a;
    }

    public List<LeaveApproval> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveApproval ORDER BY leave_approval_id DESC";
        List<LeaveApproval> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<LeaveApproval> getByLeaveRequestId(int leaveRequestId) throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveApproval WHERE leave_request_id=? ORDER BY action_date DESC";
        List<LeaveApproval> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, leaveRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public LeaveApproval getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveApproval WHERE leave_approval_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(LeaveApproval a) throws SQLException {
        String sql = "INSERT INTO dbo.LeaveApproval(leave_request_id, approver_employee_id, action, action_date, comment) " +
                     "VALUES (?, ?, ?, COALESCE(?, SYSUTCDATETIME()), ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getLeaveRequestId());
            ps.setInt(2, a.getApproverEmployeeId());
            ps.setString(3, a.getAction());

            if (a.getActionDate() == null) ps.setNull(4, Types.TIMESTAMP);
            else ps.setTimestamp(4, new Timestamp(a.getActionDate().getTime()));

            ps.setString(5, a.getComment());
            ps.executeUpdate();
        }
    }

    public void update(LeaveApproval a) throws SQLException {
        String sql = "UPDATE dbo.LeaveApproval SET leave_request_id=?, approver_employee_id=?, action=?, action_date=?, comment=? " +
                     "WHERE leave_approval_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getLeaveRequestId());
            ps.setInt(2, a.getApproverEmployeeId());
            ps.setString(3, a.getAction());
            ps.setTimestamp(4, a.getActionDate() == null ? null : new Timestamp(a.getActionDate().getTime()));
            ps.setString(5, a.getComment());
            ps.setInt(6, a.getLeaveApprovalId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.LeaveApproval WHERE leave_approval_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
