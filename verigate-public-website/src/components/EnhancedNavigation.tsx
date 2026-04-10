import { Button } from "@/components/ui/button";
import { Menu, X, ChevronDown, ChevronRight } from "lucide-react";
import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import verigateLogo from "@/assets/verigate-logo.svg";

const EnhancedNavigation = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null);
  const [activeMobileSection, setActiveMobileSection] = useState<string | null>(null);
  const location = useLocation();

  const productsMenu = {
    verificationTypes: [
      { label: "Criminal Record Checks", href: "/verification-types/criminal-record-checks" },
      { label: "Identity Verification", href: "/verification-types/identity-verification" },
      { label: "Qualification Checks", href: "/verification-types/qualification-checks" },
      { label: "Employment History", href: "/verification-types/employment-history" },
      { label: "Credit Screening", href: "/verification-types/credit-screening" },
      { label: "Face Verification", href: "/verification-types/face-verification" },
    ],
    compliance: [
      { label: "KYC", href: "/compliance/kyc" },
      { label: "KYB", href: "/compliance/kyb" },
      { label: "AML Screening", href: "/compliance/aml-screening" },
      { label: "POPIA Compliance", href: "/compliance/popia-compliance" },
      { label: "ISO 27001", href: "/compliance/iso-27001" },
    ],
    fraudPrevention: [
      { label: "Identity Fraud", href: "/fraud-prevention/identity-fraud" },
      { label: "Document Fraud", href: "/fraud-prevention/document-fraud" },
      { label: "Deepfake Detection", href: "/fraud-prevention/deepfake-detection" },
    ],
  };

  const solutionsMenu = [
    { label: "Banking", href: "/solutions/banking" },
    { label: "Fintech", href: "/solutions/fintech" },
    { label: "Cryptocurrency", href: "/solutions/cryptocurrency" },
    { label: "Gaming", href: "/solutions/gaming" },
    { label: "Healthcare", href: "/solutions/healthcare" },
    { label: "E-commerce", href: "/solutions/ecommerce" },
    { label: "Travel & Hospitality", href: "/solutions/travel-hospitality" },
    { label: "Real Estate", href: "/solutions/real-estate" },
    { label: "Forex Trading", href: "/solutions/forex-trading" },
    { label: "Gig Economy", href: "/solutions/gig-economy" },
    { label: "Marketplaces", href: "/solutions/marketplaces" },
    { label: "Social Networks", href: "/solutions/social-networks" },
  ];

  const resourcesMenu = [
    { label: "Blog", href: "/blog", description: "Industry insights and updates" },
    { label: "Resource Library", href: "/resources", description: "Guides, whitepapers, and e-books" },
    { label: "FAQs", href: "/faqs", description: "Frequently asked questions" },
    { label: "Technical Support", href: "/technical-support", description: "Help and support resources" },
    { label: "Supported Documents", href: "/supported-documents", description: "Document types we verify" },
    { label: "ROI Calculator", href: "/roi-calculator", description: "Calculate your savings" },
  ];

  const companyMenu = [
    { label: "About", href: "/about", description: "Our mission and team" },
    { label: "Careers", href: "/careers", description: "Join our team" },
    { label: "Partner Program", href: "/partner-program", description: "Become a partner" },
    { label: "Events", href: "/events", description: "Upcoming events and webinars" },
    { label: "Contact", href: "/contact", description: "Get in touch" },
  ];

  const handleMouseEnter = (menu: string) => {
    setActiveDropdown(menu);
  };

  const handleMouseLeave = () => {
    setActiveDropdown(null);
  };

  const toggleMobileSection = (section: string) => {
    setActiveMobileSection(activeMobileSection === section ? null : section);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
    setActiveMobileSection(null);
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
            {/* Products Mega Menu */}
            <div
              className="relative"
              onMouseEnter={() => handleMouseEnter("products")}
              onMouseLeave={handleMouseLeave}
            >
              <button className="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5">
                Products
                <ChevronDown className="w-4 h-4" />
              </button>

              {activeDropdown === "products" && (
                <div className="absolute top-full left-0 mt-1 w-[720px] bg-background border border-border rounded-lg shadow-xl p-6 animate-in fade-in-0 slide-in-from-top-2 duration-200">
                  <div className="grid grid-cols-3 gap-6">
                    {/* Verification Types */}
                    <div>
                      <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3">Verification Types</h3>
                      <div className="space-y-1">
                        {productsMenu.verificationTypes.map((item) => (
                          <Link
                            key={item.label}
                            to={item.href}
                            className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"
                          >
                            {item.label}
                          </Link>
                        ))}
                        <Link
                          to="/verification-types"
                          className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm text-accent font-medium"
                        >
                          View All →
                        </Link>
                      </div>
                    </div>

                    {/* Compliance */}
                    <div>
                      <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3">Compliance</h3>
                      <div className="space-y-1">
                        {productsMenu.compliance.map((item) => (
                          <Link
                            key={item.label}
                            to={item.href}
                            className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"
                          >
                            {item.label}
                          </Link>
                        ))}
                        <Link
                          to="/compliance"
                          className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm text-accent font-medium"
                        >
                          View All →
                        </Link>
                      </div>
                    </div>

                    {/* Fraud Prevention */}
                    <div>
                      <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3">Fraud Prevention</h3>
                      <div className="space-y-1">
                        {productsMenu.fraudPrevention.map((item) => (
                          <Link
                            key={item.label}
                            to={item.href}
                            className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"
                          >
                            {item.label}
                          </Link>
                        ))}
                        <Link
                          to="/fraud-prevention"
                          className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm text-accent font-medium"
                        >
                          View All →
                        </Link>
                      </div>
                    </div>
                  </div>

                  {/* Bottom links */}
                  <div className="mt-4 pt-4 border-t border-border flex gap-6">
                    <Link to="/platform" className="text-sm font-medium text-accent hover:underline">
                      Platform Overview →
                    </Link>
                    <Link to="/integrations" className="text-sm font-medium text-accent hover:underline">
                      Integrations →
                    </Link>
                  </div>
                </div>
              )}
            </div>

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
                <div className="absolute top-full left-0 mt-1 w-[420px] bg-background border border-border rounded-lg shadow-xl p-4 animate-in fade-in-0 slide-in-from-top-2 duration-200">
                  <h3 className="text-xs font-semibold text-muted-foreground uppercase mb-3 px-3">Industries</h3>
                  <div className="grid grid-cols-2 gap-1">
                    {solutionsMenu.map((item) => (
                      <Link
                        key={item.label}
                        to={item.href}
                        className="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"
                      >
                        {item.label}
                      </Link>
                    ))}
                  </div>
                  <div className="mt-3 pt-3 border-t border-border">
                    <Link to="/solutions" className="block px-3 py-2 text-sm text-accent font-medium hover:underline">
                      View All Solutions →
                    </Link>
                  </div>
                </div>
              )}
            </div>

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

            {/* Pricing */}
            <Link
              to="/pricing"
              className={`px-4 py-2 text-sm font-medium transition-colors rounded-md hover:bg-accent/5 ${
                location.pathname === "/pricing" ? "text-accent" : "text-foreground hover:text-accent"
              }`}
            >
              Pricing
            </Link>

            <Button variant="hero" size="sm" className="ml-4" asChild>
              <Link to="/request-demo">Get Started</Link>
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
            <div className="flex flex-col gap-1">
              {/* Products */}
              <div>
                <button
                  onClick={() => toggleMobileSection("products")}
                  className="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground"
                >
                  Products
                  <ChevronDown className={`w-4 h-4 transition-transform ${activeMobileSection === "products" ? "rotate-180" : ""}`} />
                </button>
                {activeMobileSection === "products" && (
                  <div className="pl-4 pb-2 space-y-1">
                    <p className="text-xs font-semibold text-muted-foreground uppercase px-2 pt-1">Verification Types</p>
                    {productsMenu.verificationTypes.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                    <Link to="/verification-types" className="block px-2 py-2 text-sm text-accent font-medium" onClick={closeMenu}>View All →</Link>

                    <p className="text-xs font-semibold text-muted-foreground uppercase px-2 pt-3">Compliance</p>
                    {productsMenu.compliance.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                    <Link to="/compliance" className="block px-2 py-2 text-sm text-accent font-medium" onClick={closeMenu}>View All →</Link>

                    <p className="text-xs font-semibold text-muted-foreground uppercase px-2 pt-3">Fraud Prevention</p>
                    {productsMenu.fraudPrevention.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                    <Link to="/fraud-prevention" className="block px-2 py-2 text-sm text-accent font-medium" onClick={closeMenu}>View All →</Link>
                  </div>
                )}
              </div>

              {/* Solutions */}
              <div>
                <button
                  onClick={() => toggleMobileSection("solutions")}
                  className="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground"
                >
                  Solutions
                  <ChevronDown className={`w-4 h-4 transition-transform ${activeMobileSection === "solutions" ? "rotate-180" : ""}`} />
                </button>
                {activeMobileSection === "solutions" && (
                  <div className="pl-4 pb-2 space-y-1">
                    {solutionsMenu.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                    <Link to="/solutions" className="block px-2 py-2 text-sm text-accent font-medium" onClick={closeMenu}>View All Solutions →</Link>
                  </div>
                )}
              </div>

              {/* Resources */}
              <div>
                <button
                  onClick={() => toggleMobileSection("resources")}
                  className="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground"
                >
                  Resources
                  <ChevronDown className={`w-4 h-4 transition-transform ${activeMobileSection === "resources" ? "rotate-180" : ""}`} />
                </button>
                {activeMobileSection === "resources" && (
                  <div className="pl-4 pb-2 space-y-1">
                    {resourcesMenu.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                  </div>
                )}
              </div>

              {/* Company */}
              <div>
                <button
                  onClick={() => toggleMobileSection("company")}
                  className="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground"
                >
                  Company
                  <ChevronDown className={`w-4 h-4 transition-transform ${activeMobileSection === "company" ? "rotate-180" : ""}`} />
                </button>
                {activeMobileSection === "company" && (
                  <div className="pl-4 pb-2 space-y-1">
                    {companyMenu.map((item) => (
                      <Link key={item.label} to={item.href} className="block px-2 py-2 text-sm text-foreground hover:text-accent" onClick={closeMenu}>
                        {item.label}
                      </Link>
                    ))}
                  </div>
                )}
              </div>

              {/* Pricing */}
              <Link
                to="/pricing"
                className={`px-2 py-3 text-sm font-semibold transition-colors ${
                  location.pathname === "/pricing" ? "text-accent" : "text-foreground hover:text-accent"
                }`}
                onClick={closeMenu}
              >
                Pricing
              </Link>

              <Button variant="hero" size="sm" className="w-full mt-4" asChild>
                <Link to="/request-demo" onClick={closeMenu}>Get Started</Link>
              </Button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default EnhancedNavigation;
