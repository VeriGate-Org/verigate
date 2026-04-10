<svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full max-w-md opacity-90">
  <defs>
    <linearGradient id="indGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#0A2540"/><stop offset="100%" stop-color="#00B4D8"/>
    </linearGradient>
    <radialGradient id="indGlow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#00B4D8" stop-opacity="0.2"/><stop offset="100%" stop-color="#00B4D8" stop-opacity="0"/>
    </radialGradient>
  </defs>
  <circle cx="200" cy="150" r="120" fill="url(#indGlow)"/>
  <!-- Central hub -->
  <circle cx="200" cy="150" r="30" fill="#0A2540" fill-opacity="0.1" stroke="url(#indGrad)" stroke-width="2"/>
  <circle cx="200" cy="150" r="12" fill="#00B4D8" fill-opacity="0.3"/>
  <!-- Satellite nodes -->
  <circle cx="100" cy="80" r="20" fill="#0A2540" fill-opacity="0.05" stroke="#0A2540" stroke-width="1.5" stroke-opacity="0.3"/>
  <circle cx="300" cy="80" r="20" fill="#0A2540" fill-opacity="0.05" stroke="#0A2540" stroke-width="1.5" stroke-opacity="0.3"/>
  <circle cx="80" cy="180" r="18" fill="#0A2540" fill-opacity="0.05" stroke="#00B4D8" stroke-width="1" stroke-opacity="0.3"/>
  <circle cx="320" cy="180" r="18" fill="#0A2540" fill-opacity="0.05" stroke="#00B4D8" stroke-width="1" stroke-opacity="0.3"/>
  <circle cx="140" cy="250" r="16" fill="#0A2540" fill-opacity="0.05" stroke="#0A2540" stroke-width="1" stroke-opacity="0.2"/>
  <circle cx="260" cy="250" r="16" fill="#0A2540" fill-opacity="0.05" stroke="#0A2540" stroke-width="1" stroke-opacity="0.2"/>
  <!-- Connection lines -->
  <line x1="200" y1="150" x2="100" y2="80" stroke="#0A2540" stroke-width="1" stroke-opacity="0.2"/>
  <line x1="200" y1="150" x2="300" y2="80" stroke="#0A2540" stroke-width="1" stroke-opacity="0.2"/>
  <line x1="200" y1="150" x2="80" y2="180" stroke="#00B4D8" stroke-width="1" stroke-opacity="0.2"/>
  <line x1="200" y1="150" x2="320" y2="180" stroke="#00B4D8" stroke-width="1" stroke-opacity="0.2"/>
  <line x1="200" y1="150" x2="140" y2="250" stroke="#0A2540" stroke-width="1" stroke-opacity="0.15"/>
  <line x1="200" y1="150" x2="260" y2="250" stroke="#0A2540" stroke-width="1" stroke-opacity="0.15"/>
  <!-- Node icons (simplified buildings) -->
  <rect x="92" y="72" width="16" height="16" rx="2" fill="none" stroke="#0A2540" stroke-width="1" stroke-opacity="0.5"/>
  <rect x="292" y="72" width="16" height="16" rx="2" fill="none" stroke="#0A2540" stroke-width="1" stroke-opacity="0.5"/>
  <!-- Orbit ring -->
  <ellipse cx="200" cy="150" rx="140" ry="90" fill="none" stroke="#00B4D8" stroke-width="0.5" stroke-opacity="0.15" stroke-dasharray="6 4"/>
</svg>
