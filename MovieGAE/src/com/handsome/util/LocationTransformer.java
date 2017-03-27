package com.handsome.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class LocationTransformer {
	private String key = "&key=AIzaSyA7vIe4tb9qkYf4K4vSbr_FBPqFE0BAkXo";//web(Browser) using this one
	private String serverKey ="15af3ee1a51e333b85ca875d7da3f9c962d188db";//cron(Server itself) using this!!
	private String transformerUrl = "https://maps.google.com/maps/api/geocode/json?address=%s&language=zh-TW&sensor=false&region=tw"+serverKey;
	
	public LocationTransformer() {

	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public String[] getLocationInfoByAddress(String address)
			throws IOException, JSONException {

		String[] latLngStr = new String[2];

		String encodeAddress = URLEncoder.encode(address, "Utf-8");
		String formatAddress = String.format(transformerUrl, encodeAddress);
		InputStream is = new URL(formatAddress).openConnection()
				.getInputStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			// System.out.println(jsonText);

			try {
				Object status = new JSONObject(jsonText).get("status");
				//System.out.println(status);
				if (status.equals("OK")) {
					JSONArray arr = new JSONObject(jsonText)
							.getJSONArray("results");
					for (int i = 0; i < arr.length(); ++i) {
						if (arr.getJSONObject(i).get("geometry") != null) {
							Object lat = arr.getJSONObject(i)
									.getJSONObject("geometry")
									.getJSONObject("location").get("lat");
							Object lng = arr.getJSONObject(i)
									.getJSONObject("geometry")
									.getJSONObject("location").get("lng");

							latLngStr[0] = lat.toString();
							latLngStr[1] = lng.toString();
							//System.out.println(latLngStr[0]+","+latLngStr[1]);
							break;
						}
					}
				}else{
					return null;
				}

			} catch (JSONException e) {
			}

		} finally {
			is.close();
		}

		return latLngStr;
	}

}
