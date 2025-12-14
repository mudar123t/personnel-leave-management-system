package dao;

import model.Position;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    public List<Position> getAll() throws SQLException {
        String sql = "SELECT position_id, name, description, min_salary, max_salary FROM dbo.Position ORDER BY position_id";
        List<Position> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Position p = new Position();
                p.setPositionId(rs.getInt("position_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));

                var min = rs.getBigDecimal("min_salary");
                var max = rs.getBigDecimal("max_salary");
                p.setMinSalary(min == null ? null : min.doubleValue());
                p.setMaxSalary(max == null ? null : max.doubleValue());

                list.add(p);
            }
        }
        return list;
    }

    public Position getById(int id) throws SQLException {
        String sql = "SELECT position_id, name, description, min_salary, max_salary FROM dbo.Position WHERE position_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Position p = new Position();
                p.setPositionId(rs.getInt("position_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                var min = rs.getBigDecimal("min_salary");
                var max = rs.getBigDecimal("max_salary");
                p.setMinSalary(min == null ? null : min.doubleValue());
                p.setMaxSalary(max == null ? null : max.doubleValue());
                return p;
            }
        }
    }

    public void insert(Position p) throws SQLException {
        String sql = "INSERT INTO dbo.Position(name, description, min_salary, max_salary) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            if (p.getMinSalary() == null) ps.setNull(3, Types.DECIMAL); else ps.setBigDecimal(3, new java.math.BigDecimal(p.getMinSalary()));
            if (p.getMaxSalary() == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, new java.math.BigDecimal(p.getMaxSalary()));
            ps.executeUpdate();
        }
    }

    public void update(Position p) throws SQLException {
        String sql = "UPDATE dbo.Position SET name=?, description=?, min_salary=?, max_salary=? WHERE position_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            if (p.getMinSalary() == null) ps.setNull(3, Types.DECIMAL); else ps.setBigDecimal(3, new java.math.BigDecimal(p.getMinSalary()));
            if (p.getMaxSalary() == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, new java.math.BigDecimal(p.getMaxSalary()));
            ps.setInt(5, p.getPositionId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Position WHERE position_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
