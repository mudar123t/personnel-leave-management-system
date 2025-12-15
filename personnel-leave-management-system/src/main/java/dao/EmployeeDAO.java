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
    
    
    public String getRoleNameForEmployee(int employeeId) throws SQLException {
    String sql =
            "SELECT r.role_name " +
            "FROM dbo.UserAccount ua " +
            "JOIN dbo.Role r ON r.role_id = ua.role_id " +
            "WHERE ua.employee_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, employeeId);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }
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
        if (e.getSalary() == null) {
            ps.setNull(7, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(
                7,
                java.math.BigDecimal.valueOf(e.getSalary())
                    .setScale(2, java.math.RoundingMode.HALF_UP)
            );
        }

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
        if (e.getSalary() == null) {
            ps.setNull(7, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(
                7,
                java.math.BigDecimal.valueOf(e.getSalary())
                    .setScale(2, java.math.RoundingMode.HALF_UP)
            );
        }

        ps.setInt(8, e.getLocationId());
        ps.setInt(9, e.getDepartmentId());
        ps.setInt(10, e.getPositionId());
        ps.setInt(11, e.getEmploymentTypeId());
        ps.setString(12, e.getStatus());

        ps.setInt(13, e.getEmployeeId());

        ps.executeUpdate();
    }
}
public boolean existsNationalId(String nationalId) throws SQLException {
    String sql = "SELECT 1 FROM dbo.Employee WHERE national_id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nationalId);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}

public boolean existsNationalIdForOtherEmployee(String nationalId, int employeeId) throws SQLException {
    String sql = "SELECT 1 FROM dbo.Employee WHERE national_id = ? AND employee_id <> ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nationalId);
        ps.setInt(2, employeeId);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}


public int insertWithDeptHistory(Employee e) throws SQLException {
    String insertEmp =
        "INSERT INTO dbo.Employee " +
        "(first_name, last_name, national_id, birth_date, gender, hire_date, salary, " +
        " location_id, department_id, position_id, employment_type_id, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String insertHist =
        "INSERT INTO dbo.EmployeeDepartmentHistory " +
        "(employee_id, department_id, start_date, end_date) " +
        "VALUES (?, ?, ?, NULL)";

    Connection con = null;
    PreparedStatement psEmp = null;
    PreparedStatement psHist = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        psEmp = con.prepareStatement(insertEmp, Statement.RETURN_GENERATED_KEYS);

        psEmp.setString(1, e.getFirstName());
        psEmp.setString(2, e.getLastName());
        psEmp.setString(3, e.getNationalId());
        psEmp.setDate(4, new java.sql.Date(e.getBirthDate().getTime()));
        psEmp.setString(5, e.getGender());
        psEmp.setDate(6, new java.sql.Date(e.getHireDate().getTime()));

        // salary nullable
        if (e.getSalary() == null) {
            psEmp.setNull(7, java.sql.Types.DECIMAL);
        } else {
            psEmp.setBigDecimal(
                7,
                java.math.BigDecimal.valueOf(e.getSalary())
                    .setScale(2, java.math.RoundingMode.HALF_UP)
            );
        }

        psEmp.setInt(8, e.getLocationId());
        psEmp.setInt(9, e.getDepartmentId());
        psEmp.setInt(10, e.getPositionId());
        psEmp.setInt(11, e.getEmploymentTypeId());
        psEmp.setString(12, e.getStatus());

        psEmp.executeUpdate();

        int newEmployeeId;
        try (ResultSet keys = psEmp.getGeneratedKeys()) {
            if (!keys.next()) throw new SQLException("Insert employee failed: no generated key returned.");
            newEmployeeId = keys.getInt(1);
        }

        psHist = con.prepareStatement(insertHist);
        psHist.setInt(1, newEmployeeId);
        psHist.setInt(2, e.getDepartmentId());
        psHist.setDate(3, new java.sql.Date(e.getHireDate().getTime())); // start_date = hire_date
        psHist.executeUpdate();

        con.commit();
        return newEmployeeId;

    } catch (SQLException ex) {
        if (con != null) {
            try { con.rollback(); } catch (SQLException ignored) {}
        }
        throw ex;
    } finally {
        if (psHist != null) try { psHist.close(); } catch (SQLException ignored) {}
        if (psEmp != null) try { psEmp.close(); } catch (SQLException ignored) {}
        if (con != null) {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
            try { con.close(); } catch (SQLException ignored) {}
        }
    }
}


public void updateWithDeptHistory(Employee e, java.util.Date changeDate) throws SQLException {

    String selectCurrentDept =
        "SELECT department_id FROM dbo.Employee WHERE employee_id = ?";

    String updateEmp =
        "UPDATE dbo.Employee SET " +
        "first_name=?, last_name=?, national_id=?, birth_date=?, gender=?, hire_date=?, salary=?, " +
        "location_id=?, department_id=?, position_id=?, employment_type_id=?, status=? " +
        "WHERE employee_id=?";

    String closeCurrentHist =
        "UPDATE dbo.EmployeeDepartmentHistory " +
        "SET end_date = ? " +
        "WHERE employee_id = ? AND end_date IS NULL";

    String insertNewHist =
        "INSERT INTO dbo.EmployeeDepartmentHistory (employee_id, department_id, start_date, end_date) " +
        "VALUES (?, ?, ?, NULL)";

    Connection con = null;
    PreparedStatement psSel = null;
    PreparedStatement psUp = null;
    PreparedStatement psClose = null;
    PreparedStatement psIns = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        // 1) fetch current department
        psSel = con.prepareStatement(selectCurrentDept);
        psSel.setInt(1, e.getEmployeeId());
        int oldDeptId;
        try (ResultSet rs = psSel.executeQuery()) {
            if (!rs.next()) throw new SQLException("Employee not found for update.");
            oldDeptId = rs.getInt(1);
        }

        // 2) update employee (same as your update)
        psUp = con.prepareStatement(updateEmp);

        psUp.setString(1, e.getFirstName());
        psUp.setString(2, e.getLastName());
        psUp.setString(3, e.getNationalId());
        psUp.setDate(4, new java.sql.Date(e.getBirthDate().getTime()));
        psUp.setString(5, e.getGender());
        psUp.setDate(6, new java.sql.Date(e.getHireDate().getTime()));

        if (e.getSalary() == null) {
            psUp.setNull(7, java.sql.Types.DECIMAL);
        } else {
            psUp.setBigDecimal(
                7,
                java.math.BigDecimal.valueOf(e.getSalary())
                    .setScale(2, java.math.RoundingMode.HALF_UP)
            );
        }

        psUp.setInt(8, e.getLocationId());
        psUp.setInt(9, e.getDepartmentId());
        psUp.setInt(10, e.getPositionId());
        psUp.setInt(11, e.getEmploymentTypeId());
        psUp.setString(12, e.getStatus());
        psUp.setInt(13, e.getEmployeeId());

        psUp.executeUpdate();

        // 3) if department changed -> update history
        if (oldDeptId != e.getDepartmentId()) {
            java.sql.Date cd = new java.sql.Date(changeDate.getTime());

            psClose = con.prepareStatement(closeCurrentHist);
            psClose.setDate(1, cd);
            psClose.setInt(2, e.getEmployeeId());
            psClose.executeUpdate();

            psIns = con.prepareStatement(insertNewHist);
            psIns.setInt(1, e.getEmployeeId());
            psIns.setInt(2, e.getDepartmentId());
            psIns.setDate(3, cd);
            psIns.executeUpdate();
        }

        con.commit();

    } catch (SQLException ex) {
        if (con != null) {
            try { con.rollback(); } catch (SQLException ignored) {}
        }
        throw ex;
    } finally {
        if (psIns != null) try { psIns.close(); } catch (SQLException ignored) {}
        if (psClose != null) try { psClose.close(); } catch (SQLException ignored) {}
        if (psUp != null) try { psUp.close(); } catch (SQLException ignored) {}
        if (psSel != null) try { psSel.close(); } catch (SQLException ignored) {}
        if (con != null) {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
            try { con.close(); } catch (SQLException ignored) {}
        }
    }
}


