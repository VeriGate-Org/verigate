import { luhnCheck, extractDateOfBirth, extractGender, extractCitizenship } from "@/lib/utils/sa-id-validation";
import type { SanctionsScreeningResponse, ScoredMatchEntity, EntityDetail, AdjacentEntity, ScreeningHistoryItem, EntityType, SanctionsScreeningRequest } from "@/lib/types/sanctions-screening";

const DEFAULT_DELAY_RANGE = { min: 600, max: 1400 };

const towns = ["Sandton", "Midrand", "Centurion", "Fourways", "Bryanston", "Randburg"];
const bondholders = ["ABSA", "Standard Bank", "FNB", "Nedbank", "Investec"];

export type PersonalDetailsRequest = {
  firstName?: string;
  surname?: string;
  idNumber: string;
  reason?: string;
};

export type PersonalDetailsResponse = {
  reference: string;
  provider: string;
  subject: { firstName: string; surname: string; idNumber: string };
  derived: {
    birthDate: string | null;
    gender: "male" | "female" | "unknown";
    citizenship: "SA" | "Non-SA" | "unknown";
    age: number | null;
  };
  validation: {
    idNumberValid: boolean;
    nameMatchConfidence: number;
    verified: boolean;
  };
  flags: { deceased: boolean; restricted: boolean };
  metadata?: { reason?: string };
  generatedAt: string;
};

export type CompanyRequest = {
  regNumber?: string;
  name?: string;
};

export interface DirectorSummary {
  name: string;
  idNumber: string;
  appointed: string;
  status: string;
}

export type CompanyResponse = {
  reference: string;
  provider: string;
  criteria: {
    regNumber: string;
    name: string;
  };
  entity: {
    regNumber: string;
    name: string;
    status: string;
    incorporationDate: string;
    registeredAddress: string;
    lastAnnualReturn: string;
  };
  directors: DirectorSummary[];
  complianceFlags: {
    annualReturnOverdue: boolean;
    deregistrationPending: boolean;
    addressMismatch: boolean;
  };
  generatedAt: string;
};

export type PropertyOwnershipRequest = {
  searchType: string;
  query: string;
  province: string;
};

export interface BondDetail {
  bondholder: string;
  amount: number;
  registered: string;
}

export interface MunicipalSummary {
  accountNumber: string;
  arrears: number;
  ratesFlag: boolean;
}

export interface PropertyRecord {
  propertyId: string;
  erfNumber: number;
  portion: number;
  township: string;
  province: string;
  titleDeed: string;
  deedNumber?: string;
  registrationDate: string;
  ownerName: string;
  ownerIdNumber: string;
  streetAddress?: string;
  coOwners: string[];
  currentBonds: BondDetail[];
  lastTransfer: { date: string; amount: number };
  municipal: MunicipalSummary;
}

export interface PropertyOwnershipResponse {
  summary: {
    totalProperties: number;
    totalActiveBonds: number;
    totalMunicipalFlags: number;
  };
  items: PropertyRecord[];
  criteria: {
    searchType: string;
    query: string;
    province: string;
  };
}

export type AvsRequest = {
  name?: string;
  surname?: string;
  accountNumber: string;
  bank: string;
};

export type AvsResponse = {
  correlationId: string;
  result: {
    avsStatus: string;
    bank: string;
    accountMasked: string;
    name?: string;
    surname?: string;
    // AVS Response Packet Fields
    accountStatus: string;
    accountFound: boolean;
    accountOpen: boolean;
    accountTypeMatch: boolean;
    accountLengthMatch: boolean;
    nameMatch: boolean;
    acceptsDebits: boolean;
    idMatch: boolean;
    emailMatch: boolean;
    phoneMatch: boolean;
    errorCode?: string;
    errorDescription?: string;
  };
};

export type SanctionsRequest = SanctionsScreeningRequest & { name?: string };
export type SanctionsResponse = SanctionsScreeningResponse;

export function generatePersonalDetailsResponse({
  firstName = "",
  surname = "",
  idNumber,
  reason = "",
}: PersonalDetailsRequest): PersonalDetailsResponse {
  if (!idNumber) {
    throw new Error("ID number is required");
  }

  const birthDateIso = extractDateOfBirth(idNumber);
  const gender = extractGender(idNumber);
  const citizenship = extractCitizenship(idNumber);
  const idNumberValid = luhnCheck(idNumber);

  let age: number | null = null;
  if (birthDateIso) {
    const bd = new Date(birthDateIso);
    const now = new Date();
    age = now.getFullYear() - bd.getFullYear() - (now < new Date(now.getFullYear(), bd.getMonth(), bd.getDate()) ? 1 : 0);
  }

  const nameMatchConfidence = Math.round(similarity(firstName, firstName) * 40 + similarity(surname, surname) * 60);
  const deceased = idNumber.endsWith("9");
  const restricted = idNumber.endsWith("99");
  const verified = idNumberValid && !deceased;

  return {
    reference: `pd-${Date.now()}`,
    provider: "DHA",
    subject: { firstName, surname, idNumber },
    derived: { birthDate: birthDateIso, gender, citizenship, age },
    validation: { idNumberValid, nameMatchConfidence, verified },
    flags: { deceased, restricted },
    metadata: { reason },
    generatedAt: new Date().toISOString(),
  };
}

export function generateCompanyResponse({ regNumber = "", name = "" }: CompanyRequest): CompanyResponse {
  if (!regNumber && !name) {
    throw new Error("Registration number or company name required");
  }

  const seedVal = (regNumber + name).split("").reduce((a: number, c: string) => a + c.charCodeAt(0), 0) || 42;
  const rnd = seeded(seedVal);

  const status = rnd() > 0.1 ? "Active" : "Deregistered";
  const incorporationDate = new Date(Date.now() - Math.floor(30 + rnd() * 7000) * 24 * 60 * 60 * 1000).toISOString();
  const lastAnnualReturn = new Date(Date.now() - Math.floor(rnd() * 900) * 24 * 60 * 60 * 1000).toISOString();
  const registeredAddress = `${Math.floor(10 + rnd() * 999)} Main Road, Johannesburg, GP`;

  const directors = Array.from({ length: 1 + Math.floor(rnd() * 4) }).map((_, i) => ({
    name: `Director ${i + 1}`,
    idNumber: `7${Math.floor(100000000000 + rnd() * 899999999999)}`,
    appointed: new Date(Date.now() - Math.floor(1000 + rnd() * 3000) * 24 * 60 * 60 * 1000).toISOString(),
    status: rnd() > 0.85 ? "Resigned" : "Active",
  }));

  const complianceFlags = {
    annualReturnOverdue: rnd() > 0.8,
    deregistrationPending: rnd() > 0.9,
    addressMismatch: rnd() > 0.85,
  };

  return {
    reference: `cipc-${Date.now()}`,
    provider: "CIPC",
    criteria: { regNumber, name },
    entity: {
      regNumber: regNumber || `20${Math.floor(10 + rnd() * 15)}${Math.floor(100000 / (1 + rnd()))}`,
      name: name || `Sample Company ${Math.floor(rnd() * 1000)}`,
      status,
      incorporationDate,
      registeredAddress,
      lastAnnualReturn,
    },
    directors,
    complianceFlags,
    generatedAt: new Date().toISOString(),
  };
}

