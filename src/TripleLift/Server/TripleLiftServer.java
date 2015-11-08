package TripleLift.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Triple Lift Server Emulator Class.
public class TripleLiftServer {
	
	private int TIMEOUT = 2000;
	private String errorLog = ""; // Can be implemented using String Buffers for longer logs.
	private String urlString = "http://dan.triplelift.net/code_test.php?advertiser_id=";
	private TreeMap<String, Long[]> dataMap = new TreeMap<String, Long[]>(Collections.reverseOrder()); // TreeMap used to provide ordering.
	
	public TripleLiftServer(){ /* Use default URL and Timeout. */ }
	
	public void setURL(String urlString){
		this.urlString = urlString;
	}
	
	// Timeout time in milliseconds.
	public void setTimeout(int timeout){
		this.TIMEOUT = timeout;
	}
	
	// Method to call to fetch data by date for
	public String fetchData(long[] idArray){
		
		// Remove duplicates by pushing IDs into HashSet.
		LinkedHashSet<Long> idSet = new LinkedHashSet<Long>();
		for(int i=0; i<idArray.length; i++){
			idSet.add(idArray[i]);
		}
		for(long ID : idSet){
			connectAndFetch(ID);
		}
		
		// Print advertiser IDs whose data could not be found.
		System.out.println(errorLog);
		
		return output();
	}
	
	// Get data from server for each Advertiser ID.
	public void connectAndFetch(long ID){
		try{
			URL url = new URL(urlString + ID);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			String jsonString = "";
			while((inputLine=reader.readLine()) != null){
				jsonString += inputLine;
			}
			createDataMap(jsonString);
			reader.close();
		}
		catch (MalformedURLException e){
			System.out.println("Invalid URL.");
		}
		catch (SocketTimeoutException e){
			errorLog += "The data for advertiser ID: " + ID + " could not be retrieved.\n";
		}
		catch (IOException e){
			System.out.println("IO Exception Occured.");
		}
	}
	
	// Create data map from JSON Object.
	public void createDataMap(String jsonString){
		JsonParser parser = new JsonParser(jsonString);
		JsonObject jsonObject;
		while((jsonObject = parser.getNext()) != null){
			String date = jsonObject.getValue("ymd", false);
			long impressions = Long.parseLong(jsonObject.getValue("num_impressions", true));
			long clicks = Long.parseLong(jsonObject.getValue("num_clicks", true));
			Long[] data;
			if(dataMap.containsKey(date)){
				data = dataMap.get(date);
				data[0] += impressions;
				data[1] += clicks;
			}
			else{
				data = new Long[2];
				data[0] = impressions;
				data[1] = clicks;
			}
			dataMap.put(date, data);
		}
	}
	
	// Dump all aggregated data.
	public String output(){
		String output = "";
		for(Entry<String, Long[]> entry : dataMap.entrySet()){
			Long[] data = entry.getValue();
			output += entry.getKey() + ": " + "Impressions = " + data[0] + ", Clicks = " + data[1] + "\n";
		}
		return output;
	}
	
	public String getLog(){
		return errorLog;
	}
}

// Parser to parse 1 Dimensional JSON String.
class JsonParser{
	
	private String[] jsonArray;
	private int currentIndex;
	private JsonObject currentObject;
	
	public JsonParser(String jsonString){
		// Reset values.
		currentIndex = 0;
		currentObject = null;
		
		// Removing additional spaces.
		jsonString = jsonString.replace(" ", "");
		
		// Removing starting and ending brackets.
		jsonString = jsonString.replace("[{", "").replace("}]", "");
		
		// Split each JSON object into a JSON Array.
		String splitBy = "\\},\\{";
		this.jsonArray = jsonString.substring(0, jsonString.length()).split(splitBy);
	}
	
	
	// Return next JSON object in the array and increment pointer.
	public JsonObject getNext(){
		if(currentIndex < jsonArray.length){
			if(currentObject == null) currentObject = new JsonObject();
			currentObject.setObject(jsonArray[currentIndex]);
			currentIndex++;
			return currentObject;
		}
		else return null;
	}
}

// Class to represent 1 Dimensional JSON Objects.
class JsonObject{
	
	private String jsonString;
	private HashMap<String, Pattern> patternMap = new HashMap<String, Pattern>();
	
	public void setObject(String jsonString){
		this.jsonString = jsonString + ",";
	}
	
	// Returns the value for the provided key if found in the JSON object.
	public String getValue(String key, boolean isNumeric){
		// Caching patterns to avoid compiling again.
		Pattern pattern;
		if(patternMap.containsKey(key)) pattern = patternMap.get(key);
		else{
			String regex;
			if(isNumeric) regex = "\"" + key + "\":(.+?),"; // If value is Numeric.
			else regex = "\"" + key + "\":\"(.+?)\""; // If value is String.
			pattern = Pattern.compile(regex);
			patternMap.put(key, pattern);
		}
		
		// Match the key in the string to find its value.
		Matcher matcher = pattern.matcher(jsonString);
		if(matcher.find()){ // If found.
			return matcher.group(1);
		}
		else{ // If not found.
			String value = "";
			if(isNumeric) value = "0";
			return value;
		}
	}
}