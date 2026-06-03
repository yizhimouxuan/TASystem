package model;
public class CV {
    private String account;
    private String fileName;
    private String filePath;
    private boolean qualified;
    private String rejectReason;
    public CV() { this.qualified = false; }
    public CV(String account, String fileName, String filePath, boolean qualified, String rejectReason) {
        this.account = account; this.fileName = fileName; this.filePath = filePath;
        this.qualified = qualified; this.rejectReason = rejectReason;
    }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public boolean isQualified() { return qualified; }
    public void setQualified(boolean qualified) { this.qualified = qualified; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    @Override
    public String toString() { return "CV{account='" + account + "', fileName='" + fileName + "', qualified=" + qualified + "}"; }
}
