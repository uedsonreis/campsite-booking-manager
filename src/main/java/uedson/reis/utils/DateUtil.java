package uedson.reis.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	
	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	public static void setDefaultHMS(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
}
