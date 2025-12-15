package ui;

import dao.HolidayDAO;
import model.Holiday;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HolidayFrame extends JFrame {

    private final HolidayDAO holidayDAO = new HolidayDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Name", "Recurring"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public HolidayFrame() {
        setTitle("Holidays");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JScrollPane scroll = new JScrollPane(table);

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");

        btnAdd.addActionListener(e -> onAdd());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());
        btnClose.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        buttons.add(btnClose);

        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        loadData();
    }

    private void onAdd() {
        AddHolidayDialog dlg = new AddHolidayDialog(this);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadData();
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<Holiday> list = holidayDAO.getAll();
            for (Holiday h : list) {
                model.addRow(new Object[]{
                    h.getHolidayId(),
                    h.getDate(), // ✅ correct getter
                    h.getName(),
                    h.isRecurring()
                });
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error while loading holidays:\n" + ex.getMessage(),
                    "SQLException", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int ok = JOptionPane.showConfirmDialog(this, "Delete holiday #" + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            try {
                holidayDAO.delete(id);
                loadData();
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "DB error while deleting holiday:\n" + ex.getMessage(),
                        "SQLException", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            java.util.logging.Logger.getLogger(HolidayFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HolidayFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HolidayFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HolidayFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HolidayFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
