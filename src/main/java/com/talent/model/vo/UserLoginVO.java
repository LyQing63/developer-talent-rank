package com.talent.model.vo;

import com.talent.model.dto.User;
import lombok.Data;

@Data
public class UserLoginVO {
    private User user;
    private String token;
}
