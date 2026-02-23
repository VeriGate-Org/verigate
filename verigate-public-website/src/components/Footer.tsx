import { Link } from "react-router-dom";
import verigateLogo from "@/assets/verigate-logo.webp";
import { NewsletterSignup } from "@/components/forms/NewsletterSignup";
import { Linkedin, Twitter, Github } from "lucide-react";

const Footer = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <footer className="bg-primary text-primary-foreground py-12 px-4">
      <div className="container mx-auto max-w-6xl">
        <div className="grid md:grid-cols-5 gap-8 mb-8">
          {/* Brand & Newsletter */}
          <div className="md:col-span-2 space-y-4">
            <img 
              src={verigateLogo} 
              alt="VeriGate Logo" 
              className="h-12 w-auto"
            />
            <p className="text-primary-foreground/80 text-sm">
              Enterprise-grade identity verification and compliance solutions.
            </p>
            
            {/* Newsletter Signup */}
            <div className="pt-4">
              <h4 className="font-semibold mb-3 text-sm">Stay Updated</h4>
              <NewsletterSignup source="footer" variant="inline" />
            </div>

            {/* Social Media */}
            <div className="flex gap-4 pt-2">
              <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">
                <Linkedin className="w-5 h-5" />
              </a>
              <a href="https://twitter.com" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">
                <Twitter className="w-5 h-5" />
              </a>
              <a href="https://github.com" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">
                <Github className="w-5 h-5" />
              </a>
            </div>
          </div>
          
          {/* Product */}
          <div>
            <h3 className="font-semibold mb-4">Product</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/pricing" className="hover:text-accent transition-colors">Pricing</Link></li>
              <li><Link to="/product" className="hover:text-accent transition-colors">Features</Link></li>
              <li><Link to="/developers/api-reference" className="hover:text-accent transition-colors">API Docs</Link></li>
              <li><Link to="/integrations" className="hover:text-accent transition-colors">Integrations</Link></li>
            </ul>
          </div>
          
          {/* Company */}
          <div>
            <h3 className="font-semibold mb-4">Company</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/about" className="hover:text-accent transition-colors">About Us</Link></li>
              <li><Link to="/contact" className="hover:text-accent transition-colors">Contact</Link></li>
              <li><Link to="/partners" className="hover:text-accent transition-colors">Partners</Link></li>
              <li><Link to="/blog" className="hover:text-accent transition-colors">Blog</Link></li>
            </ul>
          </div>
          
          {/* Legal */}
          <div>
            <h3 className="font-semibold mb-4">Legal</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/security" className="hover:text-accent transition-colors">Privacy & Security</Link></li>
              <li><Link to="/trust-center" className="hover:text-accent transition-colors">Trust Center</Link></li>
              <li><Link to="/security" className="hover:text-accent transition-colors">Compliance</Link></li>
              <li><Link to="/faq" className="hover:text-accent transition-colors">FAQ</Link></li>
            </ul>
          </div>
        </div>
        
        {/* Bottom Bar */}
        <div className="pt-8 border-t border-primary-foreground/20 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-primary-foreground/80">
          <p>© {currentYear} VeriGate. All rights reserved.</p>
          <div className="flex gap-6">
            <Link to="/security" className="hover:text-accent transition-colors">Privacy</Link>
            <Link to="/trust-center" className="hover:text-accent transition-colors">Terms</Link>
            <Link to="/security" className="hover:text-accent transition-colors">Security</Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
