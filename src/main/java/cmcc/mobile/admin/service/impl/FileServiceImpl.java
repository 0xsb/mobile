package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cmcc.mobile.admin.dao.FileMapper;
import cmcc.mobile.admin.entity.File;
import cmcc.mobile.admin.service.FileService;
import cmcc.mobile.admin.util.Base64;
import cmcc.mobile.admin.util.HttpClientUtil;
import cmcc.mobile.admin.util.ImageUtil;
import cmcc.mobile.admin.util.PropertiesUtil;
import net.mikesu.fastdfs.FastdfsClient;
import net.mikesu.fastdfs.FastdfsClientFactory;

/**
 * 文件上传下载
 * 
 * @author renlinggao
 * @Date 2016年5月30日
 */
@Service
public class FileServiceImpl implements FileService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	FileMapper fileMapper;

	@Autowired
	private FastdfsClientFactory fastdfsFactory;

	private static int IMAGE_WIDTH = 128; // 压缩的像素大小
	private static int IMAGE_HEIGHT = 128; // 压缩的像素大小

	private static float SCALE = 0.8f; // 压缩的比例
	// private static float QUALITY = 0.8f; //压缩的图片品质

	@Override
	public File fileUpload(MultipartHttpServletRequest mr, HttpServletRequest request, String num, String userId,
			String companyId) {
		String fileUPloadPath = PropertiesUtil.getAppByKey("FILE_UPLOAD_PATH");// 文件上传保存的地址
		java.io.File file = null;// 文件实体
		String fileName = null;// 上传的文件名
		String extension = null;// 文件后缀
		String fileid = null;
		java.io.File thumFile = null;
		File hyFile = null;

		FastdfsClient fastdfsClient = fastdfsFactory.getFastdfsClient();
		try {
			for (Iterator<MultipartFile> ite = mr.getFileMap().values().iterator(); ite.hasNext();) {
				MultipartFile multipartFile = ite.next();
				if (multipartFile.getSize() > 0) {
					// 获取文件名
					fileName = multipartFile.getOriginalFilename();
					// 获取文件后缀名
					extension = FilenameUtils.getExtension(fileName);
					// 获取文件名称去除后缀
					// String name = FilenameUtils.getBaseName(fileName);
					// 保存文本地文件
					file = createFile(fileUPloadPath, extension);
					multipartFile.transferTo(file);
					Map<String, String> meta = new HashMap<String, String>();
					meta.put("companyId", companyId);
					meta.put("fileName", fileName);
					meta.put("userId", userId);
					meta.put("num", num);
					fileid = fastdfsClient.upload(file, extension, meta);
					// 压缩图片
					ImageUtil.imageCompress(fileUPloadPath + java.io.File.separator, file.getName(),
							"thum_" + file.getName(), SCALE, IMAGE_WIDTH, IMAGE_HEIGHT);
					thumFile = new java.io.File(fileUPloadPath + java.io.File.separator + "thum_" + file.getName());
					// 上传缩略图
					String thumFileId = fastdfsClient.uploadSlave(thumFile, fileid, "128X128", extension);
					logger.debug(thumFileId);
					// 保存文件信息到数据库
					hyFile = new File();
					hyFile.setAddr(Base64.getBase64(fileid));
					hyFile.setCompanyId(companyId);
					hyFile.setCreatetime(new Date());
					hyFile.setUserId(userId);
					hyFile.setName(fileName);
					hyFile.setSize(file.length() + "");
					hyFile.setNum(num);
					fileMapper.insertSelective(hyFile);

					logger.debug("文件：" + multipartFile.getOriginalFilename() + "保存到：" + file.getPath());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("文件保存失败");
		} finally {
			// 如果文件保存到了本地 删除文件
			if (file != null && file.exists()) {
				file.delete();
			}
			if (thumFile.exists()) {
				thumFile.delete();
			}
		}

		return hyFile;
	}

	@Override
	public java.io.File download(HttpServletResponse response, String name) throws Exception {
		String url = "";
		String urlPrefix = PropertiesUtil.getAppByKey("FASTDFS_URL");
		String fileTempParent = PropertiesUtil.getAppByKey("FILE_UPLOAD_PATH");
		String filePath = fileTempParent + java.io.File.separator + UUID.randomUUID().toString();
		if (name.matches("^[thum_].+$")) {
			name = name.substring(name.indexOf("_") + 1);
			String fileId = Base64.getFromBase64(name);
			url = urlPrefix + fileId.substring(0, fileId.lastIndexOf(".")) + "128X128"
					+ fileId.substring(fileId.lastIndexOf("."));
		} else {
			url = urlPrefix + Base64.getFromBase64(name);
		}
		java.io.File file = HttpClientUtil.downFile(url, filePath);
		if (file.exists()) {// 如果文件存在
			return file;
		}
		return null;
	}

	@Override
	public List<File> multipleFilesUpload(HttpServletRequest request, HttpServletResponse response, String num,
			String userId, String companyId) throws Exception {
		List<File> list = new ArrayList<File>();
		FastdfsClient fastdfsClient = fastdfsFactory.getFastdfsClient();
		String fileUPloadPath = PropertiesUtil.getAppByKey("FILE_UPLOAD_PATH");// 文件上传保存的地址
		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<List<MultipartFile>> iter = multiRequest.getMultiFileMap().values().iterator();
			while (iter.hasNext()) {
				// 取得上传文件
				List<MultipartFile> files = iter.next();
				if (files != null) {
					for (MultipartFile file : files) {
						// 取得当前上传文件的文件名称
						String myFileName = file.getOriginalFilename();
						// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
						if (myFileName.trim() != "") {
							// 获取文件名
							String fileName = file.getOriginalFilename();
							// 获取文件后缀名
							String extension = FilenameUtils.getExtension(fileName);
							// 定义上传路径
							java.io.File localFile = createFile(fileUPloadPath, extension);
							file.transferTo(localFile);
							// 上传给fastdfs
							Map<String, String> meta = new HashMap<String, String>();
							meta.put("companyId", companyId);
							meta.put("fileName", fileName);
							meta.put("userId", userId);
							meta.put("num", num);
							String fileid = fastdfsClient.upload(localFile, extension, meta);
							// 保存文件信息到数据库
							File hyFile = new File();
							hyFile.setAddr(Base64.getBase64(fileid));
							hyFile.setCompanyId(companyId);
							hyFile.setCreatetime(new Date());
							hyFile.setUserId(userId);
							hyFile.setName(fileName);
							hyFile.setSize(localFile.length() + "");
							hyFile.setNum(num);
							fileMapper.insertSelective(hyFile);
							// 添加进返回集合
							list.add(hyFile);
							// 删除文件
							if (localFile != null && localFile.exists()) {
								localFile.delete();
							}
						}
					}
				}
			}

		}
		return list;
	}

	public java.io.File createFile(String fileUploadPath, String extension) {
		String filePath = fileUploadPath + java.io.File.separator + UUID.randomUUID().toString() + "." + extension;
		java.io.File file = new java.io.File(filePath);
		if (file.exists()) {
			createFile(fileUploadPath, extension);
		}
		return file;
	}

	@Override
	public File getById(Integer id) {
		return fileMapper.selectByPrimaryKey(id);
	}

	@Override
	public void delteFile(Integer id, String addr) {
		File file = fileMapper.selectByPrimaryKey(id);
		if (file == null)
			throw new RuntimeException("文件不存在");
		FastdfsClient fastdfsClient = fastdfsFactory.getFastdfsClient();
		String fileId = Base64.getFromBase64(file.getAddr());
		try {
			fastdfsClient.delete(fileId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException("");
		}
		file.setFlag(0);
		fileMapper.updateByPrimaryKeySelective(file);
	}
}