export function generatePropertyOwnershipResponse({ searchType, query, province }: PropertyOwnershipRequest): PropertyOwnershipResponse {
  if (!searchType || !query) {
    throw new Error("searchType and query are required");
  }

  const seed = Array.from(query).reduce((acc: number, ch: string) => acc + ch.charCodeAt(0), 0);
  const rand = (min: number, max: number) => {
    const x = Math.sin(seed + min * 13.37 + max * 7.77) * 10000;
    return Math.floor((x - Math.floor(x)) * (max - min + 1) + min);
  };

  const items = Array.from({ length: Math.max(1, (seed % 3) + 1) }).map((_, i) => {
    const erfNumber = 1000 + rand(1, 999) + i;
    const portion = rand(1, 50);
    const township = towns[rand(0, towns.length - 1)];
    const titleDeed = `T${rand(10, 99)}/${rand(10000, 99999)}`;
    const deedNumber = `D${rand(10, 99)}/${rand(10000, 99999)}`;
    const registrationDate = new Date(Date.now() - rand(30, 3000) * 24 * 60 * 60 * 1000).toISOString();
    const ownerName = searchType === "ownerName" ? query : `Owner ${i + 1}`;
    const ownerIdNumber = searchType === "ownerId" ? query : `8${rand(0, 9)}0${rand(0, 9)}${rand(1000000000, 9999999999)}`;
    const streetAddress = `${rand(10, 999)} ${township} Avenue`;
    const bondsCount = rand(0, 2);
    const currentBonds = Array.from({ length: bondsCount }).map(() => ({
      bondholder: bondholders[rand(0, bondholders.length - 1)],
      amount: rand(200_000, 3_000_000),
      registered: new Date(Date.now() - rand(30, 2000) * 24 * 60 * 60 * 1000).toISOString(),
    }));
    const lastTransfer = { date: new Date(Date.now() - rand(60, 4000) * 24 * 60 * 60 * 1000).toISOString(), amount: rand(300_000, 5_000_000) };
    const municipal = { accountNumber: `ACC-${erfNumber}-${portion}`, arrears: rand(0, 1) ? 0 : rand(500, 15000), ratesFlag: rand(0, 4) === 0 };

    return {
      propertyId: `${province}-${township}-${erfNumber}-${portion}`,
      erfNumber,
      portion,
      township,
      province,
      titleDeed,
      deedNumber,
      registrationDate,
      ownerName,
      ownerIdNumber,
      streetAddress,
      coOwners: rand(0, 3) === 0 ? ["Co Owner One"] : [],
      currentBonds,
      lastTransfer,
      municipal,
    };
  });

  const summary = {
    totalProperties: items.length,
    totalActiveBonds: items.reduce((acc, p) => acc + (p.currentBonds?.length || 0), 0),
    totalMunicipalFlags: items.reduce((acc, p) => acc + (p.municipal?.ratesFlag ? 1 : 0), 0),
  };

  return { summary, items, criteria: { searchType, query, province } };
}

export function generateAvsResponse({ name, surname, accountNumber, bank }: AvsRequest): AvsResponse {
  if (!accountNumber || !bank) {
    throw new Error("Bank and account number are required");
  }

  // Simulate AVS response packet data based on account number
  const hasName = !!name && !!surname;
  const accountLen = String(accountNumber).length;
  
  // Deterministic responses based on account number patterns
  const lastDigit = parseInt(String(accountNumber).slice(-1));
  const isEvenAccount = lastDigit % 2 === 0;
  
  return {
    correlationId: `adhoc-avs-${Date.now()}`,
    result: {
      avsStatus: isEvenAccount ? "verified" : "partial_match",
      bank,
      accountMasked: `****${String(accountNumber).slice(-4)}`,
      name,
      surname,
      // AVS Response Packet Fields (simulated from XML response)
      accountStatus: "0", // 0 = Active
      accountFound: true, // AccFound: Y
      accountOpen: isEvenAccount, // AccOpen: varies
      accountTypeMatch: accountLen >= 10, // AccTypeMatch: based on length
      accountLengthMatch: accountLen >= 8 && accountLen <= 12, // AccLngthMatch
      nameMatch: hasName ? true : false, // NameMatch
      acceptsDebits: isEvenAccount, // AcptDebits
      idMatch: hasName ? true : false, // IdMatch
      emailMatch: lastDigit > 5, // EmailMatch: varies
      phoneMatch: lastDigit > 3, // TelnoMatch: varies
      errorCode: "7106",
      errorDescription: "ACCOUNT_OPEN_RESPONSE",
    },
  };
}

