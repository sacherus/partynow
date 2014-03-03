package com.sacherus.partynow.pojos;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sacherus.partynow.provider.PartynowContracts;
import com.sacherus.partynow.provider.PartynowContracts.PartyColumnHelper;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

public class Party implements Serializable, IContentValuesPOJO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1746216876503061317L;
	private int id;
	private String title;
	private String description;
	private String start = "2014-02-25T18:00:00Z";
	private String end = "2014-02-25T18:00:00Z";
	private boolean isPrivate;
	private List<Integer> organizers;
	private List<Integer> participants;
	private double longitude;
	private double latitude;

	Party() {
		organizers = new ArrayList<Integer>();
		participants = new ArrayList<Integer>();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public List<Integer> getOrganizers() {
		return organizers;
	}

	public void setOrganizers(List<Integer> organizers) {
		this.organizers = organizers;
	}

	public List<Integer> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Integer> participants) {
		this.participants = participants;
	}
	
	private GregorianCalendar getStartDate() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(Utils.stringToDate(start)); 
		return (GregorianCalendar)cal;
	}
	
	private GregorianCalendar getEndDate() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(Utils.stringToDate(end)); 
		return (GregorianCalendar)cal;
	}
	
	public int getStartDay() {
		return getStartDate().DAY_OF_MONTH;
	}
	
	public int getStartMonth() {
		return getStartDate().MONTH + 1;
	}
	
	public int getStartYear(){
		return getStartDate().YEAR;
	}
	
	public int getEndDay() {
		return getEndDate().DAY_OF_MONTH;
	}
	
	public int getEndMonth() {
		return getEndDate().MONTH + 1;
	}
	
	public int getEndYear(){
		Log.i("lol","ry");
		return getEndDate().YEAR;
	}

	public String toString() {
		return title;
	}
	
	@Override
	public ContentValues toContent() {
		ContentValues cv = new ContentValues();
		cv.put(PartyColumnHelper.TITLE, getTitle());
		cv.put(PartyColumnHelper.LATITUDE, getLatitude());
		cv.put(PartyColumnHelper.LONGITUDE, getLongitude());
		cv.put(PartyColumnHelper.START, getStart());
		cv.put(PartyColumnHelper.DESCRIPTION_NAME, getDescription());
		cv.put(PartyColumnHelper.END, getEnd());
		cv.put(PartyColumnHelper._ID, getId());
		Gson gson = RestApi.i().getGson();
		cv.put(PartyColumnHelper.PARTICIPANTS, gson.toJson(getParticipants()));
		cv.put(PartyColumnHelper.ORGANIZERS, gson.toJson(getOrganizers()));
		Utils.log(cv.toString());
		return cv;
	}
	
	public boolean isOrganizedBy(Integer userId) {
		return organizers.contains(userId);
	}
	
	/*
	 * used in:
	 * Cursor -> Object, 
	 * Activity -> ContentProvider -> RestApi
	 */
	@Override
	public void fromContentLocal(ContentValues cv) {
		setId(cv.getAsInteger(PartynowContracts.PartyColumnHelper._ID));
		setTitle(cv.getAsString(PartynowContracts.PartyColumnHelper.TITLE));
		setLatitude(cv.getAsDouble(PartynowContracts.PartyColumnHelper.LATITUDE));
		setLongitude(cv.getAsDouble(PartynowContracts.PartyColumnHelper.LONGITUDE));
		setStart(cv.getAsString(PartyColumnHelper.START));
		setDescription(cv.getAsString(PartyColumnHelper.DESCRIPTION_NAME));
		setEnd(cv.getAsString(PartyColumnHelper.END));
		String organizersJson = cv.getAsString(PartyColumnHelper.ORGANIZERS);
		String participantsJson = cv.getAsString(PartyColumnHelper.PARTICIPANTS);
		Type collectionType = new TypeToken<ArrayList<Integer>>(){}.getType();
		List<Integer> organizers = RestApi.i().getGson().fromJson(organizersJson, collectionType);
		List<Integer> participants = RestApi.i().getGson().fromJson(participantsJson, collectionType);
		setOrganizers(organizers);
		setParticipants(participants);
	}

	public static Party fromContent(ContentValues cv) {
		Party party = new Party();
		party.fromContentLocal(cv);
		return party;
	}

	public static class PartyBuilder {
		private Party instance;

		public PartyBuilder() {
			instance = new Party();
		}

		public PartyBuilder addLongitude(double v) {
			instance.longitude = v;
			return this;
		}
		
		public PartyBuilder addLatitude(double v) {
			instance.latitude = v;
			return this;
		}
		
		public PartyBuilder addTitle(String v) {
			instance.title = v;
			return this;
		}
		
		public PartyBuilder addStartDate(String v) {
			instance.start = v;
			return this;
		}
		
		public PartyBuilder addEndDate(String v) {
			instance.end = v;
			return this;
		}
		
		public PartyBuilder addId(int id) {
			instance.id = id;
			return this;
		}
		
		public Party build() {
			return instance;
		}

	}

}
