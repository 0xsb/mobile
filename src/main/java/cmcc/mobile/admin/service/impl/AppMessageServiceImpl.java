package cmcc.mobile.admin.service.impl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cmcc.mobile.admin.base.Constants;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.EmailConfigMapper;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.TAppMessageMapper;
import cmcc.mobile.admin.dao.TAppSendMessageMapper;
import cmcc.mobile.admin.dao.TAppUserClientMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.EmailConfig;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.TAppMessage;
import cmcc.mobile.admin.entity.TAppSendMessage;
import cmcc.mobile.admin.entity.TAppUserClient;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.AppUserService;
import cmcc.mobile.admin.service.MsgService;
import cmcc.mobile.admin.service.TAppMessageService;
import cmcc.mobile.admin.util.Push2AppUtil;
import cmcc.mobile.admin.vo.AppMessageVo;
import cmcc.mobile.admin.vo.AppOrganizationVo;
import cmcc.mobile.admin.vo.PageVo;



/**
 * 
 * @author wubj
 *
 */
@Service
public class AppMessageServiceImpl implements TAppMessageService {

	@Autowired
	private TAppMessageMapper messageMapper ;
	@Autowired 
	private TAppSendMessageMapper appSendMessageMapper ;
	@Autowired
	private TotalUserMapper totalUserMapper ;
	@Autowired
	private AppUserService appUserService ;
	@Autowired
	private OrganizationMapper organizationMapper ;
	@Autowired
	private TAppUserClientMapper userClientMapper ;
	@Autowired
	private UserMapper userMapper ;
	@Autowired
	private EmailConfigMapper emailConfigMapper ;
	@Override
	public int deleteByPrimaryKey(Long id) {
		return messageMapper.updateById(id);
	}


	@Override
	public TAppMessage selectByPrimaryKey(Long id) {
		return messageMapper.selectByPrimaryKey(id);
	}



