import { motion, useReducedMotion } from "framer-motion";
import { ReactNode } from "react";

type AnimationVariant = "fade-up" | "fade-in" | "slide-left" | "slide-right" | "scale-up";

interface AnimatedSectionProps {
  children: ReactNode;
  className?: string;
  delay?: number;
  variant?: AnimationVariant;
}

/**
 * AnimatedSection - Wrapper component for fade-in animations on scroll
 * Uses Framer Motion for smooth scroll-triggered animations
 * Respects prefers-reduced-motion
 */
const variantConfig: Record<AnimationVariant, { initial: Record<string, number>; animate: Record<string, number> }> = {
  "fade-up": { initial: { opacity: 0, y: 20 }, animate: { opacity: 1, y: 0 } },
  "fade-in": { initial: { opacity: 0 }, animate: { opacity: 1 } },
  "slide-left": { initial: { opacity: 0, x: -30 }, animate: { opacity: 1, x: 0 } },
  "slide-right": { initial: { opacity: 0, x: 30 }, animate: { opacity: 1, x: 0 } },
  "scale-up": { initial: { opacity: 0, scale: 0.95 }, animate: { opacity: 1, scale: 1 } },
};

export const AnimatedSection = ({
  children,
  className = "",
  delay = 0,
  variant = "fade-up"
}: AnimatedSectionProps) => {
  const shouldReduceMotion = useReducedMotion();
  const config = variantConfig[variant];

  return (
    <motion.div
      initial={shouldReduceMotion ? false : config.initial}
      whileInView={config.animate}
      viewport={{ once: true, margin: "-100px" }}
      transition={{
        duration: shouldReduceMotion ? 0 : 0.5,
        delay: shouldReduceMotion ? 0 : delay,
        ease: [0.4, 0, 0.2, 1]
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
 * Respects prefers-reduced-motion
 */
export const StaggeredList = ({
  children,
  className = "",
  staggerDelay = 0.1,
  containerDelay = 0
}: StaggeredListProps) => {
  const shouldReduceMotion = useReducedMotion();

  const container = {
    hidden: { opacity: shouldReduceMotion ? 1 : 0 },
    visible: {
      opacity: 1,
      transition: {
        delayChildren: shouldReduceMotion ? 0 : containerDelay,
        staggerChildren: shouldReduceMotion ? 0 : staggerDelay
      }
    }
  };

  const item = {
    hidden: { opacity: shouldReduceMotion ? 1 : 0, y: shouldReduceMotion ? 0 : 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: shouldReduceMotion ? 0 : 0.5,
        ease: [0.4, 0, 0.2, 1]
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
 * Respects prefers-reduced-motion
 */
export const FadeIn = ({
  children,
  className = "",
  delay = 0
}: AnimatedSectionProps) => {
  const shouldReduceMotion = useReducedMotion();

  return (
    <motion.div
      initial={shouldReduceMotion ? false : { opacity: 0 }}
      whileInView={{ opacity: 1 }}
      viewport={{ once: true }}
      transition={{ duration: shouldReduceMotion ? 0 : 0.5, delay: shouldReduceMotion ? 0 : delay, ease: [0.4, 0, 0.2, 1] }}
      className={className}
    >
      {children}
    </motion.div>
  );
};
