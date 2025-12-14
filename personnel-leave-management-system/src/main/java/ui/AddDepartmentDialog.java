package ui;

import dao.DepartmentDAO;
import model.Department;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddDepartmentDialog extends JDialog {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    private JTextField txtName = new JTextField(18);
    private JTextField txtCode = new JTextField(10);

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public AddDepartmentDialog(Frame owner) {
        super(owner, "Add Department", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 200);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

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
        form.add(new JLabel("Code"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(txtCode, c);

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

        String name = txtName.getText().trim();
        String code = txtCode.getText().trim().toUpperCase();

        btnSave.setEnabled(false);
        try {
            // ✅ code uniqueness check before hitting insert
            if (departmentDAO.existsCode(code)) {
                showErrors(List.of("Department code already exists. Choose a different code."));
                return;
            }

            Department d = new Department();
            d.setName(name);
            d.setCode(code);
            d.setManagerEmployeeId(null); // keep simple

            departmentDAO.insert(d);
            JOptionPane.showMessageDialog(this, "Department added successfully!");
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
        String code = txtCode.getText().trim().toUpperCase();

        if (name.isEmpty()) errors.add("Name is required.");
        else if (name.length() > 100) errors.add("Name must be at most 100 characters.");

        if (code.isEmpty()) errors.add("Code is required.");
        else if (code.length() > 20) errors.add("Code must be at most 20 characters.");
        else if (code.contains(" ")) errors.add("Code must not contain spaces.");

        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }
}
