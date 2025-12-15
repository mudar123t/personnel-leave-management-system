package dao;

import model.LookupItem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<LookupItem> getAllRoles() throws SQLException {
        String sql = "SELECT role_id, role_name FROM dbo.Role ORDER BY role_id";
        List<LookupItem> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LookupItem(rs.getInt("role_id"), rs.getString("role_name")));
            }
        }
        return list;
    }

    public Integer getEmployeeRoleId() throws SQLException {
        String sql = "SELECT role_id FROM dbo.Role WHERE LOWER(role_name)='employee'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }
}
