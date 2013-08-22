package be.kuleuven.noiseapp.points;


import be.kuleuven.noiseapp.R;
public class Badges {
	public static final int RECORDING1 = 0001;
	public static final int RECORDING50 = 0050;
	public static final int RECORDING100 = 0100;
	public static final int RECORDING500 = 0500;
	
	public static final String NAMERECORDING1 = "First Recording";
	public static final String NAMERECORDING50 = "Fifth Recording";
	public static final String NAMERECORDING100 = "Hundredth Recording";
	public static final String NAMERECORDING500 = "Five Hundredth Recording";
	
	public static final int SOUNDBATTLEWON1 = 1001;
	public static final int SOUNDBATTLEWON5 = 1005;
	public static final int SOUNDBATTLEWON20 = 1020;
	public static final int SOUNDBATTLEWON50 = 1050;
	public static final int SOUNDBATTLEWON100 = 1100;
	
	public static final String NAMESOUNDBATTLEWON1 = "First SoundBattle Win";
	public static final String NAMESOUNDBATTLEWON5 = "Fifth SoundBattle Win";
	public static final String NAMESOUNDBATTLEWON20 = "Twentieth SoundBattle Win";
	public static final String NAMESOUNDBATTLEWON50 = "Fiftieth SoundBattle Win";
	public static final String NAMESOUNDBATTLEWON100 = "Hundredth SoundBattle Win";

	public static final int NOISEHUNT1 = 2001;
	public static final int NOISEHUNT2 = 2002;
	public static final int NOISEHUNT3 = 2003;
	public static final int NOISEHUNT4 = 2004;
	
	public static final String NAMENOISEHUNT1 = "Walk In The Park";
	public static final String NAMENOISEHUNT2 = "Blitzkrieg";
	public static final String NAMENOISEHUNT3 = "Party Time";
	public static final String NAMENOISEHUNT4 = "Riverside";
	
	public static final int SOUNDCHECKIN1 = 3001;
	public static final int SOUNDCHECKIN50 = 3050;
	public static final int SOUNDCHECKIN100 = 3100;
	public static final int SOUNDCHECKIN500 = 3500;

	public static final String NAMESOUNDCHECKIN1 = "First Sound Checkin";
	public static final String NAMESOUNDCHECKIN50 = "Fifth Sound Checkin";
	public static final String NAMESOUNDCHECKIN100 = "Hundredth Sound Checkin";
	public static final String NAMESOUNDCHECKIN500 = "Five Hundredth Sound Checkin";
	
	public static int getBadgeImage(int badgeID){
		switch(badgeID){
		case RECORDING1:
			return R.drawable.recording1;
		case RECORDING50:
			return R.drawable.recording50;
		case RECORDING100:
			return R.drawable.recording100;
		case RECORDING500:
			return R.drawable.recording500;
		case SOUNDBATTLEWON1:
			return R.drawable.soundbattlewon1;
		case SOUNDBATTLEWON5:
			return R.drawable.soundbattlewon5;
		case SOUNDBATTLEWON20:
			return R.drawable.soundbattlewon20;
		case SOUNDBATTLEWON50:
			return R.drawable.soundbattlewon50;
		case SOUNDBATTLEWON100:
			return R.drawable.soundbattlewon100;
		case NOISEHUNT1:
			return R.drawable.noisehunt1;
		case NOISEHUNT2:
			return R.drawable.noisehunt2;
		case NOISEHUNT3:
			return R.drawable.noisehunt3;
		case NOISEHUNT4:
			return R.drawable.noisehunt4;
		case SOUNDCHECKIN1:
			return R.drawable.soundcheckin1;
		case SOUNDCHECKIN50:
			return R.drawable.soundcheckin50;
		case SOUNDCHECKIN100:
			return R.drawable.soundcheckin100;
		case SOUNDCHECKIN500:
			return R.drawable.soundcheckin500;
		default:
			return 0;	
		}
	}
	
	public static String getBadgeName(int badgeID){
		switch(badgeID){
		case RECORDING1:
			return NAMERECORDING1;
		case RECORDING50:
			return NAMERECORDING50;
		case RECORDING100:
			return NAMERECORDING100;
		case RECORDING500:
			return NAMERECORDING500;
		case SOUNDBATTLEWON1:
			return NAMESOUNDBATTLEWON1;
		case SOUNDBATTLEWON5:
			return NAMESOUNDBATTLEWON5;
		case SOUNDBATTLEWON20:
			return NAMESOUNDBATTLEWON20;
		case SOUNDBATTLEWON50:
			return NAMESOUNDBATTLEWON50;
		case SOUNDBATTLEWON100:
			return NAMESOUNDBATTLEWON100;
		case NOISEHUNT1:
			return NAMENOISEHUNT1;
		case NOISEHUNT2:
			return NAMENOISEHUNT2;
		case NOISEHUNT3:
			return NAMENOISEHUNT3;
		case NOISEHUNT4:
			return NAMENOISEHUNT4;
		case SOUNDCHECKIN1:
			return NAMESOUNDCHECKIN1;
		case SOUNDCHECKIN50:
			return NAMESOUNDCHECKIN50;
		case SOUNDCHECKIN100:
			return NAMESOUNDCHECKIN100;
		case SOUNDCHECKIN500:
			return NAMESOUNDCHECKIN500;
		default:
			return "";	
		}
	}
	
}
