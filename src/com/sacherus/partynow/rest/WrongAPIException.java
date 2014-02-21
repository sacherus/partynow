package com.sacherus.partynow.rest;

import java.io.IOException;

public class WrongAPIException extends IOException {
	public WrongAPIException(String msg) {
		super(msg);
	}
}
