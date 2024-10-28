package com.talent.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RatingResultVO implements Serializable {
    private List<RatingVO> ratingResults;
    private Integer totalScore;
}
