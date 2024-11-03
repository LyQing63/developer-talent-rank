import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/LyQing63/developer-talent-rank',
          blankTarget: true,
        },
        {
          key: 'Github Talent Rank',
          title: 'Github Talent Rank',
          href: 'https://github.com/LyQing63/developer-talent-rank',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
