package ui;

import dao.EmployeeDAO;
import dao.LookupDAO;
import model.Employee;
import model.LookupItem;

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

        c.gridx = 0; c.weightx = 0;
        panel.add(new JLabel(label1), c);

        c.gridx = 1; c.weightx = 1;
        panel.add(comp1, c);

        c.gridx = 2; c.weightx = 0;
        panel.add(new JLabel(label2), c);

        c.gridx = 3; c.weightx = 1;
        panel.add(comp2, c);
    }

    private void loadLookups() {
        try {
            fillCombo(cmbLocation, lookupDAO.getLocations());
            fillCombo(cmbDepartment, lookupDAO.getDepartments());
            fillCombo(cmbPosition, lookupDAO.getPositions());
            fillCombo(cmbEmploymentType, lookupDAO.getEmploymentTypes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load lookup data:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void fillCombo(JComboBox<LookupItem> combo, List<LookupItem> items) {
        DefaultComboBoxModel<LookupItem> m = new DefaultComboBoxModel<>();
        for (LookupItem it : items) m.addElement(it);
        combo.setModel(m);
        if (m.getSize() > 0) combo.setSelectedIndex(0);
    }

    private void setDefaultsForAdd() {
        df.setLenient(false);
        txtBirthDate.setText("2000-01-01");
        txtHireDate.setText(df.format(new Date()));
        txtSalary.setText("0");
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
            txtSalary.setText(String.valueOf(e.getSalary()));
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
        try {
            Employee e = buildEmployeeFromForm();

            if (editingEmployeeId == null) {
                employeeDAO.insert(e);
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } else {
                e.setEmployeeId(editingEmployeeId);
                employeeDAO.update(e);
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            }

            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private Employee buildEmployeeFromForm() {
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String natId = txtNationalId.getText().trim();

        if (first.isEmpty()) throw new IllegalArgumentException("First name is required.");
        if (last.isEmpty()) throw new IllegalArgumentException("Last name is required.");
        if (natId.isEmpty()) throw new IllegalArgumentException("National ID is required.");

        Date birth = parseDateStrict(txtBirthDate.getText().trim(), "Birth date");
        Date hire = parseDateStrict(txtHireDate.getText().trim(), "Hire date");

        double salary;
        try {
            salary = Double.parseDouble(txtSalary.getText().trim());
            if (salary < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Salary must be a non-negative number.");
        }

        LookupItem loc = (LookupItem) cmbLocation.getSelectedItem();
        LookupItem dept = (LookupItem) cmbDepartment.getSelectedItem();
        LookupItem pos = (LookupItem) cmbPosition.getSelectedItem();
        LookupItem empType = (LookupItem) cmbEmploymentType.getSelectedItem();

        if (loc == null || dept == null || pos == null || empType == null)
            throw new IllegalArgumentException("Please make sure Location/Department/Position/Employment Type are selected.");

        String gender = (String) cmbGender.getSelectedItem();
        if (gender != null && gender.isBlank()) gender = null;

        String status = (String) cmbStatus.getSelectedItem();
        if (status == null || status.isBlank()) status = "Active";

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
}
