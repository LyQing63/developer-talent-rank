package com.talent.model.bo;

import lombok.Data;

@Data
public class Rating {

    private Boolean bioExists;
    private Boolean companyExists;
    private Integer bioRating = 0;
    private Boolean locExists;
    private Boolean blogExists;
    private Integer userPopularity;
    private Integer repoPopularity;
    private Integer repoDescriptionRating = 0;
    private Integer webpageRating = 0;
    private Integer totalForks;
    private Integer totalStars;
    private Integer repoCount;
    private Integer backlinkRating = 0;

}
