"""
⚠️ 보안 권장:
- DB 비밀번호/API 키를 코드에 하드코딩하지 말고 환경변수로 넣으세요.
  - TULIP_DB_HOST, TULIP_DB_USER, TULIP_DB_PASSWORD, TULIP_DB_NAME
  - DATA_GO_KR_SERVICE_KEY (data.go.kr)
  - KOSIS_API_KEY (KOSIS)
"""

from __future__ import annotations

import os
from dataclasses import dataclass
from datetime import datetime
from functools import lru_cache
from typing import Any, Optional

import pandas as pd
import pymysql
import requests
from dotenv import load_dotenv

# .env 파일 로드 현재 디렉토리나 상위 디렉토리에서 찾음.
load_dotenv()

# -----------------------------
# Config (use env vars)
# -----------------------------

@dataclass(frozen=True)
class DbConfig:
    host: str = "mysql"
    user: str = os.getenv("DB_USERNAME", "user") #.env
    password: str = os.getenv("DB_PASSWORD", "") #.env
    db: str = "tulipmetric"
    charset: str = "utf8"


DATA_GO_KR_SERVICE_KEY = os.getenv("DATA_GO_KR_SERVICE_KEY", "") #.env
KOSIS_API_KEY = os.getenv("KOSIS_API_KEY", "") #.env

MARKET_ORDER = ['음식료·담배', '섬유·의류', '기타제조', '종이·목재', '화학', '제약', '비금속', '금속', '기계·장비', '전기·전자',
                '의료·정밀기기', '운송장비·부품', '유통', '전기·가스', '건설', '운송·창고', '통신', '증권', '보험', '일반서비스',
                '부동산', 'IT 서비스', '오락·문화']

KOSPI_PARAMS = {
    "serviceKey": DATA_GO_KR_SERVICE_KEY,
    "numOfRows": 958,
    "resultType": "json",
    "basDt": "20251229",
    "mrktCls": "KOSPI",
}

# data.go.kr 개별 주식 정보 API
STOCK_URL = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo"

REQUEST_TIMEOUT_SEC = 20


# -----------------------------
# KOSIS URL templates (apiKey만 환경변수로 주입)
# -----------------------------

CHART_URL_TEMPLATE = 'https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey={apiKey}&itmId=13103130657T1+&objL1=13102130657A.01+13102130657A.02+13102130657A.03+13102130657A.04+13102130657A.05+13102130657A.06+13102130657A.07+13102130657A.08+13102130657A.09+13102130657A.10+13102130657A.11+13102130657A.12+13102130657A.13+13102130657A.14+13102130657A.15+13102130657A.16+13102130657A.17+13102130657A.20+13102130657A.21+13102130657A.22+13102130657A.23+13102130657A.24+13102130657A.25+&objL2=&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&newEstPrdCnt=12&outputFields=NM+ITM_NM+PRD_DE+&orgId=343&tblId=DT_343_2010_S0190'
CAP_URL_TEMPLATE   = 'https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey={apiKey}&itmId=13103792812T1+&objL1=13102792812A.03+13102792812A.04+13102792812A.05+13102792812A.06+13102792812A.07+13102792812A.08+13102792812A.09+13102792812A.10+13102792812A.11+13102792812A.12+13102792812A.13+13102792812A.14+13102792812A.15+13102792812A.16+13102792812A.17+13102792812A.18+13102792812A.19+13102792812A.2003+13102792812A.2004+13102792812A.21+13102792812A.22+13102792812A.23+13102792812A.24+&objL2=&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&newEstPrdCnt=1&outputFields=NM+ITM_NM+&orgId=343&tblId=DT_343_2010_S0026'
PER_URL_TEMPLATE   = 'https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey={apiKey}&itmId=13103792793T1+&objL1=13102792793A.19+13102792793A.01+13102792793A.02+13102792793A.03+13102792793A.04+13102792793A.05+13102792793A.06+13102792793A.07+13102792793A.08+13102792793A.09+13102792793A.10+13102792793A.11+13102792793A.12+13102792793A.13+13102792793A.14+13102792793A.15+13102792793A.16+13102792793A.1703+13102792793A.1704+13102792793A.18+13102792793A.20+13102792793A.21+13102792793A.22+&objL2=13102792793B.02+&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&newEstPrdCnt=1&outputFields=NM+ITM_NM+&orgId=343&tblId=DT_343_2010_S0052'
COUNT_URL_TEMPLATE = 'https://kosis.kr/openapi/Param/statisticsParameterData.do?method=getList&apiKey={apiKey}&itmId=13103792790T1+&objL1=13102792790A.04+13102792790A.05+13102792790A.06+13102792790A.07+13102792790A.08+13102792790A.09+13102792790A.10+13102792790A.11+13102792790A.12+13102792790A.13+13102792790A.14+13102792790A.15+13102792790A.16+13102792790A.17+13102792790A.18+13102792790A.21+13102792790A.19+13102792790A.2003+13102792790A.2004+13102792790A.22+13102792790A.23+13102792790A.24+13102792790A.25+&objL2=13102792790B.02+&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=&format=json&jsonVD=Y&prdSe=M&newEstPrdCnt=1&outputFields=NM+ITM_NM+&orgId=343&tblId=DT_343_2010_S0016'


