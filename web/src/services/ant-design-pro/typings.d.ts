declare namespace API {
  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserLoginVO = {
    code?: number;
    data?: UserLoginVO;
    message?: string;
  };

  type createDescriptionParams = {
    login: string;
  };

  type getDescriptionParams = {
    id: string;
  };

  type getDeveloperParams = {
    login?: string;
    id?: number;
  };

  type getRatingResultParams = {
    account: string;
  };

  type getTotalRatingByParamParams = {
    param: string;
    value: string;
  };

  type loginParams = {
    code: string;
  };

  type User = {
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
  };

  type UserLoginVO = {
    user?: User;
    token?: string;
  };
}
