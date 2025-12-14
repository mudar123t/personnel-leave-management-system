package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    public static class LeaveRequestRow {
        public int id;
        public int employeeId;
        public String employeeName; // optional join
        public int leaveTypeId;
        public String leaveTypeName; // optional join
        public Date requestDate;
        public Date startDate;
        public Date endDate;
        public int totalDays;
        public String status;
        public String reason;
        public Integer approvedByEmployeeId;
        public Date decisionDate;
    }

    public List<LeaveRequestRow> getAllWithNames() throws SQLException {
        String sql =
            "SELECT lr.leave_request_id, lr.employee_id, (e.first_name + ' ' + e.last_name) AS employee_name, " +
            "       lr.leave_type_id, lt.name AS leave_type_name, lr.request_date, lr.start_date, lr.end_date, " +
            "       lr.total_days, lr.status, lr.reason, lr.approved_by_employee_id, lr.decision_date " +
            "FROM dbo.LeaveRequest lr " +
            "JOIN dbo.Employee e ON e.employee_id = lr.employee_id " +
            "JOIN dbo.LeaveType lt ON lt.leave_type_id = lr.leave_type_id " +
            "ORDER BY lr.leave_request_id DESC";

        List<LeaveRequestRow> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveRequestRow r = new LeaveRequestRow();
                r.id = rs.getInt("leave_request_id");
                r.employeeId = rs.getInt("employee_id");
                r.employeeName = rs.getString("employee_name");
                r.leaveTypeId = rs.getInt("leave_type_id");
                r.leaveTypeName = rs.getString("leave_type_name");
                r.requestDate = rs.getDate("request_date");
                r.startDate = rs.getDate("start_date");
                r.endDate = rs.getDate("end_date");
                r.totalDays = rs.getInt("total_days");
                r.status = rs.getString("status");
                r.reason = rs.getString("reason");
                int ap = rs.getInt("approved_by_employee_id");
                r.approvedByEmployeeId = rs.wasNull() ? null : ap;
                r.decisionDate = rs.getDate("decision_date");
                list.add(r);
            }
        }
        return list;
    }

    public int insert(int employeeId, int leaveTypeId, Date requestDate, Date startDate, Date endDate, int totalDays, String reason) throws SQLException {
        String sql =
            "INSERT INTO dbo.LeaveRequest (employee_id, leave_type_id, request_date, start_date, end_date, total_days, status, reason) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setDate(3, requestDate);
            ps.setDate(4, startDate);
            ps.setDate(5, endDate);
            ps.setInt(6, totalDays);
            ps.setString(7, (reason == null || reason.isBlank()) ? null : reason);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            throw new SQLException("Insert leave request failed (no generated key).");
        }
    }

    public void cancel(int leaveRequestId) throws SQLException {
        String sql =
            "UPDATE dbo.LeaveRequest SET status='Canceled' " +
            "WHERE leave_request_id=? AND status='Pending'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, leaveRequestId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Only Pending requests can be canceled (or request not found).");
            }
        }
    }

    public List<LeaveRequestRow> getPendingWithNames() throws SQLException {
        String sql =
            "SELECT lr.leave_request_id, lr.employee_id, (e.first_name + ' ' + e.last_name) AS employee_name, " +
            "       lr.leave_type_id, lt.name AS leave_type_name, lr.request_date, lr.start_date, lr.end_date, " +
            "       lr.total_days, lr.status, lr.reason " +
            "FROM dbo.LeaveRequest lr " +
            "JOIN dbo.Employee e ON e.employee_id = lr.employee_id " +
            "JOIN dbo.LeaveType lt ON lt.leave_type_id = lr.leave_type_id " +
            "WHERE lr.status='Pending' " +
            "ORDER BY lr.request_date DESC, lr.leave_request_id DESC";

        List<LeaveRequestRow> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LeaveRequestRow r = new LeaveRequestRow();
                r.id = rs.getInt("leave_request_id");
                r.employeeId = rs.getInt("employee_id");
                r.employeeName = rs.getString("employee_name");
                r.leaveTypeId = rs.getInt("leave_type_id");
                r.leaveTypeName = rs.getString("leave_type_name");
                r.requestDate = rs.getDate("request_date");
                r.startDate = rs.getDate("start_date");
                r.endDate = rs.getDate("end_date");
                r.totalDays = rs.getInt("total_days");
                r.status = rs.getString("status");
                r.reason = rs.getString("reason");
                list.add(r);
            }
        }
        return list;
    }

    public void decide(int leaveRequestId, int approverEmployeeId, String action, String comment) throws SQLException {
        if (!"Approved".equals(action) && !"Rejected".equals(action)) {
            throw new IllegalArgumentException("action must be Approved or Rejected");
        }

        String updateReq =
            "UPDATE dbo.LeaveRequest " +
            "SET status=?, approved_by_employee_id=?, decision_date=? " +
            "WHERE leave_request_id=? AND status='Pending'";

        String insertApproval =
            "INSERT INTO dbo.LeaveApproval (leave_request_id, approver_employee_id, action, comment) " +
            "VALUES (?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement psUp = null;
        PreparedStatement psIns = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            psUp = con.prepareStatement(updateReq);
            psUp.setString(1, action);
            psUp.setInt(2, approverEmployeeId);
            psUp.setDate(3, new Date(System.currentTimeMillis()));
            psUp.setInt(4, leaveRequestId);

            int affected = psUp.executeUpdate();
            if (affected == 0) throw new SQLException("Request is not Pending or not found.");

            psIns = con.prepareStatement(insertApproval);
            psIns.setInt(1, leaveRequestId);
            psIns.setInt(2, approverEmployeeId);
            psIns.setString(3, action);
            psIns.setString(4, (comment == null || comment.isBlank()) ? null : comment);
            psIns.executeUpdate();

            con.commit();

        } catch (SQLException ex) {
            if (con != null) try { con.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            if (psIns != null) try { psIns.close(); } catch (SQLException ignored) {}
            if (psUp != null) try { psUp.close(); } catch (SQLException ignored) {}
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                try { con.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
