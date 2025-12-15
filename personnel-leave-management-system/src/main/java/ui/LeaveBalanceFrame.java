package ui;

import dao.LeaveBalanceDAO;
import dao.LookupDAO;
import model.LookupItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LeaveBalanceFrame extends JFrame {

    private final LeaveBalanceDAO dao = new LeaveBalanceDAO();
    private final LookupDAO lookupDAO = new LookupDAO();

    private JComboBox<LookupItem> cmbEmployee = new JComboBox<>();
    private JComboBox<LookupItem> cmbLeaveType = new JComboBox<>();
    private JTextField txtYear = new JTextField(6);

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"BalanceID","EmpID","Employee","LeaveTypeID","Leave Type","Year","Allocated","Used","Remaining"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);

    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnEnsureRow = new JButton("Ensure Row");
    private JButton btnSync = new JButton("Sync From Policy");
    private JButton btnClose = new JButton("Close");

    public LeaveBalanceFrame() {
        super("Leave Balances");
        initUI();
        loadLookups();
        loadData();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top filters
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.add(new JLabel("Employee"));
        top.add(cmbEmployee);

        top.add(new JLabel("Leave Type"));
        top.add(cmbLeaveType);

        top.add(new JLabel("Year"));
        txtYear.setText(String.valueOf(LocalDate.now().getYear()));
        top.add(txtYear);

        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> loadData());
        top.add(btnFilter);

        add(top, BorderLayout.NORTH);

        // Table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnEnsureRow);
        btns.add(btnSync);
        btns.add(btnRefresh);
        btns.add(btnClose);

        btnEnsureRow.addActionListener(e -> ensureSelectedRow());
        btnSync.addActionListener(e -> syncSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadLookups() {
        try {
            fillComboWithAll(cmbEmployee, lookupDAO.getEmployees());
            fillComboWithAll(cmbLeaveType, lookupDAO.getLeaveTypes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load lookups:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            dispose();
        }
    }

    private void fillComboWithAll(JComboBox<LookupItem> combo, List<LookupItem> items) {
        DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
        m.addElement(new LookupItem(-1, "All"));
        for (LookupItem it : items) m.addElement(it);
        combo.setModel(m);
        combo.setSelectedIndex(0);
    }

    private Integer parseYearOrNull() {
        String s = txtYear.getText().trim();
        if (s.isEmpty()) return null;
        if (!s.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Year must be 4 digits (e.g., 2025).");
            return null;
        }
        return Integer.parseInt(s);
    }

    private void loadData() {
        try {
            model.setRowCount(0);

            LookupItem emp = (LookupItem) cmbEmployee.getSelectedItem();
            LookupItem lt  = (LookupItem) cmbLeaveType.getSelectedItem();

            Integer employeeId = (emp == null || emp.getId() == -1) ? null : emp.getId();
            Integer leaveTypeId = (lt == null || lt.getId() == -1) ? null : lt.getId();
            Integer year = parseYearOrNull();

            List<LeaveBalanceDAO.LeaveBalanceRow> list = dao.search(employeeId, leaveTypeId, year);

            for (var r : list) {
                model.addRow(new Object[]{
                        r.leaveBalanceId,
                        r.employeeId,
                        r.employeeName,
                        r.leaveTypeId,
                        r.leaveTypeName,
                        r.year,
                        r.totalAllocatedDays,
                        r.usedDays,
                        r.remainingDays
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load balances:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void ensureSelectedRow() {
        // Uses the filters to ensure a row exists (employee + leave type + year must be chosen)
        LookupItem emp = (LookupItem) cmbEmployee.getSelectedItem();
        LookupItem lt  = (LookupItem) cmbLeaveType.getSelectedItem();
        Integer year = parseYearOrNull();

        if (emp == null || emp.getId() == -1) {
            JOptionPane.showMessageDialog(this, "Select a specific Employee (not All).");
            return;
        }
        if (lt == null || lt.getId() == -1) {
            JOptionPane.showMessageDialog(this, "Select a specific Leave Type (not All).");
            return;
        }
        if (year == null) return;

        try {
            dao.ensureBalanceRowExists(emp.getId(), lt.getId(), year);
            JOptionPane.showMessageDialog(this, "Balance row ensured (created if missing).");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void syncSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a balance row first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int employeeId = (int) model.getValueAt(modelRow, 1);
        int leaveTypeId = (int) model.getValueAt(modelRow, 3);
        int year = (int) model.getValueAt(modelRow, 5);

        try {
            dao.syncAllocatedFromPolicy(employeeId, leaveTypeId, year);
            JOptionPane.showMessageDialog(this, "Synced allocated/remaining from policy.");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error:\n" + ex.getMessage(),
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
            java.util.logging.Logger.getLogger(LeaveBalanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveBalanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveBalanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveBalanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LeaveBalanceFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
