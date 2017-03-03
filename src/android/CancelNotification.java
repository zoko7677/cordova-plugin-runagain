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
import android.util.Log ;

 
public class CancelNotification extends CordovaPlugin {
 
    /*TextView tv = null;
    //@Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        System.out.println("onCreate");        
        Bundle extras = getIntent().getExtras();
 
        if(extras != null){
            String data1 = extras.getString("id");            
            System.out.println("Ddata1 : " + data1);            
        }
        
        alertView("Hello");
       
    }*/ 
    public void onCreate(){
        Log.d("NotifiNote",1);
        webView.loadUrl("javascript:alert('load notifi mode');");
    }  
}
