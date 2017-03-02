/*
    Copyright 2013-2014 appPlant UG

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance�
    with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */

package zoko7677.cordova.plugin.background;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.ActivityNotFoundException;
import android.os.IBinder;
import android.app.PendingIntent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;

public class BackgroundMode extends CordovaPlugin {

    // Event types for callbacks
    private enum Event {
        ACTIVATE, DEACTIVATE, FAILURE
    }

    // Plugin namespace
    private static final String JS_NAMESPACE =
            "cordova.plugins.backgroundMode";

    // Flag indicates if the app is in background or foreground
    private boolean inBackground = false;

    // Flag indicates if the plugin is enabled or disabled
    private boolean isDisabled = true;

    // Flag indicates if the service is bind
    private boolean isBind = false;

    // Default settings for the notification
    private static JSONObject defaultSettings = new JSONObject();
	
    private Context mContext;
    private String PACK_NAME;

    ForegroundService mService;

    // Used to (un)bind the service to with the activity
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ForegroundService.ForegroundBinder binder =
                    (ForegroundService.ForegroundBinder) service;

            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Nothing to do here
        }
    };

    /**
     * Executes the request.
     *
     * @param action   The action to execute.
     * @param args     The exec() arguments.
     * @param callback The callback context used when
     *                 calling back into JavaScript.
     *
     * @return
     *      Returning false results in a "MethodNotFound" error.
     *
     * @throws JSONException
     */
    @Override
    public boolean execute (String action, JSONArray args,
                            CallbackContext callback) throws JSONException {
	
        PACK_NAME = this.cordova.getActivity().getPackageName();	  
    
        if (action.equalsIgnoreCase("configure")) {
            JSONObject settings = args.getJSONObject(0);
            boolean update = args.getBoolean(1);

            if (update) {
                updateNotification(settings);
            } else {
                setDefaultSettings(settings);
            }

            return true;
        }

        if (action.equalsIgnoreCase("enable")) {
            enableMode();
            return true;
        }

        if (action.equalsIgnoreCase("disable")) {
            disableMode();
            return true;
        }
	
	if (action.equalsIgnoreCase("restart")) {
            restartMode();
            return true;
        }

        return false;
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking
     *      Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onPause(boolean multitasking) {
	super.onPause(multitasking);    
        inBackground = true;
        startService();
	//
    }

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking
     *      Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        inBackground = false;
        stopService();
	
	
    }

    /**
     * Called when the activity will be destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopService();
	Intent LaunchIntent;		    
	try{		
	  LaunchIntent = cordova.getActivity().getPackageManager().getLaunchIntentForPackage(PACK_NAME);
	  LaunchIntent.setAction(this.getIntentValueString("ACTION_MAIN"));
	  cordova.getActivity().startActivityForResult(LaunchIntent, 1);	 
	  System.exit(0);
	}catch (IllegalAccessException e) {
	   //callback.error("IllegalAccessException: " + e.getMessage());	
	}catch (NoSuchFieldException e) {
		
	}    	    
    }
	
    /**
     * Called when the activity will be retart app.
     */    
    private void restartMode() {
	webView.loadUrl("javascript:alert('load restart mode');");	    
        Intent i = cordova.getActivity().getPackageManager().getLaunchIntentForPackage(PACK_NAME);
	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	cordova.getActivity().startActivity(i);
	System.exit(0);	 
    }

    /**
     * Enable the background mode.
     */
    private void enableMode() {
        isDisabled = false;

        if (inBackground) {
            startService();
        }
    }

    /**
     * Disable the background mode.
     */
    private void disableMode() {
        stopService();
        isDisabled = true;
    }

    /**
     * Update the default settings for the notification.
     *
     * @param settings
     *      The new default settings
     */
    private void setDefaultSettings(JSONObject settings) {
        defaultSettings = settings;
    }

    /**
     * The settings for the new/updated notification.
     *
     * @return
     *      updateSettings if set or default settings
     */
    protected static JSONObject getSettings() {
        return defaultSettings;
    }

    /**
     * Update the notification.
     *
     * @param settings
     *      The config settings
     */
    private void updateNotification(JSONObject settings) {
        if (isBind) {
            mService.updateNotification(settings);
        }
    }

    /**
     * Bind the activity to a background service and put them into foreground
     * state.
     */
    private void startService() {
        Activity context = cordova.getActivity();

        if (isDisabled || isBind)
            return;

        Intent intent = new Intent(
                context, ForegroundService.class);

        try {
            context.bindService(intent,
                    connection, Context.BIND_AUTO_CREATE);

            fireEvent(Event.ACTIVATE, null);

            context.startService(intent);
        } catch (Exception e) {
            fireEvent(Event.FAILURE, e.getMessage());
        }

        isBind = true;
    }

    /**
     * Bind the activity to a background service and put them into foreground
     * state.
     */
    private void stopService() {
        Activity context = cordova.getActivity();

        Intent intent = new Intent(
                context, ForegroundService.class);

        if (!isBind)
            return;

        fireEvent(Event.DEACTIVATE, null);

        context.unbindService(connection);
        context.stopService(intent);

        isBind = false;
    }

    /**
     * Fire vent with some parameters inside the web view.
     *
     * @param event
     *      The name of the event
     * @param params
     *      Optional arguments for the event
     */
    private void fireEvent (Event event, String params) {
        String eventName;

        switch (event) {
            case ACTIVATE:
                eventName = "activate"; break;
            case DEACTIVATE:
                eventName = "deactivate"; break;
            default:
                eventName = "failure";
        }

        String active = event == Event.ACTIVATE ? "true" : "false";

        String flag = String.format("%s._isActive=%s;",
                JS_NAMESPACE, active);

        String fn = String.format("setTimeout('%s.on%s(%s)',0);",
                JS_NAMESPACE, eventName, params);

        final String js = flag + fn;

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + js);
            }
        });
    }
	
	/**
	 * static functions
	 */
	static String parseExtraName(String extraName) {
		String parseIntentExtra = extraName;
		
		try {
			parseIntentExtra = getIntentValueString(extraName);			
		}
		catch(NoSuchFieldException e) {
			parseIntentExtra = extraName;	
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
			return extraName;
		}
		return parseIntentExtra;
	}
	
	static String getIntentValueString(String flag) throws NoSuchFieldException, IllegalAccessException {
		Field field = Intent.class.getDeclaredField(flag);
		field.setAccessible(true);

		return (String) field.get(null);
	}
	
	static int getIntentValue(String flag) throws NoSuchFieldException, IllegalAccessException {
		Field field = Intent.class.getDeclaredField(flag);
		field.setAccessible(true);
		
		return field.getInt(null);
	}
	
	/* */
	private void makeNotificationCusts() {
      	 Context context = this.cordova.getActivity();
       	 String pkgName  = context.getPackageName();
       	 Intent intent   = context.getPackageManager().getLaunchIntentForPackage(pkgName);

      	 Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle("hello")
                .setContentText("chuyen nho")               
                .setOngoing(true);                
        
	 PendingIntent contentIntent = PendingIntent.getActivity(context, 999999, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	 notification.setContentIntent(contentIntent);	    
	 Notification contentNotifi = notification.build();
         NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	 
	 service.notify(999999, contentNotifi);		
    }
}
