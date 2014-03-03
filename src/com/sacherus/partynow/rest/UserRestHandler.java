package com.sacherus.partynow.rest;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.pojos.User;
import com.sacherus.partynow.provider.PartynowContracts;
import com.sacherus.partynow.provider.PartynowContracts.UserColumnHelper;
import com.sacherus.partynow.rest.RestService.Plurality;
import com.sacherus.utils.Utils;

public class UserRestHandler extends ResponseHandler {
	
	UserRestHandler(Context context, Plurality plur, Class objectClass, String location) {
		super(context, plur, objectClass, location);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleResponse(String response) {	
		switch (plur) {
		case PLURAL:
			List<User> users = (List<User>) gson.fromJson(response, new TypeToken<List<User>>() {
			}.getType());
			final ContentValues cvs[] = Utils.toContents(users);
			context.getContentResolver().bulkInsert(UserColumnHelper.URI, cvs);
			showMsg("Information for " + cvs.length + " users loaded");
			break;
		case SINGULAR:
			User user = gson.fromJson(response, User.class);
			context.getContentResolver().insert(UserColumnHelper.URI, user.toContent());
			showMsg("Information about " + user.getUsername() + " added");
			break;
		}
		
	}
}
