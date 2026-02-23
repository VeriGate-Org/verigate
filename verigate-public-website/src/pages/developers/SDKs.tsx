import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { MultiLanguageCode } from "@/components/MultiLanguageCode";
import { 
  Download,
  Github,
  ExternalLink,
  Package,
  Star,
  Code,
  Terminal,
  BookOpen,
  CheckCircle2
} from "lucide-react";
import { Link } from "react-router-dom";

const SDKs = () => {
  const sdks = [
    {
      name: "JavaScript / Node.js",
      icon: "📜",
      package: "@verigate/sdk",
      stars: "2.5k",
      version: "v2.1.0",
      npm: "https://www.npmjs.com/package/@verigate/sdk",
      github: "https://github.com/verigate/verigate-node",
      install: "npm install @verigate/sdk",
      features: ["Promise-based API", "TypeScript support", "Automatic retries", "Webhook helpers"],
      quickStart: [
        {
          language: "javascript",
          label: "JavaScript",
          code: `// Install
npm install @verigate/sdk

// Import and initialize
import VeriGate from '@verigate/sdk';

const verigate = new VeriGate({
  apiKey: process.env.VERIGATE_API_KEY,
  environment: 'production' // or 'sandbox'
});

// Create a verification
const verification = await verigate.verifications.create({
  type: 'identity',
  documents: {
    front: './id_front.jpg',
    back: './id_back.jpg',
    selfie: './selfie.jpg'
  },
  userData: {
    firstName: 'John',
    lastName: 'Doe',
    dateOfBirth: '1990-01-15'
  }
});

console.log(verification.status); // 'pending', 'approved', or 'rejected'`,
          filename: "index.js"
        }
      ]
    },
    {
      name: "Python",
      icon: "🐍",
      package: "verigate",
      stars: "1.8k",
      version: "v1.9.0",
      pypi: "https://pypi.org/project/verigate/",
      github: "https://github.com/verigate/verigate-python",
      install: "pip install verigate",
      features: ["Type hints", "Async support", "Django integration", "Context managers"],
      quickStart: [
        {
          language: "python",
          label: "Python",
          code: `# Install
pip install verigate

# Import and initialize
from verigate import VeriGate

verigate = VeriGate(
    api_key=os.environ['VERIGATE_API_KEY'],
    environment='production'  # or 'sandbox'
)

# Create a verification
verification = verigate.verifications.create(
    type='identity',
    documents={
        'front': open('id_front.jpg', 'rb'),
        'back': open('id_back.jpg', 'rb'),
        'selfie': open('selfie.jpg', 'rb')
    },
    user_data={
        'first_name': 'John',
        'last_name': 'Doe',
        'date_of_birth': '1990-01-15'
    }
)

print(verification.status)  # 'pending', 'approved', or 'rejected'`,
          filename: "main.py"
        }
      ]
    },
    {
      name: "Ruby",
      icon: "💎",
      package: "verigate",
      stars: "890",
      version: "v1.5.0",
      rubygems: "https://rubygems.org/gems/verigate",
      github: "https://github.com/verigate/verigate-ruby",
      install: "gem install verigate",
      features: ["Rails integration", "Active Record models", "Webhook middleware", "Idempotency keys"],
      quickStart: [
        {
          language: "ruby",
          label: "Ruby",
          code: `# Install
gem install verigate

# Or add to Gemfile
gem 'verigate'

# Initialize
require 'verigate'

VeriGate.configure do |config|
  config.api_key = ENV['VERIGATE_API_KEY']
  config.environment = 'production' # or 'sandbox'
end

# Create a verification
verification = VeriGate::Verification.create(
  type: 'identity',
  documents: {
    front: File.open('id_front.jpg'),
    back: File.open('id_back.jpg'),
    selfie: File.open('selfie.jpg')
  },
  user_data: {
    first_name: 'John',
    last_name: 'Doe',
    date_of_birth: '1990-01-15'
  }
)

puts verification.status  # 'pending', 'approved', or 'rejected'`,
          filename: "example.rb"
        }
      ]
    },
    {
      name: "PHP",
      icon: "🐘",
      package: "verigate/verigate-php",
      stars: "1.2k",
      version: "v2.0.0",
      packagist: "https://packagist.org/packages/verigate/verigate-php",
      github: "https://github.com/verigate/verigate-php",
      install: "composer require verigate/verigate-php",
      features: ["PSR-4 autoloading", "Laravel integration", "Symfony bundle", "Exception handling"],
      quickStart: [
        {
          language: "php",
          label: "PHP",
          code: `// Install
composer require verigate/verigate-php

// Initialize
<?php
require_once 'vendor/autoload.php';

use VeriGate\\VeriGate;

$verigate = new VeriGate([
    'api_key' => getenv('VERIGATE_API_KEY'),
    'environment' => 'production' // or 'sandbox'
]);

// Create a verification
$verification = $verigate->verifications->create([
    'type' => 'identity',
    'documents' => [
        'front' => fopen('id_front.jpg', 'r'),
        'back' => fopen('id_back.jpg', 'r'),
        'selfie' => fopen('selfie.jpg', 'r')
    ],
    'user_data' => [
        'first_name' => 'John',
        'last_name' => 'Doe',
        'date_of_birth' => '1990-01-15'
    ]
]);

echo $verification->status; // 'pending', 'approved', or 'rejected'`,
          filename: "index.php"
        }
      ]
    },
    {
      name: "Java",
      icon: "☕",
      package: "com.verigate:verigate-java",
      stars: "1.5k",
      version: "v3.0.1",
      maven: "https://search.maven.org/artifact/com.verigate/verigate-java",
      github: "https://github.com/verigate/verigate-java",
      install: "Maven or Gradle",
      features: ["Thread-safe", "Spring Boot support", "Reactive API", "Connection pooling"],
      quickStart: [
        {
          language: "java",
          label: "Java",
          code: `// Maven dependency
<dependency>
    <groupId>com.verigate</groupId>
    <artifactId>verigate-java</artifactId>
    <version>3.0.1</version>
</dependency>

// Initialize
import com.verigate.VeriGate;
import com.verigate.model.Verification;

VeriGate verigate = new VeriGate.Builder()
    .apiKey(System.getenv("VERIGATE_API_KEY"))
    .environment("production") // or "sandbox"
    .build();

// Create a verification
Verification verification = verigate.verifications().create(
    new VerificationRequest()
        .type("identity")
        .documents(new Documents()
            .front(new File("id_front.jpg"))
            .back(new File("id_back.jpg"))
            .selfie(new File("selfie.jpg"))
        )
        .userData(new UserData()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth("1990-01-15")
        )
);

System.out.println(verification.getStatus()); // "pending", "approved", or "rejected"`,
          filename: "Main.java"
        }
      ]
    },
    {
      name: "Go",
      icon: "🔷",
      package: "github.com/verigate/verigate-go",
      stars: "980",
      version: "v1.4.0",
      godoc: "https://pkg.go.dev/github.com/verigate/verigate-go",
      github: "https://github.com/verigate/verigate-go",
      install: "go get github.com/verigate/verigate-go",
      features: ["Context support", "Goroutine-safe", "Minimal dependencies", "Streaming API"],
      quickStart: [
        {
          language: "go",
          label: "Go",
          code: `// Install
go get github.com/verigate/verigate-go

// Initialize
package main

import (
    "context"
    "fmt"
    "os"
    verigate "github.com/verigate/verigate-go"
)

func main() {
    client := verigate.New(&verigate.Config{
        APIKey:      os.Getenv("VERIGATE_API_KEY"),
        Environment: "production", // or "sandbox"
    })

    // Create a verification
    verification, err := client.Verifications.Create(context.Background(), &verigate.VerificationRequest{
        Type: "identity",
        Documents: &verigate.Documents{
            Front:  "id_front.jpg",
            Back:   "id_back.jpg",
            Selfie: "selfie.jpg",
        },
        UserData: &verigate.UserData{
            FirstName:   "John",
            LastName:    "Doe",
            DateOfBirth: "1990-01-15",
        },
    })
    
    if err != nil {
        panic(err)
    }

    fmt.Println(verification.Status) // "pending", "approved", or "rejected"
}`,
          filename: "main.go"
        }
      ]
    },
    {
      name: "C# / .NET",
      icon: "🔹",
      package: "VeriGate",
      stars: "750",
      version: "v1.3.0",
      nuget: "https://www.nuget.org/packages/VeriGate/",
      github: "https://github.com/verigate/verigate-dotnet",
      install: "dotnet add package VeriGate",
      features: ["Async/await support", ".NET Core 6+", "Dependency injection", "Strong typing"],
      quickStart: [
        {
          language: "csharp",
          label: "C#",
          code: `// Install
dotnet add package VeriGate

// Initialize
using VeriGate;

var client = new VeriGateClient(new VeriGateOptions
{
    ApiKey = Environment.GetEnvironmentVariable("VERIGATE_API_KEY"),
    Environment = VeriGateEnvironment.Production // or Sandbox
});

// Create a verification
var verification = await client.Verifications.CreateAsync(new CreateVerificationRequest
{
    Type = "identity",
    Documents = new Documents
    {
        Front = File.OpenRead("id_front.jpg"),
        Back = File.OpenRead("id_back.jpg"),
        Selfie = File.OpenRead("selfie.jpg")
    },
    UserData = new UserData
    {
        FirstName = "John",
        LastName = "Doe",
        DateOfBirth = "1990-01-15"
    }
});

Console.WriteLine(verification.Status); // "pending", "approved", or "rejected"`,
          filename: "Program.cs"
        }
      ]
    },
    {
      name: "Swift (iOS)",
      icon: "🦅",
      package: "VeriGateSDK",
      stars: "620",
      version: "v1.2.0",
      cocoapods: "https://cocoapods.org/pods/VeriGateSDK",
      github: "https://github.com/verigate/verigate-swift",
      install: "CocoaPods or Swift Package Manager",
      features: ["iOS 13+", "SwiftUI support", "Combine framework", "Camera integration"],
      quickStart: [
        {
          language: "swift",
          label: "Swift",
          code: `// Install via SPM
// Add: https://github.com/verigate/verigate-swift

// Initialize
import VeriGateSDK

let verigate = VeriGate(
    apiKey: ProcessInfo.processInfo.environment["VERIGATE_API_KEY"]!,
    environment: .production // or .sandbox
)

// Create a verification
do {
    let verification = try await verigate.verifications.create(
        type: .identity,
        documents: Documents(
            front: frontImageData,
            back: backImageData,
            selfie: selfieImageData
        ),
        userData: UserData(
            firstName: "John",
            lastName: "Doe",
            dateOfBirth: "1990-01-15"
        )
    )
    
    print(verification.status) // .pending, .approved, or .rejected
} catch {
    print("Error: \\(error)")
}`,
          filename: "ViewController.swift"
        }
      ]
    },
    {
      name: "Kotlin (Android)",
      icon: "🟣",
      package: "com.verigate:verigate-android",
      stars: "540",
      version: "v1.1.0",
      maven: "https://search.maven.org/artifact/com.verigate/verigate-android",
      github: "https://github.com/verigate/verigate-android",
      install: "Gradle",
      features: ["Android 5.0+", "Kotlin coroutines", "Jetpack Compose", "Camera2 API"],
      quickStart: [
        {
          language: "kotlin",
          label: "Kotlin",
          code: `// Gradle dependency
implementation 'com.verigate:verigate-android:1.1.0'

// Initialize
import com.verigate.VeriGate
import com.verigate.model.Verification

val verigate = VeriGate(
    apiKey = System.getenv("VERIGATE_API_KEY"),
    environment = VeriGate.Environment.PRODUCTION // or SANDBOX
)

// Create a verification
lifecycleScope.launch {
    try {
        val verification = verigate.verifications.create(
            type = VerificationType.IDENTITY,
            documents = Documents(
                front = frontImageFile,
                back = backImageFile,
                selfie = selfieImageFile
            ),
            userData = UserData(
                firstName = "John",
                lastName = "Doe",
                dateOfBirth = "1990-01-15"
            )
        )
        
        println(verification.status) // PENDING, APPROVED, or REJECTED
    } catch (e: Exception) {
        println("Error: \${e.message}")
    }
}`,
          filename: "MainActivity.kt"
        }
      ]
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
              <Package className="w-3 h-3 mr-1" />
              Official SDKs
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              SDKs & Client Libraries
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Official client libraries for 9 programming languages. 
              Get started in minutes with idiomatic code for your platform.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button size="lg" asChild>
                <Link to="/developers/api-reference">
                  <BookOpen className="w-4 h-4 mr-2" />
                  API Reference
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/developers/sdks">
                  <Code className="w-4 h-4 mr-2" />
                  View SDKs
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* SDKs Grid */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="space-y-12">
            {sdks.map((sdk, index) => (
              <div key={index} id={sdk.name.toLowerCase().replace(/\s+/g, '-')}>
                <Card>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex items-center gap-3">
                        <div className="text-4xl">{sdk.icon}</div>
                        <div>
                          <CardTitle className="text-2xl mb-1">{sdk.name}</CardTitle>
                          <div className="flex items-center gap-3 text-sm text-muted-foreground">
                            <Badge variant="secondary">{sdk.version}</Badge>
                            <div className="flex items-center gap-1">
                              <Star className="w-3 h-3 fill-yellow-400 text-yellow-400" />
                              <span>{sdk.stars}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        {sdk.github && (
                          <Button variant="outline" size="sm" asChild>
                            <a href={sdk.github} target="_blank" rel="noopener noreferrer">
                              <Github className="w-4 h-4 mr-1" />
                              GitHub
                            </a>
                          </Button>
                        )}
                        {(sdk.npm || sdk.pypi || sdk.rubygems || sdk.packagist || sdk.maven || sdk.godoc || sdk.nuget || sdk.cocoapods) && (
                          <Button variant="outline" size="sm" asChild>
                            <a href={sdk.npm || sdk.pypi || sdk.rubygems || sdk.packagist || sdk.maven || sdk.godoc || sdk.nuget || sdk.cocoapods} target="_blank" rel="noopener noreferrer">
                              <ExternalLink className="w-4 h-4 mr-1" />
                              Package
                            </a>
                          </Button>
                        )}
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* Features */}
                    <div>
                      <h4 className="font-semibold mb-3 flex items-center gap-2">
                        <CheckCircle2 className="w-4 h-4 text-green-500" />
                        Key Features
                      </h4>
                      <div className="grid md:grid-cols-2 gap-2">
                        {sdk.features.map((feature, idx) => (
                          <div key={idx} className="flex items-center gap-2 text-sm text-muted-foreground">
                            <div className="w-1 h-1 bg-primary rounded-full" />
                            {feature}
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Installation */}
                    <div>
                      <h4 className="font-semibold mb-3 flex items-center gap-2">
                        <Terminal className="w-4 h-4" />
                        Installation
                      </h4>
                      <div className="bg-slate-950 text-slate-50 p-4 rounded-lg font-mono text-sm">
                        {sdk.install}
                      </div>
                    </div>

                    {/* Quick Start */}
                    <div>
                      <h4 className="font-semibold mb-3">Quick Start Example</h4>
                      <MultiLanguageCode examples={sdk.quickStart} />
                    </div>
                  </CardContent>
                </Card>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Support Section */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Need Help?</h2>
            <p className="text-muted-foreground mb-8">
              Our developer support team is here to help you get started
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Button asChild>
                <Link to="/help">
                  <BookOpen className="w-4 h-4 mr-2" />
                  Help Center
                </Link>
              </Button>
              <Button variant="outline" asChild>
                <Link to="/contact">
                  Contact Support
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

export default SDKs;
