
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import util.DBConnection;


public class LookupItem {
    
    private int id;
    private String name;

    public LookupItem(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
    
    
//    public void delete(int employeeId) throws SQLException {
//    String sql = "DELETE FROM dbo.Employee WHERE employee_id = ?";
//    try (Connection con = DBConnection.getConnection();
//         PreparedStatement ps = con.prepareStatement(sql)) {
//        ps.setInt(1, employeeId);
//        ps.executeUpdate();
//    }
//}

}
