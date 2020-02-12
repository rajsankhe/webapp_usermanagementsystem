package com.usermanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	String FILE_EXTENSION = "^.*\\.(jpg|png|jpeg|pdf)$";


	public boolean validateAttachmentExtension(String url) {
		Pattern pattern = Pattern.compile(FILE_EXTENSION);
		Matcher matcher = pattern.matcher(url.toLowerCase());
		return matcher.matches();
		
	}
	
	public String computeMD5Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        byte[] digest = messageDigest.digest(data);

        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        return sb.toString();
    }
}
