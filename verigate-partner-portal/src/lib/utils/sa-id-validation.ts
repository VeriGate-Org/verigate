/**
 * South African ID number validation and data extraction utilities.
 * TypeScript port of SAIdNumberValidityCheck.java Luhn algorithm.
 *
 * SA ID format: YYMMDD SSSS C A Z
 *   YYMMDD = date of birth
 *   SSSS   = gender sequence (0000-4999 = female, 5000-9999 = male)
 *   C      = citizenship (0 = SA citizen, 1 = permanent resident)
 *   A      = unused (historically race indicator, now always 8)
 *   Z      = Luhn check digit
 */

export interface SaIdValidationResult {
  valid: boolean;
  errors: string[];
  dateOfBirth: string | null;
  gender: "male" | "female" | "unknown";
  citizenship: "SA" | "Non-SA" | "unknown";
  age: number | null;
}

/**
 * Validates a South African ID number using the Luhn mod-10 algorithm
 * and extracts demographic information.
 */
export function validateSaId(idNumber: string): SaIdValidationResult {
  const errors: string[] = [];

  if (!idNumber || idNumber.length !== 13) {
    errors.push("ID number must be exactly 13 digits.");
    return { valid: false, errors, dateOfBirth: null, gender: "unknown", citizenship: "unknown", age: null };
  }

  if (!/^\d{13}$/.test(idNumber)) {
    errors.push("ID number must contain only digits.");
    return { valid: false, errors, dateOfBirth: null, gender: "unknown", citizenship: "unknown", age: null };
  }

  const dob = extractDateOfBirth(idNumber);
  if (!dob) {
    errors.push("ID number contains an invalid date of birth.");
  }

  if (!luhnCheck(idNumber)) {
    errors.push("ID number failed checksum validation.");
  }

  const gender = extractGender(idNumber);
  const citizenship = extractCitizenship(idNumber);
  const age = dob ? calculateAge(dob) : null;

  return {
    valid: errors.length === 0,
    errors,
    dateOfBirth: dob,
    gender,
    citizenship,
    age,
  };
}

/**
 * Luhn mod-10 checksum validation.
 * Ported from SAIdNumberValidityCheck.java.
 */
export function luhnCheck(idNumber: string): boolean {
  if (!/^\d{13}$/.test(idNumber)) return false;

  let sum = 0;
  let alternate = false;

  for (let i = idNumber.length - 1; i >= 0; i--) {
    let n = parseInt(idNumber[i], 10);
    if (alternate) {
      n *= 2;
      if (n > 9) {
        n = (n % 10) + 1;
      }
    }
    sum += n;
    alternate = !alternate;
  }

  return sum % 10 === 0;
}

/**
 * Extracts date of birth as ISO date string from SA ID number.
 */
export function extractDateOfBirth(idNumber: string): string | null {
  if (!/^\d{13}$/.test(idNumber)) return null;

  const yy = parseInt(idNumber.slice(0, 2), 10);
  const mm = parseInt(idNumber.slice(2, 4), 10);
  const dd = parseInt(idNumber.slice(4, 6), 10);

  if (mm < 1 || mm > 12 || dd < 1 || dd > 31) return null;

  const now = new Date();
  const currentYY = now.getFullYear() % 100;
  const century = yy > currentYY ? 1900 : 2000;
  const fullYear = century + yy;

  const date = new Date(fullYear, mm - 1, dd);
  if (
    date.getFullYear() !== fullYear ||
    date.getMonth() !== mm - 1 ||
    date.getDate() !== dd
  ) {
    return null;
  }

  if (date > now) return null;

  return date.toISOString().split("T")[0];
}

/**
 * Extracts gender from SA ID number.
 * Digits 7-10 (0-indexed 6-9): 0000-4999 = female, 5000-9999 = male.
 */
export function extractGender(idNumber: string): "male" | "female" | "unknown" {
  if (!/^\d{13}$/.test(idNumber)) return "unknown";
  const seq = parseInt(idNumber.slice(6, 10), 10);
  return seq >= 5000 ? "male" : "female";
}

/**
 * Extracts citizenship from SA ID number.
 * Digit 11 (0-indexed 10): 0 = SA citizen, 1 = permanent resident.
 */
export function extractCitizenship(idNumber: string): "SA" | "Non-SA" | "unknown" {
  if (!/^\d{13}$/.test(idNumber)) return "unknown";
  const c = idNumber[10];
  if (c === "0") return "SA";
  if (c === "1") return "Non-SA";
  return "unknown";
}

function calculateAge(dobIso: string): number {
  const bd = new Date(dobIso);
  const now = new Date();
  let age = now.getFullYear() - bd.getFullYear();
  const monthDiff = now.getMonth() - bd.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < bd.getDate())) {
    age--;
  }
  return age;
}
