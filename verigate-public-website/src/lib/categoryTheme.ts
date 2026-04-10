export type CategoryKey =
  | "verification"
  | "compliance"
  | "fraud"
  | "industry"
  | "platform"
  | "company";

export interface CategoryTheme {
  iconBg: string;
  iconColor: string;
  badgeBg: string;
  badgeText: string;
  badgeBorder: string;
  cardHoverBorder: string;
  cardHoverShadow: string;
  heroBgTo: string;
  sectionTint: string;
  accentBg: string;
  accentText: string;
}

const themes: Record<CategoryKey, CategoryTheme> = {
  verification: {
    iconBg: "bg-category-verification/10",
    iconColor: "text-category-verification",
    badgeBg: "bg-category-verification/10",
    badgeText: "text-category-verification",
    badgeBorder: "border-category-verification/30",
    cardHoverBorder: "hover:border-category-verification/50",
    cardHoverShadow: "hover:shadow-blue-500/10",
    heroBgTo: "to-category-verification/5",
    sectionTint: "bg-blue-50/30 dark:bg-blue-950/20",
    accentBg: "bg-category-verification/10",
    accentText: "text-category-verification",
  },
  compliance: {
    iconBg: "bg-category-compliance/10",
    iconColor: "text-category-compliance",
    badgeBg: "bg-category-compliance/10",
    badgeText: "text-category-compliance",
    badgeBorder: "border-category-compliance/30",
    cardHoverBorder: "hover:border-category-compliance/50",
    cardHoverShadow: "hover:shadow-teal-500/10",
    heroBgTo: "to-category-compliance/5",
    sectionTint: "bg-teal-50/30 dark:bg-teal-950/20",
    accentBg: "bg-category-compliance/10",
    accentText: "text-category-compliance",
  },
  fraud: {
    iconBg: "bg-category-fraud/10",
    iconColor: "text-category-fraud",
    badgeBg: "bg-category-fraud/10",
    badgeText: "text-category-fraud",
    badgeBorder: "border-category-fraud/30",
    cardHoverBorder: "hover:border-category-fraud/50",
    cardHoverShadow: "hover:shadow-amber-500/10",
    heroBgTo: "to-category-fraud/5",
    sectionTint: "bg-amber-50/30 dark:bg-amber-950/20",
    accentBg: "bg-category-fraud/10",
    accentText: "text-category-fraud",
  },
  industry: {
    iconBg: "bg-category-industry/10",
    iconColor: "text-category-industry",
    badgeBg: "bg-category-industry/10",
    badgeText: "text-category-industry",
    badgeBorder: "border-category-industry/30",
    cardHoverBorder: "hover:border-category-industry/50",
    cardHoverShadow: "hover:shadow-green-500/10",
    heroBgTo: "to-category-industry/5",
    sectionTint: "bg-green-50/30 dark:bg-green-950/20",
    accentBg: "bg-category-industry/10",
    accentText: "text-category-industry",
  },
  platform: {
    iconBg: "bg-category-platform/10",
    iconColor: "text-category-platform",
    badgeBg: "bg-category-platform/10",
    badgeText: "text-category-platform",
    badgeBorder: "border-category-platform/30",
    cardHoverBorder: "hover:border-category-platform/50",
    cardHoverShadow: "hover:shadow-purple-500/10",
    heroBgTo: "to-category-platform/5",
    sectionTint: "bg-purple-50/30 dark:bg-purple-950/20",
    accentBg: "bg-category-platform/10",
    accentText: "text-category-platform",
  },
  company: {
    iconBg: "bg-primary/10",
    iconColor: "text-primary",
    badgeBg: "bg-primary/10",
    badgeText: "text-primary",
    badgeBorder: "border-primary/30",
    cardHoverBorder: "hover:border-primary/50",
    cardHoverShadow: "hover:shadow-blue-900/10",
    heroBgTo: "to-primary/5",
    sectionTint: "bg-secondary/30",
    accentBg: "bg-primary/10",
    accentText: "text-primary",
  },
};

export function getCategoryTheme(category: CategoryKey): CategoryTheme {
  return themes[category] ?? themes.company;
}
