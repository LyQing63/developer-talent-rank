package com.talent.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Map;

import com.talent.model.bo.UserRating;
import com.talent.model.vo.RatingResultVO;
import lombok.Data;

/**
 * 开发者分析数据
 * @TableName developer_analysis
 */
@TableName(value ="developer_analysis")
@Data
public class DeveloperAnalysis implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 排名分数
     */
    private Integer totalranking;

    /**
     * bio 评分
     */
    private Integer biorating;

    /**
     * repo_description 评分
     */
    private Integer repodescriptionrating;

    /**
     * webpage 评分
     */
    private Integer webpagerating;

    /**
     * backlink 评分
     */
    private Integer backlinkrating;

    /**
     * 用户热度评分
     */
    private Integer userpopularity;

    /**
     * 仓库热度评分
     */
    private Integer repopopularity;

    /**
     * 住址置信度
     */
    private Integer locationconfidence;

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
        DeveloperAnalysis other = (DeveloperAnalysis) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTotalranking() == null ? other.getTotalranking() == null : this.getTotalranking().equals(other.getTotalranking()))
            && (this.getBiorating() == null ? other.getBiorating() == null : this.getBiorating().equals(other.getBiorating()))
            && (this.getRepodescriptionrating() == null ? other.getRepodescriptionrating() == null : this.getRepodescriptionrating().equals(other.getRepodescriptionrating()))
            && (this.getWebpagerating() == null ? other.getWebpagerating() == null : this.getWebpagerating().equals(other.getWebpagerating()))
            && (this.getBacklinkrating() == null ? other.getBacklinkrating() == null : this.getBacklinkrating().equals(other.getBacklinkrating()))
            && (this.getUserpopularity() == null ? other.getUserpopularity() == null : this.getUserpopularity().equals(other.getUserpopularity()))
            && (this.getRepopopularity() == null ? other.getRepopopularity() == null : this.getRepopopularity().equals(other.getRepopopularity()))
            && (this.getLocationconfidence() == null ? other.getLocationconfidence() == null : this.getLocationconfidence().equals(other.getLocationconfidence()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTotalranking() == null) ? 0 : getTotalranking().hashCode());
        result = prime * result + ((getBiorating() == null) ? 0 : getBiorating().hashCode());
        result = prime * result + ((getRepodescriptionrating() == null) ? 0 : getRepodescriptionrating().hashCode());
        result = prime * result + ((getWebpagerating() == null) ? 0 : getWebpagerating().hashCode());
        result = prime * result + ((getBacklinkrating() == null) ? 0 : getBacklinkrating().hashCode());
        result = prime * result + ((getUserpopularity() == null) ? 0 : getUserpopularity().hashCode());
        result = prime * result + ((getRepopopularity() == null) ? 0 : getRepopopularity().hashCode());
        result = prime * result + ((getLocationconfidence() == null) ? 0 : getLocationconfidence().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", totalranking=").append(totalranking);
        sb.append(", biorating=").append(biorating);
        sb.append(", repodescriptionrating=").append(repodescriptionrating);
        sb.append(", webpagerating=").append(webpagerating);
        sb.append(", backlinkrating=").append(backlinkrating);
        sb.append(", userpopularity=").append(userpopularity);
        sb.append(", repopopularity=").append(repopopularity);
        sb.append(", locationconfidence=").append(locationconfidence);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public static DeveloperAnalysis parseRatingResultVO(Long id, UserRating userRating, Integer totalScore) {

        DeveloperAnalysis developerAnalysis = new DeveloperAnalysis();
        developerAnalysis.setId(id);
        developerAnalysis.setTotalranking(totalScore);
        developerAnalysis.setBiorating(userRating.getRating().getBioRating());
        developerAnalysis.setRepodescriptionrating(userRating.getRating().getRepoDescriptionRating());
        developerAnalysis.setWebpagerating(userRating.getRating().getWebpageRating());
        developerAnalysis.setBacklinkrating(userRating.getRating().getBacklinkRating());
        developerAnalysis.setUserpopularity(userRating.getRating().getUserPopularity());
        developerAnalysis.setRepopopularity(userRating.getRating().getRepoPopularity());
        developerAnalysis.setLocationconfidence(null);

        return developerAnalysis;
    }
}