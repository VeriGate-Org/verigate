import { useState, useEffect } from "react";
import { testimonials } from "@/data/social-proof";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ChevronLeft, ChevronRight, Star, Quote, CheckCircle2, Linkedin } from "lucide-react";
import { AnimatedSection } from "@/components/AnimatedSection";
import { motion, AnimatePresence } from "framer-motion";

export function Testimonials() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isPaused, setIsPaused] = useState(false);

  // Auto-rotate testimonials
  useEffect(() => {
    if (!isPaused) {
      const interval = setInterval(() => {
        setCurrentIndex((prev) => (prev + 1) % testimonials.length);
      }, 6000); // Change every 6 seconds

      return () => clearInterval(interval);
    }
  }, [isPaused]);

  const nextTestimonial = () => {
    setCurrentIndex((prev) => (prev + 1) % testimonials.length);
    setIsPaused(true);
    setTimeout(() => setIsPaused(false), 10000); // Resume after 10s
  };

  const prevTestimonial = () => {
    setCurrentIndex((prev) => (prev - 1 + testimonials.length) % testimonials.length);
    setIsPaused(true);
    setTimeout(() => setIsPaused(false), 10000);
  };

  const goToTestimonial = (index: number) => {
    setCurrentIndex(index);
    setIsPaused(true);
    setTimeout(() => setIsPaused(false), 10000);
  };

  const current = testimonials[currentIndex];

  return (
    <section className="py-20 px-4 bg-secondary/30">
      <div className="container mx-auto max-w-6xl">
        {/* Header */}
        <div className="max-w-2xl mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            What Our Customers Say
          </h2>
          <p className="text-lg text-muted-foreground">
            Real feedback from companies using VeriGate
          </p>
        </div>

        {/* Main Testimonial Card */}
        <Card className="border-border relative overflow-hidden">
          {/* Quote Icon */}
          <div className="absolute top-6 right-6 opacity-10">
            <Quote className="w-24 h-24 text-accent" />
          </div>

          <CardContent className="pt-12 pb-8 px-8 relative z-10">
            {/* Rating */}
            <div className="flex items-center gap-1 mb-6">
              {[...Array(current.rating)].map((_, i) => (
                <Star key={i} className="w-5 h-5 fill-yellow-400 text-yellow-400" />
              ))}
            </div>

            {/* Quote */}
            <blockquote className="text-xl md:text-2xl font-medium text-foreground mb-8 leading-relaxed">
              "{current.quote}"
            </blockquote>

            {/* Metrics Badge */}
            {current.metrics && (
              <div className="mb-8">
                <Badge className="bg-accent/10 text-accent border-accent px-4 py-2 text-sm">
                  <CheckCircle2 className="w-4 h-4 mr-2" />
                  {current.metrics}
                </Badge>
              </div>
            )}

            {/* Author Info */}
            <div className="flex items-center gap-4 pt-6 border-t border-border">
              {/* Author Photo */}
              <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-xl flex-shrink-0">
                {current.author.split(' ').map(n => n[0]).join('')}
              </div>

              {/* Author Details */}
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <h4 className="font-semibold text-foreground">{current.author}</h4>
                  {current.verified && (
                    <CheckCircle2 className="w-4 h-4 text-accent" title="Verified Customer" />
                  )}
                </div>
                <p className="text-sm text-muted-foreground">
                  {current.role} at {current.company}
                </p>
                <div className="flex items-center gap-2 mt-1">
                  <Badge variant="outline" className="text-xs">{current.industry}</Badge>
                  {current.linkedin && (
                    <a 
                      href={current.linkedin} 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="text-accent hover:text-accent/80 transition-colors"
                    >
                      <Linkedin className="w-4 h-4" />
                    </a>
                  )}
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Navigation */}
        <div className="flex items-center justify-center gap-4 mt-8">
          <Button
            variant="outline"
            size="icon"
            onClick={prevTestimonial}
            aria-label="Previous testimonial"
          >
            <ChevronLeft className="w-4 h-4" />
          </Button>

          {/* Dots */}
          <div className="flex gap-2">
            {testimonials.map((_, index) => (
              <button
                key={index}
                onClick={() => goToTestimonial(index)}
                className={`w-2 h-2 rounded-full transition-all ${
                  index === currentIndex
                    ? "bg-accent w-8"
                    : "bg-border hover:bg-accent/50"
                }`}
                aria-label={`Go to testimonial ${index + 1}`}
              />
            ))}
          </div>

          <Button
            variant="outline"
            size="icon"
            onClick={nextTestimonial}
            aria-label="Next testimonial"
          >
            <ChevronRight className="w-4 h-4" />
          </Button>
        </div>

        {/* All Testimonials Grid (Optional - for showing multiple at once) */}
        <div className="mt-16 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {testimonials.slice(0, 3).map((testimonial) => (
            <Card key={testimonial.id} className="border-border hover:shadow-md transition-shadow">
              <CardContent className="pt-6">
                {/* Rating */}
                <div className="flex items-center gap-1 mb-4">
                  {[...Array(testimonial.rating)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                  ))}
                </div>

                {/* Quote */}
                <p className="text-sm text-muted-foreground mb-4 line-clamp-4">
                  "{testimonial.quote}"
                </p>

                {/* Author */}
                <div className="flex items-center gap-3 pt-4 border-t border-border">
                  <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-sm flex-shrink-0">
                    {testimonial.author.split(' ').map(n => n[0]).join('')}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-sm truncate">{testimonial.author}</p>
                    <p className="text-xs text-muted-foreground truncate">{testimonial.company}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
}
