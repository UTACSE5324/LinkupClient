package service;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import okhttp3.Headers;
import set2.linkup.R;

/**
 * Name: LinkupApplication
 * Description: Used for initialization of Http header, Http cache, SharedPreference.
 * Created on 2016/10/2 0002.
 */

public class LinkupApplication extends Application{
    public static final String APPLICATION_NAME = "Linkup";
    public static SharedPreferences preferences;
    public static Context context;
    public static GoogleApiClient apiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences(
                APPLICATION_NAME, Context.MODE_PRIVATE);
        context = this;

        initHttpUtil();
        initGoogleClient();
    }


    public void initGoogleClient(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        apiClient.connect();
    }

    public void initHttpUtil(){
        OkHttpFinalConfiguration.Builder builder =
                new OkHttpFinalConfiguration.Builder();

        //common http header
        Headers commonHeaders = new Headers.Builder().build();
        builder.setCommenHeaders(commonHeaders);

        OkHttpFinal.getInstance().init(builder.build());
    }



    public static Context getContext(){
        return context;
    }

    public static SharedPreferences getPreferences(){
        return preferences;
    }

    public static String getStringPref(String key){
        return preferences.getString(key,"");
    }

    public static void setStringPref(String key, String value){
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(key, value);
        ed.commit();
    }
}
