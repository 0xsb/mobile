package cmcc.mobile.admin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author renlinggao
 * @Date 2016年7月3日
 */
public class HttpClientUtil {
	public static final int CACHE = 10 * 1024;
	private static Logger logger = Logger.getLogger(HttpClientUtil.class);

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param filePath
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public static File downFile(String url, String filePath) throws UnsupportedOperationException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = client.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();

		File file = new File(filePath);
		FileOutputStream fileout = new FileOutputStream(file);
		byte[] buffer = new byte[CACHE];
		int ch = 0;
		while ((ch = is.read(buffer)) != -1) {
			fileout.write(buffer, 0, ch);
		}
		is.close();
		fileout.flush();
		fileout.close();
		return file;
	}

	/**
	 * post方式请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, String> params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	private static String invoke(DefaultHttpClient httpclient, HttpUriRequest httpost) {

		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);

		return body;
	}

	private static String paseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();

		String charset = EntityUtils.getContentCharSet(entity);

		String body = null;
		try {
			body = EntityUtils.toString(entity);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient, HttpUriRequest httpost) {
		HttpResponse response = null;

		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

		return httpost;
	}

	/**
	 * post上传数据和文件
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 */
	public static String postFormAndFiles(String url, Map<String, String> params, List<File> files) {
		// 1:创建一个httpclient对象
		HttpClient httpclient = new DefaultHttpClient();
		Charset charset = Charset.forName("UTF-8");// 设置编码
		try {
			// 2：创建http的发送方式对象，是GET还是post
			HttpPost httppost = new HttpPost(url);

			// 3：创建要发送的实体，就是key-value的这种结构，借助于这个类，可以实现文件和参数同时上传，很简单的。
			MultipartEntity reqEntity = new MultipartEntity();
			ArrayList<FileBody> fileBodys = new ArrayList<>();
			for (File file : files) {
				FileBody bin = new FileBody(file);
				fileBodys.add(bin);
			}

			addFileBodyPart("file", fileBodys, reqEntity);
			for (String key : params.keySet()) {
				StringBody strbody = new StringBody(params.get(key), charset);
				reqEntity.addPart(key, strbody);
			}
			httppost.setEntity(reqEntity);

			// 4：执行httppost对象，从而获得信息
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			// 获得返回来的信息，转化为字符串string
			String resString = EntityUtils.toString(resEntity);
			return resString;

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
				logger.error(ignore.getMessage(), ignore);
			}
		}
		return null;
	}

	/**
	 * 当多个文件需要上传时，对文件进行组装
	 * 
	 * @param paramName
	 * @param fileBodys
	 * @param reqEntity
	 */
	private static void addFileBodyPart(String paramName, ArrayList<FileBody> fileBodys, MultipartEntity reqEntity) {
		if (fileBodys == null || fileBodys.size() < 1 || reqEntity == null || paramName == null) {
			return;
		}
		for (FileBody iteam : fileBodys) {
			reqEntity.addPart(paramName, iteam);
		}
	}
}
