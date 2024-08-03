package ru.dragomirov.cloudfilestorage.entity.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElse(null);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void update(Long id, User updatePerson) {
        updatePerson.setId(id);
        userRepository.save(updatePerson);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
