package cn.xwh.controller.developer;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.jdbc.StringUtils;

import cn.xwh.entity.AppCategory;
import cn.xwh.entity.AppInfo;
import cn.xwh.entity.AppVersion;
import cn.xwh.entity.DataDictionary;
import cn.xwh.entity.DevUser;
import cn.xwh.service.developer.AppCategoryService;
import cn.xwh.service.developer.AppInfoService;
import cn.xwh.service.developer.AppVersionService;
import cn.xwh.service.developer.DataDictionaryService;
import cn.xwh.tools.Constants;
import cn.xwh.tools.PageSupport;

@Controller
@RequestMapping("/dev/flatform/app")
public class AppController {

	private Logger logger = Logger.getLogger(AppController.class);

	@Resource(name = "appCategoryService")
	private AppCategoryService appCategoryService;
	@Resource(name = "dataDictionaryService")
	private DataDictionaryService dataDictionaryService;
	@Resource(name = "appInfoService")
	private AppInfoService appInfoService;
	@Resource(name = "appVersionService")
	private AppVersionService appVersionService;

	@RequestMapping(value = "/list")
	public String getAppInfoList(Model model, HttpSession session,
			@RequestParam(value = "querySoftwareName", required = false) String _querySoftwareName,
			@RequestParam(value = "queryStatus", required = false) String _queryStatus,
			@RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
			@RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
			@RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
			@RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {
		System.out.println("======================进入app控制器");
		logger.debug("=================_querySoftwareName" + _querySoftwareName);
		logger.debug("=================_queryStatus" + _queryStatus);
		logger.debug("=================_queryCategoryLevel1" + _queryCategoryLevel1);
		logger.debug("=================_queryCategoryLevel2" + _queryCategoryLevel2);
		logger.debug("=================_queryCategoryLevel3" + _queryCategoryLevel3);
		logger.debug("=================_queryFlatformId" + _queryFlatformId);
		logger.debug("=================pageIndex" + pageIndex);

		List<AppInfo> appInfoList = null; // app集合
		List<DataDictionary> statusList = null; // 状态集合
		List<DataDictionary> flatformList = null; // 平台集合
		List<AppCategory> categoryLevel1List = null; // 一级目录集合
		List<AppCategory> categoryLevel2List = null; // 二级目录集合
		List<AppCategory> categoryLevel3List = null; // 三级目录集合

		Integer queryStatus = null; // 状态id
		Integer queryFlatformId = null; // 平台id
		Integer queryCategoryLevel1 = null; // 一级
		Integer queryCategoryLevel2 = null; // 二级
		Integer queryCategoryLevel3 = null; // 三级

		int pageSize = Constants.pageSize; // 每页记录数
		Integer pageNo = 1; // 当前页面
		if (pageIndex != null && !"".equals(pageIndex)) {
			pageNo = Integer.parseInt(pageIndex);
		}
		// 判断非空
		if (_queryStatus != null && !"".equals(_queryStatus)) {
			queryStatus = Integer.parseInt(_queryStatus);
		}
		if (_queryFlatformId != null && !"".equals(_queryFlatformId)) {
			queryFlatformId = Integer.parseInt(_queryFlatformId);
		}
		if (_queryCategoryLevel1 != null && !"".equals(_queryCategoryLevel1)) {
			queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
		}

		if (_queryCategoryLevel2 != null && !"".equals(_queryCategoryLevel2)) {
			queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
		}
		if (_queryCategoryLevel3 != null && !"".equals(_queryCategoryLevel3)) {
			queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
		}
		// 获得总记录数
		int appCount = 0;
		try {
			appCount = appInfoService.getAppInfoCount(_querySoftwareName, queryStatus, queryFlatformId,
					queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3);
			categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null); // 获得一级分类
			statusList = this.getDataDictionaryList("APP_STATUS"); // 获得状态信息
			flatformList = this.getDataDictionaryList("APP_FLATFORM"); // 获得平台
			// 获得app信息集合
			appInfoList = appInfoService.getAppInfoList(_querySoftwareName, queryStatus, queryFlatformId,
					queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, (pageNo - 1) * pageSize, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PageSupport pages = new PageSupport();
		pages.setPageSize(pageSize);
		pages.setTotalCount(appCount);
		pages.setCurrentPageNo(pageNo);
		int totalPageCount = pages.getTotalPageCount(); // 获得总页数
		// 判断二级目录是否为空
		if (_queryCategoryLevel2 != null && !"".equals(_queryCategoryLevel2)) {
			// 调用按父级id查询分类列表方法
			categoryLevel2List = getCategoryList(_queryCategoryLevel1.toString());
			model.addAttribute("categoryLevel2List", categoryLevel2List);
		}
		// 判断三级
		if (_queryCategoryLevel3 != null && !"".equals(_queryCategoryLevel3)) {
			// 调用按父级id查询分类列表方法
			categoryLevel3List = getCategoryList(_queryCategoryLevel2.toString());
			model.addAttribute("categoryLevel3List", categoryLevel3List);
		}
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("statusList", statusList);
		model.addAttribute("flatFormList", flatformList);
		model.addAttribute("appInfoList", appInfoList);
		model.addAttribute("pages", pages);

		model.addAttribute("querySoftwareName", _querySoftwareName);
		model.addAttribute("queryStatus", queryStatus);
		model.addAttribute("queryFlatformId", queryFlatformId);
		model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
		model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
		model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
		return "/developer/appinfolist";
	}

	/**
	 * 异步加载分类信息
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/categorylevellist.json", method = RequestMethod.GET)
	@ResponseBody
	public List<AppCategory> getAppcategoryList(@RequestParam String pid) {
		logger.debug("================异步查询分类" + pid);
		return this.getCategoryList(pid);
	}

	@RequestMapping(value = "/datadictionarylist.json", method = RequestMethod.GET)
	@ResponseBody
	public Object getDataDicList(@RequestParam String tcode) {
		logger.debug("================获取平台信息");
		return this.getDataDictionaryList(tcode);
	}

	@RequestMapping(value = "/appinfoadd", method = RequestMethod.GET)
	public String getAddAppInfp() {
		logger.debug("================获得新增app信息页面");
		return "/developer/appinfoadd";
	}

	@RequestMapping(value = "/appinfoaddsave", method = RequestMethod.POST)
	public String addAppInfoSave(AppInfo appinfo, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "attach", required = false) MultipartFile attach) {
		String logoPicPath = null; // 图片路径
		String logoLocPath = null; // 服务器存储路径
		String path = "D:\\SSM\\files";
		// 判断文件是否为空
		if (!attach.isEmpty()) {
			// String
			// path=request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename(); // 获得源文件名
			logger.debug("==================源文件名:" + oldFileName);
			String prefix = FilenameUtils.getExtension(oldFileName); // 获得源文件后缀
			logger.debug("===================源文件后缀" + prefix);
			int filesize = 500000;
			logger.debug("===============文件大小:" + attach.getSize());
			if (attach.getSize() > filesize) {// 上传文件不能超过规定大小 500kb
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
			} else if (prefix.equalsIgnoreCase("png") || prefix.equalsIgnoreCase("jpg")
					|| prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")) {
				// 设置文件名,保证不重复
				String fileName = System.currentTimeMillis() + RandomUtils.nextInt(1000000) + "_Personal.jpg";
				logger.debug("new fileName========文件名:" + fileName);
				File targetFile = new File(path, fileName);
				// 判断文件是否存在,如果不存在则创建
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				// 保存
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				}
				// logoPicPath=request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoPicPath = fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		// 设置创建者
		appinfo.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		// 设置创建时间
		appinfo.setCreationDate(new Date());
		// 设置
		appinfo.setLogoLocPath(logoLocPath);
		appinfo.setLogoPicPath(logoPicPath);
		appinfo.setDevId(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appinfo.setStatus(1);
		try {
			// 执行新增
			if (appInfoService.add(appinfo)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appinfoadd";
	}

	/**
	 * 验证APK是否存在
	 * 
	 * @param APKName
	 * @return
	 */
	@RequestMapping(value = "/apkexist.json", method = RequestMethod.GET)
	@ResponseBody
	public Object getAPK(@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "APKName", required = false) String APKName) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		AppInfo appInfo = null;
		if (APKName == null || "".equals(APKName)) {
			resultMap.put("APKName", "empty");
		} else {
			try {
				appInfo = appInfoService.getAppInfo(id, APKName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (null != appInfo) {
				System.out.println("====存在");
				resultMap.put("APKName", "exist");
			} else {
				System.out.println("====不存在");
				resultMap.put("APKName", "noexist");
			}
		}
		return resultMap;
	}

	/**
	 * 修改反显信息
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/appinfomodify", method = RequestMethod.GET)
	public String getApp(@RequestParam Integer id, Model model) {
		logger.debug("==============获得修改页面");
		AppInfo appInfo = null;
		try {
			appInfo = appInfoService.getAppInfo(id, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("appInfo", appInfo);
		return "/developer/appinfomodify";
	}

	/**
	 * 删除applog图片,apk文件
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delfile.json", method = RequestMethod.GET)
	@ResponseBody
	public Object delfile(@RequestParam Integer id, @RequestParam(value = "flag", required = false) String flag) {
		logger.debug("================进入删除log图片");
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String path = "";
		System.out.println("===============falg"+flag);
		if (flag == null || "".equals(flag)) {
			resultMap.put("result", "failed");
		} else if (flag.equals("logo")) { // 删除app的logo文件
			try {
				path = appInfoService.getAppInfo(id, null).getLogoLocPath();
				File file = new File(path);

				if (file.exists()) {
					if (file.delete()) {
						if (appInfoService.delLogFile(id)) {
							resultMap.put("result", "success");
						} else {
							resultMap.put("result", "failed");
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (flag.equals("apk")) { // 删除app的版本文件
			try {
				path = appVersionService.getAppVersionById(id).getApkLocPath();// 下载路径
				File file = new File(path);
				if (file.exists()) {
					if (file.delete()) {
						if (appVersionService.delAPKFile(id)) {
							resultMap.put("result", "success");
						} else {
							resultMap.put("result", "failed");
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	/**
	 * 修改app
	 * 
	 * @param appinfo
	 * @param request
	 * @param session
	 * @param attach
	 * @return
	 */
	@RequestMapping(value = "/appinfomodifysave", method = RequestMethod.POST)
	public String appinfomodifysave(AppInfo appinfo, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "attach", required = false) MultipartFile attach) {
		logger.debug("======================保存修改");
		String logoPicPath = null; // 图片路径
		String logoLocPath = null; // 服务器存储路径
		String path = "D:\\SSM\\files";
		// 判断文件是否为空
		if (!attach.isEmpty()) {
			// String
			// path=request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename(); // 获得源文件名
			logger.debug("==================源文件名:" + oldFileName);
			String prefix = FilenameUtils.getExtension(oldFileName); // 获得源文件后缀
			logger.debug("===================源文件后缀" + prefix);
			int filesize = 500000;
			logger.debug("===============文件大小:" + attach.getSize());
			if (attach.getSize() > filesize) {// 上传文件不能超过规定大小 500kb
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
				logger.debug("===================图片过大");
				return "redirect:/dev/flatform/app/appinfomodify?id=" + appinfo.getId();
			} else if (prefix.equalsIgnoreCase("png") || prefix.equalsIgnoreCase("jpg")
					|| prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")) {
				// 设置文件名,保证不重复
				String fileName = System.currentTimeMillis() + RandomUtils.nextInt(1000000) + "_Personal.jpg";
				logger.debug("new fileName========文件名:" + fileName);
				File targetFile = new File(path, fileName);
				// 判断文件是否存在,如果不存在则创建
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				// 保存
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "redirect:/dev/flatform/app/appinfomodify?id=" + appinfo.getId();
				}
				// logoPicPath=request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoPicPath = fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				logger.debug("======================后缀");
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "redirect:/dev/flatform/app/appinfomodify?id=" + appinfo.getId();
			}
		}
		// 设置修改
		appinfo.setModifyBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		// 设置修改时间
		appinfo.setModifyDate(new Date());
		// 设置图片路径
		appinfo.setLogoLocPath(logoLocPath);
		appinfo.setLogoPicPath(logoPicPath);
		appinfo.setUpdateDate(new Date());
		try {
			// 执行修改
			if (appInfoService.appModifySave(appinfo)) {
				logger.debug("=======================成功");
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("=======================失败");
		return "redirect:/dev/flatform/app/appinfomodify?id=" + appinfo.getId();
	}

	/**
	 * 获得新增版本页面
	 * 
	 * @param id
	 * @param fileUploadError
	 * @param appVersion
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/appversionadd", method = RequestMethod.GET)
	public String addVersion(@RequestParam String id,
			@RequestParam(value = "error", required = false) String fileUploadError, AppVersion appVersion,
			Model model) {
		List<AppVersion> versionList = null;
		// 判断提示消息
		if (fileUploadError != null && fileUploadError.equals("error1")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		} else if (fileUploadError != null && fileUploadError.equals("error2")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		} else if (fileUploadError != null && fileUploadError.equals("error3")) {
			System.out.println("格式不正确");
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		// 设置appid
		appVersion.setAppId(Integer.parseInt(id));
		try {
			// 查询版本集合
			versionList = appVersionService.getAppVersionList(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("appVersionList", versionList);
		model.addAttribute("appVersion", appVersion);
		model.addAttribute("fileUploadError", fileUploadError);
		return "/developer/appversionadd";
	}

	/**
	 * 保存新增信息
	 * 
	 * @param appVersion
	 *            版本对象
	 * @param session
	 * @param request
	 * @param attach
	 *            文件对象
	 * @return
	 */
	@RequestMapping(value = "addversionsave", method = RequestMethod.POST)
	public String addVersionSave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "a_downloadLink") MultipartFile attach) {
		String path = "D:\\SSM\\files";
		String apkFileName = null; // apk名字
		String downloadLink = null; // 下载路径
		String apkLocPath = null; // 存放路径
		// 判断文件是否为空
		if (!attach.isEmpty()) {
			String oldFileName = attach.getOriginalFilename(); // 获得源文件名
			logger.debug("==================源文件名:" + oldFileName);
			String prefix = FilenameUtils.getExtension(oldFileName); // 获得源文件后缀
			logger.debug("===================源文件后缀" + prefix);
			int filesize = 500000;
			logger.debug("===============文件大小:" + attach.getSize());
			if (prefix.equals("apk")) {
				// 获得app的apk
				String APKName = null;
				try {
					APKName = (appInfoService.getAppInfo(appVersion.getAppId(), null)).getAPKName();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.debug("new fileName========文件名:" + APKName);

				// 设置apk名字
				apkFileName = APKName + appVersion.getVersionNo() + ".apk";
				// app的apk信息不能为空
				if (APKName == null || "".equals(APKName)) {
					return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId() + "&error=error1";
				}
				File targetFile = new File(path, apkFileName);
				// 判断文件是否存在,如果不存在则创建
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				// 保存
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					// 异常
					return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId() + "&error=error2";
				}
				// 下载路径
				downloadLink = request.getContextPath() + apkFileName;
				// 存放路径
				apkLocPath = path + File.separator + apkFileName;
			} else {
				logger.debug("======================后缀不符合");
				return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId() + "&error=error3";
			}
		} else {
			;
		}
		// 创建者
		appVersion.setCreatedBy(((DevUser) session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setCreationDate(new Date());// 创建时间
		appVersion.setApkFileName(apkFileName);// 上传的apk名称
		appVersion.setDownloadLink(downloadLink);// 下载路径
		appVersion.setApkLocPath(apkLocPath);// 存放路径
		try {
			if (appVersionService.addAppVersion(appVersion)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/dev/flatform/app/appversionadd?id=" + appVersion.getAppId();
	}

	/***
	 * 获得修改本版本页面
	 * 
	 * @param vid
	 *            版本id
	 * @param aid
	 *            appid
	 * @param fileUploadError
	 *            异常信息
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/appversionmodify", method = RequestMethod.GET)
	public String appVersionModify(@RequestParam(value = "vid", required = false) Integer vid,
			@RequestParam(value = "aid", required = false) Integer aid,
			@RequestParam(value = "error", required = false) String fileUploadError, Model model) {
		// 判断提示消息
		if (fileUploadError != null && fileUploadError.equals("error1")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_1; // apk不完成
		} else if (fileUploadError != null && fileUploadError.equals("error2")) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2; // 上传失败
		} else if (fileUploadError != null && fileUploadError.equals("error3")) {
			System.out.println("格式不正确");
			fileUploadError = Constants.FILEUPLOAD_ERROR_3; // 格式不正确
		}

		AppVersion appVersion = null;
		List<AppVersion> appVersionList = null;
		try {
			appVersion = appVersionService.getAppVersionById(vid); // 获得版本对象
			appVersionList = appVersionService.getAppVersionList(aid); // 获得该app的版本集合
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("appVersion", appVersion);
		model.addAttribute("appVersionList", appVersionList);
		return "/developer/appversionmodify";
	}

	/**
	 * 保存修改版本
	 * 
	 * @param appVersion
	 * @param session
	 * @param request
	 * @param attach
	 * @return
	 */
	@RequestMapping(value = "/appversionmodifysave", method = RequestMethod.POST)
	public String appVersionModifySave(AppVersion appVersion, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "attach") MultipartFile attach) {
		String path = "D:\\SSM\\files";
		String apkFileName = null; // apk名字
		String downloadLink = null; // 下载路径
		String apkLocPath = null; // 存放路径
		// 判断文件是否为空
		if (!attach.isEmpty()) {
			String oldFileName = attach.getOriginalFilename(); // 获得源文件名
			logger.debug("==================源文件名:" + oldFileName);
			String prefix = FilenameUtils.getExtension(oldFileName); // 获得源文件后缀
			logger.debug("===================源文件后缀" + prefix);
			// int filesize = 500000;
			logger.debug("===============文件大小:" + attach.getSize());
			if (prefix.equals("apk")) {
				// 获得app的apk
				String APKName = null;
				try {
					// 获得app的apk
					APKName = (appInfoService.getAppInfo(appVersion.getAppId(), null)).getAPKName();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.debug("new fileName========文件名:" + APKName);

				// 设置apk名字 apk+版本号
				apkFileName = APKName + appVersion.getVersionNo() + ".apk";
				// app的apk信息不能为空
				if (APKName == null || "".equals(APKName)) {
					return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId() + "&aid="
							+ appVersion.getAppId() + "&error=error1";
				}
				File targetFile = new File(path, apkFileName);
				// 判断文件是否存在,如果不存在则创建
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				// 保存
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					// 异常
					return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId() + "&aid="
							+ appVersion.getAppId() + "&error=error1";
				}
				// 下载路径
				downloadLink = request.getContextPath() + apkFileName;
				// 存放路径
				apkLocPath = path + File.separator + apkFileName;
			} else {
				logger.debug("======================后缀不符合");
				return "redirect:/dev/flatform/app/appversionmodify?vid=" + appVersion.getId() + "&aid="
						+ appVersion.getAppId() + "&error=error1";
			}
		} else {
			;
		}
		
		appVersion.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());	//修改者
		appVersion.setModifyDate(new Date());	//修改时间
		appVersion.setDownloadLink(downloadLink);	//下载路径
		appVersion.setApkLocPath(apkLocPath);	//存放路径
		appVersion.setApkFileName(apkFileName);	//apk名字
		try {
			if (appVersionService.appVersionModify(appVersion)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/developer/appversionmodify";

	}
	
	/**
	 * 查看app信息
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appview",method=RequestMethod.GET)
	public String viewApp(@RequestParam String id,Model model){
		AppInfo appInfo=null;
		List<AppVersion> verList=null;
		try {
			appInfo=appInfoService.getAppInfo(Integer.parseInt(id), null);
			verList=appVersionService.getAppVersionList(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("appInfo",appInfo);
		model.addAttribute("appVersionList",verList);
		return "developer/appinfoview";
	}
	
	/**
	 * 删除app
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delapp.json",method=RequestMethod.GET)
	@ResponseBody
	public Object delApp(@RequestParam String id){
		HashMap< String, String> resultMap=new HashMap<String,String>();
		//如果id==null 则表示不存在此app
		if (StringUtils.isNullOrEmpty(id)) {
			resultMap.put("delResult", "notexist");
		}else {
			try {
				if (appInfoService.delAppById(Integer.parseInt(id))) {
					resultMap.put("delResult", "true");
				}else {
					resultMap.put("delResult", "false");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 上架,下架
	 * @param appId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sale",method=RequestMethod.GET)
	@ResponseBody
	public Object sale(@RequestParam("appid") String appid,HttpSession session){
		System.out.println("=========执行上架,下架");
		HashMap<String, String> resultMap=new  HashMap<String,String>();
		Integer aId=0;
		try {
			aId=Integer.parseInt(appid);
		} catch (Exception e) {
			aId=0;
		}
		resultMap.put("errorCode", "0");
		resultMap.put("appId", appid);
		if (aId>0) {
			AppInfo appInfo=new AppInfo();
			appInfo.setId(aId);	//修改的id
			appInfo.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());	//修改者
			try {
				if (appInfoService.appsysUpdateSaleStatusByAppId(appInfo)) {
					resultMap.put("resultMsg", "success");
				}else {
					resultMap.put("resultMsg", "failed");
				}
			} catch (Exception e) {
				resultMap.put("errorCode", "exception000001");
			}
		}else {
			resultMap.put("errorCode", "param000001");
		}
		return resultMap;
	}
	/**
	 * 获得平台信息
	 * 
	 * @param typeCode
	 * @return
	 */
	public List<DataDictionary> getDataDictionaryList(String typeCode) {
		List<DataDictionary> list = null;
		try {
			list = dataDictionaryService.getDataDictionaryList(typeCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据父级id获取分类
	 * 
	 * @return
	 */
	public List<AppCategory> getCategoryList(String pid) {
		List<AppCategory> list = null;
		try {
			list = appCategoryService
					.getAppCategoryListByParentId(pid == null || pid == "" ? null : Integer.parseInt(pid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
