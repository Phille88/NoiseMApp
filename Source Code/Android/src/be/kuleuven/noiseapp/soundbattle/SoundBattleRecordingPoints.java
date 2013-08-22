package be.kuleuven.noiseapp.soundbattle;

import java.io.Serializable;

import be.kuleuven.noiseapp.points.Point;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.tools.JSONTags;

public class SoundBattleRecordingPoints implements Serializable{

	private static final long serialVersionUID = -6323600420481507867L;
	private RecordingPoints userPoints;
	private RecordingPoints opponentPoints;

	public SoundBattleRecordingPoints(RecordingPoints userPoints, RecordingPoints opponentPoints){
		setUserPoints(userPoints);
		setOpponentPoints(opponentPoints);
	}

	public void setUserPoints(RecordingPoints userPoints) {
		this.userPoints = userPoints;
	}

	public RecordingPoints getUserPoints() {
		return userPoints;
	}

	public void setOpponentPoints(RecordingPoints opponentPoints) {
		this.opponentPoints = opponentPoints;
	}

	public RecordingPoints getOpponentPoints() {
		return opponentPoints;
	}
	
	public int getUserQualityPoints(){
		int toReturn = 0;
		for(Point p : userPoints.getPoints())
			if(p.getDescription().equals(JSONTags.QUALITY))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getUserAccuracyPoints(){
		int toReturn = 0;
		for(Point p : userPoints.getPoints())
			if(p.getDescription().equals(JSONTags.ACCURACY))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getUserSpeedPoints(){
		int toReturn = 0;
		for(Point p : userPoints.getPoints())
			if(p.getDescription().equals(JSONTags.SPEED))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getUserTotalPoints(){
		return userPoints.getTotalPoints();
	}
	
	public int getOpponentQualityPoints(){
		int toReturn = 0;
		for(Point p : opponentPoints.getPoints())
			if(p.getDescription().equals(JSONTags.QUALITY))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getOpponentAccuracyPoints(){
		int toReturn = 0;
		for(Point p : opponentPoints.getPoints())
			if(p.getDescription().equals(JSONTags.ACCURACY))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getOpponentSpeedPoints(){
		int toReturn = 0;
		for(Point p : opponentPoints.getPoints())
			if(p.getDescription().equals(JSONTags.SPEED))
				toReturn = p.getPoint();
		return toReturn;
	}
	
	public int getOpponentTotalPoints(){
		return opponentPoints.getTotalPoints();
	}
	
}
