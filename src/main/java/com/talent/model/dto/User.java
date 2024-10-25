package com.talent.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLogin() == null ? other.getLogin() == null : this.getLogin().equals(other.getLogin()))
            && (this.getNodeid() == null ? other.getNodeid() == null : this.getNodeid().equals(other.getNodeid()))
            && (this.getAvatarurl() == null ? other.getAvatarurl() == null : this.getAvatarurl().equals(other.getAvatarurl()))
            && (this.getAccounttype() == null ? other.getAccounttype() == null : this.getAccounttype().equals(other.getAccounttype()))
            && (this.getAccountname() == null ? other.getAccountname() == null : this.getAccountname().equals(other.getAccountname()))
            && (this.getCompany() == null ? other.getCompany() == null : this.getCompany().equals(other.getCompany()))
            && (this.getBlog() == null ? other.getBlog() == null : this.getBlog().equals(other.getBlog()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getHireable() == null ? other.getHireable() == null : this.getHireable().equals(other.getHireable()))
            && (this.getPublicRepos() == null ? other.getPublicRepos() == null : this.getPublicRepos().equals(other.getPublicRepos()))
            && (this.getPublicGists() == null ? other.getPublicGists() == null : this.getPublicGists().equals(other.getPublicGists()))
            && (this.getAccountfollowers() == null ? other.getAccountfollowers() == null : this.getAccountfollowers().equals(other.getAccountfollowers()))
            && (this.getAccountfollowing() == null ? other.getAccountfollowing() == null : this.getAccountfollowing().equals(other.getAccountfollowing()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLogin() == null) ? 0 : getLogin().hashCode());
        result = prime * result + ((getNodeid() == null) ? 0 : getNodeid().hashCode());
        result = prime * result + ((getAvatarurl() == null) ? 0 : getAvatarurl().hashCode());
        result = prime * result + ((getAccounttype() == null) ? 0 : getAccounttype().hashCode());
        result = prime * result + ((getAccountname() == null) ? 0 : getAccountname().hashCode());
        result = prime * result + ((getCompany() == null) ? 0 : getCompany().hashCode());
        result = prime * result + ((getBlog() == null) ? 0 : getBlog().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getHireable() == null) ? 0 : getHireable().hashCode());
        result = prime * result + ((getPublicRepos() == null) ? 0 : getPublicRepos().hashCode());
        result = prime * result + ((getPublicGists() == null) ? 0 : getPublicGists().hashCode());
        result = prime * result + ((getAccountfollowers() == null) ? 0 : getAccountfollowers().hashCode());
        result = prime * result + ((getAccountfollowing() == null) ? 0 : getAccountfollowing().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", login=").append(login);
        sb.append(", nodeid=").append(nodeid);
        sb.append(", avatarurl=").append(avatarurl);
        sb.append(", accounttype=").append(accounttype);
        sb.append(", accountname=").append(accountname);
        sb.append(", company=").append(company);
        sb.append(", blog=").append(blog);
        sb.append(", location=").append(location);
        sb.append(", email=").append(email);
        sb.append(", hireable=").append(hireable);
        sb.append(", publicRepos=").append(publicRepos);
        sb.append(", publicGists=").append(publicGists);
        sb.append(", accountfollowers=").append(accountfollowers);
        sb.append(", accountfollowing=").append(accountfollowing);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}