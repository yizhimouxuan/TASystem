package data;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.*;
import model.enums.UserRole;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
public class DataStore {
    private static final String USER_FILE = "data/users.json";
    private static final String PROFILE_FILE = "data/profiles.json";
    private static final String CV_FILE = "data/cvs.json";
    private static final String JOB_FILE = "data/jobs.json";
    private static final String APPLICATION_FILE = "data/applications.json";
    private static final String FEEDBACK_FILE = "data/feedbacks.json";
    private static final Gson GSON;
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(User.class, new UserPolymorphicDeserializer());
        builder.registerTypeAdapter(new TypeToken<User[]>(){}.getType(), new UserArrayPolymorphicDeserializer());
        GSON = builder.create();
    }
    private static final JsonDataStore<User> USER_STORE = new JsonDataStore<>(USER_FILE, User[].class, GSON);
    private static final JsonDataStore<TAProfile> PROFILE_STORE = new JsonDataStore<>(PROFILE_FILE, TAProfile[].class, GSON);
    private static final JsonDataStore<CV> CV_STORE = new JsonDataStore<>(CV_FILE, CV[].class, GSON);
    private static final JsonDataStore<Job> JOB_STORE = new JsonDataStore<>(JOB_FILE, Job[].class, GSON);
    private static final JsonDataStore<Application> APPLICATION_STORE = new JsonDataStore<>(APPLICATION_FILE, Application[].class, GSON);
    private static final JsonDataStore<Feedback> FEEDBACK_STORE = new JsonDataStore<>(FEEDBACK_FILE, Feedback[].class, GSON);
    private DataStore() {}
    private static class UserPolymorphicDeserializer implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            UserRole role = UserRole.valueOf(obj.get("role").getAsString().toUpperCase());
            Class<? extends User> concreteClass;
            switch (role) {
                case TA: concreteClass = TA.class; break;
                case MO: concreteClass = MO.class; break;
                case ADMIN: concreteClass = Admin.class; break;
                default: throw new JsonParseException("Unknown user role: " + role);
            }
            return context.deserialize(json, concreteClass);
        }
    }
    private static class UserArrayPolymorphicDeserializer implements JsonDeserializer<User[]> {
        @Override
        public User[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            User[] users = new User[array.size()];
            for (int i = 0; i < array.size(); i++) {
                users[i] = context.deserialize(array.get(i), User.class);
            }
            return users;
        }
    }
    public static List<User> getAllUsers() { return USER_STORE.findAll(); }
    public static void addUser(User user) { USER_STORE.add(user); }
    public static void updateUser(User user) { USER_STORE.update(u -> u.getAccount().equals(user.getAccount()), user); }
    public static User findUserByAccount(String account) { return USER_STORE.findAll().stream().filter(u -> u.getAccount().equals(account)).findFirst().orElse(null); }
    public static User findUserByEmail(String email) { return USER_STORE.findAll().stream().filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null); }
    public static void removeUserIf(Predicate<User> predicate) { USER_STORE.removeIf(predicate); }
    public static List<TAProfile> getAllProfiles() { return PROFILE_STORE.findAll(); }
    public static void addProfile(TAProfile profile) { PROFILE_STORE.add(profile); }
    public static void updateProfile(TAProfile profile) { PROFILE_STORE.update(p -> p.getAccount().equals(profile.getAccount()), profile); }
    public static TAProfile findProfileByAccount(String account) { return PROFILE_STORE.findAll().stream().filter(p -> p.getAccount().equals(account)).findFirst().orElse(null); }
    public static List<CV> getAllCVs() { return CV_STORE.findAll(); }
    public static void addCV(CV cv) { CV_STORE.add(cv); }
    public static void updateCV(CV cv) { CV_STORE.update(c -> c.getAccount().equals(cv.getAccount()), cv); }
    public static CV findCVByAccount(String account) { return CV_STORE.findAll().stream().filter(c -> c.getAccount().equals(account)).findFirst().orElse(null); }
    public static void removeCVIf(Predicate<CV> predicate) { CV_STORE.removeIf(predicate); }
    public static List<Job> getAllJobs() { return JOB_STORE.findAll(); }
    public static void addJob(Job job) { JOB_STORE.add(job); }
    public static void updateJob(Job job) { JOB_STORE.update(j -> j.getJobId().equals(job.getJobId()), job); }
    public static Job findJobById(String jobId) { return JOB_STORE.findAll().stream().filter(j -> j.getJobId().equals(jobId)).findFirst().orElse(null); }
    public static List<Job> findJobsByMO(String moAccount) { return JOB_STORE.findAll().stream().filter(j -> j.getMoAccount().equals(moAccount)).collect(Collectors.toList()); }
    public static List<Job> findPublishedJobs() { return JOB_STORE.findAll().stream().filter(j -> j.getStatus() == model.enums.JobStatus.PUBLISHED && !j.isHired()).collect(Collectors.toList()); }
    public static List<Job> findPendingJobs() { return JOB_STORE.findAll().stream().filter(j -> j.getStatus() == model.enums.JobStatus.PENDING_REVIEW).collect(Collectors.toList()); }
    public static void clearAndRepopulateJobs(List<Job> jobs) { JOB_STORE.clear(); for (Job job : jobs) JOB_STORE.add(job); }
    public static List<Application> getAllApplications() { return APPLICATION_STORE.findAll(); }
    public static void addApplication(Application application) { APPLICATION_STORE.add(application); }
    public static void updateApplication(Application application) { APPLICATION_STORE.update(a -> a.getApplicationId().equals(application.getApplicationId()), application); }
    public static Application findApplicationById(String applicationId) { return APPLICATION_STORE.findAll().stream().filter(a -> a.getApplicationId().equals(applicationId)).findFirst().orElse(null); }
    public static List<Application> findApplicationsByTA(String taAccount) { return APPLICATION_STORE.findAll().stream().filter(a -> a.getTaAccount().equals(taAccount)).collect(Collectors.toList()); }
    public static List<Application> findApplicationsByJob(String jobId) { return APPLICATION_STORE.findAll().stream().filter(a -> a.getJobId().equals(jobId)).collect(Collectors.toList()); }
    public static void deleteApplication(String applicationId) { APPLICATION_STORE.removeIf(a -> a.getApplicationId().equals(applicationId)); }
    public static void clearAndRepopulateApplications(List<Application> applications) { APPLICATION_STORE.clear(); for (Application app : applications) APPLICATION_STORE.add(app); }
    public static List<Feedback> getAllFeedbacks() { return FEEDBACK_STORE.findAll(); }
    public static void addFeedback(Feedback feedback) { FEEDBACK_STORE.add(feedback); }
    public static void flush() {
        USER_STORE.save(); PROFILE_STORE.save(); CV_STORE.save();
        JOB_STORE.save(); APPLICATION_STORE.save(); FEEDBACK_STORE.save();
    }
}
