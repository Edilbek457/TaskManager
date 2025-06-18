package org.example.taskFlow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskFlow.app.redis.RedisPublisher;
import org.example.taskFlow.dto.user_security.UserCreateRequest;
import org.example.taskFlow.dto.user_security.UserUpdateRequest;
import org.example.taskFlow.enums.RedisChannel;
import org.example.taskFlow.enums.Role;
import org.example.taskFlow.exception.security.PrivilegeExceededException;
import org.example.taskFlow.exception.user.EmailAlreadyExistsException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.example.taskFlow.model.User;
import org.example.taskFlow.model.Event;
import org.example.taskFlow.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RedisPublisher redisPublisher;
    private final EventService eventService;
    private final String redisUserId = "userId:";

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById (long id) {
        Object user = redisTemplate.opsForValue().get(redisUserId+id);
        if (user != null) {
            return objectMapper.convertValue(user, User.class);
        } else {
            Optional<User> foundedUser = userRepository.findById(id);
            if (foundedUser.isPresent()) {
                redisTemplate.opsForValue().set(redisUserId+id, foundedUser.get(), 24, TimeUnit.HOURS);
                return foundedUser.get();
            } else {
                throw new UserNotFoundException(id);
            }
        }
    }

    public boolean thisIsNewUserByEmail(String email) {
        Optional<User> foundedUser = userRepository.findByEmail(email);
        return foundedUser.isEmpty();
    }

    public User getUserByEmailWithoutRedis (String email) {
        Optional<User> foundedUser = userRepository.findByEmail(email);
        if (foundedUser.isPresent()) {
            return foundedUser.get();
        } else {
            throw new UserNotFoundException(email);
        }
    }

    public User saveUser (@Valid UserCreateRequest userCreateRequest){
        if (userRepository.findByEmail(userCreateRequest.email()).isPresent()) {
            throw new EmailAlreadyExistsException(userCreateRequest.email());
        } else {
            User createddUser = createUser(userCreateRequest);
            User savedUser = userRepository.save(createddUser);
            redisTemplate.opsForValue().set(redisUserId+savedUser.getId(), savedUser, 24, TimeUnit.HOURS);
            Event event = eventService.createEvent("USER_CREATE", UUID_Service.fromLong(savedUser.getId()), "USER", null, savedUser);
            redisPublisher.publish(RedisChannel.USER_CREATE.getChannel(), event);
            return savedUser;
        }
    }

    public User createUser(@Valid UserCreateRequest userCreateRequest) {
        User user = new User();
        user.setFirstName(userCreateRequest.firstName());
        user.setLastName(userCreateRequest.lastName());
        user.setEmail(userCreateRequest.email());
        user.setActive(true);
        user.setRole(Role.USER);
        return user;
    }

    public User updateUser (long id, @Valid UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        User foundedUser = new User();
        foundedUser.setFirstName(user.getFirstName());
        foundedUser.setLastName(user.getLastName());
        foundedUser.setEmail(user.getEmail());

        user.setFirstName(userUpdateRequest.firstName());
        user.setLastName(userUpdateRequest.lastName());
        User savedUser = userRepository.save(user);
        redisTemplate.opsForValue().set(redisUserId+savedUser.getId(), savedUser, 24, TimeUnit.HOURS);
        Event event = eventService.createEvent("USER_UPDATE", UUID_Service.fromLong(savedUser.getId()), "USER", userUpdateRequest.from(foundedUser), userUpdateRequest);
        redisPublisher.publish(RedisChannel.USER_UPDATE.getChannel(), event);
        return savedUser;
    }

    public void deleteUser (long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        redisTemplate.delete(redisUserId + id);
        Event event = eventService.createEvent("USER_DELETE", UUID_Service.fromLong(user.getId()), "USER", user, null);
        redisPublisher.publish(RedisChannel.USER_DELETE.getChannel(), event);
        userRepository.deleteById(id);
    }

    public User grantRoleByUserId (long userId, Role role) {
        if (role.equals(Role.ADMIN)) {throw new PrivilegeExceededException();}
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setRole(role);
        return userRepository.save(user);
    }
}

