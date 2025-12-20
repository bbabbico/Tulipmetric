// Mock API responses - 실제 환경에서는 서버에서 받아온 데이터

// 기업 정보: 회사명, 산업군, 현재주가, 시가총액, 등락(원), 등락률, PER, 거래량, 산업군내 거래비중
const companiesData = {
  'agriculture': [
    { name: '농업기업 1', industry: '농업, 임업 및 어업', currentPrice: 85300, marketCap: 150000000000, priceChange: 2100, changeRate: 2.5, per: 15.2, volume: 125000, industryVolumeRatio: 18.5 },
    { name: '농업기업 2', industry: '농업, 임업 및 어업', currentPrice: 72510, marketCap: 120000000000, priceChange: -870, changeRate: -1.2, per: 12.8, volume: 98000, industryVolumeRatio: 14.2 },
    { name: '농업기업 3', industry: '농업, 임업 및 어업', currentPrice: 61630, marketCap: 96000000000, priceChange: 1850, changeRate: 3.1, per: 14.5, volume: 87000, industryVolumeRatio: 12.8 },
    { name: '농업기업 4', industry: '농업, 임업 및 어업', currentPrice: 52390, marketCap: 76800000000, priceChange: 930, changeRate: 1.8, per: 11.3, volume: 76000, industryVolumeRatio: 10.5 },
    { name: '농업기업 5', industry: '농업, 임업 및 어업', currentPrice: 44530, marketCap: 61440000000, priceChange: -220, changeRate: -0.5, per: 13.7, volume: 65000, industryVolumeRatio: 9.8 },
    { name: '농업기업 6', industry: '농업, 임업 및 어업', currentPrice: 37850, marketCap: 49152000000, priceChange: 820, changeRate: 2.2, per: 10.9, volume: 54000, industryVolumeRatio: 8.2 },
    { name: '농업기업 7', industry: '농업, 임업 및 어업', currentPrice: 32170, marketCap: 39321600000, priceChange: -570, changeRate: -1.8, per: 12.1, volume: 45000, industryVolumeRatio: 6.9 },
    { name: '농업기업 8', industry: '농업, 임업 및 어업', currentPrice: 27350, marketCap: 31457280000, priceChange: 240, changeRate: 0.9, per: 11.5, volume: 38000, industryVolumeRatio: 5.7 },
    { name: '농업기업 9', industry: '농업, 임업 및 어업', currentPrice: 23240, marketCap: 25165824000, priceChange: 340, changeRate: 1.5, per: 13.2, volume: 32000, industryVolumeRatio: 4.8 },
    { name: '농업기업 10', industry: '농업, 임업 및 어업', currentPrice: 19760, marketCap: 20132659200, priceChange: -60, changeRate: -0.3, per: 10.7, volume: 27000, industryVolumeRatio: 3.9 }
  ],
  'semiconductor': [
    { name: 'Samsung Electronics', industry: '반도체', currentPrice: 1485000, marketCap: 950000000000, priceChange: 112500, changeRate: 8.2, per: 28.5, volume: 8500000, industryVolumeRatio: 22.5 },
    { name: 'TSMC', industry: '반도체', currentPrice: 198900, marketCap: 850000000000, priceChange: 14350, changeRate: 7.8, per: 32.1, volume: 7200000, industryVolumeRatio: 19.8 },
    { name: 'NVIDIA', industry: '반도체', currentPrice: 525320, marketCap: 720000000000, priceChange: 58650, changeRate: 12.5, per: 45.8, volume: 6800000, industryVolumeRatio: 18.2 },
    { name: 'Intel', industry: '반도체', currentPrice: 48150, marketCap: 420000000000, priceChange: 2760, changeRate: 6.1, per: 18.9, volume: 5500000, industryVolumeRatio: 14.5 },
    { name: 'AMD', industry: '반도체', currentPrice: 158450, marketCap: 330000000000, priceChange: 13500, changeRate: 9.3, per: 38.7, volume: 4200000, industryVolumeRatio: 11.2 },
    { name: 'Qualcomm', industry: '반도체', currentPrice: 135600, marketCap: 265000000000, priceChange: 9680, changeRate: 7.7, per: 22.4, volume: 3100000, industryVolumeRatio: 8.3 },
    { name: 'Broadcom', industry: '반도체', currentPrice: 992300, marketCap: 225000000000, priceChange: 75200, changeRate: 8.2, per: 35.6, volume: 2800000, industryVolumeRatio: 7.5 },
    { name: 'SK Hynix', industry: '반도체', currentPrice: 115200, marketCap: 180000000000, priceChange: 11500, changeRate: 10.9, per: 25.3, volume: 2500000, industryVolumeRatio: 6.7 },
    { name: 'Micron', industry: '반도체', currentPrice: 98350, marketCap: 158000000000, priceChange: 7560, changeRate: 8.3, per: 19.8, volume: 2100000, industryVolumeRatio: 5.6 },
    { name: 'Applied Materials', industry: '반도체', currentPrice: 162800, marketCap: 135000000000, priceChange: 10350, changeRate: 6.8, per: 21.7, volume: 1850000, industryVolumeRatio: 4.9 }
  ]
};

