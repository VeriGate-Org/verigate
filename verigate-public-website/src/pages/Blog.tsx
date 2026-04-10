import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import {
  Search,
  Clock,
  ArrowRight,
  Tag,
  TrendingUp,
  BookOpen,
  Filter
} from "lucide-react";
import { Link } from "react-router-dom";
import { useState } from "react";
import { blogPosts } from "@/data/blogContent";

const Blog = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("all");

  const categories = [
    { id: "all", name: "All Posts" },
    { id: "compliance", name: "Compliance" },
    { id: "industry", name: "Industry" },
    { id: "technical", name: "Technical" },
    { id: "product", name: "Product" },
  ];

  const featuredPost = blogPosts.find((p) => p.featured) || blogPosts[0];
  const otherPosts = blogPosts.filter((p) => p.slug !== featuredPost.slug);

  const filteredPosts = otherPosts.filter((post) => {
    const matchesCategory =
      selectedCategory === "all" || post.category.toLowerCase() === selectedCategory;
    const matchesSearch =
      searchQuery === "" ||
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.excerpt.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const popularTags = ["POPIA", "FICA", "Criminal Checks", "Compliance", "Background Screening", "Biometrics", "South Africa"];

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-primary/5 via-primary/10 to-secondary/5">
        <div className="container mx-auto max-w-6xl text-center">
          <Badge className="mb-4" variant="outline">
            <BookOpen className="w-3 h-3 mr-1" />
            Blog & Insights
          </Badge>
          <h1 className="text-4xl md:text-5xl font-bold mb-6">VeriGate Blog</h1>
          <p className="text-xl text-muted-foreground mb-8">
            Insights on background screening, compliance, and verification trends in South Africa
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
      </section>

      {/* Categories */}
      <section className="py-6 border-b bg-background">
        <div className="container mx-auto max-w-6xl">
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
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* Featured Post */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <div className="flex items-center gap-2 mb-6">
            <TrendingUp className="w-5 h-5 text-primary" />
            <h2 className="text-2xl font-bold">Featured Article</h2>
          </div>

          <Card className="overflow-hidden hover:shadow-xl transition-shadow">
            <div className="grid md:grid-cols-2 gap-0">
              <div className="bg-gradient-to-br from-primary/10 to-secondary/10 p-12 flex items-center justify-center">
                <div className="text-center">
                  {featuredPost.author.photo ? (
                    <img src={featuredPost.author.photo} alt={featuredPost.author.name} className="w-20 h-20 mx-auto mb-4 rounded-full object-cover" loading="lazy" />
                  ) : (
                    <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-accent/20 flex items-center justify-center text-2xl font-bold text-accent">
                      {featuredPost.author.avatar}
                    </div>
                  )}
                  <Badge className="mb-2">Featured</Badge>
                </div>
              </div>
              <div className="p-8 flex flex-col justify-between">
                <div>
                  <div className="flex items-center gap-2 mb-3">
                    <Badge variant="secondary">{featuredPost.category}</Badge>
                    <span className="text-sm text-muted-foreground">
                      {new Date(featuredPost.date).toLocaleDateString("en-ZA", {
                        day: "numeric",
                        month: "short",
                        year: "numeric",
                      })}
                    </span>
                  </div>
                  <h3 className="text-2xl font-bold mb-3 hover:text-primary transition-colors">
                    <Link to={`/blog/${featuredPost.slug}`}>{featuredPost.title}</Link>
                  </h3>
                  <p className="text-muted-foreground mb-4">{featuredPost.excerpt}</p>
                  <div className="flex flex-wrap gap-2 mb-4">
                    {featuredPost.tags.map((tag) => (
                      <Badge key={tag} variant="outline" className="text-xs">
                        <Tag className="w-3 h-3 mr-1" />
                        {tag}
                      </Badge>
                    ))}
                  </div>
                </div>
                <div className="flex items-center justify-between pt-4 border-t">
                  <div className="flex items-center gap-3">
                    {featuredPost.author.photo ? (
                      <img src={featuredPost.author.photo} alt={featuredPost.author.name} className="w-10 h-10 rounded-full object-cover" loading="lazy" />
                    ) : (
                      <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white text-sm font-bold">
                        {featuredPost.author.avatar}
                      </div>
                    )}
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
      </section>

      {/* Recent Posts */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold mb-8">
            {selectedCategory === "all"
              ? "Recent Articles"
              : `${categories.find((c) => c.id === selectedCategory)?.name} Articles`}
          </h2>

          {filteredPosts.length === 0 ? (
            <Card className="p-12 text-center">
              <p className="text-muted-foreground">No articles found matching your criteria.</p>
            </Card>
          ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredPosts.map((post) => (
                <Card key={post.slug} className="flex flex-col hover:shadow-lg transition-shadow">
                  <div className="h-48 bg-gradient-to-br from-primary/10 to-secondary/10 flex items-center justify-center">
                    {post.author.photo ? (
                      <img src={post.author.photo} alt={post.author.name} className="w-16 h-16 rounded-full object-cover" loading="lazy" />
                    ) : (
                      <div className="w-16 h-16 rounded-full bg-accent/20 flex items-center justify-center text-xl font-bold text-accent">
                        {post.author.avatar}
                      </div>
                    )}
                  </div>
                  <CardHeader className="flex-grow">
                    <div className="flex items-center gap-2 mb-2">
                      <Badge variant="secondary" className="text-xs">{post.category}</Badge>
                      <span className="text-xs text-muted-foreground">
                        {new Date(post.date).toLocaleDateString("en-ZA", {
                          day: "numeric",
                          month: "short",
                        })}
                      </span>
                    </div>
                    <CardTitle className="text-lg hover:text-primary transition-colors line-clamp-2">
                      <Link to={`/blog/${post.slug}`}>{post.title}</Link>
                    </CardTitle>
                    <CardDescription className="line-clamp-3">{post.excerpt}</CardDescription>
                  </CardHeader>
                  <CardContent className="pt-0">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        {post.author.photo ? (
                          <img src={post.author.photo} alt={post.author.name} className="w-8 h-8 rounded-full object-cover" loading="lazy" />
                        ) : (
                          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white text-xs font-bold">
                            {post.author.avatar}
                          </div>
                        )}
                        <span className="text-sm font-medium">{post.author.name}</span>
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
      </section>

      {/* Popular Tags */}
      <section className="py-16">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold mb-8">Popular Topics</h2>
          <div className="flex flex-wrap gap-3">
            {popularTags.map((tag) => (
              <Button key={tag} variant="outline" size="lg">
                <Tag className="w-4 h-4 mr-2" />
                {tag}
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* Newsletter CTA */}
      <section className="py-16 bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
        <div className="max-w-3xl mx-auto text-center px-4">
          <h2 className="text-3xl font-bold mb-4">Stay Updated</h2>
          <p className="text-lg mb-8 text-primary-foreground/90">
            Subscribe for the latest insights on background screening, compliance, and verification in South Africa.
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
      </section>
    </div>
  );
};

export default Blog;
