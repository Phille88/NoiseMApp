package be.kuleuven.noiseapp.soundcheckin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class PlaceSearchTask extends AsyncTask<URI, Void, String> {

	HttpClient client;
	
	@Override
	protected String doInBackground(URI... uri) {
		BufferedReader in = null;
		String data = null;
		try{
			client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(uri[0]);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");
			while((l = in.readLine()) != null)
				sb.append(l + nl);
			in.close();
			data = sb.toString();
			return data;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(in != null){
				try{
					in.close();
					return data;
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return data;
	}
}
