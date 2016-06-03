package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.example.CompanyExample;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.Member;
import cn.xyz.mianshi.vo.PageVO;

public interface CompanyManager {

	CompanyVO get(Integer companyId);

	int register(CompanyVO company);

	List<CompanyVO> selectByExample(CompanyExample example);

	PageVO getPage(CompanyExample example);

	void update(CompanyVO company);

	void addMember(Member member);

	void updateMember(Member member);

	void deleteMember(int memberId);

	List<Member> selectMemberByUserId(int userId);

	List<Member> selectMemberByCompanyId(int companyId);

	Member getMemberById(int memberId);

	Member getMemberByUserId(int userId);

}