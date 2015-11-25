package DAO;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Vector;

import servicesPkg.MlistInfo;

import DTO.ServiceDTO;

public class ServiceDAO {

	public static ServiceDTO getServiceDTO(String serviceName) {
		ServiceDTO service = null;

		String funName = "getServiceDTO ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM services WHERE services= '" + serviceName
				+ "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					service = new ServiceDTO();

					String services = rs.getString("services");
					String minutes = rs.getString("minutes");
					String hours = rs.getString("hours");
					String dayofmonth = rs.getString("dayofmonth");
					String month = rs.getString("month");
					String dayofweek = rs.getString("dayofweek");
					String weekofyear = rs.getString("weekofyear");
					int result = rs.getInt("result");
					int retries = rs.getInt("retries");
					Timestamp lasttime = rs.getTimestamp("lasttime");
					String className = rs.getString("class");
					String name = rs.getString("name");
					String options = rs.getString("options");
					int alertmin = rs.getInt("alertmin");
					int notcharge = rs.getInt("notcharge");
					String timesendcharge = rs.getString("timesendcharge");
					int active = rs.getInt("active");
					Timestamp fromDateFree = rs.getTimestamp("from_date_free");
					Timestamp toDateFree = rs.getTimestamp("to_date_free");
					int numberFree = rs.getInt("number_free");
					int activeFree = rs.getInt("active_free");
					int packetOrMt = rs.getInt("packet_or_mt");
					int timeRechagePacket = rs.getInt("time_rechage_packet");
					int runInsertListSend = rs.getInt("run_insert_list_send");

					service.setServices(services);
					service.setMinutes(minutes);
					service.setHours(hours);
					service.setDayofmonth(dayofmonth);
					service.setMonth(month);
					service.setDayofmonth(dayofmonth);
					service.setDayofweek(dayofweek);
					service.setWeekofyear(weekofyear);
					service.setResult(result);
					service.setRetries(retries);
					service.setLasttime(lasttime);
					service.setClassName(className);
					service.setName(name);
					service.setOptions(options);
					service.setAlertmin(alertmin);
					service.setNotcharge(notcharge);
					service.setTimeRechagePacket(timeRechagePacket);
					service.setTimesendcharge(timesendcharge);
					service.setActive(active);
					service.setFromDateFree(fromDateFree);
					service.setToDateFree(toDateFree);
					service.setNumberFree(numberFree);
					service.setActiveFree(activeFree);
					service.setRunInsertListSend(runInsertListSend);
					service.setPacketOrMt(packetOrMt);
				}
			} else {
				Util.logger.error(funName + " connection null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}

		return service;
	}

	public static Vector<String> getMlistByCommandCode(String services, String poolName) {
		Vector<String> vMlist = new Vector<String>();

		String funName = "getMlistByCommandCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT REPLACE (SUBSTRING(OPTIONS,1,IF(POSITION('&' IN OPTIONS) > 1, POSITION"
				+ "('&' IN OPTIONS) - 1, LENGTH(OPTIONS))),'mlist=','') mlist , service_name "
				+ "FROM keywords WHERE OPTIONS LIKE 'mlist=mlist_%' "
				+ "AND service_name IN ("
				+ services
				+ " ) "
				+ "GROUP BY 1 ORDER BY 1";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnection(poolName);
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					String mlist = rs.getString("mlist");
					String serviceName = rs.getString("service_name");
					vMlist.add(mlist+";"+serviceName);
				}

				Util.logger.info(funName + " @sql:"+sql);
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}

		return vMlist;
	}
	
	public static Vector<String> getUserFromMlist(String sMlist,String poolName){
		Vector<String> vUser = new Vector<String>();
		String funName = "getUserFromMlist ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT USER_ID, COMMAND_CODE FROM "+sMlist;
		DBPool pool = new DBPool();
		try {
			con = pool.getConnection(poolName);
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					String userId = rs.getString("USER_ID");
					String commandCode =rs.getString("COMMAND_CODE");
					
					vUser.add(userId+";"+commandCode);
				}

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return vUser;
	}

}
