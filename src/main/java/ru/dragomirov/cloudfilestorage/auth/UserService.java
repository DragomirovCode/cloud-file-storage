package ru.dragomirov.cloudfilestorage.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.auth.login.UserNotFoundException;
import ru.dragomirov.cloudfilestorage.auth.registration.DuplicateUserException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "UserService::getById", key = "#id")
    public User getByById(Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "UserService::getByUsername", key = "#username")
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = "UserService::getById", key = "#user.id"),
            @CachePut(value = "UserService::getByUsername", key = "#user.username")}
    )
    public void save(User user) {
        try {
            user.setRole("user");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new DuplicateUserException();
        }
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = "UserService::getById", key = "#updatePerson.id"),
            @CachePut(value = "UserService::getByUsername", key = "#updatePerson.username")}
    )
    public void update(Long id, User updatePerson) {
        updatePerson.setId(id);
        userRepository.save(updatePerson);
    }

    @Transactional
    @CacheEvict(value = "UserService::delete", key = "#id")
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
