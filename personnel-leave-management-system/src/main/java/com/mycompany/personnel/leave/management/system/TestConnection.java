package com.mycompany.personnel.leave.management.system;

import util.DBConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("✅ Connected!");
        } catch (Exception e) {
            System.out.println("❌ Connection failed");
            e.printStackTrace();
        }
    }
}
