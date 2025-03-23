//package com.threefour;
//
//import jakarta.persistence.EntityManager;
//import net.bytebuddy.asm.Advice;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.BatchPreparedStatementSetter;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@ActiveProfiles("test")
//@SpringBootTest(classes = {ThreefourApplication.class, TestDatabaseConfig.class})
//public class DummyDataLoader {
//    private static final String INSERT_SQL =
//            "insert into post (author_nickname, category, title, content, created_at, updated_at) values (?, ?, ?, ?, ?, ?)";
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    void batchUpdate() {
//        // 더미 데이터 수
//        int total = 1_000_000;
//        // 배치 사이즈
//        int batchSize = 1000;
//
//        List<PostDto> postDtos = new ArrayList<>(total);
//        LocalDateTime now = LocalDateTime.now();
//        for (int i = 0; i < total; i++) {
//            postDtos.add(new PostDto("이재형", "4구게시판", "더미데이터 " + (i + 2_000_001), "더미데이터", now, now));
//        }
//
//        for (int i = 0; i < postDtos.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, postDtos.size());
//            List<PostDto> batch = postDtos.subList(i, end);
//
//            jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
//                @Override
//                public void setValues(PreparedStatement ps, int i) throws SQLException {
//                    PostDto postDto = batch.get(i);
//                    ps.setString(1, postDto.authorNickname);
//                    ps.setString(2, postDto.category);
//                    ps.setString(3, postDto.title);
//                    ps.setString(4, postDto.content);
//                    ps.setTimestamp(5, Timestamp.valueOf(postDto.createdAt));
//                    ps.setTimestamp(6, Timestamp.valueOf(postDto.updatedAt));
//                }
//
//                @Override
//                public int getBatchSize() {
//                    return batch.size();
//                }
//            });
//        }
//    }
//
//    class PostDto {
//        String authorNickname;
//        String category;
//        String title;
//        String content;
//        LocalDateTime createdAt;
//        LocalDateTime updatedAt;
//
//        public PostDto(String authorNickname, String category, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
//            this.authorNickname = authorNickname;
//            this.category = category;
//            this.title = title;
//            this.content = content;
//            this.createdAt = createdAt;
//            this.updatedAt = updatedAt;
//        }
//    }
//}
