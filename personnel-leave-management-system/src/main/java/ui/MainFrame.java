package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JButton btnEmployees = new JButton("Employees");
    private JButton btnDepartments = new JButton("Departments");
    private JButton btnLocations = new JButton("Locations");
    private JButton btnPositions = new JButton("Positions");
    private JButton btnEmploymentTypes = new JButton("Employment Types");

    private JButton btnLeaveTypes = new JButton("Leave Types");
    private JButton btnLeaveRequests = new JButton("Leave Requests");
    private JButton btnLeaveApprovals = new JButton("Leave Approvals");
    private JButton btnLeaveHistory = new JButton("Leave Decisions History");

    private JButton btnExit = new JButton("Exit");

    public MainFrame() {
        super("Personnel Leave Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
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

        grid.add(btnLocations);
        grid.add(btnPositions);

        grid.add(btnEmploymentTypes);
        grid.add(btnLeaveTypes);

        grid.add(btnLeaveRequests);
        grid.add(btnLeaveApprovals);

        grid.add(btnLeaveHistory);
        grid.add(new JLabel("")); // filler

        root.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnExit);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        // actions
        btnEmployees.addActionListener(e -> new EmployeeTableFrame().setVisible(true));
        btnDepartments.addActionListener(e -> new DepartmentFrame().setVisible(true));
        btnLocations.addActionListener(e -> new LocationFrame().setVisible(true));
        btnPositions.addActionListener(e -> new PositionFrame().setVisible(true));
        btnEmploymentTypes.addActionListener(e -> new EmploymentTypeFrame().setVisible(true));

        btnLeaveTypes.addActionListener(e -> new LeaveTypeFrame().setVisible(true));
        btnLeaveRequests.addActionListener(e -> new LeaveRequestFrame().setVisible(true));
        btnLeaveHistory.addActionListener(e -> new LeaveDecisionHistoryFrame().setVisible(true));

        btnLeaveApprovals.addActionListener(e -> openApprovals());

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void openApprovals() {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter approver employee ID:",
                "Open Leave Approvals",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) return; // cancel

        input = input.trim();
        if (!input.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric employee ID.");
            return;
        }

        int approverId = Integer.parseInt(input);
        new LeaveApprovalFrame(approverId).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
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
