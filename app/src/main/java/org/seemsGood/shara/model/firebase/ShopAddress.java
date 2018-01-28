package org.seemsGood.shara.model.firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShopAddress extends Address {

	private List<WorkTime> workTimes;
	private String tags;

	private int timeTo = -1;
	private double rating = 0.0;
	private List<String> picturesNames;
	private List<Integer> points;

	public ShopAddress(){
		points = new ArrayList<>();
		points.add(0);
		points.add(0);
		points.add(0);
		points.add(0);
		points.add(0);
	}

	public List<WorkTime> getWorkTimes() {
		return workTimes;
	}

	public WorkTime getWorkTime(int i) {
		return workTimes.get(i);
	}

	public void setWorkTimes(List<WorkTime> workTimes) {
		this.workTimes = workTimes;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(int timeTo) {
		this.timeTo = timeTo;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public List<Integer> getPoints() {
		return points;
	}

	public void setPoints(List<Integer> points) {
		this.points = points;
	}

	public WorkTime getTodayWorkTime(){
		int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
		d = d < 0 ? d+7: d;
		return workTimes.get(d);
	}

	public List<String> getPicturesNames() {
		return picturesNames;
	}

	public void setPicturesNames(List<String> picturesNames) {
		this.picturesNames = picturesNames;
	}
}
