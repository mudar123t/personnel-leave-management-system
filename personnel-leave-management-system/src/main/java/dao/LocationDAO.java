package dao;

import model.Location;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    public List<Location> getAll() throws SQLException {
        String sql = "SELECT location_id, name, city, country, address FROM dbo.Location ORDER BY location_id";
        List<Location> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Location l = new Location();
                l.setLocationId(rs.getInt("location_id"));
                l.setName(rs.getString("name"));
                l.setCity(rs.getString("city"));
                l.setCountry(rs.getString("country"));
                l.setAddress(rs.getString("address"));
                list.add(l);
            }
        }
        return list;
    }

    public Location getById(int id) throws SQLException {
        String sql = "SELECT location_id, name, city, country, address FROM dbo.Location WHERE location_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Location l = new Location();
                l.setLocationId(rs.getInt("location_id"));
                l.setName(rs.getString("name"));
                l.setCity(rs.getString("city"));
                l.setCountry(rs.getString("country"));
                l.setAddress(rs.getString("address"));
                return l;
            }
        }
    }

    public void insert(Location l) throws SQLException {
        String sql = "INSERT INTO dbo.Location(name, city, country, address) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getName());
            ps.setString(2, l.getCity());
            ps.setString(3, l.getCountry());
            ps.setString(4, l.getAddress());
            ps.executeUpdate();
        }
    }

    public void update(Location l) throws SQLException {
        String sql = "UPDATE dbo.Location SET name=?, city=?, country=?, address=? WHERE location_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getName());
            ps.setString(2, l.getCity());
            ps.setString(3, l.getCountry());
            ps.setString(4, l.getAddress());
            ps.setInt(5, l.getLocationId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Location WHERE location_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