export function generateSanctionsResponse(request: SanctionsRequest): SanctionsResponse {
  const name = request.name || request.firstName || request.lastName || request.name || "";
  const fullName = request.firstName && request.lastName
    ? `${request.firstName} ${request.lastName}`
    : name;

  if (!fullName) {
    throw new Error("Name is required");
  }

  const upper = fullName.trim().toUpperCase();
  const seedVal = upper.split("").reduce((a: number, c: string) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);

  const correlationId = `adhoc-sanctions-${Date.now()}`;
  const datasets = ["us_ofac_sdn", "eu_fsf", "un_sc_sanctions"];
  const algorithm = request.algorithm || "logic-v1";
  const threshold = request.threshold ?? 0.5;
  const dataset = request.dataset || "default";

  // Determine match behavior based on name patterns
  const isHighRisk = upper.includes("PUTIN") || upper.includes("JONG");
  const hasSanctionsHit = upper.includes("Z") || isHighRisk;
  const isPepHit = upper.includes("MINISTER") || upper.includes("MP");

  // Generate 0-5 matches depending on name
  let matchCount = 0;
  if (isHighRisk) {
    matchCount = 3 + Math.floor(rnd() * 3); // 3-5
  } else if (hasSanctionsHit) {
    matchCount = 1 + Math.floor(rnd() * 2); // 1-2
  } else if (isPepHit) {
    matchCount = 1 + Math.floor(rnd() * 2); // 1-2
  } else {
    matchCount = rnd() > 0.7 ? 1 : 0; // mostly 0
  }
  matchCount = Math.min(matchCount, 5);

  const scoreBase = isHighRisk ? 0.85 : hasSanctionsHit ? 0.6 : isPepHit ? 0.5 : 0.3;

  const matches: ScoredMatchEntity[] = Array.from({ length: matchCount }).map((_, i) => {
    const score = Math.min(0.99, Math.max(0.3, scoreBase + (rnd() * 0.15) - i * 0.12));
    const roundedScore = Math.round(score * 100) / 100;
    const entityIsPep = isPepHit && i === 0;
    const entityIsSanctioned = hasSanctionsHit && i < 2;

    const nationalities = ["RU", "ZA", "KP", "IR", "SY", "US", "GB"];
    const schemas = ["Person", "LegalEntity", "Organization"];

    return {
      id: `Q${100000 + Math.floor(rnd() * 900000)}`,
      caption: i === 0 ? upper : `${upper} VARIANT ${i}`,
      schema: request.entityType === "Company" ? "LegalEntity" : schemas[Math.floor(rnd() * schemas.length)],
      score: roundedScore,
      datasets: datasets.slice(0, 1 + Math.floor(rnd() * datasets.length)),
      properties: {
        name: [i === 0 ? fullName : `${fullName} (alias ${i})`],
        birthDate: request.dateOfBirth ? [request.dateOfBirth] : [`19${60 + Math.floor(rnd() * 40)}-0${1 + Math.floor(rnd() * 9)}-${10 + Math.floor(rnd() * 18)}`],
        nationality: [request.nationality || nationalities[Math.floor(rnd() * nationalities.length)]],
      },
      features: {
        name_match: Math.round((0.7 + rnd() * 0.25) * 100) / 100,
        birthDate_match: Math.round((0.5 + rnd() * 0.5) * 100) / 100,
      },
      isPep: entityIsPep,
      isSanctioned: entityIsSanctioned,
      target: i === 0,
      firstSeen: new Date(Date.now() - Math.floor(365 + rnd() * 3650) * 24 * 60 * 60 * 1000).toISOString(),
      lastSeen: new Date(Date.now() - Math.floor(rnd() * 30) * 24 * 60 * 60 * 1000).toISOString(),
      lastChange: new Date(Date.now() - Math.floor(rnd() * 90) * 24 * 60 * 60 * 1000).toISOString(),
    };
  });

  // Determine outcome based on highest score
  const highestScore = matches.length > 0 ? Math.max(...matches.map(m => m.score)) : 0;
  let outcome: SanctionsScreeningResponse["outcome"];
  if (isHighRisk && highestScore >= 0.8) {
    outcome = "HARD_FAIL";
  } else if (highestScore >= 0.7) {
    outcome = "SOFT_FAIL";
  } else {
    outcome = "SUCCEEDED";
  }

  return {
    correlationId,
    outcome,
    matches,
    totalMatches: matches.length,
    algorithm,
    screenedAt: new Date().toISOString(),
    provider: "OpenSanctions",
    dataset,
    threshold,
  };
}

