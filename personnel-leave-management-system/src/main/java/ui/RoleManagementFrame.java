package ui;

import dao.EmployeeDAO;
import dao.RoleDAO;
import dao.UserAccountDAO;
import model.Employee;
import model.LookupItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RoleManagementFrame extends JFrame {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final UserAccountDAO userDAO = new UserAccountDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"EmpId","Name","NationalId","CurrentRole"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private JTable table = new JTable(model);

    private JComboBox<LookupItem> cmbRoles = new JComboBox<>();
    private JButton btnAssign = new JButton("Assign Role");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public RoleManagementFrame() {
        super("Role Management (Manager)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadRoles();
        loadEmployees();
    }

    private void initUI() {
        setSize(900, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(new JLabel("Role:"));
        bottom.add(cmbRoles);
        bottom.add(btnAssign);
        bottom.add(btnRefresh);
        bottom.add(btnClose);

        btnAssign.addActionListener(e -> assignSelected());
        btnRefresh.addActionListener(e -> loadEmployees());
        btnClose.addActionListener(e -> dispose());

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadRoles() {
        try {
            DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
            for (LookupItem r : roleDAO.getAllRoles()) m.addElement(r);
            cmbRoles.setModel(m);
            if (m.getSize() > 0) cmbRoles.setSelectedIndex(0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load roles:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadEmployees() {
        try {
            model.setRowCount(0);
            List<Employee> list = employeeDAO.getAll(); // you already have this in your project
            for (Employee e : list) {
                String roleName = employeeDAO.getRoleNameForEmployee(e.getEmployeeId()); // add method below
                model.addRow(new Object[]{
                        e.getEmployeeId(),
                        e.getFirstName() + " " + e.getLastName(),
                        e.getNationalId(),
                        roleName == null ? "(no account)" : roleName
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employees:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void assignSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        LookupItem role = (LookupItem) cmbRoles.getSelectedItem();
        if (role == null) {
            JOptionPane.showMessageDialog(this, "Select a role.");
            return;
        }

        int employeeId = (int) model.getValueAt(row, 0);
        String nationalId = String.valueOf(model.getValueAt(row, 2));
        int roleId = role.getId();

        try {
            Integer userId = userDAO.getUserIdByEmployee(employeeId);

            if (userId == null) {
                // create account with default credentials
                userDAO.createAccountForEmployee(employeeId, nationalId, "1234", roleId);
                JOptionPane.showMessageDialog(this,
                        "Account created.\nUsername: " + nationalId + "\nTemp Password: 1234",
                        "Created", JOptionPane.INFORMATION_MESSAGE);
            } else {
                userDAO.updateRole(userId, roleId);
                JOptionPane.showMessageDialog(this, "Role updated successfully.");
            }

            loadEmployees();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
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
            java.util.logging.Logger.getLogger(RoleManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RoleManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RoleManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RoleManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoleManagementFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