def kosis_url(template: str) -> str:
    if not KOSIS_API_KEY:
        raise RuntimeError("환경변수 KOSIS_API_KEY가 비어있습니다. KOSIS API 키를 설정하세요.")
    return template.format(apiKey=KOSIS_API_KEY)


# -----------------------------
# HTTP helpers
# -----------------------------

def get_json(url: str, params: Optional[dict[str, Any]] = None) -> Any:
    """GET + timeout + raise_for_status"""
    with requests.Session() as s:
        resp = s.get(url, params=params, timeout=REQUEST_TIMEOUT_SEC)
        resp.raise_for_status()
        return resp.json()


def to_int(value: Any) -> Optional[int]:
    """문자열/float/int/None -> int or None

    - '1,234' 같은 콤마 포함 숫자도 처리
    - '15253.53' 같은 소수 문자열도 int로 변환
    """
    if value is None:
        return None
    try:
        s = str(value).strip().replace(",", "")
        return int(float(s))
    except (TypeError, ValueError):
        return None


def normalize_rate(val: Any) -> Optional[float]:
    """
    fltRt 보정:
      -.63 -> -0.63, .63 -> 0.63, +.63 -> +0.63
    반환은 float(실수)로 통일.
    """
    if val is None:
        return None
    s = str(val).strip()
    if s.startswith("-."):
        s = "-0" + s[1:]
    elif s.startswith("+."):
        s = "+0" + s[1:]
    elif s.startswith("."):
        s = "0" + s
    try:
        return float(s)
    except ValueError:
        return None


# -----------------------------
# Data shaping helpers
# -----------------------------

def normalize_sector_name(name: Optional[str]) -> Optional[str]:
    if not name:
        return None
    return "기타제조" if name == "제조" else name