export function generateEntityDetail(entityId: string): EntityDetail {
  const seedVal = entityId.split("").reduce((a: number, c: string) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);

  const sanctionAuthorities = [
    { authority: "US Treasury - OFAC", program: "Specially Designated Nationals (SDN)" },
    { authority: "EU Council", program: "EU Financial Sanctions Facility" },
    { authority: "UN Security Council", program: "UN Consolidated Sanctions List" },
  ];

  const sanctionCount = 1 + Math.floor(rnd() * 3); // 1-3
  const sanctions = sanctionAuthorities.slice(0, sanctionCount).map((sa) => ({
    authority: sa.authority,
    program: sa.program,
    startDate: new Date(Date.now() - Math.floor(365 + rnd() * 3650) * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
    endDate: rnd() > 0.7 ? new Date(Date.now() + Math.floor(365 * rnd()) * 24 * 60 * 60 * 1000).toISOString().split("T")[0] : undefined,
    reason: rnd() > 0.5 ? "Designated for involvement in activities threatening international peace and security" : "Listed pursuant to relevant UN Security Council resolutions",
    status: rnd() > 0.2 ? "Active" : "Delisted",
  }));

  const pepRoles = [
    { role: "Minister of Finance", organization: "Government", country: "ZA" },
    { role: "Member of Parliament", organization: "National Assembly", country: "ZA" },
    { role: "Director General", organization: "Department of Trade", country: "ZA" },
  ];
  const pepCount = rnd() > 0.5 ? Math.floor(1 + rnd() * 2) : 0; // 0-2
  const positions = pepRoles.slice(0, pepCount).map((p) => ({
    role: p.role,
    organization: p.organization,
    country: p.country,
    startDate: new Date(Date.now() - Math.floor(365 + rnd() * 3650) * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
    endDate: rnd() > 0.6 ? new Date(Date.now() - Math.floor(rnd() * 365) * 24 * 60 * 60 * 1000).toISOString().split("T")[0] : undefined,
  }));

  const aliases = [
    `Alias ${entityId.slice(-3)} Alpha`,
    `Alias ${entityId.slice(-3)} Beta`,
    `${entityId.slice(-3)} Gamma Variant`,
    `A.K.A. ${entityId.slice(-4)}`,
  ].slice(0, 2 + Math.floor(rnd() * 3));

  const identifierTypes = ["passport", "national_id", "tax_id", "registration_number"];
  const identifiers = identifierTypes.slice(0, 1 + Math.floor(rnd() * 3)).map((type) => ({
    type,
    value: `${type.toUpperCase().slice(0, 2)}-${Math.floor(100000 + rnd() * 900000)}`,
    country: rnd() > 0.4 ? "ZA" : rnd() > 0.5 ? "RU" : "US",
  }));

  return {
    id: entityId,
    caption: `Entity ${entityId}`,
    schema: "Person",
    score: Math.round((0.6 + rnd() * 0.35) * 100) / 100,
    datasets: ["us_ofac_sdn", "eu_fsf", "un_sc_sanctions"].slice(0, 1 + Math.floor(rnd() * 3)),
    properties: {
      name: [`Entity ${entityId}`, ...aliases.slice(0, 2)],
      birthDate: [`19${60 + Math.floor(rnd() * 40)}-0${1 + Math.floor(rnd() * 9)}-${10 + Math.floor(rnd() * 18)}`],
      nationality: [rnd() > 0.5 ? "RU" : "ZA"],
    },
    features: {
      name_match: Math.round((0.7 + rnd() * 0.25) * 100) / 100,
      birthDate_match: Math.round((0.5 + rnd() * 0.5) * 100) / 100,
    },
    isPep: pepCount > 0,
    isSanctioned: sanctions.some((s) => s.status === "Active"),
    target: true,
    firstSeen: new Date(Date.now() - Math.floor(365 + rnd() * 3650) * 24 * 60 * 60 * 1000).toISOString(),
    lastSeen: new Date(Date.now() - Math.floor(rnd() * 30) * 24 * 60 * 60 * 1000).toISOString(),
    lastChange: new Date(Date.now() - Math.floor(rnd() * 90) * 24 * 60 * 60 * 1000).toISOString(),
    sanctions,
    positions,
    aliases,
    identifiers,
    referents: [`ref-${entityId}-001`, `ref-${entityId}-002`],
  };
}

export function generateAdjacentEntities(entityId: string): AdjacentEntity[] {
  const seedVal = entityId.split("").reduce((a: number, c: string) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);

  const relationships = ["associate", "family", "linked_to", "business_partner"];
  const captions = [
    "Nikolai Petrov",
    "Fatima Al-Hassan",
    "Global Trade Holdings Ltd",
    "Maria Ivanova",
    "Red Star Shipping Corp",
  ];
  const schemas = ["Person", "LegalEntity", "Person", "Person", "Company"];

  const count = 2 + Math.floor(rnd() * 3); // 2-4
  return Array.from({ length: Math.min(count, captions.length) }).map((_, i) => ({
    id: `Q${200000 + Math.floor(rnd() * 800000)}`,
    caption: captions[i],
    schema: schemas[i],
    relationship: relationships[i % relationships.length],
    datasets: ["us_ofac_sdn", "eu_fsf", "un_sc_sanctions"].slice(0, 1 + Math.floor(rnd() * 3)),
  }));
}

export function generateScreeningHistory(): ScreeningHistoryItem[] {
  const seedVal = Date.now() % 10000;
  const rnd = seeded(seedVal);

  const subjectNames = [
    "John Smith", "Vladimir Kuznetsov", "Zanele Dlamini", "Minister Patel",
    "Ahmed Al-Rashid", "Thabo Mokwena", "Olga Petrova", "Chen Wei",
    "MP Nkosi", "Global Trade Corp", "Red Diamond Shipping", "Fatima Osman",
    "Bongani Zulu", "Sipho Ndlovu", "Lerato van Wyk",
  ];

  const entityTypes: EntityType[] = ["Person", "Person", "Person", "Person", "Person", "Person", "Person", "Person", "Person", "Company", "Vessel", "Person", "Person", "Person", "Person"];
  const outcomes = ["SUCCEEDED", "HARD_FAIL", "SOFT_FAIL", "SUCCEEDED", "SUCCEEDED", "SUCCEEDED", "SOFT_FAIL", "SUCCEEDED", "SOFT_FAIL", "SUCCEEDED", "HARD_FAIL", "SUCCEEDED", "SOFT_FAIL", "SUCCEEDED", "SUCCEEDED"];
  const dispositions: (ScreeningHistoryItem["disposition"] | undefined)[] = [
    undefined, "CONFIRMED_MATCH", "PENDING_REVIEW", undefined, undefined,
    undefined, "FALSE_POSITIVE", undefined, "ESCALATED", undefined,
    "CONFIRMED_MATCH", undefined, "PENDING_REVIEW", undefined, undefined,
  ];

  const count = 10 + Math.floor(rnd() * 6); // 10-15
  return Array.from({ length: Math.min(count, subjectNames.length) }).map((_, i) => {
    const matchCount = outcomes[i] === "SUCCEEDED" ? (rnd() > 0.7 ? 1 : 0) : 1 + Math.floor(rnd() * 4);
    return {
      screeningId: `scr-${1000 + i}-${Math.floor(rnd() * 99999)}`,
      subjectName: subjectNames[i],
      entityType: entityTypes[i],
      outcome: outcomes[i],
      matchCount,
      screenedAt: new Date(Date.now() - Math.floor(i * 24 + rnd() * 48) * 60 * 60 * 1000).toISOString(),
      disposition: dispositions[i],
      provider: "OpenSanctions",
    };
  });
}

export async function mockPersonalDetails(request: PersonalDetailsRequest, delayMs?: number) {
  await wait(delayMs);
  return generatePersonalDetailsResponse(request);
}

export async function mockCompany(request: CompanyRequest, delayMs?: number) {
  await wait(delayMs);
  return generateCompanyResponse(request);
}

export async function mockPropertyOwnership(request: PropertyOwnershipRequest, delayMs?: number) {
  await wait(delayMs);
  return generatePropertyOwnershipResponse(request);
}

export async function mockAvs(request: AvsRequest, delayMs?: number) {
  await wait(delayMs);
  return generateAvsResponse(request);
}

export async function mockSanctions(request: SanctionsRequest, delayMs?: number) {
  await wait(delayMs);
  return generateSanctionsResponse(request);
}

function wait(delayMs?: number) {
  const duration = typeof delayMs === "number" ? delayMs : randomDelay();
  return new Promise((resolve) => setTimeout(resolve, duration));
}

function randomDelay(range = DEFAULT_DELAY_RANGE) {
  const { min, max } = range;
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function similarity(a: string, b: string) {
  a = (a || "").trim().toLowerCase();
  b = (b || "").trim().toLowerCase();
  if (!a || !b) return 0;
  if (a === b) return 1;
  const setA = new Set(a.split("").filter(Boolean));
  const setB = new Set(b.split("").filter(Boolean));
  const inter = Array.from(setA).filter((ch) => setB.has(ch)).length;
  const union = new Set([...Array.from(setA), ...Array.from(setB)]).size;
  return inter / union;
}

function seeded(seed: number) {
  return () => {
    seed = (seed * 1103515245 + 12345) % 2 ** 31;
    return seed / 2 ** 31;
  };
}

/* ===== NEW MOCK GENERATORS (10 additional verification types) ===== */

export type EmploymentRequest = { idNumber: string; employerName?: string; employeeNumber?: string };
export type EmploymentResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string; employerName: string; employeeNumber: string };
  employment: {
    verified: boolean;
    status: string;
    startDate: string;
    jobTitle: string;
    department: string;
  };
  generatedAt: string;
};

export function generateEmploymentResponse({ idNumber, employerName = "", employeeNumber = "" }: EmploymentRequest): EmploymentResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const verified = rnd() > 0.15;
  const titles = ["Software Engineer", "Accountant", "Sales Manager", "Operations Lead", "HR Specialist"];
  const departments = ["Engineering", "Finance", "Sales", "Operations", "Human Resources"];
  return {
    reference: `emp-${Date.now()}`,
    provider: "EmployVerify",
    subject: { idNumber, employerName: employerName || "Unknown Employer", employeeNumber: employeeNumber || `E${Math.floor(1000 + rnd() * 9000)}` },
    employment: {
      verified,
      status: verified ? "Currently Employed" : "Not Found",
      startDate: new Date(Date.now() - Math.floor(365 + rnd() * 3000) * 24 * 60 * 60 * 1000).toISOString(),
      jobTitle: titles[Math.floor(rnd() * titles.length)],
      department: departments[Math.floor(rnd() * departments.length)],
    },
    generatedAt: new Date().toISOString(),
  };
}

