package model;
import model.enums.UserRole;
public class Admin extends User {
    public Admin() { setRole(UserRole.ADMIN); }
    @Override
    public String getRoleDisplayName() { return "Administrator"; }
}
