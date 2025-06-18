package org.example.taskFlow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.user_security.UserCreateRequest;
import org.example.taskFlow.dto.user_security.UserResponse;
import org.example.taskFlow.dto.user_security.UserUpdateRequest;
import org.example.taskFlow.model.User;
import org.example.taskFlow.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        UserResponse userResponse = UserResponse.from(user);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "id") String sortBy,
                                       @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userService.getAllUsers(pageable);
        Page<UserResponse> userResponses = usersPage.map(UserResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        User user = userService.saveUser(userCreateRequest);
        UserResponse userResponse = UserResponse.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable long id, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        User user = userService.updateUser(id, userUpdateRequest);
        UserResponse userResponse = UserResponse.from(user);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
