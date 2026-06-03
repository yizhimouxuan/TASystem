package service;
import model.Job;
import model.enums.JobStatus;
import data.DataStore;
import util.WorkTimeUtil;
import java.util.List;
import java.util.stream.Collectors;
public class JobService {
    private JobService() {}
    public static boolean postJob(Job job) {
        job.setStatus(JobStatus.PENDING_REVIEW);
        job.setHired(false);
        DataStore.addJob(job);
        return true;
    }
    public static boolean reviewJob(String jobId, boolean approved, String reason) {
        Job job = DataStore.findJobById(jobId);
        if (job == null) return false;
        if (approved) { job.setStatus(JobStatus.PUBLISHED); job.setRejectReason(""); }
        else { job.setStatus(JobStatus.REJECTED); job.setRejectReason(reason != null ? reason : ""); }
        DataStore.updateJob(job);
        return true;
    }

    /**
     * Check all published/full jobs and mark as ENDED if all work slots have passed.
     */
    private static void expireJobs() {
        for (Job job : DataStore.getAllJobs()) {
            if (job.getStatus() == JobStatus.PUBLISHED || job.getStatus() == JobStatus.FULL) {
                if (job.getWorkTime() != null && !job.getWorkTime().isEmpty()) {
                    if (WorkTimeUtil.isAllPast(job.getWorkTime())) {
                        job.setStatus(JobStatus.ENDED);
                        DataStore.updateJob(job);
                    }
                }
            }
        }
    }

    public static List<Job> getPublishedJobs() {
        expireJobs();
        return DataStore.findPublishedJobs();
    }
    public static List<Job> getMOJobs(String moAccount) {
        expireJobs();
        return DataStore.findJobsByMO(moAccount);
    }
    public static List<Job> getPendingJobs() {
        expireJobs();
        return DataStore.findPendingJobs();
    }
    public static void updateJob(Job job) { DataStore.updateJob(job); }
    public static void deleteJob(String jobId) {
        Job job = DataStore.findJobById(jobId);
        if (job != null && (job.getStatus() == JobStatus.PENDING_REVIEW || job.getStatus() == JobStatus.REJECTED)) {
            List<Job> jobs = DataStore.getAllJobs().stream().filter(j -> !j.getJobId().equals(jobId)).collect(Collectors.toList());
            DataStore.clearAndRepopulateJobs(jobs);
        }
    }
    public static List<Job> searchJobs(String keyword, String type) {
        expireJobs();
        List<Job> jobs = getPublishedJobs();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            jobs = jobs.stream().filter(j -> j.getTitle().toLowerCase().contains(kw)
                || j.getRequirements().toLowerCase().contains(kw)
                || j.getDescription().toLowerCase().contains(kw)).collect(Collectors.toList());
        }
        if (type != null && !type.trim().isEmpty()) {
            jobs = jobs.stream().filter(j -> j.getType().toString().equalsIgnoreCase(type.trim())).collect(Collectors.toList());
        }
        return jobs;
    }
}
