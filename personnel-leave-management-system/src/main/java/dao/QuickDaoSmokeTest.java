package dao;

public class QuickDaoSmokeTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== DAO Smoke Test ===");

        // 1) LOOKUPS
        System.out.println("Roles: " + new RoleDAO().getAll().size());
        System.out.println("Departments: " + new DepartmentDAO().getAll().size());
        System.out.println("Positions: " + new PositionDAO().getAll().size());
        System.out.println("EmploymentTypes: " + new EmploymentTypeDAO().getAll().size());
        System.out.println("Locations: " + new LocationDAO().getAll().size());

        // 2) LEAVE LOOKUPS
        System.out.println("LeaveTypes: " + new LeaveTypeDAO().getAll().size());
        System.out.println("Holidays: " + new HolidayDAO().getAll().size());

        // 3) MAIN TABLES
        System.out.println("Employees: " + new EmployeeDAO().getAll().size()); // you already have it
//        System.out.println("EmployeeContacts: " + new EmployeeContactDAO().getAll().size());
        System.out.println("DeptHistory: " + new EmployeeDepartmentHistoryDAO().getAll().size());

        System.out.println("LeavePolicies: " + new LeavePolicyDAO().getAll().size());
        System.out.println("LeaveBalances: " + new LeaveBalanceDAO().getAll().size());
//        System.out.println("LeaveRequests: " + new LeaveRequestDAO().getAll().size());
        System.out.println("LeaveApprovals: " + new LeaveApprovalDAO().getAll().size());

        System.out.println("UserAccounts: " + new UserAccountDAO().getAll().size());

        System.out.println("✅ Smoke test finished.");
    }
}
