package cmcc.mobile.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import cmcc.mobile.admin.entity.TAppUserClient;


public class Push2AppUtil {
	// 定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
	private static String appId = "";
	private static String appKey = "";
	private static String masterSecret = "";
	private static String url = "";

	private String messageDetailUrl = "";
	private String serviceDetailUrl = "";

	public String getMessageDetailUrl() {
		return messageDetailUrl;
	}

	public String getServiceDetailUrl() {
		return serviceDetailUrl;
	}

	public Push2AppUtil() {
		InputStream in = null;
		try {
			in = new FileInputStream(new File(getClass().getClassLoader().getResource("message.properties").getFile()));
			Properties pps = new Properties();
			pps.load(in);
			String appId = pps.getProperty("getui.appId");
			String appKey = pps.getProperty("getui.appkey");
			String masterSecret = pps.getProperty("getui.masterSecret");
			String url = pps.getProperty("getui.url");
			String messageDetail = pps.getProperty("messageDetailUrl");
			String serviceDetail = pps.getProperty("serviceDetailUrl");
			if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(appKey)) {
				Push2AppUtil.appId = appId;
				Push2AppUtil.appKey = appKey;
				Push2AppUtil.masterSecret = masterSecret;
				Push2AppUtil.url = url;
				this.messageDetailUrl = messageDetail;
				this.serviceDetailUrl = serviceDetail;
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != in){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Boolean AppPush(TransmissionTemplate template, List<TAppUserClient> android) throws IOException {

		IGtPush push = new IGtPush(url, appKey, masterSecret);
		// 通知透传模板
		ListMessage message = new ListMessage();
		template.setAppId(appId);
		template.setAppkey(appKey);
		message.setData(template);
		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);
		// taskId用于在推送时去查找对应的message
		String taskId = push.getContentId(message);
		List<Target> targets = new ArrayList<Target>();
		for (TAppUserClient tAppUserClient : android) {
			Target target = new Target();
			target.setAppId(appId);
			target.setClientId(tAppUserClient.getClientId());
			targets.add(target);
			}
		push.pushMessageToList(taskId, targets);
		return true;
	}

	public void IOSApnPush(APNPayload.DictionaryAlertMsg alertMsg, List<TAppUserClient> ios, String content) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);

		APNPayload payload = new APNPayload();
		payload.setContentAvailable(1);
		payload.setSound("default");
		payload.setCategory(content);
		payload.setAlertMsg(alertMsg);
		template.setAPNInfo(payload);

		ListMessage message = new ListMessage();
		message.setData(template);
		String contentId = push.getAPNContentId(appId, message);

		List<String> tokens = new ArrayList<String>();
		for (TAppUserClient client : ios) {
			tokens.add(client.getDeviceToken());
		}
		push.pushAPNMessageToList(appId, contentId, tokens);
	}

	public Boolean AppPush(TransmissionTemplate template) {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		// TransmissionTemplate template = transmissionTemplateDemo();
		template.setAppId(appId);
		template.setAppkey(appKey);
		AppMessage message = new AppMessage();
		message.setData(template);
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);
		// 推送给App的目标用户需要满足的条件
		AppConditions cdt = new AppConditions();
		List<String> appIdList = new ArrayList<String>();
		appIdList.add(appId);
		message.setAppIdList(appIdList);
		// 手机类型
		List<String> phoneTypeList = new ArrayList<String>();
		phoneTypeList.add("IOS");
		phoneTypeList.add("ANDROID");
		// 省份
		List<String> provinceList = new ArrayList<String>();
		// 自定义tag
		List<String> tagList = new ArrayList<String>();

		cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
		cdt.addCondition(AppConditions.REGION, provinceList);
		cdt.addCondition(AppConditions.TAG, tagList);
		message.setConditions(cdt);

		push.pushMessageToApp(message);
		return true;
	}

}
