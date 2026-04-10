interface Stat {
  value: string;
  label: string;
}

interface StatsBarProps {
  stats: Stat[];
  className?: string;
}

export function StatsBar({ stats, className = "" }: StatsBarProps) {
  return (
    <div className={`flex flex-wrap items-center justify-center gap-x-8 gap-y-4 py-6 ${className}`}>
      {stats.map((stat, index) => (
        <div key={stat.label} className="flex items-center gap-x-8">
          <div className="text-center">
            <div className="text-2xl md:text-3xl font-bold text-accent">
              {stat.value}
            </div>
            <div className="text-xs md:text-sm text-muted-foreground">
              {stat.label}
            </div>
          </div>
          {index < stats.length - 1 && (
            <div className="hidden sm:block w-px h-10 bg-border" />
          )}
        </div>
      ))}
    </div>
  );
}
