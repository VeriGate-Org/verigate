import {
  CircleDot,
  BadgeCheck,
  ShieldCheck,
  ShieldAlert,
  Flame,
  AlertTriangle,
  ArrowRightCircle,
  Sparkles,
  Star,
  Gem,
  Lightbulb,
  Leaf,
  CircleCheck,
  type LucideIcon,
} from "lucide-react";
import type { CategoryKey } from "./categoryTheme";

const categoryIcons: Record<CategoryKey | "general", LucideIcon[]> = {
  verification: [CircleDot, BadgeCheck, ShieldCheck, CircleCheck],
  compliance: [ShieldCheck, CircleCheck, BadgeCheck, CircleDot],
  fraud: [ShieldAlert, Flame, AlertTriangle, BadgeCheck],
  industry: [ArrowRightCircle, Sparkles, Star, Gem],
  platform: [Sparkles, Lightbulb, CircleDot, Star],
  company: [CircleDot, ArrowRightCircle, Sparkles, BadgeCheck],
  general: [CircleDot, ArrowRightCircle, Sparkles, Leaf],
};

export function getBulletIcon(
  category: CategoryKey | "general",
  index: number
): LucideIcon {
  const icons = categoryIcons[category] ?? categoryIcons.general;
  return icons[index % icons.length];
}
