package org.seemsGood.shara.model.firebase;

import java.util.Map;

public class Review {

	private String title;
	private int points;
	private String pluses;
	private String minuses;
	private String description;
	private String date;
	private String address;
	private String userID;
	private int status = 0;

	private String userName;
	private String userImageRef;

	private Map<String,String> answer;

	public Review(){}

	public Review(String title, int points, String pluses, String minuses, String description, String date, String address, String userID) {
		this.title = title;
		this.points = points;
		this.pluses = pluses;
		this.minuses = minuses;
		this.description = description;
		this.date = date;
		this.address = address;
		this.userID = userID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getPluses() {
		return pluses;
	}

	public void setPluses(String pluses) {
		this.pluses = pluses;
	}

	public String getMinuses() {
		return minuses;
	}

	public void setMinuses(String minuses) {
		this.minuses = minuses;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImageRef() {
		return userImageRef;
	}

	public void setUserImageRef(String userImageRef) {
		this.userImageRef = userImageRef;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, String> getAnswer() {
		return answer;
	}

	public void setAnswer(Map<String, String> answer) {
		this.answer = answer;
	}
}
