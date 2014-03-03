package com.sacherus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sacherus.partynow.pojos.IContentValuesPOJO;
import com.sacherus.partynow.pojos.Party;

import android.content.ContentValues;
import android.util.Log;

public class Utils {
	public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	final static String tag = "important";
	
	public static void log(String msg) {
		Log.d(tag, msg);
	}
	
	public static void log(double msg) {
		Log.d(tag, Double.toString(msg));
	}

	public static void log(long id) {
		log(Long.toString(id));
	}

	public static ContentValues[] toContents(List<? extends IContentValuesPOJO> list) {

		List<ContentValues> returnList = new LinkedList<ContentValues>();
		int size = 0;
		for (IContentValuesPOJO el : list) {
			size++;
			returnList.add(el.toContent());
		}
		ContentValues[] array = returnList.toArray(new ContentValues[size]);
		return array;
	}

	public static Date stringToDate(String s) {

		Date date = null;
		try {
			date = dateformat.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String dateToString(Date d) {
		String datetime = dateformat.format(d);
		return datetime;
	}
	
	public static String dayOfMonth(int month) {
		return month < 10 ? "0" + month : "" + month;
	}
}
