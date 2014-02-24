package com.sacherus.utils;

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
}
