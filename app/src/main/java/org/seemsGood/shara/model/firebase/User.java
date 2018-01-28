package org.seemsGood.shara.model.firebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class User implements Serializable{

	private String email;

	private String firstName;
	private String fatherName;
	private String lastName;

	private int sex;
	private int age;

	private String phone;

	private String registerDate;

	private int familySize = 1;

	private String relationship;
	private String education;
	private String status;

	private List<UserAddress> addresses;

	private Map<String,List<String>> orders;
	private Map<String,List<String>> reviews;
	private Map<String,String> social;
	private Map<String,Boolean> views;

	private List<String> favoriteCompanies;

	public User() {
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public User setSex(int sex) {
		this.sex = sex;
		return this;
	}

	public User setAge(int age) {
		this.age = age;
		return this;
	}

	public User setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public User setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
		return this;
	}

	public User setFamilySize(int familySize) {
		this.familySize = familySize;
		return this;
	}

	public User setRelationship(String relationship) {
		this.relationship = relationship;
		return this;
	}

	public User setEducation(String education) {
		this.education = education;
		return this;
	}

	public User setStatus(String status) {
		this.status = status;
		return this;
	}

	public User setAddresses(List<UserAddress> addresses) {
		this.addresses = addresses;
		return this;
	}

	public User setOrders(Map<String, List<String>> orders) {
		this.orders = orders;
		return this;
	}

	public User setReviews(Map<String, List<String>> reviews) {
		this.reviews = reviews;
		return this;
	}

	public User setSocial(Map<String, String> social) {
		this.social = social;
		return this;
	}

	public User setViews(Map<String, Boolean> views) {
		this.views = views;
		return this;
	}

	public User setFavoriteCompanies(List<String> favoriteCompanies) {
		this.favoriteCompanies = favoriteCompanies;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getSex() {
		return sex;
	}

	public int getAge() {
		return age;
	}

	public String getPhone() {
		return phone;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public int getFamilySize() {
		return familySize;
	}

	public String getRelationship() {
		return relationship;
	}

	public String getEducation() {
		return education;
	}

	public String getStatus() {
		return status;
	}

	public List<UserAddress> getAddresses() {
		return addresses;
	}

	public Map<String, List<String>> getOrders() {
		return orders;
	}

	public Map<String, List<String>> getReviews() {
		return reviews;
	}

	public Map<String, String> getSocial() {
		return social;
	}

	public Map<String, Boolean> getViews() {
		return views;
	}

	public List<String> getFavoriteCompanies() {
		return favoriteCompanies;
	}
}
