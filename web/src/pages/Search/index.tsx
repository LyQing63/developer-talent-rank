import React from 'react';
import { AudioOutlined } from '@ant-design/icons';
import { Input, Space } from 'antd';
import type { GetProps } from 'antd';
import styles from './index.less';
type SearchProps = GetProps<typeof Input.Search>;
import { ProList } from '@ant-design/pro-components';
import { Button, Tag } from 'antd';
import request from 'umi-request';

type GithubIssueItem = {
  url: string;
  id: number;
  number: number;
  title: string;
  labels: {
    name: string;
    color: string;
  }[];
  state: string;
  comments: number;
  created_at: string;
  updated_at: string;
  closed_at?: string;
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
  <Space direction="vertical"
         style={{
           position: 'absolute',
           top: '15%',
           left: '50%',
           transform: 'translate(-50%, -50%)'
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

const MyTable: React.FC = () => (
  <ProList<GithubIssueItem>
    toolBarRender={() => {
      return [
        <Button key="3" type="primary">
          新建
        </Button>,
      ];
    }}
    search={{}}
    rowKey="name"
    headerTitle="基础列表"
    request={async (params = {} as Record<string, any>) =>
      request<{
      data: GithubIssueItem[];
    }>('https://proapi.azurewebsites.net/github/issues', {
      params,
    })
    }
    pagination={{
      pageSize: 5,
    }}
    showActions="hover"
    metas={{
      title: {
        dataIndex: 'user',
        title: '用户',
      },
      avatar: {
        dataIndex: 'avatar',
        search: false,
      },
      description: {
        dataIndex: 'title',
        search: false,
      },
      subTitle: {
        dataIndex: 'labels',
        render: (_, row) => {
          return (
            <Space size={0}>
              {row.labels?.map((label: { name: string }) => (
                <Tag color="blue" key={label.name}>
                  {label.name}
                </Tag>
              ))}
            </Space>
          );
        },
        search: false,
      },
      actions: {
        render: (text, row) => [
          <a
            href={row.url}
            target="_blank"
            rel="noopener noreferrer"
            key="link"
          >
            链路
          </a>,
          <a
            href={row.url}
            target="_blank"
            rel="noopener noreferrer"
            key="warning"
          >
            报警
          </a>,
          <a
            href={row.url}
            target="_blank"
            rel="noopener noreferrer"
            key="view"
          >
            查看
          </a>,
        ],
        search: false,
      },
      status: {
        // 自己扩展的字段，主要用于筛选，不在列表中显示
        title: '状态',
        valueType: 'select',
        valueEnum: {
          all: { text: '全部', status: 'Default' },
          open: {
            text: '未解决',
            status: 'Error',
          },
          closed: {
            text: '已解决',
            status: 'Success',
          },
          processing: {
            text: '解决中',
            status: 'Processing',
          },
        },
      },
    }}
  />
)

const App: React.FC = () => {
  const handleSearch = (value: string) => {
    console.log('搜索内容:', value);
    // 这里可以添加你需要的搜索逻辑
  };

  return (
    <div style={{ position: 'relative', height: '100vh' }}>
      <MySearch onSearch={handleSearch} />
      <MyTable />
    </div>
  );
};

export default App;
