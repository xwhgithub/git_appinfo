package cn.xwh.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.apache.taglibs.standard.lang.jstl.BooleanLiteral;
import org.springframework.stereotype.Service;

import cn.xwh.dao.appinfo.AppInfoMapper;
import cn.xwh.dao.appversion.AppVersionMapper;
import cn.xwh.entity.AppVersion;
@Service("appVersionService")
public class AppVersionServiceImpl implements AppVersionService {
	
	@Resource(name="appVersionMapper")
	private AppVersionMapper appVersionMapper;
	@Resource(name="appInfoMapper")
	private AppInfoMapper appInfoMapper;
	/**
	 * 获得APP版本集合
	 */
	public List<AppVersion> getAppVersionList(Integer pid) throws Exception {
		
		return appVersionMapper.getAppVersionList(pid);
	}

	/**
	 * 新增版本信息
	 */
	public boolean addAppVersion(AppVersion appversion) throws Exception {
		//如果新增成功 则修改app版本信息
		if (appVersionMapper.addAppVersion(appversion)>0) {
			//调用修改app版本信息方法
			if (appInfoMapper.updateVersionId(appversion.getId(), appversion.getAppId())>0) {
				return  true;
			}
		}
		return false;
	}

	/**
	 * 获得版本对象,用于修改版本反显信息
	 * @return
	 * @throws Exception
	 */
	public AppVersion getAppVersionById(Integer id) throws Exception {
		return appVersionMapper.getAppVersionById(id);
	}

	/**
	 * 修改版本文件
	 */
	public boolean delAPKFile(Integer id)throws Exception {
		return appVersionMapper.delAPKFile(id)>0?true:false;
	}

	/**
	 * 保存修改版本信息
	 * @return
	 */
	public boolean appVersionModify(AppVersion appVersion) throws Exception {
		return appVersionMapper.appVersionModify(appVersion)>0?true:false;
	}

	/**
	 * 获得app数量
	 */
	public int getAppVersionCount(Integer aid) {
		
		return 0;
	}

	/**
	 * 删除app版本信息
	 */
	public int delAppVersionByAppId(Integer aid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
