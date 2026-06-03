package model;
import model.enums.UserRole;
public class MO extends User {
    public MO() { setRole(UserRole.MO); }
    @Override
    public String getRoleDisplayName() { return "Module Organizer"; }
}
