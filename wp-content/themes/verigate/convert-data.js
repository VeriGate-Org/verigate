/**
 * Convert VeriGate React TypeScript data files to JSON for WordPress import.
 * Usage: node convert-data.js
 * Output: _import-data/ directory with JSON files
 */
const fs = require('fs');
const path = require('path');

const REACT_SRC = path.resolve(__dirname, '../../../verigate-public-website/src/data');
const OUT_DIR = path.join(__dirname, '_import-data');

if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR, { recursive: true });

function loadTSArray(filename, varName) {
  const filepath = path.join(REACT_SRC, filename);
  let code = fs.readFileSync(filepath, 'utf8');

  // Remove import lines
  code = code.replace(/^import\s+.*$/gm, '');

  // Find the array assignment and extract just the array
  // Pattern: `export const varName: Type[] = [` ... `];`
  const regex = new RegExp(`export\\s+const\\s+${varName}[^=]*=\\s*`);
  const match = code.match(regex);
  if (!match) throw new Error(`Could not find 'export const ${varName}' in ${filename}`);

  // Get everything after the assignment
  let arrayCode = code.slice(match.index + match[0].length);

  // Find the matching end of the array by counting brackets
  let depth = 0;
  let endIdx = -1;
  for (let i = 0; i < arrayCode.length; i++) {
    if (arrayCode[i] === '[') depth++;
    else if (arrayCode[i] === ']') {
      depth--;
      if (depth === 0) { endIdx = i + 1; break; }
    }
  }
  if (endIdx === -1) throw new Error(`Could not find end of array for ${varName}`);

  arrayCode = arrayCode.slice(0, endIdx);

  // Evaluate the array as JavaScript (it's valid JS - no TS in the data itself)
  const result = eval('(' + arrayCode + ')');
  return result;
}

// 1. Verification Types
try {
  const data = loadTSArray('verificationTypes.ts', 'verificationTypes');
  fs.writeFileSync(path.join(OUT_DIR, 'verificationTypes.json'), JSON.stringify(data, null, 2));
  console.log(`✓ verificationTypes: ${data.length} entries`);
} catch (e) {
  console.error('✗ verificationTypes:', e.message);
}

// 2. Compliance
try {
  const data = loadTSArray('compliance.ts', 'complianceData');
  fs.writeFileSync(path.join(OUT_DIR, 'compliance.json'), JSON.stringify(data, null, 2));
  console.log(`✓ compliance: ${data.length} entries`);
} catch (e) {
  console.error('✗ compliance:', e.message);
}

// 3. Fraud Prevention
try {
  const data = loadTSArray('fraudPrevention.ts', 'fraudPreventionData');
  fs.writeFileSync(path.join(OUT_DIR, 'fraudPrevention.json'), JSON.stringify(data, null, 2));
  console.log(`✓ fraudPrevention: ${data.length} entries`);
} catch (e) {
  console.error('✗ fraudPrevention:', e.message);
}

// 4. Industries
try {
  const data = loadTSArray('industries.ts', 'industriesData');
  fs.writeFileSync(path.join(OUT_DIR, 'industries.json'), JSON.stringify(data, null, 2));
  console.log(`✓ industries: ${data.length} entries`);
} catch (e) {
  console.error('✗ industries:', e.message);
}

// 5. Social Proof (3 separate exports)
try {
  const logos = loadTSArray('social-proof.ts', 'customerLogos');
  const testimonials = loadTSArray('social-proof.ts', 'testimonials');
  const statistics = loadTSArray('social-proof.ts', 'statistics');
  fs.writeFileSync(path.join(OUT_DIR, 'social-proof.json'), JSON.stringify({
    customerLogos: logos,
    testimonials: testimonials,
    statistics: statistics,
  }, null, 2));
  console.log(`✓ social-proof: ${logos.length} logos, ${testimonials.length} testimonials, ${statistics.length} stats`);
} catch (e) {
  console.error('✗ social-proof:', e.message);
}

console.log(`\nJSON files written to ${OUT_DIR}`);
