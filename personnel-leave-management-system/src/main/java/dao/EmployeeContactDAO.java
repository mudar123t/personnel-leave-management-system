package dao;

import model.EmployeeContact;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeContactDAO {

    private EmployeeContact mapRow(ResultSet rs) throws SQLException {
        EmployeeContact c = new EmployeeContact();
        c.setEmployeeContactId(rs.getInt("employee_contact_id"));
        c.setEmployeeId(rs.getInt("employee_id"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setEmergencyContactName(rs.getString("emergency_contact_name"));
        c.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        return c;
    }

    public List<EmployeeContact> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.EmployeeContact ORDER BY employee_contact_id";
        List<EmployeeContact> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public EmployeeContact getById(int id) throws SQLException {
        String sql = "SELECT * FROM dbo.EmployeeContact WHERE employee_contact_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public EmployeeContact getByEmployeeId(int employeeId) throws SQLException {
        String sql = "SELECT TOP 1 * FROM dbo.EmployeeContact WHERE employee_id=? ORDER BY employee_contact_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(EmployeeContact c) throws SQLException {
        String sql = "INSERT INTO dbo.EmployeeContact(employee_id, phone, email, address, emergency_contact_name, emergency_contact_phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getEmployeeId());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getEmergencyContactName());
            ps.setString(6, c.getEmergencyContactPhone());
            ps.executeUpdate();
        }
    }

    public void update(EmployeeContact c) throws SQLException {
        String sql = "UPDATE dbo.EmployeeContact SET employee_id=?, phone=?, email=?, address=?, emergency_contact_name=?, emergency_contact_phone=? " +
                     "WHERE employee_contact_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getEmployeeId());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getEmergencyContactName());
            ps.setString(6, c.getEmergencyContactPhone());
            ps.setInt(7, c.getEmployeeContactId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.EmployeeContact WHERE employee_contact_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void deleteByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM dbo.EmployeeContact WHERE employee_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }
}
