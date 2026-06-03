package model;

public class TAProfile {
    private String account;
    private String name;
    private String email;
    private String phone;
    private String education;
    private String major;
    private String gpa;
    private String skills;
    private String experience;
    private String availability;
    private String preferredTypes;
    private boolean completed;

    public TAProfile() {}

    public TAProfile(String account, String name, String email, String phone,
                     String education, String major, String gpa,
                     String skills, String experience, String availability,
                     String preferredTypes, boolean completed) {
        this.account = account;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.education = education;
        this.major = major;
        this.gpa = gpa;
        this.skills = skills;
        this.experience = experience;
        this.availability = availability;
        this.preferredTypes = preferredTypes;
        this.completed = completed;
    }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getPreferredTypes() { return preferredTypes; }
    public void setPreferredTypes(String preferredTypes) { this.preferredTypes = preferredTypes; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return "TAProfile{account='" + account + "', completed=" + completed + "}";
    }
}
