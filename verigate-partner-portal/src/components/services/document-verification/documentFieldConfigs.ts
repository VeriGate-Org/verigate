export type FieldConfig = {
  name: string;
  label: string;
  description?: string;
  type: "text" | "select";
  options?: { value: string; label: string }[];
  required?: boolean;
  placeholder?: string;
  inputMode?: "text" | "numeric";
  maxLength?: number;
};

export const DOCUMENT_TYPE_GROUPS = [
  {
    label: "Identity Documents",
    types: [
      { value: "id_card", label: "SA ID Card" },
      { value: "passport", label: "Passport" },
      { value: "drivers_license", label: "Driver's License" },
    ],
  },
  {
    label: "Permits",
    types: [
      { value: "asylum_seeker_permit", label: "Asylum Seeker Permit" },
      { value: "general_work_permit", label: "General Work Permit" },
    ],
  },
  {
    label: "Business Documents",
    types: [
      { value: "b_bbee_certificate", label: "B-BBEE Certificate" },
      { value: "cipc_registration", label: "CIPC Registration" },
    ],
  },
  {
    label: "Financial Documents",
    types: [
      { value: "tax_certificate", label: "Tax Clearance Certificate" },
      { value: "financial_statement", label: "Financial Statement" },
    ],
  },
  {
    label: "Proof of Address",
    types: [
      { value: "utility_bill", label: "Utility Bill" },
    ],
  },
];

/** Flat lookup: document type value -> display label */
export const DOCUMENT_TYPE_LABELS: Record<string, string> = Object.fromEntries(
  DOCUMENT_TYPE_GROUPS.flatMap((g) => g.types.map((t) => [t.value, t.label]))
);

/** All known document type values */
export const KNOWN_DOCUMENT_TYPES = new Set(
  DOCUMENT_TYPE_GROUPS.flatMap((g) => g.types.map((t) => t.value))
);

const PROVINCE_OPTIONS = [
  { value: "GP", label: "Gauteng" },
  { value: "WC", label: "Western Cape" },
  { value: "KZN", label: "KwaZulu-Natal" },
  { value: "EC", label: "Eastern Cape" },
  { value: "FS", label: "Free State" },
  { value: "LP", label: "Limpopo" },
  { value: "MP", label: "Mpumalanga" },
  { value: "NW", label: "North West" },
  { value: "NC", label: "Northern Cape" },
];

