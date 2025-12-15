package ui;

import dao.LeaveRequestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LeaveRequestFrame extends JFrame {

    private final LeaveRequestDAO dao = new LeaveRequestDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Employee","Leave Type","Request Date","Start","End","Days","Status"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);

    private JButton btnAdd = new JButton("New Request");
    private JButton btnCancelReq = new JButton("Cancel Request");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public LeaveRequestFrame() {
        super("Leave Requests");
        initUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadRequests();
    }

    private void initUI() {
        setSize(980, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnHistory = new JButton("Decisions History");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnAdd);
        btns.add(btnCancelReq);
        btns.add(btnRefresh);
        btns.add(btnHistory);
        btns.add(btnClose);

        btnHistory.addActionListener(e -> new LeaveDecisionHistoryFrame().setVisible(true));
        btnAdd.addActionListener(e -> openAddDialog());
        btnCancelReq.addActionListener(e -> cancelSelected());
        btnRefresh.addActionListener(e -> loadRequests());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadRequests() {
        try {
            model.setRowCount(0);
            List<LeaveRequestDAO.LeaveRequestRow> list = dao.getAllWithNames();
            for (var r : list) {
                model.addRow(new Object[]{
                        r.id,
                        r.employeeName,
                        r.leaveTypeName,
                        r.requestDate,
                        r.startDate,
                        r.endDate,
                        r.totalDays,
                        r.status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leave requests:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openAddDialog() {
        AddLeaveRequestDialog dlg = new AddLeaveRequestDialog(this);
        dlg.setVisible(true);
        loadRequests();
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String status = String.valueOf(model.getValueAt(row, 7));
        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only Pending requests can be canceled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel this leave request?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.cancel(id);
            loadRequests();
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
            java.util.logging.Logger.getLogger(LeaveRequestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveRequestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveRequestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveRequestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LeaveRequestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