export type NegativeNewsRequest = { firstName: string; lastName: string };
export type NegativeNewsResponse = {
  reference: string;
  provider: string;
  subject: { firstName: string; lastName: string };
  screening: { hitCount: number; riskLevel: string; sources: string[] };
  generatedAt: string;
};

export function generateNegativeNewsResponse({ firstName, lastName }: NegativeNewsRequest): NegativeNewsResponse {
  if (!firstName || !lastName) throw new Error("First and last name required");
  const seedVal = (firstName + lastName).split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const hitCount = rnd() > 0.7 ? Math.floor(1 + rnd() * 3) : 0;
  const allSources = ["Reuters", "Bloomberg", "local media", "court records", "regulatory filings"];
  const sources = hitCount > 0 ? allSources.slice(0, Math.min(hitCount, 3)) : [];
  return {
    reference: `nn-${Date.now()}`,
    provider: "MediaScreen",
    subject: { firstName, lastName },
    screening: {
      hitCount,
      riskLevel: hitCount === 0 ? "LOW" : hitCount <= 2 ? "MEDIUM" : "HIGH",
      sources,
    },
    generatedAt: new Date().toISOString(),
  };
}

export type FraudWatchlistRequest = { firstName: string; lastName: string; idNumber: string };
export type FraudWatchlistResponse = {
  reference: string;
  provider: string;
  subject: { firstName: string; lastName: string; idNumber: string };
  watchlist: { listed: boolean; listType: string; dateAdded: string | null; caseReference: string | null };
  generatedAt: string;
};

export function generateFraudWatchlistResponse({ firstName, lastName, idNumber }: FraudWatchlistRequest): FraudWatchlistResponse {
  if (!firstName || !lastName || !idNumber) throw new Error("First name, last name, and ID number required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const listed = rnd() > 0.85;
  return {
    reference: `fw-${Date.now()}`,
    provider: "SAFPS",
    subject: { firstName, lastName, idNumber },
    watchlist: {
      listed,
      listType: listed ? "SAFPS Fraud Prevention" : "None",
      dateAdded: listed ? new Date(Date.now() - Math.floor(30 + rnd() * 1000) * 24 * 60 * 60 * 1000).toISOString() : null,
      caseReference: listed ? `SAFPS-${Math.floor(10000 + rnd() * 90000)}` : null,
    },
    generatedAt: new Date().toISOString(),
  };
}

export type DocumentVerificationRequest = { documentType: string; documentNumber: string };
export type DocumentVerificationResponse = {
  reference: string;
  provider: string;
  document: { documentType: string; documentNumber: string; verified: boolean; status: string; issuedDate: string; expiryDate: string | null };
  generatedAt: string;
};

export function generateDocumentVerificationResponse({ documentType, documentNumber }: DocumentVerificationRequest): DocumentVerificationResponse {
  if (!documentType || !documentNumber) throw new Error("Document type and number required");
  const seedVal = documentNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const verified = rnd() > 0.1;
  return {
    reference: `doc-${Date.now()}`,
    provider: "DocumentVerify",
    document: {
      documentType,
      documentNumber,
      verified,
      status: verified ? "Valid" : "Invalid",
      issuedDate: new Date(Date.now() - Math.floor(365 + rnd() * 3650) * 24 * 60 * 60 * 1000).toISOString(),
      expiryDate: rnd() > 0.3 ? new Date(Date.now() + Math.floor(365 + rnd() * 1825) * 24 * 60 * 60 * 1000).toISOString() : null,
    },
    generatedAt: new Date().toISOString(),
  };
}

export type QualificationRequest = { idNumber: string; qualificationType?: string; institution?: string };
export type QualificationResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string };
  qualification: { verified: boolean; qualificationType: string; institution: string; yearCompleted: number; status: string };
  generatedAt: string;
};

export function generateQualificationResponse({ idNumber, qualificationType = "", institution = "" }: QualificationRequest): QualificationResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const verified = rnd() > 0.15;
  const types = ["Bachelor's Degree", "Master's Degree", "Diploma", "Certificate", "Doctorate"];
  const institutions = ["University of Cape Town", "University of Pretoria", "Wits University", "Stellenbosch University", "UNISA"];
  return {
    reference: `qual-${Date.now()}`,
    provider: "SAQA",
    subject: { idNumber },
    qualification: {
      verified,
      qualificationType: qualificationType || types[Math.floor(rnd() * types.length)],
      institution: institution || institutions[Math.floor(rnd() * institutions.length)],
      yearCompleted: 2005 + Math.floor(rnd() * 18),
      status: verified ? "Confirmed" : "Not Found",
    },
    generatedAt: new Date().toISOString(),
  };
}

