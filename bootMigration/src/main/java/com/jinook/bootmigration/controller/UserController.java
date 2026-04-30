package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.security.CustomUserDetails;
import com.jinook.bootmigration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 유저 컨트롤러.
 * [Spring Security 도입 후 변경사항]
 * 1. 로그인/로그아웃 엔드포인트 제거
 * - Before: @PostMapping("/login"), @GetMapping("/logout") 직접 구현
 * - After:  Spring Security가 /user/login (POST), /user/logout (POST) 자동 처리
 * → 로그인 "폼"을 보여주는 GET /user/login은 유지 (loginPage 설정)
 * 2. 로그인 유저 획득 방식 변경
 * - Before: HttpSession session → session.getAttribute("loginUser") → UserDTO 캐스팅
 * - After:  @AuthenticationPrincipal CustomUserDetails userDetails → userDetails.getId()
 * 3. 세션 직접 조작 제거
 * - Before: session.setAttribute("loginUser", user), session.invalidate()
 * - After:  Spring Security가 SecurityContext를 통해 자동 관리
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 폼
     */
    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String nickname,
                           Model model) {
        try {
            UserDTO dto = UserDTO.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .build();
            userService.register(dto);
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }

    /**
     * 로그인 폼.
     * Spring Security가 POST /user/login은 자동 처리하지만,
     * GET /user/login (폼 페이지)은 우리가 직접 제공해야 한다.
     */
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    // @PostMapping("/login") → 삭제됨 (Spring Security가 자동 처리)
    // @GetMapping("/logout") → 삭제됨 (Spring Security가 자동 처리)

    /**
     * 회원정보 수정 폼.
     * <p>
     * [Before] HttpSession에서 loginUser 꺼내기
     * [After]  @AuthenticationPrincipal로 현재 로그인 유저 주입받기
     */
    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        UserDTO user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        return "user/edit";
    }

    /**
     * 회원정보 수정 처리
     */
    @PostMapping("/update")
    public String update(@RequestParam String nickname,
                         @RequestParam(required = false) String password,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(userDetails.getId(), nickname, password);

            // SecurityContext의 인증 정보도 갱신 (닉네임 변경 반영)
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/edit";
        }
    }

    /**
     * 회원탈퇴
     */
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(userDetails.getId());
        SecurityContextHolder.clearContext();  // 인증 정보 제거
        return "redirect:/user/login";
    }
}
