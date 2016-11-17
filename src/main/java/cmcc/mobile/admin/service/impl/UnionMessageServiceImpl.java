package cmcc.mobile.admin.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.UnionMessageService;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.util.XMLParseUtil;
import cmcc.mobile.admin.vo.UnionMessageVo;

@Service
public class UnionMessageServiceImpl implements UnionMessageService {

	private Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	UserMapper userMapper;

	private static String APP_ID;
	private static String APP_PWD;
	private static String SSO_URL;
	private static String MSG_SERVICE_URL;

	private static final String SSO_REQUEST_BODY_TEMPLATE = "<request><token>{0}</token><employeeNumber/></request>";

	public UnionMessageServiceImpl() {
		Map<String, String> properties = PropertiesUtil.readProperties("union_msg.properties");
		APP_ID = properties.get("APP_ID");
		APP_PWD = properties.get("APP_PWD");
		SSO_URL = properties.get("SSO_URL");
		MSG_SERVICE_URL = properties.get("MSG_SERVICE_URL");
	}

	@Override
	public boolean checkSSO(HttpServletRequest request, String token) {
		String requestBody = MessageFormat.format(SSO_REQUEST_BODY_TEMPLATE, token);

		HttpClientBuilder httpclient = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpclient.build();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();

		HttpPost ssoRequest = new HttpPost(SSO_URL);
		StringEntity entity = new StringEntity(requestBody, "UTF-8");
		ssoRequest.setEntity(entity);

		ssoRequest.setConfig(requestConfig);
		HttpResponse httpResponse = null;
		try {
			httpResponse = closeableHttpClient.execute(ssoRequest);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (HttpStatus.ACCEPTED.value() != httpResponse.getStatusLine().getStatusCode() || httpEntity == null) {
				throw new RuntimeException("SSO接口未正确返回信息!,响应内容:\n" + httpResponse);
			}
			Document document = XMLParseUtil.loadXML(EntityUtils.toString(httpEntity, "UTF-8"));
			Element response = document.getDocumentElement();
			String status = response.getElementsByTagName("status").item(0).getTextContent();
			// String uid =
			// response.getElementsByTagName("uid").item(0).getTextContent();
			String employeeNumber = response.getElementsByTagName("employeeNumber").item(0).getTextContent();
			String message = response.getElementsByTagName("message").item(0).getTextContent();
			if (!status.equals("ok")) {
				// 001 请求参数错误,
				// 002 连接认证服务器失败，
				// 003 token认证失败,
				// 004 session已过期，
				// 005 查询用户信息失败
				throw new RuntimeException("请求SSO接口失败,errorcode:" + message);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("workNumber", employeeNumber);
			List<User> list = userMapper.selectByQueryCriteria(map);
			if (!list.isEmpty()) {
				return false;
			}

			User user = list.get(0);
			HttpSession session = request.getSession();
			session.setAttribute("DynamicDbName", "business1");
			session.setAttribute("companyId", user.getCompanyId());
			session.setAttribute("companyName", "江苏移动");
			session.setAttribute("userId", user.getId());
			session.setAttribute(BaseController.MOBILE, user.getMobile());

			return true;
		} catch (Exception e) {
			LOGGER.error("请求统一待办接口失败:", e);
			// throw new RuntimeException("请求统一待办接口失败:",e);
		} finally {
			if (null != closeableHttpClient) {
				try {
					closeableHttpClient.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
			}
		}
		return false;
	}

	@Override
	public void insert(ThirdApprovalDeal deal, String taskName) {
		UnionMessageVo msg = new UnionMessageVo();
		msg.setMsg_type("0");
		msg.setApp_msg_id(deal.getId());

		User receiver = userMapper.selectByPrimaryKey(deal.getUserId());
		// TODO 前一步发起者
		User sender = userMapper.selectByPrimaryKey(deal.getUserStartId());

		msg.setInstance_id(deal.getRunId());
		msg.setSender(sender.getWorkNumber());
		msg.setSender_name(sender.getUserName());
		msg.setReceiver(receiver.getWorkNumber());
		msg.setReceiver_name(receiver.getUserName());
		// TODO 标题
		msg.setMsg_title(deal.getApprovalName());
		msg.setDoc_type(deal.getApprovalName());

		msg.setMsg_status(taskName);
		msg.setMsg_url(deal.getLink());
		msg.setUrgent_level(deal.getRemark4());

		// 构造请求对象
		HttpPost httpPost = new HttpPost(MSG_SERVICE_URL + "/unionMessage");
		StringEntity entity = new StringEntity(getMessageParam(msg), "UTF-8");
		httpPost.setEntity(entity);
		// 执行请求
		doRequest(httpPost);
	}

	@Override
	public void delete(ThirdApprovalDeal deal) {
		boolean deleteInstance = StringUtils.equals(deal.getStatus(), "2");
		StringBuilder sb = new StringBuilder();
		sb.append(MSG_SERVICE_URL).append("?app_id=").append(APP_ID).append("&app_pwd=").append(APP_PWD);

		if (deleteInstance) {
			sb.append("&instance_id=").append(deal.getRunId());
		} else {
			sb.append("app_msg_id=").append(deal.getId());
		}

		// 构造请求对象
		HttpDelete request = new HttpDelete(sb.toString());
		// 执行请求
		doRequest(request);

	}

	@Override
	public void updateState(ThirdApprovalDeal deal) {
		updateState(deal, "0");
	}

	@Override
	public void updateState(ThirdApprovalDeal deal, String msgType) {
		UnionMessageVo msg = new UnionMessageVo();
		msg.setMsg_type(msgType);
		msg.setApp_msg_id(deal.getId());

		User receiver = userMapper.selectByPrimaryKey(deal.getUserId());
		msg.setReceiver(receiver.getWorkNumber());
		msg.setReceiver_name(receiver.getUserName());

		// 构造请求对象
		HttpPut request = new HttpPut(MSG_SERVICE_URL + "/status");
		StringEntity entity = new StringEntity(getMessageParam(msg), "UTF-8");
		request.setEntity(entity);
		// 执行请求
		doRequest(request);

	}

	private String getMessageParam(UnionMessageVo msg) {
		msg.setApp_id(APP_ID);
		msg.setApp_pwd(APP_PWD);
		return JSON.toJSONString(msg);
	}

	private void doRequest(HttpRequestBase request) {
		HttpClientBuilder httpclient = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpclient.build();
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();

		request.setConfig(requestConfig);
		HttpResponse httpResponse = null;
		try {
			httpResponse = closeableHttpClient.execute(request);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (HttpStatus.ACCEPTED.value() != httpResponse.getStatusLine().getStatusCode() || httpEntity == null) {
				throw new RuntimeException("请求未正确返回信息!,响应内容:\n" + httpResponse);
			}
			String responseStr = EntityUtils.toString(httpEntity, "UTF-8");
			JSONObject responseJson = JSONObject.parseObject(responseStr);
			if (responseJson.getIntValue("result") != 1) {
				// 当result为0时，字段有效。1表示系统异常,2表示接入系统ID或密码错误，3表示传入参数不符合要求，4表示重复数据
				throw new RuntimeException("调用统一待办接口错误,错误信息:" + responseStr);
			}
		} catch (Exception e) {
			LOGGER.error("请求统一待办接口失败:", e);
			// throw new RuntimeException("请求统一待办接口失败:",e);
		} finally {
			if (null != closeableHttpClient) {
				try {
					closeableHttpClient.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
			}
		}
	}

}
