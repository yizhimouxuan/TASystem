package service;
import model.User;
import model.enums.UserRole;
import data.DataStore;
import util.WorkTimeUtil;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin service providing user management and workload statistics.
 * Workload only counts work slots whose end time has passed (completed work).
 */
public class AdminService {
    private AdminService() {}

    public static List<User> getAllUsers(String roleFilter, String search) {
        List<User> users = DataStore.getAllUsers();
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            try {
                UserRole role = UserRole.valueOf(roleFilter.toUpperCase());
                users = users.stream().filter(u -> u.getRole() == role).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {}
        }
        if (search != null && !search.trim().isEmpty()) {
            String kw = search.toLowerCase().trim();
            users = users.stream().filter(u -> (u.getAccount() != null && u.getAccount().toLowerCase().contains(kw)) || (u.getName() != null && u.getName().toLowerCase().contains(kw))).collect(Collectors.toList());
        }
        return users;
    }

    public static boolean freezeUser(String account, boolean frozen) {
        User user = DataStore.findUserByAccount(account);
        if (user != null) { user.setFrozen(frozen); DataStore.updateUser(user); return true; }
        return false;
    }

    /**
     * Get workload statistics for all TAs.
     * Only counts work slots whose end time has already passed.
     * Work that is scheduled for the future is NOT included in workload.
     */
    public static WorkloadReport getWorkloadStats() {
        WorkloadReport report = new WorkloadReport();
        long now = System.currentTimeMillis();

        DataStore.getAllApplications().stream()
            .filter(a -> a.getStatus() == model.enums.ApplicationStatus.HIRED)
            .collect(Collectors.groupingBy(model.Application::getTaAccount))
            .forEach((taAccount, apps) -> {
                User user = DataStore.findUserByAccount(taAccount);
                String taName = user != null ? user.getName() : taAccount;

                // Only count jobs whose ALL work slots have ended
                int completedJobCount = 0;
                int completedHours = 0;

                for (model.Application app : apps) {
                    model.Job job = DataStore.findJobById(app.getJobId());
                    if (job != null && job.getWorkTime() != null && !job.getWorkTime().isEmpty()) {
                        // Check if all slots have ended (work is fully completed)
                        List<WorkTimeUtil.WorkSlot> slots = WorkTimeUtil.decodeSlots(job.getWorkTime());
                        boolean allSlotsEnded = !slots.isEmpty();
                        int jobHours = 0;
                        for (WorkTimeUtil.WorkSlot slot : slots) {
                            if (!slot.isPast()) {
                                allSlotsEnded = false;
                                break;
                            }
                            jobHours += WorkTimeUtil.getSlotHours(slot.timeSlot);
                        }
                        if (allSlotsEnded) {
                            completedJobCount++;
                            completedHours += jobHours;
                        }
                    }
                }

                // Only add entry if TA has completed work
                if (completedJobCount > 0) {
                    WorkloadReport.Entry entry = new WorkloadReport.Entry();
                    entry.taAccount = taAccount;
                    entry.taName = taName;
                    entry.jobCount = completedJobCount;
                    entry.totalHours = completedHours;
                    report.addEntry(entry);
                }
            });
        return report;
    }
}
