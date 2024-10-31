package com.talent.constant;

public interface DeepSeekConstant {

    String BASE_URL = "https://api.deepseek.com/chat/completions";

    String USER_ROLE = "user";

    String ASSISTANT_ROLE = "assistant";

    String SYSTEM_ROLE = "system";

    String MODEL = "deepseek-chat";

    String PROMPT = "你是一个开发者评估专家，现在给你开发者的博客内容，以及一些个人介绍，请给出这个开发者的介绍以及其主攻方向，介绍内容不用人称，area用关键词。\n" +
                    "输出格式如下\n" +
                    "{\n" +
                    "profile: \"开发者介绍内容\"\n" +
                    "area: \"主攻方向\"\n" +
                    "}";

}
