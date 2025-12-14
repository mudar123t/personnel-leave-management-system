/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;
import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import ui.AddEmployeeDialog;

/**
 *
 * @author shawa
 */
public class EmployeeTableFrame extends javax.swing.JFrame {

     private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private JTable table;
    private DefaultTableModel model;
    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;


    public EmployeeTableFrame() {
        setTitle("Employees");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 500);
        setLocationRelativeTo(null);

        initUI();
        loadEmployees();
    }

    private void initUI() {
        String[] cols = {
            "ID", "First Name", "Last Name", "National ID",
            "Birth Date", "Gender", "Hire Date", "Salary",
            "Location", "Department", "Position", "Employment Type",
            "Status"
        };
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnRefresh = new JButton("Refresh");
        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");

        btnRefresh.addActionListener(e -> loadEmployees());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedEmployee());

        topPanel.add(btnRefresh);
        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);



        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        table = new JTable(model);
        table.setAutoCreateRowSorter(true); // enable sorting
        table.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(table);

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadEmployees());

        topPanel.add(btnRefresh);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void openAddDialog() {
    AddEmployeeDialog dialog = new AddEmployeeDialog(this);
    dialog.setVisible(true);
    loadEmployees();
}

private void openEditDialog() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select an employee first");
        return;
    }

    int modelRow = table.convertRowIndexToModel(row);
    int employeeId = (int) model.getValueAt(modelRow, 0);

    AddEmployeeDialog dialog = new AddEmployeeDialog(this, employeeId);
    dialog.setVisible(true);
    loadEmployees();
}

private void deleteSelectedEmployee() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select an employee first");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this employee?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    int modelRow = table.convertRowIndexToModel(row);
    int employeeId = (int) model.getValueAt(modelRow, 0);

    try {
        employeeDAO.delete(employeeId);
        loadEmployees();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
        ex.printStackTrace();
    }
}

    
    private void loadEmployees() {
    model.setRowCount(0);

    try {
        List<Object[]> rows = employeeDAO.getAllWithNames();
        for (Object[] r : rows) {
            model.addRow(r);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Failed to load employees:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
        ex.printStackTrace();
    }
}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EmployeeTableFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeeTableFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeeTableFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeeTableFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmployeeTableFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
