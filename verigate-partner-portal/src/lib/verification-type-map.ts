import type { VerificationType, BffVerificationType } from "./types";
import {
  Shield, CreditCard, ShieldAlert, Building2, Map, Briefcase,
  Newspaper, AlertTriangle, FileCheck, GraduationCap, TrendingUp,
  Receipt, DollarSign, Fingerprint, CheckSquare, Eye, ScanFace, Camera,
  Search,
} from "lucide-react";
import type { ComponentType } from "react";

export interface VerificationTypeInfo {
  portalType: VerificationType;
  bffType: BffVerificationType;
  label: string;
  shortLabel: string;
  provider: string;
  icon: ComponentType<{ className?: string }>;
  route: string;
  category: "identity" | "financial" | "business" | "screening" | "composite" | "biometric";
}

const TYPE_MAP: Record<VerificationType, VerificationTypeInfo> = {
  ID: {
    portalType: "ID",
    bffType: "VERIFICATION_OF_PERSONAL_DETAILS",
    label: "Home Affairs ID Verification",
    shortLabel: "Home Affairs ID",
    provider: "DHA",
    icon: Shield,
    route: "/services/personal-details",
    category: "identity",
  },
  IDENTITY: {
    portalType: "IDENTITY",
    bffType: "IDENTITY_VERIFICATION",
    label: "Identity Verification",
    shortLabel: "Identity",
    provider: "DHA",
    icon: Fingerprint,
    route: "/services/identity",
    category: "identity",
  },
  DOCUMENT: {
    portalType: "DOCUMENT",
    bffType: "DOCUMENT_VERIFICATION",
    label: "Document Verification",
    shortLabel: "Document",
    provider: "DocumentVerify",
    icon: FileCheck,
    route: "/services/document-verification",
    category: "identity",
  },
  AVS: {
    portalType: "AVS",
    bffType: "BANK_ACCOUNT_VERIFICATION",
    label: "Bank Account Validation",
    shortLabel: "Bank Account",
    provider: "Qlink",
    icon: CreditCard,
    route: "/services/bank-account",
    category: "financial",
  },
  CREDIT: {
    portalType: "CREDIT",
    bffType: "CREDIT_CHECK",
    label: "Credit Check",
    shortLabel: "Credit",
    provider: "TransUnion",
    icon: TrendingUp,
    route: "/services/credit-check",
    category: "financial",
  },
  INCOME: {
    portalType: "INCOME",
    bffType: "INCOME_VERIFICATION",
    label: "Income Verification",
    shortLabel: "Income",
    provider: "PayrollVerify",
    icon: DollarSign,
    route: "/services/income",
    category: "financial",
  },
  TAX: {
    portalType: "TAX",
    bffType: "TAX_COMPLIANCE_VERIFICATION",
    label: "Tax Compliance Verification",
    shortLabel: "Tax Compliance",
    provider: "SARS",
    icon: Receipt,
    route: "/services/tax-compliance",
    category: "financial",
  },
  CIPC: {
    portalType: "CIPC",
    bffType: "COMPANY_VERIFICATION",
    label: "Company & Directors Verification",
    shortLabel: "Company & Directors",
    provider: "CIPC",
    icon: Building2,
    route: "/services/company",
    category: "business",
  },
  DEEDS: {
    portalType: "DEEDS",
    bffType: "PROPERTY_OWNERSHIP_VERIFICATION",
    label: "Deeds Registry Search",
    shortLabel: "Deeds Registry",
    provider: "Deeds Registry",
    icon: Map,
    route: "/services/property-ownership",
    category: "business",
  },
  EMPLOYMENT: {
    portalType: "EMPLOYMENT",
    bffType: "EMPLOYMENT_VERIFICATION",
    label: "Employment Verification",
    shortLabel: "Employment",
    provider: "EmployVerify",
    icon: Briefcase,
    route: "/services/employment",
    category: "business",
  },
  QUALIFICATION: {
    portalType: "QUALIFICATION",
    bffType: "QUALIFICATION_VERIFICATION",
    label: "Qualification Verification",
    shortLabel: "Qualification",
    provider: "SAQA",
    icon: GraduationCap,
    route: "/services/qualification",
    category: "business",
  },
  SANCTIONS: {
    portalType: "SANCTIONS",
    bffType: "SANCTIONS_SCREENING",
    label: "Sanctions & PEP Screening",
    shortLabel: "Sanctions & PEP",
    provider: "OpenSanctions",
    icon: ShieldAlert,
    route: "/services/sanctions",
    category: "screening",
  },
  NEGATIVE_NEWS: {
    portalType: "NEGATIVE_NEWS",
    bffType: "NEGATIVE_NEWS_SCREENING",
    label: "Negative News Screening",
    shortLabel: "Negative News",
    provider: "MediaScreen",
    icon: Newspaper,
    route: "/services/negative-news",
    category: "screening",
  },
  FRAUD_WATCHLIST: {
    portalType: "FRAUD_WATCHLIST",
    bffType: "FRAUD_WATCHLIST_SCREENING",
    label: "Fraud Watchlist Screening",
    shortLabel: "Fraud Watchlist",
    provider: "SAFPS",
    icon: AlertTriangle,
    route: "/services/fraud-watchlist",
    category: "screening",
  },
  WATCHLIST: {
    portalType: "WATCHLIST",
    bffType: "WATCHLIST_SCREENING",
    label: "Watchlist Screening",
    shortLabel: "Watchlist",
    provider: "OpenSanctions",
    icon: Eye,
    route: "/services/sanctions",
    category: "screening",
  },
  VAT_VENDOR: {
    portalType: "VAT_VENDOR",
    bffType: "VAT_VENDOR_VERIFICATION",
    label: "VAT Vendor Search",
    shortLabel: "VAT Vendor",
    provider: "SARS",
    icon: Search,
    route: "/services/vat-vendor-search",
    category: "financial",
  },
  FULL_VERIFICATION: {
    portalType: "FULL_VERIFICATION",
    bffType: "FULL_VERIFICATION",
    label: "Full Verification",
    shortLabel: "Full Verification",
    provider: "VeriGate",
    icon: CheckSquare,
    route: "/services/full-verification",
    category: "composite",
  },
  BIOMETRIC: {
    portalType: "BIOMETRIC",
    bffType: "BIOMETRIC_VERIFICATION",
    label: "Biometric Verification",
    shortLabel: "Biometric",
    provider: "Coming Soon",
    icon: ScanFace,
    route: "/services/biometric",
    category: "biometric",
  },
  LIVENESS: {
    portalType: "LIVENESS",
    bffType: "LIVENESS_CHECK",
    label: "Liveness Detection",
    shortLabel: "Liveness",
    provider: "Coming Soon",
    icon: Camera,
    route: "/services/liveness",
    category: "biometric",
  },
};

export function getTypeInfo(type: VerificationType): VerificationTypeInfo {
  return TYPE_MAP[type];
}

export function getBffType(portalType: VerificationType): BffVerificationType {
  return TYPE_MAP[portalType].bffType;
}

export function getPortalType(bffType: BffVerificationType): VerificationType | undefined {
  const entry = Object.values(TYPE_MAP).find((info) => info.bffType === bffType);
  return entry?.portalType;
}

export function getAllTypes(): VerificationTypeInfo[] {
  return Object.values(TYPE_MAP);
}

export function getTypesByCategory(category: VerificationTypeInfo["category"]): VerificationTypeInfo[] {
  return Object.values(TYPE_MAP).filter((info) => info.category === category);
}

export { TYPE_MAP };