export type CreditCheckRequest = { idNumber: string; consent?: boolean };
export type CreditCheckResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string };
  credit: { score: number; riskGrade: string; accountsInGoodStanding: number; defaults: number; judgements: number; totalDebt: number };
  generatedAt: string;
};

export function generateCreditCheckResponse({ idNumber }: CreditCheckRequest): CreditCheckResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const score = Math.floor(300 + rnd() * 550);
  const grades = ["A", "B", "C", "D", "E"];
  const gradeIdx = score > 700 ? 0 : score > 600 ? 1 : score > 500 ? 2 : score > 400 ? 3 : 4;
  return {
    reference: `cred-${Date.now()}`,
    provider: "TransUnion",
    subject: { idNumber },
    credit: {
      score,
      riskGrade: grades[gradeIdx],
      accountsInGoodStanding: Math.floor(2 + rnd() * 8),
      defaults: Math.floor(rnd() * 3),
      judgements: rnd() > 0.8 ? Math.floor(1 + rnd() * 2) : 0,
      totalDebt: Math.floor(5000 + rnd() * 500000),
    },
    generatedAt: new Date().toISOString(),
  };
}

export type TaxComplianceRequest = { taxReferenceNumber: string };
export type TaxComplianceResponse = {
  reference: string;
  provider: string;
  subject: { taxReferenceNumber: string };
  compliance: { status: string; compliant: boolean; lastFilingDate: string; taxClearanceCertificate: boolean; outstandingReturns: number };
  generatedAt: string;
};

export function generateTaxComplianceResponse({ taxReferenceNumber }: TaxComplianceRequest): TaxComplianceResponse {
  if (!taxReferenceNumber) throw new Error("Tax reference number is required");
  const seedVal = taxReferenceNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const compliant = rnd() > 0.2;
  return {
    reference: `tax-${Date.now()}`,
    provider: "SARS",
    subject: { taxReferenceNumber },
    compliance: {
      status: compliant ? "Compliant" : "Non-Compliant",
      compliant,
      lastFilingDate: new Date(Date.now() - Math.floor(30 + rnd() * 365) * 24 * 60 * 60 * 1000).toISOString(),
      taxClearanceCertificate: compliant && rnd() > 0.3,
      outstandingReturns: compliant ? 0 : Math.floor(1 + rnd() * 4),
    },
    generatedAt: new Date().toISOString(),
  };
}

export type IncomeRequest = { idNumber: string; employerName?: string; period?: string };
export type IncomeResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string; employerName: string };
  income: { verified: boolean; grossMonthly: number; netMonthly: number; period: string; payFrequency: string };
  generatedAt: string;
};

export function generateIncomeResponse({ idNumber, employerName = "", period = "" }: IncomeRequest): IncomeResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const gross = Math.floor(15000 + rnd() * 85000);
  const net = Math.floor(gross * (0.7 + rnd() * 0.1));
  return {
    reference: `inc-${Date.now()}`,
    provider: "PayrollVerify",
    subject: { idNumber, employerName: employerName || "Employer Corp" },
    income: {
      verified: rnd() > 0.1,
      grossMonthly: gross,
      netMonthly: net,
      period: period || "Last 3 months",
      payFrequency: "Monthly",
    },
    generatedAt: new Date().toISOString(),
  };
}

export type IdentityRequest = { idNumber: string; firstName?: string; lastName?: string; includePhoto?: boolean };
export type IdentityResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string; firstName: string; lastName: string };
  identity: { verified: boolean; matchScore: number; photoMatch: boolean; livenessCheck: boolean };
  generatedAt: string;
};

export function generateIdentityResponse({ idNumber, firstName = "", lastName = "" }: IdentityRequest): IdentityResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const matchScore = Math.floor(60 + rnd() * 40);
  return {
    reference: `id-${Date.now()}`,
    provider: "DHA",
    subject: { idNumber, firstName: firstName || "Unknown", lastName: lastName || "Unknown" },
    identity: {
      verified: matchScore > 75,
      matchScore,
      photoMatch: rnd() > 0.2,
      livenessCheck: rnd() > 0.15,
    },
    generatedAt: new Date().toISOString(),
  };
}

// --- HANIS NPR Identity Verification ---

import type {
  HanisIdentityVerificationResponse,
  HanisVerificationStatus,
  HanisVerificationOutcome,
} from "@/lib/types/identity-verification";

export type HanisIdentityRequest = {
  idNumber: string;
  firstName?: string;
  lastName?: string;
  includePhoto?: boolean;
};

// 1x1 transparent PNG as base64 for mock photo
const MOCK_PHOTO_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";

