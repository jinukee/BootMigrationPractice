package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 홈 컨트롤러.
 *
 * [JSP 대응]
 * JSP에서는 index.jsp가 /post/list.do로 리다이렉트했다.
 * Spring Boot에서는 HomeController가 "/" 요청을 직접 처리한다.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    // 요청 처리 흐름
    // dispatcherServlet이 요청 수신
    // HandlerMapping을 통해 home() 이 실행되어야 함을 알아냄
    // DispatcherServlet이 HandlerAdapter에게 해당 controller의 method를 실행해달라고 요청
    // HandlerAdapter는 controller 의 method parameter를 분석하여 Model 객체 생성 후 controller에게 전달
    // home() 이 실행되며, postList가 model에 담기고 "index"라는 문자열이 반환됨
    // 다시 HandlerAdapter가 반환된 view name("index")와 Model을 묶어서 ModelAndView 객체 생성
    // ModelAndView를 DispatcherServlet에게 반환
    // DispatcherServlet은 ModelAndView 내부의 view name을 꺼내서 ViewResolver에게 전달
    // ViewResolver는 해당 view name을 가진 view(template)의 실제 물리적 위치로부터 view 객체를 꺼내서 DispatcherServlet에게 전달
    // DispatcherServlet은 찾은 실제 view객체에 Model 데이터를 전달하고, view 객체는 render() 명령을 통해 완성된 view를 만들어 client에게 전달
    @GetMapping("/")
    public String home(Model model) {
        List<PostDTO> postList = postService.findAll();
        model.addAttribute("postList", postList);
        return "index";
    }
}
