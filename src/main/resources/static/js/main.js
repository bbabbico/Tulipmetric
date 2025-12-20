// State management
let favorites = JSON.parse(localStorage.getItem('favorites') || '[]');
let searchQuery = '';
let statusFilter = 'all';

// Format number
function formatNumber(num) {
  if (num >= 1000000000000) {
    return (num / 1000000000000).toFixed(1) + '조';
  } else if (num >= 100000000) {
    return (num / 100000000).toFixed(0) + '억';
  }
  return '₩' + num.toLocaleString();
}

// Toggle favorite
function toggleFavorite(id) {
  const index = favorites.indexOf(id);
  if (index > -1) {
    favorites.splice(index, 1);
  } else {
    favorites.push(id);
  }
  localStorage.setItem('favorites', JSON.stringify(favorites));
  renderAll();
}

// Create industry card
function createIndustryCard(industry, isTrending = false) {
  const isFavorite = favorites.includes(industry.id);
  const changeColor = industry.growthRate >= 0 ? 'text-green-600' : 'text-red-600';
  const changeSymbol = industry.growthRate >= 0 ? '+' : '';
  
  return `
    <div class="relative">
      <!-- Favorite Button -->
      <button 
        onclick="event.preventDefault(); event.stopPropagation(); toggleFavorite('${industry.id}')" 
        class="absolute top-2 right-2 z-10 p-1.5 rounded-full transition-all duration-200 shadow-sm ${
          isFavorite 
            ? 'bg-yellow-100 text-yellow-600 hover:bg-yellow-200' 
            : 'bg-white/80 text-slate-400 hover:bg-white hover:text-slate-600'
        }"
        title="${isFavorite ? '즐겨찾기 해제' : '즐겨찾기 추가'}"
      >
        <svg class="w-4 h-4 ${isFavorite ? 'fill-current' : ''}" viewBox="0 0 24 24" fill="${isFavorite ? 'currentColor' : 'none'}" stroke="currentColor" stroke-width="2">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
      </button>
      
      <a href="industry-detail?id=${industry.id}" class="block bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden border border-slate-200 hover:border-slate-300 group">
        <div class="p-6">
          <div class="flex items-start justify-between mb-4">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h3 class="text-slate-900 group-hover:text-blue-600 transition-colors">
                  ${industry.name}
                </h3>
                ${isTrending ? `
                  <span class="px-2 py-1 bg-gradient-to-r from-orange-500 to-pink-500 text-white text-xs rounded-full">
                    급상승
                  </span>
                ` : ''}
              </div>
              <p class="text-slate-600 text-sm line-clamp-2">${industry.description}</p>
            </div>
            <div class="ml-4">
              ${createTulipSVG(industry.marketStatus, 'md')}
            </div>
          </div>

          <div class="flex items-center justify-between pt-4 border-t border-slate-100">
            <div>
              <div class="flex items-center gap-2 mb-1">
                ${industry.growthRate >= 0 ? `
                  <svg class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
                  </svg>
                ` : `
                  <svg class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6"/>
                  </svg>
                `}
                <span class="${changeColor}">
                  ${changeSymbol}${industry.growthRate.toFixed(1)}%
                </span>
              </div>
              <p class="text-xs text-slate-500">30일 성장률</p>
            </div>
            
            <div class="text-right">
              <p class="text-slate-900" style="font-weight: 600;">${formatNumber(industry.totalMarketCap)}</p>
              <p class="text-xs text-slate-500">총 시가총액</p>
            </div>
            
            <div>
              <span class="px-3 py-1 rounded-full text-sm ${getStatusColor(industry.marketStatus)}">
                ${getStatusLabel(industry.marketStatus)}
              </span>
            </div>
          </div>

          <div class="mt-4 flex items-center gap-2 text-sm text-slate-600">
            <span>${industry.companies ? industry.companies.length : 0}개 기업</span>
            <span class="text-slate-300">•</span>
            <span class="text-blue-600 group-hover:underline">자세히 보기 →</span>
          </div>
        </div>
      </a>
    </div>
  `;
}

// Filter industries
function filterIndustries() {
  let filtered = [...industries];
  
  if (statusFilter !== 'all') {
    filtered = filtered.filter(ind => ind.marketStatus === statusFilter);
  }
  
  if (searchQuery.trim()) {
    const query = searchQuery.toLowerCase();
    filtered = filtered.filter(ind => {
      if (ind.name.toLowerCase().includes(query)) return true;
      return ind.companies && ind.companies.some(comp => 
        comp.name.toLowerCase().includes(query)
      );
    });
  }
  
  return filtered;
}

