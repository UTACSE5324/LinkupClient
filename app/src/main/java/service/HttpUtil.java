package service;

import java.io.File;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import util.UserUtil;

/**
 * Name: Constant
 * Description: Used for Http request.
 *
 * Created on 2016/10/2 0002.
 */

public class HttpUtil {

    /*
    * GOOGLE_KEY Google Translate API Primary Key
    * */
    public static final String GOOGLE_KEY = "AIzaSyDfFV2_CJD1NOe1a7hiedPlOXZySVe92D4";

    /*
    * decide the header of request
    * NORMAL_PARAMS: normal one
    * FILE_PARAMS: from request
    * NON_TOKEN_PARAMS; request without user token
    * */
    public final static int NORMAL_PARAMS = 0;
    public final static int FILE_PARAMS = 1;
    public final static int NON_TOKEN_PARAMS = 2;

    RequestParams params;

    public HttpUtil(int type){
        params = new RequestParams();
        switch(type){
            case FILE_PARAMS:
                params.addHeader("Content-Type", "multipart/form-data");
                break;
            case NON_TOKEN_PARAMS:
                params.addHeader("Content-Type", "application/x-www-form-urlencoded");
                break;
            default:
                params.addHeader("Content-Type", "application/x-www-form-urlencoded");
                break;
        }
    }

    public HttpUtil addHead(String key, String value){
        params.addHeader(key , value);
        return this;
    }

    public HttpUtil add(String key, String value){
        params.addFormDataPart(key,value);
        return this;
    }

    /*
    * addBody used for post file
    * */
    public HttpUtil addBody(File file){
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MediaType type = MediaType.parse("multipart/form-data");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(type)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"Filedata\"; filename=\""+file.getName()+"\""), fileBody)
                .build();

        params.setCustomRequestBody(requestBody);
        return this;
    }

    /*
    * translate used for translate request
    * */
    public void translate(List<String> source, BaseHttpRequestCallback callback){

        for(int i = 0 ; i< source.size() ; i++){
            params.addFormDataPart("q",source.get(i));
        }

        params.addFormDataPart("key", HttpUtil.GOOGLE_KEY);
        params.addFormDataPart("source","en");
        params.addFormDataPart("target", LinkupApplication.getStringPref(UserUtil.LANGUAGE));
        HttpRequest.get(Constant.URL_TRANSLATE,params,callback);
    }

    public void translateReverse(List<String> source, BaseHttpRequestCallback callback){

        for(int i = 0 ; i< source.size() ; i++){
            params.addFormDataPart("q",source.get(i));
        }

        params.addFormDataPart("key", HttpUtil.GOOGLE_KEY);
        params.addFormDataPart("source",LinkupApplication.getStringPref(UserUtil.LANGUAGE));
        params.addFormDataPart("target", "en");
        HttpRequest.get(Constant.URL_TRANSLATE,params,callback);
    }

    public void post(String url, BaseHttpRequestCallback callback){
        HttpRequest.post(url,params,callback);
    }

    public void get(String url, BaseHttpRequestCallback callback){
        HttpRequest.get(url,params,callback);
    }
}
