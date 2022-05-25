package com.choong.spr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choong.spr.domain.MemberDto;
import com.choong.spr.mapper.MemberMapper;

@Service
//spring bean으로 만들어주는 annotation
public class MemberService {
	
	@Autowired
	private MemberMapper mapper;
	//mapper에게 의존하는 service

	public boolean addMember(MemberDto member) {
		
		return mapper.insertMember(member) == 1;
		//하나가 잘 들어가면 1  return
	}

	public boolean hasMemberId(String id) {
		return mapper.countMemberId(id) > 0;
	}

	public boolean hasMemberEmail(String email) {
		return mapper.countMemberEmail(email) > 0;
	}

	public boolean hasMembernickName(String nickName) {
		return mapper.countMembernickName(nickName) > 0;
	}


	

}
