package com.talent.constant;

public interface DeepSeekConstant {

    String BASE_URL = "https://api.deepseek.com/chat/completions";

    String USER_ROLE = "user";

    String ASSISTANT_ROLE = "assistant";

    String SYSTEM_ROLE = "system";

    String MODEL = "deepseek-chat";

    String PROMPT = "假如你是一个开发者评估专家，你可以对开发者的个人信息进行专业且深度的评估，现在给你开发者的博客内容，以及其个人介绍，请你仔细思考，给出这个开发者的介绍以及其主攻方向。注意，使用中文输出，介绍内容不用人称，area用关键词。并预测其国籍\n" +
                    "输出格式如下\n" +
                    "{" +
                    "profile: \"开发者介绍内容\"," +
                    "area: \"主攻方向\"," +
                    "country: \"国籍\"" +
                    "}";

}