def order_markets(
    market_order: list[str],
    items: list[dict[str, Any]],
    *,
    name_key: str = "C1_NM",
    required_keys: tuple[str, ...] = (),
) -> tuple[list[dict[str, Any]], list[str]]:
    """
    market_order 순으로 정렬하고, 누락 업종은 원본 company.py와 동일하게
    "이웃 업종 데이터 복제 + 업종명만 변경" 방식으로 채웁니다.

    - 먼저 이전(왼쪽)에서 가장 가까운 업종을 찾고,
    - 없으면 이후(오른쪽)에서 가장 가까운 업종을 복제합니다.
    """
    by_name: dict[str, dict[str, Any]] = {}
    for it in items:
        nm = normalize_sector_name(it.get(name_key))
        if nm and nm not in by_name:
            cp = dict(it)  # shallow copy
            cp[name_key] = nm
            by_name[nm] = cp

    missing: list[str] = []
    ordered: list[dict[str, Any]] = []

    # 1) market_order 순서대로 넣되, 누락은 placeholder로 기록
    for nm in market_order:
        it = by_name.get(nm)
        if it is None:
            missing.append(nm)
            ordered.append({name_key: nm, "__missing__": True})
        else:
            ordered.append(it)

    # 2) 누락 placeholder는 이웃 업종을 복제해서 채움
    for i, it in enumerate(ordered):
        if not it.get("__missing__"):
            continue

        src: Optional[dict[str, Any]] = None

        # 왼쪽에서 가장 가까운 값
        for j in range(i - 1, -1, -1):
            if not ordered[j].get("__missing__"):
                src = ordered[j]
                break

        # 없으면 오른쪽에서 가장 가까운 값
        if src is None:
            for j in range(i + 1, len(ordered)):
                if not ordered[j].get("__missing__"):
                    src = ordered[j]
                    break

        if src is None:
            # 응답 자체가 비어있는 경우 등
            ph: dict[str, Any] = {name_key: it[name_key]}
            for k in required_keys:
                ph[k] = None
            ordered[i] = ph
            continue

        cp = dict(src)
        cp[name_key] = it[name_key]
        cp.pop("__missing__", None)
        for k in required_keys:
            cp.setdefault(k, None)
        ordered[i] = cp

    # 3) market_order에 없는 업종이 응답에 있으면 뒤에 붙임(예: '제조' 등)
    order_set = set(market_order)
    for nm, it in by_name.items():
        if nm not in order_set:
            ordered.append(it)

    return ordered, missing


def last_two_numbers(chart: list[Optional[int]]) -> Optional[tuple[int, int]]:
    """chart에서 뒤에서부터 None이 아닌 숫자 2개를 찾아 (prev, last) 반환"""
    vals: list[int] = []
    for v in reversed(chart):
        if isinstance(v, int):
            vals.append(v)
            if len(vals) == 2:
                break
    if len(vals) < 2:
        return None
    last, prev = vals[0], vals[1]
    return prev, last


def growth_rate_30d(chart: list[Optional[int]]) -> Optional[float]:
    pair = last_two_numbers(chart)
    if not pair:
        return None
    prev, last = pair
    if prev == 0:
        return None
    return (last - prev) / prev * 100.0


# -----------------------------
# KOSIS (industry) fetchers
# -----------------------------

def fetch_kosis_list(url: str) -> list[dict[str, Any]]:
    data = get_json(url)
    if isinstance(data, list):
        return data
    raise TypeError(f"KOSIS 응답이 list가 아님: {type(data)}")


def fetch_market_metric(url: str, *, out_key: str, remove_keys: set[str]) -> list[dict[str, Any]]:
    """
    KOSIS list 응답에서 DT를 out_key로 rename해서 int로 변환.
    remove_keys에 들어있는 키는 제거.
    """
    rows = fetch_kosis_list(url)

    out: list[dict[str, Any]] = []
    for row in rows:
        d = {k: v for k, v in row.items() if k not in remove_keys}
        sector = normalize_sector_name(d.get("C1_NM") or d.get("NM"))
        if sector is not None:
            d["C1_NM"] = sector  # merge 단계에서 C1_NM로 통일
        d[out_key] = to_int(d.get("DT"))
        d.pop("DT", None)
        out.append(d)

    ordered, missing = order_markets(MARKET_ORDER, out, required_keys=(out_key,))
    if missing:
        print("누락 업종:", missing)
    return ordered


def fetch_sector_chart(url: str, *, months: int = 12) -> dict[str, list[Optional[int]]]:
    """
    산업군 지수 추이(월별) -> {업종: [과거..최신] 12개}
    """
    rows = fetch_kosis_list(url)

    cleaned: list[dict[str, Any]] = []
    for row in rows:
        # ITM_NM은 필요 없어서 제거
        r = {k: v for k, v in row.items() if k != "ITM_NM"}
        sector = normalize_sector_name(r.get("C1_NM") or r.get("NM"))
        if not sector:
            continue
        r["C1_NM"] = sector
        r["DT"] = to_int(r.get("DT"))
        cleaned.append(r)

    return build_sector_dt_map_old_to_new(cleaned, months=months)


