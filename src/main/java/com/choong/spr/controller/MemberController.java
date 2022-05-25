package com.choong.spr.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choong.spr.domain.MemberDto;
import com.choong.spr.service.MemberService;

@Controller
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	private MemberService service;

	//TODO : MemberService 작성
	//add Member method 작성
	//MemberMapper.java, xml 작성
	//insertMember method 작성


	@GetMapping("signup")
	public void signupForm() {
		
	}
	
	@PostMapping("signup")
	public void signupProcess(MemberDto member) {
		boolean success = service.addMember(member);
		
		if(success) {
			
		} else {
			
		}
		
	}
	
	@GetMapping(path = "check", params = "id") //idCheck실행위해 params=id
	@ResponseBody //view로 해석될 필요 없으므로
	public String idCheck(String id) {
		
		boolean exist = service.hasMemberId(id);
		
		if (exist) {
			return "notOk";
			
		} else {
			return "ok";
			
		}
		
	}
	
	@GetMapping(path = "check", params = "email") //idCheck실행위해 params=id
	@ResponseBody //view로 해석될 필요 없으므로
	public String emailCheck(String email) {
		//버튼, 메시지 출력, 서비스메소드, mappermethod,
		boolean exist = service.hasMemberEmail(email);
		
		if (exist) {
			return "notOk";
			
		} else {
			return "ok";
			
		}
		
	}
	
	@GetMapping(path = "check", params = "nickName") //idCheck실행위해 params=id
	@ResponseBody //view로 해석될 필요 없으므로
	public String nickNameCheck(String nickName) {
		//버튼, 메시지 출력, 서비스메소드, mappermethod,
		boolean exist = service.hasMembernickName(nickName);
		
		if (exist) {
			return "notOk";
			
		} else {
			return "ok";
			
		}
		
	}
}
