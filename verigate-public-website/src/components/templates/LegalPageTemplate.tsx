import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { FileText, List } from "lucide-react";
import { AnimatedSection } from "@/components/AnimatedSection";

interface LegalPageTemplateProps {
  title: string;
  lastUpdated: string;
  sections: { id: string; title: string; content: string }[];
}

const LegalPageTemplate = ({ title, lastUpdated, sections }: LegalPageTemplateProps) => {
  return (
    <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 bg-gradient-to-br from-secondary via-background to-primary/5 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-mesh opacity-30" />
        <div className="container mx-auto max-w-6xl relative z-10">
          <AnimatedSection className="max-w-3xl mx-auto text-center">
            <Badge variant="secondary" className="mb-4 text-sm">
              <FileText className="w-3.5 h-3.5 mr-1.5" />
              Legal
            </Badge>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground">
              {title}
            </h1>
            <p className="text-lg text-muted-foreground">
              Last updated: {lastUpdated}
            </p>
          </AnimatedSection>
        </div>
      </section>

      {/* Content Section */}
      <section className="py-16 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="flex flex-col lg:flex-row gap-12">
            {/* Sidebar TOC - Desktop Only */}
            <aside className="hidden lg:block lg:w-72 flex-shrink-0">
              <div className="sticky top-24">
                <Card className="p-6 border-border/50">
                  <div className="flex items-center gap-2 mb-4">
                    <List className="w-4 h-4 text-primary" />
                    <h3 className="text-sm font-semibold text-foreground uppercase tracking-wider">
                      Table of Contents
                    </h3>
                  </div>
                  <nav className="space-y-1">
                    {sections.map((section) => (
                      <a
                        key={section.id}
                        href={`#${section.id}`}
                        className="block px-3 py-2 text-sm text-muted-foreground hover:text-primary hover:bg-primary/5 rounded-md transition-colors duration-200"
                      >
                        {section.title}
                      </a>
                    ))}
                  </nav>
                </Card>
              </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 min-w-0">
              <AnimatedSection>
                <div className="prose prose-slate dark:prose-invert max-w-none">
                  {sections.map((section, index) => (
                    <div key={section.id} id={section.id} className="scroll-mt-24">
                      {index > 0 && (
                        <hr className="my-10 border-border/50" />
                      )}
                      <h2 className="text-2xl md:text-3xl font-bold text-foreground mb-6">
                        {`${index + 1}. ${section.title}`}
                      </h2>
                      <div
                        className="text-muted-foreground leading-relaxed space-y-4 text-base"
                        dangerouslySetInnerHTML={{ __html: section.content }}
                      />
                    </div>
                  ))}
                </div>
              </AnimatedSection>
            </main>
          </div>
        </div>
      </section>
    </>
  );
};

export default LegalPageTemplate;
