package ui;

import dao.LookupDAO;
import dao.LeavePolicyDAO;
import model.EmploymentType;
import model.LeavePolicy;
import model.LeaveType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import model.LookupItem;

public class AddLeavePolicyDialog extends JDialog {

    private final LeavePolicyDAO policyDAO = new LeavePolicyDAO();
    private final LookupDAO lookupDAO = new LookupDAO();

    private boolean saved = false;

    private final JComboBox<LookupItem> cbLeaveType = new JComboBox<>();
    private final JComboBox<LookupItem> cbEmploymentType = new JComboBox<>();

    private final JTextField txtAnnualQuota = new JTextField(10);
    private final JTextField txtMaxConsecutive = new JTextField(10);  
    private final JTextField txtMinServiceMonths = new JTextField(10); 

    public AddLeavePolicyDialog(Frame owner) {
        super(owner, "Add Leave Policy", true);
        setSize(520, 280);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        loadCombos(); 

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Leave Type:"));
        form.add(cbLeaveType);

        form.add(new JLabel("Employment Type:"));
        form.add(cbEmploymentType);

        form.add(new JLabel("Annual Quota (days):"));
        form.add(txtAnnualQuota);

        form.add(new JLabel("Max Consecutive (optional):"));
        form.add(txtMaxConsecutive);

        form.add(new JLabel("Min Service Months (optional):"));
        form.add(txtMinServiceMonths);

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

    private void loadCombos() {
        cbLeaveType.removeAllItems();
        cbEmploymentType.removeAllItems();

        try {

            List<LookupItem> leaveTypes = lookupDAO.getLeaveTypes();
            for (LookupItem lt : leaveTypes) {
                cbLeaveType.addItem(lt);
            }

            List<LookupItem> empTypes = lookupDAO.getEmploymentTypes();
            for (LookupItem et : empTypes) {
                cbEmploymentType.addItem(et);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error while loading dropdowns:\n" + ex.getMessage(),
                    "SQLException", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        LookupItem lt = (LookupItem) cbLeaveType.getSelectedItem();
        LookupItem et = (LookupItem) cbEmploymentType.getSelectedItem();

        if (lt == null || et == null) {
            JOptionPane.showMessageDialog(this, "Select Leave Type and Employment Type.");
            return;
        }

        Integer annualQuota = parseRequiredInt(txtAnnualQuota.getText(), "Annual Quota");
        if (annualQuota == null) {
            return;
        }

        Integer maxConsecutive = parseOptionalInt(txtMaxConsecutive.getText());
        Integer minService = parseOptionalInt(txtMinServiceMonths.getText());

        LeavePolicy p = new LeavePolicy();
        p.setLeaveTypeId(lt.getId());          //  from LookupItem
        p.setEmploymentTypeId(et.getId());     //  from LookupItem
        p.setAnnualQuotaDays(annualQuota);
        p.setMaxConsecutiveDays(maxConsecutive);
        p.setMinServiceMonthsRequired(minService);

        try {
            policyDAO.insert(p);
            saved = true;
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Integer parseRequiredInt(String s, String fieldName) {
        try {
            int v = Integer.parseInt(s.trim());
            if (v <= 0) {
                throw new NumberFormatException();
            }
            return v;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, fieldName + " must be a positive integer.");
            return null;
        }
    }

    private Integer parseOptionalInt(String s) {
        String t = s.trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            int v = Integer.parseInt(t);
            return v > 0 ? v : null;
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
