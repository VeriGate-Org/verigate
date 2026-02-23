import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { 
  Play,
  Code,
  Key,
  Zap,
  AlertCircle,
  CheckCircle2,
  Copy,
  ExternalLink
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Playground = () => {
  const [selectedEndpoint, setSelectedEndpoint] = useState("verifications");
  const [method, setMethod] = useState("POST");
  const [apiKey, setApiKey] = useState("");
  const [requestBody, setRequestBody] = useState(`{
  "type": "identity",
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "1990-01-15"
}`);
  const [response, setResponse] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const endpoints = [
    {
      id: "verifications",
      name: "Create Verification",
      method: "POST",
      path: "/v1/verifications",
      description: "Create a new identity verification"
    },
    {
      id: "get-verification",
      name: "Get Verification",
      method: "GET",
      path: "/v1/verifications/:id",
      description: "Retrieve verification status"
    },
    {
      id: "aml-screening",
      name: "AML Screening",
      method: "POST",
      path: "/v1/aml/screenings",
      description: "Run AML screening"
    },
    {
      id: "documents",
      name: "Supported Documents",
      method: "GET",
      path: "/v1/documents/supported",
      description: "Get list of supported documents"
    }
  ];

  const handleSubmit = async () => {
    setIsLoading(true);
    setResponse("");
    
    // Simulate API call
    setTimeout(() => {
      const mockResponse = {
        id: "ver_" + Math.random().toString(36).substr(2, 9),
        status: "pending",
        type: "identity",
        created_at: new Date().toISOString(),
        message: "Verification created successfully"
      };
      
      setResponse(JSON.stringify(mockResponse, null, 2));
      setIsLoading(false);
    }, 1500);
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <Play className="w-3 h-3 mr-1" />
              Interactive Testing
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              API Playground
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Test VeriGate API endpoints in real-time. Experiment with different parameters 
              and see live responses without writing code.
            </p>
            <Alert className="max-w-2xl mx-auto">
              <AlertCircle className="w-4 h-4" />
              <AlertDescription>
                <strong>Note:</strong> This playground uses test mode. No real verifications will be performed.
              </AlertDescription>
            </Alert>
          </div>
        </div>
      </section>

      {/* Playground */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="grid lg:grid-cols-3 gap-6">
              {/* Sidebar - Endpoint Selection */}
              <div className="lg:col-span-1">
                <Card>
                  <CardHeader>
                    <CardTitle>Select Endpoint</CardTitle>
                    <CardDescription>
                      Choose an API endpoint to test
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-2">
                    {endpoints.map((endpoint) => (
                      <button
                        key={endpoint.id}
                        onClick={() => {
                          setSelectedEndpoint(endpoint.id);
                          setMethod(endpoint.method);
                        }}
                        className={`w-full p-3 rounded-lg text-left transition-colors ${
                          selectedEndpoint === endpoint.id
                            ? 'bg-primary text-primary-foreground'
                            : 'bg-muted hover:bg-muted/80'
                        }`}
                      >
                        <div className="flex items-center justify-between mb-1">
                          <span className="font-medium text-sm">{endpoint.name}</span>
                          <Badge variant={endpoint.method === "GET" ? "secondary" : "default"} className="text-xs">
                            {endpoint.method}
                          </Badge>
                        </div>
                        <code className="text-xs opacity-80">{endpoint.path}</code>
                      </button>
                    ))}
                  </CardContent>
                </Card>

                <Card className="mt-6">
                  <CardHeader>
                    <CardTitle className="text-sm">Quick Links</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-2">
                    <Button variant="ghost" className="w-full justify-start" size="sm" asChild>
                      <Link to="/developers/api-reference">
                        <Code className="w-4 h-4 mr-2" />
                        API Reference
                      </Link>
                    </Button>
                    <Button variant="ghost" className="w-full justify-start" size="sm" asChild>
                      <Link to="/developers/sdks">
                        <ExternalLink className="w-4 h-4 mr-2" />
                        View SDKs
                      </Link>
                    </Button>
                  </CardContent>
                </Card>
              </div>

              {/* Main Content - Request/Response */}
              <div className="lg:col-span-2 space-y-6">
                {/* API Key */}
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Key className="w-5 h-5" />
                      Authentication
                    </CardTitle>
                    <CardDescription>
                      Enter your test API key or use the example below
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      <Label htmlFor="api-key">API Key</Label>
                      <Input
                        id="api-key"
                        type="password"
                        placeholder="sk_test_..."
                        value={apiKey}
                        onChange={(e) => setApiKey(e.target.value)}
                      />
                      <p className="text-xs text-muted-foreground">
                        Don't have an API key? <Link to="/contact" className="text-primary hover:underline">Get one here</Link>
                      </p>
                    </div>
                  </CardContent>
                </Card>

                {/* Request */}
                <Card>
                  <CardHeader>
                    <CardTitle>Request</CardTitle>
                    <CardDescription>
                      Configure your API request
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center gap-4">
                      <div className="flex-shrink-0">
                        <Badge variant={method === "GET" ? "secondary" : "default"}>
                          {method}
                        </Badge>
                      </div>
                      <code className="text-sm bg-muted px-3 py-2 rounded flex-1">
                        {endpoints.find(e => e.id === selectedEndpoint)?.path}
                      </code>
                    </div>

                    {method === "POST" && (
                      <div className="space-y-2">
                        <Label htmlFor="request-body">Request Body (JSON)</Label>
                        <Textarea
                          id="request-body"
                          value={requestBody}
                          onChange={(e) => setRequestBody(e.target.value)}
                          rows={10}
                          className="font-mono text-sm"
                        />
                      </div>
                    )}

                    <Button 
                      onClick={handleSubmit}
                      disabled={isLoading || !apiKey}
                      className="w-full"
                      size="lg"
                    >
                      {isLoading ? (
                        <>
                          <span className="animate-spin mr-2">⏳</span>
                          Sending Request...
                        </>
                      ) : (
                        <>
                          <Play className="w-4 h-4 mr-2" />
                          Send Request
                        </>
                      )}
                    </Button>
                  </CardContent>
                </Card>

                {/* Response */}
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                      Response
                      {response && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => navigator.clipboard.writeText(response)}
                        >
                          <Copy className="w-3 h-3 mr-1" />
                          Copy
                        </Button>
                      )}
                    </CardTitle>
                    <CardDescription>
                      {response ? "API response" : "Send a request to see the response"}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    {response ? (
                      <div className="bg-slate-950 text-slate-50 p-4 rounded-lg overflow-x-auto">
                        <pre className="text-sm font-mono">
                          {response}
                        </pre>
                      </div>
                    ) : (
                      <div className="text-center py-12 text-muted-foreground">
                        <Zap className="w-12 h-12 mx-auto mb-3 opacity-20" />
                        <p>No response yet. Send a request to get started.</p>
                      </div>
                    )}
                  </CardContent>
                </Card>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Code Generation */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Generate Code</h2>
            <p className="text-center text-muted-foreground mb-8">
              Get ready-to-use code snippets in your preferred language
            </p>

            <Card>
              <CardContent className="p-6">
                <Tabs defaultValue="javascript" className="w-full">
                  <TabsList className="grid w-full grid-cols-4">
                    <TabsTrigger value="javascript">JavaScript</TabsTrigger>
                    <TabsTrigger value="python">Python</TabsTrigger>
                    <TabsTrigger value="ruby">Ruby</TabsTrigger>
                    <TabsTrigger value="php">PHP</TabsTrigger>
                  </TabsList>
                  
                  <TabsContent value="javascript" className="mt-4">
                    <div className="bg-slate-950 text-slate-50 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm font-mono">
{`const response = await fetch('https://api.verigate.com${endpoints.find(e => e.id === selectedEndpoint)?.path}', {
  method: '${method}',
  headers: {
    'Authorization': 'Bearer YOUR_API_KEY',
    'Content-Type': 'application/json'
  },
  ${method === "POST" ? `body: JSON.stringify(${requestBody})` : ''}
});

const data = await response.json();
console.log(data);`}
                      </pre>
                    </div>
                  </TabsContent>

                  <TabsContent value="python" className="mt-4">
                    <div className="bg-slate-950 text-slate-50 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm font-mono">
{`import requests

headers = {
    'Authorization': 'Bearer YOUR_API_KEY',
    'Content-Type': 'application/json'
}

response = requests.${method.toLowerCase()}(
    'https://api.verigate.com${endpoints.find(e => e.id === selectedEndpoint)?.path}',
    headers=headers${method === "POST" ? `,\n    json=${requestBody}` : ''}
)

data = response.json()
print(data)`}
                      </pre>
                    </div>
                  </TabsContent>

                  <TabsContent value="ruby" className="mt-4">
                    <div className="bg-slate-950 text-slate-50 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm font-mono">
{`require 'net/http'
require 'json'

uri = URI('https://api.verigate.com${endpoints.find(e => e.id === selectedEndpoint)?.path}')
request = Net::HTTP::${method.charAt(0) + method.slice(1).toLowerCase()}.new(uri)
request['Authorization'] = 'Bearer YOUR_API_KEY'
request['Content-Type'] = 'application/json'
${method === "POST" ? `request.body = ${requestBody}.to_json` : ''}

response = Net::HTTP.start(uri.hostname, uri.port, use_ssl: true) do |http|
  http.request(request)
end

puts JSON.parse(response.body)`}
                      </pre>
                    </div>
                  </TabsContent>

                  <TabsContent value="php" className="mt-4">
                    <div className="bg-slate-950 text-slate-50 p-4 rounded-lg overflow-x-auto">
                      <pre className="text-sm font-mono">
{`<?php
$curl = curl_init();

curl_setopt_array($curl, array(
    CURLOPT_URL => 'https://api.verigate.com${endpoints.find(e => e.id === selectedEndpoint)?.path}',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_CUSTOMREQUEST => '${method}',
    CURLOPT_HTTPHEADER => array(
        'Authorization: Bearer YOUR_API_KEY',
        'Content-Type: application/json'
    ),
    ${method === "POST" ? `CURLOPT_POSTFIELDS => json_encode(${requestBody})` : ''}
));

$response = curl_exec($curl);
curl_close($curl);

$data = json_decode($response);
print_r($data);`}
                      </pre>
                    </div>
                  </TabsContent>
                </Tabs>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Ready to Integrate?</h2>
            <p className="text-muted-foreground mb-8">
              Get your production API keys and start verifying identities
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/contact">
                  <Key className="w-4 h-4 mr-2" />
                  Get API Keys
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers/api-reference">
                  <Code className="w-4 h-4 mr-2" />
                  View Documentation
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Playground;
