package be.kuleuven.noiseapp.profile;

import be.kuleuven.noiseapp.auth.UserDetails;

public class FriendItem {
	
	private UserDetails userDetails;
	
	public FriendItem(UserDetails userDetails){
		this.userDetails = userDetails;
	}

	/**
	 * @return the fName
	 */
	public String getFName() {
		return userDetails.getFName();
	}

	/**
	 * @return the lName
	 */
	public String getLName() {
		return userDetails.getLName();
	}

	/**
	 * @return the totalPoints
	 */
	public long getTotalPoints() {
		return userDetails.getTotalPoints();
	}

	public String getFullName() {
		return userDetails.getFullName();
	}
	
	public String getObfuscatedFullName() {
		return userDetails.getObfuscatedFullName();
	}

	public long getUserID() {
		return userDetails.getUserID();
	}
	
	public UserDetails getUserDetails(){
		return userDetails;
	}

}
