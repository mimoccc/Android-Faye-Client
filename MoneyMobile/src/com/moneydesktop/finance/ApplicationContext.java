package com.moneydesktop.finance;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneydesktop.finance.data.SyncEngine;
import com.moneydesktop.finance.database.DaoMaster;
import com.moneydesktop.finance.database.DaoMaster.DevOpenHelper;
import com.moneydesktop.finance.database.DaoSession;
import com.moneydesktop.finance.exception.CustomExceptionHandler;
import com.moneydesktop.finance.model.EventMessage.LoginEvent;

import de.greenrobot.event.EventBus;

public class ApplicationContext extends Application {
	
//	private final String TAG = "ApplicationContext";

	final static String WAKE_TAG = "wake_lock";

	private static ApplicationContext instance;
	
	private static ObjectMapper mapper;
	
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
	
	private static WakeLock wl;

	public void onCreate() {
		super.onCreate();
		
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        
        initializeDatabase();
        
		acquireWakeLock();
		
		registerEventListeners();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		releaseWakeLock();
	}
	
	private void initializeDatabase() {

        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "finance-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
	}
	
	private void registerEventListeners() {
		
		EventBus eventBus = EventBus.getDefault();
		eventBus.register(this);
	}
	
	public void onEvent(LoginEvent event) {
		
		SyncEngine.sharedInstance().beginSync(true);
	}
	
    public ApplicationContext() {
        instance = this;
    }
    
    public static Context getContext() {
        return instance;
    }
    
    public static ObjectMapper getObjectMapper() {
    	
    	if (mapper == null) {
    		mapper = new ObjectMapper();
    		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    	}
    	
    	return mapper;
    }

	public static SQLiteDatabase getDb() {
		return db;
	}

	public static DaoMaster getDaoMaster() {
		return daoMaster;
	}

	public static DaoSession getDaoSession() {
		return daoSession;
	}

	public void acquireWakeLock() {
			
		PowerManager pm = (PowerManager) ApplicationContext.getContext().getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKE_TAG);
		wl.acquire();
	}

	private void releaseWakeLock() {
		
		if (wl != null) {
			
			wl.release();
			wl = null;
		}
	}
}
