package com.example.mohamedshiyas.parsenoteapp;

import android.app.Application;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.HashMap;

/**
 * Created by mohamedshiyas on 07/08/17.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parse_app_id))
                .server(getResources().getString(R.string.parse_server_url))
                .clientKey(getResources().getString(R.string.parse_client_key))
                .enableLocalDataStore()
                .build());
        ParseObject.registerSubclass(Notes.class);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
    }
}