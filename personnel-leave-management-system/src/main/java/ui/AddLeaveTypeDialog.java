package ui;

import dao.LeaveTypeDAO;
import model.LeaveType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddLeaveTypeDialog extends JDialog {

    private final LeaveTypeDAO dao = new LeaveTypeDAO();

    private JTextField txtName = new JTextField(18);
    private JTextArea txtDescription = new JTextArea(3, 18);
    private JCheckBox chkPaid = new JCheckBox("Paid leave");

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public AddLeaveTypeDialog(Frame owner) {
        super(owner, "Add Leave Type", true);
        initUI();
    }

    private void initUI() {
        setSize(480, 280);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Name"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(txtName, c);

        c.gridy = 1;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Description"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(new JScrollPane(txtDescription), c);

        c.gridy = 2;
        c.gridx = 1;
        form.add(chkPaid, c);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnSave);
        btns.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void onSave() {
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }

        LeaveType t = new LeaveType();
        t.setName(txtName.getText().trim());
        String desc = txtDescription.getText().trim();
        t.setDescription(desc.isEmpty() ? null : desc);
        t.setPaid(chkPaid.isSelected());

        btnSave.setEnabled(false);
        try {
            dao.insert(t);
            JOptionPane.showMessageDialog(this, "Leave type added successfully!");
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

        String name = txtName.getText().trim();
        if (name.isEmpty()) errors.add("Name is required.");
        else if (name.length() > 100) errors.add("Name must be at most 100 characters.");

        if (txtDescription.getText().trim().length() > 255)
            errors.add("Description must be at most 255 characters.");

        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }
}
