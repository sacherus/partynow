package com.sacherus.partynow.rest;

import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.pojos.User;
import com.sacherus.utils.Utils;

import android.app.IntentService;
import android.content.Intent;

public class RestService extends IntentService {
	private static String tag = "RestService";
	public static final String TEST_INTENT = "testIntent";
	public static final String DATA = "options";
	public static final String METHOD = "methods";
	public static final String LOCATION = "location";
	public static final String HANDLER = "handler";
	public static final String PLURALITY = "plurality";
	public static final String CLASS = "class";

	enum Method {
		GET("GET"), POST("POST"), PUT("PUT");

		private Method(final String text) {
			this.text = text;
		}

		private final String text;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}
	}

	enum Plurality {
		SINGULAR, PLURAL
	}

	public RestService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public RestService() {
		this("Rest service");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 * TODO: Good place for factory design pattern
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Method method = (Method) intent.getSerializableExtra(METHOD);
		String location = intent.getStringExtra(LOCATION);
		String data = intent.getStringExtra(DATA);
		Class objectClass = (Class) intent.getSerializableExtra(CLASS);
		Plurality plurality = (Plurality) intent.getSerializableExtra(PLURALITY);
		// String methodString = method.toString();
		ResponseHandler handler;
		if (location.contains("user")) {
			handler = new UserRestHandler(getApplicationContext(), plurality, objectClass, location);
		} else {
			handler = new RestHandler(getApplicationContext(), plurality, objectClass, location);
		}
		// handler.init(getApplicationContext());
		Utils.log(objectClass.toString());
		RestClient rc = new RestClient(handler, location, method);
		switch (method) {
		case GET:
			break;
		case POST:
			rc.setData(data);
			break;
		case PUT:
			rc.setData(data);
			break;
		default:
			return;
		}
		Thread task = new Thread(rc);
		task.start();
	}

}
