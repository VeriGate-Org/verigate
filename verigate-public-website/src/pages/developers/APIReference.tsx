import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { MultiLanguageCode } from "@/components/MultiLanguageCode";
import { 
  Code,
  Lock,
  Zap,
  CheckCircle2,
  XCircle,
  AlertCircle,
  Copy,
  ExternalLink,
  ArrowRight,
  Key,
  ShieldCheck,
  Clock
} from "lucide-react";
import { Link } from "react-router-dom";

const APIReference = () => {
  // Authentication Examples
  const authExamples = [
    {
      language: "javascript",
      label: "JavaScript",
      code: `// Using API Key (Recommended)
const axios = require('axios');

const headers = {
  'Authorization': 'Bearer YOUR_API_KEY',
  'Content-Type': 'application/json'
};

const response = await axios.get(
  'https://api.verigate.com/v1/verifications',
  { headers }
);`,
      filename: "auth.js"
    },
    {
      language: "python",
      label: "Python",
      code: `# Using API Key
import requests

headers = {
    'Authorization': 'Bearer YOUR_API_KEY',
    'Content-Type': 'application/json'
}

response = requests.get(
    'https://api.verigate.com/v1/verifications',
    headers=headers
)`,
      filename: "auth.py"
    },
    {
      language: "bash",
      label: "cURL",
      code: `# Using API Key in header
curl -X GET "https://api.verigate.com/v1/verifications" \\
  -H "Authorization: Bearer YOUR_API_KEY" \\
  -H "Content-Type: application/json"`,
      filename: "auth.sh"
    }
  ];

  // Verification Request Examples
  const verificationExamples = [
    {
      language: "javascript",
      label: "JavaScript",
      code: `// Create a verification
const createVerification = async () => {
  const formData = new FormData();
  formData.append('document_front', documentFront);
  formData.append('document_back', documentBack);
  formData.append('selfie', selfieImage);
  formData.append('first_name', 'John');
  formData.append('last_name', 'Doe');
  formData.append('date_of_birth', '1990-01-15');
  
  const response = await fetch(
    'https://api.verigate.com/v1/verifications',
    {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer YOUR_API_KEY'
      },
      body: formData
    }
  );
  
  return await response.json();
};`,
      filename: "create-verification.js"
    },
    {
      language: "python",
      label: "Python",
      code: `# Create a verification
import requests

files = {
    'document_front': open('id_front.jpg', 'rb'),
    'document_back': open('id_back.jpg', 'rb'),
    'selfie': open('selfie.jpg', 'rb')
}

data = {
    'first_name': 'John',
    'last_name': 'Doe',
    'date_of_birth': '1990-01-15'
}

response = requests.post(
    'https://api.verigate.com/v1/verifications',
    headers={'Authorization': 'Bearer YOUR_API_KEY'},
    files=files,
    data=data
)

result = response.json()`,
      filename: "create_verification.py"
    }
  ];

  // Webhook Examples
  const webhookExamples = [
    {
      language: "javascript",
      label: "Node.js",
      code: `// Webhook endpoint handler
const crypto = require('crypto');

app.post('/webhooks/verigate', (req, res) => {
  // Verify webhook signature
  const signature = req.headers['x-verigate-signature'];
  const payload = JSON.stringify(req.body);
  
  const expectedSignature = crypto
    .createHmac('sha256', WEBHOOK_SECRET)
    .update(payload)
    .digest('hex');
  
  if (signature !== expectedSignature) {
    return res.status(401).send('Invalid signature');
  }
  
  // Process the event
  const event = req.body;
  
  switch(event.type) {
    case 'verification.completed':
      handleVerificationCompleted(event.data);
      break;
    case 'verification.failed':
      handleVerificationFailed(event.data);
      break;
    case 'verification.review_needed':
      handleReviewNeeded(event.data);
      break;
  }
  
  res.status(200).send('Received');
});`,
      filename: "webhooks.js"
    },
    {
      language: "python",
      label: "Python",
      code: `# Webhook handler with Flask
from flask import Flask, request
import hmac
import hashlib
import json

app = Flask(__name__)

@app.route('/webhooks/verigate', methods=['POST'])
def handle_webhook():
    # Verify signature
    signature = request.headers.get('X-Verigate-Signature')
    payload = request.get_data()
    
    expected_signature = hmac.new(
        WEBHOOK_SECRET.encode(),
        payload,
        hashlib.sha256
    ).hexdigest()
    
    if signature != expected_signature:
        return 'Invalid signature', 401
    
    # Process event
    event = request.json
    
    if event['type'] == 'verification.completed':
        handle_verification_completed(event['data'])
    elif event['type'] == 'verification.failed':
        handle_verification_failed(event['data'])
    
    return 'Received', 200`,
      filename: "webhooks.py"
    }
  ];

  const endpoints = [
    {
      category: "Verifications",
      items: [
        {
          method: "POST",
          path: "/v1/verifications",
          description: "Create a new identity verification",
          auth: "API Key",
          rateLimit: "100/min"
        },
        {
          method: "GET",
          path: "/v1/verifications/:id",
          description: "Retrieve a verification by ID",
          auth: "API Key",
          rateLimit: "1000/min"
        },
        {
          method: "GET",
          path: "/v1/verifications",
          description: "List all verifications with pagination",
          auth: "API Key",
          rateLimit: "1000/min"
        },
        {
          method: "DELETE",
          path: "/v1/verifications/:id",
          description: "Delete a verification (GDPR compliance)",
          auth: "API Key",
          rateLimit: "100/min"
        }
      ]
    },
    {
      category: "AML Screening",
      items: [
        {
          method: "POST",
          path: "/v1/aml/screen",
          description: "Screen an individual against sanctions lists",
          auth: "API Key",
          rateLimit: "200/min"
        },
        {
          method: "GET",
          path: "/v1/aml/screen/:id",
          description: "Get screening results",
          auth: "API Key",
          rateLimit: "1000/min"
        },
        {
          method: "POST",
          path: "/v1/aml/monitor",
          description: "Setup ongoing monitoring",
          auth: "API Key",
          rateLimit: "100/min"
        }
      ]
    },
    {
      category: "Documents",
      items: [
        {
          method: "POST",
          path: "/v1/documents/verify",
          description: "Verify document authenticity",
          auth: "API Key",
          rateLimit: "100/min"
        },
        {
          method: "POST",
          path: "/v1/documents/extract",
          description: "Extract data from document (OCR)",
          auth: "API Key",
          rateLimit: "100/min"
        },
        {
          method: "GET",
          path: "/v1/documents/supported",
          description: "List supported document types",
          auth: "None",
          rateLimit: "10000/min"
        }
      ]
    },
    {
      category: "Webhooks",
      items: [
        {
          method: "POST",
          path: "/v1/webhooks",
          description: "Create a webhook endpoint",
          auth: "API Key",
          rateLimit: "10/min"
        },
        {
          method: "GET",
          path: "/v1/webhooks",
          description: "List all webhook endpoints",
          auth: "API Key",
          rateLimit: "100/min"
        },
        {
          method: "DELETE",
          path: "/v1/webhooks/:id",
          description: "Delete a webhook endpoint",
          auth: "API Key",
          rateLimit: "10/min"
        }
      ]
    }
  ];

  const errorCodes = [
    {
      code: 200,
      type: "Success",
      description: "Request completed successfully"
    },
    {
      code: 201,
      type: "Created",
      description: "Resource created successfully"
    },
    {
      code: 400,
      type: "Bad Request",
      description: "Invalid request parameters"
    },
    {
      code: 401,
      type: "Unauthorized",
      description: "Invalid or missing API key"
    },
    {
      code: 403,
      type: "Forbidden",
      description: "API key doesn't have required permissions"
    },
    {
      code: 404,
      type: "Not Found",
      description: "Resource not found"
    },
    {
      code: 429,
      type: "Too Many Requests",
      description: "Rate limit exceeded"
    },
    {
      code: 500,
      type: "Internal Server Error",
      description: "Server error occurred"
    },
    {
      code: 503,
      type: "Service Unavailable",
      description: "Service temporarily unavailable"
    }
  ];

  const responseExample = {
    language: "json",
    label: "Response",
    code: `{
  "id": "ver_1a2b3c4d5e6f",
  "object": "verification",
  "status": "approved",
  "created_at": "2024-01-15T10:30:00Z",
  "completed_at": "2024-01-15T10:30:45Z",
  "person": {
    "first_name": "John",
    "last_name": "Doe",
    "date_of_birth": "1990-01-15",
    "nationality": "US"
  },
  "document": {
    "type": "drivers_license",
    "country": "US",
    "state": "CA",
    "number": "D1234567",
    "expiry_date": "2028-01-15",
    "authenticity_score": 0.98
  },
  "biometric": {
    "liveness_score": 0.95,
    "face_match_score": 0.96
  },
  "risk_assessment": {
    "risk_level": "low",
    "risk_score": 12,
    "flags": []
  },
  "metadata": {
    "ip_address": "192.168.1.1",
    "user_agent": "Mozilla/5.0...",
    "reference_id": "user_12345"
  }
}`,
    filename: "response.json"
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <Badge className="mb-4 bg-blue-100 text-blue-700 hover:bg-blue-100">
              <Code className="w-3 h-3 mr-1" />
              API Documentation v1.0
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold text-slate-900 mb-6">
              API Reference
            </h1>
            <p className="text-xl text-slate-600 max-w-3xl mx-auto mb-8">
              Complete reference documentation for the VeriGate API. Build powerful identity verification into your applications.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Link to="/developers/playground">
                <Button size="lg" className="gap-2">
                  <Zap className="w-4 h-4" />
                  Try API Playground
                </Button>
              </Link>
              <Link to="/developers">
                <Button size="lg" variant="outline" className="gap-2">
                  <ArrowRight className="w-4 h-4" />
                  Back to Developer Hub
                </Button>
              </Link>
            </div>
          </div>

          {/* Quick Stats */}
          <div className="grid md:grid-cols-4 gap-4 mb-16">
            <Card>
              <CardContent className="pt-6 text-center">
                <CheckCircle2 className="w-8 h-8 text-green-600 mx-auto mb-2" />
                <div className="text-2xl font-bold text-slate-900">99.9%</div>
                <div className="text-sm text-slate-600">API Uptime</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <Clock className="w-8 h-8 text-blue-600 mx-auto mb-2" />
                <div className="text-2xl font-bold text-slate-900">&lt;150ms</div>
                <div className="text-sm text-slate-600">Avg Response</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <ShieldCheck className="w-8 h-8 text-purple-600 mx-auto mb-2" />
                <div className="text-2xl font-bold text-slate-900">TLS 1.3</div>
                <div className="text-sm text-slate-600">Encryption</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 text-center">
                <Key className="w-8 h-8 text-orange-600 mx-auto mb-2" />
                <div className="text-2xl font-bold text-slate-900">OAuth 2.0</div>
                <div className="text-sm text-slate-600">Auth Support</div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Authentication Section */}
      <section className="py-12 bg-slate-50">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-slate-900 mb-4">Authentication</h2>
            <p className="text-slate-600 max-w-3xl">
              The VeriGate API uses API keys for authentication. Include your API key in the Authorization header of every request.
            </p>
          </div>

          <div className="grid lg:grid-cols-2 gap-8 mb-8">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Lock className="w-5 h-5" />
                  API Key Authentication
                </CardTitle>
                <CardDescription>
                  Recommended method for server-to-server communication
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="font-semibold mb-2">Header Format:</h4>
                    <code className="block bg-slate-900 text-green-400 p-3 rounded text-sm">
                      Authorization: Bearer YOUR_API_KEY
                    </code>
                  </div>
                  <div>
                    <h4 className="font-semibold mb-2">Security Best Practices:</h4>
                    <ul className="space-y-2 text-sm text-slate-600">
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Store API keys securely in environment variables
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Never commit API keys to version control
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Rotate keys periodically
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Use different keys for different environments
                      </li>
                    </ul>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Rate Limiting</CardTitle>
                <CardDescription>
                  API rate limits by plan tier
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <div className="flex justify-between items-center p-3 bg-slate-50 rounded">
                    <span className="font-medium">Free Tier</span>
                    <Badge variant="outline">100 req/min</Badge>
                  </div>
                  <div className="flex justify-between items-center p-3 bg-slate-50 rounded">
                    <span className="font-medium">Starter</span>
                    <Badge variant="outline">1,000 req/min</Badge>
                  </div>
                  <div className="flex justify-between items-center p-3 bg-slate-50 rounded">
                    <span className="font-medium">Professional</span>
                    <Badge variant="outline">5,000 req/min</Badge>
                  </div>
                  <div className="flex justify-between items-center p-3 bg-slate-50 rounded">
                    <span className="font-medium">Enterprise</span>
                    <Badge variant="outline">Custom</Badge>
                  </div>
                </div>
                <p className="text-sm text-slate-600 mt-4">
                  Rate limit information is included in response headers:
                </p>
                <code className="block bg-slate-900 text-green-400 p-3 rounded text-xs mt-2">
                  X-RateLimit-Limit: 1000<br/>
                  X-RateLimit-Remaining: 999<br/>
                  X-RateLimit-Reset: 1609459200
                </code>
              </CardContent>
            </Card>
          </div>

          <MultiLanguageCode examples={authExamples} />
        </div>
      </section>

      {/* API Endpoints Section */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-slate-900 mb-4">API Endpoints</h2>
            <p className="text-slate-600 max-w-3xl">
              Base URL: <code className="bg-slate-100 px-2 py-1 rounded">https://api.verigate.com</code>
            </p>
          </div>

          <div className="space-y-6">
            {endpoints.map((category, idx) => (
              <Card key={idx}>
                <CardHeader>
                  <CardTitle>{category.category}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {category.items.map((endpoint, endIdx) => (
                      <div key={endIdx} className="border-l-4 border-blue-500 pl-4 py-3 hover:bg-slate-50 transition-colors">
                        <div className="flex flex-wrap items-center gap-3 mb-2">
                          <Badge className={
                            endpoint.method === "GET" ? "bg-green-600" :
                            endpoint.method === "POST" ? "bg-blue-600" :
                            endpoint.method === "DELETE" ? "bg-red-600" :
                            "bg-yellow-600"
                          }>
                            {endpoint.method}
                          </Badge>
                          <code className="text-sm font-mono">{endpoint.path}</code>
                          <Badge variant="outline" className="text-xs">
                            {endpoint.rateLimit}
                          </Badge>
                        </div>
                        <p className="text-sm text-slate-600">{endpoint.description}</p>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Create Verification Example */}
      <section className="py-12 bg-slate-50">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-slate-900 mb-4">Create Verification</h2>
            <p className="text-slate-600 max-w-3xl">
              The primary endpoint for creating identity verifications. Submit documents, biometric data, and personal information.
            </p>
          </div>

          <MultiLanguageCode examples={verificationExamples} />

          <div className="mt-8">
            <h3 className="text-xl font-bold text-slate-900 mb-4">Response</h3>
            <MultiLanguageCode examples={[responseExample]} />
          </div>
        </div>
      </section>

      {/* Webhooks Section */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-slate-900 mb-4">Webhooks</h2>
            <p className="text-slate-600 max-w-3xl mb-6">
              Receive real-time notifications when verification events occur. Webhooks are signed with HMAC-SHA256 for security.
            </p>
          </div>

          <div className="grid lg:grid-cols-2 gap-8 mb-8">
            <Card>
              <CardHeader>
                <CardTitle>Webhook Events</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <div className="p-3 bg-green-50 border-l-4 border-green-500">
                    <code className="text-sm font-mono">verification.completed</code>
                    <p className="text-sm text-slate-600 mt-1">Verification completed successfully</p>
                  </div>
                  <div className="p-3 bg-red-50 border-l-4 border-red-500">
                    <code className="text-sm font-mono">verification.failed</code>
                    <p className="text-sm text-slate-600 mt-1">Verification failed</p>
                  </div>
                  <div className="p-3 bg-yellow-50 border-l-4 border-yellow-500">
                    <code className="text-sm font-mono">verification.review_needed</code>
                    <p className="text-sm text-slate-600 mt-1">Manual review required</p>
                  </div>
                  <div className="p-3 bg-blue-50 border-l-4 border-blue-500">
                    <code className="text-sm font-mono">aml.match_found</code>
                    <p className="text-sm text-slate-600 mt-1">AML screening found a match</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Webhook Security</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="font-semibold mb-2">Signature Verification</h4>
                    <p className="text-sm text-slate-600 mb-2">
                      Every webhook request includes a signature in the <code className="bg-slate-100 px-1 rounded">X-Verigate-Signature</code> header.
                    </p>
                    <code className="block bg-slate-900 text-green-400 p-2 rounded text-xs">
                      HMAC-SHA256(payload, webhook_secret)
                    </code>
                  </div>
                  <div>
                    <h4 className="font-semibold mb-2">Best Practices</h4>
                    <ul className="space-y-2 text-sm text-slate-600">
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Always verify webhook signatures
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Use HTTPS for webhook endpoints
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Implement retry logic
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-600 mt-0.5" />
                        Return 200 OK within 5 seconds
                      </li>
                    </ul>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <MultiLanguageCode examples={webhookExamples} />
        </div>
      </section>

      {/* Error Codes Section */}
      <section className="py-12 bg-slate-50">
        <div className="container mx-auto max-w-6xl">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-slate-900 mb-4">Error Codes</h2>
            <p className="text-slate-600 max-w-3xl">
              The VeriGate API uses standard HTTP status codes to indicate success or failure of requests.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {errorCodes.map((error, idx) => (
              <Card key={idx} className={
                error.code < 300 ? "border-green-200 bg-green-50" :
                error.code < 400 ? "border-blue-200 bg-blue-50" :
                error.code < 500 ? "border-yellow-200 bg-yellow-50" :
                "border-red-200 bg-red-50"
              }>
                <CardContent className="pt-6">
                  <div className="flex items-center gap-3 mb-2">
                    {error.code < 300 ? <CheckCircle2 className="w-5 h-5 text-green-600" /> :
                     error.code < 400 ? <AlertCircle className="w-5 h-5 text-yellow-600" /> :
                     <XCircle className="w-5 h-5 text-red-600" />}
                    <code className="text-lg font-bold">{error.code}</code>
                  </div>
                  <h4 className="font-semibold text-slate-900 mb-1">{error.type}</h4>
                  <p className="text-sm text-slate-600">{error.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          <Card className="mt-8">
            <CardHeader>
              <CardTitle>Error Response Format</CardTitle>
            </CardHeader>
            <CardContent>
              <MultiLanguageCode examples={[{
                language: "json",
                label: "Error Response",
                code: `{
  "error": {
    "code": "invalid_request",
    "message": "Missing required parameter: document_front",
    "param": "document_front",
    "type": "invalid_request_error"
  },
  "request_id": "req_1a2b3c4d5e6f"
}`,
                filename: "error.json"
              }]} />
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Next Steps */}
      <section className="py-12">
        <div className="container mx-auto max-w-6xl">
          <Card className="bg-gradient-to-r from-blue-600 to-purple-600 text-white">
            <CardContent className="pt-6">
              <div className="text-center">
                <h2 className="text-3xl font-bold mb-4">Ready to Start Building?</h2>
                <p className="text-lg mb-8 opacity-90">
                  Explore our interactive playground, download SDKs, or view more code examples.
                </p>
                <div className="flex flex-wrap gap-4 justify-center">
                  <Link to="/developers/playground">
                    <Button size="lg" variant="secondary" className="gap-2">
                      <Zap className="w-4 h-4" />
                      Try API Playground
                    </Button>
                  </Link>
                  <Link to="/developers/sdks">
                    <Button size="lg" variant="outline" className="gap-2 bg-white/10 hover:bg-white/20 text-white border-white/30">
                      <ExternalLink className="w-4 h-4" />
                      View SDKs
                    </Button>
                  </Link>
                  <Link to="/developers/webhooks">
                    <Button size="lg" variant="outline" className="gap-2 bg-white/10 hover:bg-white/20 text-white border-white/30">
                      <ExternalLink className="w-4 h-4" />
                      Webhook Guide
                    </Button>
                  </Link>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default APIReference;
