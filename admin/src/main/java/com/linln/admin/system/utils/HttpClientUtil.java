package com.linln.admin.system.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import static javax.swing.text.DefaultStyledDocument.ElementSpec.ContentType;

public class HttpClientUtil {
    public static String doPost(String postUrl,String param){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(postUrl);
            ByteArrayEntity byteArrayEntity=new ByteArrayEntity(param.getBytes("utf-8"), org.apache.http.entity.ContentType.APPLICATION_JSON);
            httpPost.setEntity(byteArrayEntity);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"UTF-8");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;

    }



}
