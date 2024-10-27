package com.talent.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class RatingVO {
    private String name;
    private String message;
    private Integer score;
    private List<String> suggestions;
    private Boolean partial = false;
    public RatingVO(String name, String message, Integer score) {
        this.name = name;
        this.message = message;
        this.score = score;
    }

    public RatingVO(String name, String message, Integer score, List<String> suggestions) {
        this.name = name;
        this.message = message;
        this.score = score;
        this.suggestions = suggestions;
    }

    public RatingVO(String name, String message, Integer score, List<String> suggestions, Boolean partial) {
        this.name = name;
        this.message = message;
        this.score = score;
        this.partial = partial;
        this.suggestions = suggestions;
    }
}
