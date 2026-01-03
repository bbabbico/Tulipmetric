// SSR(Thymeleaf) 버전: 카드 생성은 서버가 하고, JS는 '상태/검색/즐겨찾기/가이드'만 담당

// State
let favorites = [];
let useLocalFavorites = true;
let searchQuery = '';
let statusFilter = 'all';

function $(sel, root = document) { return root.querySelector(sel); }
function $$(sel, root = document) { return Array.from(root.querySelectorAll(sel)); }

function saveFavorites() {
  if (!useLocalFavorites) return;
  localStorage.setItem('favorites', JSON.stringify(favorites));
}

function hasServerWishlist() {
  const body = document.body;
  if (!body) return false;
  return body.dataset.hasServerWishlist === 'true';
}

function loadServerFavorites() {
  return getAllMasterCards()
    .filter(card => card.dataset.isFavorite === 'true')
    .map(card => card.dataset.id);
}

function initializeFavorites() {
  useLocalFavorites = !hasServerWishlist();

  if (useLocalFavorites) {
    favorites = JSON.parse(localStorage.getItem('favorites') || '[]');
    return;
  }

  favorites = loadServerFavorites();
}

// 기존 index.js 로직(평균 성장률 기반 시장 온도) 그대로 사용 (industries -> DOM 카드 데이터로 대체)
function calculateMarketSentiment(avgGrowthRate) {
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

// 즐겨찾기 버튼 UI를 기존 스타일대로 맞춤
function setFavoriteButtonUI(btn, isFav) {
  btn.title = isFav ? '즐겨찾기 해제' : '즐겨찾기 추가';

  // button bg/text
  btn.classList.remove('bg-yellow-100', 'text-yellow-600', 'hover:bg-yellow-200');
  btn.classList.remove('bg-white/80', 'text-slate-400', 'hover:bg-white', 'hover:text-slate-600');

  if (isFav) {
    btn.classList.add('bg-yellow-100', 'text-yellow-600', 'hover:bg-yellow-200');
  } else {
    btn.classList.add('bg-white/80', 'text-slate-400', 'hover:bg-white', 'hover:text-slate-600');
  }

  const svg = btn.querySelector('svg');
  if (!svg) return;

  svg.classList.toggle('fill-current', isFav);
  svg.setAttribute('fill', isFav ? 'currentColor' : 'none');
}

async function toggleFavorite(id) {
  const idx = favorites.indexOf(id);
  const isFav = idx > -1;

  if (useLocalFavorites) {
    if (isFav) favorites.splice(idx, 1);
    else favorites.push(id);

    saveFavorites();
    renderAll();
    return;
  }

  const url = isFav ? '/deletwishmarket' : '/savewishmarket';
  const body = new URLSearchParams();
  body.set('id', id);

  try {
    await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body
    });

    if (isFav) favorites.splice(idx, 1);
    else favorites.push(id);

    renderAll();
  } catch (err) {
    console.error('즐겨찾기 요청 실패', err);
  }
}

// 카드 내부의 튤립/배지 텍스트를 클라이언트에서 채움 (tulip.js 함수 사용)
function hydrateCard(cardEl) {
  const status = cardEl.dataset.status || 'stable';

  // 튤립 아이콘
  const tulip = cardEl.querySelector('.tulip-icon');
  if (tulip && typeof createTulipSVG === 'function') {
    const size = tulip.dataset.size || 'md';
    tulip.innerHTML = createTulipSVG(status, size);
  }

  // 상태 배지
  const badge = cardEl.querySelector('.status-badge');
  if (badge && typeof getStatusLabel === 'function' && typeof getStatusColor === 'function') {
    const base = 'status-badge px-3 py-1 rounded-full text-sm';
    badge.className = base + ' ' + getStatusColor(status);
    badge.textContent = getStatusLabel(status);
  }

  // 즐겨찾기 버튼
  const favBtn = cardEl.querySelector('.favorite-btn');
  if (favBtn) {
    const id = favBtn.dataset.id;
    setFavoriteButtonUI(favBtn, favorites.includes(id));
    favBtn.onclick = (e) => {
      e.preventDefault();
      e.stopPropagation();
      toggleFavorite(id);
    };
  }
}

// DOM 기반 필터링
function getAllMasterCards() {
  // 즐겨찾기 섹션은 렌더링 시 클론이 들어가므로 제외
  return $$('.industry-card').filter(el => !el.closest('#favoritesList'));
}

function getAvgGrowth(cards) {
  const nums = cards
    .map(c => parseFloat(c.dataset.growth))
    .filter(v => !Number.isNaN(v));
  if (nums.length === 0) return 0;
  return nums.reduce((a, b) => a + b, 0) / nums.length;
}

function matchesFilter(card) {
  const name = (card.dataset.name || '').toLowerCase();
  const status = card.dataset.status || '';

  if (statusFilter !== 'all' && status !== statusFilter) return false;
  if (searchQuery.trim()) {
    const q = searchQuery.trim().toLowerCase();
    if (!name.includes(q)) return false;
  }
  return true;
}

