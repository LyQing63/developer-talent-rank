// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
// src/constants/index.ts
export const loginPath = '/user/login';

/** 此处后端没有提供注释 GET /login/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseUser>('/login/currentUser', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /login/get_developer */
export async function getDeveloper(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDeveloperParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseUser>('/login/get_developer', {
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
  return request<API.BaseResponseUser>('/login/oauth', {
    method: 'GET',
    params: {
      ...params,
      user: undefined,
      ...params['user'],
    },
    ...(options || {}),
  });
}

export async function outLogin() {
  try {
    // 调用后端的登出接口
    await request('/api/logout', {
      method: 'POST',
    });

    // 清除本地存储的用户信息
    localStorage.removeItem('currentUser');
    sessionStorage.removeItem('token');

    // 重定向到登录页面
    history.replaceState(null, '/');
  } catch (error) {
    console.error('登出失败', error);
  }
}
