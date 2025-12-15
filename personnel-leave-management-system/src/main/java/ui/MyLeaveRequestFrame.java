package ui;

import dao.LeaveRequestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class MyLeaveRequestFrame extends JFrame {

    private final int employeeId;
    private final LeaveRequestDAO dao = new LeaveRequestDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Leave Type","Request Date","Start","End","Days","Status"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private JTable table = new JTable(model);

    private JButton btnNew = new JButton("New Request");
    private JButton btnCancel = new JButton("Cancel Pending");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public MyLeaveRequestFrame(int employeeId) {
        super("My Leave Requests (Employee ID: " + employeeId + ")");
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadMine();
    }

    private void initUI() {
        setSize(900, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnNew);
        btns.add(btnCancel);
        btns.add(btnRefresh);
        btns.add(btnClose);

        btnNew.addActionListener(e -> openNew());
        btnCancel.addActionListener(e -> cancelSelected());
        btnRefresh.addActionListener(e -> loadMine());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadMine() {
        try {
            model.setRowCount(0);
            for (var r : dao.getByEmployee(employeeId)) {
                model.addRow(new Object[]{
                        r.id, r.leaveTypeName, r.requestDate, r.startDate, r.endDate, r.totalDays, r.status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load your requests:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openNew() {
        AddLeaveRequestDialog dlg = new AddLeaveRequestDialog(this);

        // IMPORTANT: lock employee selection to this employee
        dlg.lockEmployee(employeeId); // we add this method below

        dlg.setVisible(true);
        loadMine();
    }
    
    



    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String status = String.valueOf(model.getValueAt(row, 6));
        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only Pending requests can be canceled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this request?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.cancel(id);
            loadMine();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
