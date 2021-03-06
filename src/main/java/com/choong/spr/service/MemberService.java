package com.choong.spr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choong.spr.domain.BoardDto;
import com.choong.spr.domain.MemberDto;
import com.choong.spr.mapper.BoardMapper;
import com.choong.spr.mapper.MemberMapper;
import com.choong.spr.mapper.ReplyMapper;

@Service
//spring bean으로 만들어주는 annotation
public class MemberService {
	
	@Autowired
	private MemberMapper mapper;
	//mapper에게 의존하는 service
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Autowired
	private BoardMapper boardMapper;
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public boolean addMember(MemberDto member) {
		
		//평문암호를 암호화(encoding)
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		
		//암호화된 암호를 다시 세팅
		member.setPassword(encodedPassword);
	
		//insert member
		int cnt1 = mapper.insertMember(member);
		
	
		//insert auth
		int cnt2 = mapper.insertAuth(member.getId(), "ROLE_USER");
		
		return cnt1 == 1 && cnt2 == 1;
		
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

	@Transactional
	public boolean removeMember(MemberDto dto) {
		MemberDto member = mapper.selectMemberById(dto.getId());
		
		String rawPW = dto.getPassword();
		String encodedPW = member.getPassword();
		
		if (passwordEncoder.matches(rawPW, encodedPW)) {
			//댓글 삭제
			replyMapper.deleteByMemberId(dto.getId());
			
			//이 멤버가 쓴 게시글 삭제
			List<BoardDto> boardList = boardMapper.listByMemberId(dto.getId());
			for (BoardDto board : boardList) {
				boardService.deleteBoard(board.getId());
			}
			
			//권한테이블 삭제
			mapper.deleteAuth(member.getId(), "ROLE_USER");
			
			//멤버테이블 삭제
			int cnt = mapper.deleteMemberById(dto.getId());
			
			//순서 중요! 1,2 바뀌면 foreign key 위배됨
			return cnt == 1;
			//권한이 2개 이상 있을수도 있으니까 cnt1은 안쓴다.
			
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


	public void initPassword(String id) {
		
		String pw = passwordEncoder.encode(id);
		
		mapper.updatePasswordById(id, pw);
		
	}


}
