package com.choong.spr.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choong.spr.domain.MemberDto;
import com.choong.spr.service.MemberService;

@Controller
@RequestMapping("member")
public class MemberController {

	@Autowired
	private MemberService service;

	// TODO : MemberService 작성
	// add Member method 작성
	// MemberMapper.java, xml 작성
	// insertMember method 작성

	@GetMapping("signup")
	public void signupForm() {

	}

	@PostMapping("signup")
	public String signupProcess(MemberDto member, RedirectAttributes rttr) {
		boolean success = service.addMember(member);

		if (success) {
			rttr.addFlashAttribute("message", "회원가입이 완료되었습니다.");
			return "redirect:/board/list";
		} else {
			rttr.addFlashAttribute("message", "회원가입이 실패되었습니다.");
			rttr.addFlashAttribute("member", member);
			return "redirect:/member/signup";
		}

	}

	@GetMapping(path = "check", params = "id") // idCheck실행위해 params=id
	@ResponseBody // view로 해석될 필요 없으므로
	public String idCheck(String id) {

		boolean exist = service.hasMemberId(id);

		if (exist) {
			return "notOk";

		} else {
			return "ok";

		}

	}

	@GetMapping(path = "check", params = "email") // idCheck실행위해 params=id
	@ResponseBody // view로 해석될 필요 없으므로
	public String emailCheck(String email) {
		// 버튼, 메시지 출력, 서비스메소드, mappermethod,
		boolean exist = service.hasMemberEmail(email);

		if (exist) {
			return "notOk";

		} else {
			return "ok";

		}

	}

	@GetMapping(path = "check", params = "nickName") // idCheck실행위해 params=id
	@ResponseBody // view로 해석될 필요 없으므로
	public String nickNameCheck(String nickName) {
		// 버튼, 메시지 출력, 서비스메소드, mappermethod,
		boolean exist = service.hasMemberNickName(nickName);

		if (exist) {
			return "notOk";

		} else {
			return "ok";

		}

	}
	


	@GetMapping("list")
	public void list(Model model) {
		List<MemberDto> list = service.listMember();
		model.addAttribute("memberList", list);
	}

	// jsp (id, password, email, nickName, inserted) table로 보여주세요.
	// service, mapper 작성 ORDER BY inserted DESC (나중에 가입한 사람이 위로)

	@GetMapping("get")
	public void getMember(String id, Model model) {
		MemberDto member = service.getMemberById(id);

		model.addAttribute("member", member);
	}

	@PostMapping("remove")
	public String removeMember(MemberDto dto, Principal principal, RedirectAttributes rttr) {
		boolean success = service.removeMember(dto);

		if (success) {
			rttr.addFlashAttribute("message", "회원 탈퇴 되었습니다.");
			return "redirect:/board/list";
		} else {
			rttr.addAttribute("id", dto.getId());
			return "redirect:/member/get";
		}

	}
	
	@PostMapping("modify")
	public String modifyMember(MemberDto dto, String oldPassword, RedirectAttributes rttr) {
		//model은 forward할때 request1번, redirect는 request2번일어날때
		//2번이라 query string을 붙여서 넘어감
		//request보다 넓은 영역인 session에 붙이면 되는데 권장되지 않아서
		
		boolean success = service.modifyMember(dto, oldPassword);
		
		if (success) {
			rttr.addFlashAttribute("message","회원 정보가 수정되었습니다.");
		} else {
			rttr.addFlashAttribute("message","회원 정보가 수정되지 않았습니다.");
		}
		
		rttr.addFlashAttribute("member", dto); // 객체를 model처럼 object (m
		rttr.addAttribute("id",dto.getId()); // 객체를 query string으로 받아서 id =tweety
		return "redirect:/member/get";
	

}
	@GetMapping("login")
	public void loginPage() {
		
	}
	
	
}