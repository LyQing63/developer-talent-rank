declare namespace API {
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

  type getDeveloperParams = {
    login: string;
    token: string;
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
