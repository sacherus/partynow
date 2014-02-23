package com.sacherus.partynow.rest;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.gpstracking.GPSTracker;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sacherus.partynow.activities.RegisterActivity;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.pojos.Token;
import com.sacherus.partynow.pojos.User;
import com.sacherus.partynow.rest.RestService.Method;
import com.sacherus.partynow.rest.RestService.Plurality;

//not thread save
//aka ServiceHelper
public class RestApi {
	private RestClient rc = new RestClient();
	
	private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	private GPSTracker gpstracker;

	private static final String LOGIN = "login";
	private static final String CLIENT_ID = "holenderskie";
	private static final String CLIENT_SECRET = "wCzekoladzie";

	final static String PARTY_PATH = "party/";
	final static String USER_PATH = "users/";
	final static String REGISTER_PATH = PARTY_PATH + USER_PATH + "register";
	final static String JOIN_PATH = PARTY_PATH + "join/";

	// final static String PARTIES_IN_AREA_PATH = PARTY_PATH + "area/";

	private RestApi() {
	}

	private Context context;

	public void init(Context cont) {
		context = cont.getApplicationContext();
		gpstracker = new GPSTracker(context);
	}

	private static RestApi instance;

	public static RestApi i() {
		if (instance == null) {
			instance = new RestApi();
		}
		return instance;
	}
	
	/*
	 * should add party object to content provider
	 */
	public void join(int partyId) {
		
	}

	public void getToken(String user, String password) throws IOException {
		final String OUTH2_TOKEN = "oauth2/access_token/";
		final String data = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
				+ "&grant_type=password&username=" + user + "&password=" + password;
		String jsonToken;
		jsonToken = rc.sendData(OUTH2_TOKEN, data);
		Token token = gson.fromJson(jsonToken, Token.class);
		RestClient.setToken(token);
		Log.d("AccessToken", jsonToken);
		Log.d("token", token.getAccessToken());
	}

	/*
	 * Working in main thread
	 */
	public void register(User user) throws IOException {
		final String data = gson.toJson(user);
		rc.sendJSON(REGISTER_PATH, data);
	}

	// public List<Party> getParties() {
	// String json;
	// try {
	// json = rc.getData(PARTY_PATH);
	// Log.d(this.getClass().toString(), json);
	// List<Party> parties = (List<Party>) gson.fromJson(json, new
	// TypeToken<List<Party>>() {
	// }.getType());
	// Log.d(this.getClass().toString(), parties.toString());
	// return parties;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }

	// public void sendParty(Party party) throws IOException {
	// String json = gson.toJson(party);
	// rc.sendJSON(PARTY, json);
	// }

	public boolean logout() {
		return false;
	}

	public void TestAPI() {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.TEST_INTENT, "hello");
		context.startService(intent);
	}

	public void getParty(int id) {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.METHOD, Method.GET);
		intent.putExtra(RestService.CLASS, Party.class);
		intent.putExtra(RestService.PLURALITY, Plurality.SINGULAR);
		intent.putExtra(RestService.LOCATION, PARTY_PATH + Integer.toString(id));
		context.startService(intent);
	}

	public void getParties() {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.METHOD, Method.GET);
		intent.putExtra(RestService.CLASS, Party.class);
		intent.putExtra(RestService.PLURALITY, Plurality.PLURAL);
		intent.putExtra(RestService.LOCATION, PARTY_PATH);
		context.startService(intent);
	}

	private Intent getPartiesIntent() {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.METHOD, Method.GET);
		intent.putExtra(RestService.CLASS, Party.class);
		intent.putExtra(RestService.PLURALITY, Plurality.PLURAL);
		intent.putExtra(RestService.CLASS, Party.class);
		return intent;
	}

	private String preparePartiesInAreaLocation(double kms) {
		return PARTY_PATH + "?area=" + kms + "&longitude=" + gpstracker.getLongitude() + "&latitude="
				+ gpstracker.getLatitude();
	}

	public void getPartiesInArea(double kms) {
		Intent intent = getPartiesIntent();
		intent.putExtra(RestService.LOCATION, preparePartiesInAreaLocation(kms));
		context.startService(intent);
	}

	public void sendParty(Party party) {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.METHOD, Method.POST);
		intent.putExtra(RestService.LOCATION, PARTY_PATH);
		intent.putExtra(RestService.PLURALITY, Plurality.SINGULAR);
		intent.putExtra(RestService.DATA, gson.toJson(party));
		intent.putExtra(RestService.CLASS, Party.class);
		context.startService(intent);
	}

	public void msg(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void shortMsg(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}

class RestBuilder {
	private Intent intent;

	RestBuilder addString(String name, String str) {
		intent.putExtra(name, str);
		return this;
	}

	private RestBuilder() {
	}

	public static RestBuilder createBuilder() {
		return new RestBuilder();
	}

	Intent produce() {
		return intent;
	}
}
