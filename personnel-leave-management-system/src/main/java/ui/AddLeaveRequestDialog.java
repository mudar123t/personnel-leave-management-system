package ui;

import dao.LeaveRequestDAO;
import dao.LookupDAO;
import model.LookupItem;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AddLeaveRequestDialog extends JDialog {

    private final LeaveRequestDAO leaveDAO = new LeaveRequestDAO();
    private final LookupDAO lookupDAO = new LookupDAO();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private JComboBox<LookupItem> cmbEmployee = new JComboBox<>();
    private JComboBox<LookupItem> cmbLeaveType = new JComboBox<>();

    private JTextField txtStartDate = new JTextField(10);
    private JTextField txtEndDate = new JTextField(10);
    private JTextField txtTotalDays = new JTextField(6);

    private JTextArea txtReason = new JTextArea(4, 20);

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public AddLeaveRequestDialog(Frame owner) {
        super(owner, "Add Leave Request", true);
        initUI();
        loadLookups();
        setDefaults();
    }

    private void initUI() {
        
        setSize(620, 360);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        df.setLenient(false);
        txtTotalDays.setEditable(false);

        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, c, row++, "Employee", cmbEmployee, "Leave Type", cmbLeaveType);
        addRow(form, c, row++, "Start Date (yyyy-MM-dd)", txtStartDate, "End Date (yyyy-MM-dd)", txtEndDate);
        addRow(form, c, row++, "Total Days", txtTotalDays, "", new JLabel(""));

        // Reason row
        c.gridy = row++;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Reason"), c);

        c.gridx = 1; c.weightx = 1;
        c.gridwidth = 3;
        form.add(new JScrollPane(txtReason), c);
        c.gridwidth = 1;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnSave);
        btns.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        attachAutoRecalc(txtStartDate);
        attachAutoRecalc(txtEndDate);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row,
                        String label1, JComponent comp1,
                        String label2, JComponent comp2) {
        c.gridy = row;

        c.gridx = 0; c.weightx = 0;
        panel.add(new JLabel(label1), c);

        c.gridx = 1; c.weightx = 1;
        panel.add(comp1, c);

        c.gridx = 2; c.weightx = 0;
        panel.add(new JLabel(label2), c);

        c.gridx = 3; c.weightx = 1;
        panel.add(comp2, c);
    }
    
    private void attachAutoRecalc(JTextField field) {
    field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        private void update() { SwingUtilities.invokeLater(() -> recalcTotalDays()); }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    });

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override public void focusLost(java.awt.event.FocusEvent e) {
            recalcTotalDays();
        }
    });
}


    private void loadLookups() {
        try {
            fillCombo(cmbEmployee, lookupDAO.getEmployees());
            fillCombo(cmbLeaveType, lookupDAO.getLeaveTypes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load lookup data:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            dispose();
        }
    }

    private void fillCombo(JComboBox<LookupItem> combo, List<LookupItem> items) {
        DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
        for (LookupItem it : items) m.addElement(it);
        combo.setModel(m);
        if (m.getSize() > 0) combo.setSelectedIndex(0);
    }

    private void setDefaults() {
        txtStartDate.setText(df.format(new java.util.Date()));
        txtEndDate.setText(df.format(new java.util.Date()));
        recalcTotalDays();
    }

    private void recalcTotalDays() {
        try {
            java.util.Date s = parseDateStrict(txtStartDate.getText().trim(), "Start date");
            java.util.Date e = parseDateStrict(txtEndDate.getText().trim(), "End date");
            if (e.before(s)) {
                txtTotalDays.setText("");
                return;
            }
            long diffMs = e.getTime() - s.getTime();
            long days = (diffMs / (1000L * 60 * 60 * 24)) + 1; // inclusive
            txtTotalDays.setText(String.valueOf(days));
        } catch (IllegalArgumentException ex) {
            txtTotalDays.setText("");
        }
    }

    private void onSave() {
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }

        LookupItem emp = (LookupItem) cmbEmployee.getSelectedItem();
        LookupItem lt = (LookupItem) cmbLeaveType.getSelectedItem();

        java.util.Date s = parseDateStrict(txtStartDate.getText().trim(), "Start date");
        java.util.Date e = parseDateStrict(txtEndDate.getText().trim(), "End date");
        int totalDays = Integer.parseInt(txtTotalDays.getText().trim());

        btnSave.setEnabled(false);
        try {
            leaveDAO.insert(
                    emp.getId(),
                    lt.getId(),
                    new Date(System.currentTimeMillis()),
                    new Date(s.getTime()),
                    new Date(e.getTime()),
                    totalDays,
                    txtReason.getText()
            );

            JOptionPane.showMessageDialog(this, "Leave request created (Pending).");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            btnSave.setEnabled(true);
        }
    }

    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();

        if (cmbEmployee.getSelectedItem() == null) errors.add("Employee is required.");
        if (cmbLeaveType.getSelectedItem() == null) errors.add("Leave type is required.");

        java.util.Date s = null, e = null;
        try { s = parseDateStrict(txtStartDate.getText().trim(), "Start date"); }
        catch (IllegalArgumentException ex) { errors.add(ex.getMessage()); }

        try { e = parseDateStrict(txtEndDate.getText().trim(), "End date"); }
        catch (IllegalArgumentException ex) { errors.add(ex.getMessage()); }

        if (s != null && e != null && e.before(s)) errors.add("End date must be >= start date.");

        // total days must exist
        String td = txtTotalDays.getText().trim();
        if (td.isEmpty()) errors.add("Total days could not be calculated (check dates).");

        // Reason optional but you can cap it if you want
        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    private java.util.Date parseDateStrict(String s, String fieldName) {
        try {
            return df.parse(s);
        } catch (ParseException e) {
            throw new IllegalArgumentException(fieldName + " must be in format yyyy-MM-dd.");
        }
    }
}