public List<String> getEmployeeDeleteBlockers(int employeeId) throws SQLException {
    List<String> blockers = new ArrayList<>();

    if (exists("SELECT 1 FROM dbo.EmployeeDepartmentHistory WHERE employee_id=?", employeeId))
        blockers.add("Employee has department history.");

    if (exists("SELECT 1 FROM dbo.EmployeeContact WHERE employee_id=?", employeeId))
        blockers.add("Employee has contact info.");

    if (exists("SELECT 1 FROM dbo.UserAccount WHERE employee_id=?", employeeId))
        blockers.add("Employee has a user account.");

    if (exists("SELECT 1 FROM dbo.LeaveRequest WHERE employee_id=?", employeeId))
        blockers.add("Employee has leave requests.");

    if (exists("SELECT 1 FROM dbo.LeaveRequest WHERE approved_by_employee_id=?", employeeId))
        blockers.add("Employee has approved leave requests for others.");

    if (exists("SELECT 1 FROM dbo.LeaveApproval WHERE approver_employee_id=?", employeeId))
        blockers.add("Employee has leave approvals.");

    if (exists("SELECT 1 FROM dbo.LeaveBalance WHERE employee_id=?", employeeId))
        blockers.add("Employee has leave balances.");

    if (exists("SELECT 1 FROM dbo.Department WHERE manager_employee_id=?", employeeId))
        blockers.add("Employee is assigned as a department manager.");

    return blockers;
}

private boolean exists(String sql, int id) throws SQLException {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}

public List<Object[]> getDepartmentHistoryRows(int employeeId) throws SQLException {
    String sql =
        "SELECT h.emp_dept_hist_id, d.name AS department_name, h.start_date, h.end_date " +
        "FROM dbo.EmployeeDepartmentHistory h " +
        "JOIN dbo.Department d ON d.department_id = h.department_id " +
        "WHERE h.employee_id = ? " +
        "ORDER BY h.start_date DESC";

    List<Object[]> rows = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, employeeId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("emp_dept_hist_id"),
                        rs.getString("department_name"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                });
            }
        }
    }
    return rows;
}


}
