package com.sustech.regency.util;

import com.github.yulichang.base.MPJBaseMapper;
import com.sustech.regency.db.dao.FileDao;
import com.sustech.regency.db.po.DisPlayable;
import com.sustech.regency.web.handler.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static cn.hutool.core.io.FileUtil.getSuffix;
import static com.sustech.regency.web.util.AssertUtil.asserts;

@Component
public class FileUtil {
	@Value("${file-root-path}")
	private String fileRootPath; //保存文件的根路径
	@Resource
	private FileDao fileDao;
	private static final Set<String> VALID_PICTURE_SUFFIXES=Set.of("jpg","jpeg","png");
	private static final Set<String> VALID_VIDEO_SUFFIXES=Set.of("mp4");

	private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("/yyyy/MM/dd/");

	/**
	 * @return 前端获取文件的URL
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public String uploadFile(MultipartFile file, String uuid){
		String originalFilename = file.getOriginalFilename();
		String suffix = cn.hutool.core.io.FileUtil.getSuffix(originalFilename);

		String newFileName = uuid + "." + suffix;

		Date curTime = new Date();
		String dateDir = DATE_FORMAT.format(curTime);
		@SuppressWarnings("ConstantConditions")
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String baseDir = fileRootPath + dateDir;
		File folder = new File(fileRootPath + dateDir);
		if(!folder.exists()){
			folder.mkdirs();
		}
		try {
			File dist = new File(baseDir + newFileName);
			cn.hutool.core.io.FileUtil.writeBytes(file.getBytes(),dist);
		} catch (IOException e) {
			e.printStackTrace();
			throw ApiException.INTERNAL_SEVER_ERROR;
		}
		fileDao.insert(new com.sustech.regency.db.po.File(uuid,curTime,null,suffix));
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ "/public/file" + dateDir + newFileName;
	}

	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static void checkMediaSuffix(MultipartFile file){
		String originalFilename = file.getOriginalFilename();
		String suffix = getSuffix(originalFilename);
		asserts(VALID_PICTURE_SUFFIXES.contains(suffix) || VALID_VIDEO_SUFFIXES.contains(suffix),
				"文件格式不支持，仅支持"+VALID_PICTURE_SUFFIXES+VALID_VIDEO_SUFFIXES);
	}

	public static void checkPictureSuffix(MultipartFile file){
		String originalFilename = file.getOriginalFilename();
		String suffix = getSuffix(originalFilename);
		asserts(VALID_PICTURE_SUFFIXES.contains(suffix),"图片格式不支持，仅支持"+VALID_PICTURE_SUFFIXES);
	}

	public static String getUrl(com.sustech.regency.db.po.File file){
		@SuppressWarnings("ConstantConditions")
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String dateDir=DATE_FORMAT.format(file.getUploadTime());
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ "/public/file" + dateDir +file.getId()+"."+file.getSuffix();
	}

	/**
	 * 上传展示物的封面
	 * @param picture 要上传的封面
	 * @param displayDao 展示物DAO
	 * @param displayId 展示物id，如：酒店id、房型id...
	 * @return 上传成功后的获取URL
	 */
	public <T extends DisPlayable> String uploadDisplayCover(MultipartFile picture, MPJBaseMapper<T> displayDao, Integer displayId){
		checkPictureSuffix(picture);
		T display = displayDao.selectById(displayId);
		asserts(display!=null,"该id不存在");

		//1.如果有原封面，需要删除
		if(display.getCoverId()!=null){
			fileDao.updateById(com.sustech.regency.db.po.File.builder()
							  .id(display.getCoverId())
							  .deleteTime(new Date())
							  .build());
		}
		//2.上传封面
		String uuid = getUUID();
		String url = uploadFile(picture, uuid);
		//3.更换封面
		display.setCoverId(uuid);
		displayDao.updateById(display);
		return url;
	}

	/**
	 * @param display 展示物
	 * @return 封面URL
	 */
	public <T extends DisPlayable> String getCoverUrl(T display){
		String coverId = display.getCoverId();
		if(coverId==null){return null;}
		return getUrl(fileDao.selectById(coverId));
	}
}
