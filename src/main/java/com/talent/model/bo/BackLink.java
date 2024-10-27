package com.talent.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackLink {
    private String type;
    private Boolean isExists;
}
