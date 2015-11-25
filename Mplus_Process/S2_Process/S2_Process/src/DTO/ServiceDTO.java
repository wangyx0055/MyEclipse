package DTO;

import java.sql.Timestamp;

public class ServiceDTO {
	private String services;
	private String minutes;
	private String hours;
	private String dayofmonth;
	private String month;
	private String dayofweek;
	private String weekofyear;
	private int result;
	private int retries;
	private Timestamp lasttime;
	private String className;
	private String name;
	private String options;
	private int alertmin;
	private int notcharge;
	private String timesendcharge;
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	public String getMinutes() {
		return minutes;
	}
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getDayofmonth() {
		return dayofmonth;
	}
	public void setDayofmonth(String dayofmonth) {
		this.dayofmonth = dayofmonth;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDayofweek() {
		return dayofweek;
	}
	public void setDayofweek(String dayofweek) {
		this.dayofweek = dayofweek;
	}
	public String getWeekofyear() {
		return weekofyear;
	}
	public void setWeekofyear(String weekofyear) {
		this.weekofyear = weekofyear;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public Timestamp getLasttime() {
		return lasttime;
	}
	public void setLasttime(Timestamp lasttime) {
		this.lasttime = lasttime;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public int getAlertmin() {
		return alertmin;
	}
	public void setAlertmin(int alertmin) {
		this.alertmin = alertmin;
	}
	public int getNotcharge() {
		return notcharge;
	}
	public void setNotcharge(int notcharge) {
		this.notcharge = notcharge;
	}
	public String getTimesendcharge() {
		return timesendcharge;
	}
	public void setTimesendcharge(String timesendcharge) {
		this.timesendcharge = timesendcharge;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public Timestamp getFromDateFree() {
		return fromDateFree;
	}
	public void setFromDateFree(Timestamp fromDateFree) {
		this.fromDateFree = fromDateFree;
	}
	public Timestamp getToDateFree() {
		return toDateFree;
	}
	public void setToDateFree(Timestamp toDateFree) {
		this.toDateFree = toDateFree;
	}
	public int getNumberFree() {
		return numberFree;
	}
	public void setNumberFree(int numberFree) {
		this.numberFree = numberFree;
	}
	public int getActiveFree() {
		return activeFree;
	}
	public void setActiveFree(int activeFree) {
		this.activeFree = activeFree;
	}
	public int getPacketOrMt() {
		return packetOrMt;
	}
	public void setPacketOrMt(int packetOrMt) {
		this.packetOrMt = packetOrMt;
	}
	public int getTimeRechagePacket() {
		return timeRechagePacket;
	}
	public void setTimeRechagePacket(int timeRechagePacket) {
		this.timeRechagePacket = timeRechagePacket;
	}
	public int getRunInsertListSend() {
		return runInsertListSend;
	}
	public void setRunInsertListSend(int runInsertListSend) {
		this.runInsertListSend = runInsertListSend;
	}
	private int active;
	private Timestamp fromDateFree;
	private Timestamp toDateFree;
	private int numberFree;
	private int activeFree;
	private int packetOrMt;
	private int timeRechagePacket;
	private int runInsertListSend;
	
}
