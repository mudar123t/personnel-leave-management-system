package ui;

import dao.HolidayDAO;
import model.Holiday;

import javax.swing.*;
import java.awt.*;

public class AddHolidayDialog extends JDialog {

    private final HolidayDAO holidayDAO = new HolidayDAO();
    private boolean saved = false;

    private final JTextField txtDate = new JTextField(12); // YYYY-MM-DD
    private final JTextField txtName = new JTextField(20);
    private final JCheckBox chkRecurring = new JCheckBox("Recurring yearly");

    public AddHolidayDialog(Frame owner) {
        super(owner, "Add Holiday", true);
        setSize(420, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(txtDate);
        form.add(new JLabel("Name:"));
        form.add(txtName);
        form.add(new JLabel(""));
        form.add(chkRecurring);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void onSave() {
        String dateStr = txtDate.getText().trim();
        String name = txtName.getText().trim();

        if (dateStr.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and Name are required.");
            return;
        }

        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(dateStr); // expects YYYY-MM-DD
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        Holiday h = new Holiday();
        h.setDate(sqlDate);                       // ✅ correct setter
        h.setName(name);
        h.setRecurring(chkRecurring.isSelected());

        try {
            holidayDAO.insert(h);                 // ✅ handle SQLException
            saved = true;
            dispose();
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error while saving holiday:\n" + ex.getMessage(),
                    "SQLException", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
