package com.usermanagement.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
	public String getFileNameFromPath(String path) {
		String[] pathArr = path.split("/");
		return pathArr[pathArr.length - 1];
	}
	
	public String getCurrentDateWithFormat(String format) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date());
	}
}
