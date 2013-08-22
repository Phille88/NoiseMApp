package be.kuleuven.noiseapp.auth;

/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

import com.google.android.gms.auth.GoogleAuthUtil;

/**
 * Display personalized greeting. This class contains boilerplate code to
 * consume the token but isn't integral to getting the tokens.
 */
public abstract class AbstractGetInfoTask extends AsyncTask<Void, Void, UserDetails> {
	private static final String TAG = "TokenInfoTask";
	private static final String GOOGLEID_KEY = "id";
	private static final String NAME_KEY = "given_name";
	private static final String FAMILY_NAME_KEY = "family_name";
	private static final String PICTURE_KEY = "picture";

	protected MainActivity mActivity;
	protected String mScope;
	protected String mEmail;
	protected int mRequestCode;

	AbstractGetInfoTask(MainActivity activity, String email, String scope, int requestCode) {
		this.mActivity = activity;
		this.mScope = scope;
		this.mEmail = email;
		this.mRequestCode = requestCode;
	}

	@Override
	protected UserDetails doInBackground(Void... params) {
		try {
			return fetchInfoFromGoogleServer();
		} catch (IOException ex) {
			onError("Following Error occured, please try again. " + ex.getMessage(), ex);
		} catch (JSONException e) {
			onError("Bad response: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(UserDetails userDetails) {
		saveUserProfile(userDetails);
		return;
	}

	protected void onError(String msg, Exception e) {
		if (e != null) {
			Log.e(TAG, "Exception: ", e);
		}
	}

	/**
	 * Get a authentication token if one is not available. If the error is not
	 * recoverable then it displays the error message on parent activity.
	 */
	protected abstract String fetchToken() throws IOException;

	/**
	 * Contacts the user info server to get the profile of the user and extracts
	 * the first name of the user from the profile. In order to authenticate
	 * with the user info server the method first fetches an access token from
	 * Google Play services.
	 * 
	 * @throws IOException
	 *             if communication with user info server failed.
	 * @throws JSONException
	 *             if the response from the server could not be parsed.
	 */
	private UserDetails fetchInfoFromGoogleServer() throws IOException, JSONException {
		String token = fetchToken();
		if (token == null) {
			// error has already been handled in fetchToken()
			return null;
		}
		URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		int sc = con.getResponseCode();
		if (sc == 200) {
			InputStream is = con.getInputStream();
			String response = readResponse(is);
			JSONObject profile = new JSONObject(response);
			BigInteger googleID = getGoogleID(profile);
			String firstName = getFirstName(profile);
			String lastName = getLastName(profile);
			String pictureURL = getPicture(profile);
			is.close();
			return new UserDetails(0L, googleID, firstName, lastName, mEmail, 0L, 0L, new ArrayList<Integer>(), null, pictureURL);
		} else if (sc == 401) {
			GoogleAuthUtil.invalidateToken(mActivity, token);
			onError("Server auth error, please try again.", null);
			Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
			return null;
		} else {
			onError("Server returned the following error code: " + sc, null);
			Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
			return null;
		}
	}

	private void saveUserProfile(UserDetails userDetails) {
		if(userDetails != null){ //TODO check
			saveUserProfileLocal(userDetails);
			saveUserProfileServer(userDetails); 
		}
	}

	/**
	 * Reads the response from the input stream and returns it as a string.
	 */
	private static String readResponse(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len = 0;
		while ((len = is.read(data, 0, data.length)) >= 0) {
			bos.write(data, 0, len);
		}
		return new String(bos.toByteArray(), "UTF-8");
	}

	private void saveUserProfileLocal(UserDetails userDetails) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
		Editor edit = sp.edit();
		edit.putString(MemoryFileNames.USERDETAILS, ObjectSerializer.serialize(userDetails));
		edit.commit();
		if (userDetails != null){ //TODO check!
			new ImageDownloaderTask(mActivity, MemoryFileNames.PROFILE_PICTURE).execute(userDetails.getPictureURL());
		}
	}

	private void saveUserProfileServer(UserDetails userDetails) {
		new CreateUserProfileTask(mActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userDetails);
	}

	private BigInteger getGoogleID(JSONObject profile) throws JSONException {
		try{
			return new BigInteger(profile.getString(GOOGLEID_KEY));
		}
		catch (JSONException e){
			e.printStackTrace();
			return new BigInteger("0");
		}
	}

	/**
	 * Parses the response and returns the first name of the user.
	 * 
	 * @throws JSONException
	 *             if the response is not JSON or if first name does not exist
	 *             in response
	 */
	private String getFirstName(JSONObject profile) {
		try {
			return profile.getString(NAME_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			return "Unknown";
		}
	}

	/**
	 * Parses the response and returns the last name of the user.
	 * 
	 * @throws JSONException
	 *             if the response is not JSON or if first name does not exist
	 *             in response
	 */
	private String getLastName(JSONObject profile) {
		try {
			return profile.getString(FAMILY_NAME_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			return "Unknown";
		}
	}

	private String getPicture(JSONObject profile) {
		try {
			return profile.getString(PICTURE_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			return "http://i.stack.imgur.com/WmvM0.png";
		}
		
	}
}
