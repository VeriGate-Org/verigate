import { Link } from "react-router-dom";
import verigateLogoWhite from "@/assets/verigate-logo-white.svg";
import { NewsletterSignup } from "@/components/forms/NewsletterSignup";
import { Linkedin, Twitter } from "lucide-react";

const Footer = () => {
  return (
    <footer className="bg-primary text-primary-foreground py-12 px-4">
      <div className="container mx-auto max-w-6xl">
        {/* Top section: Brand + Newsletter */}
        <div className="grid md:grid-cols-2 gap-8 mb-12">
          <div className="space-y-4">
            <img
              src={verigateLogoWhite}
              alt="VeriGate Logo"
              className="h-12 w-auto"
            />
            <p className="text-primary-foreground/80 text-sm max-w-md">
              Enterprise-grade background screening and verification platform trusted by South Africa's leading organisations.
            </p>
            <div className="flex gap-4 pt-2">
              <a href="https://linkedin.com/company/verigate" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">
                <Linkedin className="w-5 h-5" />
              </a>
              <a href="https://twitter.com/verigate_za" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">
                <Twitter className="w-5 h-5" />
              </a>
            </div>
          </div>
          <div>
            <h4 className="font-semibold mb-3 text-sm">Stay Updated</h4>
            <NewsletterSignup source="footer" variant="inline" />
          </div>
        </div>

        {/* 5-column link grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-8 mb-12">
          {/* Products */}
          <div>
            <h3 className="font-semibold mb-4">Products</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/verification-types" className="hover:text-accent transition-colors">Verification Types</Link></li>
              <li><Link to="/compliance" className="hover:text-accent transition-colors">Compliance</Link></li>
              <li><Link to="/fraud-prevention" className="hover:text-accent transition-colors">Fraud Prevention</Link></li>
              <li><Link to="/platform" className="hover:text-accent transition-colors">Platform</Link></li>
              <li><Link to="/integrations" className="hover:text-accent transition-colors">Integrations</Link></li>
            </ul>
          </div>

          {/* Solutions */}
          <div>
            <h3 className="font-semibold mb-4">Solutions</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/solutions/banking" className="hover:text-accent transition-colors">Banking</Link></li>
              <li><Link to="/solutions/fintech" className="hover:text-accent transition-colors">Fintech</Link></li>
              <li><Link to="/solutions/cryptocurrency" className="hover:text-accent transition-colors">Cryptocurrency</Link></li>
              <li><Link to="/solutions/gaming" className="hover:text-accent transition-colors">Gaming</Link></li>
              <li><Link to="/solutions/gig-economy" className="hover:text-accent transition-colors">Gig Economy</Link></li>
              <li><Link to="/solutions" className="hover:text-accent transition-colors text-accent">View All →</Link></li>
            </ul>
          </div>

          {/* Company */}
          <div>
            <h3 className="font-semibold mb-4">Company</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/about" className="hover:text-accent transition-colors">About</Link></li>
              <li><Link to="/careers" className="hover:text-accent transition-colors">Careers</Link></li>
              <li><Link to="/partner-program" className="hover:text-accent transition-colors">Partners</Link></li>
              <li><Link to="/events" className="hover:text-accent transition-colors">Events</Link></li>
              <li><Link to="/contact" className="hover:text-accent transition-colors">Contact</Link></li>
            </ul>
          </div>

          {/* Resources */}
          <div>
            <h3 className="font-semibold mb-4">Resources</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/blog" className="hover:text-accent transition-colors">Blog</Link></li>
              <li><Link to="/resources" className="hover:text-accent transition-colors">Library</Link></li>
              <li><Link to="/faqs" className="hover:text-accent transition-colors">FAQs</Link></li>
              <li><Link to="/technical-support" className="hover:text-accent transition-colors">Support</Link></li>
              <li><Link to="/analytics" className="hover:text-accent transition-colors">Analytics</Link></li>
            </ul>
          </div>

          {/* Legal */}
          <div>
            <h3 className="font-semibold mb-4">Legal</h3>
            <ul className="space-y-2 text-sm text-primary-foreground/80">
              <li><Link to="/privacy" className="hover:text-accent transition-colors">Privacy Policy</Link></li>
              <li><Link to="/terms" className="hover:text-accent transition-colors">Terms of Service</Link></li>
              <li><Link to="/cookie-policy" className="hover:text-accent transition-colors">Cookie Policy</Link></li>
              <li><Link to="/south-africa" className="hover:text-accent transition-colors">South Africa</Link></li>
              <li><Link to="/supported-documents" className="hover:text-accent transition-colors">Supported Docs</Link></li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-primary-foreground/20 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-primary-foreground/80">
          <p>&copy; 2026 VeriGate (Pty) Ltd. All rights reserved. Built by <a href="https://arthmatic.co.za" target="_blank" rel="noopener noreferrer" className="hover:text-accent transition-colors">Arthmatic</a></p>
          <div className="flex gap-6">
            <Link to="/privacy" className="hover:text-accent transition-colors">Privacy</Link>
            <Link to="/terms" className="hover:text-accent transition-colors">Terms</Link>
            <Link to="/cookie-policy" className="hover:text-accent transition-colors">Cookies</Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
