package ui;

import dao.RoleDAO;
import dao.UserAccountDAO;
import model.LookupItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AccountManagementFrame extends JFrame {

    private final UserAccountDAO userDao = new UserAccountDAO();
    private final RoleDAO roleDao = new RoleDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"EmpId","Employee","NationalId","UserId","Username","Role","Active"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private JTable table = new JTable(model);

    private JTextField txtUsername = new JTextField(22); // email/login
    private JPasswordField txtNewPassword = new JPasswordField(22); // for create/reset only
    private JCheckBox chkActive = new JCheckBox("Active", true);

    private JComboBox<LookupItem> cmbRole = new JComboBox<>();

    private JButton btnCreateOrUpdate = new JButton("Create / Update");
    private JButton btnResetPassword = new JButton("Reset Password");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public AccountManagementFrame() {
        super("Account Management (Manager)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadRoles();
        loadData();
    }

    private void initUI() {
        setSize(1050, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        c.gridx=0; c.gridy=row; c.weightx=0;
        form.add(new JLabel("Username (Email/Login)"), c);
        c.gridx=1; c.weightx=1;
        form.add(txtUsername, c);

        c.gridx=2; c.weightx=0;
        form.add(new JLabel("Role"), c);
        c.gridx=3; c.weightx=1;
        form.add(cmbRole, c);

        row++;

        c.gridx=0; c.gridy=row; c.weightx=0;
        form.add(new JLabel("New Password (create/reset only)"), c);
        c.gridx=1; c.weightx=1;
        form.add(txtNewPassword, c);

        c.gridx=2; c.weightx=0;
        form.add(new JLabel(""), c);
        c.gridx=3; c.weightx=1;
        form.add(chkActive, c);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnCreateOrUpdate);
        bottom.add(btnResetPassword);
        bottom.add(btnRefresh);
        bottom.add(btnClose);

        btnClose.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadData());
        btnCreateOrUpdate.addActionListener(e -> createOrUpdate());
        btnResetPassword.addActionListener(e -> resetPassword());

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(bottom, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelected();
        });
    }

    private void loadRoles() {
        try {
            DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
            for (LookupItem r : roleDao.getAllRoles()) m.addElement(r);
            cmbRole.setModel(m);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load roles:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<UserAccountDAO.EmployeeAccountRow> list = userDao.getEmployeesWithAccounts();
            for (var r : list) {
                model.addRow(new Object[]{
                        r.employeeId,
                        r.fullName,
                        r.nationalId,
                        r.userId,
                        r.username,
                        r.roleName,
                        r.active
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employees/accounts:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormFromSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        Integer userId = (Integer) model.getValueAt(row, 3);
        String username = (String) model.getValueAt(row, 4);
        Boolean active = (Boolean) model.getValueAt(row, 6);

        txtUsername.setText(username == null ? "" : username);
        chkActive.setSelected(active != null && active);

        // select role by name
        String roleName = (String) model.getValueAt(row, 5);
        selectRoleByName(roleName);

        txtNewPassword.setText(""); // never show old password (impossible)
        btnResetPassword.setEnabled(userId != null);
    }

    private void selectRoleByName(String roleName) {
        if (roleName == null) return;
        ComboBoxModel<LookupItem> m = cmbRole.getModel();
        for (int i=0; i<m.getSize(); i++) {
            LookupItem it = m.getElementAt(i);
            if (it.getName().equalsIgnoreCase(roleName)) {
                cmbRole.setSelectedIndex(i);
                return;
            }
        }
    }

    private void createOrUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        int employeeId = (int) model.getValueAt(row, 0);
        Integer userId = (Integer) model.getValueAt(row, 3);

        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username/Email is required.");
            return;
        }

        LookupItem role = (LookupItem) cmbRole.getSelectedItem();
        if (role == null) {
            JOptionPane.showMessageDialog(this, "Role is required.");
            return;
        }

        boolean active = chkActive.isSelected();

        try {
            if (userDao.usernameExistsForOther(username, userId)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Choose another.");
                return;
            }

            if (userId == null) {
                // default role if you want: EMPLOYEE
                Integer defaultEmpRole = roleDao.getEmployeeRoleId();
                int roleId = (defaultEmpRole != null ? defaultEmpRole : role.getId());

                String pass = new String(txtNewPassword.getPassword()).trim();
                if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Password is required when creating an account.");
                    return;
                }

                userDao.createAccount(employeeId, username, pass, roleId, active);
                JOptionPane.showMessageDialog(this, "Account created successfully.");
            } else {
                userDao.updateAccount(userId, username, role.getId(), active);
                JOptionPane.showMessageDialog(this, "Account updated successfully.");
            }

            loadData();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        Integer userId = (Integer) model.getValueAt(row, 3);
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "This employee has no account yet.");
            return;
        }

        String pass = new String(txtNewPassword.getPassword()).trim();
        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a new password to reset.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Reset password for this account?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            userDao.resetPassword(userId, pass);
            JOptionPane.showMessageDialog(this, "Password reset successfully.");
            txtNewPassword.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
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
            java.util.logging.Logger.getLogger(AccountManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountManagementFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AccountManagementFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
