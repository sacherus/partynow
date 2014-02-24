package com.sacherus.partynow.rest;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.rest.RestService.Plurality;
import com.sacherus.utils.Utils;

public class UserRestHandler extends ResponseHandler {
	
	UserRestHandler(Context context, Plurality plur, Class objectClass) {
		super(context, plur, objectClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleResponse(String response) {	
		switch (plur) {
		case PLURAL:
			List<Party> users = (List<Party>) gson.fromJson(response, new TypeToken<List<Party>>() {
			}.getType());
			final ContentValues cvs[] = Utils.toContents(users);
			context.getContentResolver().bulkInsert(PartiesContract.PartyColumnHelper.CONTENT_URI, cvs);
			showMsg("Information for " + cvs.length + " users loaded");
			break;
		case SINGULAR:
			Party party = gson.fromJson(response, objectClass);
			context.getContentResolver().insert(PartiesContract.PartyColumnHelper.CONTENT_URI, party.toContent());
			showMsg("Information about " + party.getTitle() + " added");
			break;
		}
		
	}
}
