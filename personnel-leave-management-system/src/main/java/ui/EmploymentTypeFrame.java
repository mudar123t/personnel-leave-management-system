package ui;

import dao.EmploymentTypeDAO;
import model.EmploymentType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EmploymentTypeFrame extends JFrame {

    private final EmploymentTypeDAO dao = new EmploymentTypeDAO();

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Description"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);

    private JButton btnAdd = new JButton("Add");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnClose = new JButton("Close");

    public EmploymentTypeFrame() {
        super("Employment Types");
        initUI();
        loadTypes();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 420);
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
            List<EmploymentType> list = dao.getAll();
            for (EmploymentType t : list) {
                model.addRow(new Object[]{
                        t.getEmploymentTypeId(),
                        t.getName(),
                        t.getDescription()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employment types:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openAddDialog() {
        AddEmploymentTypeDialog dlg = new AddEmploymentTypeDialog(this);
        dlg.setVisible(true);
        loadTypes();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employment type first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = String.valueOf(model.getValueAt(row, 1));

        try {
            if (dao.hasEmployees(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete employment type \"" + name + "\".\nEmployees are assigned to it.",
                        "Delete Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete employment type \"" + name + "\"?",
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
            java.util.logging.Logger.getLogger(EmploymentTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmploymentTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmploymentTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmploymentTypeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmploymentTypeFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
