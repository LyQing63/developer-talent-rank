package com.talent.model.bo;

import com.talent.model.vo.RatingVO;

import java.util.ArrayList;
import java.util.List;

public class ResultFinalizer {

    public static List<RatingVO> finalizeResult(Rating rating, Suggestions suggestions) {
        List<RatingVO> result = new ArrayList<>();

        // 用户受欢迎程度
        result.add(new RatingVO("用户受欢迎程度",
                "您的个人资料页面通过您感兴趣的仓库、您所做的贡献以及您参与的对话，讲述了您工作的故事。"
                        + "<a href=\"https://docs.github.com/en/github/setting-up-and-managing-your-github-profile/customizing-your-profile/about-your-profile\">查看更多。</a>\n\n"
                        + "通常，GitHub 不是决策者寻找编程角色的主要地方。"
                        + "相反，它是一个有用的工具，可以精确匹配技能并验证候选人是否具备这些技能。"
                        + "如果您希望通过 GitHub 进行职业发展，您需要以清晰易懂的方式展示您的项目和账户活动。",
                (int) (rating.getUserPopularity() * 1.5)));

        // 仓库描述
        result.add(new RatingVO("仓库描述",
                "您应该为您的仓库提供简要描述。\n\n"
                        + "没人应该花时间直接查看代码并试图理解其目的。"
                        + "相反，请确保您所做的每个仓库都有填写描述字段。"
                        + "在描述部分和 README 文件中，您想要谈论您在项目中编写的功能。"
                        + "通过用一两句话清晰地描述您在 Git 中的角色，展示您的商业头脑，说明您是如何帮助推动项目的。"
                        + "将描述视为标题，您将在 ReadMe 中进一步详细说明。",
                (int) (rating.getRepoDescriptionRating() * 0.5), suggestions.getRepository()));

        // 个人简介
        result.add(new RatingVO("个人简介",
                "在您的个人资料中添加个人简介，以便与其他 GitHub 用户分享有关您的信息。"
                        + "<a href=\"https://docs.github.com/en/github/setting-up-and-managing-your-github-profile/customizing-your-profile/personalizing-your-profile#adding-a-bio-to-your-profile\">查看更多。</a>\n\n"
                        + "建议拥有至少 5 个字的个人简介，包括过去和现在的雇主、"
                        + "您参与过的项目，或您喜欢使用的编程语言和框架，或您正在学习的内容。"
                        + "包括您未来感兴趣的公司类型或项目类型。",
                (int) (rating.getBioRating() * 0.7)));

        // 仓库受欢迎程度
        result.add(new RatingVO("仓库受欢迎程度",
                "花时间对您固定的仓库进行排序，以便在查看者面前留下最佳印象是值得的。\n\n"
                        + "您可以通过拖放更改它们的显示位置。"
                        + "点击每个仓库时，您可以添加一个简短的描述，该描述将在您的个人资料中可见。"
                        + "因此，您要添加一些内容，让他们了解项目的一些信息并引起他们的兴趣。",
                (int) (rating.getRepoPopularity() * 1.5)));

        // 反向链接和信息
        result.add(new RatingVO("反向链接和信息",
                "提供有关您自己的基本信息，例如您目前的公司、电子邮件地址以及您的作品集、GitLab、CodePen 或博客的链接。",
                (int) ((rating.getBacklinkRating() + 60) * 0.5), suggestions.getBacklinks()));

        // 提供网页
        result.add(new RatingVO("提供网页",
                "您可以使用 GitHub Pages 来托管有关您自己、您的组织或您的项目的网站，"
                        + "直接来自 GitHub 仓库。"
                        + "<a href=\"https://docs.github.com/en/pages/getting-started-with-github-pages/about-github-pages\">查看更多。</a>\n\n"
                        + "如果您的仓库对你而言意义非凡，提供网页非常重要。"
                        + "您可以使用 gitbook 或类似工具来记录您的项目或提供其工作演示。",
                (int) (rating.getWebpageRating() * 0.7)));

        List<RatingVO> partials = new ArrayList<>();

        // 许可证仓库
        partials.add(new RatingVO("添加开源许可证",
                "您可以在仓库中包含开源许可证，以便其他人更容易地进行贡献。"
                        + "<a href=\"https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/adding-a-license-to-a-repository\">查看更多。</a>",
                suggestions.getLicensing() != null && !suggestions.getLicensing().isEmpty() ? 0 : 100, suggestions.getLicensing(), true));

        // 存档不再维护的仓库
        partials.add(new RatingVO("存档不再维护的仓库",
                "您可以存档一个仓库，使其对所有用户只读，并表明该仓库不再积极维护。"
                        + "<a href=\"https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/archiving-a-github-repository/archiving-repositories\">查看更多。</a>",
                suggestions.getArchive() != null && !suggestions.getArchive().isEmpty() ? 0 : 100, suggestions.getArchive(), true));

        result.addAll(partials);

        return result;
    }
}