export function generateHanisIdentityResponse({ idNumber, firstName = "", lastName = "", includePhoto = true }: HanisIdentityRequest): HanisIdentityVerificationResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);

  const birthDate = extractDateOfBirth(idNumber);
  const gender = extractGender(idNumber);
  const citizenship = extractCitizenship(idNumber);

  // Scenario routing by last digit
  const lastDigit = parseInt(idNumber.slice(-2, -1));

  // Deceased scenario
  if (lastDigit === 9) {
    return buildHanisResponse(idNumber, "DECEASED", "HARD_FAIL", {
      name: firstName || "JOHN",
      surname: lastName || "DOE",
      citizenshipStatus: "CITIZEN",
      birthCountry: "ZA",
      onHanis: true,
      onNpr: true,
      smartCardIssued: true,
      idIssueDate: "2015/01/15",
      idSequenceNo: "001",
      idnBlocked: false,
      maritalStatus: "Married",
      dateOfMarriage: "2010/03/20",
      deceased: true,
      dateOfDeath: "2023/06/15",
      matchDetails: "Individual is recorded as deceased. Date of death: 2023/06/15.",
      includePhoto,
    });
  }

  // Blocked scenario
  if (lastDigit === 8) {
    return buildHanisResponse(idNumber, "BLOCKED", "HARD_FAIL", {
      name: firstName || "BLOCKED",
      surname: lastName || "PERSON",
      citizenshipStatus: "CITIZEN",
      birthCountry: "ZA",
      onHanis: true,
      onNpr: true,
      smartCardIssued: false,
      idIssueDate: "",
      idSequenceNo: "002",
      idnBlocked: true,
      maritalStatus: "Single",
      dateOfMarriage: "",
      deceased: false,
      dateOfDeath: "",
      matchDetails: "ID number is blocked in the population register",
      includePhoto: false,
    });
  }

  // Not found scenario
  if (lastDigit === 7) {
    return buildHanisResponse(idNumber, "NOT_FOUND", "SOFT_FAIL", {
      name: "",
      surname: "",
      citizenshipStatus: "UNKNOWN",
      birthCountry: "",
      onHanis: false,
      onNpr: false,
      smartCardIssued: false,
      idIssueDate: "",
      idSequenceNo: "",
      idnBlocked: false,
      maritalStatus: "",
      dateOfMarriage: "",
      deceased: false,
      dateOfDeath: "",
      matchDetails: "ID number not found on NPR",
      includePhoto: false,
    });
  }

  // Default: successful verification
  const detectedName = gender === "male" ? "THABO" : "NOMSA";
  return buildHanisResponse(idNumber, "VERIFIED", "SUCCEEDED", {
    name: firstName || detectedName,
    surname: lastName || "MOKWENA",
    citizenshipStatus: citizenship === "SA" ? "CITIZEN" : "PERMANENT_RESIDENT",
    birthCountry: citizenship === "SA" ? "ZA" : "MZ",
    onHanis: true,
    onNpr: true,
    smartCardIssued: rnd() > 0.3,
    idIssueDate: birthDate ? `${new Date(birthDate).getFullYear() + 16}/05/10` : "2018/05/10",
    idSequenceNo: "000",
    idnBlocked: false,
    maritalStatus: rnd() > 0.4 ? "Married" : "Single",
    dateOfMarriage: rnd() > 0.4 ? "2016/09/14" : "",
    deceased: false,
    dateOfDeath: "",
    matchDetails: `Name: ${firstName || detectedName}, Surname: ${lastName || "MOKWENA"}, Smart Card: ${rnd() > 0.3 ? "Yes" : "No"}, On HANIS: Yes, On NPR: Yes, Birth Country: ZA`,
    includePhoto,
  });
}

function buildHanisResponse(
  idNumber: string,
  status: HanisVerificationStatus,
  outcome: HanisVerificationOutcome,
  data: {
    name: string; surname: string; citizenshipStatus: string; birthCountry: string;
    onHanis: boolean; onNpr: boolean; smartCardIssued: boolean; idIssueDate: string;
    idSequenceNo: string; idnBlocked: boolean; maritalStatus: string; dateOfMarriage: string;
    deceased: boolean; dateOfDeath: string; matchDetails: string; includePhoto: boolean;
  },
): HanisIdentityVerificationResponse {
  return {
    reference: `hanis-${Date.now()}`,
    provider: "DHA/HANIS",
    status,
    outcome,
    citizenDetails: {
      name: data.name,
      surname: data.surname,
      idNumber,
      citizenshipStatus: data.citizenshipStatus,
      birthCountry: data.birthCountry,
      onHanis: data.onHanis,
      onNpr: data.onNpr,
    },
    documentInfo: {
      smartCardIssued: data.smartCardIssued,
      idIssueDate: data.idIssueDate,
      idSequenceNo: data.idSequenceNo,
      idnBlocked: data.idnBlocked,
    },
    maritalInfo: {
      maritalStatus: data.maritalStatus,
      dateOfMarriage: data.dateOfMarriage,
    },
    vitalStatus: {
      status: data.deceased ? "DECEASED" : "ALIVE",
      deceased: data.deceased,
      dateOfDeath: data.dateOfDeath,
    },
    matchDetails: data.matchDetails,
    photoAvailable: data.includePhoto && status === "VERIFIED",
    photoBase64: data.includePhoto && status === "VERIFIED" ? MOCK_PHOTO_BASE64 : undefined,
    source: "mock",
    transactionId: `TXN-MOCK-${Date.now()}`,
    generatedAt: new Date().toISOString(),
  };
}

export async function mockHanisIdentity(request: HanisIdentityRequest, delayMs?: number) {
  await wait(delayMs);
  return generateHanisIdentityResponse(request);
}

// --- Bulk Identity Verification ---

import type {
  BulkVerificationJob,
  BulkVerificationResult,
  BulkResultsSummary,
  BillingGroupSelection,
} from "@/lib/types/bulk-identity-verification";

export type BulkCreateRequest = {
  idNumbers: string[];
  billingGroups: BillingGroupSelection;
};

export type BulkCreateResponse = {
  job: BulkVerificationJob;
};

export type BulkResultsResponse = {
  results: BulkVerificationResult[];
  summary: BulkResultsSummary;
};

const MOCK_NAMES = ["THABO", "NOMSA", "SIPHO", "LERATO", "BONGANI", "ZANELE", "TSHEPO", "PALESA"];
const MOCK_SURNAMES = ["MOKWENA", "NDLOVU", "MTHEMBU", "VAN DER MERWE", "NKOSI", "DLAMINI"];

export function generateBulkVerificationJob(idCount: number): BulkVerificationJob {
  return {
    jobId: `bulk-${Date.now()}`,
    partnerId: "partner-001",
    status: "COMPLETED",
    requestId: `REQ-${Math.floor(Math.random() * 999999)}`,
    billingGroups: "Y,Y,Y,Y,Y,N,N,N,N,N",
    idCount,
    createdAt: new Date(Date.now() - 3600000).toISOString(),
    uploadedAt: new Date(Date.now() - 3500000).toISOString(),
    completedAt: new Date(Date.now() - 1800000).toISOString(),
    errorCode: 0,
    errorDescription: null,
  };
}

