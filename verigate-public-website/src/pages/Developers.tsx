import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { MultiLanguageCode } from "@/components/MultiLanguageCode";
import { 
  Code,
  Book,
  Zap,
  Terminal,
  Package,
  Webhook,
  Key,
  CheckCircle2,
  ArrowRight,
  Play,
  Download,
  FileCode
} from "lucide-react";
import { Link } from "react-router-dom";

const Developers = () => {
  const quickStartCode = [
    {
      language: "javascript",
      label: "JavaScript",
      code: `// Install the VeriGate SDK
npm install @verigate/sdk

// Initialize the client
import VeriGate from '@verigate/sdk';

const verigate = new VeriGate(process.env.VERIGATE_API_KEY);

// Verify an identity
const result = await verigate.verify({
  documentImage: req.files.id_front,
  selfieImage: req.files.selfie,
  userData: {
    firstName: 'John',
    lastName: 'Doe'
  }
});

console.log(result.status); // 'approved' or 'rejected'`,
      filename: "quickstart.js"
    },
    {
      language: "python",
      label: "Python",
      code: `# Install the VeriGate SDK
pip install verigate

# Initialize the client
from verigate import VeriGate

verigate = VeriGate(api_key=os.environ['VERIGATE_API_KEY'])

# Verify an identity
result = verigate.verify(
    document_image=request.files['id_front'],
    selfie_image=request.files['selfie'],
    user_data={
        'first_name': 'John',
        'last_name': 'Doe'
    }
)

print(result.status)  # 'approved' or 'rejected'`,
      filename: "quickstart.py"
    },
    {
      language: "ruby",
      label: "Ruby",
      code: `# Install the VeriGate gem
gem install verigate

# Initialize the client
require 'verigate'

verigate = VeriGate::Client.new(api_key: ENV['VERIGATE_API_KEY'])

# Verify an identity
result = verigate.verify(
  document_image: params[:id_front],
  selfie_image: params[:selfie],
  user_data: {
    first_name: 'John',
    last_name: 'Doe'
  }
)

puts result.status  # 'approved' or 'rejected'`,
      filename: "quickstart.rb"
    },
    {
      language: "php",
      label: "PHP",
      code: `// Install the VeriGate package
composer require verigate/sdk

// Initialize the client
require 'vendor/autoload.php';
use VeriGate\\Client;

$verigate = new Client($_ENV['VERIGATE_API_KEY']);

// Verify an identity
$result = $verigate->verify([
    'documentImage' => $_FILES['id_front'],
    'selfieImage' => $_FILES['selfie'],
    'userData' => [
        'firstName' => 'John',
        'lastName' => 'Doe'
    ]
]);

echo $result->status;  // 'approved' or 'rejected'`,
      filename: "quickstart.php"
    }
  ];

  const resources = [
    {
      icon: Book,
      title: "API Reference",
      description: "Complete documentation for all API endpoints",
      link: "/developers/api-reference",
      badge: "Essential"
    },
    {
      icon: Code,
      title: "SDKs & Libraries",
      description: "Official SDKs for 9 programming languages",
      link: "/developers/sdks",
      badge: "Popular"
    },
    {
      icon: Webhook,
      title: "Webhooks",
      description: "Real-time notifications for verification events",
      link: "/developers/webhooks",
      badge: "New"
    },
    {
      icon: Terminal,
      title: "API Playground",
      description: "Test API calls interactively in your browser",
      link: "/developers/playground",
      badge: "Interactive"
    },
    {
      icon: FileCode,
      title: "Code Examples",
      description: "Ready-to-use code snippets and templates",
      link: "/developers/examples",
      badge: null
    },
    {
      icon: Key,
      title: "Authentication",
      description: "Learn how to authenticate API requests",
      link: "/developers/api-reference#authentication",
      badge: null
    }
  ];

  const popularEndpoints = [
    {
      method: "POST",
      endpoint: "/v1/verifications",
      description: "Create a new identity verification"
    },
    {
      method: "GET",
      endpoint: "/v1/verifications/{id}",
      description: "Retrieve verification status and results"
    },
    {
      method: "POST",
      endpoint: "/v1/aml/screen",
      description: "Screen a person against AML databases"
    },
    {
      method: "POST",
      endpoint: "/v1/documents/extract",
      description: "Extract data from identity documents"
    }
  ];

  const features = [
    { text: "99.9% uptime SLA", icon: CheckCircle2 },
    { text: "< 500ms average response time", icon: Zap },
    { text: "RESTful API design", icon: Code },
    { text: "Comprehensive error handling", icon: CheckCircle2 },
    { text: "Webhook support", icon: Webhook },
    { text: "Test mode included", icon: Play }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-blue-50 via-background to-blue-100 dark:from-blue-950 dark:to-background">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <div className="inline-flex items-center gap-2 mb-4 py-2 bg-primary/10 rounded-full">
              <Code className="w-4 h-4 text-primary" />
              <span className="text-sm font-medium text-primary">Developer Documentation</span>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Build with VeriGate
              <span className="text-primary block mt-2">Powerful Identity Verification APIs</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-3xl mx-auto mb-8">
              Everything you need to integrate identity verification into your application. 
              Complete API reference, SDKs, and code examples to get started in minutes.
            </p>
            <div className="flex gap-4 flex-wrap">
              <Button size="lg" asChild>
                <Link to="/contact">
                  <Key className="w-4 h-4 mr-2" />
                  Get API Keys
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers/api-reference">
                  View API Docs
                </Link>
              </Button>
            </div>
          </div>

          {/* Features Grid */}
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 max-w-4xl mx-auto">
            {features.map((feature, index) => (
              <div key={index} className="flex items-center gap-2 text-sm">
                <feature.icon className="w-4 h-4 text-primary flex-shrink-0" />
                <span>{feature.text}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Quick Start */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-start">
            <div>
              <Badge className="mb-4">Quick Start</Badge>
              <h2 className="text-3xl font-bold mb-4">Get Started in 5 Minutes</h2>
              <p className="text-lg text-muted-foreground mb-6">
                Install our SDK, initialize with your API key, and make your first verification request.
              </p>
              
              <div className="space-y-4 mb-8">
                <div className="flex gap-3">
                  <div className="flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center font-bold text-primary">
                    1
                  </div>
                  <div>
                    <div className="font-semibold">Install SDK</div>
                    <div className="text-sm text-muted-foreground">Choose from 9 languages</div>
                  </div>
                </div>
                <div className="flex gap-3">
                  <div className="flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center font-bold text-primary">
                    2
                  </div>
                  <div>
                    <div className="font-semibold">Get API Keys</div>
                    <div className="text-sm text-muted-foreground">Sign up for free account</div>
                  </div>
                </div>
                <div className="flex gap-3">
                  <div className="flex-shrink-0 w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center font-bold text-primary">
                    3
                  </div>
                  <div>
                    <div className="font-semibold">Make First Request</div>
                    <div className="text-sm text-muted-foreground">Verify an identity</div>
                  </div>
                </div>
              </div>

              <Button asChild>
                <Link to="/developers/api-reference">
                  View Full Guide <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </div>

            <div>
              <MultiLanguageCode examples={quickStartCode} />
            </div>
          </div>
        </div>
      </section>

      {/* Resources Grid */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Developer Resources</h2>
            <p className="text-lg text-muted-foreground">
              Everything you need to build with VeriGate
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {resources.map((resource, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow" asChild>
                <Link to={resource.link}>
                  <CardHeader>
                    <div className="flex items-start justify-between mb-2">
                      <div className="p-2 bg-primary/10 rounded-lg">
                        <resource.icon className="w-6 h-6 text-primary" />
                      </div>
                      {resource.badge && (
                        <Badge variant="secondary" className="text-xs">{resource.badge}</Badge>
                      )}
                    </div>
                    <CardTitle className="text-lg">{resource.title}</CardTitle>
                    <CardDescription>{resource.description}</CardDescription>
                  </CardHeader>
                </Link>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Popular Endpoints */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Popular API Endpoints</h2>
            <p className="text-lg text-muted-foreground">
              Most commonly used endpoints to get you started
            </p>
          </div>

          <div className="space-y-4">
            {popularEndpoints.map((endpoint, index) => (
              <Card key={index} className="hover:shadow-md transition-shadow">
                <CardContent className="p-4">
                  <div className="flex items-center gap-4">
                    <Badge 
                      variant="outline" 
                      className={`font-mono font-bold ${
                        endpoint.method === 'GET' ? 'border-green-500/20 text-green-500' : 'border-blue-500/20 text-blue-500'
                      }`}
                    >
                      {endpoint.method}
                    </Badge>
                    <code className="flex-1 text-sm font-mono bg-gray-100 dark:bg-gray-800 px-3 py-1.5 rounded">
                      {endpoint.endpoint}
                    </code>
                    <span className="text-sm text-muted-foreground hidden md:block">
                      {endpoint.description}
                    </span>
                    <ArrowRight className="w-4 h-4 text-muted-foreground" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="text-center mt-8">
            <Button variant="outline" asChild>
              <Link to="/developers/api-reference">
                View All Endpoints <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* SDKs */}
      <section className="py-16 bg-gray-50 dark:bg-gray-900">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-12">
            <h2 className="text-3xl font-bold mb-4">Official SDKs & Libraries</h2>
            <p className="text-lg text-muted-foreground">
              Native libraries for your favorite programming language
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
            {[
              { name: "JavaScript", icon: "📦", downloads: "50K+", link: "/developers/sdks#javascript" },
              { name: "Python", icon: "🐍", downloads: "35K+", link: "/developers/sdks#python" },
              { name: "Ruby", icon: "💎", downloads: "12K+", link: "/developers/sdks#ruby" },
              { name: "PHP", icon: "🐘", downloads: "18K+", link: "/developers/sdks#php" },
              { name: "Java", icon: "☕", downloads: "22K+", link: "/developers/sdks#java" },
              { name: "Go", icon: "🔵", downloads: "15K+", link: "/developers/sdks#go" },
              { name: "C# / .NET", icon: "🔷", downloads: "14K+", link: "/developers/sdks#csharp" },
              { name: "Swift", icon: "🍎", downloads: "8K+", link: "/developers/sdks#swift" },
              { name: "Kotlin", icon: "🤖", downloads: "6K+", link: "/developers/sdks#kotlin" },
              { name: "REST API", icon: "🔗", downloads: "All", link: "/developers/api-reference" },
            ].map((sdk, index) => (
              <Card key={index} className="text-center hover:shadow-lg transition-shadow cursor-pointer" asChild>
                <Link to={sdk.link}>
                  <CardContent className="pt-6">
                    <div className="text-4xl mb-2">{sdk.icon}</div>
                    <div className="font-semibold">{sdk.name}</div>
                    <div className="text-xs text-muted-foreground mt-1">
                      <Download className="w-3 h-3 inline mr-1" />
                      {sdk.downloads}
                    </div>
                  </CardContent>
                </Link>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="max-w-4xl">
          <h2 className="text-3xl font-bold mb-4">
            Ready to Start Building?
          </h2>
          <p className="text-xl text-muted-foreground mb-8">
            Get your API keys and start integrating in minutes
          </p>
          <div className="flex gap-4 flex-wrap">
            <Button size="lg" asChild>
              <Link to="/contact">
                <Key className="w-4 h-4 mr-2" />
                Get API Keys
              </Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/developers/playground">
                <Play className="w-4 h-4 mr-2" />
                Try API Playground
              </Link>
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Developers;
