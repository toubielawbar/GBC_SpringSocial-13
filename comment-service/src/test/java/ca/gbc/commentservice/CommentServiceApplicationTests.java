package ca.gbc.commentservice;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.repository.CommentRepository;
import ca.gbc.commentservice.service.CommentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    CommentRequest getCommentRequest() {
        return CommentRequest.builder()
                .author("comment author")
                .post("post comment")
                .comment("comment")
                .build();
    }

    private List<Comment> getCommentList() {
        List<Comment> commentList = new ArrayList<>();
        Comment comment = new Comment();
        Long id = 1L;
        comment.setId(id);
        comment.setAuthor("comment author");
        comment.setPost("post comment");
        comment.setComment("comment");
        commentList.add(comment);
        return commentList;
    }

    private String convertObjectToJson(List<CommentResponse> commentList) throws Exception {
        return objectMapper.writeValueAsString(commentList);
    }

    private List<CommentResponse> convertJsonToObject(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, new TypeReference<List<CommentResponse>>() {
        });
    }

    @Test
    void createComment() throws Exception {
        CommentRequest commentRequest = getCommentRequest();
        String commentRequestString = objectMapper.writeValueAsString(commentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(commentRepository.findAll().size() > 0);
    }

    @Test
    void getCommentById() throws Exception {
        // Arrange
        Comment comment = getCommentList().get(0);

        if(comment == null) {
            throw new Exception("Comment is null");
        }

        // Action
        commentRepository.save(comment);
    }

    @Test
    void updateComment() throws Exception {
        commentService.createComment(getCommentRequest());

        Comment savedComment = commentRepository.findAll().stream().findFirst().orElse(null);
        Assertions.assertNotNull(savedComment, "Expected saved comment not to be null");

        CommentRequest updatedCommentRequest = CommentRequest.builder()
                .post(savedComment.getPost())
                .comment("Comment updated")
                .author(savedComment.getAuthor())
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/comment/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteComment() throws Exception {
        commentService.createComment(getCommentRequest());

        Comment savedComment = commentRepository.findAll().stream().findFirst().orElse(null);
        Assertions.assertNotNull(savedComment, "Expected saved comment not to be null");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/comment/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}

