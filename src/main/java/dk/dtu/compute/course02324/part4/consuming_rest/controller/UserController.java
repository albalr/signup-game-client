package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public class UserController {
    private final RestApiService apiService;
    private User currentUser;

    public UserController(RestApiService apiService) {
        this.apiService = apiService;
    }

    public User signIn(String username) {
        List<User> users = apiService.getClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/searchusers")
                        .queryParam("name", username)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<User>>() {});

        if (users != null && !users.isEmpty()) { // check if user exists
            currentUser = users.get(0);
            return currentUser;
        }

        return null;
    }

    public User signUp(String username) {
        User newUser = new User();
        newUser.setName(username);

        currentUser = apiService.getClient().post() // post user data to the server
                .uri("/users/signup")
                .body(newUser)
                .retrieve()
                .body(User.class);

        return currentUser;
    }

    public void signOut() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}