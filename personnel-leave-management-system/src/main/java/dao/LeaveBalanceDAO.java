package dao;

import model.LeaveBalance;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveBalanceDAO {

    public static class LeaveBalanceRow {
        public int leaveBalanceId;
        public int employeeId;
        public String employeeName;
        public int leaveTypeId;
        public String leaveTypeName;
        public int year;
        public int totalAllocatedDays;
        public int usedDays;
        public int remainingDays;
    }

    // ====== Listing / Filtering ======

    public List<LeaveBalanceRow> getAllWithNames() throws SQLException {
        String sql =
            "SELECT lb.leave_balance_id, lb.employee_id, (e.first_name + ' ' + e.last_name) AS employee_name, " +
            "       lb.leave_type_id, lt.name AS leave_type_name, lb.[year], " +
            "       lb.total_allocated_days, lb.used_days, lb.remaining_days " +
            "FROM dbo.LeaveBalance lb " +
            "JOIN dbo.Employee e ON e.employee_id = lb.employee_id " +
            "JOIN dbo.LeaveType lt ON lt.leave_type_id = lb.leave_type_id " +
            "ORDER BY lb.[year] DESC, lb.employee_id, lb.leave_type_id";

        List<LeaveBalanceRow> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<LeaveBalanceRow> search(Integer employeeId, Integer leaveTypeId, Integer year) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT lb.leave_balance_id, lb.employee_id, (e.first_name + ' ' + e.last_name) AS employee_name, " +
            "       lb.leave_type_id, lt.name AS leave_type_name, lb.[year], " +
            "       lb.total_allocated_days, lb.used_days, lb.remaining_days " +
            "FROM dbo.LeaveBalance lb " +
            "JOIN dbo.Employee e ON e.employee_id = lb.employee_id " +
            "JOIN dbo.LeaveType lt ON lt.leave_type_id = lb.leave_type_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (employeeId != null) { sql.append(" AND lb.employee_id=?"); params.add(employeeId); }
        if (leaveTypeId != null) { sql.append(" AND lb.leave_type_id=?"); params.add(leaveTypeId); }
        if (year != null) { sql.append(" AND lb.[year]=?"); params.add(year); }

        sql.append(" ORDER BY lb.[year] DESC, lb.employee_id, lb.leave_type_id");

        List<LeaveBalanceRow> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private LeaveBalanceRow mapRow(ResultSet rs) throws SQLException {
        LeaveBalanceRow r = new LeaveBalanceRow();
        r.leaveBalanceId = rs.getInt("leave_balance_id");
        r.employeeId = rs.getInt("employee_id");
        r.employeeName = rs.getString("employee_name");
        r.leaveTypeId = rs.getInt("leave_type_id");
        r.leaveTypeName = rs.getString("leave_type_name");
        r.year = rs.getInt("year");
        r.totalAllocatedDays = rs.getInt("total_allocated_days");
        r.usedDays = rs.getInt("used_days");
        r.remainingDays = rs.getInt("remaining_days");
        return r;
    }

    // ====== Policy <-> Balance helpers ======

    public static class BalanceInfo {
        public int totalAllocated;
        public int used;
        public int remaining;
    }

    public BalanceInfo getBalanceInfo(int employeeId, int leaveTypeId, int year) throws SQLException {
        String sql =
            "SELECT total_allocated_days, used_days, remaining_days " +
            "FROM dbo.LeaveBalance WHERE employee_id=? AND leave_type_id=? AND [year]=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                BalanceInfo b = new BalanceInfo();
                b.totalAllocated = rs.getInt("total_allocated_days");
                b.used = rs.getInt("used_days");
                b.remaining = rs.getInt("remaining_days");
                return b;
            }
        }
    }

    public void ensureBalanceRowExists(int employeeId, int leaveTypeId, int year) throws SQLException {
        String selectSql =
            "SELECT 1 FROM dbo.LeaveBalance WHERE employee_id=? AND leave_type_id=? AND [year]=?";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, leaveTypeId);
                ps.setInt(3, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return;
                }
            }

            Integer quota = getPolicyQuota(con, employeeId, leaveTypeId);
            if (quota == null) throw new SQLException("No LeavePolicy found for employee + leave type (cannot create balance).");

            insertBalance(con, employeeId, leaveTypeId, year, quota);
        }
    }

    private Integer getPolicyQuota(Connection con, int employeeId, int leaveTypeId) throws SQLException {
        String policySql =
            "SELECT lp.annual_quota_days " +
            "FROM dbo.LeavePolicy lp " +
            "JOIN dbo.Employee e ON e.employment_type_id = lp.employment_type_id " +
            "WHERE e.employee_id=? AND lp.leave_type_id=?";

        try (PreparedStatement ps = con.prepareStatement(policySql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, leaveTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getInt(1);
            }
        }
    }

    private void insertBalance(Connection con, int employeeId, int leaveTypeId, int year, int quota) throws SQLException {
        String insertSql =
            "INSERT INTO dbo.LeaveBalance(employee_id, leave_type_id, [year], total_allocated_days, used_days, remaining_days) " +
            "VALUES(?, ?, ?, ?, 0, ?)";

        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setInt(3, year);
            ps.setInt(4, quota);
            ps.setInt(5, quota);
            ps.executeUpdate();
        }
    }

    // Sync quota from policy into balances (does NOT change used_days)
    public void syncAllocatedFromPolicy(int employeeId, int leaveTypeId, int year) throws SQLException {
        String policySql =
            "SELECT lp.annual_quota_days " +
            "FROM dbo.LeavePolicy lp " +
            "JOIN dbo.Employee e ON e.employment_type_id = lp.employment_type_id " +
            "WHERE e.employee_id=? AND lp.leave_type_id=?";

        String updateSql =
            "UPDATE dbo.LeaveBalance " +
            "SET total_allocated_days=?, remaining_days=(? - used_days) " +
            "WHERE employee_id=? AND leave_type_id=? AND [year]=?";

        try (Connection con = DBConnection.getConnection()) {
            Integer quota = null;

            try (PreparedStatement ps = con.prepareStatement(policySql)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, leaveTypeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) quota = rs.getInt(1);
                }
            }
            if (quota == null) throw new SQLException("No LeavePolicy found (cannot sync balance).");

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setInt(1, quota);
                ps.setInt(2, quota);
                ps.setInt(3, employeeId);
                ps.setInt(4, leaveTypeId);
                ps.setInt(5, year);
                ps.executeUpdate();
            }
        }
    }
}
