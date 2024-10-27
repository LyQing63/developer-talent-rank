import { AudioOutlined } from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import type { GetProps } from 'antd';
import { Button, Input, Space, Tag } from 'antd';
import React from 'react';
import request from 'umi-request';
import styles from './index.less';

type SearchProps = GetProps<typeof Input.Search>;

type GithubIssueItem = {
  url: string;
  id: number;
  number: number;
  title: string;
  labels: {
    name: string;
    color: string;
  }[];
  state: string;
  comments: number;
  created_at: string;
  updated_at: string;
  closed_at?: string;
};

const { Search: Index } = Input;

const suffix = (
  <AudioOutlined
    style={{
      fontSize: 16,
      color: '#1677ff',
    }}
  />
);

const onSearch: SearchProps['onSearch'] = (value, _e, info) => console.log(info?.source, value);

const MySearch: React.FC = () => (
  <Space
    direction="vertical"
    style={{
      display: 'flex',
      justifyContent: 'center', // 水平居中
      alignItems: 'center',     // 垂直居中
      height: '15vh',          // 让父容器高度占满视口
    }}
  >
    <Index
      placeholder="Search一下，你就知道"
      enterButton="Search"
      // size="large"
      style={{
        display: 'flex',
        justifyContent: 'space-between',
      }}
      suffix={suffix}
      onSearch={onSearch}
      className={styles.myinput}
    />
  </Space>
);



