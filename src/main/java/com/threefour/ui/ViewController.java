package com.threefour.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 페이지(html)를 불러오는 GET api는 Security Filter를 거치지 않습니다.
 * 해당 html 로드 시점에 AccessToken을 검증하는 API를 호출해서 인증하는 방식입니다
 */
@Controller
public class ViewController {

    /**
     * 홈 화면 API
     */
    @GetMapping("/")
    public String getHome() {
        return "home";
    }

    /**
     * 페이징 홈 화면 API
     */
    @GetMapping("/view/home/{page}")
    public String getHomeByPaging(@PathVariable int page, Model model) {
        model.addAttribute("page", page);
        return "homeByPaging";
    }

    /**
     * 게시판(category) 화면 API
     */
    @GetMapping("/view/posts/{category}")
    public String getPostCategory(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        return "post/category";
    }

    /**
     * 페이징 게시판(category) 화면 API
     */
    @GetMapping("/view/posts/{category}/{page}")
    public String getPostCategoryByPaging(@PathVariable String category, @PathVariable int page, Model model) {
        model.addAttribute("category", category);
        model.addAttribute("page", page);
        return "post/categoryByPaging";
    }

    /**
     * 게시글 상세 화면 API
     */
    @GetMapping("/view/posts/{postId}/details")
    public String getPostDetails(@PathVariable Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "post/details";
    }

    /**
     * 게시글 수정 화면 API
     *
     * 회원만 가능합니다. (로드하는 시점에 /api/token/validate API 호출 필요)
     */
    @GetMapping("/view/posts/{postId}/edit")
    public String getEditPost(@PathVariable Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "post/edit";
    }

    /**
     * 게시글 작성 화면 API
     *
     * 회원만 가능합니다. (로드하는 시점에 /api/token/validate API 호출 필요)
     */
    @GetMapping("/view/posts/{category}/write")
    public String getWritePost(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        return "post/write";
    }

    /**
     * 회원가입 화면 API
     */
    @GetMapping("/view/users/join")
    public String getJoin() {
        return "join";
    }

    /**
     * 마이페이지 화면 API
     *
     * 회원만 가능합니다. (로드하는 시점에 /api/token/validate API 호출 필요)
     */
    @GetMapping("/view/users/my")
    public String getMyInfo() {
        return "user/myInfo";
    }
}