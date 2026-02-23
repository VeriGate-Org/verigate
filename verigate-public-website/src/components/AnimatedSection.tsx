import { motion } from "framer-motion";
import { ReactNode } from "react";

interface AnimatedSectionProps {
  children: ReactNode;
  className?: string;
  delay?: number;
}

/**
 * AnimatedSection - Wrapper component for fade-in animations on scroll
 * Uses Framer Motion for smooth scroll-triggered animations
 */
export const AnimatedSection = ({ 
  children, 
  className = "",
  delay = 0 
}: AnimatedSectionProps) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-100px" }}
      transition={{ 
        duration: 0.6, 
        delay,
        ease: [0.25, 0.1, 0.25, 1] // Custom easing for smooth feel
      }}
      className={className}
    >
      {children}
    </motion.div>
  );
};

interface StaggeredListProps {
  children: ReactNode[];
  className?: string;
  staggerDelay?: number;
  containerDelay?: number;
}

/**
 * StaggeredList - Animates children with stagger effect
 * Perfect for grids, lists, and card collections
 */
export const StaggeredList = ({ 
  children, 
  className = "",
  staggerDelay = 0.1,
  containerDelay = 0
}: StaggeredListProps) => {
  const container = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        delayChildren: containerDelay,
        staggerChildren: staggerDelay
      }
    }
  };

  const item = {
    hidden: { opacity: 0, y: 20 },
    visible: { 
      opacity: 1, 
      y: 0,
      transition: {
        duration: 0.5,
        ease: [0.25, 0.1, 0.25, 1]
      }
    }
  };

  return (
    <motion.div
      variants={container}
      initial="hidden"
      whileInView="visible"
      viewport={{ once: true, margin: "-50px" }}
      className={className}
    >
      {children.map((child, index) => (
        <motion.div key={index} variants={item}>
          {child}
        </motion.div>
      ))}
    </motion.div>
  );
};

/**
 * FadeIn - Simple fade-in component
 * Lighter alternative to AnimatedSection
 */
export const FadeIn = ({ 
  children, 
  className = "",
  delay = 0 
}: AnimatedSectionProps) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      whileInView={{ opacity: 1 }}
      viewport={{ once: true }}
      transition={{ duration: 0.8, delay }}
      className={className}
    >
      {children}
    </motion.div>
  );
};
