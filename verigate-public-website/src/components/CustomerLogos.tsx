import { customerLogos } from "@/data/social-proof";

interface CustomerLogosProps {
  title?: string;
  subtitle?: string;
  maxLogos?: number;
  showIndustryFilter?: boolean;
}

export function CustomerLogos({
  title = "Trusted by South Africa's Leading Organisations",
  subtitle = "Join 200+ companies using VeriGate for background screening and verification",
  maxLogos,
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
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-8 items-center">
          {logosToDisplay.map((customer) => (
            <div
              key={customer.name}
              className="flex items-center justify-center p-6 group"
            >
              <img
                src={customer.logo}
                alt={customer.name}
                className="h-10 w-auto max-w-[160px] object-contain opacity-70 group-hover:opacity-100 transition-opacity duration-200"
              />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
