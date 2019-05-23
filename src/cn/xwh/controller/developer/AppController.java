package cn.xwh.controller.developer;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.xwh.entity.AppCategory;
import cn.xwh.entity.AppInfo;
import cn.xwh.entity.DataDictionary;
import cn.xwh.service.developer.AppCategoryService;

@Controller
@RequestMapping("/dev/flatform/app")
public class AppController {

	private Logger logger = Logger.getLogger(AppController.class);

	@Resource(name = "appCategoryService")
	private AppCategoryService appCategoryService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String getAppInfoList(Model model, HttpSession session,
			@RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
			@RequestParam(value = "queryStatus", required = false) String _queryStatus,
			@RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
			@RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
			@RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
			@RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {

			
			List<AppInfo> appInfoList=null;	//app集合
			List<DataDictionary> statusList=null;	//状态集合
			List<DataDictionary> flatformList=null;		//平台集合
			List<AppCategory> categoryLevel1List=null;	//一级目录集合
			List<AppCategory> categoryLevel2List=null;	//二级目录集合
			List<AppCategory> categoryLevel3List=null;	//三级目录集合
			
			try {
				categoryLevel1List=appCategoryService.getAppCategoryListByParentId(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//判断二级目录是否为空
			if (_queryCategoryLevel2 !=null && !"".equals(_queryCategoryLevel2)) {
				//调用按父级id查询分类列表方法
				categoryLevel2List=getCategoryList(_queryCategoryLevel1.toString());
				model.addAttribute("categoryLevel2List",categoryLevel2List);
			}
			//判断三级
			if (_queryCategoryLevel3 !=null && !"".equals(_queryCategoryLevel3)) {
				//调用按父级id查询分类列表方法
				categoryLevel3List=getCategoryList(_queryCategoryLevel2.toString());
				model.addAttribute("categoryLevel3List",categoryLevel3List);
			}
			model.addAttribute("categoryLevel1List",categoryLevel1List);
			return "/developer/appinfolist";

	}
	
	/**
	 * 异步加载分类信息
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="categorylevellist.json",method=RequestMethod.GET)
	@ResponseBody
	public List<AppCategory> getAppcategoryList(String pid){
		logger.debug("================异步查询分类"+pid);
		return getCategoryList(pid);
	}
	
	/**
	 * 根据父级id获取分类
	 * @return
	 */
	public List<AppCategory> getCategoryList(String pid){
		List<AppCategory> list=null;
		try {
			list=appCategoryService.getAppCategoryListByParentId(pid==null?null:Integer.parseInt(pid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
