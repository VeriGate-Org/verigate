import { useRef, useState, useEffect } from "react";
import { statistics } from "@/data/social-proof";
import { Users, Globe, CheckCircle2, Target, Zap, Activity } from "lucide-react";

const iconMap = {
  users: Users,
  globe: Globe,
  check: CheckCircle2,
  target: Target,
  zap: Zap,
  activity: Activity,
};

// Individual stat counter component
function AnimatedStat({ stat, index, isVisible }: { stat: typeof statistics[0], index: number, isVisible: boolean }) {
  const [count, setCount] = useState(0);
  const Icon = iconMap[stat.icon as keyof typeof iconMap] || CheckCircle2;

  useEffect(() => {
    if (!isVisible) return;

    // Extract numeric value from string
    const numericMatch = stat.value.match(/[\d.]+/);
    if (!numericMatch) return;

    const targetValue = parseFloat(numericMatch[0]);
    const duration = 2000;
    const startTime = Date.now();
    const endTime = startTime + duration;

    const timer = setInterval(() => {
      const now = Date.now();
      const progress = Math.min((now - startTime) / duration, 1);
      
      // Easing function (easeOutExpo) for smooth deceleration
      const easeProgress = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);
      
      const currentCount = targetValue * easeProgress;
      setCount(currentCount);

      if (now >= endTime) {
        setCount(targetValue);
        clearInterval(timer);
      }
    }, 16); // ~60fps

    return () => clearInterval(timer);
  }, [isVisible, stat.value]);

  const formatValue = (count: number) => {
    if (stat.value.includes('+')) {
      return `${Math.floor(count)}+`;
    } else if (stat.value.includes('%')) {
      return `${count.toFixed(1)}%`;
    } else if (stat.value.includes('B')) {
      return `${count.toFixed(1)}B+`;
    } else if (stat.value.includes('<')) {
      return `<${Math.floor(count)}s`;
    }
    return Math.floor(count).toString();
  };

  return (
    <div
      className="text-center group"
      style={{
        opacity: isVisible ? 1 : 0,
        transform: isVisible ? 'translateY(0)' : 'translateY(20px)',
        transition: `all 0.6s ease-out ${index * 0.1}s`, // Stagger effect
      }}
    >
      {/* Icon */}
      <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-accent/10 flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-300">
        <Icon className="w-8 h-8 text-accent" />
      </div>

      {/* Animated Number */}
      <div className="text-4xl md:text-5xl font-bold mb-2 text-accent">
        {formatValue(count)}
      </div>

      {/* Label */}
      <div className="text-sm md:text-base text-primary-foreground/80">
        {stat.label}
      </div>
    </div>
  );
}

export function StatsCounter() {
  const [isVisible, setIsVisible] = useState(false);
  const sectionRef = useRef<HTMLDivElement>(null);

  // Intersection Observer to trigger animation when in view
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !isVisible) {
          setIsVisible(true);
        }
      },
      { threshold: 0.2 }
    );

    if (sectionRef.current) {
      observer.observe(sectionRef.current);
    }

    return () => observer.disconnect();
  }, [isVisible]);

  return (
    <section ref={sectionRef} className="py-20 bg-primary text-primary-foreground">
      <div className="container mx-auto max-w-6xl">
        {/* Header */}
        <div className="max-w-2xl mb-16">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">
            Trusted at Scale
          </h2>
          <p className="text-primary-foreground/80 text-lg">
            Numbers that speak to our reliability and global reach
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-8">
          {statistics.map((stat, index) => (
            <AnimatedStat key={stat.label} stat={stat} index={index} isVisible={isVisible} />
          ))}
        </div>

        {/* Additional Context */}
        <div className="mt-16 text-center">
          <p className="text-primary-foreground/60 text-sm">
            Join thousands of companies worldwide trusting VeriGate with their identity verification needs
          </p>
        </div>
      </div>
    </section>
  );
}
