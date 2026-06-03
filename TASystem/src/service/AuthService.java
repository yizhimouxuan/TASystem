package service;
import model.User;
import data.DataStore;
import util.PasswordUtil;
public class AuthService {
    private AuthService() {}
    public static boolean register(User user) {
        if (DataStore.findUserByAccount(user.getAccount()) != null) return false;
        user.setPassword(PasswordUtil.hash(user.getPassword()));
        user.setFrozen(false);
        DataStore.addUser(user);
        return true;
    }
    public static User login(String account, String password) {
        User user = DataStore.findUserByAccount(account);
        if (user == null) return null;
        if (!user.getPassword().equals(PasswordUtil.hash(password))) return null;
        if (user.isFrozen()) return null;
        return user;
    }
    public static boolean changePassword(String account, String oldPassword, String newPassword) {
        User user = DataStore.findUserByAccount(account);
        if (user == null) return false;
        if (!user.getPassword().equals(PasswordUtil.hash(oldPassword))) return false;
        user.setPassword(PasswordUtil.hash(newPassword));
        DataStore.updateUser(user);
        return true;
    }
    public static boolean resetPassword(String account, String email, String newPassword) {
        User user = DataStore.findUserByAccount(account);
        if (user == null) return false;
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) return false;
        user.setPassword(PasswordUtil.hash(newPassword));
        DataStore.updateUser(user);
        return true;
    }
}
