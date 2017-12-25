package com.warhut.banmanager;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {
	public static double yearsToSeconds(int months){
		double seconds = months*31536000;
		return seconds;
	}

	public static long monthsToSeconds(int months){
		long seconds = months*2592000;
		return seconds;
	}
	public static long weeksToSeconds(int weeks){
		long seconds = weeks*604800;
		return seconds;
	}
	public static long daysToSeconds(int days){
		long seconds = days*86400;
		return seconds;
	}
	public static long hoursToSeconds(int hours){
		long seconds = hours*3600;
		return seconds;
	}
	public static long minutesToSeconds(int minutes){
		long seconds = minutes*60;
		return seconds;
	}
	
	public static String secondsToDate(long seconds){
		//TODO add month support.
		int day = (int)TimeUnit.SECONDS.toDays(seconds);        
		long hour = TimeUnit.SECONDS.toHours(seconds) - (day *24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
		String convertedTime = "";
		if(day != 0){
			convertedTime += day + " days ";
		}if(hour != 0){
			convertedTime += hour + " hours ";
		}if(minute != 0){
			convertedTime += minute + " minutes ";
		}if(second != 0){
			convertedTime += second + " seconds";
		}
		return convertedTime;
	}
	public static long stringToSeconds(String str2Check){
    	Pattern checkRegex = Pattern.compile("(\\d+)([smhdwy]|mo)");
    	
    	Matcher regexMatcher = checkRegex.matcher(str2Check);
    	long totalTime = 0;
    	while(regexMatcher.find()){
    		if(regexMatcher.group().length() != 0){
    			switch(regexMatcher.group(2)){
    				case "y":
    					totalTime += TimeConverter.yearsToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				case "mo":
    					totalTime += TimeConverter.monthsToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				case "w":
    					totalTime += TimeConverter.weeksToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				case "d":
    					totalTime += TimeConverter.daysToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				case "h":
    					totalTime += TimeConverter.hoursToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				case "m":
    					totalTime += TimeConverter.hoursToSeconds(Integer.parseInt(regexMatcher.group(1).trim()));
    					break;
    				default:
    					break;
    			}
    		}
    	}
    	return totalTime;
	}
}
