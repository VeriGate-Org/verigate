import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { 
  Search,
  Calendar,
  Clock,
  User,
  ArrowRight,
  Tag,
  TrendingUp,
  BookOpen,
  Filter
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";

const Blog = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("all");

  const categories = [
    { id: "all", name: "All Posts", count: 24 },
    { id: "compliance", name: "Compliance", count: 8 },
    { id: "security", name: "Security", count: 6 },
    { id: "product", name: "Product Updates", count: 5 },
    { id: "industry", name: "Industry Insights", count: 3 },
    { id: "technical", name: "Technical", count: 2 }
  ];

  const featuredPost = {
    id: 1,
    title: "The Complete Guide to KYC Compliance in 2025",
    excerpt: "Everything you need to know about global KYC regulations, best practices, and how to implement compliant identity verification in your platform.",
    author: {
      name: "Sarah Mitchell",
      role: "Chief Compliance Officer",
      avatar: "👩‍💼"
    },
    date: "2025-01-15",
    readTime: "12 min read",
    category: "Compliance",
    tags: ["KYC", "Compliance", "Regulations", "Best Practices"],
    image: "/blog/kyc-compliance-2025.jpg",
    featured: true
  };

  const blogPosts = [
    {
      id: 2,
      title: "AML Screening: Best Practices for Fintech Companies",
      excerpt: "Learn how to implement effective AML screening processes that balance compliance requirements with user experience.",
      author: {
        name: "Michael Chen",
        role: "Product Manager",
        avatar: "👨‍💻"
      },
      date: "2025-01-12",
      readTime: "8 min read",
      category: "Compliance",
      tags: ["AML", "Fintech", "Screening"],
      image: "/blog/aml-screening.jpg"
    },
    {
      id: 3,
      title: "How to Integrate Identity Verification in 5 Minutes",
      excerpt: "A step-by-step tutorial showing how to add identity verification to your application using our JavaScript SDK.",
      author: {
        name: "Alex Rodriguez",
        role: "Developer Advocate",
        avatar: "👨‍🔧"
      },
      date: "2025-01-10",
      readTime: "5 min read",
      category: "Technical",
      tags: ["Tutorial", "JavaScript", "Integration"],
      image: "/blog/integration-tutorial.jpg"
    },
    {
      id: 4,
      title: "Document Fraud Detection with AI: Behind the Scenes",
      excerpt: "Explore the machine learning algorithms and techniques we use to detect fraudulent documents with 99.8% accuracy.",
      author: {
        name: "Dr. Emily Watson",
        role: "Head of AI Research",
        avatar: "👩‍🔬"
      },
      date: "2025-01-08",
      readTime: "10 min read",
      category: "Technical",
      tags: ["AI", "Machine Learning", "Fraud Detection"],
      image: "/blog/ai-fraud-detection.jpg"
    },
    {
      id: 5,
      title: "Biometric Authentication: Active vs Passive Liveness",
      excerpt: "Understanding the differences between active and passive liveness detection and when to use each approach.",
      author: {
        name: "James Park",
        role: "Security Architect",
        avatar: "👨‍💼"
      },
      date: "2025-01-05",
      readTime: "7 min read",
      category: "Security",
      tags: ["Biometrics", "Liveness", "Security"],
      image: "/blog/biometric-liveness.jpg"
    },
    {
      id: 6,
      title: "GDPR Compliance Checklist for Identity Verification",
      excerpt: "A comprehensive checklist to ensure your identity verification process complies with GDPR requirements.",
      author: {
        name: "Sarah Mitchell",
        role: "Chief Compliance Officer",
        avatar: "👩‍💼"
      },
      date: "2025-01-03",
      readTime: "9 min read",
      category: "Compliance",
      tags: ["GDPR", "Privacy", "Compliance"],
      image: "/blog/gdpr-checklist.jpg"
    },
    {
      id: 7,
      title: "Travel Rule Compliance for Crypto Exchanges",
      excerpt: "How crypto exchanges can implement the FATF Travel Rule while maintaining user privacy and security.",
      author: {
        name: "David Kim",
        role: "Crypto Compliance Lead",
        avatar: "👨‍💼"
      },
      date: "2025-01-01",
      readTime: "11 min read",
      category: "Compliance",
      tags: ["Crypto", "Travel Rule", "FATF"],
      image: "/blog/travel-rule.jpg"
    },
    {
      id: 8,
      title: "Age Verification for Gaming Platforms: Complete Guide",
      excerpt: "Implementing compliant age verification for online gaming and gambling platforms across different jurisdictions.",
      author: {
        name: "Lisa Anderson",
        role: "Gaming Industry Expert",
        avatar: "👩‍💼"
      },
      date: "2024-12-28",
      readTime: "8 min read",
      category: "Industry",
      tags: ["Gaming", "Age Verification", "Compliance"],
      image: "/blog/age-verification-gaming.jpg"
    },
    {
      id: 9,
      title: "HIPAA-Compliant Patient Verification in Telehealth",
      excerpt: "Best practices for implementing patient identity verification in telehealth platforms while maintaining HIPAA compliance.",
      author: {
        name: "Dr. Robert Martinez",
        role: "Healthcare Compliance Advisor",
        avatar: "👨‍⚕️"
      },
      date: "2024-12-25",
      readTime: "10 min read",
      category: "Industry",
      tags: ["Healthcare", "HIPAA", "Telehealth"],
      image: "/blog/hipaa-telehealth.jpg"
    },
    {
      id: 10,
      title: "ROI of Automated Identity Verification",
      excerpt: "Calculate the return on investment from automating your identity verification process with real-world examples.",
      author: {
        name: "Jennifer Lee",
        role: "Business Analyst",
        avatar: "👩‍💼"
      },
      date: "2024-12-22",
      readTime: "6 min read",
      category: "Industry",
      tags: ["ROI", "Business Case", "Automation"],
      image: "/blog/roi-automation.jpg"
    },
    {
      id: 11,
      title: "Product Update: New Document Types and Coverage",
      excerpt: "We've added support for 500+ new document types across 25 countries. Here's what's new and improved.",
      author: {
        name: "Michael Chen",
        role: "Product Manager",
        avatar: "👨‍💻"
      },
      date: "2024-12-20",
      readTime: "4 min read",
      category: "Product",
      tags: ["Product Update", "Documents", "Coverage"],
      image: "/blog/product-update-docs.jpg"
    }
  ];

  const popularTags = [
    "KYC", "AML", "Compliance", "Security", "GDPR", 
    "Biometrics", "AI", "Integration", "Tutorial", "Best Practices"
  ];

  const filteredPosts = blogPosts.filter(post => {
    const matchesCategory = selectedCategory === "all" || post.category.toLowerCase() === selectedCategory;
    const matchesSearch = searchQuery === "" || 
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.excerpt.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <Badge className="mb-4" variant="outline">
              <BookOpen className="w-3 h-3 mr-1" />
              Blog & Insights
            </Badge>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              VeriGate Blog
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Insights on identity verification, compliance, security, and industry trends 
              from the VeriGate team and experts.
            </p>
            
            {/* Search */}
            <div className="max-w-2xl mx-auto relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
              <Input
                type="search"
                placeholder="Search articles..."
                className="pl-12 h-12 text-lg"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="py-8 border-b bg-background sticky top-0 z-10">
        <div className="container mx-auto">
          <div className="flex items-center gap-2 overflow-x-auto pb-2">
            <Filter className="w-4 h-4 text-muted-foreground flex-shrink-0" />
            {categories.map((category) => (
              <Button
                key={category.id}
                variant={selectedCategory === category.id ? "default" : "outline"}
                size="sm"
                onClick={() => setSelectedCategory(category.id)}
                className="flex-shrink-0"
              >
                {category.name}
                <Badge variant="secondary" className="ml-2 text-xs">
                  {category.count}
                </Badge>
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* Featured Post */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="flex items-center gap-2 mb-6">
              <TrendingUp className="w-5 h-5 text-primary" />
              <h2 className="text-2xl font-bold">Featured Article</h2>
            </div>

            <Card className="overflow-hidden hover:shadow-xl transition-shadow">
              <div className="grid md:grid-cols-2 gap-0">
                <div className="bg-gradient-to-br from-primary/10 to-secondary/10 p-12 flex items-center justify-center">
                  <div className="text-center">
                    <div className="text-6xl mb-4">📋</div>
                    <Badge className="mb-2">Featured</Badge>
                  </div>
                </div>
                <div className="p-8 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center gap-2 mb-3">
                      <Badge variant="secondary">{featuredPost.category}</Badge>
                      <span className="text-sm text-muted-foreground">
                        {new Date(featuredPost.date).toLocaleDateString('en-US', { 
                          month: 'short', 
                          day: 'numeric', 
                          year: 'numeric' 
                        })}
                      </span>
                    </div>
                    <h3 className="text-2xl font-bold mb-3 hover:text-primary transition-colors">
                      <Link to={`/blog/${featuredPost.id}`}>
                        {featuredPost.title}
                      </Link>
                    </h3>
                    <p className="text-muted-foreground mb-4">
                      {featuredPost.excerpt}
                    </p>
                    <div className="flex flex-wrap gap-2 mb-4">
                      {featuredPost.tags.map((tag, index) => (
                        <Badge key={index} variant="outline" className="text-xs">
                          <Tag className="w-3 h-3 mr-1" />
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  </div>
                  <div className="flex items-center justify-between pt-4 border-t">
                    <div className="flex items-center gap-3">
                      <div className="text-2xl">{featuredPost.author.avatar}</div>
                      <div>
                        <div className="font-medium text-sm">{featuredPost.author.name}</div>
                        <div className="text-xs text-muted-foreground">{featuredPost.author.role}</div>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Clock className="w-4 h-4" />
                      {featuredPost.readTime}
                    </div>
                  </div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* Recent Posts */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">
              {selectedCategory === "all" ? "Recent Articles" : `${categories.find(c => c.id === selectedCategory)?.name} Articles`}
            </h2>

            {filteredPosts.length === 0 ? (
              <Card className="p-12 text-center">
                <p className="text-muted-foreground">No articles found matching your criteria.</p>
              </Card>
            ) : (
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredPosts.map((post) => (
                  <Card key={post.id} className="flex flex-col hover:shadow-lg transition-shadow">
                    <div className="h-48 bg-gradient-to-br from-primary/10 to-secondary/10 flex items-center justify-center">
                      <div className="text-6xl">📄</div>
                    </div>
                    <CardHeader className="flex-grow">
                      <div className="flex items-center gap-2 mb-2">
                        <Badge variant="secondary" className="text-xs">{post.category}</Badge>
                        <span className="text-xs text-muted-foreground">
                          {new Date(post.date).toLocaleDateString('en-US', { 
                            month: 'short', 
                            day: 'numeric' 
                          })}
                        </span>
                      </div>
                      <CardTitle className="text-lg hover:text-primary transition-colors line-clamp-2">
                        <Link to={`/blog/${post.id}`}>
                          {post.title}
                        </Link>
                      </CardTitle>
                      <CardDescription className="line-clamp-3">
                        {post.excerpt}
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="pt-0">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <div className="text-xl">{post.author.avatar}</div>
                          <div className="text-sm">
                            <div className="font-medium">{post.author.name}</div>
                          </div>
                        </div>
                        <div className="flex items-center gap-1 text-xs text-muted-foreground">
                          <Clock className="w-3 h-3" />
                          {post.readTime}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Popular Tags */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Popular Topics</h2>
            <div className="flex flex-wrap gap-3">
              {popularTags.map((tag, index) => (
                <Button key={index} variant="outline" size="lg">
                  <Tag className="w-4 h-4 mr-2" />
                  {tag}
                </Button>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Newsletter CTA */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
        <div className="container mx-auto">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Stay Updated</h2>
            <p className="text-lg mb-8 text-primary-foreground/90">
              Subscribe to our newsletter for the latest insights on identity verification, 
              compliance, and industry trends.
            </p>
            <div className="flex gap-4 max-w-md mx-auto">
              <Input 
                type="email" 
                placeholder="Enter your email" 
                className="bg-primary-foreground text-foreground"
              />
              <Button size="lg" variant="secondary">
                Subscribe
              </Button>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Blog;
