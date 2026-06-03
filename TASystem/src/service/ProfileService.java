package service;

import model.TAProfile;
import data.DataStore;

public class ProfileService {
    private ProfileService() {}

    public static boolean createProfile(TAProfile profile) {
        profile.setCompleted(checkComplete(profile));
        DataStore.addProfile(profile);
        return true;
    }

    public static boolean updateProfile(TAProfile profile) {
        profile.setCompleted(checkComplete(profile));
        DataStore.updateProfile(profile);
        return true;
    }

    public static TAProfile getProfile(String account) {
        return DataStore.findProfileByAccount(account);
    }

    public static boolean isProfileComplete(String account) {
        TAProfile profile = DataStore.findProfileByAccount(account);
        return profile != null && profile.isCompleted();
    }

    private static boolean checkComplete(TAProfile profile) {
        return notEmpty(profile.getName())
            && notEmpty(profile.getEmail())
            && notEmpty(profile.getPhone())
            && notEmpty(profile.getEducation())
            && notEmpty(profile.getMajor())
            && notEmpty(profile.getGpa())
            && notEmpty(profile.getSkills())
            && notEmpty(profile.getExperience())
            && notEmpty(profile.getAvailability())
            && notEmpty(profile.getPreferredTypes());
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
