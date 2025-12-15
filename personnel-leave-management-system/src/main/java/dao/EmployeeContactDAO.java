package dao;

import model.EmployeeContact;
import util.DBConnection;

import java.sql.*;

public class EmployeeContactDAO {

    public EmployeeContact getByEmployeeId(int employeeId) throws SQLException {
        String sql =
            "SELECT employee_contact_id, employee_id, phone, email, address, " +
            "       emergency_contact_name, emergency_contact_phone " +
            "FROM dbo.EmployeeContact WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

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
        }
    }

    public void insert(EmployeeContact c) throws SQLException {
        String sql =
            "INSERT INTO dbo.EmployeeContact " +
            "(employee_id, phone, email, address, emergency_contact_name, emergency_contact_phone) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getEmployeeId());
            ps.setString(2, emptyToNull(c.getPhone()));
            ps.setString(3, emptyToNull(c.getEmail()));
            ps.setString(4, emptyToNull(c.getAddress()));
            ps.setString(5, emptyToNull(c.getEmergencyContactName()));
            ps.setString(6, emptyToNull(c.getEmergencyContactPhone()));

            ps.executeUpdate();
        }
    }

    public void update(EmployeeContact c) throws SQLException {
        String sql =
            "UPDATE dbo.EmployeeContact SET " +
            "phone=?, email=?, address=?, emergency_contact_name=?, emergency_contact_phone=? " +
            "WHERE employee_contact_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emptyToNull(c.getPhone()));
            ps.setString(2, emptyToNull(c.getEmail()));
            ps.setString(3, emptyToNull(c.getAddress()));
            ps.setString(4, emptyToNull(c.getEmergencyContactName()));
            ps.setString(5, emptyToNull(c.getEmergencyContactPhone()));
            ps.setInt(6, c.getEmployeeContactId());

            ps.executeUpdate();
        }
    }

    public void upsertByEmployeeId(EmployeeContact c) throws SQLException {
        EmployeeContact existing = getByEmployeeId(c.getEmployeeId());
        if (existing == null) {
            insert(c);
        } else {
            c.setEmployeeContactId(existing.getEmployeeContactId());
            update(c);
        }
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
