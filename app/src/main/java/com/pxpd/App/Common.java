package com.pxpd.App;

import android.content.res.XmlResourceParser;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Created by Stereo on 16/7/8.
 */
public class Common {


    public static String getjsonForXML(String xml) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        String inner;
        try {
            parser.setInput(inputStream, "UTF-8");
            // 直到文档的结尾处
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                // 如果遇到了开始标签
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();// 获取标签的名字
                    if (tagName.equals("string")) {
                        inner = parser.nextText();

                        return inner;
                    }
                }
                parser.next();// 获取解析下一个事件
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return "";
    }

}
