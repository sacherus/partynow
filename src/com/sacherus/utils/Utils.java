package com.sacherus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sacherus.partynow.pojos.Party;

import android.content.ContentValues;
import android.util.Log;

public class Utils {
	public static void log(String msg) {
		final String tag = "important";
		Log.d(tag, msg);
	}
	
	
	public static void log(long id) {
		log(Long.toString(id));
	}
	
	public static ContentValues[] toContents(List<Party> list) {
		
		List<ContentValues> returnList = new LinkedList<ContentValues>();
		int size = 0;
		for(Party el : list) {
			size++;
			returnList.add(el.toContent());
		}
		ContentValues[] array = returnList.toArray(new ContentValues[size]);
		return array;
	}

	public static Date stringToDate(String s) {
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		Date date = null;
		try {
			date = format.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String dateToString(Date d) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		String datetime = dateformat.format(d);
		return datetime;
	}
}
