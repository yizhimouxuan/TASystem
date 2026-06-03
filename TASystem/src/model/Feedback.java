package model;
import java.util.UUID;
public class Feedback {
    private String feedbackId;
    private String moAccount;
    private String taAccount;
    private String jobId;
    private int rating;
    private String comment;
    private long submitTime;
    public Feedback() {
        this.feedbackId = UUID.randomUUID().toString();
        this.submitTime = System.currentTimeMillis();
    }
    public Feedback(String moAccount, String taAccount, String jobId, int rating, String comment) {
        this();
        this.moAccount = moAccount; this.taAccount = taAccount; this.jobId = jobId;
        this.rating = rating; this.comment = comment;
    }
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getMoAccount() { return moAccount; }
    public void setMoAccount(String moAccount) { this.moAccount = moAccount; }
    public String getTaAccount() { return taAccount; }
    public void setTaAccount(String taAccount) { this.taAccount = taAccount; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public long getSubmitTime() { return submitTime; }
    public void setSubmitTime(long submitTime) { this.submitTime = submitTime; }
    @Override
    public String toString() { return "Feedback{feedbackId='" + feedbackId + "', taAccount='" + taAccount + "', rating=" + rating + "}"; }
}