// 산업군 정보: 산업군명, 종목수, 산업군 시가총액, 등락(원), 등락률, PER, 한달간 산업군 주가 성장률, 1~12개월 전 산업군 주가 성장률
const industriesData = [
  {
    name: '농업, 임업 및 어업',
    id: 'agriculture',
    stockCount: 10,
    totalMarketCap: 420000000000,
    priceChange: 2580,
    changeRate: 5.2,
    averagePer: 12.6,
    monthlyGrowthRate: 5.2,
    growthHistory: [5.2, 5.0, 4.9, 4.7, 4.5, 4.2, 4.0, 3.8, 3.5, 3.2, 3.0, 2.8], // 1개월 전부터 12개월 전까지
    description: '농산물 생산, 임업, 어업 및 관련 서비스를 제공하는 기업들입니다.',
    marketStatus: 'stable'
  },
  {
    name: '음식료·담배',
    id: 'food-beverage',
    stockCount: 10,
    totalMarketCap: 1250000000000,
    priceChange: 10250,
    changeRate: 8.7,
    averagePer: 15.3,
    monthlyGrowthRate: 8.7,
    growthHistory: [8.7, 8.5, 8.3, 8.1, 7.8, 7.5, 7.2, 6.8, 6.5, 6.2, 5.8, 5.5],
    description: '식품, 음료, 담배를 제조 및 유통하는 기업들입니다.',
    marketStatus: 'stable'
  },
  {
    name: '제약',
    id: 'pharmaceutical',
    stockCount: 10,
    totalMarketCap: 1650000000000,
    priceChange: 38500,
    changeRate: 28.4,
    averagePer: 24.8,
    monthlyGrowthRate: 28.4,
    growthHistory: [28.4, 27.1, 25.8, 24.1, 22.3, 20.5, 18.7, 16.9, 15.2, 13.5, 11.8, 10.2],
    description: '의약품을 연구 개발하고 제조하는 기업들입니다.',
    marketStatus: 'growing',
    isTrending: true
  },
  {
    name: '전기·전자',
    id: 'electronics',
    stockCount: 10,
    totalMarketCap: 2150000000000,
    priceChange: 56800,
    changeRate: 35.6,
    averagePer: 28.9,
    monthlyGrowthRate: 35.6,
    growthHistory: [35.6, 33.9, 32.2, 30.1, 27.8, 25.3, 23.0, 20.8, 18.5, 16.2, 14.0, 11.8],
    description: '전자제품 및 전기 기기를 제조하는 기업들입니다.',
    marketStatus: 'growing',
    isTrending: true
  },
  {
    name: '의료·정밀기기',
    id: 'medical-devices',
    stockCount: 10,
    totalMarketCap: 920000000000,
    priceChange: 17850,
    changeRate: 24.7,
    averagePer: 22.5,
    monthlyGrowthRate: 24.7,
    growthHistory: [24.7, 23.7, 22.6, 21.3, 19.9, 18.5, 17.2, 15.8, 14.5, 13.2, 11.8, 10.5],
    description: '의료 장비 및 정밀 기기를 제조하는 기업들입니다.',
    marketStatus: 'growing',
    isTrending: true
  },
  {
    name: 'IT 서비스',
    id: 'it-services',
    stockCount: 10,
    totalMarketCap: 2450000000000,
    priceChange: 125800,
    changeRate: 85.3,
    averagePer: 42.5,
    monthlyGrowthRate: 85.3,
    growthHistory: [85.3, 81.2, 76.8, 71.3, 64.5, 58.2, 52.5, 47.2, 42.1, 37.5, 33.2, 29.5],
    description: 'IT 솔루션 및 소프트웨어 서비스를 제공하는 기업들입니다.',
    marketStatus: 'overvalued',
    isTrending: true
  },
  {
    name: '오락·문화',
    id: 'entertainment',
    stockCount: 10,
    totalMarketCap: 780000000000,
    priceChange: 18950,
    changeRate: 31.4,
    averagePer: 26.8,
    monthlyGrowthRate: 31.4,
    growthHistory: [31.4, 30.0, 28.5, 26.8, 24.9, 22.8, 20.8, 18.9, 17.2, 15.5, 13.8, 12.2],
    description: '엔터테인먼트 및 문화 콘텐츠를 제공하는 기업들입니다.',
    marketStatus: 'growing',
    isTrending: true
  },
  {
    name: '반도체',
    id: 'semiconductor',
    stockCount: 10,
    totalMarketCap: 3250000000000,
    priceChange: 178500,
    changeRate: 92.8,
    averagePer: 28.3,
    monthlyGrowthRate: 92.8,
    growthHistory: [92.8, 88.7, 84.5, 78.9, 72.1, 65.3, 58.8, 52.5, 46.8, 41.5, 36.8, 32.5],
    description: '반도체 칩과 관련 장비를 제조하는 기업들로, 현대 기술 산업의 핵심 인프라를 제공합니다.',
    marketStatus: 'overvalued',
    isTrending: true
  },
  {
    name: '건설',
    id: 'construction',
    stockCount: 10,
    totalMarketCap: 680000000000,
    priceChange: -6250,
    changeRate: -9.7,
    averagePer: 8.5,
    monthlyGrowthRate: -9.7,
    growthHistory: [-9.7, -8.6, -7.5, -6.1, -4.8, -3.2, -1.8, -0.5, 0.8, 2.1, 3.5, 4.8],
    description: '건축 및 토목 공사를 수행하는 기업들입니다.',
    marketStatus: 'declining'
  },
  {
    name: '부동산',
    id: 'real-estate',
    stockCount: 10,
    totalMarketCap: 420000000000,
    priceChange: -11250,
    changeRate: -28.5,
    averagePer: 6.2,
    monthlyGrowthRate: -28.5,
    growthHistory: [-28.5, -25.6, -22.5, -19.2, -15.8, -12.3, -8.9, -5.5, -2.2, 1.1, 4.5, 7.8],
    description: '부동산 개발 및 임대 사업을 하는 기업들입니다.',
    marketStatus: 'crashed'
  }
];

