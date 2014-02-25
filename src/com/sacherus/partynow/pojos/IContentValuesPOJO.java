package com.sacherus.partynow.pojos;

import android.content.ContentValues;

public interface IContentValuesPOJO {
	ContentValues toContent();
	void fromContentLocal(ContentValues cv);	
}
