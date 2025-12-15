package ui;

import dao.LeavePolicyDAO;
import model.LeavePolicy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LeavePolicyFrame extends JFrame {

    private final LeavePolicyDAO dao = new LeavePolicyDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "LeaveTypeId", "EmploymentTypeId", "AnnualQuota", "MaxConsecutive", "MinServiceMonths"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private JTable table = new JTable(model);

    private JButton btnAdd = new JButton("Add");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public LeavePolicyFrame() {
        super("Leave Policies");
        initUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadPolicies();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 420);
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
        btnRefresh.addActionListener(e -> loadPolicies());
        btnClose.addActionListener(e -> dispose());

        add(btns, BorderLayout.SOUTH);
    }

    private void loadPolicies() {
        try {
            model.setRowCount(0);
            List<LeavePolicy> list = dao.getAll();

            for (LeavePolicy p : list) {
                model.addRow(new Object[]{
                    p.getLeavePolicyId(),
                    p.getLeaveTypeId(),
                    p.getEmploymentTypeId(),
                    p.getAnnualQuotaDays(),
                    (p.getMaxConsecutiveDays() == null ? "" : p.getMaxConsecutiveDays()),
                    (p.getMinServiceMonthsRequired() == null ? "" : p.getMinServiceMonthsRequired())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leave policies:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openAddDialog() {
        AddLeavePolicyDialog dlg = new AddLeavePolicyDialog(this);
        dlg.setVisible(true);
        loadPolicies();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a leave policy first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete policy ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.delete(id);
            loadPolicies();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LeavePolicyFrame().setVisible(true));
    }

    @SuppressWarnings("unchecked")
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
