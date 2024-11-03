// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /ai/description */
export async function createDescription(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.createDescriptionParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponse>('/api/ai/description', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /ai/getDescription */
export async function getDescription(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDescriptionParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponse>('/api/ai/getDescription', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
