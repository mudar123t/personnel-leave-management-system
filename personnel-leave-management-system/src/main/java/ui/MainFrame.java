package ui;

import model.AuthenticatedUser;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final AuthenticatedUser user;

    private JButton btnEmployees = new JButton("Employees");
    private JButton btnDepartments = new JButton("Departments");
    private JButton btnLocations = new JButton("Locations");
    private JButton btnPositions = new JButton("Positions");
    private JButton btnEmploymentTypes = new JButton("Employment Types");
    private JButton btnAccountManagement = new JButton("Account Management");

    private JButton btnLeaveTypes = new JButton("Leave Types");
    private JButton btnLeaveRequests = new JButton("Leave Requests");
    private JButton btnLeaveApprovals = new JButton("Leave Approvals");
    private JButton btnLeaveHistory = new JButton("Leave Decisions History");

    private JButton btnExit = new JButton("Exit");

    public MainFrame(AuthenticatedUser user) {
        this.user = user;

        setTitle("Dashboard - " + user.getUsername() + " (" + user.getRoleName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        applyRolePermissions();
    }

    private void initUI() {
        setSize(520, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        root.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));

        grid.add(btnEmployees);
        grid.add(btnDepartments);
        
        grid.add(btnAccountManagement);

        grid.add(btnLocations);
        grid.add(btnPositions);

        grid.add(btnEmploymentTypes);
        grid.add(btnLeaveTypes);

        grid.add(btnLeaveRequests);
        grid.add(btnLeaveApprovals);
        
        
        grid.add(btnLeaveHistory);
        grid.add(new JLabel("")); // filler to keep grid even

        root.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnExit);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        // Actions
        btnEmployees.addActionListener(e -> new EmployeeTableFrame().setVisible(true));
        btnDepartments.addActionListener(e -> new DepartmentFrame().setVisible(true));
        btnLocations.addActionListener(e -> new LocationFrame().setVisible(true));
        btnPositions.addActionListener(e -> new PositionFrame().setVisible(true));
        btnEmploymentTypes.addActionListener(e -> new EmploymentTypeFrame().setVisible(true));
        btnAccountManagement.addActionListener(e -> new AccountManagementFrame().setVisible(true));


        btnLeaveTypes.addActionListener(e -> new LeaveTypeFrame().setVisible(true));

        btnLeaveRequests.addActionListener(e -> openLeaveRequests());
        btnLeaveApprovals.addActionListener(e -> openApprovals());
        btnLeaveHistory.addActionListener(e -> new LeaveDecisionHistoryFrame().setVisible(true));

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void applyRolePermissions() {
        String role = user.getRoleName() == null ? "" : user.getRoleName().trim().toLowerCase();

        boolean isManager = role.equals("manager");
        boolean isHr = role.equals("hr");
        boolean isAdmin = role.equals("admin");
        boolean isEmployee = role.equals("employee");

        // Hide all
        btnEmployees.setVisible(false);
        btnDepartments.setVisible(false);
        btnLocations.setVisible(false);
        btnPositions.setVisible(false);
        btnEmploymentTypes.setVisible(false);
        btnAccountManagement.setVisible(isManager);
        btnLeaveTypes.setVisible(false);
        btnLeaveRequests.setVisible(false);
        btnLeaveApprovals.setVisible(false);
        btnLeaveHistory.setVisible(false);
        btnAccountManagement.setVisible(false);

        if (isManager) { // ONLY manager
            btnAccountManagement.setVisible(true);
        }
        // Manager/Admin: everything
        if (isManager || isAdmin) {
            btnEmployees.setVisible(true);
            btnDepartments.setVisible(true);
            btnLocations.setVisible(true);
            btnPositions.setVisible(true);
            btnEmploymentTypes.setVisible(true);

            btnLeaveTypes.setVisible(true);
            btnLeaveRequests.setVisible(true);
            btnLeaveApprovals.setVisible(true);
            btnLeaveHistory.setVisible(true);
            return;
        }

        // HR: employees + approvals + history (+ leave types + requests)
        if (isHr) {
            btnEmployees.setVisible(true);

            btnLeaveRequests.setVisible(true);
            btnLeaveApprovals.setVisible(true);
            btnLeaveHistory.setVisible(true);
            btnLeaveTypes.setVisible(true);
            return;
        }

        // Employee: only own leave requests
        if (isEmployee) {
            btnLeaveRequests.setVisible(true);
        }
    }

    private void openLeaveRequests() {
        String role = user.getRoleName() == null ? "" : user.getRoleName().trim().toLowerCase();

        if (role.equals("employee")) {
            new MyLeaveRequestFrame(user.getEmployeeId()).setVisible(true);
        } else {
            new LeaveRequestFrame().setVisible(true);
        }
    }

    private void openApprovals() {
        String role = user.getRoleName() == null ? "" : user.getRoleName().trim().toLowerCase();

        if (role.equals("manager") || role.equals("admin") || role.equals("hr")) {
            new LeaveApprovalFrame(user.getEmployeeId()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "You are not allowed to approve leave requests.");
        }
    }


   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
