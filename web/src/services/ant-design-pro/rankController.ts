// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /rank/score */
export async function getRatingResult(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getRatingResultParams,
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

/** 此处后端没有提供注释 GET /rank/search100Rank */
export async function search100Rank(options?: { [key: string]: any }) {
  return request<API.BaseResponse>('/api/rank/search100Rank', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /rank/totalRating */
export async function getTotalRating(options?: { [key: string]: any }) {
  return request<API.BaseResponse>('/api/rank/totalRating', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /rank/totalRating */
export async function getTotalRatingByParam(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getTotalRatingByParamParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponse>('/api/rank/totalRating', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
