package dao;

import model.EmployeeDepartmentHistory;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDepartmentHistoryDAO {

    private EmployeeDepartmentHistory mapRow(ResultSet rs) throws SQLException {
        EmployeeDepartmentHistory h = new EmployeeDepartmentHistory();
        h.setEmpDeptHistId(rs.getInt("emp_dept_hist_id"));
        h.setEmployeeId(rs.getInt("employee_id"));
        h.setDepartmentId(rs.getInt("department_id"));
        h.setStartDate(rs.getDate("start_date"));
        h.setEndDate(rs.getDate("end_date")); // nullable
        return h;
    }

    public List<EmployeeDepartmentHistory> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.EmployeeDepartmentHistory ORDER BY emp_dept_hist_id";
        List<EmployeeDepartmentHistory> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<EmployeeDepartmentHistory> getByEmployeeId(int employeeId) throws SQLException {
        String sql = "SELECT * FROM dbo.EmployeeDepartmentHistory WHERE employee_id=? ORDER BY start_date DESC";
        List<EmployeeDepartmentHistory> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public EmployeeDepartmentHistory getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.EmployeeDepartmentHistory WHERE emp_dept_hist_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(EmployeeDepartmentHistory h) throws SQLException {
        String sql = "INSERT INTO dbo.EmployeeDepartmentHistory(employee_id, department_id, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getEmployeeId());
            ps.setInt(2, h.getDepartmentId());
            ps.setDate(3, new java.sql.Date(h.getStartDate().getTime()));
            if (h.getEndDate() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, new java.sql.Date(h.getEndDate().getTime()));
            ps.executeUpdate();
        }
    }

    public void update(EmployeeDepartmentHistory h) throws SQLException {
        String sql = "UPDATE dbo.EmployeeDepartmentHistory SET employee_id=?, department_id=?, start_date=?, end_date=? WHERE emp_dept_hist_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getEmployeeId());
            ps.setInt(2, h.getDepartmentId());
            ps.setDate(3, new java.sql.Date(h.getStartDate().getTime()));
            if (h.getEndDate() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, new java.sql.Date(h.getEndDate().getTime()));
            ps.setInt(5, h.getEmpDeptHistId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.EmployeeDepartmentHistory WHERE emp_dept_hist_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
