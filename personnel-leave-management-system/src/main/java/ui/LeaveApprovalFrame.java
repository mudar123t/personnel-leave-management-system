package ui;

import dao.LeaveRequestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LeaveApprovalFrame extends JFrame {

    private final LeaveRequestDAO dao = new LeaveRequestDAO();
    private final int approverEmployeeId;

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"EmpId", "ID", "Employee", "Leave Type", "Request Date", "Start", "End", "Days", "Reason"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private JTable table = new JTable(model);

    private JButton btnApprove = new JButton("Approve");
    private JButton btnReject = new JButton("Reject");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public LeaveApprovalFrame(int approverEmployeeId) {
        super("Leave Approvals (Approver ID: " + approverEmployeeId + ")");
        this.approverEmployeeId = approverEmployeeId;

        initUI();
        loadPending();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hides EmpId visually

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnApprove);
        btns.add(btnReject);
        btns.add(btnRefresh);
        btns.add(btnClose);

        btnApprove.addActionListener(e -> decideSelected("Approved"));
        btnReject.addActionListener(e -> decideSelected("Rejected"));
        btnRefresh.addActionListener(e -> loadPending());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadPending() {
        try {
            model.setRowCount(0);
            List<LeaveRequestDAO.LeaveRequestRow> list = dao.getPendingWithNames();
            for (var r : list) {
                model.addRow(new Object[]{
                    r.employeeId, r.id, r.employeeName, r.leaveTypeName, r.requestDate,
                    r.startDate, r.endDate, r.totalDays, r.reason
                });
                

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load pending requests:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    

    private void decideSelected(String action) {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a request first");
        return;
    }

    int modelRow = table.convertRowIndexToModel(row);
    int employeeId = (int) model.getValueAt(modelRow, 0); // EmpId
    int requestId  = (int) model.getValueAt(modelRow, 1);

    if (employeeId == approverEmployeeId) {
        JOptionPane.showMessageDialog(this,
                "You cannot approve/reject your own leave request.",
                "Blocked", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String comment = JOptionPane.showInputDialog(
            this,
            "Optional comment (" + action + "):",
            action,
            JOptionPane.PLAIN_MESSAGE
    );
    if (comment == null) return;

    try {
        dao.decide(requestId, approverEmployeeId, action, comment);
        JOptionPane.showMessageDialog(this, "Request " + action + " successfully.");
        loadPending();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
