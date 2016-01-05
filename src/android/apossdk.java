/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package com.mrboss.posapp.apossdk;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import java.io.IOException;
import java.io.InputStream;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.app.AlertDialog;

import com.apos.aposprinter.*;

public class apossdk extends CordovaPlugin {

    private static final String LOG_TAG = "apossdkPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("TestPrint".equals(action)) {
            try {            
                Print printer = new Print(cordova.getActivity());
                printer.openPrinter(Print.DEVTYPE_USB, "RTPSO", 0, 0);

                Builder build = new Builder("RTPSO", Builder.MODEL_ANK);
                int []status = {1};
                byte clear[] = {0x1b,0x40};
                build.addCommand(clear);
                build.addTextAlign(Builder.ALIGN_CENTER);
                build.addText("123xxstreet,xxxcity,xxxxstate\n");
                build.addCut(Builder.CUT_FEED);
                printer.sendData(build, 1000, status);
                build.clearCommandBuffer();
            }
            catch (AposException e) {
                // TODO Auto-generated catch block
                // Alert(e.getMessage());
                Alert("Error Code:" + String.valueOf(e.getErrorStatus()));
                return false;
            }
            catch (Exception e) {
                Alert(e.getMessage());
                // Alert(e.getMessage());
                return false;
            }
            return true;
        }
        return false;  
    }

    private void Alert(String msg){
        Dialog alertDialog = new AlertDialog.Builder(this.cordova.getActivity()).
                setTitle("对话框的标题").
                setMessage(msg).
                setCancelable(false).
                setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).
                create();
        alertDialog.show();
    }
}