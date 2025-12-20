// Tulip SVG generation based on market status
function getTulipConfig(status) {
  const configs = {
    crashed: {
      petalColor: '#4A4A4A',
      stemColor: '#3A3A3A',
      leafColor: '#4A4A4A',
      broken: true,
      opacity: 0.5,
      glow: false,
      title: '꺾인 튤립 - 급격한 하락'
    },
    declining: {
      petalColor: '#8B7355',
      stemColor: '#6B5D4F',
      leafColor: '#7A6F5D',
      broken: false,
      opacity: 0.6,
      glow: false,
      title: '시든 튤립 - 시장 하락세'
    },
    stable: {
      petalColor: '#FF69B4',
      stemColor: '#27AE60',
      leafColor: '#27AE60',
      broken: false,
      opacity: 1,
      glow: false,
      title: '평범한 튤립 - 안정적 상승'
    },
    growing: {
      petalColor: '#F39C12',
      stemColor: '#27AE60',
      leafColor: '#27AE60',
      broken: false,
      opacity: 1,
      glow: true,
      title: '화려한 튤립 - 강한 성장세'
    },
    overvalued: {
      petalColor: '#9B59B6',
      stemColor: '#27AE60',
      leafColor: '#27AE60',
      broken: false,
      opacity: 1,
      glow: true,
      sparkle: true,
      title: '매우 화려한 튤립 - 과열 경고'
    }
  };
  
  return configs[status] || configs.stable;
}

function createTulipSVG(status, size = 'md') {
  const config = getTulipConfig(status);
  const sizes = {
    sm: 'w-12 h-12',
    md: 'w-16 h-16', 
    lg: 'w-24 h-24',
    xl: 'w-32 h-32'
  };
  
  const filterId = `glow-${status}-${Math.random().toString(36).substr(2, 9)}`;
  
  let svg = `
    <svg viewBox="0 0 100 100" class="${sizes[size]}" style="opacity: ${config.opacity}" title="${config.title}">
  `;
  
  // Add glow filter
  if (config.glow) {
    svg += `
      <defs>
        <filter id="${filterId}" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
    `;
  }
  
  // Stem
  if (config.broken) {
    svg += `
      <path d="M 50 60 Q 45 65 38 68" stroke="${config.stemColor}" stroke-width="3" fill="none" stroke-linecap="round"/>
      <path d="M 42 72 Q 40 81 38 90" stroke="${config.stemColor}" stroke-width="3" fill="none" stroke-linecap="round"/>
      <path d="M 38 68 L 40 70 L 38 72 L 42 72" stroke="${config.stemColor}" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
    `;
  } else {
    svg += `
      <path d="M 50 60 Q 48 75 46 90" stroke="${config.stemColor}" stroke-width="3" fill="none" stroke-linecap="round"/>
    `;
  }
  
  // Leaf
  svg += `
    <path d="M 46 75 Q 35 75 30 80 Q 35 78 46 77" fill="${config.leafColor}" opacity="0.8"/>
  `;
  
  // Petals
  const petalTransform = config.broken ? 'transform="rotate(-55 50 40)"' : '';
  const filterAttr = config.glow ? `filter="url(#${filterId})"` : '';
  
  svg += `
    <g ${petalTransform} ${filterAttr}>
      <ellipse cx="50" cy="35" rx="8" ry="18" fill="${config.petalColor}" opacity="0.9"/>
      <ellipse cx="42" cy="40" rx="8" ry="18" fill="${config.petalColor}" transform="rotate(-25 42 40)" opacity="0.85"/>
      <ellipse cx="58" cy="40" rx="8" ry="18" fill="${config.petalColor}" transform="rotate(25 58 40)" opacity="0.85"/>
      <ellipse cx="38" cy="42" rx="7" ry="16" fill="${config.petalColor}" transform="rotate(-40 38 42)" opacity="0.7"/>
      <ellipse cx="62" cy="42" rx="7" ry="16" fill="${config.petalColor}" transform="rotate(40 62 42)" opacity="0.7"/>
    </g>
  `;
  
  // Sparkle effect for overvalued
  if (config.sparkle) {
    svg += `
      <circle cx="45" cy="30" r="1.5" fill="#FFD700" opacity="0.8">
        <animate attributeName="opacity" values="0.8;1;0.8" dur="2s" repeatCount="indefinite"/>
      </circle>
      <circle cx="55" cy="32" r="1.5" fill="#FFD700" opacity="0.8">
        <animate attributeName="opacity" values="1;0.8;1" dur="2s" repeatCount="indefinite"/>
      </circle>
      <circle cx="50" cy="25" r="1.5" fill="#FFD700" opacity="0.8">
        <animate attributeName="opacity" values="0.8;1;0.8" dur="2.5s" repeatCount="indefinite"/>
      </circle>
    `;
  }
  
  svg += `</svg>`;
  
  return svg;
}

// Helper function to get status label
function getStatusLabel(status) {
  const labels = {
    crashed: '급격한 하락',
    declining: '시장 하락',
    stable: '시장 상승',
    growing: '강한 상승',
    overvalued: '과열 구간'
  };
  return labels[status] || '시장 상승';
}

function getStatusDescription(status) {
  const descriptions = {
    crashed: '급격한 하락',
    declining: '시장 하락세',
    stable: '안정적 상승',
    growing: '강한 성장세',
    overvalued: '과열 경고'
  };
  return descriptions[status] || '안정적 상승';
}

function getStatusColor(status) {
  const colors = {
    crashed: 'text-gray-800 bg-gray-200',
    declining: 'text-red-600 bg-red-50',
    stable: 'text-blue-600 bg-blue-50',
    growing: 'text-orange-600 bg-orange-50',
    overvalued: 'text-purple-600 bg-purple-50'
  };
  return colors[status] || 'text-blue-600 bg-blue-50';
}
