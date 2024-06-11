package com.cex.vulcano.utils;

import java.util.regex.Pattern;

public class URLUtils {

	public static String mergeURL(String url, String baseURL) {
		if (url==null) {
			throw new NullPointerException();
		}
		boolean hasBaseUrl = baseURL!=null && !baseURL.isEmpty();
		boolean hasProtocol = hasProtocol(url);
		if (!hasProtocol) {
			if (isRelative(url)) {
				url = url.substring(1);
			}
			if (hasBaseUrl) {
				if (!baseURL.endsWith("/")) {
					baseURL += "/";
				}
				url = baseURL + "/" + url;
			} else {
				url = "http://" + url;
			}
		}
		return removeMultipleSlashesFrom(url);
	}
	
	public static Pattern mergeUrlPattern(String urlPattern, String baseURL) {
		if (urlPattern==null) {
			throw new NullPointerException();
		}
		boolean hasBaseUrl = baseURL!=null && !baseURL.isEmpty();
		if (hasBaseUrl) {
			urlPattern = toRegexp(mergeURL(urlPattern, baseURL));
		} else {
			if (isRelative(urlPattern)) {
				urlPattern = "https?://" + toRegexp("*" + urlPattern);
			} else if (!hasProtocol(urlPattern)) {
				urlPattern = "https?://" + toRegexp(urlPattern);
			} else {
				urlPattern = toRegexp(urlPattern);
			}
		}
		return Pattern.compile("^" + urlPattern + "$");
	}
	
	public static String getFileName(String url) {
		if (url == null) {
			return null;
		}
		int pos = url.indexOf('?');
		if (pos >= 0) {
			url = url.substring(0, pos);
		}
		pos = url.indexOf('#');
		if (pos >= 0) {
			url = url.substring(0, pos);
		}
		pos = url.lastIndexOf('/');
		if (pos >= 0) {
			url = url.substring(pos + 1);
		}
		return url;
	}

	private static String removeMultipleSlashesFrom(String url) {
		int pos = url.indexOf("://");
		String protocol;
		if (pos>=0) {
			pos += 3;
			protocol = url.substring(0, pos);
			url = url.substring(pos);
		} else {
			protocol = "";
		}
        return protocol + url.replaceAll("//+", "/");
    }
	
	private static String toRegexp(String pattern) {
		String metachars = "<([{\\^-=$!|]})+.>";
		StringBuilder str = new StringBuilder();
		for (int i=0; i<pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (c=='*') {
				if (i<pattern.length()-1 && pattern.charAt(i+1)=='*') {
					str.append(".*");
					i++;
				} else {
					str.append("[^/]*");
				}
			} else if (c=='?') {
				str.append("[^/]");
			} else if (metachars.indexOf(c)>=0) {
				str.append('\\');
				str.append(c);
			} else {
				str.append(c);
			}
		}
		return str.toString();
	}
	
	private static boolean hasProtocol(String url) {
		String urlLower = url.toLowerCase();
		return urlLower.startsWith("http:") || urlLower.startsWith("https:") || urlLower.startsWith("file:");
	}
	
	private static boolean isRelative(String url) {
		return url.startsWith("/");
	}
	
}
