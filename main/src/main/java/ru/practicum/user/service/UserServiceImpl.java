package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utility.MyPageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.mapper.UserMapper.toUser;
import static ru.practicum.user.mapper.UserMapper.toUserDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = userRepository.save(toUser(newUserRequest));

        return toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        return userRepository.findByIdIn(ids, new MyPageRequest(from, size))
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User", userId));

        userRepository.deleteById(userId);
    }
}
