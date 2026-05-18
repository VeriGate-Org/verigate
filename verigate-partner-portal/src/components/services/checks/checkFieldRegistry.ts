import type { FieldConfig } from "../document-verification/documentFieldConfigs";
import type { BffVerificationType } from "@/lib/types";
import { SA_BANKS } from "@/lib/sa-banks";

// ── Common fields shared across checks ───────────────────────────────

export type CommonFields = {
  idNumber: string;
  firstName: string;
  lastName: string;
};

// ── Check definition ─────────────────────────────────────────────────

export interface CheckDefinition {
  type: BffVerificationType;
  label: string;
  description: string;
  /** Which common subject fields this check sends to the BFF */
  usesCommonFields: ("idNumber" | "firstName" | "lastName")[];
  /** Extra fields shown when this check is selected */
  additionalFields: FieldConfig[];
  /** Build the metadata payload for executeVerification() */
  buildPayload: (
    common: CommonFields,
    additional: Record<string, string>,
  ) => Record<string, unknown>;
  /** Route to the individual service page for "View details" links */
  servicePath: string;
  /** Whether this check returns in real-time (true) or async */
  realTime: boolean;
}

// ── Check definitions ────────────────────────────────────────────────

export const CHECK_DEFINITIONS: Record<string, CheckDefinition> = {
  IDENTITY_VERIFICATION: {
    type: "IDENTITY_VERIFICATION",
    label: "Identity Verification",
    description: "Verify identity via DHA or HANIS",
    usesCommonFields: ["idNumber", "firstName", "lastName"],
    additionalFields: [
      {
        name: "includePhoto",
        label: "Include photo",
        description: "Request a photo from the DHA database.",
        type: "select",
        options: [
          { value: "false", label: "No" },
          { value: "true", label: "Yes" },
        ],
      },
      {
        name: "useHanis",
        label: "Use HANIS",
        description: "Query the HANIS database instead of standard DHA.",
        type: "select",
        options: [
          { value: "false", label: "No" },
          { value: "true", label: "Yes" },
        ],
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      firstName: c.firstName,
      lastName: c.lastName,
      includePhoto: a.includePhoto === "true",
      useHanis: a.useHanis === "true",
    }),
    servicePath: "/services/identity",
    realTime: true,
  },

  FRAUD_WATCHLIST_SCREENING: {
    type: "FRAUD_WATCHLIST_SCREENING",
    label: "Fraud Watchlist",
    description: "Screen against known fraud watchlists",
    usesCommonFields: ["firstName", "lastName", "idNumber"],
    additionalFields: [],
    buildPayload: (c) => ({
      firstName: c.firstName,
      lastName: c.lastName,
      idNumber: c.idNumber,
    }),
    servicePath: "/services/fraud-watchlist",
    realTime: true,
  },

  CREDIT_CHECK: {
    type: "CREDIT_CHECK",
    label: "Credit Check",
    description: "Run a credit bureau enquiry",
    usesCommonFields: ["idNumber"],
    additionalFields: [
      {
        name: "consent",
        label: "Credit consent",
        description:
          "I confirm the subject has provided written consent for this credit enquiry in terms of the National Credit Act (NCA).",
        type: "select",
        required: true,
        options: [
          { value: "false", label: "No" },
          { value: "true", label: "Yes - consent obtained" },
        ],
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      consent: a.consent === "true",
    }),
    servicePath: "/services/credit-check",
    realTime: true,
  },

  BANK_ACCOUNT_VERIFICATION: {
    type: "BANK_ACCOUNT_VERIFICATION",
    label: "Bank Account (AVS)",
    description: "Verify bank account details via AVS",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "bank",
        label: "Bank",
        description: "Select the issuing bank.",
        type: "select",
        required: true,
        options: SA_BANKS.map((b) => ({ value: b, label: b })),
      },
      {
        name: "accountNumber",
        label: "Account number",
        description: "Cheque or savings account number.",
        type: "text",
        required: true,
        placeholder: "e.g. 1234567890",
        inputMode: "numeric",
        maxLength: 16,
      },
      {
        name: "accountHolderName",
        label: "Account holder name",
        description: "Optional - for name match.",
        type: "text",
      },
      {
        name: "accountHolderSurname",
        label: "Account holder surname",
        description: "Optional - for name match.",
        type: "text",
      },
    ],
    buildPayload: (_c, a) => ({
      bank: a.bank,
      accountNumber: a.accountNumber,
      name: a.accountHolderName || "",
      surname: a.accountHolderSurname || "",
    }),
    servicePath: "/services/bank-account",
    realTime: true,
  },

  INCOME_VERIFICATION: {
    type: "INCOME_VERIFICATION",
    label: "Income Verification",
    description: "Verify income via payroll data",
    usesCommonFields: ["idNumber"],
    additionalFields: [
      {
        name: "employerName",
        label: "Employer name",
        description: "Name of the employer (optional).",
        type: "text",
        placeholder: "e.g. Acme Corp",
      },
      {
        name: "period",
        label: "Period",
        description: "How many months of income to verify.",
        type: "select",
        options: [
          { value: "3", label: "3 months" },
          { value: "6", label: "6 months" },
          { value: "12", label: "12 months" },
        ],
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      employerName: a.employerName || undefined,
      period: a.period ? Number(a.period) : undefined,
    }),
    servicePath: "/services/income",
    realTime: false,
  },

  TAX_COMPLIANCE_VERIFICATION: {
    type: "TAX_COMPLIANCE_VERIFICATION",
    label: "Tax Compliance",
    description: "Verify tax compliance status with SARS",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "taxReferenceNumber",
        label: "Tax reference number",
        description: "10-digit SARS tax reference number.",
        type: "text",
        required: true,
        placeholder: "e.g. 1234567890",
        inputMode: "numeric",
        maxLength: 10,
      },
    ],
    buildPayload: (_c, a) => ({
      taxReferenceNumber: a.taxReferenceNumber,
    }),
    servicePath: "/services/tax-compliance",
    realTime: true,
  },

  EMPLOYMENT_VERIFICATION: {
    type: "EMPLOYMENT_VERIFICATION",
    label: "Employment Verification",
    description: "Verify current or past employment",
    usesCommonFields: ["idNumber"],
    additionalFields: [
      {
        name: "employerName",
        label: "Employer name",
        description: "Name of the employer (optional).",
        type: "text",
        placeholder: "e.g. Acme Corp",
      },
      {
        name: "employeeNumber",
        label: "Employee number",
        description: "Employee ID / staff number (optional).",
        type: "text",
        placeholder: "e.g. EMP001",
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      employerName: a.employerName || undefined,
      employeeNumber: a.employeeNumber || undefined,
    }),
    servicePath: "/services/employment",
    realTime: false,
  },

  QUALIFICATION_VERIFICATION: {
    type: "QUALIFICATION_VERIFICATION",
    label: "Qualification Verification",
    description: "Verify academic qualifications",
    usesCommonFields: ["idNumber"],
    additionalFields: [
      {
        name: "qualificationType",
        label: "Qualification type",
        description: "Type of qualification to verify.",
        type: "select",
        options: [
          { value: "bachelors", label: "Bachelor's Degree" },
          { value: "masters", label: "Master's Degree" },
          { value: "diploma", label: "Diploma" },
          { value: "certificate", label: "Certificate" },
          { value: "doctorate", label: "Doctorate" },
        ],
      },
      {
        name: "institution",
        label: "Institution",
        description: "Name of the educational institution (optional).",
        type: "text",
        placeholder: "e.g. University of Cape Town",
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      qualificationType: a.qualificationType || undefined,
      institution: a.institution || undefined,
    }),
    servicePath: "/services/qualification",
    realTime: false,
  },

  NEGATIVE_NEWS_SCREENING: {
    type: "NEGATIVE_NEWS_SCREENING",
    label: "Negative News",
    description: "Screen for adverse media mentions",
    usesCommonFields: ["firstName", "lastName"],
    additionalFields: [],
    buildPayload: (c) => ({
      firstName: c.firstName,
      lastName: c.lastName,
    }),
    servicePath: "/services/negative-news",
    realTime: true,
  },

  SANCTIONS_SCREENING: {
    type: "SANCTIONS_SCREENING",
    label: "Sanctions & PEP",
    description: "Screen sanctions lists and PEP databases",
    usesCommonFields: ["firstName", "lastName"],
    additionalFields: [
      {
        name: "dateOfBirth",
        label: "Date of birth",
        description: "Improves matching accuracy (optional).",
        type: "text",
        placeholder: "YYYY-MM-DD",
      },
      {
        name: "nationality",
        label: "Nationality",
        description: "Country of nationality (optional).",
        type: "text",
        placeholder: "e.g. South African",
      },
    ],
    buildPayload: (c, a) => ({
      entityType: "Person",
      firstName: c.firstName,
      lastName: c.lastName,
      dateOfBirth: a.dateOfBirth || undefined,
      nationality: a.nationality || undefined,
      dataset: "sanctions",
      algorithm: "logic-v2",
      threshold: 0.7,
      topics: ["sanction", "role.pep"],
    }),
    servicePath: "/services/sanctions",
    realTime: true,
  },

  VERIFICATION_OF_PERSONAL_DETAILS: {
    type: "VERIFICATION_OF_PERSONAL_DETAILS",
    label: "Personal Details (DHA)",
    description: "Verify personal details against DHA records",
    usesCommonFields: ["idNumber", "firstName", "lastName"],
    additionalFields: [
      {
        name: "reason",
        label: "Reason for enquiry",
        description: "Optional reason for this verification.",
        type: "text",
        placeholder: "e.g. KYC onboarding",
      },
    ],
    buildPayload: (c, a) => ({
      idNumber: c.idNumber,
      firstName: c.firstName,
      surname: c.lastName,
      reason: a.reason || undefined,
    }),
    servicePath: "/services/personal-details",
    realTime: true,
  },

  COMPANY_VERIFICATION: {
    type: "COMPANY_VERIFICATION",
    label: "Company & Directors",
    description: "Verify company registration and directors via CIPC",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "regNumber",
        label: "Registration number",
        description: "CIPC registration number (e.g. 2019/123456/07).",
        type: "text",
        placeholder: "e.g. 2019/123456/07",
      },
      {
        name: "companyName",
        label: "Company name",
        description: "Registered company name.",
        type: "text",
        placeholder: "e.g. Acme Trading (Pty) Ltd",
      },
    ],
    buildPayload: (_c, a) => ({
      regNumber: a.regNumber || undefined,
      name: a.companyName || undefined,
    }),
    servicePath: "/services/company",
    realTime: true,
  },

  PROPERTY_OWNERSHIP_VERIFICATION: {
    type: "PROPERTY_OWNERSHIP_VERIFICATION",
    label: "Property / Deeds",
    description: "Search the deeds registry for property ownership",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "searchType",
        label: "Search type",
        description: "Search by owner ID or owner name.",
        type: "select",
        required: true,
        options: [
          { value: "ownerId", label: "Owner ID number" },
          { value: "ownerName", label: "Owner name" },
        ],
      },
      {
        name: "query",
        label: "Search query",
        description: "ID number or owner name to search.",
        type: "text",
        required: true,
        placeholder: "e.g. 9001015009087",
      },
      {
        name: "province",
        label: "Province",
        description: "Province to search in.",
        type: "select",
        required: true,
        options: [
          { value: "GP", label: "Gauteng" },
          { value: "WC", label: "Western Cape" },
          { value: "KZN", label: "KwaZulu-Natal" },
          { value: "EC", label: "Eastern Cape" },
          { value: "FS", label: "Free State" },
          { value: "LP", label: "Limpopo" },
          { value: "MP", label: "Mpumalanga" },
          { value: "NW", label: "North West" },
          { value: "NC", label: "Northern Cape" },
        ],
      },
    ],
    buildPayload: (_c, a) => ({
      searchType: a.searchType,
      query: a.query,
      province: a.province,
    }),
    servicePath: "/services/property-ownership",
    realTime: true,
  },

  VAT_VENDOR_VERIFICATION: {
    type: "VAT_VENDOR_VERIFICATION",
    label: "VAT Vendor",
    description: "Verify VAT vendor registration with SARS",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "vatNumber",
        label: "VAT number",
        description: "10-digit SARS VAT registration number.",
        type: "text",
        required: true,
        placeholder: "e.g. 4123456789",
        inputMode: "numeric",
        maxLength: 10,
      },
    ],
    buildPayload: (_c, a) => ({
      vatNumber: a.vatNumber,
    }),
    servicePath: "/services/vat-vendor-search",
    realTime: true,
  },

  // ── Immigration & Permits ──────────────────────────────────────────
  // These route through DOCUMENT_VERIFICATION with a specific documentType.

  DOCUMENT_VERIFICATION__ASYLUM: {
    type: "DOCUMENT_VERIFICATION",
    label: "Asylum Seeker Permit",
    description: "Verify asylum seeker permit against DHA records",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "permitNumber",
        label: "Permit number",
        description: "Asylum seeker permit number (8-15 characters).",
        type: "text",
        required: true,
        placeholder: "e.g. ASP12345678",
      },
      {
        name: "nationality",
        label: "Nationality",
        description: "Nationality of the permit holder.",
        type: "text",
        required: true,
        placeholder: "e.g. Congolese",
      },
      {
        name: "refugeeOffice",
        label: "Refugee reception office",
        description: "Office that issued the permit (optional).",
        type: "text",
        placeholder: "e.g. Pretoria Refugee Reception Office",
      },
    ],
    buildPayload: (_c, a) => ({
      documentType: "asylum_seeker_permit",
      documentNumber: a.permitNumber,
      permitNumber: a.permitNumber,
      nationality: a.nationality,
      refugeeOffice: a.refugeeOffice || undefined,
    }),
    servicePath: "/services/document-verification",
    realTime: false,
  },

  DOCUMENT_VERIFICATION__WORK_VISA: {
    type: "DOCUMENT_VERIFICATION",
    label: "General Work Visa",
    description: "Verify general work visa / permit against DHA records",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "permitNumber",
        label: "Permit number",
        description: "Work permit number (8-15 characters).",
        type: "text",
        required: true,
        placeholder: "e.g. WP12345678",
      },
      {
        name: "nationality",
        label: "Nationality",
        description: "Nationality of the permit holder.",
        type: "text",
        required: true,
        placeholder: "e.g. Nigerian",
      },
      {
        name: "employerName",
        label: "Employer name",
        description: "Name of the sponsoring employer.",
        type: "text",
        placeholder: "e.g. Karisani Technologies (Pty) Ltd",
      },
    ],
    buildPayload: (_c, a) => ({
      documentType: "general_work_permit",
      documentNumber: a.permitNumber,
      permitNumber: a.permitNumber,
      nationality: a.nationality,
      employerName: a.employerName || undefined,
    }),
    servicePath: "/services/document-verification",
    realTime: false,
  },

  DOCUMENT_VERIFICATION__PASSPORT: {
    type: "DOCUMENT_VERIFICATION",
    label: "Passport Verification",
    description: "Verify passport details",
    usesCommonFields: [],
    additionalFields: [
      {
        name: "passportNumber",
        label: "Passport number",
        description: "Alphanumeric passport number (6-9 characters).",
        type: "text",
        required: true,
        placeholder: "e.g. A12345678",
      },
      {
        name: "nationality",
        label: "Nationality",
        description: "Nationality of the passport holder.",
        type: "text",
        required: true,
        placeholder: "e.g. South African",
      },
      {
        name: "issuingCountry",
        label: "Issuing country",
        description: "Country that issued the passport (optional).",
        type: "text",
        placeholder: "e.g. ZA",
      },
    ],
    buildPayload: (_c, a) => ({
      documentType: "passport",
      documentNumber: a.passportNumber,
      passportNumber: a.passportNumber,
      nationality: a.nationality,
      issuingCountry: a.issuingCountry || undefined,
    }),
    servicePath: "/services/document-verification",
    realTime: false,
  },
};

