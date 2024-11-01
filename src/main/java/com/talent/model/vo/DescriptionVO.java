package com.talent.model.vo;

import cn.hutool.json.JSONObject;
import com.talent.model.dto.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DescriptionVO implements Serializable {
    private Long id;

    /**
     * 用户名
     */
    private String login;

    /**
     * node_id
     */
    private String nodeid;

    /**
     * 头像地址
     */
    private String avatarurl;

    /**
     * 账户类别
     */
    private String accounttype;

    /**
     * 账户名称
     */
    private String accountname;

    /**
     * 公司
     */
    private String company;

    /**
     * 博客地址
     */
    private String blog;

    private String bio;

    private String profile;

    private String area;

    private String country;

    /**
     * 住址
     */
    private String location;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否在职, 0:不在职，1:在职
     */
    private Integer hireable;

    /**
     * 公开仓库数
     */
    private Integer publicRepos;

    /**
     * 共享仓库数
     */
    private Integer publicGists;

    /**
     * 粉丝数
     */
    private Integer accountfollowers;

    /**
     * 偶像数
     */
    private Integer accountfollowing;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    public static DescriptionVO parseDescriptionVO(User developer, JSONObject description) {

        DescriptionVO descriptionVO = new DescriptionVO();
        descriptionVO.setId(developer.getId());
        descriptionVO.setLogin(developer.getLogin());
        descriptionVO.setNodeid(developer.getNodeid());
        descriptionVO.setAvatarurl(developer.getAvatarurl());
        descriptionVO.setAccounttype(developer.getAccounttype());
        descriptionVO.setAccountname(developer.getAccountname());
        descriptionVO.setCompany(developer.getCompany());
        descriptionVO.setBlog(developer.getBlog());
        descriptionVO.setBio(developer.getBio());
        descriptionVO.setProfile(description.getStr("profile"));
        descriptionVO.setArea(description.getStr("area"));
        descriptionVO.setCountry(description.getStr("country"));
        descriptionVO.setLocation(developer.getLocation());
        descriptionVO.setEmail(developer.getEmail());
        descriptionVO.setHireable(developer.getHireable());
        descriptionVO.setPublicRepos(developer.getPublicRepos());
        descriptionVO.setPublicGists(developer.getPublicGists());
        descriptionVO.setAccountfollowers(developer.getAccountfollowers());
        descriptionVO.setAccountfollowing(developer.getAccountfollowing());
        descriptionVO.setCreatetime(developer.getCreatetime());
        descriptionVO.setUpdatetime(developer.getUpdatetime());

        return descriptionVO;
    }

}
