package com.fujitsu.tmmin.central.api;


import android.os.StrictMode;

import com.fujitsu.tmmin.central.id.common.domain.Message;
import com.fujitsu.tmmin.central.id.common.domain.UserSessionInfo;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Ibnu on 23/04/2016.
 */
public class CENTRAL010102WAPI {
    private static CENTRAL010102WAPI instance = null;
    private HttpClient client;
    private String host = "192.168.137.1";
    private String port = "82";
    private String basicURL = "http://"+host+":"+port+"/CENTRAL010102W";
    public static CENTRAL010102WAPI Instance()
    {
        CENTRAL010102WAPI result = null;
        if (instance == null)
        {
            result = new CENTRAL010102WAPI();
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            result.client = new DefaultHttpClient(httpParameters);
        }
        return result;
    }

    public UserSessionInfo validateUser(String UserId, String Pass, String Mac){
        UserSessionInfo result = null;
        StringBuilder sbUrl = new StringBuilder(basicURL);
        sbUrl.append("/ValidateUser");
        sbUrl.append("?userId=" + UserId);
        sbUrl.append("&pass=" + Pass);
        sbUrl.append("&mac="+ Mac);

        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            HttpGet request = new HttpGet(sbUrl.toString());
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if(status==200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                result = new Gson().fromJson(data, UserSessionInfo.class);
            }else{
                result = new UserSessionInfo();
                result.setContent("error : "+Integer.toString(status));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new UserSessionInfo();
            result.setContent(e.getMessage());
        } catch (Exception e){
            result = new UserSessionInfo();
            if(e.getMessage()!=null){
                result.setContent(e.getMessage());
            }else {
                result.setContent(e.getClass().toString());
            }
        }

        return result;
    }

    public UserSessionInfo forgotPass(String msgCD){
        UserSessionInfo result = null;
        StringBuilder sbUrl = new StringBuilder(basicURL);
        sbUrl.append("/forgotPass");
        sbUrl.append("?msgCD=" + msgCD);

        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            HttpGet request = new HttpGet(sbUrl.toString());
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if(status==200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                result = new Gson().fromJson(data, UserSessionInfo.class);
            }else{
                result = new UserSessionInfo();
                result.setContent("error : "+Integer.toString(status));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new UserSessionInfo();
            result.setContent(e.getMessage());
        } catch (Exception e){
            result = new UserSessionInfo();
            if(e.getMessage()!=null){
                result.setContent(e.getMessage());
            }else {
                result.setContent(e.getClass().toString());
            }
        }

        return result;
    }
}
