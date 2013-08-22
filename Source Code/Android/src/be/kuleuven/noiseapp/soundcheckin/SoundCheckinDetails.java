package be.kuleuven.noiseapp.soundcheckin;

import java.io.Serializable;

public class SoundCheckinDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1454401980925145469L;
	private String placeName;
	private double dB;

	public SoundCheckinDetails(String placeName, double dB){
		this.setPlaceName(placeName);
		this.setdB(dB);
	}

	private void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceName() {
		return placeName;
	}

	private void setdB(double dB) {
		this.dB = dB;
	}

	public double getDB() {
		return dB;
	}
}
