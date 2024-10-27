// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /rank/score */
export async function getScore(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getScoreParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponse>('/api/rank/score', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
