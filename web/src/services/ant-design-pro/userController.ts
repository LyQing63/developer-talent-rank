// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /login/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseUser>('/login/currentUser', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /login/getDeveloper */
export async function getDeveloper(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDeveloperParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseUser>('/login/getDeveloper', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /login/oauth */
export async function login(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.loginParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseUserLoginVO>('/login/oauth', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
