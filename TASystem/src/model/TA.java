package model;
import model.enums.UserRole;
public class TA extends User {
    public TA() { setRole(UserRole.TA); }
    @Override
    public String getRoleDisplayName() { return "Teaching Assistant"; }
}
