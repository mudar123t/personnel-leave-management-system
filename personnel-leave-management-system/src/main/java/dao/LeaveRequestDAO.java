package dao;

import model.LeaveRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    private LeaveRequest mapRow(ResultSet rs) throws SQLException {
        LeaveRequest r = new LeaveRequest();
        r.setLeaveRequestId(rs.getInt("leave_request_id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setLeaveTypeId(rs.getInt("leave_type_id"));
        r.setRequestDate(rs.getDate("request_date"));
        r.setStartDate(rs.getDate("start_date"));
        r.setEndDate(rs.getDate("end_date"));
        r.setTotalDays(rs.getInt("total_days"));
        r.setStatus(rs.getString("status"));
        r.setReason(rs.getString("reason"));

        int appr = rs.getInt("approved_by_employee_id");
        r.setApprovedByEmployeeId(rs.wasNull() ? null : appr);

        r.setDecisionDate(rs.getDate("decision_date")); // nullable
        return r;
    }

    public List<LeaveRequest> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveRequest ORDER BY leave_request_id DESC";
        List<LeaveRequest> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<LeaveRequest> getByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveRequest WHERE status=? ORDER BY leave_request_id DESC";
        List<LeaveRequest> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public LeaveRequest getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.LeaveRequest WHERE leave_request_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(LeaveRequest r) throws SQLException {
        String sql = "INSERT INTO dbo.LeaveRequest(employee_id, leave_type_id, request_date, start_date, end_date, total_days, status, reason, approved_by_employee_id, decision_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getEmployeeId());
            ps.setInt(2, r.getLeaveTypeId());
            ps.setDate(3, new java.sql.Date(r.getRequestDate().getTime()));
            ps.setDate(4, new java.sql.Date(r.getStartDate().getTime()));
            ps.setDate(5, new java.sql.Date(r.getEndDate().getTime()));
            ps.setInt(6, r.getTotalDays());
            ps.setString(7, r.getStatus());
            ps.setString(8, r.getReason());

            if (r.getApprovedByEmployeeId() == null) ps.setNull(9, Types.INTEGER);
            else ps.setInt(9, r.getApprovedByEmployeeId());

            if (r.getDecisionDate() == null) ps.setNull(10, Types.DATE);
            else ps.setDate(10, new java.sql.Date(r.getDecisionDate().getTime()));

            ps.executeUpdate();
        }
    }

    public void update(LeaveRequest r) throws SQLException {
        String sql = "UPDATE dbo.LeaveRequest SET employee_id=?, leave_type_id=?, request_date=?, start_date=?, end_date=?, total_days=?, status=?, reason=?, approved_by_employee_id=?, decision_date=? " +
                     "WHERE leave_request_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getEmployeeId());
            ps.setInt(2, r.getLeaveTypeId());
            ps.setDate(3, new java.sql.Date(r.getRequestDate().getTime()));
            ps.setDate(4, new java.sql.Date(r.getStartDate().getTime()));
            ps.setDate(5, new java.sql.Date(r.getEndDate().getTime()));
            ps.setInt(6, r.getTotalDays());
            ps.setString(7, r.getStatus());
            ps.setString(8, r.getReason());

            if (r.getApprovedByEmployeeId() == null) ps.setNull(9, Types.INTEGER);
            else ps.setInt(9, r.getApprovedByEmployeeId());

            if (r.getDecisionDate() == null) ps.setNull(10, Types.DATE);
            else ps.setDate(10, new java.sql.Date(r.getDecisionDate().getTime()));

            ps.setInt(11, r.getLeaveRequestId());

            ps.executeUpdate();
        }
    }

    // convenient for approval screen
    public void setDecision(int leaveRequestId, String status, int approvedByEmployeeId, java.util.Date decisionDate) throws SQLException {
        String sql = "UPDATE dbo.LeaveRequest SET status=?, approved_by_employee_id=?, decision_date=? WHERE leave_request_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approvedByEmployeeId);
            ps.setDate(3, new java.sql.Date(decisionDate.getTime()));
            ps.setInt(4, leaveRequestId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.LeaveRequest WHERE leave_request_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
