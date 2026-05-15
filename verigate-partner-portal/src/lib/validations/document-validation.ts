import { validateSaId } from "@/lib/utils/sa-id-validation";

export type ValidationRule = {
  required?: boolean;
  pattern?: RegExp;
  minLength?: number;
  maxLength?: number;
  custom?: (value: string) => string | null;
  message: string;
};

function buildRules(documentType: string): Record<string, ValidationRule> {
  switch (documentType) {
    case "id_card":
      return {
        idNumber: {
          required: true,
          message: "Valid 13-digit SA ID number required.",
          custom: (v) => {
            const result = validateSaId(v);
            return result.valid ? null : result.errors[0] ?? "Invalid SA ID number.";
          },
        },
      };

    case "passport":
      return {
        passportNumber: {
          required: true,
          pattern: /^[A-Za-z][A-Za-z0-9]{5,8}$/,
          message: "Passport number must be 6-9 alphanumeric characters starting with a letter.",
        },
        nationality: {
          required: true,
          minLength: 2,
          message: "Nationality is required.",
        },
      };

    case "drivers_license":
      return {
        licenseNumber: {
          required: true,
          pattern: /^[A-Za-z0-9]{8,12}$/,
          message: "License number must be 8-12 alphanumeric characters.",
        },
        licenseCode: {
          required: true,
          message: "License code is required.",
        },
      };

    case "asylum_seeker_permit":
      return {
        permitNumber: {
          required: true,
          pattern: /^[A-Za-z0-9]{8,15}$/,
          message: "Permit number must be 8-15 alphanumeric characters.",
        },
        nationality: {
          required: true,
          minLength: 2,
          message: "Nationality is required.",
        },
      };

    case "general_work_permit":
      return {
        permitNumber: {
          required: true,
          pattern: /^[A-Za-z0-9]{8,15}$/,
          message: "Permit number must be 8-15 alphanumeric characters.",
        },
        nationality: {
          required: true,
          minLength: 2,
          message: "Nationality is required.",
        },
        employerName: {
          required: true,
          minLength: 2,
          message: "Employer name is required (min 2 characters).",
        },
      };

    case "b_bbee_certificate":
      return {
        certificateNumber: {
          required: true,
          pattern: /^[A-Za-z0-9\-]{5,20}$/,
          message: "Certificate number must be 5-20 alphanumeric characters.",
        },
        companyName: {
          required: true,
          minLength: 2,
          message: "Company name is required.",
        },
      };

    case "cipc_registration":
      return {
        registrationNumber: {
          required: true,
          pattern: /^\d{4}\/\d{6}\/\d{2}$/,
          message: "Registration number must be in YYYY/NNNNNN/NN format.",
        },
        companyName: {
          required: true,
          minLength: 2,
          message: "Company name is required.",
        },
      };

    case "tax_certificate":
      return {
        taxReferenceNumber: {
          required: true,
          pattern: /^\d{10}$/,
          message: "Tax reference number must be exactly 10 digits.",
        },
        taxPayerName: {
          required: true,
          minLength: 2,
          message: "Taxpayer name is required.",
        },
      };

    case "financial_statement":
      return {
        referenceNumber: {
          required: true,
          minLength: 1,
          message: "Reference number is required.",
        },
        companyName: {
          required: true,
          minLength: 2,
          message: "Company name is required.",
        },
        financialYear: {
          required: false,
          pattern: /^\d{4}$/,
          message: "Financial year must be a 4-digit year.",
        },
      };

    case "utility_bill":
      return {
        accountNumber: {
          required: true,
          pattern: /^[A-Za-z0-9\-]{5,20}$/,
          message: "Account number must be 5-20 alphanumeric characters.",
        },
        accountHolder: {
          required: true,
          minLength: 2,
          message: "Account holder name is required.",
        },
        address: {
          required: true,
          minLength: 10,
          message: "Address is required (min 10 characters).",
        },
      };

    default:
      return {};
  }
}

export function getValidationRules(documentType: string): Record<string, ValidationRule> {
  return buildRules(documentType);
}

export function validateField(
  documentType: string,
  fieldName: string,
  value: string
): string | null {
  const rules = buildRules(documentType);
  const rule = rules[fieldName];
  if (!rule) return null;

  const trimmed = value.trim();

  // If the field is not required and empty, skip validation
  if (!rule.required && trimmed.length === 0) return null;

  // Required check
  if (rule.required && trimmed.length === 0) {
    return rule.message;
  }

  // Custom validation (e.g. SA ID Luhn)
  if (rule.custom) {
    return rule.custom(trimmed);
  }

  // Min length
  if (rule.minLength && trimmed.length < rule.minLength) {
    return rule.message;
  }

  // Max length
  if (rule.maxLength && trimmed.length > rule.maxLength) {
    return rule.message;
  }

  // Pattern
  if (rule.pattern && !rule.pattern.test(trimmed)) {
    return rule.message;
  }

  return null;
}

export function validateAllFields(
  documentType: string,
  fields: Record<string, string>
): Record<string, string | null> {
  const rules = buildRules(documentType);
  const errors: Record<string, string | null> = {};

  for (const fieldName of Object.keys(rules)) {
    errors[fieldName] = validateField(documentType, fieldName, fields[fieldName] ?? "");
  }

  return errors;
}
