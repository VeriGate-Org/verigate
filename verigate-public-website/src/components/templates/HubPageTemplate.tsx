import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowRight,
  Shield,
  ChevronRight,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getCategoryTheme, type CategoryKey } from "@/lib/categoryTheme";
import PageCTA from "@/components/PageCTA";
import { AnimatedSection, StaggeredList } from "@/components/AnimatedSection";

export interface HubPageData {
  title: string;
  subtitle: string;
  description: string;
  items: { slug: string; title: string; description: string; icon: string }[];
  basePath: string;
  ctaTitle: string;
  ctaDescription: string;
}

interface HubPageTemplateProps {
  data: HubPageData;
  category?: CategoryKey;
}

const HubPageTemplate = ({ data, category }: HubPageTemplateProps) => {
  const theme = getCategoryTheme(category ?? "company");

  return (
    <>
      {/* Hero Section */}
      <section className={`pt-24 pb-16 bg-gradient-to-br from-secondary via-background ${theme.heroBgTo} relative overflow-hidden`}>
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection className="max-w-3xl mx-auto text-center">
            <Badge className={`mb-4 text-sm ${theme.badgeBg} ${theme.badgeText} ${theme.badgeBorder}`}>
              <Shield className="w-3.5 h-3.5 mr-1.5" />
              {data.subtitle}
            </Badge>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
              {data.title}
            </h1>
            <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
              {data.description}
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/request-demo">
                  Request a Demo
                  <ArrowRight className="ml-2 w-4 h-4" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/pricing">View Pricing</Link>
              </Button>
            </div>
          </AnimatedSection>
        </div>
      </section>

      {/* Items Grid */}
      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <AnimatedSection className="max-w-2xl mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4 text-foreground">
              Explore All Options
            </h2>
            <p className="text-lg text-muted-foreground">
              Browse our complete range of services and capabilities
            </p>
          </AnimatedSection>

          <StaggeredList
            className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
            staggerDelay={0.08}
            containerDelay={0.15}
          >
            {data.items.map((item, index) => (
              <Link
                key={index}
                to={`${data.basePath}/${item.slug}`}
                className="group"
              >
                <Card className={`h-full border-border/50 ${theme.cardHoverBorder} transition-all duration-200 hover:shadow-xl ${theme.cardHoverShadow} group-hover:-translate-y-1 bg-gradient-to-br from-card to-card/50 hover:from-card hover:${theme.heroBgTo}`}>
                  <CardHeader>
                    <div className={`w-12 h-12 rounded-lg ${theme.iconBg} flex items-center justify-center mb-3 group-hover:bg-opacity-20 group-hover:scale-110 transition-all duration-200`}>
                      <Shield className={`w-6 h-6 ${theme.iconColor}`} />
                    </div>
                    <CardTitle className="text-xl flex items-center justify-between">
                      {item.title}
                      <ChevronRight className={`w-5 h-5 text-muted-foreground group-hover:${theme.iconColor} group-hover:translate-x-1 transition-all duration-200`} />
                    </CardTitle>
                    <CardDescription className="leading-relaxed">
                      {item.description}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <span className={`inline-flex items-center gap-1.5 text-sm font-medium ${theme.accentText} group-hover:gap-2.5 transition-all duration-200`}>
                      Learn more
                      <ArrowRight className="w-4 h-4" />
                    </span>
                  </CardContent>
                </Card>
              </Link>
            ))}
          </StaggeredList>
        </div>
      </section>

      {/* CTA Section */}
      <PageCTA variant="homepage" />
    </>
  );
};

export default HubPageTemplate;
