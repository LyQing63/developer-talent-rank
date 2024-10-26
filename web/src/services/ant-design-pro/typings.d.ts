declare namespace API {
  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
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

  type CurrentUser = {};

  type OAuth2User = {
    authorities?: GrantedAuthority[];
    attributes?: Record<string, any>;
    name?: string;
  };
}
