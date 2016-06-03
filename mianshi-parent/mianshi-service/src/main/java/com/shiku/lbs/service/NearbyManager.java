package com.shiku.lbs.service;

import java.util.List;

import cn.xyz.mianshi.vo.CompanyVO;

import com.shiku.lbs.vo.BasePoi;
import com.shiku.lbs.vo.NearbyJob;
import com.shiku.lbs.vo.NearbyUser;

public interface NearbyManager {

	void saveCompany(CompanyVO company);

	void updateCompany(CompanyVO company);

	List<Integer> getCompanyIdList(BasePoi poi);

	List<CompanyVO> getCompanyList(BasePoi poi);

	
	void saveJob(NearbyJob poi);

	void updateJob(NearbyJob poi);

	List<NearbyJob> getJobList(NearbyJob poi);

	
	void saveUser(NearbyUser poi);

	void updateUser(NearbyUser poi);

	List<NearbyUser> getTalentsList(NearbyUser poi);

	List<NearbyUser> getUserList(NearbyUser poi);
	

	void saveIMUser(NearbyUser poi);

	void updateIMUser(NearbyUser poi);

	List<NearbyUser> getIMUserList(NearbyUser poi);

}
