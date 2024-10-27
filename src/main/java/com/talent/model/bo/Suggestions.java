package com.talent.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Suggestions {
    private List<String> repository;
    private List<String> backlinks;
    private List<String> licensing;
    private List<String> archive;
}
