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

import java.io.*;

public class apossdk extends CordovaPlugin {

    private static final String LOG_TAG = "apossdkPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("TestPrint".equals(action)) {
                TestPrint();
                callbackContext.success(200);
                return true;
            }
            if ("Print".equals(action)) {
                String key = args.getString(0);
                Print(key);
                callbackContext.success(200);
                return true;
            }
        } catch (AposException e) {
            // TODO Auto-generated catch block
            int errstatus = e.getErrorStatus();
            callbackContext.error(errstatus);
            return false;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        return false;
    }

    private void Alert(String msg) {
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

    public void TestPrint() throws AposException {
        Print printer = new Print(cordova.getActivity());
        printer.openPrinter(Print.DEVTYPE_USB, "RTPSO", 0, 0);
        Builder build = new Builder("RTPSO", Builder.MODEL_CHINESE);
        int []status = {1};
        byte clear[] = {0x1b, 0x40};
        build.addCommand(clear);
        build.addTextAlign(Builder.ALIGN_CENTER);
        build.addTextFont(Builder.FONT_A);
        String testStr = "打印测试:乱打一通返点的咯额尔范菲发";
        try {
            byte[] t_utf8 = testStr.getBytes("UTF-8");
            // String t_utf8Toibm850 = new String(t_utf8, "ibm850");
            // build.addText(t_utf8Toibm850);
            build.addCommand(t_utf8);
        } catch (UnsupportedEncodingException e) {
            build.addText("UnsupportedEncodingException:ibm850");
        }
        build.addText("\n");
        build.addText("列印測試:亂打一通返點的咯額爾范菲發\n");
        build.addText("Print Test\n");
        build.addText("テスト印刷\n");
        build.addCut(Builder.CUT_FEED);
        printer.sendData(build, 1000, status);
        build.clearCommandBuffer();
    }

    public void Print(String printtext) throws AposException {
        Print printer = new Print(cordova.getActivity());
        printer.openPrinter(Print.DEVTYPE_USB, "RTPSO", 0, 0);
        Builder build = new Builder("RTPSO", Builder.MODEL_CHINESE);
        // String[] printArr = printtext.split("!#%");
        // for (int i = 0; i < printArr.length; i++) {
        //     String[] oneprint = printArr[i].split("@$^");
        //     if (oneprint.length > 0) {
        //         ExplainComment(build, oneprint);
        //     }
        // }
        byte clear[] = {0x1b, 0x40};
        build.addCommand(clear);
        build.addText(printtext + "ss\n");
        build.addCut(Builder.CUT_FEED);
        int []status = {1};
        printer.sendData(build, 1000, status);
        build.clearCommandBuffer();
    }

    private void ExplainComment(Builder build, String[] oneprint) throws AposException {
        String comment = oneprint[0];
        if (comment == "addText") {
            build.addText(oneprint[1]);
        } else if (comment == "addTextAlign") {
            build.addTextAlign(Integer.parseInt(oneprint[1]));
        } else if (comment == "clearCommandBuffer") {
            build.clearCommandBuffer();
        } else if (comment == "addCut") {
            if (oneprint[1] == "") {
                build.addCut(Builder.CUT_FEED);
            } else {
                build.addCut(Integer.parseInt(oneprint[1]));
            }
        } else if (comment == "addTextLineSpace") {
            build.addTextLineSpace(Integer.parseInt(oneprint[1]));
        } else if (comment == "addTextRotate") {
            build.addTextRotate(Integer.parseInt(oneprint[1]));
        } else if (comment == "addTextFont") {
            build.addTextFont(Integer.parseInt(oneprint[1]));
        } else if (comment == "addTextSmooth") {
            build.addTextSmooth(Integer.parseInt(oneprint[1]));
        } else if (comment == "addTextDouble") {
            if (oneprint[1] == "") {
                build.addTextDouble(Builder.FALSE, Builder.FALSE);
            } else {
                build.addTextDouble(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
            }
        } else if (comment == "addTextSize") {
            build.addTextSize(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
        } else if (comment == "addTextStyle") {
            build.addTextStyle(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]), Integer.parseInt(oneprint[4]));
        } else if (comment == "addTextPosition") {
            build.addTextPosition(Integer.parseInt(oneprint[1]));
        } else if (comment == "addFeedUnit") {
            build.addFeedUnit(Integer.parseInt(oneprint[1]));
        } else if (comment == "addFeedLine") {
            build.addFeedLine(Integer.parseInt(oneprint[1]));
        } else if (comment == "addBarcode") {
            if (oneprint[2] == "") {
                build.addBarcode(oneprint[1], Builder.BARCODE_EAN13,
                                 Builder.HRI_BELOW, Builder.PARAM_UNSPECIFIED,
                                 2, 60);
            } else {
                build.addBarcode(oneprint[1], Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]),
                                 Integer.parseInt(oneprint[4]), Integer.parseInt(oneprint[5]), Integer.parseInt(oneprint[6]));
            }
        } else if (comment == "addSymbol") {
            build.addSymbol(oneprint[1], Builder.SYMBOL_QRCODE_MODEL_2, Builder.LEVEL_L, 120, 120, 0);
        } else if (comment == "addPageBegin") {
            build.addPageBegin();
        } else if (comment == "addPageEnd") {
            build.addPageEnd();
        } else if (comment == "addPageArea") {
            build.addPageArea(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]), Integer.parseInt(oneprint[4]));
        } else if (comment == "addPageDirection") {
            build.addPageDirection(Integer.parseInt(oneprint[1]));
        } else if (comment == "addPulse") {
            build.addPulse(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
        } else if (comment == "clear") {
            byte clear[] = {0x1b, 0x40};
            build.addCommand(clear);
        }
    }
}