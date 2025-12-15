package ui;

import dao.EmployeeContactDAO;
import model.EmployeeContact;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeContactDialog extends JDialog {

    private final EmployeeContactDAO contactDAO = new EmployeeContactDAO();
    private final int employeeId;

    private JTextField txtPhone = new JTextField(18);
    private JTextField txtEmail = new JTextField(18);
    private JTextArea txtAddress = new JTextArea(3, 18);
    private JTextField txtEmergencyName = new JTextField(18);
    private JTextField txtEmergencyPhone = new JTextField(18);

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public EmployeeContactDialog(Frame owner, int employeeId) {
        super(owner, "Employee Contact", true);
        this.employeeId = employeeId;

        initUI();
        loadContact();
    }

    private void initUI() {
        setSize(520, 340);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, c, row++, "Phone", txtPhone);
        addRow(form, c, row++, "Email", txtEmail);

        // Address as textarea
        c.gridy = row++;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Address"), c);

        c.gridx = 1; c.weightx = 1;
        JScrollPane sp = new JScrollPane(txtAddress);
        form.add(sp, c);

        addRow(form, c, row++, "Emergency Name", txtEmergencyName);
        addRow(form, c, row++, "Emergency Phone", txtEmergencyPhone);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnSave);
        btns.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent comp) {
        c.gridy = row;

        c.gridx = 0; c.weightx = 0;
        panel.add(new JLabel(label), c);

        c.gridx = 1; c.weightx = 1;
        panel.add(comp, c);
    }

    private void loadContact() {
        try {
            EmployeeContact existing = contactDAO.getByEmployeeId(employeeId);
            if (existing == null) return;

            txtPhone.setText(nullToEmpty(existing.getPhone()));
            txtEmail.setText(nullToEmpty(existing.getEmail()));
            txtAddress.setText(nullToEmpty(existing.getAddress()));
            txtEmergencyName.setText(nullToEmpty(existing.getEmergencyContactName()));
            txtEmergencyPhone.setText(nullToEmpty(existing.getEmergencyContactPhone()));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load contact:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            dispose();
        }
    }

    private void onSave() {
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }

        EmployeeContact c = new EmployeeContact();
        c.setEmployeeId(employeeId);
        c.setPhone(txtPhone.getText());
        c.setEmail(txtEmail.getText());
        c.setAddress(txtAddress.getText());
        c.setEmergencyContactName(txtEmergencyName.getText());
        c.setEmergencyContactPhone(txtEmergencyPhone.getText());

        btnSave.setEnabled(false);
        try {
            contactDAO.upsertByEmployeeId(c);
            JOptionPane.showMessageDialog(this, "Contact saved.");
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

        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String emergencyPhone = txtEmergencyPhone.getText().trim();

        // Phone rules: optional, but if present max 20 and allowed chars
        if (!phone.isEmpty()) {
            if (phone.length() > 20) errors.add("Phone must be at most 20 characters.");
            if (!phone.matches("[0-9+\\-()\\s]{5,20}")) errors.add("Phone format looks invalid.");
        }

        // Email rules: optional, but if present max 150 and basic format
        if (!email.isEmpty()) {
            if (email.length() > 150) errors.add("Email must be at most 150 characters.");
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) errors.add("Email format is invalid.");
        }

        // Address max 255
        if (txtAddress.getText().trim().length() > 255) errors.add("Address must be at most 255 characters.");

        // Emergency name max 150
        if (txtEmergencyName.getText().trim().length() > 150) errors.add("Emergency name must be at most 150 characters.");

        // Emergency phone rules: optional, max 20
        if (!emergencyPhone.isEmpty()) {
            if (emergencyPhone.length() > 20) errors.add("Emergency phone must be at most 20 characters.");
            if (!emergencyPhone.matches("[0-9+\\-()\\s]{5,20}")) errors.add("Emergency phone format looks invalid.");
        }

        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
