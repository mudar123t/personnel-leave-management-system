package dao;

import model.Employee;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("employee_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setNationalId(rs.getString("national_id"));
        e.setBirthDate(rs.getDate("birth_date"));
        e.setGender(rs.getString("gender"));
        e.setHireDate(rs.getDate("hire_date"));

        BigDecimal sal = rs.getBigDecimal("salary");
        e.setSalary(sal == null ? 0.0 : sal.doubleValue());

        e.setLocationId(rs.getInt("location_id"));
        e.setDepartmentId(rs.getInt("department_id"));
        e.setPositionId(rs.getInt("position_id"));
        e.setEmploymentTypeId(rs.getInt("employment_type_id"));
        e.setStatus(rs.getString("status"));
        return e;
    }

    public List<Employee> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.Employee ORDER BY employee_id";
        List<Employee> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    
    public List<Object[]> getAllWithNames() throws SQLException {
    String sql =
        "SELECT e.employee_id, e.first_name, e.last_name, e.national_id, e.birth_date, e.gender, " +
        "       e.hire_date, e.salary, " +
        "       l.name AS location_name, d.name AS department_name, p.name AS position_name, " +
        "       et.name AS employment_type_name, e.status " +
        "FROM dbo.Employee e " +
        "JOIN dbo.Location l ON e.location_id = l.location_id " +
        "JOIN dbo.Department d ON e.department_id = d.department_id " +
        "JOIN dbo.Position p ON e.position_id = p.position_id " +
        "JOIN dbo.EmploymentType et ON e.employment_type_id = et.employment_type_id " +
        "ORDER BY e.employee_id";

    List<Object[]> rows = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object salaryObj = rs.getBigDecimal("salary"); // can be null
            rows.add(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("national_id"),
                    rs.getDate("birth_date"),
                    rs.getString("gender"),
                    rs.getDate("hire_date"),
                    salaryObj == null ? null : salaryObj,
                    rs.getString("location_name"),
                    rs.getString("department_name"),
                    rs.getString("position_name"),
                    rs.getString("employment_type_name"),
                    rs.getString("status")
            });
        }
    }
    return rows;
}
    
    public void delete(int employeeId) throws SQLException {
    String sql = "DELETE FROM dbo.Employee WHERE employee_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, employeeId);
        ps.executeUpdate();
    }
}

    
    public Employee getById(int employeeId) throws SQLException {
    String sql = "SELECT * FROM dbo.Employee WHERE employee_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, employeeId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return mapRow(rs);
        }
    }
    return null;
}

public void insert(Employee e) throws SQLException {
    String sql =
        "INSERT INTO dbo.Employee " +
        "(first_name, last_name, national_id, birth_date, gender, hire_date, salary, " +
        " location_id, department_id, position_id, employment_type_id, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, e.getFirstName());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getNationalId());
        ps.setDate(4, new java.sql.Date(e.getBirthDate().getTime()));
        ps.setString(5, e.getGender()); // "M"/"F"/"O" or null
        ps.setDate(6, new java.sql.Date(e.getHireDate().getTime()));
        ps.setBigDecimal(7, new java.math.BigDecimal(e.getSalary()));

        ps.setInt(8, e.getLocationId());
        ps.setInt(9, e.getDepartmentId());
        ps.setInt(10, e.getPositionId());
        ps.setInt(11, e.getEmploymentTypeId());
        ps.setString(12, e.getStatus());

        ps.executeUpdate();
    }
}

public void update(Employee e) throws SQLException {
    String sql =
        "UPDATE dbo.Employee SET " +
        "first_name=?, last_name=?, national_id=?, birth_date=?, gender=?, hire_date=?, salary=?, " +
        "location_id=?, department_id=?, position_id=?, employment_type_id=?, status=? " +
        "WHERE employee_id=?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, e.getFirstName());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getNationalId());
        ps.setDate(4, new java.sql.Date(e.getBirthDate().getTime()));
        ps.setString(5, e.getGender());
        ps.setDate(6, new java.sql.Date(e.getHireDate().getTime()));
        ps.setBigDecimal(7, new java.math.BigDecimal(e.getSalary()));

        ps.setInt(8, e.getLocationId());
        ps.setInt(9, e.getDepartmentId());
        ps.setInt(10, e.getPositionId());
        ps.setInt(11, e.getEmploymentTypeId());
        ps.setString(12, e.getStatus());

        ps.setInt(13, e.getEmployeeId());

        ps.executeUpdate();
    }
}


}
