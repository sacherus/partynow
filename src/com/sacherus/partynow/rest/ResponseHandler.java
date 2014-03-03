package com.sacherus.partynow.rest;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sacherus.partynow.rest.RestService.Plurality;

/**
 * Enables custom handling of HttpResponse and the entities they contain.
 */
abstract public class ResponseHandler {
	protected Context context;
	protected Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();
	private Handler handler;
	protected Plurality plur;
	protected Class objectClass;
	protected String location;
	
	ResponseHandler(Context context, Plurality plur, Class objectClass, String location) {
		this(context);
		this.plur = plur;
		this.objectClass = objectClass;
		this.location = location;
	}
	

	public ResponseHandler(Context context) {
		this.context = context.getApplicationContext();
		handler = new Handler(context.getMainLooper());
	}




	public void showMsg(final String msg) {
		handler.post(new Runnable() {
	        @Override
	        public void run() {
	        	Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	        }
	    });
	}

	void handleResponse(Exception e) {
		showMsg(e.getMessage());
	};

	abstract void handleResponse(String response);
}
