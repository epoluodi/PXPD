package com.pxpd.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装http请求
 * 对apache的http请求的二次封装
 * @author liuzeren
 *
 */
public final class YYHttpClient {
	

	
	/**
	 * get请求
	 */
	public final static int REQ_METHOD_GET = 0;
	/**
	 * post请求
	 */
	public final static int REQ_METHOD_POST = 1;
	
	private HttpClient m_httpClient = null;
	private HttpGet m_httpGet = null;
	private HttpPost m_httpPost = null;
	private HttpResponse m_httpResp = null;
	private List<NameValuePair> pairList;

		
	public YYHttpClient() {
		String USER_AGENT = "Mozilla/4.0 "
				+ "(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR "
				+ "1.1.4322; .NET CLR 2.0.50727; InfoPath.2; Alexa Toolbar)";
		// 设置一些基本参数
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, false);
		HttpProtocolParams.setUserAgent(params, USER_AGENT);
		// 超时设置
		HttpConnectionParams.setConnectionTimeout(params, 15000);// 连接超时(单位：毫秒)
		// 读取超时(单位：毫秒)

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(
				params, schReg);
		m_httpClient = new DefaultHttpClient(connectionMgr, params);
		pairList =new ArrayList<>();
	}
		
	public boolean openRequest(String url, int nReqMethod) {
		closeRequest();


		if (nReqMethod == REQ_METHOD_GET) {
			m_httpGet = new HttpGet(url);

		} else if (nReqMethod == REQ_METHOD_POST) {
			m_httpPost = new HttpPost(url);

		} else {
			return false;
		}
		
		return true;
	}



	public void addHeader(String name, String value) {
		if (m_httpGet != null) {
			m_httpGet.addHeader(name, value);
		} else if (m_httpPost != null) {
			m_httpPost.addHeader(name, value);
		} 
	}
	
	public void setEntity(HttpEntity entity) {
		if (m_httpPost != null) {
			m_httpPost.setEntity(entity);
		}
	}
	public void setEntity(UrlEncodedFormEntity entity) {
		if (m_httpPost != null) {
			m_httpPost.setEntity(entity);
		}
	}

	public void setPostValuesForKey(String Key,String value)
	{
		BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key,value);
		pairList.add(basicNameValuePair);


	}


	public UrlEncodedFormEntity getPostData()
	{
		try {
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
			return urlEncodedFormEntity;
		}
		catch (Exception e)
		{e.printStackTrace();}
		return null;
	}
	
	public Boolean sendRequest() {
		if (null == m_httpClient)
			return false;
		
		try {
			if (m_httpGet != null) {

				m_httpResp = m_httpClient.execute(m_httpGet);

				return true;
			} else if (m_httpPost != null) {
				m_httpResp = m_httpClient.execute(m_httpPost);
				return true;
			}
		} catch (ClientProtocolException e) {
//			Logger.e(TAG, e);
			return false;
		} catch (IOException e) {
//			Logger.e(TAG, e);
			return false;

		}
		return false;
	}
	
	public int getRespCode() {
		if (m_httpResp != null)
			return m_httpResp.getStatusLine().getStatusCode();
		else
			return 0;
	}
	
	public Header[] getRespHeader() {
		if (m_httpResp != null)
			return m_httpResp.getAllHeaders();
		else
			return null;
	}
	
	public List<Cookie> getCookies() {
		if (m_httpClient != null)
			return ((DefaultHttpClient)m_httpClient).getCookieStore().getCookies();
		else
			return null;
	}


	public HttpResponse getHttpResponse()
	{
		try
		{


			return m_httpResp;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取服务器返回数据
	 * @return
	 */
	public byte[] getRespBodyData() {
		try {
			if (m_httpResp != null) {
				InputStream is = m_httpResp.getEntity().getContent();
				byte[] bytData = InputStreamToByte(is);
				is.close();
				return bytData;
			}
		} catch (IllegalStateException e) {

		} catch (IOException e) {

		}
		
		return null;
	}

	public InputStream getRespBodyDataInputStream() {
		try {
			if (m_httpResp != null) {
				InputStream is = m_httpResp.getEntity().getContent();

				return is;
			}
		} catch (IllegalStateException e) {

		} catch (IOException e) {

		}

		return null;
	}


	
	public void closeRequest() {
		if (m_httpGet != null)
			m_httpGet.abort();
		
		if (m_httpPost != null)
			m_httpPost.abort();
		
		m_httpResp = null;
		m_httpGet = null;
		m_httpPost = null;
	}
	
	public HttpClient getHttpClient() {
		return m_httpClient;
	}
	
	private byte[] InputStreamToByte(InputStream is) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		byte[] buf = new byte[1024 * 4];
		byte data[] = null;
		
		try {
			while ((ch = is.read(buf)) != -1) {
				out.write(buf, 0, ch);
			}
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {

		} finally {
			
		}
		
		return data;
	}
}
