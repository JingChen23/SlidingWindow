package util;

import java.util.Calendar;

public class TimeTransHelper {
	public long toTime(String time) {
		Calendar c = Calendar.getInstance();
		String year = time.substring(0,4);
		String month = time.substring(4,6);
		String day = time.substring(6,8);
		String hour = time.substring(8,10);
		String minute = time.substring(10,12);
		String second = time.substring(12,14);
		try {
			c.set(Integer.parseInt(year), Integer.parseInt(month),Integer.parseInt(day),
					Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(second));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}
}
