package com.moneydesktop.finance.data;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.moneydesktop.communication.HttpRequest;
import com.moneydesktop.finance.ApplicationContext;
import com.moneydesktop.finance.DebugActivity;
import com.moneydesktop.finance.model.User;

public class DataBridge {
	
	private final String TAG = "DataBridge";
	
	private static final String ENDPOINT_DEVICE = "devices";
	private static final String ENDPOINT_FULL_SYNC = "sync/full";
	private static final String ENDPOINT_SYNC = "sync";
	
	private static DataBridge sharedInstance;
	
	private String protocol = "https";
	
	private Context context;
	private HashMap<String, String> headers;

	public static DataBridge sharedInstance() {
		
		if (sharedInstance == null) {
    		sharedInstance = new DataBridge();
    	}
    	
    	return sharedInstance;
	}
	
	public DataBridge() {
		this.context = ApplicationContext.getContext();
		this.headers = getHeaders();
	}
	
	/**
	 * Login user by authenticating to the server with the supplied user name and password.
	 * 
	 * @param userName The user's name
	 * @param password The user's password
	 * @throws Exception
	 */
	public void authenticateUser(String userName, String password) throws Exception {
		
		long start = System.currentTimeMillis();
		
		AuthObject auth = new AuthObject(context, userName, password);
        String body = auth.toString();
        
        String baseUrl = Preferences.getString(Preferences.KEY_API_HOST, DebugActivity.PROD_API_HOST);
        		
        String url = String.format("%s://%s/%s", protocol, baseUrl, ENDPOINT_DEVICE);
        
        String response = HttpRequest.sendPost(url, headers, null, body);
        
        JSONObject json = new JSONObject(response);
        
        if (json != null && json.has(Constant.KEY_DEVICE)) {
        	
        	JSONObject data = json.getJSONObject(Constant.KEY_DEVICE);
        	data.put(Constant.KEY_USERNAME, userName);
        	
        	User.registerUser(data, context);
        	headers = getHeaders();
        }
        
        Log.i(TAG, "Auth in " + (System.currentTimeMillis() - start) + " ms");
	}
	
	/**
	 * Make a sync request with the server to pull down all data that needs to be
	 * sync'd with the mobile client
	 * 
	 * @param fullSync Flag to indicate a full sync is required
	 * @return The response parsed into a JSONObject
	 */
	public JSONObject downloadSync(boolean fullSync) {
		
		String endpoint = fullSync ? ENDPOINT_FULL_SYNC : ENDPOINT_SYNC;
        
        String baseUrl = Preferences.getString(Preferences.KEY_SYNC_HOST, DebugActivity.PROD_SYNC_HOST);
        		
        String url = String.format("%s://%s/%s", protocol, baseUrl, endpoint);
        
		try {

			Long start = System.currentTimeMillis();
			
			String response = HttpRequest.sendGet(url, headers, null);
			
			Log.i(TAG, "Sync Get: " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();
			
			JSONObject json = new JSONObject(response);
			
	        Log.i(TAG, "Sync Parsed: " + (System.currentTimeMillis() - start) + " ms");
			
			return json;
	        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the generic headers used in every request sent to the server
	 * 
	 * @return A map of request headers and their value
	 */
	public HashMap<String, String> getHeaders() {
		
		String version = "0.0";
		
		try {
			
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = pInfo.versionName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		headers.put("MD-App-Build", version);
		
		// If the user is logged in then we will need their token to authenticate a request
		if (User.getCurrentUser() != null && !User.getCurrentUser().getUserId().equals("")) {
			headers.put("X-Auth-UserToken", User.getCurrentUser().getAuthorizationToken());
		}
		
		return headers;
	}
	
	public void setUseSSL(boolean useSSL) {
		
		if (!useSSL)
			protocol = "http";
		else
			protocol = "https";
	}
	
}
