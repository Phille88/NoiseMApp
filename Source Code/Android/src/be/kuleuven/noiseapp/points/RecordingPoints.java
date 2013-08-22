package be.kuleuven.noiseapp.points;

import java.io.Serializable;
import java.util.ArrayList;

public class RecordingPoints implements Serializable {

	private static final long serialVersionUID = -4139367393544449851L;	
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Badge> badges = new ArrayList<Badge>();
	
	public RecordingPoints(ArrayList<Point> points, ArrayList<Badge> badges){
		this.points = points;
		this.badges = badges;
	}

	public void setPoints(ArrayList<Point> points){
		this.points = points;
	}
	
	public ArrayList<Point> getPoints(){
		return points;
	}

	public void setBadges(ArrayList<Badge> badges){
		this.badges = badges;
	}
	
	public ArrayList<Badge> getBadges() {
		return badges;
	}
	
	public int getTotalPoints(){
		int toReturn = 0;
		for(Point p : points)
			toReturn += p.getPoint();
		for(Badge b : badges)
			toReturn += b.getPoint();
		return toReturn;
	}
}
