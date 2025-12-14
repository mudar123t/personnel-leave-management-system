package dao;

import model.Role;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> getAll() throws SQLException {
        String sql = "SELECT role_id, role_name, description FROM dbo.Role ORDER BY role_id";
        List<Role> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("role_id"));
                r.setRoleName(rs.getString("role_name"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        }
        return list;
    }

    public Role getById(int id) throws SQLException {
        String sql = "SELECT role_id, role_name, description FROM dbo.Role WHERE role_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Role r = new Role();
                r.setRoleId(rs.getInt("role_id"));
                r.setRoleName(rs.getString("role_name"));
                r.setDescription(rs.getString("description"));
                return r;
            }
        }
    }

    public void insert(Role r) throws SQLException {
        String sql = "INSERT INTO dbo.Role(role_name, description) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setString(2, r.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Role r) throws SQLException {
        String sql = "UPDATE dbo.Role SET role_name=?, description=? WHERE role_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setString(2, r.getDescription());
            ps.setInt(3, r.getRoleId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Role WHERE role_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
