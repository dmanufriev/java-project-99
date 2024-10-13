package hexlet.code.service;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.exception.ActionForbiddenException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                    .map(userMapper::map)
                    .toList();
    }

    public UserDTO create(@Valid UserCreateDTO userData) {
        User user = userMapper.map(userData);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO findByEmail(String email) {
        var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.map(user);
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.map(user);
    }

    public UserDTO update(@Valid UserUpdateDTO userData, Long id) {

        var user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var currentUser = userUtils.getCurrentUser();
        if (null == currentUser || id != currentUser.getId()) {
            throw new ActionForbiddenException("Update forbidden");
        }

        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        var currentUser = userUtils.getCurrentUser();
        if (null == currentUser || id != currentUser.getId()) {
            throw new ActionForbiddenException("Delete forbidden");
        }
        userRepository.deleteById(id);
    }
}
