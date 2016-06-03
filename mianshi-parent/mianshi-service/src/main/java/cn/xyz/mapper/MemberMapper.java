package cn.xyz.mapper;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.vo.Member;

public interface MemberMapper {

	int insert(Member member);

	int delete(int memberId);

	int update(Member member);

	List<Member> selectByExample(Map<String, Object> parameter);

	Member selectById(int memberId);

}
