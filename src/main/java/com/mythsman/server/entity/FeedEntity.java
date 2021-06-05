package com.mythsman.server.entity;

import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.util.JsonUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "feed")
public class FeedEntity implements Serializable {
    private static final long serialVersionUID = -4327849863180688237L;

    /**
     * 自增ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    /**
     * 唯一ID
     */
    private String uuid;

    /**
     * 域名
     */
    private String host;

    /**
     * 博客名
     */
    private String title;

    /**
     * 子标题
     */
    private String subTitle;

    /**
     * rss订阅路径
     */
    private String feedPath;

    /**
     * rss类型
     *
     * @see FeedTypeEnum
     */
    private Integer feedType;

    /**
     * 最近更新时间
     */
    private Date lastModified;

    /**
     * 框架
     */
    private String generator;

    /**
     * 状态
     *
     * @see FeedStatusEnum
     */
    private Integer status;

    /**
     * 最近检测时间（ms）
     */
    @CreatedDate
    private Date lastCheckTime;

    /**
     * 更新时间
     */
    @CreatedDate
    private Date gmtCreate;

    /**
     * 创建时间
     */
    @LastModifiedDate
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String hostName) {
        this.host = hostName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getFeedType() {
        return feedType;
    }

    public void setFeedType(Integer rssType) {
        this.feedType = rssType;
    }

    public String getFeedPath() {
        return feedPath;
    }

    public void setFeedPath(String rssPath) {
        this.feedPath = rssPath;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
