package com.bt.camnav.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateFormatterUtil {
	INSTANCE;
	private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public DateFormat getDateFormat() {
		return this.DATE_FORMAT;
	}

	public Date getDateFromString(String dateStr) {
		try {
			return getDateFormat().parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getStringFromDate(Date date) {
		return getDateFormat().format(date);
	}
}
