package service;
import java.util.ArrayList;
import java.util.List;
public class WorkloadReport {
    public static class Entry {
        public String taAccount;
        public String taName;
        public int jobCount;
        public int totalHours;
        public String getTaAccount() { return taAccount; }
        public String getTaName() { return taName; }
        public int getJobCount() { return jobCount; }
        public int getTotalHours() { return totalHours; }
        @Override
        public String toString() { return "Entry{taAccount='" + taAccount + "', taName='" + taName + "', jobCount=" + jobCount + ", totalHours=" + totalHours + "}"; }
    }
    private List<Entry> entries = new ArrayList<>();
    public void addEntry(Entry e) { entries.add(e); }
    public List<Entry> getEntries() { return entries; }
    @Override
    public String toString() { return "WorkloadReport{entries=" + entries.size() + "}"; }
}
