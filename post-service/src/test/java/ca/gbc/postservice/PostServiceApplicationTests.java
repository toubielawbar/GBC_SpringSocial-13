package ca.gbc.postservice;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class PostServiceApplicationTests extends AbstractContainerBaseTest{

//	@Test
//	void contextLoads() {
//	}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    MongoTemplate mongoTemplate;


    private PostRequest getPostRequest(){
        return PostRequest.builder()
                .user("post1")
                .title("post title1")
                .content("post content1")
                .build();
    }

    private List<Post> getPostList(){
        List<Post> posts = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        Post post = new Post().builder().
                id(uuid.toString())
                .user("post1")
                .title("post title1")
                .content("post content1")
                .build();

        posts.add(post);

        return posts;
    }

    private String convertObjectToString(List<PostResponse> postList) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper mapper = new org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper();
        return mapper.writeValueAsString(postList);
    }

    private List<PostResponse> convertObjectToString(String jsonString) throws Exception {
//        org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper mapper = new org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper();
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(jsonString, new TypeReference<List<PostResponse>>() {});
    }

    @Test
    void createPost() throws Exception {
        PostRequest postRequest = getPostRequest();
        String postRequestJsonString = objectMapper.writeValueAsString(postRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        System.out.println("postRepository.findAll().size() " + postRepository.findAll().size());

//        Assertion
        Assertions.assertTrue(postRepository.findAll().size() > 0);
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is("post title1"));
        List<Post> posts = mongoTemplate.find(query, Post.class);
        Assertions.assertTrue(posts.size() > 0);
    }

    /**
     * BDD - Behaviour Driven Development
     * Given - Setup
     * When - Action
     * Then - Verify
     */
    @Test
    void getAllPosts() throws Exception{

        //Given
        postRepository.saveAll(getPostList());

        //WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/post")
                .accept(MediaType.APPLICATION_JSON));
        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNodes.size();
        int expectedSize = getPostList().size();

        Assertions.assertEquals(expectedSize, actualSize);


    }

    @Test
    void updatePosts() throws Exception{

        //GIVEN
        Post savedPost = Post.builder()
                .id(UUID.randomUUID().toString())
                .user("post1")
                .title("post title1")
                .content("post content1")
                .build();


        postRepository.save(savedPost);

        savedPost.setTitle("post title1");

        String postRequestString = objectMapper.writeValueAsString(savedPost);

        //WHEN

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/post/" + savedPost.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestString));

        //THEN
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedPost.getId()));
        Post storedPost = mongoTemplate.findOne(query, Post.class);

        assertEquals(savedPost.getTitle(), storedPost.getTitle());

    }

    @Test
    void deletePosts() throws Exception{

        //GIVEN
        Post savedPost = Post.builder()
                .id(UUID.randomUUID().toString())
                .user("post1")
                .title("post title1")
                .content("post content1")
                .build();

        postRepository.save(savedPost);


        //WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/post/" + savedPost.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));


        //THEN
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedPost.getId()));
        Long postCount = mongoTemplate.count(query, Post.class);

        assertEquals(0, postCount);

    }

}
