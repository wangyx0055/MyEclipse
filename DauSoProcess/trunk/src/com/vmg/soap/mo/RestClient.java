package com.vmg.soap.mo;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vmg.sms.common.Util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tunm
 */
public class RestClient {
	
		
	public static Object callMethod(String url, String method, Map<String, Object> param) throws Exception {
		if(!param.containsKey(url)) param.put("method", method);
        String response = sendRequest(url,param);
        Object obj = parseResponse(response);
        return obj;
    }
	
	
	protected static Object parseResponse(String response) throws Exception {
        try {
            JSONObject result = JSON_decode(response);
            int error_code = Integer.parseInt(result.get("error_code").toString());
            if (error_code != 0) {
                throw new Exception(result.get("error_message").toString());
            }
            return result.get("data");
        } catch (Exception ex) {
            throw ex;
        }
    }
	
	protected static String sendRequest(String url, Map<String, Object> param) throws Exception {
        String targetURL = url;
		int _timeout = 1000 * 30; //30s
        URL _url;
        HttpURLConnection connection = null;
        try {
            String data = prepareData(param);

            //Create connection
            _url = new URL(targetURL);
            connection = (HttpURLConnection) _url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(_timeout);

            connection.setRequestProperty("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.7");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", data.length() + "");
            connection.setDoOutput(true);
            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            //Get Response	
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
        	Util.logger.printStackTrace(e);
            throw new Exception("Uncautch Exception "+e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

	private static JSONObject JSON_decode(String input) throws Exception {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(input);
            JSONObject jsonObj = (JSONObject) obj;
            return jsonObj;
        } catch (ParseException ex) {
            throw new Exception("Uncautch Exception: Can not parse to Object");
        }
    }
	
	private static String prepareData(Map<String, Object> param) throws Exception {
        String request = "";

        // Get a set of the entries
        Set set = param.entrySet();

        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        String key = "";
        Object value = "";
        Map.Entry me;
        while (i.hasNext()) {
            try {
                me = (Map.Entry) i.next();
                key = (String) me.getKey();
                value = me.getValue();
                request += key + "=" + java.net.URLEncoder.encode(value.toString(), "UTF-8") + "&";
            } catch (UnsupportedEncodingException ex) {
                throw new Exception("UnsupportedEncodingException exception");                        
            }
        }
        return request;
    }
}
