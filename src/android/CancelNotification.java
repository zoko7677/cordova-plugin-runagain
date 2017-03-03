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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.webkit.WebView;
import android.app.AlertDialog;
import android.util.Log ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelNotification extends Activity{  
    
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        BackgroundMode  inst = new BackgroundMode();
        inst.callJava();
        Bundle extras = getIntent().getExtras();
        Log.d("Notifi","7899999");
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.google.com");
        /*WebView webview = new WebView(this);
        webview.loadUrl("javascript:alert('load notifi mode4');");*/
        if(extras != null){
            String data1 = extras.getString("id");                        
            Log.d("Notifi Alert","Data Sent from Clicking Notification nData 1 : " + data1);
        }
    }   
}
