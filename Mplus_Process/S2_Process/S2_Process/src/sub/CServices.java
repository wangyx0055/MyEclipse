package sub;

import icom.Constants;
import icom.common.Util;

import java.sql.Timestamp;
import java.util.Calendar;

public class CServices {

	String _services = "";
	String _minutes = "x";
	String _hours = "x";
	String _dayofmonth = "x";
	String _months = "x";
	String _dayofweek = "x";
	String _weekofyear = "x";
	int _result = 0;
	int _retries = 0;
	Timestamp _lasttimes = null;
	String _classname = "";
	String _option = "";

	public CServices(String services, String minutes, String hours,
			String dayofmonth, String months, String dayofweek,
			String weekofyear, int result, int retries, Timestamp lasttimes,
			String classname, String option) {
		this._services = services;
		this._minutes = minutes;
		this._hours = hours;
		this._dayofmonth = dayofmonth;
		this._months = months;
		this._dayofweek = dayofweek;
		this._weekofyear = weekofyear;
		this._result = result;
		this._retries = retries;
		this._lasttimes = lasttimes;
		this._classname = classname;
		this._option = option;

	}

	public boolean isin(String[] arr, String val) {

		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equalsIgnoreCase(val)) {
				return true;
			}
		}
		return false;
	}

	public boolean istime2run() {

		if (_result == Constants.DELIVER_RUNNING) {
			Util.logger.info("Delivery." + _services + ":isRunning");
			return false;
		}

		String x = "x";
		Timestamp tTime = new Timestamp(System.currentTimeMillis());
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(new java.util.Date(tTime.getTime()));

		java.util.Calendar lastcalendar = java.util.Calendar.getInstance();
		lastcalendar.setTime(new java.util.Date(_lasttimes.getTime()));

		int nLastHour = lastcalendar.get(Calendar.HOUR_OF_DAY);

		int nMinutes = calendar.get(Calendar.MINUTE);
		int nHour = calendar.get(Calendar.HOUR_OF_DAY);

		int nMonth = calendar.get(Calendar.MONTH) + 1;

		int nDayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
		int nDayofweek = calendar.get(Calendar.DAY_OF_WEEK);
		int nWeekofyear = calendar.get(Calendar.WEEK_OF_YEAR);

		String[] arrDayofmonth = null;
		if (!_dayofmonth.equals(x)) {
			arrDayofmonth = _dayofmonth.split(";");
			if (!isin(arrDayofmonth, nDayofmonth + "")) {
				return false;
			}

		}

		String[] arrMonth = null;
		if (!_months.equals(x)) {
			arrMonth = _months.split(";");
			if (!isin(arrMonth, nMonth + "")) {
				return false;
			}

		}

		String[] arrDayofweek = null;
		if (!_dayofweek.equals(x)) {
			arrDayofweek = _dayofweek.split(";");
			if (!isin(arrDayofweek, nDayofweek + "")) {
				return false;
			}

		}

		String[] arrWeekofyear = null;
		if (!_weekofyear.equals(x)) {
			arrWeekofyear = _weekofyear.split(";");
			if (!isin(arrWeekofyear, nWeekofyear + "")) {
				return false;
			}

		}

		// thoa man ngay de chay

		String[] arrHours = _hours.split(";");
		// String arrMinutes = _minutes.split(";");
		// int iRunMinute = Integer.parseInt(_minutes);

		if (!isin(arrHours, nHour + "")) {
			return false;
		}

		// Thoa man phut
		if (!_minutes.equals(x)) {
			int iRunMinute = convert2int(_minutes);
			if (nMinutes < iRunMinute) {
				return false;

			}
		}

		// 9h, 17 h
		if ((nLastHour == nHour) && _result == Constants.DELIVER_OK) {

			return false;

		}

		return true;
	}

	int convert2int(String itemp) {

		try {
			return Integer.parseInt(itemp);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
}
