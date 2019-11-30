package de.bp2019.zentraldatei.model;

import java.util.List;

public class UserTmp {
    private String surname;
    private String lastname;
    private String eMail;
    private String password;
    private UserType type;
    private List<InstituteTmp> institutes;

    public enum UserType{
        SuperAdmin,
        Admin,
        WiMi,
        HiWi
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public List<InstituteTmp> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(List<InstituteTmp> institutes) {
        this.institutes = institutes;
    }

    public UserTmp() {
    }
}
