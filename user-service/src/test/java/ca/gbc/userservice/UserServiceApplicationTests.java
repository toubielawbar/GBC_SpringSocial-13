package ca.gbc.userservice;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.repository.UserRepository;
import ca.gbc.userservice.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    UserRequest getUserRequest() {
        return UserRequest.builder()
                .name("name 1")
                .username("username 1")
                .email("email 1")
                .password("password 1")
                .build();
    }

    private List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        Long id = 1L;
        user.setId(id);
        user.setName("name 1");
        user.setUsername("username 1");
        user.setEmail("email 1");
        user.setPassword("password 1");

        userList.add(user);
        return userList;
    }

    private String convertObjectToJson(List<UserResponse> userList) throws Exception {
        return objectMapper.writeValueAsString(userList);
    }

    private List<UserResponse> convertJsonToObject(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, new TypeReference<List<UserResponse>>() {
        });
    }

    @Test
    void createUser() throws Exception {
        UserRequest userRequest = getUserRequest();
        String userRequestString = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(userRepository.findAll().size() > 0);
    }

    @Test
    void getUserById() throws Exception {
        // Arrange
        User user = getUserList().get(0);

        if(user == null) {
            throw new Exception("User is null");
        }

        // Action
        userRepository.save(user);
    }

    @Test
    void updateUser() throws Exception {
        userService.createUser(getUserRequest());

        User savedUser = userRepository.findAll().stream().findFirst().orElse(null);
        Assertions.assertNotNull(savedUser, "Expected saved user not to be null");

        UserRequest updatedUserRequest = UserRequest.builder()
                .username(savedUser.getUsername())
                .email("newemail@email.com")
                .password(savedUser.getPassword())
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUser() throws Exception {
        userService.createUser(getUserRequest());

        User savedUser = userRepository.findAll().stream().findFirst().orElse(null);
        Assertions.assertNotNull(savedUser, "Expected saved user not to be null");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/user/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
