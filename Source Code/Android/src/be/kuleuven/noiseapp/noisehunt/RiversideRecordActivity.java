package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.recording.RecordActivity;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;
import be.kuleuven.noiseapp.tools.Constants;

import com.google.android.gms.maps.model.LatLng;

import be.kuleuven.noiseapp.R;
public class RiversideRecordActivity extends RecordActivity {

	private static final double HUNDRED_METERS = 100;
	private ArrayList<NoiseLocation> recordedLocations = new ArrayList<NoiseLocation>();
	private ArrayList<ArrayList<LatLng>> boundaries = new ArrayList<ArrayList<LatLng>>();
	private ArrayList<LatLng> riverBoundary1 = new ArrayList<LatLng>();
	private ArrayList<LatLng> riverBoundary2 = new ArrayList<LatLng>();
	private RiversideRecordActivity rra;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rra = this;
		initializeRiverBoundaries();
	}

	private void initializeRiverBoundaries() {
		initializeRiverBoundary1();
		initializeRiverBoundary2();
	}

	private void initializeRiverBoundary1() {
		riverBoundary1.add(new LatLng(50.8923413607329,4.7057318687438965));
		riverBoundary1.add(new LatLng(50.889769628253,4.7057318687438965));
		riverBoundary1.add(new LatLng(50.88828066560043,4.705688953399658));
		riverBoundary1.add(new LatLng(50.88809115784861,4.7005391120910645));
		riverBoundary1.add(new LatLng(50.88698116839158,4.700496196746826));
		riverBoundary1.add(new LatLng(50.88665628842514,4.702298641204834));
		riverBoundary1.add(new LatLng(50.885519190700265,4.703843593597412));
		riverBoundary1.add(new LatLng(50.88457158806118,4.7051310539245605));
		riverBoundary1.add(new LatLng(50.884057167128304,4.705989360809326));
		riverBoundary1.add(new LatLng(50.884490364080975,4.707620143890381));
		riverBoundary1.add(new LatLng(50.885167226255746,4.709508419036865));
		riverBoundary1.add(new LatLng(50.88625018528097,4.7098517417907715));
		riverBoundary1.add(new LatLng(50.88646677406509,4.709036350250244));
		riverBoundary1.add(new LatLng(50.885762856834965,4.708435535430908));
		riverBoundary1.add(new LatLng(50.885302597510616,4.707019329071045));
		riverBoundary1.add(new LatLng(50.88514015195755,4.706161022186279));
		riverBoundary1.add(new LatLng(50.88560041288678,4.705088138580322));
		riverBoundary1.add(new LatLng(50.886547994599496,4.703671932220459));
		riverBoundary1.add(new LatLng(50.88671043524355,4.706161022186279));
		riverBoundary1.add(new LatLng(50.887278973037105,4.707748889923096));
		riverBoundary1.add(new LatLng(50.891150260184645,4.707791805267334));
		riverBoundary1.add(new LatLng(50.8923413607329,4.7057318687438965));
		boundaries.add(riverBoundary1);
	}
	
	private void initializeRiverBoundary2(){
		riverBoundary2.add(new LatLng(50.88684580201422,4.699122905731201));
		riverBoundary2.add(new LatLng(50.88633140619302,4.698307514190674));
		riverBoundary2.add(new LatLng(50.88600652169519,4.698479175567627));
		riverBoundary2.add(new LatLng(50.88589822635911,4.70015287399292));
		riverBoundary2.add(new LatLng(50.88532967171439,4.6996378898620605));
		riverBoundary2.add(new LatLng(50.88432791569576,4.699509143829346));
		riverBoundary2.add(new LatLng(50.88332613813692,4.699680805206299));
		riverBoundary2.add(new LatLng(50.88321783657009,4.69890832901001));
		riverBoundary2.add(new LatLng(50.88283877910357,4.697878360748291));
		riverBoundary2.add(new LatLng(50.88256802188226,4.697363376617432));
		riverBoundary2.add(new LatLng(50.88394886726078,4.698135852813721));
		riverBoundary2.add(new LatLng(50.88440913995918,4.697577953338623));
		riverBoundary2.add(new LatLng(50.88435499046595,4.695689678192139));
		riverBoundary2.add(new LatLng(50.88562748691748,4.69740629196167));
		riverBoundary2.add(new LatLng(50.88595237405861,4.696977138519287));
		riverBoundary2.add(new LatLng(50.88538382007476,4.695346355438232));
		riverBoundary2.add(new LatLng(50.88419254160872,4.694960117340088));
		riverBoundary2.add(new LatLng(50.883786416987434,4.696462154388428));
		riverBoundary2.add(new LatLng(50.88373226677044,4.697234630584717));
		riverBoundary2.add(new LatLng(50.8826763249596,4.696462154388428));
		riverBoundary2.add(new LatLng(50.88232433903776,4.696547985076904));
		riverBoundary2.add(new LatLng(50.88126836531743,4.694702625274658));
		riverBoundary2.add(new LatLng(50.88078098475926,4.694488048553467));
		riverBoundary2.add(new LatLng(50.88075390791212,4.695260524749756));
		riverBoundary2.add(new LatLng(50.882080654918745,4.696848392486572));
		riverBoundary2.add(new LatLng(50.881376671415794,4.6964192390441895));
		riverBoundary2.add(new LatLng(50.88075390791212,4.696633815765381));
		riverBoundary2.add(new LatLng(50.88048313857518,4.697878360748291));
		riverBoundary2.add(new LatLng(50.87991451784596,4.698221683502197));
		riverBoundary2.add(new LatLng(50.87964374363124,4.69740629196167));
		riverBoundary2.add(new LatLng(50.879156346079604,4.696977138519287));
		riverBoundary2.add(new LatLng(50.87847939657818,4.696934223175049));
		riverBoundary2.add(new LatLng(50.878181535682046,4.695475101470947));
		riverBoundary2.add(new LatLng(50.87766704419516,4.694573879241943));
		riverBoundary2.add(new LatLng(50.87728794157003,4.693715572357178));
		riverBoundary2.add(new LatLng(50.87615061519022,4.694273471832275));
		riverBoundary2.add(new LatLng(50.87501326105353,4.69440221786499));
		riverBoundary2.add(new LatLng(50.874986180854826,4.69590425491333));
		riverBoundary2.add(new LatLng(50.87387587915976,4.6961188316345215));
		riverBoundary2.add(new LatLng(50.87287387687503,4.6958184242248535));
		riverBoundary2.add(new LatLng(50.87206142677591,4.695346355438232));
		riverBoundary2.add(new LatLng(50.8716010321,4.695475101470947));
		riverBoundary2.add(new LatLng(50.87073439449093,4.694359302520752));
		riverBoundary2.add(new LatLng(50.87011148875652,4.694015979766846));
		riverBoundary2.add(new LatLng(50.869028154608586,4.692728519439697));
		riverBoundary2.add(new LatLng(50.86826980572436,4.692342281341553));
		riverBoundary2.add(new LatLng(50.86789062665585,4.692857265472412));
		riverBoundary2.add(new LatLng(50.86726768291898,4.6921706199646));
		riverBoundary2.add(new LatLng(50.86705100488531,4.691312313079834));
		riverBoundary2.add(new LatLng(50.86678015592695,4.6906256675720215));
		riverBoundary2.add(new LatLng(50.86631970908631,4.690282344818115));
		riverBoundary2.add(new LatLng(50.865696744357294,4.689853191375732));
		riverBoundary2.add(new LatLng(50.865642573117874,4.689080715179443));
		riverBoundary2.add(new LatLng(50.86529045852743,4.688308238983154));
		riverBoundary2.add(new LatLng(50.86501959934043,4.687449932098389));
		riverBoundary2.add(new LatLng(50.864586221368114,4.6862053871154785));
		riverBoundary2.add(new LatLng(50.864017406665795,4.685518741607666));
		riverBoundary2.add(new LatLng(50.86355693253815,4.684703350067139));
		riverBoundary2.add(new LatLng(50.86301519244778,4.682857990264893));
		riverBoundary2.add(new LatLng(50.862473446062744,4.68217134475708));
		riverBoundary2.add(new LatLng(50.862121307536924,4.681270122528076));
		riverBoundary2.add(new LatLng(50.86138993440853,4.6796393394470215));
		riverBoundary2.add(new LatLng(50.86076690380603,4.680454730987549));
		riverBoundary2.add(new LatLng(50.86087525755227,4.682557582855225));
		riverBoundary2.add(new LatLng(50.86195878116649,4.682857990264893));
		riverBoundary2.add(new LatLng(50.86149828670699,4.684402942657471));
		riverBoundary2.add(new LatLng(50.86239218356206,4.685862064361572));
		riverBoundary2.add(new LatLng(50.86320480219542,4.684875011444092));
		riverBoundary2.add(new LatLng(50.864098666333874,4.6868062019348145));
		riverBoundary2.add(new LatLng(50.86515502913064,4.6892523765563965));
		riverBoundary2.add(new LatLng(50.86493834127749,4.690067768096924));
		riverBoundary2.add(new LatLng(50.866698900932604,4.691312313079834));
		riverBoundary2.add(new LatLng(50.866834325844536,4.692728519439697));
		riverBoundary2.add(new LatLng(50.867998963847285,4.6936726570129395));
		riverBoundary2.add(new LatLng(50.868892736069775,4.693243503570557));
		riverBoundary2.add(new LatLng(50.86954274146817,4.694573879241943));
		riverBoundary2.add(new LatLng(50.87068022910533,4.695088863372803));
		riverBoundary2.add(new LatLng(50.871248962514684,4.696033000946045));
		riverBoundary2.add(new LatLng(50.8712760448849,4.696934223175049));
		riverBoundary2.add(new LatLng(50.87230516329265,4.697020053863525));
		riverBoundary2.add(new LatLng(50.87328009661386,4.6976637840271));
		riverBoundary2.add(new LatLng(50.87425500954181,4.698350429534912));
		riverBoundary2.add(new LatLng(50.87495910064039,4.697749614715576));
		riverBoundary2.add(new LatLng(50.87520282200387,4.696719646453857));
		riverBoundary2.add(new LatLng(50.87552778183923,4.695174694061279));
		riverBoundary2.add(new LatLng(50.876990073058174,4.695217609405518));
		riverBoundary2.add(new LatLng(50.87801906529986,4.696590900421143));
		riverBoundary2.add(new LatLng(50.87804614373621,4.697577953338623));
		riverBoundary2.add(new LatLng(50.87934589017771,4.697706699371338));
		riverBoundary2.add(new LatLng(50.879562511060016,4.6988654136657715));
		riverBoundary2.add(new LatLng(50.88061852344034,4.698994159698486));
		riverBoundary2.add(new LatLng(50.88118713557842,4.697363376617432));
		riverBoundary2.add(new LatLng(50.882188959129,4.697706699371338));
		riverBoundary2.add(new LatLng(50.88262217345239,4.698522090911865));
		riverBoundary2.add(new LatLng(50.882920005963165,4.699723720550537));
		riverBoundary2.add(new LatLng(50.88338028882594,4.70041036605835));
		riverBoundary2.add(new LatLng(50.88440913995918,4.7002387046813965));
		riverBoundary2.add(new LatLng(50.88549211660664,4.700710773468018));
		riverBoundary2.add(new LatLng(50.88649384759229,4.7011399269104));
		riverBoundary2.add(new LatLng(50.88684580201422,4.699122905731201));
		boundaries.add(riverBoundary2);
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!isProviderFixed())
					Toast.makeText(getApplicationContext(),
							"Wait for the GPS to have a fixed location",
							Toast.LENGTH_LONG).show();
				else if (!isInPark())
					Toast.makeText(
							getApplicationContext(),
							"You have to get into the city park!",
							Toast.LENGTH_LONG).show();
				
				else if(!isFurtherThan50m()){
					Toast.makeText(
							getApplicationContext(),
							"You are too close to previous recorded location(s)!",
							Toast.LENGTH_LONG).show();
				}
				else {
					new RiversideRecordTask(v,rra).execute(getCurrentLocation());
				}
		}
		};
	}	

	protected boolean isEverythingRecorded() {
		return this.getRecordedLocations().size() == 3;
	}
	
	private boolean isInPark(){
      boolean result = false;
      for (int i = 0, j = riverBoundary1.size() - 1; i < riverBoundary1.size(); j = i++) {
        if ((riverBoundary1.get(i).longitude > getCurrentLocation().getLongitude()) != (riverBoundary1.get(j).longitude > getCurrentLocation().getLongitude()) &&
            (getCurrentLocation().getLatitude() < (riverBoundary1.get(j).latitude - riverBoundary1.get(i).latitude) * (getCurrentLocation().getLongitude() - riverBoundary1.get(i).longitude) / (riverBoundary1.get(j).longitude-riverBoundary1.get(i).longitude) + riverBoundary1.get(i).latitude)) {
          result = !result;
         }
      }
      return result;
	}
	
	private boolean isFurtherThan50m(){
		for(NoiseLocation nl : getRecordedLocations())
			if(nl.getDistance(getCurrentLocation()) <= HUNDRED_METERS)
				return false;
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_noise_hunt_riverside);
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_noise_hunt_riverside;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_desc_noise_hunt_riverside;
	}
	
	protected void setHuntDone(){
		new SaveFinishedNoiseHuntTask(this).execute(getNoiseHuntID());
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return "R_DSA";
	}

	protected ArrayList<NoiseLocation> getRecordedLocations() {
		return recordedLocations;
	}
	
	@Override
	protected Location getCurrentLocation() {
		return super.getCurrentLocation();
	}
	
	protected int getNoiseHuntID(){
		return Constants.BLITZKRIEG_ID;
	}
}
