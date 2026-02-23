import { Button } from "@/components/ui/button";
import { Menu, X, ChevronDown } from "lucide-react";
import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import verigateLogo from "@/assets/verigate-logo.webp";

const EnhancedNavigation = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null);
  const location = useLocation();

  const solutionsMenu = {
    mainItems: [
      { label: "KYC Verification", href: "/solutions/kyc", description: "Identity verification for compliance" },
      { label: "AML Screening", href: "/solutions/aml", description: "Anti-money laundering checks" },
      { label: "Document Verification", href: "/solutions/documents", description: "Verify IDs and documents" },
      { label: "Biometric Authentication", href: "/solutions/biometric", description: "Facial recognition & liveness" },
    ],
    industries: [
      { label: "Banking & Finance", href: "/industries/banking" },
      { label: "Fintech", href: "/industries/fintech" },
      { label: "Crypto & Web3", href: "/industries/crypto" },
      { label: "Gaming", href: "/industries/gaming" },
      { label: "Healthcare", href: "/industries/healthcare" },
    ],
  };

  const resourcesMenu = [
    { label: "Documentation", href: "/developers", description: "Technical guides and API docs" },
    { label: "Case Studies", href: "/case-studies", description: "Customer success stories" },
    { label: "Blog", href: "/blog", description: "Industry insights and updates" },
    { label: "Help Center", href: "/help-center", description: "FAQs and support articles" },
    { label: "API Reference", href: "/developers/api", description: "Complete API documentation" },
  ];

  const companyMenu = [
    { label: "About Us", href: "/about", description: "Our mission and team" },
    { label: "Security", href: "/security", description: "Security & compliance" },
    { label: "Trust Center", href: "/trust-center", description: "System status and reports" },
    { label: "Careers", href: "/careers", description: "Join our team" },
    { label: "Contact", href: "/contact", description: "Get in touch" },
  ];

  const handleMouseEnter = (menu: string) => {
    setActiveDropdown(menu);
  };

  const handleMouseLeave = () => {
    setActiveDropdown(null);
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-background/95 backdrop-blur-sm border-b border-border shadow-sm">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center">
            <img 
              src={verigateLogo} 
              alt="VeriGate Logo" 
              className="h-8 md:h-10 w-auto cursor-pointer"
            />
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden lg:flex items-center gap-1">
            {/* Solutions Dropdown */}
            <div
              className="relative"
              onMouseEnter={() => handleMouseEnter("solutions")}
              onMouseLeave={handleMouseLeave}
            >
              <button className="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5">
                Solutions
                <ChevronDown className="w-4 h-4" />
              </button>

              {activeDropdown === "solutions" && (
                <div className="absolute top-full left-0 mt-1 w-[600px] bg-background border border-border rounded-lg shadow-xl p-6 animate-in fade-in-0 slide-in-from-top-2 duration-200">
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3">Products</h3>
                      <div className="space-y-1">
                        {solutionsMenu.mainItems.map((item) => (
                          <Link
                            key={item.label}
                            to={item.href}
                            className="block p-3 rounded-md hover:bg-accent/5 transition-colors"
                          >
                            <div className="font-medium text-sm mb-1">{item.label}</div>
                            <div className="text-xs text-muted-foreground">{item.description}</div>
                          </Link>
                        ))}
                      </div>
                    </div>
                    <div>
                      <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3">Industries</h3>
                      <div className="space-y-1">
                        {solutionsMenu.industries.map((item) => (
                          <Link
                            key={item.label}
                            to={item.href}
                            className="block p-3 rounded-md hover:bg-accent/5 transition-colors text-sm"
                          >
                            {item.label}
                          </Link>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Pricing */}
            <Link
              to="/pricing"
              className={`px-4 py-2 text-sm font-medium transition-colors rounded-md hover:bg-accent/5 ${
                location.pathname === "/pricing" ? "text-accent" : "text-foreground hover:text-accent"
              }`}
            >
              Pricing
            </Link>

            {/* Resources Dropdown */}
            <div
              className="relative"
              onMouseEnter={() => handleMouseEnter("resources")}
              onMouseLeave={handleMouseLeave}
            >
              <button className="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5">
                Resources
                <ChevronDown className="w-4 h-4" />
              </button>

              {activeDropdown === "resources" && (
                <div className="absolute top-full left-0 mt-1 w-[380px] bg-background border border-border rounded-lg shadow-xl p-4 animate-in fade-in-0 slide-in-from-top-2 duration-200">
                  <div className="space-y-1">
                    {resourcesMenu.map((item) => (
                      <Link
                        key={item.label}
                        to={item.href}
                        className="block p-3 rounded-md hover:bg-accent/5 transition-colors"
                      >
                        <div className="font-medium text-sm mb-1">{item.label}</div>
                        <div className="text-xs text-muted-foreground">{item.description}</div>
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Company Dropdown */}
            <div
              className="relative"
              onMouseEnter={() => handleMouseEnter("company")}
              onMouseLeave={handleMouseLeave}
            >
              <button className="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5">
                Company
                <ChevronDown className="w-4 h-4" />
              </button>

              {activeDropdown === "company" && (
                <div className="absolute top-full right-0 mt-1 w-[300px] bg-background border border-border rounded-lg shadow-xl p-4 animate-in fade-in-0 slide-in-from-top-2 duration-200">
                  <div className="space-y-1">
                    {companyMenu.map((item) => (
                      <Link
                        key={item.label}
                        to={item.href}
                        className="block p-3 rounded-md hover:bg-accent/5 transition-colors"
                      >
                        <div className="font-medium text-sm mb-1">{item.label}</div>
                        <div className="text-xs text-muted-foreground">{item.description}</div>
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <Button variant="hero" size="sm" className="ml-4">
              Get Started
            </Button>
          </div>

          {/* Mobile Menu Button */}
          <button
            className="lg:hidden p-2 rounded-md hover:bg-accent/10 transition-colors"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            aria-label="Toggle menu"
          >
            {isMenuOpen ? (
              <X className="w-6 h-6 text-foreground" />
            ) : (
              <Menu className="w-6 h-6 text-foreground" />
            )}
          </button>
        </div>

        {/* Mobile Menu */}
        {isMenuOpen && (
          <div className="lg:hidden py-4 border-t border-border animate-fade-in max-h-[80vh] overflow-y-auto">
            <div className="flex flex-col gap-2">
              {/* Solutions */}
              <div>
                <div className="text-xs font-semibold text-muted-foreground uppercase mb-2 px-2">Solutions</div>
                {solutionsMenu.mainItems.map((item) => (
                  <Link
                    key={item.label}
                    to={item.href}
                    className="block px-2 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    {item.label}
                  </Link>
                ))}
              </div>

              {/* Pricing */}
              <Link
                to="/pricing"
                className={`px-2 py-2 text-sm font-medium transition-colors ${
                  location.pathname === "/pricing" ? "text-accent" : "text-foreground hover:text-accent"
                }`}
                onClick={() => setIsMenuOpen(false)}
              >
                Pricing
              </Link>

              {/* Resources */}
              <div className="mt-2">
                <div className="text-xs font-semibold text-muted-foreground uppercase mb-2 px-2">Resources</div>
                {resourcesMenu.map((item) => (
                  <Link
                    key={item.label}
                    to={item.href}
                    className="block px-2 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    {item.label}
                  </Link>
                ))}
              </div>

              {/* Company */}
              <div className="mt-2">
                <div className="text-xs font-semibold text-muted-foreground uppercase mb-2 px-2">Company</div>
                {companyMenu.map((item) => (
                  <Link
                    key={item.label}
                    to={item.href}
                    className="block px-2 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    {item.label}
                  </Link>
                ))}
              </div>

              <Button variant="hero" size="sm" className="w-full mt-4">
                Get Started
              </Button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default EnhancedNavigation;
