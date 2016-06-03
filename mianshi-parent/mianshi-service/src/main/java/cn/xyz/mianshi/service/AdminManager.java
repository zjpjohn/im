package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.vo.AreaVO;
import cn.xyz.mianshi.vo.OptionVO;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public interface AdminManager {

	List<AreaVO> getAreaList(int parentId);

	List<OptionVO> getDiplomaOptionList();

	List<AreaVO> getProvinceList();

	List<OptionVO> getSalaryOptionList();

	List<OptionVO> getWrokExpOptionList();

	BasicDBObject getConfig();

	void initConfig();

	void setConfig(DBObject dbObj);
}
