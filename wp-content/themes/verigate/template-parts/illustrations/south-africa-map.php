<svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full max-w-md opacity-90">
  <defs>
    <linearGradient id="mapGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#0A2540"/><stop offset="100%" stop-color="#00B4D8"/>
    </linearGradient>
    <radialGradient id="mapGlow" cx="50%" cy="50%" r="50%">
      <stop offset="0%" stop-color="#00B4D8" stop-opacity="0.15"/><stop offset="100%" stop-color="#00B4D8" stop-opacity="0"/>
    </radialGradient>
  </defs>
  <circle cx="200" cy="150" r="130" fill="url(#mapGlow)"/>
  <!-- Simplified SA outline -->
  <path d="M120 80 L160 60 L240 60 L300 80 L330 120 L340 170 L320 220 L280 250 L240 260 L200 270 L160 260 L120 230 L100 200 L90 160 L100 120 Z" fill="#0A2540" fill-opacity="0.05" stroke="url(#mapGrad)" stroke-width="2"/>
  <!-- City dots -->
  <circle cx="230" cy="200" r="5" fill="#00B4D8" fill-opacity="0.6"/><!-- Cape Town -->
  <text x="230" y="218" text-anchor="middle" font-size="7" fill="#0A2540" fill-opacity="0.5" font-family="sans-serif">Cape Town</text>
  <circle cx="280" cy="140" r="6" fill="#00B4D8" fill-opacity="0.7"/><!-- Johannesburg -->
  <text x="280" y="130" text-anchor="middle" font-size="7" fill="#0A2540" fill-opacity="0.5" font-family="sans-serif">Johannesburg</text>
  <circle cx="310" cy="170" r="4" fill="#00B4D8" fill-opacity="0.5"/><!-- Durban -->
  <text x="310" y="185" text-anchor="middle" font-size="7" fill="#0A2540" fill-opacity="0.4" font-family="sans-serif">Durban</text>
  <circle cx="250" cy="120" r="4" fill="#00B4D8" fill-opacity="0.4"/><!-- Pretoria -->
  <text x="250" y="112" text-anchor="middle" font-size="7" fill="#0A2540" fill-opacity="0.4" font-family="sans-serif">Pretoria</text>
  <!-- Connection arcs -->
  <path d="M230 200 Q255 160 280 140" stroke="#00B4D8" stroke-width="0.8" stroke-opacity="0.3" fill="none"/>
  <path d="M280 140 Q300 155 310 170" stroke="#00B4D8" stroke-width="0.8" stroke-opacity="0.3" fill="none"/>
  <path d="M250 120 Q265 130 280 140" stroke="#00B4D8" stroke-width="0.8" stroke-opacity="0.3" fill="none"/>
  <!-- Pulse rings -->
  <circle cx="280" cy="140" r="12" fill="none" stroke="#00B4D8" stroke-width="0.5" stroke-opacity="0.3"/>
  <circle cx="280" cy="140" r="20" fill="none" stroke="#00B4D8" stroke-width="0.3" stroke-opacity="0.15"/>
</svg>
