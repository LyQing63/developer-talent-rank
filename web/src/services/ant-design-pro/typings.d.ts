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
  };

  type GrantedAuthority = {
    authority?: string;
  };

  type loginParams = {
    user: OAuth2User;
  };

  type OAuth2User = {
    attributes?: Record<string, any>;
    authorities?: GrantedAuthority[];
    name?: string;
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
