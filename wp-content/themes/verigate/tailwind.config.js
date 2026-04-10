/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./*.php",
    "./inc/**/*.php",
    "./template-parts/**/*.php",
    "./page-templates/**/*.php",
    "./assets/js/**/*.js",
  ],
  safelist: [
    { pattern: /bg-category-(verification|compliance|fraud|industry|platform|company)\/(5|10|20)/ },
    { pattern: /text-category-(verification|compliance|fraud|industry|platform|company)/ },
    { pattern: /border-category-(verification|compliance|fraud|industry|platform|company)\/(20|30|50)/ },
  ],
  theme: {
    container: {
      center: true,
      padding: {
        DEFAULT: "1.5rem",
        sm: "2rem",
        lg: "3rem",
        xl: "4rem",
        "2xl": "6rem",
      },
      screens: {
        "2xl": "1280px",
      },
    },
    fontFamily: {
      sans: ['Inter', 'ui-sans-serif', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      mono: ['JetBrains Mono', 'ui-monospace', 'SFMono-Regular', 'Menlo', 'Monaco', 'monospace'],
      logo: ['Manrope', 'sans-serif'],
    },
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        brand: {
          navy: "hsl(var(--brand-navy))",
          cyan: "hsl(var(--brand-cyan))",
          "light-blue": "hsl(var(--brand-light-blue))",
          gray: "hsl(var(--brand-gray))",
        },
        rating: "hsl(var(--color-rating))",
        category: {
          verification: "hsl(var(--cat-verification))",
          compliance: "hsl(var(--cat-compliance))",
          fraud: "hsl(var(--cat-fraud))",
          industry: "hsl(var(--cat-industry))",
          platform: "hsl(var(--cat-platform))",
          company: "hsl(var(--cat-company))",
        },
      },
      backgroundImage: {
        "gradient-hero": "var(--gradient-hero)",
        "gradient-mesh": "var(--gradient-mesh)",
      },
      boxShadow: {
        sm: "var(--shadow-sm)",
        md: "var(--shadow-md)",
        lg: "var(--shadow-lg)",
        glow: "var(--shadow-glow)",
      },
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
      keyframes: {
        "fade-in": {
          "0%": { opacity: "0", transform: "translateY(10px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        "float": {
          "0%, 100%": { transform: "translateY(0px)" },
          "50%": { transform: "translateY(-20px)" },
        },
        "pulse-slow": {
          "0%, 100%": { opacity: "0.3" },
          "50%": { opacity: "0.5" },
        },
        "gradient": {
          "0%": { backgroundPosition: "0% 50%" },
          "50%": { backgroundPosition: "100% 50%" },
          "100%": { backgroundPosition: "0% 50%" },
        },
      },
      animation: {
        "fade-in": "fade-in 0.2s cubic-bezier(0.4, 0, 0.2, 1)",
        "float": "float 6s ease-in-out infinite",
        "float-delayed": "float 8s ease-in-out infinite 1s",
        "pulse-slow": "pulse-slow 4s ease-in-out infinite",
        "gradient": "gradient 3s ease-in-out infinite",
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
};