	@Override
	public JsonResult updateByPrimaryKey(TAppMessage record) {
		// 保存公告
		int isOK = messageMapper.updateByPrimaryKeySelective(record);
		
			// 普通公告进行推送
		if (record.getType()==1){
				/**
				 * 去掉重复手机号
				 */
				Map<String, String> lUsers = new HashMap<String, String>();
				Map<String, Object> maps = new HashMap<String, Object>();
				List<String> strings = new ArrayList<String>();
				List<User> user = userMapper.selectByCompanyId(record.getCompanyId()) ;
				// 发送对象为公司的话										
						for (User tAppUser : user) {
							if (!lUsers.containsKey(tAppUser.getMobile())) {
								lUsers.put(tAppUser.getMobile(), tAppUser.getMobile());
								strings.add(tAppUser.getMobile().toString());
							}
						}
				
				// 控制手机号
				if (CollectionUtils.isNotEmpty(strings) && strings.size() > 0) {
					maps.put("list", strings);
				}

				List<TAppUserClient> AllClients = userClientMapper.selectByAccount(maps);
				List<TAppUserClient> Android = new ArrayList<>();
				List<TAppUserClient> IOS = new ArrayList<>();
				if (CollectionUtils.isNotEmpty(AllClients)) {
					for (TAppUserClient tAppUserClient : AllClients) {
						if (tAppUserClient.getPhoneType() == 1) {
							Android.add(tAppUserClient);
						} else {
							IOS.add(tAppUserClient);
						}
					}
					Push2AppUtil util = new Push2AppUtil();
					TransmissionTemplate template = new TransmissionTemplate();
					template.setTransmissionType(2);
					JSONObject object = new JSONObject();
					object.put("type", 1);
					object.put("title", "公告通知");
					object.put("content", record.getTitle());
					object.put("url", util.getMessageDetailUrl() + record.getId());
					template.setTransmissionContent(object.toJSONString());
					/**
					 * ios透传模板
					 */

					APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
					alertMsg.setTitle("公告标题：" + record.getTitle());
					// 通知文本消息字符串
					alertMsg.setBody(record.getDetail());
					// 指定执行按钮所使用的Localizable.strings
					alertMsg.setActionLocKey("滑动查看");
					// 如果不是单元测试，真实推送
					
					try {
						util.AppPush(template, Android);
						util.IOSApnPush(alertMsg, IOS, object.toJSONString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
		}
		if(isOK==1){
			return new JsonResult(true,"修改成功！",null);
		}
		return new JsonResult(false,"修改失败",null) ;
			
	}


	@Override 
	public int save(AppMessageVo record,boolean sendMessage,List<String> strings) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") ;
		// 保存公告
				TAppMessage mess = new TAppMessage();
				BeanUtils.copyProperties(record, mess);
				mess.setType(record.getTypeId());
				int isOK = messageMapper.insertSelective(mess);
				
					// 普通公告进行推送
				if (record.getTypeId()==1){
						/**
						 * 去掉重复手机号
						 */
						Map<String, String> lUsers = new HashMap<String, String>();
						Map<String, Object> maps = new HashMap<String, Object>();
						List<User> user = userMapper.selectByCompanyId(record.getCompanyId()) ;
						// 发送对象为公司的话										
								for (User tAppUser : user) {
									if (!lUsers.containsKey(tAppUser.getMobile())) {
										lUsers.put(tAppUser.getMobile(), tAppUser.getMobile());
										strings.add(tAppUser.getMobile().toString());
									}
								}
						
						// 控制手机号
						if (CollectionUtils.isNotEmpty(strings) && strings.size() > 0) {
							maps.put("list", strings);
						}

						List<TAppUserClient> AllClients = userClientMapper.selectByAccount(maps);
						List<TAppUserClient> Android = new ArrayList<>();
						List<TAppUserClient> IOS = new ArrayList<>();
						if (CollectionUtils.isNotEmpty(AllClients)) {
							for (TAppUserClient tAppUserClient : AllClients) {
								if (tAppUserClient.getPhoneType() == 1) {
									Android.add(tAppUserClient);
								} else {
									IOS.add(tAppUserClient);
								}
							}
							Push2AppUtil util = new Push2AppUtil();
							TransmissionTemplate template = new TransmissionTemplate();
							template.setTransmissionType(2);
							JSONObject object = new JSONObject();
							object.put("type", 1);
							object.put("title", "公告通知");
							object.put("content", mess.getTitle());
							object.put("url", util.getMessageDetailUrl() + mess.getId());
							template.setTransmissionContent(object.toJSONString());
							/**
							 * ios透传模板
							 */

							APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
							alertMsg.setTitle("公告标题：" + mess.getTitle());
							// 通知文本消息字符串
							alertMsg.setBody(mess.getDetail());
							// 指定执行按钮所使用的Localizable.strings
							alertMsg.setActionLocKey("滑动查看");
							// 如果不是单元测试，真实推送
							
							try {
								util.AppPush(template, Android);
								util.IOSApnPush(alertMsg, IOS, object.toJSONString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
				}
					
				return isOK;
	}



	@Override
	public JsonResult selectByHistroyNotice(String type, String companyId,String title, String beginTime,
			String endTime, String status,PageVo vo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startRow", vo.getStartRow());
		map.put("endRow", vo.getEndRow());
		map.put("title", title);
		map.put("type", StringUtils.isEmpty(type) ? null : Long.parseLong(type));
		if (null != beginTime) {
			map.put("beginTime", Long.parseLong(beginTime));
		}
		if (null != endTime) {
			map.put("endTime", Long.parseLong(endTime));
		}
		if (StringUtils.isNotEmpty(status)) {
			map.put("status", Integer.parseInt(status));
		}
			

		map.put("companyId", companyId);
		int count = messageMapper.selectCountByHistroyNotice(map);
		vo.setTotalCountAndPageTotal(count);
		List<AppMessageVo> list = messageMapper.selectByHistroyNotice(map);
		return new JsonResult(true, "获取成功！", list,vo);
	}

	private Map<String, TAppSendMessage> initSendMessage(Map<String, TAppSendMessage> map, TAppSendMessage appSendMessage) {
		if (appSendMessage.getOrgId() == null) {
			map.put(appSendMessage.getCompanyId() + "|", appSendMessage);
		} else {
			if (!map.containsKey(appSendMessage.getCompanyId() + "|"))
				initOrg(map, appSendMessage);
		}
		return map;
	}
	
	private void initOrg(Map<String, TAppSendMessage> map, TAppSendMessage appSendMessage) {
		// key: cid+|+orgid
		map.put(appSendMessage.getCompanyId() + "|" + appSendMessage.getOrgId(), appSendMessage);
		Organization appOrganization = new Organization();
		appOrganization.setCompanyId(appSendMessage.getCompanyId());
		appOrganization.setPreviousId(appSendMessage.getOrgId());
		List<Organization> orgs = organizationMapper.selectByCidandPreId(appOrganization);
		for (Organization organization : orgs) {
			TAppSendMessage sendMessage = new TAppSendMessage();
			sendMessage.setCompanyId(appSendMessage.getCompanyId());
			sendMessage.setCname(appSendMessage.getCname());
			sendMessage.setOrgId(organization.getId());
			sendMessage.setDname(organization.getOrgName());
			map.put(organization.getCompanyId() + "|" + organization.getId(), sendMessage);
			initOrg(map, sendMessage);
		}
	}


	@Override
	public int updateByPrimaryKeySelective(TAppMessage message) {
		return messageMapper.updateByPrimaryKeySelective(message);
		
	}


	@Override
	public JsonResult selectAllParams(AppMessageVo vo){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		int count = 0 ;
		List<TAppMessage> list1 = new ArrayList<>() ;
		if(vo.getTypeId()==0){
			map1.put("type", vo.getTypeId());
			map1.put("companyId", vo.getCompanyId());
			map1.put("startRow", vo.getStartRow());
			map1.put("endRow", vo.getEndRow());
			count = messageMapper.selectCountAllByMap1(map1);
			list1 = messageMapper.selectAllByMap1(map1);
		}else{
		map.put("type", vo.getTypeId());
		map.put("companyId", vo.getCompanyId());
		map.put("startRow", vo.getStartRow());
		map.put("endRow", vo.getEndRow());
		count = messageMapper.selectCountAllParams(map);
		list1 = messageMapper.selectAllParams(map);
		}
		for (TAppMessage tAppMessage : list1) {
			tAppMessage.setTime(tAppMessage.getCreateTime());
		}
	
		JsonResult result = new JsonResult(true, list1.size() + "", list1);
		PageVo pageVo = new PageVo();
		pageVo.setPageNo(vo.getPageNo());
		pageVo.setPageSize(vo.getPageSize());
		pageVo.setTotalCountAndPageTotal(count);
		result.setPageVo(pageVo);
		return result;
	}

	@Override
	public JsonResult checkMessageUpdate(TAppMessage vo) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		long count = 0 ;
		List<TAppMessage> messages = new ArrayList<>() ;
		if(vo.getType()==0){
			map1.put("type", vo.getType()) ;
			map1.put("companyId", vo.getCompanyId()) ;
			map1.put("createTime",vo.getCreateTime()) ;
			map1.put("startRow", 0);
			map1.put("endRow", 1);
			count = messageMapper.selectCountAllByMap1(map1);
			messages = messageMapper.selectAllByMap1(map1);
		}else{
			map.put("type", vo.getType()) ;
			map.put("companyId", vo.getCompanyId()) ;
			map.put("createTime",vo.getCreateTime()) ;
			map.put("startRow", 0);
			map.put("endRow", 1);
			count = messageMapper.selectCountAllParams(map);
			messages = messageMapper.selectAllParams(map);
		}
		
		return new JsonResult(true, "" + count, CollectionUtils.isNotEmpty(messages) ? messages.get(0) : null);
	}


	/**
	 * 新增常用邮箱
	 * @param config
	 * @return
	 */
	@Override
	public JsonResult addEmailUrl(EmailConfig config) {
		EmailConfig email = emailConfigMapper.selectByUserId(config) ;
		if(email!=null){
			return new JsonResult(false,"该用户已经存在邮箱信息",null) ;
		}
		int isOk = emailConfigMapper.insertSelective(config) ;
		if(isOk==1){
			return new JsonResult(true,"新增成功",null) ;
		}
		return new JsonResult(false,"新增失败",null) ;
	}


	/**
	 * 编辑常用邮箱
	 * @param config
	 * @return
	 */
	@Override
	public JsonResult updateEmailUrl(EmailConfig config) {
		int isOk = emailConfigMapper.updateByPrimaryKeySelective(config) ;
		if(isOk==1){
			return new JsonResult(true,"更新成功",null) ;
		}
		return new JsonResult(false,"新增失败",null) ;
		
	}


	@Override
	public JsonResult selectEmailUrl(EmailConfig config) {
		EmailConfig email = new EmailConfig() ;
		email = emailConfigMapper.selectByUserId(config) ;
		if(email!=null){
			return new JsonResult(true,"获取成功！",email) ;
		}
		return new JsonResult(true, "没有默认邮箱",null);
	}

}
