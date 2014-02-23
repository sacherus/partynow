package com.sacherus.partynow.pojos;

import java.io.Serializable;
import java.util.List;

import android.content.ContentValues;

import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.provider.PartiesContract.PartyColumnHelper;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

public class Party implements Serializable {
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

	public String toString() {
		return title;
	}

	public ContentValues toContent() {
		ContentValues cv = new ContentValues();
		Utils.log(getId());
		cv.put(PartyColumnHelper.TITLE, getTitle());
		cv.put(PartyColumnHelper.LATITUDE, getLatitude());
		cv.put(PartyColumnHelper.LONGITUDE, getLongitude());
		cv.put(PartyColumnHelper._ID, getId());
		return cv;
	}

	private void fromContentLocal(ContentValues cv) {
		setLatitude(cv.getAsDouble(PartiesContract.PartyColumnHelper.LATITUDE));
		setLongitude(cv.getAsDouble(PartiesContract.PartyColumnHelper.LONGITUDE));
		setTitle(cv.getAsString(PartiesContract.PartyColumnHelper.TITLE));
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
		
		public Party build() {
			return instance;
		}

	}

}
