package cn.xwh.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.datadictionary.DataDictionaryMapper;
import cn.xwh.entity.DataDictionary;

@Service("dataDictionaryService")
public class DataDictionaryServiceImpl implements DataDictionaryService {
	
	@Resource(name="dataDictionaryMapper")
	private DataDictionaryMapper dataDictionaryMapper;
	
	/**
	 * 根据状态编码获取状态集合
	 */
	public List<DataDictionary> getDataDictionaryList(String typeCode) throws Exception {
		try {
			return dataDictionaryMapper.getDataDictionaryList(typeCode);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
}
