import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { MultiLanguageCode } from "@/components/MultiLanguageCode";
import { 
  Webhook,
  Shield,
  Key,
  CheckCircle2,
  AlertTriangle,
  Code,
  Lock,
  Zap,
  Clock,
  Server,
  ExternalLink
} from "lucide-react";
import { Link } from "react-router-dom";

const Webhooks = () => {
  const webhookEvents = [
    {
      event: "verification.created",
      description: "A new verification has been initiated",
      payload: {
        id: "ver_1234567890",
        type: "identity",
        status: "pending",
        created_at: "2025-01-15T10:30:00Z"
      }
    },
    {
      event: "verification.completed",
      description: "Verification processing has completed",
      payload: {
        id: "ver_1234567890",
        type: "identity",
        status: "approved",
        result: {
          decision: "approved",
          confidence: 0.98,
          reasons: []
        },
        completed_at: "2025-01-15T10:32:15Z"
      }
    },
    {
      event: "verification.failed",
      description: "Verification processing failed",
      payload: {
        id: "ver_1234567890",
        type: "identity",
        status: "rejected",
        result: {
          decision: "rejected",
          confidence: 0.45,
          reasons: ["document_quality_low", "face_mismatch"]
        },
        completed_at: "2025-01-15T10:32:15Z"
      }
    },
    {
      event: "aml.screening_completed",
      description: "AML screening has completed",
      payload: {
        id: "aml_1234567890",
        status: "completed",
        matches: [],
        risk_score: 0.12,
        completed_at: "2025-01-15T10:32:15Z"
      }
    }
  ];

  const setupExamples = [
    {
      language: "javascript",
      label: "JavaScript",
      code: `// Express.js webhook handler
const express = require('express');
const crypto = require('crypto');

app.post('/webhooks/verigate', express.raw({type: 'application/json'}), (req, res) => {
  const signature = req.headers['x-verigate-signature'];
  const payload = req.body;
  
  // Verify the webhook signature
  const hmac = crypto.createHmac('sha256', process.env.WEBHOOK_SECRET);
  const digest = hmac.update(payload).digest('hex');
  
  if (digest !== signature) {
    return res.status(401).send('Invalid signature');
  }
  
  // Parse the event
  const event = JSON.parse(payload);
  
  // Handle different event types
  switch (event.type) {
    case 'verification.completed':
      handleVerificationCompleted(event.data);
      break;
    case 'verification.failed':
      handleVerificationFailed(event.data);
      break;
    case 'aml.screening_completed':
      handleAMLCompleted(event.data);
      break;
  }
  
  res.status(200).send('OK');
});`,
      filename: "webhook-handler.js"
    },
    {
      language: "python",
      label: "Python",
      code: `# Flask webhook handler
from flask import Flask, request, jsonify
import hmac
import hashlib

app = Flask(__name__)

@app.route('/webhooks/verigate', methods=['POST'])
def handle_webhook():
    signature = request.headers.get('X-VeriGate-Signature')
    payload = request.data
    
    # Verify the webhook signature
    secret = os.environ['WEBHOOK_SECRET'].encode()
    digest = hmac.new(secret, payload, hashlib.sha256).hexdigest()
    
    if digest != signature:
        return jsonify({'error': 'Invalid signature'}), 401
    
    # Parse the event
    event = request.json
    
    # Handle different event types
    if event['type'] == 'verification.completed':
        handle_verification_completed(event['data'])
    elif event['type'] == 'verification.failed':
        handle_verification_failed(event['data'])
    elif event['type'] == 'aml.screening_completed':
        handle_aml_completed(event['data'])
    
    return jsonify({'status': 'received'}), 200`,
      filename: "webhook_handler.py"
    },
    {
      language: "ruby",
      label: "Ruby",
      code: `# Rails webhook handler
class WebhooksController < ApplicationController
  skip_before_action :verify_authenticity_token
  
  def verigate
    signature = request.headers['X-VeriGate-Signature']
    payload = request.raw_post
    
    # Verify the webhook signature
    secret = ENV['WEBHOOK_SECRET']
    digest = OpenSSL::HMAC.hexdigest('SHA256', secret, payload)
    
    unless Rack::Utils.secure_compare(digest, signature)
      render json: { error: 'Invalid signature' }, status: :unauthorized
      return
    end
    
    # Parse the event
    event = JSON.parse(payload)
    
    # Handle different event types
    case event['type']
    when 'verification.completed'
      handle_verification_completed(event['data'])
    when 'verification.failed'
      handle_verification_failed(event['data'])
    when 'aml.screening_completed'
      handle_aml_completed(event['data'])
    end
    
    render json: { status: 'received' }, status: :ok
  end
end`,
      filename: "webhooks_controller.rb"
    },
    {
      language: "php",
      label: "PHP",
      code: `<?php
// Laravel webhook handler
namespace App\\Http\\Controllers;

use Illuminate\\Http\\Request;

class WebhookController extends Controller
{
    public function handleVeriGate(Request $request)
    {
        $signature = $request->header('X-VeriGate-Signature');
        $payload = $request->getContent();
        
        // Verify the webhook signature
        $secret = env('WEBHOOK_SECRET');
        $digest = hash_hmac('sha256', $payload, $secret);
        
        if (!hash_equals($digest, $signature)) {
            return response()->json(['error' => 'Invalid signature'], 401);
        }
        
        // Parse the event
        $event = json_decode($payload, true);
        
        // Handle different event types
        switch ($event['type']) {
            case 'verification.completed':
                $this->handleVerificationCompleted($event['data']);
                break;
            case 'verification.failed':
                $this->handleVerificationFailed($event['data']);
                break;
            case 'aml.screening_completed':
                $this->handleAMLCompleted($event['data']);
                break;
        }
        
        return response()->json(['status' => 'received'], 200);
    }
}`,
      filename: "WebhookController.php"
    }
  ];

  const testingExamples = [
    {
      language: "bash",
      label: "cURL",
      code: `# Test webhook endpoint locally
curl -X POST http://localhost:3000/webhooks/verigate \\
  -H "Content-Type: application/json" \\
  -H "X-VeriGate-Signature: test_signature" \\
  -d '{
    "id": "evt_test123",
    "type": "verification.completed",
    "data": {
      "id": "ver_1234567890",
      "status": "approved",
      "result": {
        "decision": "approved",
        "confidence": 0.98
      }
    },
    "created_at": "2025-01-15T10:30:00Z"
  }'`,
      filename: "test-webhook.sh"
    }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <Webhook className="w-3 h-3 mr-1" />
              Real-time Events
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              Webhooks
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Receive real-time notifications when events happen in your VeriGate account. 
              Secure, reliable, and easy to implement.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/contact">
                  <Key className="w-4 h-4 mr-2" />
                  Get Webhook Secret
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers/api-reference">
                  <Code className="w-4 h-4 mr-2" />
                  API Reference
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Quick Overview */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">How Webhooks Work</h2>
            
            <div className="grid md:grid-cols-3 gap-6 mb-12">
              <Card>
                <CardHeader>
                  <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-3">
                    <Zap className="w-6 h-6 text-primary" />
                  </div>
                  <CardTitle>Event Occurs</CardTitle>
                  <CardDescription>
                    A verification completes, AML screening finishes, or other event happens
                  </CardDescription>
                </CardHeader>
              </Card>

              <Card>
                <CardHeader>
                  <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-3">
                    <Server className="w-6 h-6 text-primary" />
                  </div>
                  <CardTitle>VeriGate Sends</CardTitle>
                  <CardDescription>
                    We send a POST request to your configured webhook URL with event data
                  </CardDescription>
                </CardHeader>
              </Card>

              <Card>
                <CardHeader>
                  <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-3">
                    <CheckCircle2 className="w-6 h-6 text-primary" />
                  </div>
                  <CardTitle>You Respond</CardTitle>
                  <CardDescription>
                    Your server processes the event and returns a 200 OK response
                  </CardDescription>
                </CardHeader>
              </Card>
            </div>

            <Alert>
              <Shield className="w-4 h-4" />
              <AlertDescription>
                All webhook payloads are signed with HMAC SHA-256. Always verify the signature before processing events.
              </AlertDescription>
            </Alert>
          </div>
        </div>
      </section>

      {/* Available Events */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Available Events</h2>
            
            <div className="space-y-4">
              {webhookEvents.map((event, index) => (
                <Card key={index}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div>
                        <CardTitle className="text-lg font-mono mb-1">
                          {event.event}
                        </CardTitle>
                        <CardDescription>{event.description}</CardDescription>
                      </div>
                      <Badge variant="outline">POST</Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="bg-slate-950 text-slate-50 p-4 rounded-lg">
                      <pre className="text-sm font-mono overflow-x-auto">
                        {JSON.stringify(event.payload, null, 2)}
                      </pre>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Setup Guide */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Webhook Setup</h2>
            
            <div className="space-y-8">
              {/* Step 1 */}
              <div>
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-8 h-8 bg-primary text-primary-foreground rounded-full flex items-center justify-center font-bold">
                    1
                  </div>
                  <h3 className="text-xl font-semibold">Configure Webhook URL</h3>
                </div>
                <Card>
                  <CardContent className="p-6">
                    <p className="text-muted-foreground mb-4">
                      Set up your webhook endpoint URL in the VeriGate dashboard under Settings → Webhooks. 
                      Your endpoint should accept POST requests and return a 200 status code.
                    </p>
                    <div className="bg-muted p-4 rounded-lg font-mono text-sm">
                      https://yourdomain.com/webhooks/verigate
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Step 2 */}
              <div>
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-8 h-8 bg-primary text-primary-foreground rounded-full flex items-center justify-center font-bold">
                    2
                  </div>
                  <h3 className="text-xl font-semibold">Implement Webhook Handler</h3>
                </div>
                <MultiLanguageCode examples={setupExamples} />
              </div>

              {/* Step 3 */}
              <div>
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-8 h-8 bg-primary text-primary-foreground rounded-full flex items-center justify-center font-bold">
                    3
                  </div>
                  <h3 className="text-xl font-semibold">Verify Signature</h3>
                </div>
                <Card>
                  <CardContent className="p-6">
                    <Alert className="mb-4">
                      <Lock className="w-4 h-4" />
                      <AlertDescription>
                        Always verify the webhook signature to ensure the request is from VeriGate
                      </AlertDescription>
                    </Alert>
                    <div className="space-y-3 text-sm">
                      <div>
                        <strong>Header:</strong> <code className="bg-muted px-2 py-1 rounded">X-VeriGate-Signature</code>
                      </div>
                      <div>
                        <strong>Algorithm:</strong> HMAC SHA-256
                      </div>
                      <div>
                        <strong>Secret:</strong> Your webhook secret from the dashboard
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Step 4 */}
              <div>
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-8 h-8 bg-primary text-primary-foreground rounded-full flex items-center justify-center font-bold">
                    4
                  </div>
                  <h3 className="text-xl font-semibold">Test Your Endpoint</h3>
                </div>
                <MultiLanguageCode examples={testingExamples} />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Best Practices */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Best Practices</h2>
            
            <div className="grid md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <CheckCircle2 className="w-5 h-5 text-green-500" />
                    Do
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 text-sm">
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Always verify the webhook signature</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Respond quickly with 200 OK</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Process events asynchronously</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Implement idempotency checks</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Log all received webhooks</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-green-500 rounded-full mt-2" />
                      <span>Use HTTPS for your endpoint</span>
                    </li>
                  </ul>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <AlertTriangle className="w-5 h-5 text-amber-500" />
                    Don't
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2 text-sm">
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Process heavy operations synchronously</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Skip signature verification</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Return errors for duplicate events</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Expose webhook secrets in logs</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Rely solely on webhooks for state</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <div className="w-1 h-1 bg-amber-500 rounded-full mt-2" />
                      <span>Use unencrypted HTTP endpoints</span>
                    </li>
                  </ul>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* Retry Policy */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Retry Policy</h2>
            
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Clock className="w-5 h-5" />
                  Automatic Retries
                </CardTitle>
                <CardDescription>
                  If your endpoint doesn't respond with a 200 status code, we'll retry delivery
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid md:grid-cols-4 gap-4 text-center">
                  <div>
                    <div className="text-2xl font-bold text-primary mb-1">1st</div>
                    <div className="text-sm text-muted-foreground">Immediate</div>
                  </div>
                  <div>
                    <div className="text-2xl font-bold text-primary mb-1">2nd</div>
                    <div className="text-sm text-muted-foreground">5 minutes</div>
                  </div>
                  <div>
                    <div className="text-2xl font-bold text-primary mb-1">3rd</div>
                    <div className="text-sm text-muted-foreground">30 minutes</div>
                  </div>
                  <div>
                    <div className="text-2xl font-bold text-primary mb-1">4th</div>
                    <div className="text-sm text-muted-foreground">2 hours</div>
                  </div>
                </div>
                <Alert>
                  <AlertDescription>
                    After 4 failed attempts, we'll mark the webhook as failed and send an alert to your account email.
                  </AlertDescription>
                </Alert>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Ready to Set Up Webhooks?</h2>
            <p className="text-muted-foreground mb-8">
              Get your webhook secret and start receiving real-time notifications
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/contact">
                  <Key className="w-4 h-4 mr-2" />
                  Get Webhook Secret
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/help">
                  Need Help?
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

export default Webhooks;