// Calculate market sentiment
function calculateMarketSentiment() {
  const avgGrowthRate = industries.reduce((sum, ind) => sum + ind.growthRate, 0) / industries.length;
  
  let marketStatus;
  let marketTitle;
  let marketDescription;
  let titleGradient;
  let descriptionColor;
  let glowColor;
  
  if (avgGrowthRate > 80) {
    marketStatus = 'overvalued';
    marketTitle = '과열된 시장';
    marketDescription = '투기적 열기가 정점을 향해 치솟고 있습니다';
    titleGradient = 'from-purple-600 to-pink-600';
    descriptionColor = 'text-purple-700';
    glowColor = 'bg-purple-400';
  } else if (avgGrowthRate > 30) {
    marketStatus = 'growing';
    marketTitle = '강한 성장세';
    marketDescription = '혁신 기술에 투자 자금이 몰리고 있습니다';
    titleGradient = 'from-orange-600 to-yellow-600';
    descriptionColor = 'text-orange-700';
    glowColor = 'bg-orange-400';
  } else if (avgGrowthRate > 0) {
    marketStatus = 'stable';
    marketTitle = '안정적인 시장';
    marketDescription = '신기술 중심으로 관심이 쏠립니다';
    titleGradient = 'from-blue-600 to-cyan-600';
    descriptionColor = 'text-blue-700';
    glowColor = 'bg-blue-400';
  } else if (avgGrowthRate > -15) {
    marketStatus = 'declining';
    marketTitle = '조정 국면';
    marketDescription = '시장이 숨을 고르며 재정비 중입니다';
    titleGradient = 'from-red-600 to-orange-600';
    descriptionColor = 'text-red-700';
    glowColor = 'bg-red-400';
  } else {
    marketStatus = 'crashed';
    marketTitle = '약세 시장';
    marketDescription = '투자 심리가 얼어붙은 상태입니다';
    titleGradient = 'from-gray-600 to-slate-600';
    descriptionColor = 'text-gray-700';
    glowColor = 'bg-gray-400';
  }
  
  return {
    marketStatus,
    marketTitle,
    marketDescription,
    avgGrowthRate,
    titleGradient,
    descriptionColor,
    glowColor
  };
}

// Render all sections
function renderAll() {
  const filtered = filterIndustries();
  const trending = filtered.filter(ind => ind.isTrending);
  const other = filtered.filter(ind => !ind.isTrending);
  const favs = industries.filter(ind => favorites.includes(ind.id));
  
  // Market temperature section
  const marketTempSection = document.getElementById('marketTempSection');
  if (searchQuery || statusFilter !== 'all') {
    marketTempSection.style.display = 'none';
  } else {
    marketTempSection.style.display = 'block';
    
    const sentiment = calculateMarketSentiment();
    const marketTitle = document.getElementById('marketTitle');
    marketTitle.textContent = sentiment.marketTitle;
    marketTitle.className = `text-5xl md:text-6xl mb-2 text-transparent bg-clip-text bg-gradient-to-r ${sentiment.titleGradient}`;
    marketTitle.style.fontWeight = '300';
    marketTitle.style.letterSpacing = '-0.025em';
    
    const marketDescription = document.getElementById('marketDescription');
    marketDescription.textContent = sentiment.marketDescription;
    marketDescription.className = `text-xl md:text-2xl ${sentiment.descriptionColor}`;
    marketDescription.style.fontWeight = '300';
    
    document.getElementById('avgGrowthRate').textContent = (sentiment.avgGrowthRate >= 0 ? '+' : '') + sentiment.avgGrowthRate.toFixed(1) + '%';
    document.getElementById('avgGrowthRate').className = sentiment.avgGrowthRate >= 0 ? 'text-xl text-green-600' : 'text-xl text-red-600';
    
    const tulipContainer = document.getElementById('marketTulip');
    tulipContainer.innerHTML = createTulipSVG(sentiment.marketStatus, 'xl');
    tulipContainer.parentElement.querySelector('.absolute').className = `absolute inset-0 blur-2xl opacity-40 ${sentiment.glowColor}`;
  }
  
  // Favorites
  const favSection = document.getElementById('favoritesSection');
  const favsList = document.getElementById('favoritesList');
  if (favs.length > 0 && !searchQuery && statusFilter === 'all') {
    favSection.style.display = 'block';
    favsList.innerHTML = favs.map(ind => createIndustryCard(ind, false)).join('');
  } else {
    favSection.style.display = 'none';
  }
  
  // Trending
  const trendingSection = document.getElementById('trendingSection');
  const trendingList = document.getElementById('trendingList');
  if (trending.length > 0) {
    trendingSection.style.display = 'block';
    trendingList.innerHTML = trending.map(ind => createIndustryCard(ind, true)).join('');
  } else {
    trendingSection.style.display = 'none';
  }
  
  // Other
  const otherSection = document.getElementById('otherSection');
  const otherList = document.getElementById('otherList');
  if (other.length > 0) {
    otherSection.style.display = 'block';
    otherList.innerHTML = other.map(ind => createIndustryCard(ind, false)).join('');
  } else {
    otherSection.style.display = 'none';
  }
  
  // No results
  const noResults = document.getElementById('noResults');
  if (filtered.length === 0) {
    noResults.style.display = 'block';
  } else {
    noResults.style.display = 'none';
  }
}

// Reset filters
function resetFilters() {
  searchQuery = '';
  statusFilter = 'all';
  document.getElementById('searchInput').value = '';
  document.getElementById('statusFilter').value = 'all';
  renderAll();
}

// Render tulip guide
function renderTulipGuide() {
  const statuses = ['crashed', 'declining', 'stable', 'growing', 'overvalued'];
  const guideHTML = statuses.map(status => `
    <div class="flex items-center gap-3 p-3 bg-slate-50 rounded-lg">
      ${createTulipSVG(status, 'sm')}
      <div>
        <p class="text-slate-700">${getStatusLabel(status)}</p>
        <p class="text-sm text-slate-500">${getStatusDescription(status)}</p>
      </div>
    </div>
  `).join('');
  
  document.getElementById('tulipGuide').innerHTML = guideHTML;
}

// Event listeners
document.getElementById('searchInput').addEventListener('input', (e) => {
  searchQuery = e.target.value;
  renderAll();
});

document.getElementById('statusFilter').addEventListener('change', (e) => {
  statusFilter = e.target.value;
  renderAll();
});

// Initial render
renderAll();
renderTulipGuide();
