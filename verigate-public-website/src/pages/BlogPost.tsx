import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Calendar, Clock, ArrowLeft, Tag, Share2 } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import { getPostBySlug, defaultPost } from "@/data/blogContent";

const BlogPost = () => {
  const { slug } = useParams<{ slug: string }>();
  const post = getPostBySlug(slug || "") || defaultPost;

  return (
    <div className="flex flex-col">
      <article className="pt-24 pb-16">
        <div className="container mx-auto max-w-4xl">
          {/* Back Button */}
          <Button variant="ghost" asChild className="mb-6">
            <Link to="/blog">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Blog
            </Link>
          </Button>

          {/* Category and Date */}
          <div className="flex items-center gap-3 mb-4">
            <Badge variant="secondary">{post.category}</Badge>
            <span className="text-sm text-muted-foreground flex items-center gap-1">
              <Calendar className="w-4 h-4" />
              {new Date(post.date).toLocaleDateString("en-ZA", {
                day: "numeric",
                month: "long",
                year: "numeric",
              })}
            </span>
            <span className="text-sm text-muted-foreground flex items-center gap-1">
              <Clock className="w-4 h-4" />
              {post.readTime}
            </span>
          </div>

          {/* Title */}
          <h1 className="text-4xl md:text-5xl font-bold mb-6">{post.title}</h1>

          {/* Excerpt */}
          <p className="text-xl text-muted-foreground mb-8">{post.excerpt}</p>

          {/* Author and Share */}
          <div className="flex items-center justify-between pb-8 mb-8 border-b">
            <div className="flex items-center gap-3">
              {post.author.photo ? (
                <img src={post.author.photo} alt={post.author.name} className="w-12 h-12 rounded-full object-cover ring-2 ring-accent/20" loading="lazy" />
              ) : (
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold">
                  {post.author.avatar}
                </div>
              )}
              <div>
                <div className="font-semibold">{post.author.name}</div>
                <div className="text-sm text-muted-foreground">{post.author.role}</div>
              </div>
            </div>
            <Button variant="outline" size="sm">
              <Share2 className="w-4 h-4 mr-2" />
              Share
            </Button>
          </div>

          {/* Article Content */}
          <div className="prose prose-lg max-w-none mb-12">
            <div className="text-foreground leading-relaxed space-y-4">
              {post.content.split("\n\n").map((paragraph: string, index: number) => (
                <p key={index} className="text-lg">
                  {paragraph}
                </p>
              ))}
            </div>
          </div>

          {/* Tags */}
          {post.tags.length > 0 && (
            <div className="mb-12">
              <h3 className="font-semibold mb-3">Tags</h3>
              <div className="flex flex-wrap gap-2">
                {post.tags.map((tag: string) => (
                  <Badge key={tag} variant="outline">
                    <Tag className="w-3 h-3 mr-1" />
                    {tag}
                  </Badge>
                ))}
              </div>
            </div>
          )}

          {/* Author Card */}
          <div className="flex items-start gap-6 p-6 border border-border rounded-lg mb-12">
            {post.author.photo ? (
              <img src={post.author.photo} alt={post.author.name} className="w-20 h-20 rounded-full object-cover flex-shrink-0 ring-2 ring-accent/20" loading="lazy" />
            ) : (
              <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-2xl flex-shrink-0">
                {post.author.avatar}
              </div>
            )}
            <div>
              <h3 className="font-semibold text-lg">{post.author.name}</h3>
              <p className="text-sm text-accent font-medium mb-2">{post.author.role}</p>
              {post.author.bio && <p className="text-sm text-muted-foreground mb-3">{post.author.bio}</p>}
              {post.author.linkedin && (
                <a href={post.author.linkedin} target="_blank" rel="noopener noreferrer" className="text-sm text-accent hover:underline">
                  View on LinkedIn
                </a>
              )}
            </div>
          </div>

          {/* CTA */}
          <Card className="bg-gradient-to-br from-primary/10 to-secondary/10">
            <CardContent className="p-8 text-center">
              <h3 className="text-2xl font-bold mb-3">
                Ready to Streamline Your Verification Process?
              </h3>
              <p className="text-muted-foreground mb-6">
                Join 200+ South African organisations using VeriGate for background screening.
              </p>
              <div className="flex gap-4 justify-center flex-wrap">
                <Button size="lg" asChild>
                  <Link to="/request-demo">Request a Demo</Link>
                </Button>
                <Button size="lg" variant="outline" asChild>
                  <Link to="/pricing">View Pricing</Link>
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Back to Blog */}
          <div className="mt-12 text-center">
            <Button variant="outline" asChild>
              <Link to="/blog">
                <ArrowLeft className="w-4 h-4 mr-2" />
                Back to All Articles
              </Link>
            </Button>
          </div>
        </div>
      </article>
    </div>
  );
};

export default BlogPost;
