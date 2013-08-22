package be.kuleuven.noiseapp.points;

import java.io.Serializable;


public class Badge implements Serializable{

	private static final long serialVersionUID = 1L;
	private int badgeID;
	private String description;
	private int point;
	
	public Badge(int badgeID, String description, int point){
		setBadgeID(badgeID);
		setDescription(description);
		setPoint(point);
	}

	private void setBadgeID(int badgeID) {
		this.badgeID = badgeID;
	}

	public int getBadgeID() {
		return badgeID;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	private void setPoint(int point) {
		this.point = point;
	}

	public int getPoint() {
		return point;
	}
	
	public int getImage(){
		return Badges.getBadgeImage(getBadgeID());	
	}
	
	public String getName(){
		return Badges.getBadgeName(getBadgeID());
	}
}
