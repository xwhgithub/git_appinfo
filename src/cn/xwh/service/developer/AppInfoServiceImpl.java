package cn.xwh.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.appinfo.AppInfoMapper;
import cn.xwh.entity.AppInfo;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

	@Resource(name = "appInfoMapper")
	private AppInfoMapper appInfoMapper;

	/**
	 * 查询app信息 获得app集合
	 * 
	 * @param softwareName
	 *            app名称
	 * @param status
	 *            app状态
	 * @param flatformId
	 *            平台
	 * @param categoryLevel1
	 *            一级分类
	 * @param categoryLevel2
	 *            二级分类
	 * @param categoryLevel3
	 *            三级分类
	 * @param from
	 *            当前页
	 * @param pageSize
	 *            每页记录数
	 * @return 返回app集合
	 * @throws Exception
	 */
	public List<AppInfo> getAppInfoList(String softwareName, Integer status, Integer flatformId, Integer categoryLevel1,
			Integer categoryLevel2, Integer categoryLevel3, Integer from, Integer pageSize) throws Exception {
		return appInfoMapper.getAppInfoList(softwareName, status, flatformId, categoryLevel1, categoryLevel2,
				categoryLevel3, from, pageSize);
	}

	/**
	 * 获得app个数
	 */
	public int getAppInfoCount(String softwareName, Integer status, Integer flatformId, Integer categoryLevel1,
			Integer categoryLevel2, Integer categoryLevel3) throws Exception {
		return appInfoMapper.getAppInfoCount(softwareName, status, flatformId, categoryLevel1, categoryLevel2,
				categoryLevel3);
	}

	/**
	 * 添加app
	 * 
	 * @param appInfo
	 * @return
	 */
	public boolean add(AppInfo appInfo) {
		boolean flag = false;
		try {
			if (appInfoMapper.add(appInfo) > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 验证APK是否存在
	 * 
	 * @throws Exception
	 */
	public AppInfo getAppInfo(String APKName) throws Exception {
		return appInfoMapper.getAppInfo(APKName);
	}

}
