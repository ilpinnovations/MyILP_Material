package com.ilp.ilpschedule.model;

public class Badge {
	private String name;
	private int points;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoints() {
		return points;
	}

	@Override
	public String toString() {
		return "Badge [name=" + name + ", points=" + points + "]";
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
