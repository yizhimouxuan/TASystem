package service;
import model.Application;
import model.Job;
import model.User;
import model.enums.ApplicationStatus;
import model.enums.JobStatus;
import data.DataStore;
import java.util.ArrayList;
import java.util.List;
public class ApplicationService {
    private ApplicationService() {}
    public static boolean apply(String taAccount, String jobId) {
        if (!ProfileService.isProfileComplete(taAccount)) return false;
        if (!CVService.hasCV(taAccount)) return false;
        if (hasApplied(taAccount, jobId)) return false;
        Job job = DataStore.findJobById(jobId);
        if (job == null || job.getStatus() != JobStatus.PUBLISHED) return false;
        // Check if job is already fully hired
        if (isJobFullyHired(jobId)) return false;
        // Only allow applying to jobs whose work time is in the future
        if (job.getWorkTime() != null && !job.getWorkTime().isEmpty()) {
            if (!util.WorkTimeUtil.isAllFuture(job.getWorkTime())) {
                return false; // Job has already started or passed, cannot apply
            }
        }
        Application application = new Application(jobId, taAccount);
        DataStore.addApplication(application);
        return true;
    }
    private static boolean isJobFullyHired(String jobId) {
        Job job = DataStore.findJobById(jobId);
        if (job == null) return true;
        int hiredCount = getHiredCount(jobId);
        return hiredCount >= job.getTasNeeded();
    }
    public static int getHiredCount(String jobId) {
        return (int) DataStore.findApplicationsByJob(jobId).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.HIRED)
                .count();
    }
    public static List<Application> getApplicationsForJob(String jobId) { return DataStore.findApplicationsByJob(jobId); }
    public static List<Application> getMyApplications(String taAccount) { return DataStore.findApplicationsByTA(taAccount); }
    public static List<Application> getApplicationsForMO(String moAccount) {
        List<Job> jobs = DataStore.findJobsByMO(moAccount);
        List<Application> result = new ArrayList<>();
        for (Job job : jobs) {
            result.addAll(DataStore.findApplicationsByJob(job.getJobId()));
        }
        return result;
    }
    public static boolean hire(String applicationId) {
        Application app = DataStore.findApplicationById(applicationId);
        if (app == null) return false;
        Job job = DataStore.findJobById(app.getJobId());
        if (job == null || job.getStatus() != JobStatus.PUBLISHED) return false;
        // Check if already fully hired
        int hiredCount = getHiredCount(job.getJobId());
        if (hiredCount >= job.getTasNeeded()) return false;
        app.setStatus(ApplicationStatus.HIRED);
        app.setRejectReason("");
        DataStore.updateApplication(app);
        hiredCount++;
        // Only mark job as fully hired when reached tasNeeded
        if (hiredCount >= job.getTasNeeded()) {
            job.setHired(true);
            job.setStatus(JobStatus.FULL);
        }
        DataStore.updateJob(job);
        return true;
    }
    public static boolean reject(String applicationId, String reason) {
        Application app = DataStore.findApplicationById(applicationId);
        if (app == null) return false;
        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectReason(reason != null ? reason : "");
        DataStore.updateApplication(app);
        return true;
    }
    public static boolean hasApplied(String taAccount, String jobId) {
        return DataStore.getAllApplications().stream().anyMatch(a -> a.getTaAccount().equals(taAccount) && a.getJobId().equals(jobId));
    }
    public static class ApplicationDetail {
        private Application application;
        private Job job;
        private String jobTitle;
        private String moName;
        public ApplicationDetail(Application application, Job job, String jobTitle, String moName) {
            this.application = application; this.job = job; this.jobTitle = jobTitle; this.moName = moName;
        }
        public Application getApplication() { return application; }
        public Job getJob() { return job; }
        public String getJobTitle() { return jobTitle; }
        public String getMoName() { return moName; }
    }
    public static List<ApplicationDetail> getMyApplicationDetails(String taAccount) {
        List<Application> applications = getMyApplications(taAccount);
        List<ApplicationDetail> details = new ArrayList<>();
        for (Application app : applications) {
            Job job = DataStore.findJobById(app.getJobId());
            String jobTitle = job != null ? job.getTitle() : "Unknown Job";
            String moName = "";
            if (job != null && job.getMoAccount() != null) {
                User mo = DataStore.findUserByAccount(job.getMoAccount());
                moName = mo != null ? mo.getName() : job.getMoAccount();
            }
            details.add(new ApplicationDetail(app, job, jobTitle, moName));
        }
        return details;
    }
}
