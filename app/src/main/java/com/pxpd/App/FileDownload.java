package com.pxpd.App;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Base64;
import android.util.Log;


import com.pxpd.http.YYHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class FileDownload {


    private String _fileurl;
    private YYHttpClient yyHttpClient;
    private IFileDownload iFileDownload;


    public FileDownload(String fileurl,IFileDownload iFileDownload) {
        this._fileurl = fileurl;
        yyHttpClient = new YYHttpClient();
        this.iFileDownload=iFileDownload;
    }


    public void streamDownLoadFile() {

        Log.i("下载地址", _fileurl);


        yyHttpClient.openRequest(_fileurl, YYHttpClient.REQ_METHOD_GET);
        yyHttpClient.sendRequest();
        Message message = new Message();
        HttpEntity httpEntity = yyHttpClient.getHttpResponse().getEntity();
        if (httpEntity == null) {
            iFileDownload.OnFileDownloadEvent(1);
            return;
        }
        InputStream inStream;
        ByteArrayOutputStream outStream;
        byte[] bufferfile = null;
        try {
            inStream = httpEntity.getContent();
            outStream = new ByteArrayOutputStream();
            Log.i("下载文件大小inStream:", String.valueOf(inStream.available()));
            int maxbuff = 1024 * 5000;
            byte[] buffer = new byte[maxbuff];
            int len = 0;

            if (inStream == null) {
                iFileDownload.OnFileDownloadEvent(1);
                return;
            }
            System.gc();
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            bufferfile = outStream.toByteArray();
            outStream.close();
            inStream.close();
            if (bufferfile == null)
            {
                iFileDownload.OnFileDownloadEvent(1);
                return;
            }
            Log.i("下载文件大小:", String.valueOf(bufferfile.length));
//			SuyApplication.getApplication().getCacheDir()
//            File file = new File(SuyApplication.getApplication().getCacheDir(),
//                    mediaid + imgamub + mediatype);
            File file = new File(Environment.getExternalStorageDirectory(),
                    "basedb.db");
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bufferfile);
            fileOutputStream.close();
            iFileDownload.OnFileDownloadEvent(0);

        } catch (Exception e) {
            e.printStackTrace();
            iFileDownload.OnFileDownloadEvent(1);
        }

    }





    byte[] GetFileBytes(String filepath) {
        byte[] buffer;
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 下载接口
     */
    public interface IFileDownload
    {
        //下载事件 r =0 成功 1 失败
        void OnFileDownloadEvent(int r);
    }
}