const MyTable: React.FC = ({ style }: MyTableProps) => (
  <ProList<GithubIssueItem>
    className={styles.mytable}
    toolBarRender={() => {
      return [
        <Button key="3" type="primary">
          新建
        </Button>,
      ];
    }}
    search={{}}
    rowKey="name"
    headerTitle="基础列表"
    request={async (params = {} as Record<string, any>) =>
      request<{
        data: GithubIssueItem[];
      }>('https://proapi.azurewebsites.net/github/issues', {
        params,
      })
    }
    pagination={{
      pageSize: 10,
    }}
    showActions="hover"
    metas={{
      // title: {
      //   dataIndex: 'user',
      //   title: '用户',
      // },
      avatar: {
        dataIndex: 'avatar',
        search: false,
      },
      description: {
        dataIndex: 'title',
        search: false,
      },
      subTitle: {
        dataIndex: 'labels',
        render: (_, row) => {
          return (
            <Space size={0}>
              {row.labels?.map((label: { name: string }) => (
                <Tag color="blue" key={label.name}>
                  {label.name}
                </Tag>
              ))}
            </Space>
          );
        },
        search: false,
      },
      // actions: {
      //   render: (text, row) => [
      //     <a href={row.url} target="_blank" rel="noopener noreferrer" key="link">
      //       链路
      //     </a>,
      //     <a href={row.url} target="_blank" rel="noopener noreferrer" key="warning">
      //       报警
      //     </a>,
      //     <a href={row.url} target="_blank" rel="noopener noreferrer" key="view">
      //       查看
      //     </a>,
      //   ],
      //   search: false,
      // },
      status: {
        // 自己扩展的字段，主要用于筛选，不在列表中显示
        title: 'Nation',
        valueType: 'select',
        valueEnum: {
          China: { text: '中国', code: '001' },
          Angola: { text: '安哥拉', code: '002' },
          Afghanistan: { text: '阿富汗', code: '003' },
          Albania: { text: '阿尔巴尼亚', code: '004' },
          Algeria: { text: '阿尔及利亚', code: '005' },
          Andorra: { text: '安道尔共和国', code: '006' },
          Anguilla: { text: '安圭拉岛', code: '007' },
          Antigua_and_Barbuda: { text: '安提瓜和巴布达', code: '008' },
          Argentina: { text: '阿根廷', code: '009' },
          Armenia: { text: '亚美尼亚', code: '010' },
          Ascension: { text: '阿森松', code: '011' },
          Australia: { text: '澳大利亚', code: '012' },
          Austria: { text: '奥地利', code: '013' },
          Azerbaijan: { text: '阿塞拜疆', code: '014' },
          Bahamas: { text: '巴哈马', code: '015' },
          Bahrain: { text: '巴林', code: '016' },
          Bangladesh: { text: '孟加拉国', code: '017' },
          Barbados: { text: '巴巴多斯', code: '018' },
          Belarus: { text: '白俄罗斯', code: '019' },
          Belgium: { text: '比利时', code: '020' },
          Belize: { text: '伯利兹', code: '021' },
          Benin: { text: '贝宁', code: '022' },
          Bermuda_Is: { text: '百慕大群岛', code: '023' },
          Bolivia: { text: '玻利维亚', code: '024' },
          Botswana: { text: '博茨瓦纳', code: '025' },
          Brazil: { text: '巴西', code: '026' },
          Brunei: { text: '文莱', code: '027' },
          Bulgaria: { text: '保加利亚', code: '028' },
          Burkina_Faso: { text: '布基纳法索', code: '029' },
          Burma: { text: '缅甸', code: '030' },
          Burundi: { text: '布隆迪', code: '031' },
          Cameroon: { text: '喀麦隆', code: '032' },
          Canada: { text: '加拿大', code: '033' },
          Cayman_Is: { text: '开曼群岛', code: '034' },
          Central_African_Republic: { text: '中非共和国', code: '035' },
          Chad: { text: '乍得', code: '036' },
          Chile: { text: '智利', code: '037' },
          Colombia: { text: '哥伦比亚', code: '038' },
          Congo: { text: '刚果', code: '039' },
          Cook_Is: { text: '库克群岛', code: '040' },
          Costa_Rica: { text: '哥斯达黎加', code: '041' },
          Cuba: { text: '古巴', code: '042' },
          Cyprus: { text: '塞浦路斯', code: '043' },
          Czech_Republic: { text: '捷克', code: '044' },
          Denmark: { text: '丹麦', code: '045' },
          Djibouti: { text: '吉布提', code: '046' },
          Dominica_Rep: { text: '多米尼加共和国', code: '047' },
          Ecuador: { text: '厄瓜多尔', code: '048' },
          Egypt: { text: '埃及', code: '049' },
          EI_Salvador: { text: '萨尔瓦多', code: '050' },
          Estonia: { text: '爱沙尼亚', code: '051' },
          Ethiopia: { text: '埃塞俄比亚', code: '052' },
          Fiji: { text: '斐济', code: '053' },
          Finland: { text: '芬兰', code: '054' },
          France: { text: '法国', code: '055' },
          French_Guiana: { text: '法属圭亚那', code: '056' },
          French_Polynesia: { text: '法属玻利尼西亚', code: '057' },
          Gabon: { text: '加蓬', code: '058' },
          Gambia: { text: '冈比亚', code: '059' },
          Georgia: { text: '格鲁吉亚', code: '060' },
          Germany: { text: '德国', code: '061' },
          Ghana: { text: '加纳', code: '062' },
          Gibraltar: { text: '直布罗陀', code: '063' },
          Greece: { text: '希腊', code: '064' },
          Grenada: { text: '格林纳达', code: '065' },
          Guam: { text: '关岛', code: '066' },
          Guatemala: { text: '危地马拉', code: '067' },
          Guinea: { text: '几内亚', code: '068' },
          Guyana: { text: '圭亚那', code: '069' },
          Haiti: { text: '海地', code: '070' },
          Honduras: { text: '洪都拉斯', code: '071' },
          Hungary: { text: '匈牙利', code: '072' },
          Iceland: { text: '冰岛', code: '073' },
          India: { text: '印度', code: '074' },
          Indonesia: { text: '印度尼西亚', code: '075' },
          Iran: { text: '伊朗', code: '076' },
          Iraq: { text: '伊拉克', code: '077' },
          Ireland: { text: '爱尔兰', code: '078' },
          Israel: { text: '以色列', code: '079' },
          Italy: { text: '意大利', code: '080' },
          Ivory_Coast: { text: '科特迪瓦', code: '081' },
          Jamaica: { text: '牙买加', code: '082' },
          Japan: { text: '日本', code: '083' },
          Jordan: { text: '约旦', code: '084' },
          Kampuchea_Cambodia: { text: '柬埔寨', code: '085' },
          Kazakstan: { text: '哈萨克斯坦', code: '086' },
          Kenya: { text: '肯尼亚', code: '087' },
          Korea: { text: '韩国', code: '088' },
          Kuwait: { text: '科威特', code: '089' },
          Kyrgyzstan: { text: '吉尔吉斯坦', code: '090' },
          Laos: { text: '老挝', code: '091' },
          Latvia: { text: '拉脱维亚', code: '092' },
          Lebanon: { text: '黎巴嫩', code: '093' },
          Lesotho: { text: '莱索托', code: '094' },
          Liberia: { text: '利比里亚', code: '095' },
          Libya: { text: '利比亚', code: '096' },
          Liechtenstein: { text: '列支敦士登', code: '097' },
          Lithuania: { text: '立陶宛', code: '098' },
          Luxembourg: { text: '卢森堡', code: '099' },
          Madagascar: { text: '马达加斯加', code: '100' },
          Malawi: { text: '马拉维', code: '101' },
          Malaysia: { text: '马来西亚', code: '102' },
          Maldives: { text: '马尔代夫', code: '103' },
          Mali: { text: '马里', code: '104' },
          Malta: { text: '马耳他', code: '105' },
          Mariana_Is: { text: '马里亚那群岛', code: '106' },
          Martinique: { text: '马提尼克', code: '107' },
          Mauritius: { text: '毛里求斯', code: '108' },
          Mexico: { text: '墨西哥', code: '109' },
          Moldova: { text: '摩尔多瓦', code: '110' },
          Monaco: { text: '摩纳哥', code: '111' },
          Mongolia: { text: '蒙古', code: '112' },
          Montserrat_Is: { text: '蒙特塞拉特', code: '113' },
          Morocco: { text: '摩洛哥', code: '114' },
          Mozambique: { text: '莫桑比克', code: '115' },
          Namibia: { text: '纳米比亚', code: '116' },
          Nepal: { text: '尼泊尔', code: '117' },
          Netherlands: { text: '荷兰', code: '118' },
          New: { text: '新喀里多尼亚', code: '119' },
          NewZealand: { text: '新西兰', code: '120' },
          Nicaragua: { text: '尼加拉瓜', code: '121' },
          Niger: { text: '尼日尔', code: '122' },
          Nigeria: { text: '尼日利亚', code: '123' },
          Norway: { text: '挪威', code: '124' },
          Oman: { text: '阿曼', code: '125' },
          Pakistan: { text: '巴基斯坦', code: '126' },
          Panama: { text: '巴拿马', code: '127' },
          Papua: { text: '巴布亚新几内亚', code: '128' },
          Paraguay: { text: '巴拉圭', code: '129' },
          Peru: { text: '秘鲁', code: '130' },
          Philippines: { text: '菲律宾', code: '131' },
          Poland: { text: '波兰', code: '132' },
          Portugal: { text: '葡萄牙', code: '133' },
          Qatar: { text: '卡塔尔', code: '134' },
          Romania: { text: '罗马尼亚', code: '135' },
          Russia: { text: '俄罗斯', code: '136' },
          Rwanda: { text: '卢旺达', code: '137' },
          St: { text: '圣多美和普林西比', code: '138' },
          St_Kitts_and_Nevis: { text: '圣基茨和尼维斯', code: '139' },
          St_Lucia: { text: '圣卢西亚', code: '140' },
          St_Vincent_and_the_Grenadines: { text: '圣文森特和格林纳丁斯', code: '141' },
          Samoa: { text: '萨摩亚', code: '142' },
          SanMarino: { text: '圣马力诺', code: '143' },
          Sao_Tome_and_Principe: { text: '圣多美和普林西比', code: '144' },
          SaudiArabia: { text: '沙特阿拉伯', code: '145' },
          Senegal: { text: '塞内加尔', code: '146' },
          Serbia_and_Montenegro: { text: '塞尔维亚和黑山', code: '147' },
          Seychelles: { text: '塞舌尔', code: '148' },
          SierraLeone: { text: '塞拉利昂', code: '149' },
          Singapore: { text: '新加坡', code: '150' },
          Slovakia: { text: '斯洛伐克', code: '151' },
          Slovenia: { text: '斯洛文尼亚', code: '152' },
          Solomon: { text: '所罗门群岛', code: '153' },
          Somalia: { text: '索马里', code: '154' },
          South_Africa: { text: '南非', code: '155' },
          South_Korea: { text: '韩国', code: '156' },
          Spain: { text: '西班牙', code: '157' },
          Sri_Lanka: { text: '斯里兰卡', code: '158' },
          Sudan: { text: '苏丹', code: '159' },
          Suriname: { text: '苏里南', code: '160' },
          Sweden: { text: '瑞典', code: '161' },
          Switzerland: { text: '瑞士', code: '162' },
          Syria: { text: '叙利亚', code: '163' },
          Taiwan: { text: '台湾', code: '164' },
          Tajikistan: { text: '塔吉克斯坦', code: '165' },
          Tanzania: { text: '坦桑尼亚', code: '166' },
          Thailand: { text: '泰国', code: '167' },
          Togo: { text: '多哥', code: '168' },
          Tonga: { text: '汤加', code: '169' },
          Trinidad: { text: '特立尼达和多巴哥', code: '170' },
          Tunisia: { text: '突尼斯', code: '171' },
          Turkey: { text: '土耳其', code: '172' },
          Turkmenistan: { text: '土库曼斯坦', code: '173' },
          Turks_and_Caicos: { text: '特克斯和凯科斯群岛', code: '174' },
          Uganda: { text: '乌干达', code: '175' },
          Ukraine: { text: '乌克兰', code: '176' },
          UnitedArabEmirates: { text: '阿联酋', code: '177' },
          UnitedKingdom: { text: '英国', code: '178' },
          UnitedStates: { text: '美国', code: '179' },
          Uruguay: { text: '乌拉圭', code: '180' },
          Uzbekistan: { text: '乌兹别克斯坦', code: '181' },
          Vanuatu: { text: '瓦努阿图', code: '182' },
          Vatican: { text: '梵蒂冈', code: '183' },
          Venezuela: { text: '委内瑞拉', code: '184' },
          Vietnam: { text: '越南', code: '185' },
          Wallis_and_Futuna: { text: '瓦利斯和富图纳群岛', code: '186' },
          Yemen: { text: '也门', code: '187' },
          Zambia: { text: '赞比亚', code: '188' },
          Zimbabwe: { text: '津巴布韦', code: '189' }



        },
      },
    }}
  />
);

const App: React.FC = () => {
  const handleSearch = (value: string) => {
    console.log('搜索内容:', value);
    // 这里可以添加你需要的搜索逻辑
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      justifyContent: 'flex-start',
      overflow: 'auto' ,
    }}>
      <div>
        <MySearch onSearch={handleSearch} />
      </div>

      <div
      style={{
        width: '85%',
        margin: '0 auto'
      }}
      >
        <MyTable/>
      </div>
    </div>
  );
};


export default App;
