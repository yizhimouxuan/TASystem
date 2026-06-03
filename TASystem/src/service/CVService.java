package service;
import model.CV;
import data.DataStore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
public class CVService {
    private CVService() {}
    private static final String CV_DIR = "data/cvs/";
    public static boolean uploadCV(String account, File sourceFile) {
        try {
            File dir = new File(CV_DIR);
            if (!dir.exists()) dir.mkdirs();
            String ext = "";
            String origName = sourceFile.getName();
            int dot = origName.lastIndexOf('.');
            if (dot > 0) ext = origName.substring(dot);
            File destFile = new File(CV_DIR + account + "_cv" + ext);
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            CV cv = new CV(account, origName, destFile.getPath(), false, "");
            CV existing = DataStore.findCVByAccount(account);
            if (existing != null) DataStore.updateCV(cv); else DataStore.addCV(cv);
            return true;
        } catch (IOException e) { return false; }
    }
    public static CV getCV(String account) { return DataStore.findCVByAccount(account); }
    public static void markQualified(String account, boolean qualified, String reason) {
        CV cv = DataStore.findCVByAccount(account);
        if (cv != null) {
            cv.setQualified(qualified);
            cv.setRejectReason(reason);
            DataStore.updateCV(cv);
        }
    }
    public static boolean hasCV(String account) { return DataStore.findCVByAccount(account) != null; }
    public static boolean deleteCV(String account) {
        CV cv = DataStore.findCVByAccount(account);
        if (cv != null) {
            try { new File(cv.getFilePath()).delete(); } catch (Exception e) {}
            DataStore.removeCVIf(c -> c.getAccount().equals(account));
            return true;
        }
        return false;
    }
}
