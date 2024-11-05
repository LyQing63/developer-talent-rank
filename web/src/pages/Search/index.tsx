import { AudioOutlined, PlusOutlined } from '@ant-design/icons';
import { ModalForm, ProTable } from '@ant-design/pro-components';
import {GetProps, message} from 'antd';
import { Button, Input, Space, Tag} from 'antd';
import React, { useEffect, useState } from 'react';
import styles from './index.less';
import { getDeveloper } from '@/services/ant-design-pro/userController';
import {getTotalRating, getTotalRatingByParam} from '@/services/ant-design-pro/rankController';
import Footer from '@/components/Footer';
import { ProCard, StatisticCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import ShowForm from "@/pages/Search/components/ShowForm";
import {createDescription} from "@/services/ant-design-pro/aiController";
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

type History= {
  login?: string;
  taskId?: number;
}

const { Search: Index } = Input;
const { Statistic } = StatisticCard;

const suffix = (
  <AudioOutlined
    style={{
      fontSize: 16,
      color: '#1677ff',
    }}
  />
);


const MySearch: React.FC = (props) => (
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
      onSearch={props.onSearch}
      className={styles.myinput}
    />

  </Space>

);



const App: React.FC = () => {
  const [ranks, setRanks] = useState<RankedUser[]>([]);
  const [loading, setLading] = useState<boolean>(true);
  const [responsive, setResponsive] = useState(false);
// 新增：用于控制 ShowForm 组件的显示状态
  const [showFormVisible, setShowFormVisible] = useState<boolean>(false); // 控制 ShowForm 显示的状态
  const [taskId, setTaskId] = useState<string>();
  const [searchHistory, setSearchHistory] = useState<History[]>([]); // 存储搜索历史记录
  const [searchValue, setSearchValue] = useState<string>(''); // 管理搜索框内容
  const [filterValue, setFilterValue] = useState<string>(''); // 新增状态变量，用于存储筛选框内容

  // 更新筛选框输入值
  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterValue(e.target.value);
  };

  // 筛选框的确定按钮点击事件
  // const handleFilterConfirm = () => {
  //   if (!filterValue.trim()) {
  //     message.info('开启全局搜索'); // 提示全局搜索
  //     setFilterValue(''); // 清空筛选值
  //   } else {
  //     message.info(`设置筛选条件国家/地区为 ${filterValue}`); // 提示筛选条件
  //   }
  // };

  // const fetchUsers = async () => {
  //   setLading(true); // 设置加载状态
  //   try {
  //     const response = await getTotalRating();
  //     if (response.code === 0) {
  //       const rankResult = response.data;
  //
  //       const userPromises = rankResult.map(async (rank: any) => {
  //         const devResponse = await getDeveloper({ id: rank.id });
  //         return {
  //           ...devResponse.data,
  //           score: rank.totalranking,
  //         } as RankedUser;
  //       });
  //       const users = await Promise.all(userPromises);
  //       const sortedUsers = users.sort((a, b) => b.score - a.score);
  //       setRanks(sortedUsers); // 更新数据
  //     } else {
  //       message.error('获取数据失败');
  //     }
  //   } catch (error) {
  //     message.error('请求出错，请检查网络');
  //     console.error(error);
  //   } finally {
  //     setLading(false); // 关闭加载状态
  //   }
  // };

  const fetchUsers = async () => {
    setRanks([])
    const response = await getTotalRating();
    console.log(response)
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


  const handleFilterConfirm = async () => {
    if (!filterValue.trim()) {
      message.info('开启全局搜索');
      fetchUsers();
    } else {
      message.info(`设置筛选条件国家/地区为 ${filterValue}`);
      setLading(true);
      setRanks([]);
      try {

        // 构建请求参数
        const params = { param: 'nation', value: filterValue }; // 使用正确的参数结构
        console.log('请求参数:', params); // 输出请求参数

        // 使用筛选条件请求数据
        const response = await getTotalRatingByParam(params);
        console.log('后端返回的响应:', response); // 输出后端返回的完整响应

        console.log(filterValue)
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
        } else {
          message.error('请求筛选数据失败');
        }
      } catch (error) {
        message.error('请求出错，请检查网络');
        console.error(error);
      } finally {
        setLading(false);
      }
    }
  };




  const handleSearch = async (value: string) => {
    console.log('搜索内容:', value);
    // 这里可以添加你需要的搜索逻辑
    if (!value.trim()) {
      message.warning('请键入要查询的用户'); // 提示用户输入内容
      return;
    }
    const existingHistory = searchHistory.find(item => item.login === value);    // 检查搜索历史中是否已存在搜索内容
    if (!existingHistory){
      try {
        // 调用 createDescription 函数
        const response = await createDescription({ login: value });
        if (response.code === 0) {
          const id = response.data; // 假设返回的数据中包含 taskId
          console.log('任务 ID:', id);
          // 更新搜索历史
          setSearchHistory([...searchHistory, { login: value, taskId: id }]);
          setSearchValue(''); // 清空搜索框内容
          setTaskId(id);
          setShowFormVisible(true); // 点击搜索按钮后显示 ModalForm
        } else {
          message.error('请求失败，请重试'); // 处理请求失败的情况
        }
      } catch (error) {
        message.error('请求出错，请检查网络'); // 处理请求错误
        console.error(error);
      }

    }
    else {
      const taskId = existingHistory.taskId;
      setTaskId(taskId);
      setShowFormVisible(true); // 点击搜索按钮后显示 ModalForm
      // 如果需要可以直接使用 existingHistory.taskId
    };




  };
  // 处理点击历史记录标签的搜索
  const handleHistorySearch = (item: History) => {
    // console.log(item)
    setSearchValue(item.login); // 更新搜索框内容
    setTaskId(item.taskId);
    setShowFormVisible(true);
    // 这里可以执行重新搜索的逻辑，例如通过 taskId 获取更多信息
  };

  const handleRowClick = (record: API.User) => {
    if (record.login) {
      window.open(`https://github.com/${record.login}`, '_blank'); // 在新标签页打开 GitHub 用户页面
    }
  };



  useEffect(() => {

    fetchUsers()
  }, []);

  console.log(ranks)

  return (

    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      justifyContent: 'flex-start',
      overflow: 'auto',
    }}>
      {showFormVisible &&
        <ShowForm showFormVisible={showFormVisible} setShowFormVisible={setShowFormVisible} taskId={taskId} filterValue={filterValue} />}
      <div>
        <MySearch onSearch={handleSearch}/>
      </div>
      <div style={{marginLeft: '15vh', display: 'flex', flexWrap: 'wrap', gap: '8px'}}>
        {searchHistory.map((item, index) => (
          <Button
            key={index}
            type="default"
            onClick={() => handleHistorySearch(item)} // 点击历史记录再次搜索
            onMouseEnter={(e) => e.currentTarget.style.cursor = 'pointer'}
            onMouseLeave={(e) => e.currentTarget.style.cursor = 'default'}
          >
            {item.login}
            <span
              style={{marginLeft: 8, color: 'red', cursor: 'pointer'}}
              onClick={(e) => {
                e.stopPropagation(); // 阻止按钮点击事件
                const updatedHistory = searchHistory.filter((_, i) => i !== index);
                setSearchHistory(updatedHistory); // 删除历史记录
              }}
            >
              × {/* 关闭图标，可以替换为更合适的图标 */}
            </span>
          </Button>
        ))}
      </div>
      <div>
        <Space
          style={{
            display: 'flex',
            justifyContent: 'right', // 水平居中
            marginRight: '20vh',
            alignItems: 'center',     // 垂直居中
            height: '5vh',          // 让父容器高度占满视口
          }}
        >
          <Index
            placeholder="请输入要筛选的国家/地区"
            enterButton="确定"
            size="small"
            style={{
              display: 'flex',
              justifyContent: 'space-between',

            }}
            allowClear
            value={filterValue} // 绑定状态变量
            onChange={handleFilterChange} // 更新筛选框内容
            onSearch={handleFilterConfirm} // 确定按钮点击事件
            className={styles.myselect}
          />

        </Space>
      </div>
      <div
        style={{
          width: '85%',
          margin: '0 auto'
        }}
      >
        <ProTable<RankedUser>

          onRow={(record) => ({
            onClick: () => handleRowClick(record),
          })}
          loading={loading} // 控制加载状态
          search={false} // 隐藏整个搜索栏
          // metaTitle="accountname"
          rowKey="score"
          headerTitle="用户列表"
          dataSource={ranks}//原请求数据函数
          pagination={false}
          columns={[
            {
              title: '头像',
              dataIndex: 'avatarurl',
              render: (avatarurl) => <img src={avatarurl} alt="头像" style={{width: 40, borderRadius: '50%'}}/>,
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
              title: 'TA关注的',
              dataIndex: 'accountfollowing',
            },
            {
              title: 'TA的粉丝',
              dataIndex: 'accountfollowing',
            },
            {
              title: '分数',
              dataIndex: 'score',
            },
            {
              title: '操作',
              dataIndex: 'action',
              render: (_, record) => (
                <ModalForm<{
                  name: string;
                  company: string;
                }>

                  title="新建表单"
                  trigger={
                    <Button
                      type="primary"
                      onClick={(e) => {
                        e.stopPropagation(); // 阻止事件冒泡

                      }}
                    >
                      <PlusOutlined/>
                      查看详细信息
                    </Button>
                  }
                  autoFocusFirstInput
                  modalProps={{
                    destroyOnClose: true,
                    onCancel: (e) => {
                      e.stopPropagation(); // 阻止事件冒泡
                      console.log('Modal closed');
                    },
                  }}
                  submitTimeout={2000}
                > <RcResizeObserver
                  key="resize-observer"
                  onResize={(offset) => {
                    setResponsive(offset.width < 596);
                  }}
                >
                  <ProCard split="horizontal">
                    <ProCard split="horizontal">
                      <ProCard split="vertical">
                        <StatisticCard
                          chart={
                            <div style={{ display: 'flex', alignItems: 'center' }}>
                              <img
                                src={record.avatarurl || 'https://via.placeholder.com/40'} // 占位符图像
                                alt="头像"
                                style={{ width: 80, height: 80, borderRadius: '50%', marginRight: 8 }}
                              />
                              <span
                                style={{
                                  fontSize: '30px', lineHeight: '1.5'
                                }}
                              >{record.login || '暂无相关信息'}</span>
                            </div>
                          }
                        />

                      </ProCard>
                      <StatisticCard
                        chart={
                          <div>
                            <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>公司/所属组织</div>
                            <span style={{ fontSize: '16px' }}>
                                {record.company || '暂无相关信息'}
                            </span>
                          </div>
                        }
                      />

                      <ProCard split="vertical">
                        <StatisticCard
                          chart={
                            <div>
                              <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>博客地址</div>
                              {record.blog ? (
                                <a href={record.blog} target="_blank" rel="noopener noreferrer" style={{ fontSize: '16px' }}>
                                  {record.blog}
                                </a>
                              ) : (
                                <span style={{ fontSize: '16px' }}>暂无相关信息</span>
                              )}
                            </div>
                          }
                        />

                        <StatisticCard
                          chart={
                            <div>
                              <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>开发技术领域</div>
                                <span style={{ fontSize: '16px' }}>
                                  {record.area || '暂无相关信息'}
                                </span>
                              </div>
                          }
                        />

                        <StatisticCard
                          chart={
                            <div>
                              <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>国籍</div>
                              <span style={{ fontSize: '16px' }}>
                                  {record.country || '暂无相关信息'}
                              </span>
                            </div>
                          }
                        />

                      </ProCard>
                    </ProCard>

                    <ProCard split="horizontal">
                      <StatisticCard
                        chart={
                          <div>
                            <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>简介</div>
                            <span style={{ fontSize: '16px' }}>
                                {record.profile || '暂无相关信息'}
                            </span>
                          </div>
                        }
                      />


                    </ProCard>
                    <StatisticCard
                      chart={
                          <img
                            src={`https://github-readme-stats.vercel.app/api?username=${record.login}&show=reviews,discussions_started,discussions_answered,prs_merged,prs_merged_percentage&locale=cn&card_width=500px`}
                            width="100%"
                          />

                      }
                    />

                  </ProCard>
                </RcResizeObserver>
                </ModalForm>
              ),
            },
          ]}
        />
      </div>
      <Footer/>
    </div>
  );
};


export default App;
