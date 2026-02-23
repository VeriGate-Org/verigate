// Tailwind v4 primarily relies on CSS @theme tokens, but this config records
// brand colors and fonts for editor tooling and compatibility.
import type { Config } from "tailwindcss";

export default {
  theme: {
    extend: {
      colors: {
        primary: "#0972d3", // AWS console blue
        danger: "#d13212",
        success: "#2c974b",
        warning: "#c28b0b",
        info: "#0972d3",
        background: "#f2f3f3",
        surface: "#ffffff",
        text: "#1a2024",
        "text-muted": "#4f5b67",
        border: "#d5dbdb",
        "border-control": "#687078",
        "border-focused": "#0972d3",
        hover: "#e4e7e7",
      },
      fontFamily: {
        sans: ["Amazon Ember", "Inter", "ui-sans-serif", "system-ui"],
        mono: ["JetBrains Mono", "ui-monospace", "SFMono-Regular"],
      },
      fontSize: {
        'aws-caption': '12px',
        'aws-body': '13px',
        'aws-heading-xs': '16px',
        'aws-heading-s': '18px',
        'aws-heading-m': '20px',
        'aws-heading-l': '24px',
        'aws-heading-xl': '32px',
      },
      lineHeight: {
        'aws-caption': '16px',
        'aws-body': '19px',
        'aws-heading-xs': '22px',
        'aws-heading-s': '24px',
        'aws-heading-m': '28px',
        'aws-heading-l': '32px',
        'aws-heading-xl': '40px',
      },
      spacing: {
        'aws-2xs': '2px',
        'aws-xs': '4px',
        'aws-s': '8px',
        'aws-m': '16px',
        'aws-l': '20px',
        'aws-xl': '32px',
        'aws-xxl': '40px',
      },
      borderRadius: {
        'aws-control': '2px',
        'aws-container': '8px',
        'aws-token': '16px',
      },
      boxShadow: {
        'aws-base': '0 1px 1px 0 rgba(0, 28, 36, 0.3)',
        'aws-raised': '0 1px 4px 2px rgba(0, 28, 36, 0.15)',
        'aws-sticky': '0 6px 12px 4px rgba(0, 28, 36, 0.15)',
        'aws-modal': '0 8px 16px 4px rgba(0, 28, 36, 0.18)',
      },
      transitionDuration: {
        'aws-quick': '100ms',
        'aws-show': '200ms',
        'aws-slow': '500ms',
      },
      transitionTimingFunction: {
        'aws-ease': 'cubic-bezier(0.4, 0, 0.2, 1)',
      },
    },
  },
  content: ["./src/**/*.{js,ts,jsx,tsx}", "./app/**/*.{js,ts,jsx,tsx}"],
} satisfies Config;