export const DOCUMENT_FIELD_CONFIGS: Record<string, FieldConfig[]> = {
  id_card: [
    {
      name: "idNumber",
      label: "ID Number",
      description: "13-digit South African ID number.",
      type: "text",
      required: true,
      placeholder: "e.g. 9001015009087",
      inputMode: "numeric",
      maxLength: 13,
    },
  ],

  passport: [
    {
      name: "passportNumber",
      label: "Passport Number",
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
      label: "Issuing Country",
      description: "Country that issued the passport.",
      type: "text",
      required: false,
      placeholder: "e.g. ZA",
    },
  ],

  drivers_license: [
    {
      name: "licenseNumber",
      label: "License Number",
      description: "Alphanumeric license number (8-12 characters).",
      type: "text",
      required: true,
      placeholder: "e.g. DL12345678",
    },
    {
      name: "licenseCode",
      label: "License Code",
      description: "Select the license code category.",
      type: "select",
      required: true,
      options: [
        { value: "A", label: "A - Motorcycle" },
        { value: "A1", label: "A1 - Light motorcycle" },
        { value: "B", label: "B - Light motor vehicle" },
        { value: "C", label: "C - Heavy motor vehicle" },
        { value: "C1", label: "C1 - Medium motor vehicle" },
        { value: "EB", label: "EB - Articulated vehicle" },
      ],
    },
    {
      name: "province",
      label: "Province",
      description: "Province where the license was issued.",
      type: "select",
      required: false,
      options: PROVINCE_OPTIONS,
    },
  ],

  asylum_seeker_permit: [
    {
      name: "permitNumber",
      label: "Permit Number",
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
      label: "Refugee Office",
      description: "Refugee Reception Office that issued the permit.",
      type: "text",
      required: false,
      placeholder: "e.g. Pretoria Refugee Reception Office",
    },
  ],

  general_work_permit: [
    {
      name: "permitNumber",
      label: "Permit Number",
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
      label: "Employer Name",
      description: "Name of the sponsoring employer.",
      type: "text",
      required: true,
      placeholder: "e.g. Karisani Technologies (Pty) Ltd",
    },
  ],

  b_bbee_certificate: [
    {
      name: "certificateNumber",
      label: "Certificate Number",
      description: "B-BBEE certificate number (5-20 characters).",
      type: "text",
      required: true,
      placeholder: "e.g. BBEE-12345",
    },
    {
      name: "companyName",
      label: "Company Name",
      description: "Name of the certified company.",
      type: "text",
      required: true,
      placeholder: "e.g. Acme Trading (Pty) Ltd",
    },
    {
      name: "bbeeLevel",
      label: "B-BBEE Level",
      description: "Select the B-BBEE contributor level.",
      type: "select",
      required: false,
      options: [
        { value: "1", label: "Level 1" },
        { value: "2", label: "Level 2" },
        { value: "3", label: "Level 3" },
        { value: "4", label: "Level 4" },
        { value: "5", label: "Level 5" },
        { value: "6", label: "Level 6" },
        { value: "7", label: "Level 7" },
        { value: "8", label: "Level 8" },
      ],
    },
  ],

  cipc_registration: [
    {
      name: "registrationNumber",
      label: "Registration Number",
      description: "CIPC registration number (YYYY/NNNNNN/NN format).",
      type: "text",
      required: true,
      placeholder: "e.g. 2019/123456/07",
    },
    {
      name: "companyName",
      label: "Company Name",
      description: "Registered company name.",
      type: "text",
      required: true,
      placeholder: "e.g. Karisani Technologies (Pty) Ltd",
    },
  ],

  tax_certificate: [
    {
      name: "taxReferenceNumber",
      label: "Tax Reference Number",
      description: "10-digit SARS tax reference number.",
      type: "text",
      required: true,
      placeholder: "e.g. 1234567890",
      inputMode: "numeric",
      maxLength: 10,
    },
    {
      name: "taxPayerName",
      label: "Taxpayer Name",
      description: "Name of the taxpayer or company.",
      type: "text",
      required: true,
      placeholder: "e.g. Karisani Technologies (Pty) Ltd",
    },
  ],

  financial_statement: [
    {
      name: "referenceNumber",
      label: "Reference Number",
      description: "Document reference number.",
      type: "text",
      required: true,
      placeholder: "e.g. FS-2024-001",
    },
    {
      name: "companyName",
      label: "Company Name",
      description: "Company name on the statement.",
      type: "text",
      required: true,
      placeholder: "e.g. Karisani Technologies (Pty) Ltd",
    },
    {
      name: "financialYear",
      label: "Financial Year",
      description: "4-digit financial year.",
      type: "text",
      required: false,
      placeholder: "e.g. 2024",
      inputMode: "numeric",
      maxLength: 4,
    },
  ],

  utility_bill: [
    {
      name: "accountNumber",
      label: "Account Number",
      description: "Utility account number (5-20 characters).",
      type: "text",
      required: true,
      placeholder: "e.g. ACC-123456",
    },
    {
      name: "accountHolder",
      label: "Account Holder",
      description: "Name of the account holder.",
      type: "text",
      required: true,
      placeholder: "e.g. Thabo J. Mokoena",
    },
    {
      name: "address",
      label: "Address",
      description: "Physical address on the bill (min 10 characters).",
      type: "text",
      required: true,
      placeholder: "e.g. 42 Innovation Drive, Sandton, 2196",
    },
    {
      name: "municipality",
      label: "Municipality",
      description: "Municipality or service provider.",
      type: "text",
      required: false,
      placeholder: "e.g. City of Johannesburg",
    },
  ],
};
