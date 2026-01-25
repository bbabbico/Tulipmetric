/**
 * Tulipmetric 공용 유틸리티 함수
 * 여러 페이지에서 공통으로 사용되는 함수들을 모아둔 파일입니다.
 */
const TulipUtils = {
    /**
     * 숫자에 천단위 콤마를 추가합니다.
     * @param {string|number} str - 콤마를 추가할 숫자
     * @returns {string} 콤마가 추가된 문자열
     */
    comma: function (str) {
        return String(str).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },

    /**
     * 숫자를 지정된 형식으로 포맷팅합니다.
     * @param {number|string} num - 포맷팅할 숫자
     * @param {string} type - 포맷 타입 (currency, marketCap, volume, per)
     * @returns {string} 포맷팅된 문자열
     */
    formatNumber: function (num, type) {
        if (num === null || num === undefined || num === '') return '-';
        const n = Number(num);
        if (!Number.isFinite(n)) return String(num);

        switch (type) {
            case 'currency':
                return '₩' + Math.round(n).toLocaleString();
            case 'marketCap':
                if (n >= 1e12) return (n / 1e12).toFixed(1) + '조';
                if (n >= 1e8) return (n / 1e8).toFixed(0) + '억';
                return '₩' + Math.round(n).toLocaleString();
            case 'volume':
                return Math.round(n).toLocaleString();
            case 'per':
                return (Math.round(n * 100) / 100).toFixed(2);
            default:
                return Math.round(n).toLocaleString();
        }
    },

    /**
     * 시가총액을 조/억 단위로 포맷팅합니다.
     * @param {number|string|bigint} raw - 원본 시가총액 값
     * @returns {string} 포맷팅된 문자열
     */
    formatMarketCap: function (raw) {
        if (raw == null || raw === "") return "-";

        let sign = "";
        let v = BigInt(raw);
        if (v < 0n) { sign = "-"; v = -v; }

        const JO = 1_000_000_000_000n; // 1조
        const EOK = 100_000_000n;       // 1억

        if (v >= JO) {
            const scaled = (v * 10n + JO / 2n) / JO;
            const intPart = scaled / 10n;
            const dec = scaled % 10n;
            return `${sign}${this.comma(intPart.toString())}.${dec}조`;
        }

        if (v >= EOK) {
            const eok = (v + EOK / 2n) / EOK;
            return `${sign}${this.comma(eok.toString())}억`;
        }

        return `${sign}₩${this.comma(v.toString())}`;
    },

    /**
     * 카테고리별 배지 색상 클래스
     */
    CATEGORY_BADGE_CLASS: {
        '일반': 'bg-slate-100 text-slate-700',
        '산업 분석': 'bg-blue-100 text-blue-700',
        '질문': 'bg-amber-100 text-amber-700',
        '토론': 'bg-green-100 text-green-700',
        '뉴스': 'bg-purple-100 text-purple-700',
    },

    /**
     * 카테고리 배지에 색상 클래스를 적용합니다.
     * @param {HTMLElement} badge - 배지 엘리먼트
     * @param {string} category - 카테고리명
     */
    applyCategoryBadgeStyle: function (badge, category) {
        if (!badge) return;
        badge.className = 'category-badge px-3 py-1 rounded-full text-sm';
        const colorClass = this.CATEGORY_BADGE_CLASS[category] || 'bg-slate-100 text-slate-700';
        badge.classList.add(...colorClass.split(' '));
    }
};

// 전역으로도 사용할 수 있도록 window에 등록
if (typeof window !== 'undefined') {
    window.TulipUtils = TulipUtils;
    // 하위 호환성을 위해 기존 함수도 등록
    window.formatNumber = TulipUtils.formatNumber.bind(TulipUtils);
    window.formatMarketCap = TulipUtils.formatMarketCap.bind(TulipUtils);
}
