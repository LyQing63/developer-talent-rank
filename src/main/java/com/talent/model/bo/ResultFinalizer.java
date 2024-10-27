package com.talent.model.bo;

import com.talent.model.vo.RatingVO;

import java.util.ArrayList;
import java.util.List;

public class ResultFinalizer {

    public static List<RatingVO> finalizeResult(Rating rating, Suggestions suggestions) {
        List<RatingVO> result = new ArrayList<>();

        result.add(new RatingVO("User Popularity",
                "Your profile page tells people the story of your work through the repositories you're interested in, "
                        + "the contributions you've made, and the conversations you've had. "
                        + "<a href=\"https://docs.github.com/en/github/setting-up-and-managing-your-github-profile/customizing-your-profile/about-your-profile\">See More.</a>\n\n"
                        + "Typically, GitHub isn’t primary place decision-makers look to fill roles in programming. "
                        + "Rather, it’s a handy tool for fine-matching skills and verifying that a candidate possesses them. "
                        + "If you’re using GitHub for advancement, you will want to present your projects and account activity in a way that’s clear and accessible.",
                rating.getUserPopularity()));

        result.add(new RatingVO("Repository Description",
                "You should provide brief description about your repository.\n\n"
                        + "Nobody should spend their time going straight into the code and trying to understand what is the purpose from there. "
                        + "Instead, make sure that every repository you work on has the description field filled in. "
                        + "At the description part and the README file you want to talk about the features you wrote into the project. "
                        + "Demonstrate business acumen by articulating your role in the Git in one or two sentences that capture how you helped along the project. "
                        + "Think of Description as the headline, on which you’ll elaborate further in the ReadMe.",
                rating.getRepoDescriptionRating(), suggestions.getRepository()));

        result.add(new RatingVO("Biography",
                "Add a bio to your profile to share information about yourself with other GitHub users. "
                        + "<a href=\"https://docs.github.com/en/github/setting-up-and-managing-your-github-profile/customizing-your-profile/personalizing-your-profile#adding-a-bio-to-your-profile\">See More.</a>\n\n"
                        + "It is suggested to have a proper biography with at least 5 words - past and present employers, "
                        + "projects you have worked on, or languages and frameworks you enjoy using, or are currently learning. "
                        + "Include the type of company or kind of projects you would be interested in the future.",
                rating.getBioRating()));

        result.add(new RatingVO("Repository Popularity",
                "It's worth taking the time to sort your pinned repositories so that they make the best possible impression on the viewer.\n\n"
                        + "You can change the position in which they appear by dragging and dropping. "
                        + "When you click on each repository you can add a brief description that will be visible on your profile. "
                        + "So you want to add something that tells them a little bit about the project and piques their interest.",
                rating.getRepoPopularity()));

        result.add(new RatingVO("Backlinks & Information",
                "Provide general information about yourself such as what is your current company, email address and links to your portfolio, GitLab, CodePen, or blog.",
                rating.getBacklinkRating(), suggestions.getBacklinks()));

        result.add(new RatingVO("Providing Web Pages",
                "You can use GitHub Pages to host a website about yourself, your organization, or your project directly from a GitHub repository. "
                        + "<a href=\"https://docs.github.com/en/pages/getting-started-with-github-pages/about-github-pages\">See More.</a>\n\n"
                        + "It's important to provide a webpage if your repository is especially a library. "
                        + "You can use gitbook or similar tools to document your project or provide a demo of how it works.",
                rating.getWebpageRating()));

        List<RatingVO> partials = new ArrayList<>();

        partials.add(new RatingVO("License repositories",
                "You can include an open source license in your repository to make it easier for other people to contribute. "
                        + "<a href=\"https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/adding-a-license-to-a-repository\">See More.</a>",
                suggestions.getLicensing() != null && !suggestions.getLicensing().isEmpty() ? -1 : 100, suggestions.getLicensing(), true));

        partials.add(new RatingVO("Archive repositories that are no longer maintained",
                "You can archive a repository to make it read-only for all users and indicate that it's no longer actively maintained. "
                        + "<a href=\"https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/archiving-a-github-repository/archiving-repositories\">See More.</a>",
                suggestions.getArchive() != null && !suggestions.getArchive().isEmpty() ? -1 : 100, suggestions.getArchive(), true));

        result.addAll(partials);

        return result;
    }
    
}
