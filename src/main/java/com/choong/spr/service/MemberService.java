package com.choong.spr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.choong.spr.domain.MemberDto;
import com.choong.spr.mapper.MemberMapper;

@Service
//spring bean으로 만들어주는 annotation
public class MemberService {
	
	@Autowired
	private MemberMapper mapper;
	//mapper에게 의존하는 service
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public boolean addMember(MemberDto member) {
		//평문암호를 암호화(encoding)
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		//암호화된 암호를 다시 세팅
		member.setPassword(encodedPassword);
		
		return mapper.insertMember(member) == 1;
		//하나가 잘 들어가면 1  return
	}

	public boolean hasMemberId(String id) {
		return mapper.countMemberId(id) > 0;
	}

	public boolean hasMemberEmail(String email) {
		return mapper.countMemberEmail(email) > 0;
		//같은 게 있는지 없는지 중복 확인
	}

	public boolean hasMemberNickName(String nickName) {
		return mapper.countMemberNickName(nickName) > 0;
	}

	public List<MemberDto> listMember() {
		return mapper.selectAllMember();
	}

	public MemberDto getMemberById(String id) {
		return mapper.selectMemberById(id);
	}

	public boolean removeMember(MemberDto dto) {
		MemberDto member = mapper.selectMemberById(dto.getId());
		
		String rawPW = dto.getPassword();
		String encodedPW = member.getPassword();
		
		if (passwordEncoder.matches(rawPW, encodedPW)) {
			return mapper.deleteMemberById(dto.getId()) == 1;
		}
		
		return false;
	}

	public boolean modifyMember(MemberDto dto, String oldPassword) {
		//db에서 member읽어서
		MemberDto oldMember = mapper.selectMemberById(dto.getId());
		
		String encodedPW = oldMember.getPassword();
		
		//기존password가 일치할 때만 계속 진행
		if (passwordEncoder.matches(oldPassword, encodedPW)) {
			
			
			//암호 인코딩 (수정된 것도)
			dto.setPassword(passwordEncoder.encode(dto.getPassword()));
			
			return mapper.updateMember(dto) == 1;
		}
		
		return false;
	}


}
