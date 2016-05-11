package com.fujitsu.tmmin.central.api;

import android.os.StrictMode;

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
import java.util.List;

import com.fujitsu.tmmin.central.domain.ConfirmedResultMap;
import com.fujitsu.tmmin.central.id.common.domain.Message;

/**
 * Created by Ibnu on 25/04/2016.
 */
public class CENTRAL010101WAPI {
    private static CENTRAL010101WAPI instance = null;
    private HttpClient client;
    private String host = "192.168.137.1";
    private String port = "81";
    private String basicURL = "http://"+host+":"+port+"/CENTRAL010101W";
    public static CENTRAL010101WAPI Instance()
    {
        CENTRAL010101WAPI result = null;
        if (instance == null)
        {
            result = new CENTRAL010101WAPI();
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            result.client = new DefaultHttpClient(httpParameters);
        }
        return result;
    }

    public Message isValidBody(String bodyNo, String termCd, String roleId){
        Message result = null;
        StringBuilder sbUrl = new StringBuilder(basicURL);
        sbUrl.append("/BodyValidation");
        sbUrl.append("?bodyNo=" + bodyNo);
        sbUrl.append("&termCd=" + termCd);
        sbUrl.append("&roleId="+roleId);

        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            HttpGet request = new HttpGet(sbUrl.toString());
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if(status==200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                result = new Gson().fromJson(data, Message.class);
            }else{
                result = new Message();
                result.setContent("error : "+Integer.toString(status));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new Message();
            result.setContent(e.getMessage());
        } catch (Exception e){
            result = new Message();
            if(e.getMessage()!=null){
                result.setContent(e.getMessage());
            }else {
                result.setContent(e.getClass().toString());
            }
        }

        return result;
    }

    public ConfirmedResultMap confimProcess(String bodyNo, String termCd, int originalAmountPart, String roleId, String userName, List<String> parts){
        ConfirmedResultMap result = null;
        StringBuilder sbUrl = new StringBuilder(basicURL);
        sbUrl.append("/ConfirmPartValidation");
        sbUrl.append("?bodyNo=" + bodyNo);
        sbUrl.append("&termCd=" + termCd);
        sbUrl.append("&userName=" + userName);
        sbUrl.append("&roleId=" + roleId);
        sbUrl.append("&originalAmountPart=" + Integer.toString(originalAmountPart));
        sbUrl.append(getListParameters(parts));

        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            HttpGet request = new HttpGet(sbUrl.toString());
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if(status==200){
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                result = new Gson().fromJson(data, ConfirmedResultMap.class);
            }else{
                result = new ConfirmedResultMap();
                result.setContent("error : "+Integer.toString(status));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new ConfirmedResultMap();
            result.setContent(e.getMessage());
        } catch (Exception e){
            result = new ConfirmedResultMap();
            if(e.getMessage()!=null){
                result.setContent(e.getMessage());
            }else {
                result.setContent(e.getClass().toString());
            }
        }

        return result;
    }

    private String getListParameters(List<String> parts) {
        String result = "";
        for(String part : parts){
            result=result+"&partIDs="+part;
        }
        return result;
    }

//    public Message checkDuplicatePartId(String partId, List<String> partList){
//        Message result = null;
//
//        try {
//            final ObjectMapper mapper = new ObjectMapper();
//            final OutputStream out = new ByteArrayOutputStream();
//            mapper.writeValue(out, partList);
//
//            StringBuilder sbUrl = new StringBuilder(basicURL);
//            sbUrl.append("/CheckDuplicationPart");
//            HttpPost post = new HttpPost(sbUrl.toString());
//
//            Map<String,Object> param = new HashMap<String,Object>();
//            param.put("partId",partId);
//            param.put("partList", new String(((ByteArrayOutputStream) out).toByteArray()).toString());
//
//            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//            postParameters.add(new BasicNameValuePair("partId", partId));
//            postParameters.add(new BasicNameValuePair("partList", new String(((ByteArrayOutputStream) out).toByteArray()).toString()));
//            post.setEntity(new UrlEncodedFormEntity(postParameters));
//
//
////            sbUrl.append("?partId=" + partId);
////            sbUrl.append("&partList=" + new String(((ByteArrayOutputStream)out).toByteArray()));
//
//            //HttpGet request = new HttpGet(sbUrl.toString());
//            HttpResponse response = client.execute(post);
//            int status = response.getStatusLine().getStatusCode();
//            if(status==200){
//                HttpEntity entity = response.getEntity();
//                String data = EntityUtils.toString(entity);
//                ObjectMapper objectMapper = new ObjectMapper();
//                result = objectMapper.readValue(data, Message.class);
//            }else{
//                result = new Message();
//                result.setContent("error : "+Integer.toString(status));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            result = new Message();
//            result.setContent(e.getMessage());
//        } catch (Exception e){
//            result = new Message();
//            result.setContent(e.getMessage());
//        }
//
//        return result;
//    }
}
