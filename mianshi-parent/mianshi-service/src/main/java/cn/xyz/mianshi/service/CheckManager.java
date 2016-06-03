package cn.xyz.mianshi.service;

import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.User;

public interface CheckManager {

	void saveCheck(CompanyVO company);

	void saveCheck(User user);
	
	
}
