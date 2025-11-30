package com.islab1.api.dto;

import com.islab1.entities.User;
import com.islab1.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private UserRole role;

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }
}
