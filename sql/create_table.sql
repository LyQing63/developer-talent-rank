# 数据库初始化
#
#

-- 创建库
create database if not exists github_developer;

-- 切换库
use github_developer;

-- 用户表
create table if not exists user
(
    login       varchar(64)         not null comment '用户名',
    id          bigint              not null comment 'id' primary key,
    nodeId      varchar(64)         null comment 'node_id',
    avatarUrl   varchar(256)        null comment '头像地址',
    accountType varchar(32)         null comment '账户类别',
    accountName varchar(256)        null comment '账户名称',
    company     varchar(256)        null comment '公司',
    blog        varchar(256)        null comment '博客地址',
    location    varchar(256)        null comment '住址',
    email       varchar(256)        null comment '邮箱',
    hireable    tinyint default 0   null comment '是否在职, 0:不在职，1:在职',
    publicRepos int                null comment '公开仓库数',
    publicGists int                null comment '共享仓库数',
    accountFollowers int            null comment '粉丝数',
    accountFollowing int            null comment '偶像数',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',

    index idx_login (login),
    index idx_email (email)
) comment '用户' collate = utf8mb4_unicode_ci;

-- "bio": "There once was...","twitter_username": "monatheoctocat",