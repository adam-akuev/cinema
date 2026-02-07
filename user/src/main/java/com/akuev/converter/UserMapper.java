package com.akuev.converter;

import com.akuev.dto.UserDTO;
import com.akuev.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Конвертер для преобразования между User и UserDTO.
 * Использует ModelMapper для автоматического маппинга полей.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    /**
     * Преобразует сущность User в UserDTO.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    public UserDTO toDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Преобразует UserDTO в сущность User.
     *
     * @param userDTO DTO пользователя
     * @return сущность пользователя
     */
    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}