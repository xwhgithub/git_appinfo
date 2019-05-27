package cn.xwh.service.developer;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.appinfo.AppInfoMapper;
import cn.xwh.dao.appversion.AppVersionMapper;
import cn.xwh.entity.AppInfo;
import cn.xwh.entity.AppVersion;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

	@Resource(name = "appInfoMapper")
	private AppInfoMapper appInfoMapper;

	@Resource(name = "appVersionMapper")
	private AppVersionMapper appVersionMapper;

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
	public AppInfo getAppInfo(Integer id, String APKName) throws Exception {
		return appInfoMapper.getAppInfo(id, APKName);
	}

	/***
	 * 删除log图片
	 */
	public boolean delLogFile(Integer id) {
		boolean flag = false;
		try {
			if (appInfoMapper.delLogFile(id) > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 修改保存
	 */
	public boolean appModifySave(AppInfo appInfo) throws Exception {
		boolean flag = false;
		try {
			if (appInfoMapper.appModifySave(appInfo) > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除app信息
	 */
	public boolean delAppById(Integer id) throws Exception {
		boolean flag = false; // 记录是否删除成功
		// 获得该app的版本数量
		int appVerCount = appVersionMapper.getAppVersionCount(id);
		List<AppVersion> appVerList = null;
		// 如果大于0,则表示存在版本 先删除版本文件
		if (appVerCount > 0) {
			// 获得版本集合
			appVerList = appVersionMapper.getAppVersionList(id);
			// 循环删除版本文件
			for (AppVersion appVersion : appVerList) {
				if (appVersion.getApkLocPath() != null && !"".equals(appVersion.getApkLocPath())) {
					File file = new File(appVersion.getApkLocPath());
					if (file.exists()) {
						if (file.delete()) {
							flag = true;
						} else {
							throw new Exception();
						}
					}
				}
			}

			// 删除app版本
			appVersionMapper.delAppVersionByAppId(id);
		}

		// 获得app对象
		AppInfo appInfo = appInfoMapper.getAppInfo(id, null);
		// 判断是否有logo图片,如果有则先删除图片
		if (appInfo.getLogoLocPath() != null && !"".equals(appInfo.getLogoLocPath())) {
			File file = new File(appInfo.getLogoLocPath());
			if (file.exists()) {
				if (file.delete()) {
					flag = true;
				} else {
					throw new Exception();
				}
			}
		}
		// 删除app
		if (appInfoMapper.delAppById(id) > 0) {
			flag = true;
		}
		return flag;
	}

	/***
	 * app上架,下架
	 */
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo) throws Exception {
		Integer modifyId = appInfo.getModifyBy(); // 获得修改者id
		if (modifyId < 0 || appInfo.getId() < 0) {
			throw new Exception();
		}

		AppInfo appInfo2 = appInfoMapper.getAppInfo(appInfo.getId(), null); // 获得该app对象
		if (appInfo2 == null) {
			return false;
		} else {
			switch (appInfo2.getStatus()) {
				//状态为审核通过,可以执行上架操作
			case 2:
				System.out.println("上架");
				onSale(appInfo2, modifyId, 4, 2);	//上架
				break;
				//状态为下架,则可以执行上架操作
			case 5:
				System.out.println("下____上架");
				onSale(appInfo2, modifyId, 4, 2);	//上架
				break;
				//状态为上架,则可以执行下架操作
			case 4:
				offSale(appInfo2, modifyId, 5);	//下架方法
				break;
			default:
				return true;
			}
		}
		return true;
	}

	/**
	 * 上架方法
	 * 
	 * @param appInfo
	 * @param operator
	 * @param appInfStatus
	 * @param versionStatus
	 * @throws Exception
	 */
	private void onSale(AppInfo appInfo, Integer operator, Integer appInfStatus, Integer versionStatus)
			throws Exception {
		offSale(appInfo, operator, appInfStatus); // 调用上架方法,参数为app对象,修改者,状态id
		setSaleSwitchToAppVersion(appInfo, operator, versionStatus); // 修改版本状态方法
	}

	/**
	 * 下架方法
	 * 
	 * @param appInfo
	 * @param operator
	 * @param appInfStatus
	 * @return
	 * @throws Exception
	 */
	private boolean offSale(AppInfo appInfo, Integer operator, Integer appInfStatus) throws Exception {
		AppInfo _appInfo = new AppInfo();
		_appInfo.setId(appInfo.getId()); // 要下架的app的id
		_appInfo.setStatus(appInfStatus); // 修改app的状态
		_appInfo.setModifyBy(operator); // 修改者
		_appInfo.setOffSaleDate(new Date(System.currentTimeMillis())); // 下架时间
		System.out.println(_appInfo.getStatusName());
		appInfoMapper.appModifySave(_appInfo);
		return true;
	}

	/**
	 * 修改版本状态方法
	 * 
	 * @param appInfo
	 * @param operator
	 * @param saleStatus
	 * @return
	 * @throws Exception
	 */
	private boolean setSaleSwitchToAppVersion(AppInfo appInfo, Integer operator, Integer saleStatus) throws Exception {
		AppVersion appVersion = new AppVersion();
		appVersion.setId(appInfo.getVersionId()); // 要修改的版本id
		appVersion.setPublishStatus(saleStatus); // 修改的状态id
		appVersion.setModifyBy(operator); // 修改者
		appVersion.setModifyDate(new Date(System.currentTimeMillis())); // 修改时间
		appVersionMapper.appVersionModify(appVersion);
		return false;
	}

	/*	*//**
			 * 修改版本id
			 *//*
			 * public int updateVersionId(Integer versionId, Integer appId)
			 * throws Exception { // TODO Auto-generated method stub return 0; }
			 */

}
