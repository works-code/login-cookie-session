package com.code.controller;

import com.code.vo.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class LoginController {

    /***
     * 로그인 페이지
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(HttpServletRequest request, Model model){
        log.error("### session ID => {}", request.getSession().getId());
        // 쿠키 만료시 Cookie 값이 null이 된다. (유효 시간 동안은 개발자 모드 진입 후(F12) 쿠키 보면 AUTH 라는 이름으로 세션 ID가 들어가 있음)
        Cookie auth = WebUtils.getCookie(request, "AUTH");

        // 로그인 정보가 있을시
        if(!ObjectUtils.isEmpty(auth)){
            if(StringUtils.equalsIgnoreCase(auth.getValue(), request.getSession().getId())){
                String username = (String) request.getSession().getAttribute("username");
                if(StringUtils.isNotEmpty(username)){
                    model.addAttribute("username", username);
                    return "success";
                }
            }
        }
        // 로그인 만료 or 비 로그인자 일시
        return "login";
    }

    /***
     * 로그인 요청
     * @param member
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String login(Member member, HttpServletRequest request, HttpServletResponse response, Model model){
        log.error("### session ID => {}", request.getSession().getId());
        // 세션 저장 (세션 ID, 사용자 정보)
        // 세션은 브라우저 당 1개 생성(시크릿 모드도 동일, 같은 브라우저에서 새탭 or 새창 띄워도 로그인 유지) / 쿠키는 시크릿 모드시 없어짐
        request.getSession().setAttribute("username", member.getUsername());
        // 쿠키 전달 (세션 ID)
        response.addCookie(new Cookie("AUTH", request.getSession().getId()){{
            setMaxAge(60); // 자동 로그인 10 초 유지
            setPath("/");
        }});

        // 화면에 표시할 ID 셋팅
        model.addAttribute("username", member.getUsername());
        return "success";
    }

    /***
     * 로그아웃 요청
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request){
        log.error("### session ID => {}", request.getSession().getId());
        // 세션 저장소 세션 제거
        request.getSession().invalidate();
        return "redirect:/";
    }

}
