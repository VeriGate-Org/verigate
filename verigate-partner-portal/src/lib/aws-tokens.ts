/**
 * AWS Console Design Tokens
 * Based on AWS Cloudscape Design System specifications
 */

export const awsTokens = {
  // AWS Console specific spacing scale
  space: {
    'none': '0px',
    '2xs': '2px',
    'xs': '4px',
    's': '8px',
    'm': '16px',
    'l': '20px',
    'xl': '32px',
    'xxl': '40px',
    'xxxl': '64px'
  },
  
  // AWS Console border radius scale
  borderRadius: {
    'none': '0px',
    's': '2px',
    'm': '4px',
    'l': '8px',
    'control': '2px', // AWS form controls
    'container': '8px', // AWS containers
    'token': '16px' // AWS token/pill components
  },
  
  // AWS Console elevation system
  elevation: {
    'none': 'none',
    'base': '0 1px 1px 0 rgba(0, 28, 36, 0.3)',
    'raised': '0 1px 4px 2px rgba(0, 28, 36, 0.15)',
    'sticky': '0 6px 12px 4px rgba(0, 28, 36, 0.15)',
    'modal': '0 8px 16px 4px rgba(0, 28, 36, 0.18)'
  },
  
  // AWS Console motion tokens
  motion: {
    duration: {
      quick: '100ms',
      show: '200ms',
      slow: '500ms'
    },
    easing: {
      ease: 'cubic-bezier(0.4, 0, 0.2, 1)',
      easeIn: 'cubic-bezier(0.4, 0, 1, 1)',
      easeOut: 'cubic-bezier(0, 0, 0.2, 1)',
      easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)'
    }
  },
  
  // AWS Console specific color tokens (extending existing)
  colors: {
    // Status colors matching AWS Console
    status: {
      success: '#2c974b',
      warning: '#c28b0b', 
      error: '#d13212',
      info: '#0972d3',
      pending: '#8d6e63'
    },
    
    // Border variations
    border: {
      default: '#d5dbdb',
      control: '#687078',
      focused: '#0972d3',
      invalid: '#d13212',
      divider: '#e9ebed'
    },
    
    // Background variations  
    background: {
      primary: '#ffffff',
      secondary: '#f2f3f3',
      tertiary: '#e9ebed',
      control: '#ffffff',
      overlay: 'rgba(0, 28, 36, 0.5)'
    }
  },
  
  // AWS Console typography scale
  typography: {
    fontSize: {
      'caption': '12px',
      'body': '13px', // AWS Console base
      'heading-xs': '16px',
      'heading-s': '18px',
      'heading-m': '20px',
      'heading-l': '24px',
      'heading-xl': '32px'
    },
    lineHeight: {
      'caption': '16px',
      'body': '19px',
      'heading-xs': '22px',
      'heading-s': '24px',
      'heading-m': '28px',
      'heading-l': '32px',
      'heading-xl': '40px'
    },
    fontWeight: {
      'normal': '400',
      'medium': '500',
      'bold': '700'
    },
    letterSpacing: {
      'normal': '0px',
      'wide': '0.5px'
    }
  }
} as const;

// Type-safe token access
export type SpaceToken = keyof typeof awsTokens.space;
export type BorderRadiusToken = keyof typeof awsTokens.borderRadius;
export type ElevationToken = keyof typeof awsTokens.elevation;

// Utility functions for token usage
export const getSpaceValue = (token: SpaceToken) => awsTokens.space[token];
export const getBorderRadiusValue = (token: BorderRadiusToken) => awsTokens.borderRadius[token];
export const getElevationValue = (token: ElevationToken) => awsTokens.elevation[token];