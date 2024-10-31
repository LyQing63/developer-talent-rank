import { AudioOutlined } from '@ant-design/icons';
import {ProList, ProTable} from '@ant-design/pro-components';
import type { GetProps } from 'antd';
import { Button, Input, Space, Tag } from 'antd';
import React, {useEffect, useState} from 'react';
import request from 'umi-request';
import styles from './index.less';
import {getDeveloper} from "@/services/ant-design-pro/userController";
import {getTotalRating} from "@/services/ant-design-pro/rankController";
import user from "../../../mock/user";
import Footer from "@/components/Footer";

type SearchProps = GetProps<typeof Input.Search>;

type RankedUser= {
  id?: number;
  login?: string;
  nodeid?: string;
  avatarurl?: string;
  accounttype?: string;
  accountname?: string;
  company?: string;
  blog?: string;
  bio?: string;
  location?: string;
  email?: string;
  hireable?: number;
  publicRepos?: number;
  publicGists?: number;
  accountfollowers?: number;
  accountfollowing?: number;
  createtime?: string;
  updatetime?: string;
  score: number
};


const { Search: Index } = Input;

const suffix = (
  <AudioOutlined
    style={{
      fontSize: 16,
      color: '#1677ff',
    }}
  />
);

const onSearch: SearchProps['onSearch'] = (value, _e, info) => console.log(info?.source, value);

const MySearch: React.FC = () => (
  <Space
    direction="vertical"
    style={{
      display: 'flex',
      justifyContent: 'center', // 水平居中
      alignItems: 'center',     // 垂直居中
      height: '15vh',          // 让父容器高度占满视口
    }}
  >
    <Index
      placeholder="Search一下，你就知道"
      enterButton="Search"
      // size="large"
      style={{
        display: 'flex',
        justifyContent: 'space-between',
      }}
      suffix={suffix}
      onSearch={onSearch}
      className={styles.myinput}
    />
  </Space>
);


const App: React.FC = () => {
  const [ranks, setRanks] = useState<RankedUser[]>([]);
  const [loading, setLading] = useState<boolean>(true);
  // const MyTable: React.FC = ({ style }: MyTableP rops) => (
  //
  // );


  const handleSearch = (value: string) => {
    console.log('搜索内容:', value);
    // 这里可以添加你需要的搜索逻辑
  };

  const handleRowClick = (record: API.User) => {
    if (record.login) {
      window.open(`https://github.com/${record.login}`, '_blank'); // 在新标签页打开 GitHub 用户页面
    }
  };

  useEffect(() => {

      const fetchUsers = async () => {

        const response = await getTotalRating();

        if (response.code === 0) {
          const rankResult = response.data;

          const userPromises = rankResult.map(async (rank: any) => {
            const devResponse = await getDeveloper({ id: rank.id });
            return {
              ...devResponse.data,
              score: rank.totalranking,
            } as RankedUser;
          });
          // 等待所有 Promise 完成
          const users = await Promise.all(userPromises);

          // 根据 score 降序排序
          const sortedUsers = users.sort((a, b) => b.score - a.score);
          setRanks(sortedUsers); // 更新状态
          setLading(false)
        }
      }
    fetchUsers()
  }, []);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      justifyContent: 'flex-start',
      overflow: 'auto' ,
    }}>
      <div>
        <MySearch onSearch={handleSearch} />
      </div>

      <div
        style={{
          width: '85%',
          margin: '0 auto'
        }}
      >
        <ProTable<RankedUser>
          // className={styles.mytable}
          toolBarRender={() => {
            return [
              <Button key="3" type="primary">
                新建
              </Button>,
            ];
          }}
          onRow={(record) => ({
            onClick: () => handleRowClick(record),
          })}
          loading={loading} // 控制加载状态
          search={{}}
          metaTitle="accountname"
          rowKey="score"
          headerTitle="用户列表"
          dataSource={ranks}//原请求数据函数
          pagination={false}
          columns={[
            {
              title: '头像',
              dataIndex: 'avatarurl',
              render: (avatarurl) => <img src={avatarurl} alt="头像" style={{ width: 40, borderRadius: '50%' }} />,
            },
            {
              title: '用户名',
              dataIndex: 'login',
            },
            {
              title: '公司',
              dataIndex: 'company',
            },
            {
              title: '公开仓库数',
              dataIndex: 'publicRepos',
            },
            {
              title:'TA关注的',
              dataIndex:'accountfollowing',
            },
            {
              title:'TA的粉丝',
              dataIndex: 'accountfollowing',
            },
            {
              title:'分数',
              dataIndex: 'score',
            }
          ]}
        />
      </div>
      <Footer />
    </div>
  );
};


export default App;
