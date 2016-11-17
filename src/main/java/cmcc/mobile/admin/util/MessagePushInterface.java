package cmcc.mobile.admin.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessagePushInterface {

	public static OkHttpClient client = new OkHttpClient();
	private static Logger logger = Logger.getLogger(MessagePushInterface.class);

	public static boolean messagePush(String mobile, String typeName) {
		String response = "";
		try {
			JSONObject json = new JSONObject();
			json.put("fromType", "1");
			json.put("receiverType", "02");// 图文接收者范围:01(预置服务号为全部用户、其他服务号为关注此服务号的用户)02(单个或多个手机号)03(单个或多个用户ID)
			json.put("receiverPerson", mobile);
			json.put("serviceID", "0db5dc2b-c366-47a6-ab75-683111f12110");
			json.put("securityID", "ID__182371_1468308258666");
			json.put("securityKey", "DY30M7GVFFP980RXQ8S9");
			json.put("messageType", "01");
			JSONObject jsoncontent = new JSONObject();
			json.put("messageContent",
					cmcc.mobile.admin.util.Base.Base64.encodeBytes(("您有一条待办工单[" + typeName + "]").getBytes()));
			// System.out.println("1");
			String str = AESUtil.encode("DY30M7GVFFP980RXQ8S9", json.toString());
			// String str = "";
			// System.out.println("2");
			Map<String, String> sendParams = new HashMap<String, String>();
			sendParams.put("function_id", "1001");
			sendParams.put("service_id", "0db5dc2b-c366-47a6-ab75-683111f12110");
			sendParams.put("request_body", str);
			// System.out.println(str);
			String s = post("http://172.18.3.199:10086/esip", sendParams);
			// String s = post("http://112.4.17.117:10099/esip/",sendParams);
			/**
			 * jsoncontent.put("type", "mainTitle");
			 * jsoncontent.put("titleDesc", "您有一条代办工单");
			 * jsoncontent.put("titlePicUrl",
			 * "http://v2.qr.weibo.cn/inf/gen?api_key=a0241ed0d922e7286303ea5818292a76&data=https%3A%2F%2Fpassport.weibo.cn%2Fsignin%2Fqrcode%2Fscan%3Fqr%3DQRID-ja-1Bht4a-45GZJx-R5Q4wxVrRdm1AgKlUFghGl812b11%26sinainternalbrowser%3Dtopnav%26showmenu%3D0&datetime=1467020574&deadline=0&level=M&logo=http%3A%2F%2Fu1.sinaimg.cn%2Fupload%2F2014%2F05%2F27%2Fweibo-logo.png&output_type=img&redirect=0&sign=d24b666fd46bbc25972028ec5866bd3e&size=180&start_time=0&title=sso&type=url"
			 * ); jsoncontent.put("clickUrl",
			 * "http://221.178.251.109/test.jsp"); JSONArray jsonarray=new
			 * JSONArray(); jsonarray.add(jsoncontent);
			 * json.put("messageContent", jsonarray.toJSONString());
			 **/
			// System.out.println(s);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public static String post(String url, Map<String, String> map) throws IOException {
		/**
		 * RequestBody formBody = new FormBody.Builder() .add("function_id",
		 * "1001") .add("service_id", "0db5dc2b-c366-47a6-ab75-683111f12110")
		 * .add("request_body", "") .build();
		 */

		Builder f = new FormBody.Builder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			f.add(entry.getKey(), entry.getValue());
		}

		RequestBody formBody = f.build();
		Request request = new Request.Builder()
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0")
				.addHeader("Accept", "*/*").url(url).post(formBody).build();

		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	public static String get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

}
