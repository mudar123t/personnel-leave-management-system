package dao;

import model.LeavePolicy;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeavePolicyDAO {

    private LeavePolicy mapRow(ResultSet rs) throws SQLException {
        LeavePolicy p = new LeavePolicy();
        p.setLeavePolicyId(rs.getInt("leave_policy_id"));
        p.setLeaveTypeId(rs.getInt("leave_type_id"));
        p.setEmploymentTypeId(rs.getInt("employment_type_id"));
        p.setAnnualQuotaDays(rs.getInt("annual_quota_days"));

        int maxCon = rs.getInt("max_consecutive_days");
        p.setMaxConsecutiveDays(rs.wasNull() ? null : maxCon);

        int minSvc = rs.getInt("min_service_months_required");
        p.setMinServiceMonthsRequired(rs.wasNull() ? null : minSvc);

        return p;
    }

    public List<LeavePolicy> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.LeavePolicy ORDER BY leave_policy_id";
        List<LeavePolicy> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public LeavePolicy getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.LeavePolicy WHERE leave_policy_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public LeavePolicy getByTypeAndEmployment(int leaveTypeId, int employmentTypeId) throws SQLException {
        String sql = "SELECT TOP 1 * FROM dbo.LeavePolicy WHERE leave_type_id=? AND employment_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, leaveTypeId);
            ps.setInt(2, employmentTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(LeavePolicy p) throws SQLException {
        String sql = "INSERT INTO dbo.LeavePolicy(leave_type_id, employment_type_id, annual_quota_days, max_consecutive_days, min_service_months_required) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getLeaveTypeId());
            ps.setInt(2, p.getEmploymentTypeId());
            ps.setInt(3, p.getAnnualQuotaDays());
            if (p.getMaxConsecutiveDays() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, p.getMaxConsecutiveDays());
            if (p.getMinServiceMonthsRequired() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getMinServiceMonthsRequired());
            ps.executeUpdate();
        }
    }

    public void update(LeavePolicy p) throws SQLException {
        String sql = "UPDATE dbo.LeavePolicy SET leave_type_id=?, employment_type_id=?, annual_quota_days=?, max_consecutive_days=?, min_service_months_required=? " +
                     "WHERE leave_policy_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getLeaveTypeId());
            ps.setInt(2, p.getEmploymentTypeId());
            ps.setInt(3, p.getAnnualQuotaDays());
            if (p.getMaxConsecutiveDays() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, p.getMaxConsecutiveDays());
            if (p.getMinServiceMonthsRequired() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getMinServiceMonthsRequired());
            ps.setInt(6, p.getLeavePolicyId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.LeavePolicy WHERE leave_policy_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public static class PolicyCheckResult {
    public Integer annualQuotaDays;
    public Integer maxConsecutiveDays;
    public Integer minServiceMonthsRequired;
    public java.sql.Date hireDate;
}

public PolicyCheckResult getPolicyForEmployee(int employeeId, int leaveTypeId) throws SQLException {
    String sql =
        "SELECT lp.annual_quota_days, lp.max_consecutive_days, lp.min_service_months_required, e.hire_date " +
        "FROM dbo.Employee e " +
        "JOIN dbo.LeavePolicy lp ON lp.employment_type_id = e.employment_type_id " +
        "WHERE e.employee_id = ? AND lp.leave_type_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, employeeId);
        ps.setInt(2, leaveTypeId);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return null;

            PolicyCheckResult r = new PolicyCheckResult();
            r.annualQuotaDays = rs.getInt("annual_quota_days");
            if (rs.wasNull()) r.annualQuotaDays = null;

            int mc = rs.getInt("max_consecutive_days");
            r.maxConsecutiveDays = rs.wasNull() ? null : mc;

            int ms = rs.getInt("min_service_months_required");
            r.minServiceMonthsRequired = rs.wasNull() ? null : ms;

            r.hireDate = rs.getDate("hire_date");
            return r;
        }
    }
}

}
