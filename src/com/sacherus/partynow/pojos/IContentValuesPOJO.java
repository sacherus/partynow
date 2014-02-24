package com.sacherus.partynow.pojos;

import android.content.ContentValues;

public interface IContentValuesPOJO {
	IContentValuesPOJO toContent(ContentValues cv);
	ContentValues fromContentLocal(IContentValuesPOJO cv);
}
