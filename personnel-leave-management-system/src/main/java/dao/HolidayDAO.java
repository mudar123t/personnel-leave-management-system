package dao;

import model.Holiday;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAO {

    public List<Holiday> getAll() throws SQLException {
        String sql = "SELECT holiday_id, [date], name, is_recurring FROM dbo.Holiday ORDER BY [date]";
        List<Holiday> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Holiday h = new Holiday();
                h.setHolidayId(rs.getInt("holiday_id"));
                h.setDate(rs.getDate("date"));
                h.setName(rs.getString("name"));
                h.setRecurring(rs.getBoolean("is_recurring"));
                list.add(h);
            }
        }
        return list;
    }

    public Holiday getById(int id) throws SQLException {
        String sql = "SELECT holiday_id, [date], name, is_recurring FROM dbo.Holiday WHERE holiday_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Holiday h = new Holiday();
                h.setHolidayId(rs.getInt("holiday_id"));
                h.setDate(rs.getDate("date"));
                h.setName(rs.getString("name"));
                h.setRecurring(rs.getBoolean("is_recurring"));
                return h;
            }
        }
    }

    public void insert(Holiday h) throws SQLException {
        String sql = "INSERT INTO dbo.Holiday([date], name, is_recurring) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(h.getDate().getTime()));
            ps.setString(2, h.getName());
            ps.setBoolean(3, h.isRecurring());
            ps.executeUpdate();
        }
    }

    public void update(Holiday h) throws SQLException {
        String sql = "UPDATE dbo.Holiday SET [date]=?, name=?, is_recurring=? WHERE holiday_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(h.getDate().getTime()));
            ps.setString(2, h.getName());
            ps.setBoolean(3, h.isRecurring());
            ps.setInt(4, h.getHolidayId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Holiday WHERE holiday_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
