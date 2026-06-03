package model;
import model.enums.ApplicationStatus;
import java.util.UUID;
public class Application {
    private String applicationId;
    private String jobId;
    private String taAccount;
    private ApplicationStatus status;
    private String rejectReason;
    private long applyTime;
    public Application() {
        this.applicationId = UUID.randomUUID().toString();
        this.status = ApplicationStatus.PENDING;
        this.applyTime = System.currentTimeMillis();
    }
    public Application(String jobId, String taAccount) {
        this();
        this.jobId = jobId;
        this.taAccount = taAccount;
    }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTaAccount() { return taAccount; }
    public void setTaAccount(String taAccount) { this.taAccount = taAccount; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public long getApplyTime() { return applyTime; }
    public void setApplyTime(long applyTime) { this.applyTime = applyTime; }
    @Override
    public String toString() { return "Application{applicationId='" + applicationId + "', jobId='" + jobId + "', taAccount='" + taAccount + "', status=" + status + "}"; }
}
