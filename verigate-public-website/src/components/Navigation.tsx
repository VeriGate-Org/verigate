import { Button } from "@/components/ui/button";
import { 
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import { Menu, X, ChevronDown } from "lucide-react";
import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import verigateLogo from "@/assets/verigate-logo.webp";

const Navigation = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();

  const navItems = [
    { 
      label: "Product", 
      type: "dropdown",
      items: [
        { label: "Overview", href: "/product" },
        { label: "Pricing", href: "/pricing" },
        { label: "Integrations", href: "/integrations" },
        { label: "Case Studies", href: "/case-studies" },
      ]
    },
    { 
      label: "Solutions", 
      type: "dropdown",
      items: [
        { label: "KYC Verification", href: "/solutions/kyc" },
        { label: "AML Screening", href: "/solutions/aml" },
        { label: "Document Verification", href: "/solutions/document-verification" },
        { label: "Biometric Authentication", href: "/solutions/biometric" },
      ]
    },
    { 
      label: "Developers", 
      type: "dropdown",
      items: [
        { label: "Developer Hub", href: "/developers" },
        { label: "API Reference", href: "/developers/api-reference" },
        { label: "SDKs", href: "/developers/sdks" },
        { label: "API Playground", href: "/developers/playground" },
        { label: "Webhooks", href: "/developers/webhooks" },
      ]
    },
    { 
      label: "Resources", 
      type: "dropdown",
      items: [
        { label: "Blog", href: "/blog" },
        { label: "Resource Library", href: "/resources" },
        { label: "Help Center", href: "/help" },
        { label: "FAQ", href: "/faq" },
        { label: "Glossary", href: "/glossary" },
        { label: "Changelog", href: "/changelog" },
        { label: "System Status", href: "/status" },
      ]
    },
    { 
      label: "Company", 
      type: "dropdown",
      items: [
        { label: "About Us", href: "/about" },
        { label: "Security", href: "/security" },
        { label: "Trust Center", href: "/trust-center" },
        { label: "Partners", href: "/partners" },
        { label: "Contact", href: "/contact" },
      ]
    },
  ];

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
          <div className="hidden lg:flex items-center gap-6">
            {navItems.map((item) => (
              item.type === "dropdown" ? (
                <DropdownMenu key={item.label}>
                  <DropdownMenuTrigger className="flex items-center gap-1 text-sm font-medium text-foreground hover:text-accent transition-colors outline-none">
                    {item.label}
                    <ChevronDown className="w-3 h-3" />
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="start" className="w-56">
                    {item.items?.map((subItem, index) => (
                      <DropdownMenuItem key={index} asChild>
                        <Link 
                          to={subItem.href}
                          className={`cursor-pointer ${
                            location.pathname === subItem.href ? "bg-accent" : ""
                          }`}
                        >
                          {subItem.label}
                        </Link>
                      </DropdownMenuItem>
                    ))}
                  </DropdownMenuContent>
                </DropdownMenu>
              ) : (
                <Link
                  key={item.label}
                  to={item.href || "#"}
                  className={`text-sm font-medium transition-colors ${
                    location.pathname === item.href
                      ? "text-accent"
                      : "text-foreground hover:text-accent"
                  }`}
                >
                  {item.label}
                </Link>
              )
            ))}
            <Button variant="hero" size="sm" asChild>
              <Link to="/contact">Get Started</Link>
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
            <div className="flex flex-col gap-4">
              {navItems.map((item) => (
                <div key={item.label}>
                  <div className="font-semibold text-sm text-foreground mb-2 px-2">
                    {item.label}
                  </div>
                  {item.items?.map((subItem, index) => (
                    <Link
                      key={index}
                      to={subItem.href}
                      className={`block text-sm text-muted-foreground hover:text-accent transition-colors py-2 px-4 ${
                        location.pathname === subItem.href ? "text-accent bg-accent/10" : ""
                      }`}
                      onClick={() => setIsMenuOpen(false)}
                    >
                      {subItem.label}
                    </Link>
                  ))}
                </div>
              ))}
              <Button variant="hero" size="sm" className="w-full mt-2" asChild onClick={() => setIsMenuOpen(false)}>
                <Link to="/contact">Get Started</Link>
              </Button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navigation;
