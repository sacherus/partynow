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
import com.sacherus.utils.Utils;

//not thread save
//aka ServiceHelper
public class RestApi {
	private RestClient rc = new RestClient();
	
	private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	private GPSTracker gpstracker;
	private User user = new User();
	private static final String LOGIN = "login";
	private static final String CLIENT_ID = "holenderskie";
	private static final String CLIENT_SECRET = "wCzekoladzie";

	final static String PARTY_PATH = "party/";
	final static String USER_PATH = "users/";
	final static String REGISTER_PATH = PARTY_PATH + USER_PATH + "register";
	final static String JOIN = "join";
	final static String ORGANIZE = "organize";
	
	// final static String PARTIES_IN_AREA_PATH = PARTY_PATH + "area/";

	private RestApi() {
		user.setId(1);
		user.setUsername("sacherus");
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
	 * TODO: 
	 *  sent to server party_id
	 *  a) sends only party_id, used_id taken from user with login
	 *  update/add party to database in response handler
	 */
	public void join(int partyId) {
		final String location = PARTY_PATH + Integer.toString(partyId) + "/" + JOIN;
		Utils.log(location);
		RestBuilder rb = new RestBuilder(getPartyIntent());
		rb.location(location);
		context.startService(rb.build());
	}
	
	public void organize(int partyId, int userId) {
		final String location = PARTY_PATH +  partyId + "/" + ORGANIZE + "/" + userId;
		RestBuilder rb = new RestBuilder(getPartyIntent());
		rb.location(location);
		context.startService(rb.build());
	}
	
	public User getMyUser() {
		return user;
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

	public boolean logout() {
		return false;
	}
	
	private Intent getPartyIntent() {
		Intent intent = new Intent(context, RestService.class);
		intent.putExtra(RestService.METHOD, Method.GET);
		intent.putExtra(RestService.CLASS, Party.class);
		intent.putExtra(RestService.PLURALITY, Plurality.SINGULAR);
		return intent;
	}

	public void getParty(int id) {
		Intent intent = getPartyIntent();
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

	public RestBuilder() {
		intent = new Intent();
	}
	
	public RestBuilder(Intent intent) {
		this.intent = intent;
	}

	public static RestBuilder createBuilder() {
		return new RestBuilder();
	}
	
	public RestBuilder location(String location) {
		intent.putExtra(RestService.LOCATION, location);
		return this;
	}

	Intent build() {
		return intent;
	}
}
