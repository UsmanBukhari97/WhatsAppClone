package com.example.whatsappclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("rx7Ki9kb7HmGxZQTfh3CR3FbEwfz0QMPc3sOEm96")
                // if defined
                .clientKey("eLS2WKIqjqJJopMgYjBAQRac8WwcbxMvuQcjT9pv")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
