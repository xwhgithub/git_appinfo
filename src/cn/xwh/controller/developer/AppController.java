package cn.xwh.controller.developer;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.xwh.entity.AppCategory;
import cn.xwh.entity.AppInfo;
import cn.xwh.entity.DataDictionary;
import cn.xwh.entity.DevUser;
import cn.xwh.service.developer.AppCategoryService;
import cn.xwh.service.developer.AppInfoService;
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
	public String addAppInfoSave(AppInfo appinfo,HttpServletRequest request,HttpSession session,
			@RequestParam(value="attach",required=false) MultipartFile attach) {
		String logoPicPath=null;	//图片路径
		String logoLocPath=null;	//服务器存储路径
		String path = "D:\\SSM\\files";
		// 判断文件是否为空
		if (!attach.isEmpty()) {
			//String path=request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
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
				//logoPicPath=request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoPicPath=fileName;
				logoLocPath=path+File.separator+fileName;
			} else {
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		//设置创建者
		appinfo.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		//设置创建时间
		appinfo.setCreationDate(new Date());
		//设置
		appinfo.setLogoLocPath(logoLocPath);
		appinfo.setLogoPicPath(logoPicPath);
		appinfo.setDevId(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appinfo.setStatus(1);
		try {
			//执行新增
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
	 * @param APKName
	 * @return
	 */
	@RequestMapping(value="/apkexist.json",method=RequestMethod.GET)
	@ResponseBody
	public Object getAPK(@RequestParam String APKName){
		HashMap<String, String> resultMap=new HashMap<String,String>();
		AppInfo appInfo=null;
		if (APKName==null || "".equals(APKName)) {
			resultMap.put("APKName", "empty");
		}else {
			try {
				appInfoService.getAppInfo(APKName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (null!=appInfo) {
				resultMap.put("APKName", "exist");
			}else {
				resultMap.put("APKName", "noexist");
			}
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
