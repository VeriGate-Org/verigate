import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "@/components/Layout";

// Pages
import Index from "./pages/Index";
import Pricing from "./pages/Pricing";
import Contact from "./pages/Contact";
import About from "./pages/About";
import Blog from "./pages/Blog";
import BlogPost from "./pages/BlogPost";
import Resources from "./pages/Resources";
import Integrations from "./pages/Integrations";
import FAQ from "./pages/FAQ";
import NotFound from "./pages/NotFound";

// New top-level pages
import Platform from "./pages/Platform";
import Analytics from "./pages/Analytics";
import ComparePlans from "./pages/ComparePlans";
import SouthAfrica from "./pages/SouthAfrica";
import SupportedDocuments from "./pages/SupportedDocuments";
import RequestDemo from "./pages/RequestDemo";
import TechnicalSupport from "./pages/TechnicalSupport";
import Careers from "./pages/Careers";
import Events from "./pages/Events";
import ROICalculator from "./pages/ROICalculator";
import PartnerProgram from "./pages/PartnerProgram";

// Hub pages
import VerificationTypesHub from "./pages/verification-types/VerificationTypesHub";
import VerificationType from "./pages/verification-types/VerificationType";
import ComplianceHub from "./pages/compliance/ComplianceHub";
import Compliance from "./pages/compliance/Compliance";
import FraudPreventionHub from "./pages/fraud-prevention/FraudPreventionHub";
import FraudPrevention from "./pages/fraud-prevention/FraudPrevention";
import SolutionsHub from "./pages/solutions/SolutionsHub";
import Solution from "./pages/solutions/Solution";

