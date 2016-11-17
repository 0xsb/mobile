package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat.Value;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ApprovalDataMapper;
import cmcc.mobile.admin.dao.ApprovalMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigMapper;
import cmcc.mobile.admin.dao.ThirdApprovalDealMapper;
import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.service.ReportFormsService;
import cmcc.mobile.admin.vo.ApprovalVo;
import cmcc.mobile.admin.vo.JsonData;

@Service
public class ReportFormsServiceImpl implements ReportFormsService {
	@Autowired
	private ApprovalMapper dataMapper;
	@Autowired
	private ApprovalTableConfigMapper dealMapper;

	// 自定义查询报表
	@Override
	public JsonResult getReport(ApprovalVo record, String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> count = new HashMap<String, Object>();
		// 获取报表抬头
		List<ApprovalVo> type = dataMapper.selectByName(record);
		map.put("type", type);
		// 获取流程数据
		List<ApprovalVo> jsonData = dataMapper.selectByJson(record);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		// 循环解析json得到每个字段对应的value

		for (int i = 0; i < jsonData.size(); i++) {
			Map<String, Object> map2 = new HashMap<String, Object>();
			Map<String, Object> jsonmap = JSONObject.parseObject(jsonData.get(i).getName());
			String jsondata = jsonmap.get("data").toString();
			List<JsonData> list = JSONArray.parseArray(jsondata, JsonData.class);
			// 过滤掉附件和图片
			for (int y = 0; y < list.size(); y++) {
				if (!list.get(y).getControlName().equals("DDAttachment")
						|| !list.get(y).getControlName().equals("DDPhotoField")) {
					map2.put(list.get(y).getId(), list.get(y).getValue());
				}

			}

			// 将申请日期，填报人塞到map里
			map2.put("draft_date", jsonData.get(i).getDraft_date());
			map2.put("userId", jsonData.get(i).getUserId());
			map2.put("userName", jsonData.get(i).getUserName());
			// 根据关键字和值查询过滤
			if (record.getDrapId() != null && record.getDrapName() != null) {
				if (map2.get(record.getDrapId()).equals(record.getDrapName())) {
					lists.add(map2);
				}
			} else {
				lists.add(map2);
			}
		}

		// 合计行
		Map<String, Object> sumMap = new HashMap<>();
		sumMap.put("userName", "合计:");

		for (int i = 0; i < type.size(); i++) {
			long sum = 0;
			// 获取控件的id
			String id = type.get(i).getRe_name();
			// 判断是不是数字控件
			if (type.get(i).getControlId().equals("NumberField")) {
				for (Map<String, Object> d : lists) {
					String dnum = (String) d.get(id);
					sum += Long.parseLong(StringUtils.isNotEmpty(dnum) ? dnum : "0");
				}
				sumMap.put(id, sum);
			} else {
				sumMap.put(id, "——");
			}

		}
		lists.add(sumMap);
		// // 对结果集进行合计，先将需要合计的表单控件放入数组当中，数组长度就是控件的个数
		// String[] ar = new String[type.size()];
		// for (int i = 0; i < type.size(); i++) {
		// if (type.get(i).getControlId().equals("NumberField")) {
		// ar[i] = type.get(i).getRe_name();
		// }
		// }
		// // 给参数一个初始值,先循环遍历前面组装好的数组，然后在进行遍历结果集，将同一个控件ID的value进行合计计算
		// String parms = "";
		// for (int j = 0; j < ar.length; j++) {
		// int num = 0;
		// for (int i = 0; i < lists.size(); i++) {
		// if (ar[j] != null)
		// num += Integer.parseInt(lists.get(i).get(ar[j]).toString());
		// parms = ar[j];
		// }
		// // 将合计结果放入map中
		// count.put(parms, num);
		// }

		map.put("lists", lists);
		return new JsonResult(true, "查询成功", map);
	}

}
