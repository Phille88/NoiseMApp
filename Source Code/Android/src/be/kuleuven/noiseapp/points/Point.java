package be.kuleuven.noiseapp.points;

import java.io.Serializable;

public class Point implements Serializable{

	private static final long serialVersionUID = 1L;
	private String description;
	private int point;
	
	public Point(String description, int point){
		this.description = description;
		this.point = point;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getPoint() {
		return point;
	}
}