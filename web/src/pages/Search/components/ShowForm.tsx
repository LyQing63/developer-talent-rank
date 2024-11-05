import {ModalForm, ProCard, StatisticCard} from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import {Form, message} from 'antd';
import React, {useState} from 'react';
import {getDescription} from "@/services/ant-design-pro/aiController";
import { Spin } from 'antd'; // 确保导入 Spin 组件

const waitTime = (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, time);
  });
};

const { Statistic } = StatisticCard;


export default (props) => {
  const [form] = Form.useForm<{ name: string; company: string }>();
  const [responsive, setResponsive] = useState(false);
  const [description, setDescription] = useState({});
  const [loadingDescription, setLoadingDescription] = useState<boolean>(true); // 新增加载状态

  const fetchDescription = async (id) => {
    const response = await getDescription({id: id});
    console.log(response)
    if (response.code === 0) {
      // 检查 country 是否匹配 filterValue
        setDescription(response.data);
        return true;
    }
    return false
  }

  return (
    <ModalForm<{
      name: string;
      company: string;
    }>
      open={props.showFormVisible}
      submitter={false}
      onOpenChange={(visible) => {
        props.setShowFormVisible(visible);
        if (props.taskId !== null && props.taskId !== undefined) {
          const fetchDescriptions = async () => {
            let i = 0;
            for (; i < 5; i++) {
              const result = await fetchDescription(props.taskId);
              if (result) {
                setLoadingDescription(false); // 结束加载
                break; // 如果返回true，终止循环
              }

              console.log(result)
              await new Promise((resolve) => {
                setTimeout(resolve, 3000);
              }); // 等待1秒
            }
          };
          fetchDescriptions();
        }
      }}
      autoFocusFirstInput
      modalProps={{
        destroyOnClose: true,
        onCancel: (e) => {
          e.stopPropagation(); // 阻止事件冒泡
          console.log('Modal closed');
        },
      }}
    >
      {' '}
      <RcResizeObserver
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
                      src={description.avatarurl || 'https://via.placeholder.com/40'} // 占位符图像
                      alt="头像"
                      style={{ width: 80, height: 80, borderRadius: '50%', marginRight: 8 }}
                    />
                    <span
                      style={{
                        fontSize: '30px', lineHeight: '1.5'
                      }}
                    >{description.login || '暂无相关信息'}</span>
                  </div>
                }
              />

            </ProCard>
            <StatisticCard
              chart={
                <div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>公司/所属组织</div>
                  <span style={{ fontSize: '16px' }}>
                        {description.company || '暂无相关信息'}
                </span>
                </div>
              }
            />

            <ProCard split="vertical">
              <StatisticCard
                chart={
                  <div>
                    <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>博客地址</div>
                    {description.blog ? (
                      <a href={description.blog} target="_blank" rel="noopener noreferrer" style={{ fontSize: '16px' }}>
                        {description.blog}
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
                        {description.area || '暂无相关信息'}
                    </span>
                  </div>
                }
              />

              <StatisticCard
                chart={
                  <div>
                    <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>国籍</div>
                    <span style={{ fontSize: '16px' }}>
                        {description.country || '暂无相关信息'}
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
                       {description.profile || '暂无相关信息'}
                  </span>
                </div>
              }
            />


          </ProCard>
          <StatisticCard
            loading={loadingDescription} // 控制加载状态
            chart={

              loadingDescription ? ( // 当加载时显示占位符

                <div style={{ textAlign: 'center' }}>正在加载...</div>
              ) : (
                <img
                  src={`https://github-readme-stats.vercel.app/api?username=${description.login}&show=reviews,discussions_started,discussions_answered,prs_merged,prs_merged_percentage&locale=cn&card_width=500px`}
                  width="100%"
                />
              )
            }
          />

        </ProCard>
      </RcResizeObserver>
    </ModalForm>
  );
};

