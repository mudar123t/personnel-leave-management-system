package dao;

import model.EmploymentType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmploymentTypeDAO {

    public List<EmploymentType> getAll() throws SQLException {
        String sql = "SELECT employment_type_id, name, description FROM dbo.EmploymentType ORDER BY employment_type_id";
        List<EmploymentType> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmploymentType e = new EmploymentType();
                e.setEmploymentTypeId(rs.getInt("employment_type_id"));
                e.setName(rs.getString("name"));
                e.setDescription(rs.getString("description"));
                list.add(e);
            }
        }
        return list;
    }

    public EmploymentType getById(int id) throws SQLException {
        String sql = "SELECT employment_type_id, name, description FROM dbo.EmploymentType WHERE employment_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                EmploymentType e = new EmploymentType();
                e.setEmploymentTypeId(rs.getInt("employment_type_id"));
                e.setName(rs.getString("name"));
                e.setDescription(rs.getString("description"));
                return e;
            }
        }
    }

    public void insert(EmploymentType e) throws SQLException {
        String sql = "INSERT INTO dbo.EmploymentType(name, description) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(EmploymentType e) throws SQLException {
        String sql = "UPDATE dbo.EmploymentType SET name=?, description=? WHERE employment_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getEmploymentTypeId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.EmploymentType WHERE employment_type_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
