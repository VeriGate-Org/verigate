import { Card, CardContent } from "@/components/ui/card";
import { TrendingUp, Users, Globe, Award } from "lucide-react";

const stats = [
  {
    icon: Users,
    value: "5M+",
    label: "Verifications Completed",
  },
  {
    icon: Globe,
    value: "150+",
    label: "Countries Covered",
  },
  {
    icon: TrendingUp,
    value: "99.8%",
    label: "Accuracy Rate",
  },
  {
    icon: Award,
    value: "24/7",
    label: "Expert Support",
  },
];

const TrustIndicators = () => {
  return (
    <section className="py-24 px-4 bg-secondary/30">
      <div className="container mx-auto max-w-6xl">
        {/* Section Header */}
        <div className="max-w-2xl mb-16">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">
            Trusted by Industry Leaders
          </h2>
          <p className="text-lg text-muted-foreground">
            Join thousands of organizations worldwide who rely on VeriGate for secure identity verification
          </p>
        </div>
        
        {/* Stats Grid */}
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat, index) => (
            <Card 
              key={index} 
              className="border-border/50 bg-card/50 backdrop-blur-sm hover:shadow-md transition-all duration-300"
            >
              <CardContent className="p-8 text-center space-y-3">
                <div className="w-12 h-12 rounded-full bg-accent/10 flex items-center justify-center mx-auto">
                  <stat.icon className="w-6 h-6 text-accent" />
                </div>
                <div className="text-4xl font-bold text-foreground">
                  {stat.value}
                </div>
                <div className="text-sm text-muted-foreground font-medium">
                  {stat.label}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default TrustIndicators;
