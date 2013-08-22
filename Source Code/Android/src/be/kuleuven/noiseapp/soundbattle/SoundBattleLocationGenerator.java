package be.kuleuven.noiseapp.soundbattle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import be.kuleuven.noiseapp.exception.NotInLeuvenException;

public class SoundBattleLocationGenerator {
	private static double minLat = 50.8551156;
	private static double maxLat = 50.9022189;
	private static double minLon = 4.6662606;
	private static double maxLon = 4.7200426;
	
	private static int amountLon = 5;
	private static int amountLat = 7;
	
	private static String[] coords;
	private static Random r;
	private static final double NL_RADIUS_METRES = 100;
	private static Location playerLocation;
	private static Context context;
	
	public SoundBattleLocationGenerator(Context context, Location playerLocation){
		SoundBattleLocationGenerator.playerLocation = playerLocation;
		SoundBattleLocationGenerator.context = context;
	}
	
	public void generate() throws NotInLeuvenException{
		String input = readTextFile();
		coords = input.split("\n");
		r = new Random();
	}

	public static String readTextFile() throws NotInLeuvenException {
		double unitLon = ((maxLon-minLon)/amountLon); //calculate dividers
		double unitLat = ((maxLat-minLat)/amountLat);
		
	    String returnValue = "";
	    InputStream file = null;
	    String line = "";
	    try {
	    	AssetManager am = context.getResources().getAssets();
	    	//file=am.open("leuvencoordsreduced.txt");
	    	for(int iLon = 0; iLon < amountLon; iLon++)
				for (int iLat = 0; iLat < amountLat; iLat++){
					double lowerboundLon = minLon+iLon*unitLon;
					double lowerboundLat = minLat+iLat*unitLat;
					double upperboundLon = minLon+(iLon+1)*unitLon ;
					double upperboundLat = minLat+(iLat+1)*unitLat;
					if(playerLocation.getLongitude() > lowerboundLon && playerLocation.getLatitude() > lowerboundLat && playerLocation.getLongitude() <= upperboundLon && playerLocation.getLatitude() <= upperboundLat)
						file = am.open("quadrant" + iLon + iLat + ".txt");
				}
	        BufferedReader reader = new BufferedReader(new InputStreamReader(file),1024*500);
	        while ((line = reader.readLine()) != null) {
	            returnValue += line + "\n";
	        }
	    } 
	    catch (FileNotFoundException e) {
	            throw new RuntimeException("File not found");
	    } 
	    catch (IOException e) {    
	    	throw new RuntimeException("IO Error occured");
	    }
	    catch (NullPointerException e){
	    	throw new NotInLeuvenException("You are not near Leuven. Restart the application when you are.");
	    }
	    return returnValue;

	}

    public static void writeTextFile(String outputFileName, String s) {
        FileWriter output;
        try {
            output = new FileWriter(outputFileName);
            BufferedWriter writer = new BufferedWriter(output,9*1024);
            writer.write(s);
            
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public SoundBattleLocation getRandomSoundBattleLocation(Location currentLocation) {
		int randomIndex = r.nextInt(coords.length);
		
		while(getDistance(currentLocation, coords[randomIndex]) > NL_RADIUS_METRES)
			randomIndex = r.nextInt(coords.length);

		String[] coord = coords[randomIndex].split(", ");
		double lon = Double.parseDouble(coord[0]);
		double lat = Double.parseDouble(coord[1]);
		return new SoundBattleLocation(lon, lat);
	}
	
	public ArrayList<SoundBattleLocation> getAllSoundBattleLocations(Location currentLocation) {
		ArrayList<SoundBattleLocation> toReturn = new ArrayList<SoundBattleLocation>();
		for(int i = 0; i < coords.length; i++){
			String[] coord = coords[i].split(", ");
			double lon = Double.parseDouble(coord[0]);
			double lat = Double.parseDouble(coord[1]);
			toReturn.add(new SoundBattleLocation(lon, lat));
		}
		return toReturn;
	}
	
	private static double getDistance(Location l, String s){
		String[] coords = s.split(", ");
		double lon = Double.parseDouble(coords[0]);
		double lat = Double.parseDouble(coords[1]);
		
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(l.getLatitude()-lat);
	    double dLng = Math.toRadians(l.getLongitude()-lon);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(l.getLatitude())) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;
	    return dist*meterConversion;
	}
}