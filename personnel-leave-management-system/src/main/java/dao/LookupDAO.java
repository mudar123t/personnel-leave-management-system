package dao;

import model.LookupItem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LookupDAO {

    public List<LookupItem> getLocations() throws SQLException {
        return load("SELECT location_id, name FROM dbo.Location ORDER BY name",
                "location_id", "name");
    }

    public List<LookupItem> getDepartments() throws SQLException {
        return load("SELECT department_id, name FROM dbo.Department ORDER BY name",
                "department_id", "name");
    }

    public List<LookupItem> getPositions() throws SQLException {
        return load("SELECT position_id, name FROM dbo.Position ORDER BY name",
                "position_id", "name");
    }

    public List<LookupItem> getEmploymentTypes() throws SQLException {
        return load("SELECT employment_type_id, name FROM dbo.EmploymentType ORDER BY name",
                "employment_type_id", "name");
    }

    private List<LookupItem> load(String sql, String idCol, String nameCol) throws SQLException {
        List<LookupItem> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LookupItem(rs.getInt(idCol), rs.getString(nameCol)));
            }
        }
        return list;
    }
    
    public List<LookupItem> getLeaveTypes() throws SQLException {
    return load("SELECT leave_type_id, name FROM dbo.LeaveType ORDER BY name",
            "leave_type_id", "name");
}
public List<LookupItem> getEmployees() throws SQLException {
    return load("SELECT employee_id, (first_name + ' ' + last_name) AS name FROM dbo.Employee ORDER BY name",
            "employee_id", "name");
}

}