def _prev_months_old_to_new(latest_yyyymm: str, n: int) -> list[str]:
    """latest_yyyymm(YYYYMM) 기준 최근 n개월을 과거→최신 순으로 반환"""
    base = datetime.strptime(latest_yyyymm, "%Y%m")
    y, m = base.year, base.month

    tmp: list[str] = []
    for i in range(n):
        mm = m - i
        yy = y
        while mm <= 0:
            yy -= 1
            mm += 12
        tmp.append(f"{yy:04d}{mm:02d}")  # 최신 -> 과거
    return list(reversed(tmp))


def build_sector_dt_map_old_to_new(list2: list[dict[str, Any]], months: int = 12) -> dict[str, list[Optional[int]]]:
    """
    list2 원소 예:
      {"DT":"15253.53", "PRD_DE":"202411", "C1_NM":"제약", ...}

    반환:
      {"제약":[...12개...], ...}  # 과거 -> 최신 순
    """
    from collections import defaultdict

    sector_month_to_dt: dict[str, dict[str, Optional[int]]] = defaultdict(dict)
    sector_latest_month: dict[str, str] = {}

    for row in list2:
        sector = normalize_sector_name(row.get("C1_NM"))
        month = row.get("PRD_DE")
        dt_val = to_int(row.get("DT"))

        if not sector or not month:
            continue

        sector_month_to_dt[sector][month] = dt_val
        if sector not in sector_latest_month or month > sector_latest_month[sector]:
            sector_latest_month[sector] = month

    result: dict[str, list[Optional[int]]] = {}
    for sector, month_to_dt in sector_month_to_dt.items():
        latest = sector_latest_month[sector]
        months_list = _prev_months_old_to_new(latest, months)  # 과거 -> 최신
        result[sector] = [month_to_dt.get(m, None) for m in months_list]

    return result


# -----------------------------
# Merge & classify
# -----------------------------

def list_to_map_unique(lst: list[dict[str, Any]], key_field: str = "C1_NM") -> dict[str, dict[str, Any]]:
    """[{C1_NM:..., ...}] -> {C1_NM: dict} (중복이면 먼저 나온 값 유지)"""
    out: dict[str, dict[str, Any]] = {}
    for d in lst:
        k = d.get(key_field)
        if k and k not in out:
            out[k] = d
    return out


def merge_market_responses(
    chart_dict: dict[str, list[Optional[int]]],
    caps_list: list[dict[str, Any]],
    per_list: list[dict[str, Any]],
    count_list: list[dict[str, Any]],
    *,
    market_order: Optional[list[str]] = None,
) -> list[dict[str, Any]]:
    caps_by = list_to_map_unique(caps_list, "C1_NM")
    per_by = list_to_map_unique(per_list, "C1_NM")
    count_by = list_to_map_unique(count_list, "C1_NM")

    names = set(chart_dict) | set(caps_by) | set(per_by) | set(count_by)

    if market_order:
        ordered = [n for n in market_order if n in names]
        ordered += [n for n in names if n not in set(market_order)]
    else:
        ordered = list(names)

    merged: list[dict[str, Any]] = []
    for name in ordered:
        cap = caps_by.get(name, {}).get("total_market_cap")
        merged.append(
            {
                "market_name": name,
                "total_market_cap": (cap * 1_000_000) if isinstance(cap, int) else None,  # 원본과 동일하게 *1000000
                "market_per": per_by.get(name, {}).get("market_per"),
                "stock_count": count_by.get(name, {}).get("stock_count"),
                "chart": list(chart_dict.get(name, [])),  # deepcopy 대신 list()
            }
        )

    return classify_markets(merged)


