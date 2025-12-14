package ui;

import dao.LeaveTypeDAO;
import model.LeaveType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LeaveTypeFrame extends JFrame {

    private final LeaveTypeDAO dao = new LeaveTypeDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Paid"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);

    private JButton btnAdd = new JButton("Add");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public LeaveTypeFrame() {
        super("Leave Types");
        initUI();
        loadTypes();
    }

    private void initUI() {
        setSize(620, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnAdd);
        btns.add(btnDelete);
        btns.add(btnRefresh);
        btns.add(btnClose);

        btnAdd.addActionListener(e -> openAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadTypes());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadTypes() {
        try {
            model.setRowCount(0);
            List<LeaveType> list = dao.getAll();
            for (LeaveType t : list) {
                model.addRow(new Object[]{
                        t.getLeaveTypeId(),
                        t.getName(),
                        t.isPaid() ? "Yes" : "No"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leave types:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openAddDialog() {
        AddLeaveTypeDialog dlg = new AddLeaveTypeDialog(this);
        dlg.setVisible(true);
        loadTypes();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a leave type first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = String.valueOf(model.getValueAt(row, 1));

        try {
            if (dao.hasRequests(id) || dao.hasPolicies(id) || dao.hasBalances(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete leave type \"" + name + "\".\nIt is referenced by requests, policies, or balances.",
                        "Delete Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete leave type \"" + name + "\"?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            dao.delete(id);
            loadTypes();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
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
            java.util.logging.Logger.getLogger(LeaveTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LeaveTypeFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
