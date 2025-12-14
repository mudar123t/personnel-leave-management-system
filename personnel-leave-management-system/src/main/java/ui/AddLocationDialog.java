package ui;

import dao.LocationDAO;
import model.Location;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddLocationDialog extends JDialog {

    private final LocationDAO locationDAO = new LocationDAO();

    private JTextField txtName = new JTextField(18);
    private JTextField txtCity = new JTextField(18);
    private JTextField txtCountry = new JTextField(18);
    private JTextArea txtAddress = new JTextArea(3, 18);

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    public AddLocationDialog(Frame owner) {
        super(owner, "Add Location", true);
        initUI();
    }

    private void initUI() {
        setSize(460, 280);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, c, row++, "Name", txtName);
        addRow(form, c, row++, "City", txtCity);
        addRow(form, c, row++, "Country", txtCountry);

        c.gridy = row;
        c.gridx = 0; c.weightx = 0;
        form.add(new JLabel("Address"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(new JScrollPane(txtAddress), c);

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

        Location l = new Location();
        l.setName(txtName.getText().trim());
        l.setCity(txtCity.getText().trim());
        l.setCountry(txtCountry.getText().trim());
        l.setAddress(txtAddress.getText().trim());

        btnSave.setEnabled(false);
        try {
            locationDAO.insert(l);
            JOptionPane.showMessageDialog(this, "Location added successfully!");
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

        if (txtName.getText().trim().isEmpty())
            errors.add("Name is required.");
        else if (txtName.getText().trim().length() > 100)
            errors.add("Name must be at most 100 characters.");

        if (txtCity.getText().trim().isEmpty())
            errors.add("City is required.");
        else if (txtCity.getText().trim().length() > 100)
            errors.add("City must be at most 100 characters.");

        if (txtCountry.getText().trim().isEmpty())
            errors.add("Country is required.");
        else if (txtCountry.getText().trim().length() > 100)
            errors.add("Country must be at most 100 characters.");

        if (txtAddress.getText().trim().length() > 255)
            errors.add("Address must be at most 255 characters.");

        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }
}