// ── Check category groups (for the selector panel) ───────────────────

export const CHECK_CATEGORIES = [
  {
    label: "Identity & Personal",
    checks: [
      "IDENTITY_VERIFICATION",
      "VERIFICATION_OF_PERSONAL_DETAILS",
      "FRAUD_WATCHLIST_SCREENING",
    ],
  },
  {
    label: "Financial",
    checks: [
      "CREDIT_CHECK",
      "BANK_ACCOUNT_VERIFICATION",
      "INCOME_VERIFICATION",
      "TAX_COMPLIANCE_VERIFICATION",
      "VAT_VENDOR_VERIFICATION",
    ],
  },
  {
    label: "Business & Property",
    checks: [
      "COMPANY_VERIFICATION",
      "PROPERTY_OWNERSHIP_VERIFICATION",
    ],
  },
  {
    label: "Employment & Education",
    checks: ["EMPLOYMENT_VERIFICATION", "QUALIFICATION_VERIFICATION"],
  },
  {
    label: "Immigration & Permits",
    checks: [
      "DOCUMENT_VERIFICATION__ASYLUM",
      "DOCUMENT_VERIFICATION__WORK_VISA",
      "DOCUMENT_VERIFICATION__PASSPORT",
    ],
  },
  {
    label: "Screening",
    checks: ["NEGATIVE_NEWS_SCREENING", "SANCTIONS_SCREENING"],
  },
];
