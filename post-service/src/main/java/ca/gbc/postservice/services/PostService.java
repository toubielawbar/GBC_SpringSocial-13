package ca.gbc.postservice.services;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService{

    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void createPost(PostRequest postRequest) {

        log.info("Creating a new post {}", postRequest.getTitle());

        Post post = Post.builder()
                .user(postRequest.getUser())
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();

        postRepository.save(post);

        log.info("post {} is saved", post.getId());

    }

    @Override
    public String updatePost(String productId, PostRequest postRequest) {


        log.info("Updating a post with id {}", productId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(productId));
        Post post = mongoTemplate.findOne(query, Post.class);

        if(post != null){
            post.setUser(postRequest.getUser());
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            log.info("post {} is updated", post.getId());
            return postRepository.save(post).getId();
        }
        return productId.toString();

    }

    @Override
    public void deletePost(String productId) {
        log.info("product {} is deleted", productId);
        postRepository.deleteById(productId);
    }

    @Override
    public List<PostResponse> getAllPosts() {

        log.info("Returning s list of products");

        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapToPostResponse).toList();
    }


    private PostResponse mapToPostResponse(Post post){

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .build();
    }
}