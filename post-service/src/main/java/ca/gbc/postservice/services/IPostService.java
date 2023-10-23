package ca.gbc.postservice.services;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;

import java.util.List;

public interface IPostService {

    void createPost(PostRequest postRequest);

    String updatePost(String productId, PostRequest postRequest);

    void deletePost(String productId);

    List<PostResponse> getAllPosts();
}