// 게시물 정보: 게시물 구분숫자, 작성자, 게시물 제목, 게시물 내용, 작성시간, 게시글 좋아요수, 댓글수
const postsData = [
  {
    postId: 1,
    category: 'analysis',
    author: '투자왕김철수',
    title: '반도체 산업 현황 및 향후 전망 분석',
    content: 'AI 붐으로 인해 반도체 수요가 급증하고 있습니다. 특히 HBM 메모리와 AI 가속기 시장이 폭발적으로 성장하고 있는데요...',
    createdAt: '2시간 전',
    likes: 89,
    commentCount: 34,
    views: 1247
  },
  {
    postId: 2,
    category: 'discussion',
    author: '신중한투자자',
    title: 'IT 서비스 산업 과열 아닌가요?',
    content: '최근 IT 서비스 주식들이 너무 많이 올라서 걱정됩니다. 성장률이 85%인데 이게 지속 가능할까요?',
    createdAt: '5시간 전',
    likes: 45,
    commentCount: 67,
    views: 892
  },
  {
    postId: 3,
    category: 'news',
    author: 'NewsHunter',
    title: 'TSMC 3나노 공정 양산 확대 소식',
    content: 'TSMC가 3나노 칩 생산을 대폭 확대한다는 소식입니다. 관련 기업들에 긍정적 영향이 예상됩니다.',
    createdAt: '1일 전',
    likes: 124,
    commentCount: 45,
    views: 2156
  },
  {
    postId: 4,
    category: 'analysis',
    author: '바이오전문가',
    title: '제약 산업 신약 파이프라인 분석',
    content: '최근 승인된 신약들과 임상 3상 진행 중인 파이프라인을 정리해봤습니다. 제약 산업의 성장 가능성이 높아 보입니다.',
    createdAt: '1일 전',
    likes: 98,
    commentCount: 52,
    views: 1543
  },
  {
    postId: 5,
    category: 'discussion',
    author: '장기투자러',
    title: '부동산 산업 반등 시점은 언제일까요?',
    content: '부동산 주식이 계속 하락하고 있는데, 반등 시점을 어떻게 예상하시나요? 금리 인하가 시작되면 회복될까요?',
    createdAt: '2일 전',
    likes: 156,
    commentCount: 189,
    views: 3421
  }
];

// 댓글 정보: 게시물 구분숫자, 작성자, 작성시간, 댓글 좋아요수
const commentsData = {
  1: [
    { postId: 1, author: '반도체전문가', content: '좋은 분석 감사합니다. 특히 HBM 시장에 대한 부분이 인상적이네요.', createdAt: '1시간 전', likes: 23 },
    { postId: 1, author: '장기투자러', content: '과열 우려에 대한 지적이 현실적이네요.', createdAt: '30분 전', likes: 15 },
    { postId: 1, author: '테크투자자', content: 'TSMC 3나노 양산 확대는 정말 주목할 만한 이슈입니다.', createdAt: '15분 전', likes: 8 }
  ],
  2: [
    { postId: 2, author: 'IT분석가', content: '저도 같은 생각입니다. 단기 조정이 필요해 보입니다.', createdAt: '3시간 전', likes: 12 }
  ]
};

// 간단한 조회 함수들
function getIndustryData(industryId) {
  return industriesData.find(ind => ind.id === industryId);
}

function getCompaniesData(industryId) {
  return companiesData[industryId] || [];
}

function getPostData(postId) {
  return postsData.find(post => post.postId === parseInt(postId));
}

function getCommentsData(postId) {
  return commentsData[postId] || [];
}

// 메인 페이지용 산업군 목록 (간략 정보)
const industries = industriesData.map(ind => ({
  id: ind.id,
  name: ind.name,
  description: ind.description,
  marketStatus: ind.marketStatus,
  growthRate: ind.monthlyGrowthRate,
  totalMarketCap: ind.totalMarketCap,
  isTrending: ind.isTrending,
  companies: getCompaniesData(ind.id).slice(0, 3) // 미리보기용 상위 3개만
}));
