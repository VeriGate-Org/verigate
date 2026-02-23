import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Index from "./pages/Index";
import Pricing from "./pages/Pricing";
import Contact from "./pages/Contact";
import About from "./pages/About";
import Security from "./pages/Security";
import TrustCenter from "./pages/TrustCenter";
import Product from "./pages/Product";
import KYC from "./pages/solutions/KYC";
import AML from "./pages/solutions/AML";
import DocumentVerification from "./pages/solutions/DocumentVerification";
import Biometric from "./pages/solutions/Biometric";
import BankingFinance from "./pages/industries/BankingFinance";
import Fintech from "./pages/industries/Fintech";
import CryptoWeb3 from "./pages/industries/CryptoWeb3";
import Gaming from "./pages/industries/Gaming";
import Healthcare from "./pages/industries/Healthcare";
import Ecommerce from "./pages/industries/Ecommerce";
import TravelHospitality from "./pages/industries/TravelHospitality";
import RealEstate from "./pages/industries/RealEstate";
import CaseStudies from "./pages/CaseStudies";
import HelpCenter from "./pages/HelpCenter";
import FAQ from "./pages/FAQ";
import Integrations from "./pages/Integrations";
import Partners from "./pages/Partners";
import DeveloperOverview from "./pages/developers/Overview";
import APIReference from "./pages/developers/APIReference";
import SDKs from "./pages/developers/SDKs";
import Webhooks from "./pages/developers/Webhooks";
import Playground from "./pages/developers/Playground";
import Blog from "./pages/Blog";
import Resources from "./pages/Resources";
import Changelog from "./pages/Changelog";
import BlogPost from "./pages/BlogPost";
import Status from "./pages/Status";
import ROICalculator from "./pages/tools/ROICalculator";
import Glossary from "./pages/Glossary";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Index />} />
          <Route path="/pricing" element={<Pricing />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/about" element={<About />} />
          <Route path="/security" element={<Security />} />
          <Route path="/trust-center" element={<TrustCenter />} />
          
          {/* Product & Solutions */}
          <Route path="/product" element={<Product />} />
          <Route path="/solutions/kyc" element={<KYC />} />
          <Route path="/solutions/aml" element={<AML />} />
          <Route path="/solutions/document-verification" element={<DocumentVerification />} />
          <Route path="/solutions/biometric" element={<Biometric />} />
          
          {/* Industries */}
          <Route path="/industries/banking-finance" element={<BankingFinance />} />
          <Route path="/industries/fintech" element={<Fintech />} />
          <Route path="/industries/crypto-web3" element={<CryptoWeb3 />} />
          <Route path="/industries/gaming" element={<Gaming />} />
          <Route path="/industries/healthcare" element={<Healthcare />} />
          <Route path="/industries/ecommerce" element={<Ecommerce />} />
          <Route path="/industries/travel-hospitality" element={<TravelHospitality />} />
          <Route path="/industries/real-estate" element={<RealEstate />} />
          
          {/* Resources */}
          <Route path="/case-studies" element={<CaseStudies />} />
          <Route path="/help" element={<HelpCenter />} />
          <Route path="/faq" element={<FAQ />} />
          <Route path="/integrations" element={<Integrations />} />
          <Route path="/partners" element={<Partners />} />
          
          {/* Developer Portal */}
          <Route path="/developers" element={<DeveloperOverview />} />
          <Route path="/developers/overview" element={<DeveloperOverview />} />
          <Route path="/developers/api-reference" element={<APIReference />} />
          <Route path="/developers/sdks" element={<SDKs />} />
          <Route path="/developers/webhooks" element={<Webhooks />} />
          <Route path="/developers/playground" element={<Playground />} />
          
          {/* Content & Resources */}
          <Route path="/blog" element={<Blog />} />
          <Route path="/blog/:id" element={<BlogPost />} />
          <Route path="/resources" element={<Resources />} />
          <Route path="/changelog" element={<Changelog />} />
          
          {/* Tools & Interactive */}
          <Route path="/status" element={<Status />} />
          <Route path="/tools/roi-calculator" element={<ROICalculator />} />
          <Route path="/glossary" element={<Glossary />} />
          
          {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
