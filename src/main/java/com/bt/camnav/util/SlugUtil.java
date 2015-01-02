package com.bt.camnav.util;

import com.github.slugify.Slugify;

public enum SlugUtil {
	INSTANCE;

	private final Slugify slg;
	
	private SlugUtil() {
		slg = new Slugify();
	}
	
	public String slugify(String str) {
		return slg.slugify(str);
	}
}
