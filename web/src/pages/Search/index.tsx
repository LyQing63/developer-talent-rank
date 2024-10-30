import { AudioOutlined } from '@ant-design/icons';
import {ProList, ProTable} from '@ant-design/pro-components';
import type { GetProps } from 'antd';
import { Button, Input, Space, Tag } from 'antd';
import React, {useEffect, useState} from 'react';
import request from 'umi-request';
import styles from './index.less';
import {getDeveloper} from "@/services/ant-design-pro/userController";
import {getTotalRating} from "@/services/ant-design-pro/rankController";

type SearchProps = GetProps<typeof Input.Search>;

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
  const [ranks, setRanks] = useState<API.User[]>([]);

  // const MyTable: React.FC = ({ style }: MyTableProps) => (
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
    if (ranks.length < 10) {
      const getRankedUsers = () => {
        getTotalRating().then((r) => {
          if (r.code === 0) {
            const rankResult = r.data;
            for (let i = 0; i < 10; i++) {
              const id = rankResult[i].id
              getDeveloper({id: id}).then((rs) => {
                if (rs.code === 0) {
                  setRanks((preRanks) => [...preRanks, rs.data]);
                }
              });
            }
          }
        });
      }
      getRankedUsers();
    }
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
        <ProTable<API.User>
          // className={styles.mytable}
          toolBarRender={() => {
            return [
              <Button key="3" type="primary">
                新建
              </Button>,
            ];
          }}
          search={{}}
          metaTitle="accountname"
          rowKey="id"
          headerTitle="用户列表"
          dataSource={ranks}//原请求数据函数
          pagination={{
            pageSize: 10,
          }}
          onRow={(record) => ({
            onClick: () => handleRowClick(record), // 点击行时调用 handleRowClick
          })}
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
          ]}
        />
      </div>
    </div>
  );
};


export default App;
