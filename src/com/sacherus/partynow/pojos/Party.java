package com.sacherus.partynow.pojos;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;

import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.provider.PartiesContract.PartyColumnHelper;
import com.sacherus.utils.Utils;

public class Party implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1746216876503061317L;
	private int id;
	private String title;
	private String description;
	private String start;
	private String end;
	private boolean isPrivate;
	private List<Integer> organizers;
	private List<Integer> participants;
	private double longitude;
	private double latitude;

	
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
		return getStartDate().get(Calendar.DAY_OF_MONTH);
	}
	
	public int getStartMonth() {
		return getStartDate().get(Calendar.MONTH);
	}
	
	public int getStartYear(){
		return getStartDate().get(Calendar.YEAR);
	}
	
	public int getEndDay() {
		return getEndDate().get(Calendar.DAY_OF_MONTH);
	}
	
	public int getEndMonth() {
		return getEndDate().get(Calendar.MONTH);
	}
	
	public int getEndYear(){
		return getEndDate().get(Calendar.YEAR);
	}

	public String toString() {
		return title;
	}
	
	public ContentValues toContent() {
		ContentValues cv = new ContentValues();
		cv.put(PartyColumnHelper.TITLE, getTitle());
		cv.put(PartyColumnHelper.LATITUDE, getLatitude());
		cv.put(PartyColumnHelper.LONGITUDE, getLongitude());
		cv.put(PartyColumnHelper.START, getStart());
		cv.put(PartyColumnHelper.DESCRIPTION_NAME, getDescription());
		cv.put(PartyColumnHelper.END, getEnd());
		cv.put(PartyColumnHelper._ID, getId());
		
		return cv;
	}
	
	/*
	 * used in:
	 * Cursor -> Object, 
	 * Activity -> ContentProvider -> RestApi
	 */
	private void fromContentLocal(ContentValues cv) {
		setTitle(cv.getAsString(PartiesContract.PartyColumnHelper.TITLE));
		setLatitude(cv.getAsDouble(PartiesContract.PartyColumnHelper.LATITUDE));
		setLongitude(cv.getAsDouble(PartiesContract.PartyColumnHelper.LONGITUDE));
		setStart(cv.getAsString(PartyColumnHelper.START));
		setDescription(cv.getAsString(PartyColumnHelper.DESCRIPTION_NAME));
		setEnd(cv.getAsString(PartyColumnHelper.END));
		
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
			instance.longitude = v;
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
		
		public Party build() {
			return instance;
		}

	}

}
