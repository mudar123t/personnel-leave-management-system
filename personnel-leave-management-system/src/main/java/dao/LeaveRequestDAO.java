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
        public String approverName;
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

    String selectReq =
        "SELECT employee_id, leave_type_id, start_date, total_days " +
        "FROM dbo.LeaveRequest WITH (UPDLOCK, ROWLOCK) " +
        "WHERE leave_request_id=? AND status='Pending'";

    String updateReq =
        "UPDATE dbo.LeaveRequest " +
        "SET status=?, approved_by_employee_id=?, decision_date=? " +
        "WHERE leave_request_id=? AND status='Pending'";

    String insertApproval =
        "INSERT INTO dbo.LeaveApproval (leave_request_id, approver_employee_id, action, comment) " +
        "VALUES (?, ?, ?, ?)";

    // Balance + policy
    String selectBalance =
        "SELECT leave_balance_id, total_allocated_days, used_days, remaining_days " +
        "FROM dbo.LeaveBalance WITH (UPDLOCK, ROWLOCK) " +
        "WHERE employee_id=? AND leave_type_id=? AND [year]=?";

    String selectPolicyQuota =
        "SELECT lp.annual_quota_days " +
        "FROM dbo.LeavePolicy lp " +
        "JOIN dbo.Employee e ON e.employment_type_id = lp.employment_type_id " +
        "WHERE lp.leave_type_id=? AND e.employee_id=?";

    String insertBalance =
        "INSERT INTO dbo.LeaveBalance (employee_id, leave_type_id, [year], total_allocated_days, used_days, remaining_days) " +
        "VALUES (?, ?, ?, ?, 0, ?)";

    String updateBalance =
        "UPDATE dbo.LeaveBalance SET used_days=?, remaining_days=? WHERE leave_balance_id=?";

    Connection con = null;
    PreparedStatement psSelReq = null;
    PreparedStatement psUpReq = null;
    PreparedStatement psInsAp = null;
    PreparedStatement psSelBal = null;
    PreparedStatement psSelPol = null;
    PreparedStatement psInsBal = null;
    PreparedStatement psUpBal = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        // 1) Lock + read request (must be Pending)
        psSelReq = con.prepareStatement(selectReq);
        psSelReq.setInt(1, leaveRequestId);

        int employeeId;
        int leaveTypeId;
        Date startDate;
        int totalDays;

        try (ResultSet rs = psSelReq.executeQuery()) {
            if (!rs.next()) throw new SQLException("Request is not Pending or not found.");
            employeeId = rs.getInt("employee_id");
            leaveTypeId = rs.getInt("leave_type_id");
            startDate = rs.getDate("start_date");
            totalDays = rs.getInt("total_days");
        }

        if (employeeId == approverEmployeeId) {
            throw new SQLException("Approver cannot approve/reject their own request.");
        }
        if (totalDays <= 0) {
            throw new SQLException("Invalid total days on request.");
        }

        // 2) If Approved -> update LeaveBalance
        if ("Approved".equals(action)) {
            int year = startDate.toLocalDate().getYear();

            psSelBal = con.prepareStatement(selectBalance);
            psSelBal.setInt(1, employeeId);
            psSelBal.setInt(2, leaveTypeId);
            psSelBal.setInt(3, year);

            Integer balanceId = null;
            int allocated = 0;
            int used = 0;
            int remaining = 0;

            try (ResultSet rs = psSelBal.executeQuery()) {
                if (rs.next()) {
                    balanceId = rs.getInt("leave_balance_id");
                    allocated = rs.getInt("total_allocated_days");
                    used = rs.getInt("used_days");
                    remaining = rs.getInt("remaining_days");
                }
            }

            // If no balance row -> try create using LeavePolicy quota
            if (balanceId == null) {
                psSelPol = con.prepareStatement(selectPolicyQuota);
                psSelPol.setInt(1, leaveTypeId);
                psSelPol.setInt(2, employeeId);

                Integer quota = null;
                try (ResultSet rs = psSelPol.executeQuery()) {
                    if (rs.next()) quota = rs.getInt(1);
                }

                if (quota == null) {
                    throw new SQLException("No LeavePolicy found for this employee employment type + leave type.");
                }

                allocated = quota;
                used = 0;
                remaining = quota;

                psInsBal = con.prepareStatement(insertBalance);
                psInsBal.setInt(1, employeeId);
                psInsBal.setInt(2, leaveTypeId);
                psInsBal.setInt(3, year);
                psInsBal.setInt(4, allocated);
                psInsBal.setInt(5, remaining);
                psInsBal.executeUpdate();

                // re-read to get balanceId (simple way)
                psSelBal.clearParameters();
                psSelBal.setInt(1, employeeId);
                psSelBal.setInt(2, leaveTypeId);
                psSelBal.setInt(3, year);
                try (ResultSet rs = psSelBal.executeQuery()) {
                    if (rs.next()) {
                        balanceId = rs.getInt("leave_balance_id");
                        allocated = rs.getInt("total_allocated_days");
                        used = rs.getInt("used_days");
                        remaining = rs.getInt("remaining_days");
                    }
                }
                if (balanceId == null) throw new SQLException("Failed to create LeaveBalance row.");
            }

            if (remaining < totalDays) {
                throw new SQLException("Not enough remaining leave days. Remaining=" + remaining + ", requested=" + totalDays);
            }

            int newUsed = used + totalDays;
            int newRemaining = remaining - totalDays;

            psUpBal = con.prepareStatement(updateBalance);
            psUpBal.setInt(1, newUsed);
            psUpBal.setInt(2, newRemaining);
            psUpBal.setInt(3, balanceId);
            psUpBal.executeUpdate();
        }

        // 3) Update request status + decision
        psUpReq = con.prepareStatement(updateReq);
        psUpReq.setString(1, action);
        psUpReq.setInt(2, approverEmployeeId);
        psUpReq.setDate(3, new Date(System.currentTimeMillis()));
        psUpReq.setInt(4, leaveRequestId);

        int affected = psUpReq.executeUpdate();
        if (affected == 0) throw new SQLException("Request is not Pending or not found.");

        // 4) Insert approval audit record
        psInsAp = con.prepareStatement(insertApproval);
        psInsAp.setInt(1, leaveRequestId);
        psInsAp.setInt(2, approverEmployeeId);
        psInsAp.setString(3, action);
        psInsAp.setString(4, (comment == null || comment.isBlank()) ? null : comment);
        psInsAp.executeUpdate();

        con.commit();

    } catch (SQLException ex) {
        if (con != null) {
            try { con.rollback(); } catch (SQLException ignored) {}
        }
        throw ex;
    } finally {
        if (psUpBal != null) try { psUpBal.close(); } catch (SQLException ignored) {}
        if (psInsBal != null) try { psInsBal.close(); } catch (SQLException ignored) {}
        if (psSelPol != null) try { psSelPol.close(); } catch (SQLException ignored) {}
        if (psSelBal != null) try { psSelBal.close(); } catch (SQLException ignored) {}
        if (psInsAp != null) try { psInsAp.close(); } catch (SQLException ignored) {}
        if (psUpReq != null) try { psUpReq.close(); } catch (SQLException ignored) {}
        if (psSelReq != null) try { psSelReq.close(); } catch (SQLException ignored) {}
        if (con != null) {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
            try { con.close(); } catch (SQLException ignored) {}
        }
    }
}

    
    public List<LeaveRequestRow> getDecisionsWithNames() throws SQLException {
    String sql =
        "SELECT lr.leave_request_id, lr.employee_id, (e.first_name + ' ' + e.last_name) AS employee_name, " +
        "       lr.leave_type_id, lt.name AS leave_type_name, lr.request_date, lr.start_date, lr.end_date, " +
        "       lr.total_days, lr.status, lr.reason, lr.approved_by_employee_id, " +
        "       (ap.first_name + ' ' + ap.last_name) AS approver_name, lr.decision_date " +
        "FROM dbo.LeaveRequest lr " +
        "JOIN dbo.Employee e ON e.employee_id = lr.employee_id " +
        "JOIN dbo.LeaveType lt ON lt.leave_type_id = lr.leave_type_id " +
        "LEFT JOIN dbo.Employee ap ON ap.employee_id = lr.approved_by_employee_id " +
        "WHERE lr.status IN ('Approved','Rejected') " +
        "ORDER BY lr.decision_date DESC, lr.leave_request_id DESC";

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

            int apId = rs.getInt("approved_by_employee_id");
            r.approvedByEmployeeId = rs.wasNull() ? null : apId;

            // add this field in LeaveRequestRow:
            r.approverName = rs.getString("approver_name");

            r.decisionDate = rs.getDate("decision_date");
            list.add(r);
        }
    }
    return list;
}

}
