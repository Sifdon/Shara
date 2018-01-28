package org.seemsGood.shara.model;

import java.util.Date;

public class NewUser {

    private String email;

    private String firstName;
    private String lastName;
    private String fatherName;

    private int gender;
    private int age;

    private String phone;
    private Date registerDate;
    private Date birthDate;

    public NewUser(String email, Date registerDate) {
        this.email = email;
        this.registerDate = registerDate;
    }

    public void setData(String firstName, String lastName, String fatherName, int sex, int age, Date birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.gender = sex;
        this.age = age;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}
