package cn.xyz.mianshi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.mapper.CompanyMapper;
import cn.xyz.mapper.MemberMapper;
import cn.xyz.mianshi.example.CompanyExample;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.Member;
import cn.xyz.mianshi.vo.PageVO;

import com.google.common.collect.Maps;
import com.shiku.lbs.service.NearbyManager;

@Service
public class CompanyManagerImpl implements CompanyManager {

	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private NearbyManager nearbyManager;
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private UserManager userManager;

	@Override
	public CompanyVO get(Integer companyId) {
		CompanyVO company = companyMapper.get(companyId);
		if (null == company)
			throw new ServiceException("公司不存在");
		return company;
	}

	@Override
	public int register(CompanyVO company) {
		companyMapper.add(company);

		try {
			nearbyManager.saveCompany(company);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return company.getId();
	}

	@Override
	public List<CompanyVO> selectByExample(CompanyExample example) {
		return companyMapper.selectByExample(example);
	}

	@Override
	public void update(CompanyVO company) {
		companyMapper.update(company);
	}

	@Override
	public void addMember(Member member) {
		memberMapper.insert(member);
	}

	@Override
	public void updateMember(Member member) {
		memberMapper.update(member);
	}

	@Override
	public void deleteMember(int memberId) {
		memberMapper.delete(memberId);
	}

	@Override
	public List<Member> selectMemberByUserId(int userId) {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("userId", userId);
		return memberMapper.selectByExample(parameter);
	}

	@Override
	public List<Member> selectMemberByCompanyId(int companyId) {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("companyId", companyId);
		return memberMapper.selectByExample(parameter);
	}

	@Override
	public Member getMemberById(int memberId) {
		return null;
	}

	@Override
	public Member getMemberByUserId(int userId) {
		return null;
	}

	@Override
	public PageVO getPage(CompanyExample example) {
		List<?> pageData = companyMapper.selectByExample(example);
		long total = companyMapper.countByExample(example);

		PageVO page = new PageVO();
		page.setPageData(pageData);
		page.setPageIndex(example.getPageIndex());
		page.setPageSize(example.getPageSize());
		page.setTotal(total);

		return page;
	}

}