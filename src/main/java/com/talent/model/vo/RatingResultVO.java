package com.talent.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class RatingResultVO {
    private List<RatingVO> ratingResults;
    private Integer totalScore;
}
