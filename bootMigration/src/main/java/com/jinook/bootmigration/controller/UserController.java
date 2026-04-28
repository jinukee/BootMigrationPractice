package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 유저 컨트롤러.
 *
 * [JSP 대응]
 * JSP에서는 6개의 Command 클래스가 유저 관련 요청을 각각 처리했다:
 *   - RegisterCommand, LoginCommand, LogoutCommand
 *   - UserEditFormCommand, UpdateUserCommand, DeleteUserCommand
 *
 * Spring Boot에서는 하나의 @Controller 클래스 안에 여러 @RequestMapping 메서드로 통합한다.
 * → FrontControllerServlet의 URL-Command 매핑 코드가 불필요해짐.
 * → 각 메서드에 @GetMapping, @PostMapping 어노테이션이 자동 매핑.
 *
 * [주요 차이점]
 * 1. request.getParameter("email") → @RequestParam String email
 * 2. request.setAttribute("error", msg) → model.addAttribute("error", msg)
 * 3. request.getRequestDispatcher().forward() → return "viewName"
 * 4. response.sendRedirect() → return "redirect:/path"
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 회원가입 폼 - JSP의 RegisterCommand (GET 분기) */
    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    /** 회원가입 처리 - JSP의 RegisterCommand (POST 분기) */
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

    /** 로그인 폼 - JSP의 LoginCommand (GET 분기) */
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    /** 로그인 처리 - JSP의 LoginCommand (POST 분기) */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            UserDTO user = userService.login(email.trim(), password);
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }

    /** 로그아웃 - JSP의 LogoutCommand */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /** 회원정보 수정 폼 - JSP의 UserEditFormCommand */
    @GetMapping("/edit")
    public String editForm(HttpSession session, Model model) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        UserDTO user = userService.findById(loginUser.getId());
        model.addAttribute("user", user);
        return "user/edit";
    }

    /** 회원정보 수정 처리 - JSP의 UpdateUserCommand */
    @PostMapping("/update")
    public String update(@RequestParam String nickname,
                         @RequestParam(required = false) String password,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
            UserDTO updatedUser = userService.updateUser(loginUser.getId(), nickname, password);
            session.setAttribute("loginUser", updatedUser);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/edit";
        }
    }

    /** 회원탈퇴 - JSP의 DeleteUserCommand */
    @PostMapping("/delete")
    public String delete(HttpSession session) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        userService.deleteUser(loginUser.getId());
        session.invalidate();
        return "redirect:/";
    }
}
