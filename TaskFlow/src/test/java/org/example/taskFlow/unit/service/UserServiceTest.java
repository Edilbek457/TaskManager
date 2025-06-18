package org.example.taskFlow.unit.service;

import org.example.taskFlow.dto.user_security.UserCreateRequest;
import org.example.taskFlow.dto.user_security.UserUpdateRequest;
import org.example.taskFlow.exception.user.EmailAlreadyExistsException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.UserRepository;
import org.example.taskFlow.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final static long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getAllUsers_whenUserCount_20_thenGet_20_users() {
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setFirstName("User " + i);
            users.add(user);
        }
        Page<User> mockPage = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findAll(pageable)).thenReturn(mockPage);
        Page<User> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void getUserById_whenUserExists_thenGetUser() {
        User user = new User();
        user.setId(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(USER_ID);

        assertNotNull(foundUser);
        assertEquals(USER_ID, foundUser.getId());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void getUserById_whenUserDoesNotExist_thenThrowException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void saveUser_whenUserEmailAlreadyExists_thenThrowException() {
        String email = "Test@gmail.com";
        UserCreateRequest userRequest = new UserCreateRequest("Test", "Test", "toleevedilbek@gmail.com",  "1234g3rg24t423sadf");
        User user = new User();
        user.setFirstName(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.saveUser(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void saveUser_whenNewUser_thenSaveUser() {
        String email = "Test@gmail.com";
        UserCreateRequest userRequest = new UserCreateRequest("Test", "Test", "toleevedilbek@gmail.com",  "1234g3rg24t423sadf");
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setId(USER_ID);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User userResult = userService.saveUser(userRequest);

        assertNotNull(userResult);
        assertEquals(user.getLastName(), userResult.getLastName());
        assertEquals(user.getFirstName(), userResult.getFirstName());
        assertEquals(user.getEmail(), userResult.getEmail());
        assertEquals(user.getId(), userResult.getId());
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUser_thenCreateUser() {
        UserCreateRequest userRequest = new UserCreateRequest("Test", "Test", "Test@gmail.com", "1234g3rg24t423sadf");

        User userResult = userService.createUser(userRequest);

        assertNotNull(userResult);
        assertEquals(userRequest.firstName(), userResult.getFirstName());
        assertEquals(userRequest.lastName(), userResult.getLastName());
        assertEquals(userRequest.email(), userResult.getEmail());
        assertTrue(userResult.isActive());
    }

    @Test
    public void updateUser_whenUserExists_thenUpdateUser() {
        UserUpdateRequest userRequest = new UserUpdateRequest("NewSpring", "NewBoot");
        User existingUser = new User();
        existingUser.setId(USER_ID);
        existingUser.setFirstName("Spring");
        existingUser.setLastName("Boot");
        existingUser.setEmail("SpringBoot@gmail.com");
        User updatedUser = new User();
        updatedUser.setId(USER_ID);
        updatedUser.setFirstName(userRequest.firstName());
        updatedUser.setLastName(userRequest.lastName());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        User userResult = userService.updateUser(USER_ID, userRequest);

        assertNotNull(userResult);
        assertEquals(userRequest.firstName(), userResult.getFirstName());
        assertEquals(userRequest.lastName(), userResult.getLastName());
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUser_whenUserDoesNotExist_thenThrowException() {
        UserUpdateRequest userRequest = new UserUpdateRequest("firstName", "lastName");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1, userRequest));
        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void updateUser_whenUserEmailAlreadyExists_thenThrowException() {
        UserUpdateRequest userRequest = new UserUpdateRequest("firstName", "lastName");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(USER_ID, userRequest));
        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void deleteUser_whenUserExists_thenDeleteUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
        userService.deleteUser(USER_ID);

        verify(userRepository).findById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    public void deleteUser_whenUserNotExist_thenThrowException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(USER_ID));
        verify(userRepository).findById(USER_ID);
    }
}