export function generateBulkResults(idNumbers: string[]): BulkResultsResponse {
  const results: BulkVerificationResult[] = idNumbers.map((id, idx) => {
    const rndSeed = id.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
    const rnd = seeded(rndSeed);
    const lastDigit = parseInt(id.slice(-2, -1));

    // Simulate different outcomes
    const isDeceased = lastDigit === 9;
    const isNotFound = lastDigit === 7;
    const hasError = lastDigit === 6;

    if (hasError) {
      return {
        idNumber: id,
        names: "", surname: "", gender: "", dateOfBirth: "",
        birthCountry: "", citizenStatus: "", nationality: "",
        smartCardIssued: false, idCardIssueDate: "",
        idBookIssued: false, idBookIssueDate: "", idBlocked: false,
        maritalStatus: "", maidenName: "", marriageDate: "", divorceDate: "",
        dateOfDeath: "", deathPlace: "", causeOfDeath: "",
        errorCode: 800,
        success: false,
      };
    }

    return {
      idNumber: id,
      names: MOCK_NAMES[idx % MOCK_NAMES.length],
      surname: MOCK_SURNAMES[idx % MOCK_SURNAMES.length],
      gender: rnd() > 0.5 ? "Male" : "Female",
      dateOfBirth: `19${80 + Math.floor(rnd() * 20)}/0${1 + Math.floor(rnd() * 9)}/1${Math.floor(rnd() * 9)}`,
      birthCountry: "ZA",
      citizenStatus: "CITIZEN",
      nationality: "South African",
      smartCardIssued: rnd() > 0.3,
      idCardIssueDate: `20${10 + Math.floor(rnd() * 15)}/05/10`,
      idBookIssued: rnd() > 0.5,
      idBookIssueDate: rnd() > 0.5 ? `20${5 + Math.floor(rnd() * 15)}/03/15` : "",
      idBlocked: false,
      maritalStatus: rnd() > 0.4 ? "Married" : "Single",
      maidenName: rnd() > 0.7 ? "MABENA" : "",
      marriageDate: rnd() > 0.4 ? `20${10 + Math.floor(rnd() * 10)}/09/14` : "",
      divorceDate: rnd() > 0.9 ? `20${15 + Math.floor(rnd() * 5)}/06/20` : "",
      dateOfDeath: isDeceased ? `20${20 + Math.floor(rnd() * 5)}/06/15` : "",
      deathPlace: isDeceased ? "Johannesburg" : "",
      causeOfDeath: isDeceased ? "Natural causes" : "",
      errorCode: isNotFound ? 800 : 0,
      success: !isNotFound && !isDeceased,
    };
  });

  const summary: BulkResultsSummary = {
    total: results.length,
    verified: results.filter(r => r.success).length,
    notFound: results.filter(r => r.errorCode === 800).length,
    deceased: results.filter(r => r.dateOfDeath !== "").length,
    errors: results.filter(r => r.errorCode !== 0 && r.errorCode !== 800).length,
  };

  return { results, summary };
}

export async function mockBulkCreate(request: BulkCreateRequest, delayMs?: number): Promise<BulkCreateResponse> {
  await wait(delayMs ?? 1500);
  return { job: generateBulkVerificationJob(request.idNumbers.length) };
}

export async function mockBulkResults(idNumbers: string[], delayMs?: number): Promise<BulkResultsResponse> {
  await wait(delayMs ?? 800);
  return generateBulkResults(idNumbers);
}

export type FullVerificationRequest = { idNumber: string; firstName?: string; lastName?: string; subTypes?: string[] };
export type FullVerificationResponse = {
  reference: string;
  provider: string;
  subject: { idNumber: string; firstName: string; lastName: string };
  results: Array<{ type: string; status: string; provider: string; durationMs: number }>;
  overallStatus: string;
  generatedAt: string;
};

export function generateFullVerificationResponse({ idNumber, firstName = "", lastName = "", subTypes = [] }: FullVerificationRequest): FullVerificationResponse {
  if (!idNumber) throw new Error("ID number is required");
  const seedVal = idNumber.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const rnd = seeded(seedVal);
  const defaultTypes = ["Identity", "Credit", "Employment", "Sanctions"];
  const types = subTypes.length > 0 ? subTypes : defaultTypes;
  const results = types.map((type) => {
    const passed = rnd() > 0.15;
    return {
      type,
      status: passed ? "PASSED" : "FAILED",
      provider: type === "Identity" ? "DHA" : type === "Credit" ? "TransUnion" : type === "Employment" ? "EmployVerify" : "World-Check",
      durationMs: Math.floor(500 + rnd() * 3000),
    };
  });
  const allPassed = results.every((r) => r.status === "PASSED");
  return {
    reference: `full-${Date.now()}`,
    provider: "VeriGate",
    subject: { idNumber, firstName: firstName || "Unknown", lastName: lastName || "Unknown" },
    results,
    overallStatus: allPassed ? "PASSED" : "FAILED",
    generatedAt: new Date().toISOString(),
  };
}

/* ===== ASYNC MOCK WRAPPERS (new types) ===== */

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockEmployment(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateEmploymentResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockNegativeNews(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateNegativeNewsResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockFraudWatchlist(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateFraudWatchlistResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockDocumentVerification(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateDocumentVerificationResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockQualification(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateQualificationResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockCreditCheck(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateCreditCheckResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockTaxCompliance(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateTaxComplianceResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockIncome(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateIncomeResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockIdentity(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateIdentityResponse(request);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function mockFullVerification(request: any, delayMs?: number) {
  await wait(delayMs);
  return generateFullVerificationResponse(request);
}

/* ===== TENANT BRANDING MOCK DATA ===== */

import type { TenantBranding } from "@/lib/types/tenant-branding";

export const MOCK_TENANT_BRANDING: Record<string, TenantBranding> = {
  acme: {
    slug: "acme",
    name: "Acme Corp",
    logo: "/tenants/acme/logo.svg",
    logoDark: "/tenants/acme/logo-dark.svg",
    primaryColor: "#1a5276",
    accentColor: "#e67e22",
    tagline: "Trust, verified.",
  },
  fsca: {
    slug: "fsca",
    name: "FSCA",
    logo: "/tenants/fsca/logo.png",
    logoDark: "/tenants/fsca/logo-dark.png",
    faviconUrl: "/tenants/fsca/favicon.png",
    primaryColor: "#4562A1",
    accentColor: "#2D3E6E",
    tagline: "Financial Sector Conduct Authority",
  },
  default: {
    slug: "default",
    name: "VeriGate",
    primaryColor: "#0972d3",
    accentColor: "#ec7211",
  },
};
