import { Card, CardContent } from "@/components/ui/card";
import type { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

interface FeatureCardProps {
  icon?: LucideIcon;
  title: string;
  description: string;
  iconBg?: string;
  iconColor?: string;
  className?: string;
}

export function FeatureCardIconTop({
  icon: Icon,
  title,
  description,
  iconBg = "bg-accent/10",
  iconColor = "text-accent",
  className,
}: FeatureCardProps) {
  return (
    <Card className={cn("border-border/50 hover:shadow-lg transition-all duration-200 hover:-translate-y-1 group", className)}>
      <CardContent className="p-6 space-y-4">
        {Icon && (
          <div className={cn("w-12 h-12 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200", iconBg)}>
            <Icon className={cn("w-6 h-6", iconColor)} />
          </div>
        )}
        <h3 className="text-xl font-semibold text-foreground">{title}</h3>
        <p className="text-muted-foreground leading-relaxed">{description}</p>
      </CardContent>
    </Card>
  );
}

export function FeatureCardAccentBorder({
  icon: Icon,
  title,
  description,
  iconColor = "text-accent",
  className,
}: FeatureCardProps & { borderColor?: string }) {
  return (
    <div className={cn("border-l-4 border-accent pl-6 py-4", className)}>
      <div className="flex items-center gap-3 mb-2">
        {Icon && <Icon className={cn("w-5 h-5 flex-shrink-0", iconColor)} />}
        <h3 className="text-lg font-semibold text-foreground">{title}</h3>
      </div>
      <p className="text-muted-foreground text-sm leading-relaxed">{description}</p>
    </div>
  );
}

interface NumberBadgeCardProps {
  number: number | string;
  title: string;
  description: string;
  className?: string;
}

export function FeatureCardNumberBadge({
  number,
  title,
  description,
  className,
}: NumberBadgeCardProps) {
  return (
    <div className={cn("flex gap-4", className)}>
      <div className="w-10 h-10 rounded-full bg-accent text-accent-foreground font-bold flex items-center justify-center flex-shrink-0 text-sm">
        {number}
      </div>
      <div>
        <h3 className="text-lg font-semibold text-foreground mb-1">{title}</h3>
        <p className="text-muted-foreground text-sm leading-relaxed">{description}</p>
      </div>
    </div>
  );
}

export function FeatureCardMinimal({
  icon: Icon,
  title,
  description,
  iconColor = "text-accent",
  className,
}: FeatureCardProps) {
  return (
    <div className={cn("flex items-start gap-3", className)}>
      {Icon && <Icon className={cn("w-5 h-5 flex-shrink-0 mt-0.5", iconColor)} />}
      <div>
        <span className="font-medium text-foreground">{title}</span>
        {description && (
          <span className="text-muted-foreground text-sm ml-1">— {description}</span>
        )}
      </div>
    </div>
  );
}
