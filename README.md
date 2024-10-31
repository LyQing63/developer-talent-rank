# Developer talent rank - 一个 github 账户衡量工具

一个开源的 github 账户评级工具。通过使用 github 官方 api 接口爬取数据以及开发者提供给的公开账户，并对这些数据进行 ai 分析，得到对这个开发者的技能描述以及所在国家。
<a href="https://www.usememos.com">Home Page</a> •
<a href="https://demo.usememos.com/">Live Demo</a>

![demo](https://www.usememos.com/demo.png)

## 主要功能

- **Github 一键登录** ✍️: 支持 Github 用户一键登录，并可以查看自己的分数信息。
- **查询用户分数** 🏠: 通过 github api 查询到对应用户名数据，并对其分析，返回图文结果。
- **国家地区推测** 🤲: 通过开发者公开的关系网络，使用聚类算法，估计开发者国家地区。
- **AI 分析** 🧩: 爬取开发者公开信息，使用 DeepSeek 大模型平台，评估这个开发者技能水平。

## 快速搭建项目

### 申请 Github auoth

### 开通 Github token

### 配置项目

### 使用 docker 一键启动前后端

```bash
docker run -d --name memos -p 5230:5230 -v ~/.memos/:/var/opt/memos neosmemo/memos:stable
```

### 后端手动启动

### 前端手动启动
