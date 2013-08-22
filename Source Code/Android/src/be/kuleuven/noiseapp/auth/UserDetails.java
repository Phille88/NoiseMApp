package be.kuleuven.noiseapp.auth;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;

import be.kuleuven.noiseapp.soundcheckin.SoundCheckinDetails;

public class UserDetails implements Serializable, Comparator<UserDetails>{
	
	private static final long serialVersionUID = -2195746713260564986L;
	private String fName;
	private String lName;
	private String email;
	private long totalPoints;
	private long soundBattlesWon;
	private ArrayList<Integer> badgeIDs = new ArrayList<Integer>();
	private String pictureURL;
	private BigInteger googleID;
	private long userID;
	private SoundCheckinDetails lastSoundCheckin;
	
	public UserDetails(long userID, BigInteger googleID, String fName, String lName, String email, long totalPoints, long soundBattlesWon, ArrayList<Integer> badgeIDs, SoundCheckinDetails lastSoundCheckin, String pictureURL){
		setFName(fName);
		setLName(lName);
		setEmail(email);
		setTotalPoints(totalPoints);
		setSoundBattlesWon(soundBattlesWon);
		setBadgeIDs(badgeIDs);
		setPictureURL(pictureURL);
		setGoogleID(googleID);
		setUserID(userID);
		setLastSoundCheckin(lastSoundCheckin);
	}

	public UserDetails() {
		setFName(null);
		setLName(null);
		setEmail(null);
		setTotalPoints(0L);
		setSoundBattlesWon(0L);
		setPictureURL(null);
		setGoogleID(null);
		setUserID(0L);
		setLastSoundCheckin(null);
	}

	/**
	 * @return the fName
	 */
	public String getFName() {
		return fName;
	}

	/**
	 * @param fName the fName to set
	 */
	private void setFName(String fName) {
		this.fName = fName;
	}

	/**
	 * @return the lName
	 */
	public String getLName() {
		return lName;
	}

	/**
	 * @param lName the lName to set
	 */
	private void setLName(String lName) {
		this.lName = lName;
	}

	/**
	 * @return the totalPoints
	 */
	public long getTotalPoints() {
		return totalPoints;
	}

	/**
	 * @param totalPoints the totalPoints to set
	 */
	private void setTotalPoints(long totalPoints) {
		this.totalPoints = totalPoints;
	}

	/**
	 * @return the pictureURL
	 */
	public String getPictureURL() {
		return pictureURL;
	}

	/**
	 * @param pictureURL the pictureURL to set
	 */
	private void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public String getFullName() {
		return getFName() + " " + getLName();
	}
	
	public String getObfuscatedFullName(){
		return getFName() + " " + getLName().charAt(0) + ".";
	}

	private void setGoogleID(BigInteger googleID) {
		this.googleID = googleID;
	}

	public BigInteger getGoogleID() {
		return googleID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public long getUserID() {
		return userID;
	}
	
	@Override
	public boolean equals(Object other){
		UserDetails otherUD;
		if(other != null) {
			otherUD = (UserDetails) other;
			if(otherUD.userID == this.userID && otherUD.pictureURL.equals(this.pictureURL) && otherUD.totalPoints == this.totalPoints){
				return true;
			}
		}
		return false;
	}

	private void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * The comparison is conversed, so greater is first!
	 */
	@Override
	public int compare(UserDetails lhs, UserDetails rhs) {
		if(lhs.getTotalPoints() < rhs.getTotalPoints())
			return 1;
		else if(lhs.getTotalPoints() > rhs.getTotalPoints())
			return -1;
		else return 0;
	}

	private void setSoundBattlesWon(long soundBattlesWon) {
		this.soundBattlesWon = soundBattlesWon;
	}

	public long getSoundBattlesWon() {
		return soundBattlesWon;
	}

	private void setBadgeIDs(ArrayList<Integer> badgeIDs) {
		this.badgeIDs = badgeIDs;
	}

	public ArrayList<Integer> getBadgeIDs() {
		return badgeIDs;
	}

	public void setLastSoundCheckin(SoundCheckinDetails lastSoundCheckin) {
		this.lastSoundCheckin = lastSoundCheckin;
	}

	public SoundCheckinDetails getLastSoundCheckin() {
		return lastSoundCheckin;
	}

}
