import { customerLogos } from "@/data/social-proof";

interface CustomerLogosProps {
  title?: string;
  subtitle?: string;
  maxLogos?: number;
  showIndustryFilter?: boolean;
}

export function CustomerLogos({
  title = "Trusted by Industry Leaders",
  subtitle = "Join 500+ companies using VeriGate for identity verification",
  maxLogos,
  showIndustryFilter = false,
}: CustomerLogosProps) {
  const logosToDisplay = maxLogos ? customerLogos.slice(0, maxLogos) : customerLogos;

  return (
    <section className="py-16 px-4">
      <div className="container mx-auto max-w-6xl">
        {/* Header */}
        <div className="max-w-2xl mb-12">
          <h2 className="text-2xl md:text-3xl font-bold text-foreground mb-3">
            {title}
          </h2>
          <p className="text-muted-foreground">
            {subtitle}
          </p>
        </div>

        {/* Logos Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-8 items-center">
          {logosToDisplay.map((customer) => (
            <div
              key={customer.name}
              className="flex items-center justify-center p-4 rounded-lg border border-border bg-card hover:shadow-md transition-all duration-300 group"
            >
              <div className="text-center">
                {/* Placeholder for logo - replace with actual logo */}
                <div className="w-24 h-12 mx-auto mb-2 rounded bg-gradient-to-br from-primary/20 to-accent/20 flex items-center justify-center">
                  <span className="text-xs font-bold text-muted-foreground group-hover:text-foreground transition-colors">
                    {customer.name.split(' ').map(word => word[0]).join('')}
                  </span>
                </div>
                <p className="text-xs text-muted-foreground group-hover:text-foreground transition-colors">
                  {customer.name}
                </p>
              </div>
            </div>
          ))}
        </div>

        {/* Note for production */}
        <div className="mt-8 text-center">
          <p className="text-xs text-muted-foreground">
            * Replace placeholder logos with actual customer logos in production
          </p>
        </div>
      </div>
    </section>
  );
}
