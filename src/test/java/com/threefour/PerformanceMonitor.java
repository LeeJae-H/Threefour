//package com.threefour;
//
//import com.threefour.application.PostService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//
//@ActiveProfiles("test")
//@SpringBootTest(classes = {ThreefourApplication.class, TestDatabaseConfig.class})
//public class PerformanceMonitor {
//
//    @Autowired
//    private PostService postService;
//
//    @Test
//    public void findAllPostsTest() {
//        double totalTime = 0.0;
//        Pageable pageable = PageRequest.of(0, 15);
//
//        for (int i = 0; i < 5; i++) {
//            long startTime = System.currentTimeMillis(); // 시작 시간 기록
//            postService.getPostsList(pageable);
//            long endTime = System.currentTimeMillis(); // 종료 시간 기록
//
//            double durationSec = (endTime - startTime) / 1000.0; // 밀리초를 초로 변환
//            totalTime += durationSec;
//        }
//        double averageTime = totalTime / 5;
//        System.out.println("모든 게시글 페이지 조회 평균 실행 시간: " + averageTime + " seconds");
//    }
//}
