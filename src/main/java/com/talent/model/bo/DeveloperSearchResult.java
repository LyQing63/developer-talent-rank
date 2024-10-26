package com.talent.model.bo;

import com.talent.model.dto.User;
import lombok.Data;

import java.util.List;

@Data
public class DeveloperSearchResult {
    private User data;
    private Double ranking;
    private String predictNation;
    private List<String> domains;
}
