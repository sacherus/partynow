package com.sacherus.partynow.rest;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Contacts.Intents.Insert;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.rest.RestService.Plurality;
import com.sacherus.utils.Utils;

public class RestHandler extends ResponseHandler {

	private static String TAG = RestHandler.class.getName();
	
	RestHandler(Context context, Plurality plur, Class objectClass) {
		super(context, plur, objectClass);
	}

	@Override
	public void handleResponse(String response) {	
		switch (plur) {
		case PLURAL:
			List<Party> parties = (List<Party>) gson.fromJson(response, new TypeToken<List<Party>>() {
			}.getType());
			final ContentValues cvs[] = Utils.toContents(parties);
			context.getContentResolver().bulkInsert(PartiesContract.PartyColumnHelper.CONTENT_URI, cvs);
			showMsg("Parties " + cvs.length + " loaded");
			break;
		case SINGULAR:
			Party party = gson.fromJson(response, objectClass);
			context.getContentResolver().insert(PartiesContract.PartyColumnHelper.CONTENT_URI, party.toContent());
			showMsg("Party " + party.getTitle() + " added");
			break;
		}
	}
	
//	@Override
//	public void handleResponse(Exception e) {
//		// TODO Auto-generated method stub
//		
//	}
}
