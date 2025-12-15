package ui;

import dao.EmployeeDAO;
import dao.LookupDAO;
import model.Employee;
import model.LookupItem;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddEmployeeDialog extends JDialog {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final LookupDAO lookupDAO = new LookupDAO();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private Integer editingEmployeeId; // null = add mode

    // Fields
    private JTextField txtFirstName = new JTextField(18);
    private JTextField txtLastName = new JTextField(18);
    private JTextField txtNationalId = new JTextField(18);
    private JTextField txtBirthDate = new JTextField(10); // yyyy-MM-dd
    private JComboBox<String> cmbGender = new JComboBox<>(new String[]{"", "M", "F", "O"});
    private JTextField txtHireDate = new JTextField(10);  // yyyy-MM-dd
    private JTextField txtSalary = new JTextField(10);

    private JComboBox<LookupItem> cmbLocation = new JComboBox<>();
    private JComboBox<LookupItem> cmbDepartment = new JComboBox<>();
    private JComboBox<LookupItem> cmbPosition = new JComboBox<>();
    private JComboBox<LookupItem> cmbEmploymentType = new JComboBox<>();

    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
        "Active", "On Leave", "Resigned"
    });

    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    // ADD mode
    public AddEmployeeDialog(Frame owner) {
        super(owner, "Add Employee", true);
        this.editingEmployeeId = null;
        init();
        loadLookups();
        setDefaultsForAdd();
    }

    // EDIT mode
    public AddEmployeeDialog(Frame owner, int employeeId) {
        super(owner, "Edit Employee", true);
        this.editingEmployeeId = employeeId;
        init();
        loadLookups();
        loadEmployee(employeeId);
    }

    private void init() {
        setSize(650, 420);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, c, row++, "First Name", txtFirstName, "Last Name", txtLastName);
        addRow(form, c, row++, "National ID", txtNationalId, "Gender", cmbGender);
        addRow(form, c, row++, "Birth Date (yyyy-MM-dd)", txtBirthDate, "Hire Date (yyyy-MM-dd)", txtHireDate);
        addRow(form, c, row++, "Salary", txtSalary, "Status", cmbStatus);

        addRow(form, c, row++, "Location", cmbLocation, "Department", cmbDepartment);
        addRow(form, c, row++, "Position", cmbPosition, "Employment Type", cmbEmploymentType);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(btnSave);
        btns.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row,
            String label1, JComponent comp1,
            String label2, JComponent comp2) {

        c.gridy = row;

        c.gridx = 0;
        c.weightx = 0;
        panel.add(new JLabel(label1), c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(comp1, c);

        c.gridx = 2;
        c.weightx = 0;
        panel.add(new JLabel(label2), c);

        c.gridx = 3;
        c.weightx = 1;
        panel.add(comp2, c);
    }

    private void loadLookups() {
        try {
            fillCombo(cmbLocation, lookupDAO.getLocations(), "-- Select Location --");
            fillCombo(cmbDepartment, lookupDAO.getDepartments(), "-- Select Department --");
            fillCombo(cmbPosition, lookupDAO.getPositions(), "-- Select Position --");
            fillCombo(cmbEmploymentType, lookupDAO.getEmploymentTypes(), "-- Select Employment Type --");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load lookup data:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void fillCombo(JComboBox<LookupItem> combo, List<LookupItem> items) {
        DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
        for (LookupItem it : items) {
            m.addElement(it);
        }
        combo.setModel(m);
        if (m.getSize() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private void fillCombo(JComboBox<LookupItem> combo, List<LookupItem> items, String placeholder) {
        DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
        m.addElement(new LookupItem(-1, placeholder)); // placeholder (invalid id)
        for (LookupItem it : items) {
            m.addElement(it);
        }
        combo.setModel(m);
        combo.setSelectedIndex(0);
    }

    private void setDefaultsForAdd() {
        df.setLenient(false);
        txtBirthDate.setText("2000-01-01");
        txtHireDate.setText(df.format(new Date()));
        txtSalary.setText("");
        cmbGender.setSelectedItem("");
        cmbStatus.setSelectedItem("Active");
    }

    private void loadEmployee(int employeeId) {
        try {
            Employee e = employeeDAO.getById(employeeId);
            if (e == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            txtFirstName.setText(e.getFirstName());
            txtLastName.setText(e.getLastName());
            txtNationalId.setText(e.getNationalId());
            txtBirthDate.setText(df.format(e.getBirthDate()));
            cmbGender.setSelectedItem(e.getGender() == null ? "" : e.getGender());
            txtHireDate.setText(df.format(e.getHireDate()));
            txtSalary.setText(e.getSalary() == null ? "" : String.valueOf(e.getSalary()));
            cmbStatus.setSelectedItem(e.getStatus());

            selectById(cmbLocation, e.getLocationId());
            selectById(cmbDepartment, e.getDepartmentId());
            selectById(cmbPosition, e.getPositionId());
            selectById(cmbEmploymentType, e.getEmploymentTypeId());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employee:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            dispose();
        }
    }

    private void selectById(JComboBox<LookupItem> combo, int id) {
        ComboBoxModel<LookupItem> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            LookupItem it = m.getElementAt(i);
            if (it.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void onSave() {
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrors(errors);
            return; 
        }

        String natId = txtNationalId.getText().trim();

        try {
            if (editingEmployeeId == null) {
                if (employeeDAO.existsNationalId(natId)) {
                    showErrors(List.of("National ID already exists. Please use a different one."));
                    return; 
                }
            } else {
                if (employeeDAO.existsNationalIdForOtherEmployee(natId, editingEmployeeId)) {
                    showErrors(List.of("National ID already exists for another employee."));
                    return; 
                }
            }

            Employee e = buildEmployeeFromForm();

            if (editingEmployeeId == null) {
                employeeDAO.insertWithDeptHistory(e);
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } else {
                e.setEmployeeId(editingEmployeeId);
                employeeDAO.updateWithDeptHistory(e, new java.util.Date());
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            }

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, friendlyDbMessage(ex), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

    }
    
    private String friendlyDbMessage(SQLException ex) {
    String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
    if (msg.contains("ux_employee_nationalid") || msg.contains("duplicate key")) {
        return "National ID already exists. Please use a different one.";
    }
    return "Database error:\n" + ex.getMessage();
}


    private Employee buildEmployeeFromForm() {
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String natId = txtNationalId.getText().trim();

        if (first.isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (last.isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (natId.isEmpty()) {
            throw new IllegalArgumentException("National ID is required.");
        }

        Date birth = parseDateStrict(txtBirthDate.getText().trim(), "Birth date");
        Date hire = parseDateStrict(txtHireDate.getText().trim(), "Hire date");

        Double salary = null;
        String salaryTxt = txtSalary.getText().trim();
        if (!salaryTxt.isEmpty()) {
            try {
                // validation already checked up to 2 decimals, but we still parse safely
                java.math.BigDecimal bd = new java.math.BigDecimal(salaryTxt);

                if (bd.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Salary cannot be negative.");
                }
                if (bd.compareTo(new java.math.BigDecimal("99999999.99")) > 0) {
                    throw new IllegalArgumentException("Salary is too large.");
                }

                salary = bd.doubleValue(); // (later we can upgrade the model to BigDecimal if you want)
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Salary is not a valid number.");
            }
        }

        LookupItem loc = (LookupItem) cmbLocation.getSelectedItem();
        LookupItem dept = (LookupItem) cmbDepartment.getSelectedItem();
        LookupItem pos = (LookupItem) cmbPosition.getSelectedItem();
        LookupItem empType = (LookupItem) cmbEmploymentType.getSelectedItem();

        if (loc == null || dept == null || pos == null || empType == null) {
            throw new IllegalArgumentException("Please make sure Location/Department/Position/Employment Type are selected.");
        }

        String gender = (String) cmbGender.getSelectedItem();
        if (gender != null && gender.isBlank()) {
            gender = null;
        }

        String status = (String) cmbStatus.getSelectedItem();
        if (status == null || status.isBlank()) {
            status = "Active";
        }

        Employee e = new Employee();
        e.setFirstName(first);
        e.setLastName(last);
        e.setNationalId(natId);
        e.setBirthDate(birth);
        e.setGender(gender);
        e.setHireDate(hire);
        e.setSalary(salary);

        e.setLocationId(loc.getId());
        e.setDepartmentId(dept.getId());
        e.setPositionId(pos.getId());
        e.setEmploymentTypeId(empType.getId());
        e.setStatus(status);

        return e;
    }

    private Date parseDateStrict(String s, String fieldName) {
        try {
            df.setLenient(false);
            return df.parse(s);
        } catch (ParseException e) {
            throw new IllegalArgumentException(fieldName + " must be in format yyyy-MM-dd.");
        }
    }

    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();

        String first = normalizeName(txtFirstName.getText());
        String last = normalizeName(txtLastName.getText());
        String natId = txtNationalId.getText().trim();

        // Required + max length (matches DB varchar sizes)
        if (first.isEmpty()) {
            errors.add("First name is required.");
        } else if (first.length() > 100) {
            errors.add("First name must be at most 100 characters.");
        }

        if (last.isEmpty()) {
            errors.add("Last name is required.");
        } else if (last.length() > 100) {
            errors.add("Last name must be at most 100 characters.");
        }

        if (natId.isEmpty()) {
            errors.add("National ID is required.");
        } else if (natId.length() > 20) {
            errors.add("National ID must be at most 20 characters.");
        } else if (!natId.matches("\\d+")) {
            errors.add("National ID must contain digits only.");
        }

        Date birth = tryParseDate(txtBirthDate.getText().trim(), "Birth date", errors);
        Date hire = tryParseDate(txtHireDate.getText().trim(), "Hire date", errors);

        Date today = stripTime(new Date());
        if (birth != null && birth.after(today)) {
            errors.add("Birth date cannot be in the future.");
        }
        if (hire != null && hire.after(today)) {
            errors.add("Hire date cannot be in the future.");
        }
        if (birth != null && hire != null && hire.before(birth)) {
            errors.add("Hire date cannot be before birth date.");
        }

        // Salary: optional (DB allows NULL) - validate format if filled
        String salaryTxt = txtSalary.getText().trim();
        if (!salaryTxt.isEmpty()) {
            if (!salaryTxt.matches("\\d+(\\.\\d{1,2})?")) {
                errors.add("Salary must be a number with up to 2 decimals (e.g. 3500 or 3500.50).");
            }
        }

        // Lookup selections (must not be placeholder id = -1)
        errors.addAll(validateLookup("Location", cmbLocation));
        errors.addAll(validateLookup("Department", cmbDepartment));
        errors.addAll(validateLookup("Position", cmbPosition));
        errors.addAll(validateLookup("Employment Type", cmbEmploymentType));

        String status = (String) cmbStatus.getSelectedItem();
        if (status == null || status.isBlank()) {
            errors.add("Status is required.");
        }

        return errors;
    }

    private String normalizeName(String s) {
        if (s == null) {
            return "";
        }
        return s.trim().replaceAll("\\s+", " ");
    }

    private Date tryParseDate(String s, String fieldName, List<String> errors) {
        if (s == null || s.isBlank()) {
            errors.add(fieldName + " is required (yyyy-MM-dd).");
            return null;
        }
        try {
            df.setLenient(false);
            return df.parse(s);
        } catch (ParseException e) {
            errors.add(fieldName + " must be in format yyyy-MM-dd.");
            return null;
        }
    }

    private Date stripTime(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private List<String> validateLookup(String name, JComboBox<LookupItem> combo) {
        List<String> errors = new ArrayList<>();
        LookupItem it = (LookupItem) combo.getSelectedItem();
        if (it == null || it.getId() == -1) {
            errors.add(name + " must be selected.");
        }
        return errors;
    }

    private void showErrors(List<String> errors) {
        String msg = "Please fix the following:\n\n- " + String.join("\n- ", errors);
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

}
