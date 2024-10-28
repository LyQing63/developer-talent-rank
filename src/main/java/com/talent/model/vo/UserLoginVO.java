package com.talent.model.vo;

import com.talent.model.dto.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginVO implements Serializable {
    private User user;
    private String token;
}