function renderMarketTemp(cards) {
  const marketTempSection = $('#marketTempSection');
  if (!marketTempSection) return;

  if (searchQuery || statusFilter !== 'all') {
    marketTempSection.style.display = 'none';
    return;
  }

  marketTempSection.style.display = 'block';

  const avgGrowthRate = getAvgGrowth(cards);
  const sentiment = calculateMarketSentiment(avgGrowthRate);

  const marketTitle = $('#marketTitle');
  marketTitle.textContent = sentiment.marketTitle;
  marketTitle.className = `text-5xl md:text-6xl mb-2 text-transparent bg-clip-text bg-gradient-to-r ${sentiment.titleGradient}`;
  marketTitle.style.fontWeight = '300';
  marketTitle.style.letterSpacing = '-0.025em';

  const marketDescription = $('#marketDescription');
  marketDescription.textContent = sentiment.marketDescription;
  marketDescription.className = `text-xl md:text-2xl ${sentiment.descriptionColor}`;
  marketDescription.style.fontWeight = '300';

  const avgEl = $('#avgGrowthRate');
  avgEl.textContent = (sentiment.avgGrowthRate >= 0 ? '+' : '') + sentiment.avgGrowthRate.toFixed(1) + '%';
  avgEl.className = sentiment.avgGrowthRate >= 0 ? 'text-xl text-green-600' : 'text-xl text-red-600';

  const tulipContainer = $('#marketTulip');
  if (tulipContainer && typeof createTulipSVG === 'function') {
    tulipContainer.innerHTML = createTulipSVG(sentiment.marketStatus, 'xl');
    const glow = tulipContainer.parentElement?.querySelector('.absolute');
    if (glow) glow.className = `absolute inset-0 blur-2xl opacity-40 ${sentiment.glowColor}`;
  }
}

function renderFavorites(masterCards) {
  const favSection = $('#favoritesSection');
  const favsList = $('#favoritesList');
  if (!favSection || !favsList) return;

  const showFav = favorites.length > 0 && !searchQuery && statusFilter === 'all';

  if (!showFav) {
    favSection.style.display = 'none';
    favsList.innerHTML = '';
    return;
  }

  const favCards = masterCards.filter(c => favorites.includes(c.dataset.id));
  if (favCards.length === 0) {
    favSection.style.display = 'none';
    favsList.innerHTML = '';
    return;
  }

  favSection.style.display = 'block';
  favsList.innerHTML = '';
  favCards.forEach(c => {
    const clone = c.cloneNode(true);
    favsList.appendChild(clone);
    hydrateCard(clone);
  });
}

function hideIfEmpty(sectionId, listId, masterCards) {
  const section = $('#' + sectionId);
  const list = $('#' + listId);
  if (!section || !list) return;

  const visibleInList = masterCards
    .filter(c => c.closest('#' + listId))
    .some(c => c.style.display !== 'none');

  section.style.display = visibleInList ? 'block' : 'none';
}

function renderNoResults(masterCards) {
  const noResults = $('#noResults');
  if (!noResults) return;

  const anyVisible = masterCards.some(c => c.style.display !== 'none');
  noResults.style.display = anyVisible ? 'none' : 'block';
}

function renderAll() {
  const masterCards = getAllMasterCards();

  // 카드 1회 하이드레이션(튤립/배지/즐겨찾기 버튼)
  masterCards.forEach(hydrateCard);

  // 필터 적용
  masterCards.forEach(card => {
    card.style.display = matchesFilter(card) ? '' : 'none';
  });

  // 시장 온도
  renderMarketTemp(masterCards);

  // 즐겨찾기 섹션(클론)
  renderFavorites(masterCards);

  // 섹션 숨김/표시
  hideIfEmpty('trendingSection', 'trendingList', masterCards);
  hideIfEmpty('otherSection', 'otherList', masterCards);

  // 결과 없음
  renderNoResults(masterCards);
}

function resetFilters() {
  searchQuery = '';
  statusFilter = 'all';
  const s = $('#searchInput');
  const f = $('#statusFilter');
  if (s) s.value = '';
  if (f) f.value = 'all';
  renderAll();
}

function renderTulipGuide() {
  const statuses = ['crashed', 'declining', 'stable', 'growing', 'overvalued'];
  const guide = $('#tulipGuide');
  if (!guide) return;

  if (typeof createTulipSVG !== 'function' || typeof getStatusLabel !== 'function' || typeof getStatusDescription !== 'function') {
    guide.innerHTML = '';
    return;
  }

  guide.innerHTML = statuses.map(status => `
    <div class="flex items-center gap-3 p-3 bg-slate-50 rounded-lg">
      ${createTulipSVG(status, 'sm')}
      <div>
        <p class="text-slate-700">${getStatusLabel(status)}</p>
        <p class="text-sm text-slate-500">${getStatusDescription(status)}</p>
      </div>
    </div>
  `).join('');
}

// Init
document.addEventListener('DOMContentLoaded', () => {
  const searchInput = $('#searchInput');
  const filterSelect = $('#statusFilter');

  initializeFavorites();

  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      searchQuery = e.target.value || '';
      renderAll();
    });
  }

  if (filterSelect) {
    filterSelect.addEventListener('change', (e) => {
      statusFilter = e.target.value || 'all';
      renderAll();
    });
  }

  renderAll();
  renderTulipGuide();
});
