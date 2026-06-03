package model;
import model.enums.UserRole;
public abstract class User {
    private String account;
    private String password;
    private String name;
    private String email;
    private UserRole role;
    private boolean frozen;
    public User() { this.frozen = false; }
    public User(String account, String password, String name, String email, UserRole role, boolean frozen) {
        this.account = account; this.password = password; this.name = name;
        this.email = email; this.role = role; this.frozen = frozen;
    }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
    public abstract String getRoleDisplayName();
    @Override
    public String toString() { return "User{account='" + account + "', name='" + name + "', role=" + role + "}"; }
}
