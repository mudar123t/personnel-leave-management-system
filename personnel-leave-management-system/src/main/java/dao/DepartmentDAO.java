package dao;

import model.Department;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAll() throws SQLException {
        String sql = "SELECT department_id, name, code, manager_employee_id FROM dbo.Department ORDER BY department_id";
        List<Department> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setName(rs.getString("name"));
                d.setCode(rs.getString("code"));

                int mgr = rs.getInt("manager_employee_id");
                d.setManagerEmployeeId(rs.wasNull() ? null : mgr);

                list.add(d);
            }
        }
        return list;
    }

    public Department getById(int id) throws SQLException {
        String sql = "SELECT department_id, name, code, manager_employee_id FROM dbo.Department WHERE department_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setName(rs.getString("name"));
                d.setCode(rs.getString("code"));
                int mgr = rs.getInt("manager_employee_id");
                d.setManagerEmployeeId(rs.wasNull() ? null : mgr);
                return d;
            }
        }
    }

    public void insert(Department d) throws SQLException {
        String sql = "INSERT INTO dbo.Department(name, code, manager_employee_id) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            if (d.getManagerEmployeeId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, d.getManagerEmployeeId());
            ps.executeUpdate();
        }
    }

    public void update(Department d) throws SQLException {
        String sql = "UPDATE dbo.Department SET name=?, code=?, manager_employee_id=? WHERE department_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            if (d.getManagerEmployeeId() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, d.getManagerEmployeeId());
            ps.setInt(4, d.getDepartmentId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Department WHERE department_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
