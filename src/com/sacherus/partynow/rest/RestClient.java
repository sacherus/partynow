package com.sacherus.partynow.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sacherus.partynow.pojos.Token;
import com.sacherus.utils.Utils;

public class RestClient implements Runnable {

	private static String BASE_URL_DEBUG = "http://192.168.1.5:8000/";
	private static final String BASE_URL = "http://partynow.herokuapp.com/";
	private static final String TAG = RestClient.class.getName();
	public static final boolean debug = true;
	private static Token token;

	public static void setToken(Token token) {
		RestClient.token = token;
	}

	public String getAccessToken() {
		return token.getAccessToken();
	}

	private ResponseHandler rh;
	private String location;
	private String data;

	private HttpURLConnection prepareConnection(String location, String method) throws IOException {
		HttpURLConnection con = (HttpURLConnection) (new URL(getBaseURL() + location)).openConnection();
		if (token != null)
			con.addRequestProperty("Authorization", "Bearer " + getAccessToken());
		con.setRequestMethod(method);
		return con;
	}

	public RestClient(ResponseHandler rh, String location) {
		this.location = location;
		this.rh = rh;	
	}

	public RestClient() {
	}

	public void setData(String data) {
		this.data = data;
	}

	public void run() {
		String response = null;
		try {
			if (data == null) {
				response = getData(location);
			} else {
				response = sendJSON(location, data);
			}
		} catch (IOException e) {
			e.printStackTrace();
			rh.handleResponse(e);
			return;
		}
		rh.handleResponse(response);
	}

	private String getBaseURL() {
		if (debug) {
			return BASE_URL_DEBUG;
		} else {
			return BASE_URL;
		}
	}

	public String getData(String location) throws IOException {
		InputStream is = null;
		HttpURLConnection httpcon = null;
		try {
			httpcon = prepareConnection(location, "GET");
			httpcon.connect();
			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			is = getInputStream(httpcon);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null)
				buffer.append(line + "\r\n");
			is.close();
			String response = buffer.toString();
			catchError(httpcon, response);
			httpcon.disconnect();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (is != null)
				is.close();
			httpcon.disconnect();
		}
	}

	public String sendJSON(String location, String data) throws IOException {
		String content = "application/json";
		return sendData(location, data, content);
	}

	public String sendData(String location, String data) throws IOException {
		String content = null;
		return sendData(location, data, content);
	}

	public String sendData(String location, String data, String ContentType) throws IOException {
		String response = null;
		OutputStreamWriter request = null;
		HttpURLConnection httpcon = (HttpURLConnection) (new URL(getBaseURL() + location).openConnection());
		httpcon.setDoOutput(true);
		if (ContentType != null)
			httpcon.setRequestProperty("Content-Type", ContentType);
		httpcon.setRequestMethod("POST");
		if (token != null) {
			httpcon.addRequestProperty("Authorization", "Bearer " + getAccessToken());
		}

		request = new OutputStreamWriter(httpcon.getOutputStream());
		request.write(data);
		request.flush();
		request.close();
		String line = "";
		InputStream is;
		if (httpcon.getResponseCode() >= 400) {
			is = httpcon.getErrorStream();

		} else {
			is = httpcon.getInputStream();
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		response = sb.toString();
		isr.close();
		reader.close();
		catchError(httpcon, response);
		return response;
	}

	private void catchError(HttpURLConnection httpcon, String response) throws IOException {
		final int errorCode = httpcon.getResponseCode();
		if (errorCode  >= 400) {
			Document doc = Jsoup.parse(response);
			Element error;
			if(errorCode >= 500) {
				error = doc.select(".exception_value").first(); 
			} else {
				error = doc.select("body").first();
			}			
			response = "Error code: " + Integer.toString(httpcon.getResponseCode()) + " " + error;
			//response = "Error code: " + Integer.toString(httpcon.getResponseCode());
			throw new IOException(response);
		}		
	}

	private InputStream getInputStream(HttpURLConnection httpcon) throws IOException {
		return (httpcon.getResponseCode() >= 400 ? httpcon.getErrorStream() : httpcon.getInputStream());
	}

}