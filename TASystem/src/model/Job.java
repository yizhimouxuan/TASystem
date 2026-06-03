package model;
import model.enums.JobStatus;
import model.enums.JobType;
import java.util.UUID;
public class Job {
    private String jobId;
    private String moAccount;
    private String title;
    private JobType type;
    private String description;
    private String requirements;
    private String workTime;
    private int tasNeeded;
    private JobStatus status;
    private String rejectReason;
    private boolean hired;
    public Job() {
        this.jobId = UUID.randomUUID().toString();
        this.status = JobStatus.PENDING_REVIEW;
        this.hired = false;
        this.tasNeeded = 1;
    }
    public Job(String moAccount, String title, JobType type, String description, String requirements, String workTime, int tasNeeded) {
        this();
        this.moAccount = moAccount; this.title = title; this.type = type;
        this.description = description; this.requirements = requirements; this.workTime = workTime;
        this.tasNeeded = tasNeeded;
    }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getMoAccount() { return moAccount; }
    public void setMoAccount(String moAccount) { this.moAccount = moAccount; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public JobType getType() { return type; }
    public void setType(JobType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getWorkTime() { return workTime; }
    public void setWorkTime(String workTime) { this.workTime = workTime; }
    public int getTasNeeded() { return tasNeeded; }
    public void setTasNeeded(int tasNeeded) { this.tasNeeded = tasNeeded; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public boolean isHired() { return hired; }
    public void setHired(boolean hired) { this.hired = hired; }
    @Override
    public String toString() { return "Job{jobId='" + jobId + "', title='" + title + "', status=" + status + "}"; }
}