// Legal pages
import Privacy from "./pages/legal/Privacy";
import Terms from "./pages/legal/Terms";
import CookiePolicy from "./pages/legal/CookiePolicy";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          {/* Homepage — uses its own layout */}
          <Route path="/" element={<Index />} />

          {/* Core pages */}
          <Route path="/pricing" element={<Layout><Pricing /></Layout>} />
          <Route path="/contact" element={<Layout><Contact /></Layout>} />
          <Route path="/about" element={<Layout><About /></Layout>} />
          <Route path="/blog" element={<Layout><Blog /></Layout>} />
          <Route path="/blog/:slug" element={<Layout><BlogPost /></Layout>} />
          <Route path="/resources" element={<Layout><Resources /></Layout>} />
          <Route path="/integrations" element={<Layout><Integrations /></Layout>} />
          <Route path="/faqs" element={<Layout><FAQ /></Layout>} />

          {/* Platform & Products */}
          <Route path="/platform" element={<Layout><Platform /></Layout>} />
          <Route path="/analytics" element={<Layout><Analytics /></Layout>} />
          <Route path="/compare-plans" element={<Layout><ComparePlans /></Layout>} />

          {/* Verification Types */}
          <Route path="/verification-types" element={<Layout><VerificationTypesHub /></Layout>} />
          <Route path="/verification-types/:slug" element={<Layout><VerificationType /></Layout>} />

          {/* Compliance */}
          <Route path="/compliance" element={<Layout><ComplianceHub /></Layout>} />
          <Route path="/compliance/:slug" element={<Layout><Compliance /></Layout>} />

          {/* Fraud Prevention */}
          <Route path="/fraud-prevention" element={<Layout><FraudPreventionHub /></Layout>} />
          <Route path="/fraud-prevention/:slug" element={<Layout><FraudPrevention /></Layout>} />

          {/* Industry Solutions */}
          <Route path="/solutions" element={<Layout><SolutionsHub /></Layout>} />
          <Route path="/solutions/:slug" element={<Layout><Solution /></Layout>} />

          {/* New top-level pages */}
          <Route path="/careers" element={<Layout><Careers /></Layout>} />
          <Route path="/events" element={<Layout><Events /></Layout>} />
          <Route path="/south-africa" element={<Layout><SouthAfrica /></Layout>} />
          <Route path="/supported-documents" element={<Layout><SupportedDocuments /></Layout>} />
          <Route path="/request-demo" element={<Layout><RequestDemo /></Layout>} />
          <Route path="/technical-support" element={<Layout><TechnicalSupport /></Layout>} />
          <Route path="/roi-calculator" element={<Layout><ROICalculator /></Layout>} />
          <Route path="/partner-program" element={<Layout><PartnerProgram /></Layout>} />

          {/* Legal pages */}
          <Route path="/privacy" element={<Layout><Privacy /></Layout>} />
          <Route path="/terms" element={<Layout><Terms /></Layout>} />
          <Route path="/cookie-policy" element={<Layout><CookiePolicy /></Layout>} />

          {/* === Redirects: old routes → new === */}
          <Route path="/product" element={<Navigate to="/platform" replace />} />
          <Route path="/faq" element={<Navigate to="/faqs" replace />} />
          <Route path="/help" element={<Navigate to="/technical-support" replace />} />
          <Route path="/partners" element={<Navigate to="/partner-program" replace />} />
          <Route path="/tools/roi-calculator" element={<Navigate to="/roi-calculator" replace />} />

          {/* Old industry routes → new solution routes */}
          <Route path="/industries/banking-finance" element={<Navigate to="/solutions/banking" replace />} />
          <Route path="/industries/fintech" element={<Navigate to="/solutions/fintech" replace />} />
          <Route path="/industries/crypto-web3" element={<Navigate to="/solutions/cryptocurrency" replace />} />
          <Route path="/industries/gaming" element={<Navigate to="/solutions/gaming" replace />} />
          <Route path="/industries/healthcare" element={<Navigate to="/solutions/healthcare" replace />} />
          <Route path="/industries/ecommerce" element={<Navigate to="/solutions/ecommerce" replace />} />
          <Route path="/industries/travel-hospitality" element={<Navigate to="/solutions/travel-hospitality" replace />} />
          <Route path="/industries/real-estate" element={<Navigate to="/solutions/real-estate" replace />} />

          {/* Old solution routes → new category routes */}
          <Route path="/solutions/kyc" element={<Navigate to="/compliance/kyc" replace />} />
          <Route path="/solutions/aml" element={<Navigate to="/compliance/aml-screening" replace />} />
          <Route path="/solutions/document-verification" element={<Navigate to="/verification-types/document-verification" replace />} />
          <Route path="/solutions/biometric" element={<Navigate to="/verification-types/face-verification" replace />} />

          {/* Deleted page redirects */}
          <Route path="/security" element={<Navigate to="/compliance" replace />} />
          <Route path="/trust-center" element={<Navigate to="/compliance" replace />} />
          <Route path="/changelog" element={<Navigate to="/blog" replace />} />
          <Route path="/status" element={<Navigate to="/technical-support" replace />} />
          <Route path="/glossary" element={<Navigate to="/faqs" replace />} />
          <Route path="/case-studies" element={<Navigate to="/resources" replace />} />
          <Route path="/developers" element={<Navigate to="/integrations" replace />} />
          <Route path="/developers/overview" element={<Navigate to="/integrations" replace />} />
          <Route path="/developers/api-reference" element={<Navigate to="/integrations" replace />} />
          <Route path="/developers/sdks" element={<Navigate to="/integrations" replace />} />
          <Route path="/developers/webhooks" element={<Navigate to="/integrations" replace />} />
          <Route path="/developers/playground" element={<Navigate to="/integrations" replace />} />

          {/* WP blog root-level slug redirects */}
          <Route path="/state-of-verification-2026" element={<Navigate to="/blog/state-of-verification-2026" replace />} />
          <Route path="/fica-amendments-2026" element={<Navigate to="/blog/fica-amendments-2026" replace />} />
          <Route path="/popia-background-screening" element={<Navigate to="/blog/popia-background-screening" replace />} />
          <Route path="/criminal-checks-hiring" element={<Navigate to="/blog/criminal-checks-hiring" replace />} />
          <Route path="/facial-recognition-onboarding" element={<Navigate to="/blog/facial-recognition-onboarding" replace />} />
          <Route path="/bulk-verification-upload" element={<Navigate to="/blog/bulk-verification-upload" replace />} />

          {/* Catch-all */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
