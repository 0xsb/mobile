package cmcc.mobile.admin.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.TAppCheck;


public class CheckoutUtil {

	public static String LOGOTYPE = "jpg,jpeg,gif,png,bmp,JPG,JPEG,GIF,PNG,BMP";

	public static String getTextFromTHML(String htmlStr) {
		Document doc = Jsoup.parse(htmlStr);
		String text = doc.text();
		StringBuilder builder = new StringBuilder(text);
		int index = 0;
		while (builder.length() > index) {
			char tmp = builder.charAt(index);
			if (Character.isSpaceChar(tmp) || Character.isWhitespace(tmp)) {
				builder.setCharAt(index, ' ');
			}
			index++;
		}
		text = builder.toString().replaceAll(" +", " ").trim();
		return text;
	}

	public static Boolean checkMobile(String mobile) {
		Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
		Matcher m = pattern.matcher(mobile);
		return m.matches();
	}

	public static Boolean checkShortNum(String shortNum) {
		Pattern pattern = Pattern.compile("^[0-9]{3,6}$");
		Matcher m = pattern.matcher(shortNum);
		return m.matches();
	}

	public static Boolean checkEmail(String email) {
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean checkOutType(MultipartHttpServletRequest filter, String type) {
		String fileName = ((MultipartFile) filter).getOriginalFilename();
		String filetype = fileName.substring(fileName.lastIndexOf(".") + 1);
		List<String> types = Arrays.asList(type.split(","));
		if (types.contains(filetype)) {
			return true;
		}
		return false;
	}

	public static boolean checkOutType(MultipartHttpServletRequest[] filters, String type) {
		if (filters != null) {
			for (MultipartHttpServletRequest multipartFile : filters) {
				if (!checkOutType(multipartFile, type)) {
					return false;
				}
			}
		}
		return true;
	}

	public static JsonResult lackParam() {
		return new JsonResult(false, "参数缺失", "");
	}

	public static boolean checkOutParam(Object[] fields) {
		for (Object object : fields) {
			if (object == null) {
				return false;
			} else if (object instanceof String && "".equals(object)) {
				return false;
			}
		}
		return true;
	}

	public static boolean checkOutParam(Object param, String[] paramFields, Object[] fields) {
		for (Object object : fields) {
			if (object == null) {
				return false;
			} else if (object instanceof String && "".equals(object)) {
				return false;
			}
		}
		try {
			Class<? extends Object> class1 = param.getClass();
			for (String string : paramFields) {
				Field field = class1.getDeclaredField(string);
				field.setAccessible(true);
				Object id = field.get(param);
				if (id == null) {
					return false;
				} else if (id instanceof String && "".equals(id)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static Field getField(Class class1, String fieldString) {
		Field field;
		try {
			field = class1.getDeclaredField(fieldString);
		} catch (NoSuchFieldException e) {
			Class class2 = class1.getSuperclass();
			if (class2 != Object.class) {
				return getField(class2, fieldString);
			}
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
		return field;
	}

	public static boolean checkOutParam(Object object, String[] fields) {
		Class<? extends Object> class1 = object.getClass();
		for (String string : fields) {
			Field field = getField(class1, string);
			if (field == null) {
				return false;
			}
			field.setAccessible(true);
			Object id;
			try {
				id = field.get(object);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
			if (id == null) {
				return false;
			} else if (id instanceof String && "".equals(id)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		TAppCheck tAppCheck = new TAppCheck();
		tAppCheck.setProcInstId("1");
		System.out.println(CheckoutUtil.checkOutParam(tAppCheck, new String[] { "procInstId", "assigneeId", "PMId", "nextAssigneeId", "endTime" }));
	}
}
