package com.talent.model.bo;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class LicenseBo {

    private String key;
    public LicenseBo(JSONObject license) {
        if (license == null) {
            this.key = null;
        } else {
            this.key = license.getStr("key");
        }

    }
}
