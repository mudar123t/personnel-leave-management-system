package ui;

import dao.PositionDAO;
import model.Position;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddPositionDialog extends JDialog {

    private final PositionDAO positionDAO = new PositionDAO();

    private JTextField txtName = new JTextField(18);
    private JTextArea txtDescription = new JTextArea(3, 18);
    private JTextField txtMinSalary = new JTextField(10);
    private JTextField txtMaxSalary = new JTextField(10);

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public AddPositionDialog(Frame owner) {
        super(owner, "Add Position", true);
        initUI();
    }

    private void initUI() {
        setSize(500, 320);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, c, row++, "Name", txtName);

        c.gridy = row++;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Description"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(new JScrollPane(txtDescription), c);

        addRow(form, c, row++, "Min Salary", txtMinSalary);
        addRow(form, c, row++, "Max Salary", txtMaxSalary);

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

    private void onSave() {
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }

        Position p = new Position();
        p.setName(txtName.getText().trim());
        p.setDescription(emptyToNull(txtDescription.getText()));

        p.setMinSalary(parseSalary(txtMinSalary.getText()));
        p.setMaxSalary(parseSalary(txtMaxSalary.getText()));
        
        
        btnSave.setEnabled(false);
        try {
            positionDAO.insert(p);
            JOptionPane.showMessageDialog(this, "Position added successfully!");
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

        Double min = tryParseSalary(txtMinSalary.getText(), "Min salary", errors);
        Double max = tryParseSalary(txtMaxSalary.getText(), "Max salary", errors);

        if (min != null && max != null && min > max)
            errors.add("Min salary cannot be greater than max salary.");


        return errors;
    }

    private Double tryParseSalary(String s, String field, List<String> errors) {
    s = s.trim();
    if (s.isEmpty()) return null;

    if (!s.matches("\\d+(\\.\\d{1,2})?")) {
        errors.add(field + " must be a number with up to 2 decimals.");
        return null;
    }

    try {
        double v = Double.parseDouble(s);
        if (v < 0) errors.add(field + " cannot be negative.");
        // DECIMAL(10,2) max 99999999.99
        if (v > 99999999.99) errors.add(field + " is too large.");
        return v;
    } catch (NumberFormatException ex) {
        errors.add(field + " must be a valid number.");
        return null;
    }
    }

    private Double parseSalary(String s) {
        s = s.trim();
        return s.isEmpty() ? null : Double.parseDouble(s);
    }


    private String emptyToNull(String s) {
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }
}
