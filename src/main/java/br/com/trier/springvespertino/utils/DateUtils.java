package br.com.trier.springvespertino.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	private static DateTimeFormatter dateBrFormatter= DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public static ZonedDateTime dateBrToZoneDate(String dateString) {
	    LocalDate date = LocalDate.parse(dateString, dateBrFormatter);
	    return date.atStartOfDay(ZoneId.systemDefault());
	}
	
	public static String zoneDateToBrDate(ZonedDateTime date) {
		return dateBrFormatter.format(date);
	}
}