def classify_markets(items: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """
    growth_rate30d / market_status / trending 계산.

    원본 버그:
      chart가 과거→최신 순인데, (chart[0] - chart[1])로 계산하면 '최신 30일'이 아니라 '가장 오래된 2개월' 차이가 됩니다.
    -> 최신 2개(뒤에서) 기준으로 수정.
    """
    out: list[dict[str, Any]] = []
    for it in items:
        chart = it.get("chart") or []
        if not isinstance(chart, list):
            chart = []
        it["growth_rate30d"] = growth_rate_30d(chart)

        per = it.get("market_per")
        if not isinstance(per, (int, float)):
            it["market_status"] = "unknown"
            it["trending"] = False
            out.append(it)
            continue

        if per > 40:
            it["market_status"] = "overvalued"
            it["trending"] = True
        elif per > 20:
            it["market_status"] = "growing"
            it["trending"] = False
        elif per > 10:
            it["market_status"] = "stable"
            it["trending"] = False
        elif per > 5:
            it["market_status"] = "declining"
            it["trending"] = False
        else:  # per <= 5 포함
            it["market_status"] = "crashed"
            it["trending"] = False

        out.append(it)

    return out


# -----------------------------
# Stock fetch (optional)
# -----------------------------

@lru_cache(maxsize=1)
def load_sector_map(xlsx_path: str) -> dict[str, str]:
    df = pd.read_excel(xlsx_path, dtype={"종목코드": str})
    return df.set_index("종목코드")["업종명"].to_dict()


def fetch_stock_prices(
    url: str,
    *,
    params: dict[str, Any],
    sector_xlsx_path: str = "./주식별 업종 분류.xlsx",
) -> list[dict[str, Any]]:
    if not DATA_GO_KR_SERVICE_KEY:
        raise RuntimeError("환경변수 DATA_GO_KR_SERVICE_KEY가 비어있습니다. data.go.kr serviceKey를 설정하세요.")

    data = get_json(url, params=params)
    items = (
        data.get("response", {})
        .get("body", {})
        .get("items", {})
        .get("item", [])
    )
    if isinstance(items, dict):
        items = [items]
    if not isinstance(items, list):
        raise TypeError(f"주식 API items 타입이 예상과 다름: {type(items)}")

    keys_to_remove = {"isinCd", "trPrc", "lstgStCnt", "hipr", "lopr", "mkp", "mrktCtg", "basDt"}
    out: list[dict[str, Any]] = []
    for row in items:
        d = {k: v for k, v in row.items() if k not in keys_to_remove}
        d["fltRt"] = normalize_rate(d.get("fltRt"))
        out.append(d)

    sector_map = load_sector_map(sector_xlsx_path)

    not_found_codes: list[str] = []
    for it in out:
        code = str(it.get("srtnCd", "")).zfill(6)
        sector = sector_map.get(code)
        it["market"] = sector if sector else None
        if sector is None:
            not_found_codes.append(code)

    if not_found_codes:
        print("업종명을 못 찾은 종목코드 개수:", len(not_found_codes))
        print("예시(최대 20개):", not_found_codes[:20])

    # 원본은 못 찾으면 아예 제거했는데(데이터 손실) 여기서는 None으로 보존
    return out


# -----------------------------
# DB helpers
# -----------------------------

def connect_db(cfg: DbConfig) -> pymysql.connections.Connection:
    return pymysql.connect(host=cfg.host, user=cfg.user, password=cfg.password, db=cfg.db, charset=cfg.charset)


def insert_markets(conn: pymysql.connections.Connection, markets: list[dict[str, Any]]) -> None:
    rows = []
    for m in markets:
        chart = m.get("chart") or []
        chart_csv = ",".join("" if v is None else str(v) for v in chart)
        rows.append(
            (
                m.get("market_name"),
                m.get("total_market_cap"),
                m.get("market_per"),
                m.get("stock_count"),
                chart_csv,
                m.get("growth_rate30d"),
                m.get("market_status"),
                m.get("trending"),
                "설명",
            )
        )

    sql_query = (
        "INSERT INTO market "
        "(`name`, `totalmarketcap`, `marketper`, `stockcount`, `chart`, "
        "`growth_rate30d`, `market_status`, `trending`, `description`) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
    )
    with conn.cursor() as cur:
        cur.executemany(sql_query, rows)
    conn.commit()


def insert_companies(conn: pymysql.connections.Connection, companies: list[dict[str, Any]]) -> None:
    """company 테이블에 종목 정보를 삽입합니다.

    company 컬럼(사용자 기준):
      id, itmsnm, clpr, vs, fltrt, trqu, mrkttotamt, market

    - id는 AUTO_INCREMENT로 가정 (INSERT에 포함하지 않음)
    - 원본 company.py는 업종명을 못 찾은 종목은 제거했음(= market이 빈 항목 제외)
      → 기본 동작도 동일하게 market이 None인 행은 insert에서 제외
    """

    rows = []
    for c in companies:
        market = c.get("market")
        if not market:
            continue  # 원본 동작 유지: 업종명이 없으면 제거

        rows.append(
            (
                c.get("itmsNm"),
                to_int(c.get("clpr")),
                to_int(c.get("vs")),
                normalize_rate(c.get("fltRt")),
                to_int(c.get("trqu")),
                to_int(c.get("mrktTotAmt")),
                market,
            )
        )

    if not rows:
        print("company insert: 삽입할 행이 없습니다(업종 매핑 실패 등).")
        return

    sql_query = (
        "INSERT INTO company "
        "(`itmsnm`, `clpr`, `vs`, `fltrt`, `trqu`, `mrkttotamt`, `market`) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s)"
    )

    # 만약 itmsnm 또는 srtnCd 등으로 UNIQUE 제약이 걸려있어서 중복 에러가 난다면,
    # 아래처럼 ON DUPLICATE KEY UPDATE를 사용하세요.
    # sql_query += " ON DUPLICATE KEY UPDATE clpr=VALUES(clpr), vs=VALUES(vs), fltrt=VALUES(fltrt), trqu=VALUES(trqu), mrkttotamt=VALUES(mrkttotamt), market=VALUES(market)"

    with conn.cursor() as cur:
        cur.executemany(sql_query, rows)
    conn.commit()



# -----------------------------
# Main
# -----------------------------

def main() -> None:
    chart_url = kosis_url(CHART_URL_TEMPLATE)
    cap_url = kosis_url(CAP_URL_TEMPLATE)
    per_url = kosis_url(PER_URL_TEMPLATE)
    count_url = kosis_url(COUNT_URL_TEMPLATE)

    chart_dict = fetch_sector_chart(chart_url, months=12)
    total_market_cap = fetch_market_metric(cap_url, out_key="total_market_cap", remove_keys={"ITM_NM"})
    market_per = fetch_market_metric(per_url, out_key="market_per", remove_keys={"ITM_NM", "C2_NM"})
    stock_count = fetch_market_metric(count_url, out_key="stock_count", remove_keys={"ITM_NM", "C2_NM"})

    result = merge_market_responses(
        chart_dict,
        total_market_cap,
        market_per,
        stock_count,
        market_order=MARKET_ORDER,
    )

    conn = connect_db(DbConfig())
    try:
        insert_markets(conn, result)

        # 개별 주식(company) 삽입
        companies = fetch_stock_prices(STOCK_URL, params=KOSPI_PARAMS)
        insert_companies(conn, companies)
    finally:
        conn.close()


def wait_for_init() -> bool:
    """
    init 입력 전까지 대기.
    init 입력하면 True(실행), quit/exit 입력하거나 Ctrl+C/D면 False(그냥 종료)
    """
    try:
        while True:
            cmd = input('최초 배포시 데이터베이스에 현재 산업군과 상장 종목 입력을 위해 "init" 를 입력하시오. (데이터 베이스 입력이 필요없을시 "quit") ').strip().lower()
            if cmd == "init":
                return True
            if cmd in ("quit", "exit", "q"):
                return False
    except (KeyboardInterrupt, EOFError):
        return False


if __name__ == "__main__":
    if wait_for_init():
        main()
