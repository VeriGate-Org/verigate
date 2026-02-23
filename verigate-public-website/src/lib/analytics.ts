/**
 * Analytics Integration
 * Supports Google Analytics 4, custom events, and conversion tracking
 * Integrates with cookie consent for GDPR compliance
 */

// Extend Window interface for analytics scripts
declare global {
  interface Window {
    gtag?: (...args: any[]) => void;
    dataLayer?: any[];
    clarity?: (...args: any[]) => void;
    fbq?: (...args: any[]) => void;
    lintrk?: (...args: any[]) => void;
  }
}

export interface AnalyticsEvent {
  action: string;
  category: string;
  label?: string;
  value?: number;
  nonInteraction?: boolean;
}

export interface ConversionEvent {
  event: string;
  value?: number;
  currency?: string;
  transactionId?: string;
  items?: any[];
}

export interface PageViewData {
  page_title: string;
  page_location: string;
  page_path: string;
}

/**
 * Google Analytics 4 Integration
 */
class GoogleAnalytics {
  private measurementId: string;
  private isInitialized = false;

  constructor(measurementId: string) {
    this.measurementId = measurementId;
  }

  /**
   * Initialize Google Analytics
   */
  init(): void {
    if (this.isInitialized || !this.measurementId) return;

    // Create gtag script
    const script = document.createElement('script');
    script.async = true;
    script.src = `https://www.googletagmanager.com/gtag/js?id=${this.measurementId}`;
    document.head.appendChild(script);

    // Initialize dataLayer
    window.dataLayer = window.dataLayer || [];
    window.gtag = function gtag(...args: any[]) {
      window.dataLayer!.push(arguments);
    };

    window.gtag('js', new Date());
    window.gtag('config', this.measurementId, {
      send_page_view: false, // We'll send manually
      cookie_flags: 'SameSite=None;Secure',
    });

    this.isInitialized = true;
    console.log('✅ Google Analytics initialized');
  }

  /**
   * Track page view
   */
  pageView(data?: Partial<PageViewData>): void {
    if (!this.isInitialized || !window.gtag) return;

    window.gtag('event', 'page_view', {
      page_title: data?.page_title || document.title,
      page_location: data?.page_location || window.location.href,
      page_path: data?.page_path || window.location.pathname,
    });
  }

  /**
   * Track custom event
   */
  event(name: string, params?: Record<string, any>): void {
    if (!this.isInitialized || !window.gtag) return;

    window.gtag('event', name, params);
  }

  /**
   * Track conversion
   */
  conversion(conversionId: string, params?: Record<string, any>): void {
    if (!this.isInitialized || !window.gtag) return;

    window.gtag('event', 'conversion', {
      send_to: conversionId,
      ...params,
    });
  }

  /**
   * Set user ID for cross-device tracking
   */
  setUserId(userId: string): void {
    if (!this.isInitialized || !window.gtag) return;

    window.gtag('config', this.measurementId, {
      user_id: userId,
    });
  }

  /**
   * Set user properties
   */
  setUserProperties(properties: Record<string, any>): void {
    if (!this.isInitialized || !window.gtag) return;

    window.gtag('set', 'user_properties', properties);
  }
}

/**
 * Microsoft Clarity Integration
 */
class MicrosoftClarity {
  private projectId: string;
  private isInitialized = false;

  constructor(projectId: string) {
    this.projectId = projectId;
  }

  init(): void {
    if (this.isInitialized || !this.projectId) return;

    (function(c: any, l: any, a: any, r: any, i: any, t: any, y: any) {
      c[a] = c[a] || function() { (c[a].q = c[a].q || []).push(arguments); };
      t = l.createElement(r); t.async = 1; t.src = 'https://www.clarity.ms/tag/' + i;
      y = l.getElementsByTagName(r)[0]; y.parentNode.insertBefore(t, y);
    })(window, document, 'clarity', 'script', this.projectId);

    this.isInitialized = true;
    console.log('✅ Microsoft Clarity initialized');
  }

  identify(userId: string, sessionId?: string, pageId?: string, friendlyName?: string): void {
    if (!this.isInitialized || !window.clarity) return;

    window.clarity('identify', userId, sessionId, pageId, friendlyName);
  }

  set(key: string, value: string): void {
    if (!this.isInitialized || !window.clarity) return;

    window.clarity('set', key, value);
  }
}

/**
 * Facebook Pixel Integration
 */
class FacebookPixel {
  private pixelId: string;
  private isInitialized = false;

  constructor(pixelId: string) {
    this.pixelId = pixelId;
  }

  init(): void {
    if (this.isInitialized || !this.pixelId) return;

    !(function(f: any, b: any, e: any, v: any, n?: any, t?: any, s?: any) {
      if (f.fbq) return;
      n = f.fbq = function() {
        n.callMethod ? n.callMethod.apply(n, arguments) : n.queue.push(arguments);
      };
      if (!f._fbq) f._fbq = n;
      n.push = n;
      n.loaded = !0;
      n.version = '2.0';
      n.queue = [];
      t = b.createElement(e);
      t.async = !0;
      t.src = v;
      s = b.getElementsByTagName(e)[0];
      s.parentNode.insertBefore(t, s);
    })(window, document, 'script', 'https://connect.facebook.net/en_US/fbevents.js');

    window.fbq!('init', this.pixelId);
    window.fbq!('track', 'PageView');

    this.isInitialized = true;
    console.log('✅ Facebook Pixel initialized');
  }

  track(event: string, params?: Record<string, any>): void {
    if (!this.isInitialized || !window.fbq) return;

    window.fbq('track', event, params);
  }

  trackCustom(event: string, params?: Record<string, any>): void {
    if (!this.isInitialized || !window.fbq) return;

    window.fbq('trackCustom', event, params);
  }
}

/**
 * LinkedIn Insight Tag Integration
 */
class LinkedInInsight {
  private partnerId: string;
  private isInitialized = false;

  constructor(partnerId: string) {
    this.partnerId = partnerId;
  }

  init(): void {
    if (this.isInitialized || !this.partnerId) return;

    (window as any)._linkedin_partner_id = this.partnerId;
    (window as any)._linkedin_data_partner_ids = (window as any)._linkedin_data_partner_ids || [];
    (window as any)._linkedin_data_partner_ids.push(this.partnerId);

    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.async = true;
    script.src = 'https://snap.licdn.com/li.lms-analytics/insight.min.js';
    const s = document.getElementsByTagName('script')[0];
    s.parentNode!.insertBefore(script, s);

    this.isInitialized = true;
    console.log('✅ LinkedIn Insight Tag initialized');
  }

  track(conversionId: string): void {
    if (!this.isInitialized || !window.lintrk) return;

    window.lintrk('track', { conversion_id: conversionId });
  }
}

/**
 * Main Analytics Service
 * Unified interface for all analytics platforms
 */
class AnalyticsService {
  private ga?: GoogleAnalytics;
  private clarity?: MicrosoftClarity;
  private facebook?: FacebookPixel;
  private linkedin?: LinkedInInsight;
  private isEnabled = false;

  constructor() {
    // Don't initialize automatically - wait for cookie consent
  }

  /**
   * Initialize analytics platforms based on consent
   */
  init(consent: {
    analytics?: boolean;
    marketing?: boolean;
  }): void {
    if (this.isEnabled) return;

    // Analytics platforms (GA4, Clarity)
    if (consent.analytics) {
      const gaId = import.meta.env.VITE_GA_MEASUREMENT_ID;
      if (gaId) {
        this.ga = new GoogleAnalytics(gaId);
        this.ga.init();
      }

      const clarityId = import.meta.env.VITE_CLARITY_PROJECT_ID;
      if (clarityId) {
        this.clarity = new MicrosoftClarity(clarityId);
        this.clarity.init();
      }
    }

    // Marketing platforms (Facebook, LinkedIn)
    if (consent.marketing) {
      const fbPixelId = import.meta.env.VITE_FB_PIXEL_ID;
      if (fbPixelId) {
        this.facebook = new FacebookPixel(fbPixelId);
        this.facebook.init();
      }

      const linkedinId = import.meta.env.VITE_LINKEDIN_PARTNER_ID;
      if (linkedinId) {
        this.linkedin = new LinkedInInsight(linkedinId);
        this.linkedin.init();
      }
    }

    this.isEnabled = true;
    console.log('✅ Analytics service initialized', consent);
  }

  /**
   * Track page view
   */
  trackPageView(path?: string, title?: string): void {
    if (!this.isEnabled) return;

    this.ga?.pageView({
      page_path: path || window.location.pathname,
      page_title: title || document.title,
      page_location: window.location.href,
    });
  }

  /**
   * Track custom event
   */
  trackEvent(name: string, params?: Record<string, any>): void {
    if (!this.isEnabled) return;

    this.ga?.event(name, params);
    
    // Track as custom event in Facebook if marketing consent given
    if (params?.marketing) {
      this.facebook?.trackCustom(name, params);
    }
  }

  /**
   * Track form submission
   */
  trackFormSubmission(formName: string, formData?: Record<string, any>): void {
    this.trackEvent('form_submission', {
      form_name: formName,
      ...formData,
    });

    // Track specific conversions
    if (formName === 'demo_request') {
      this.facebook?.track('Lead');
      this.linkedin?.track(import.meta.env.VITE_LINKEDIN_DEMO_CONVERSION_ID || '');
    } else if (formName === 'contact') {
      this.facebook?.track('Contact');
    } else if (formName === 'newsletter') {
      this.facebook?.track('Subscribe');
    }
  }

  /**
   * Track CTA click
   */
  trackCTAClick(ctaName: string, location: string): void {
    this.trackEvent('cta_click', {
      cta_name: ctaName,
      location: location,
    });
  }

  /**
   * Track button click
   */
  trackButtonClick(buttonName: string, destination?: string): void {
    this.trackEvent('button_click', {
      button_name: buttonName,
      destination: destination,
    });
  }

  /**
   * Track resource download
   */
  trackDownload(resourceName: string, resourceType: string): void {
    this.trackEvent('resource_download', {
      resource_name: resourceName,
      resource_type: resourceType,
    });

    this.facebook?.track('Lead'); // Downloads are lead indicators
  }

  /**
   * Track video play
   */
  trackVideoPlay(videoName: string, videoUrl?: string): void {
    this.trackEvent('video_play', {
      video_name: videoName,
      video_url: videoUrl,
    });
  }

  /**
   * Track video progress
   */
  trackVideoProgress(videoName: string, progress: number): void {
    // Only track at 25%, 50%, 75%, 100%
    if ([25, 50, 75, 100].includes(progress)) {
      this.trackEvent('video_progress', {
        video_name: videoName,
        progress: progress,
      });
    }
  }

  /**
   * Track scroll depth
   */
  trackScrollDepth(depth: number): void {
    // Only track at 25%, 50%, 75%, 100%
    if ([25, 50, 75, 100].includes(depth)) {
      this.trackEvent('scroll', {
        depth: depth,
      });
    }
  }

  /**
   * Track outbound link
   */
  trackOutboundLink(url: string, linkText?: string): void {
    this.trackEvent('outbound_link', {
      url: url,
      link_text: linkText,
    });
  }

  /**
   * Track search
   */
  trackSearch(searchTerm: string, results?: number): void {
    this.trackEvent('search', {
      search_term: searchTerm,
      results: results,
    });
  }

  /**
   * Track navigation click
   */
  trackNavigation(item: string, destination: string): void {
    this.trackEvent('navigation_click', {
      item: item,
      destination: destination,
    });
  }

  /**
   * Track pricing interaction
   */
  trackPricingInteraction(plan: string, action: 'view' | 'select' | 'compare'): void {
    this.trackEvent('pricing_interaction', {
      plan: plan,
      action: action,
    });

    if (action === 'select') {
      this.facebook?.track('AddToCart', { content_name: plan });
    }
  }

  /**
   * Track user signup (conversion)
   */
  trackSignup(method: string): void {
    this.trackEvent('sign_up', {
      method: method,
    });

    this.facebook?.track('CompleteRegistration');
    this.linkedin?.track(import.meta.env.VITE_LINKEDIN_SIGNUP_CONVERSION_ID || '');
  }

  /**
   * Set user properties
   */
  setUserProperties(properties: Record<string, any>): void {
    this.ga?.setUserProperties(properties);
    this.clarity?.set('user_properties', JSON.stringify(properties));
  }

  /**
   * Identify user
   */
  identifyUser(userId: string, email?: string): void {
    this.ga?.setUserId(userId);
    this.clarity?.identify(userId, undefined, undefined, email);
  }

  /**
   * Track exception
   */
  trackException(description: string, fatal = false): void {
    this.ga?.event('exception', {
      description: description,
      fatal: fatal,
    });
  }

  /**
   * Track timing
   */
  trackTiming(category: string, variable: string, value: number, label?: string): void {
    this.trackEvent('timing_complete', {
      name: variable,
      value: value,
      event_category: category,
      event_label: label,
    });
  }
}

// Export singleton instance
export const analyticsService = new AnalyticsService();

/**
 * Hook for React components
 */
export function useAnalytics() {
  return {
    trackPageView: (path?: string, title?: string) => analyticsService.trackPageView(path, title),
    trackEvent: (name: string, params?: Record<string, any>) => analyticsService.trackEvent(name, params),
    trackFormSubmission: (formName: string, data?: Record<string, any>) => analyticsService.trackFormSubmission(formName, data),
    trackCTAClick: (ctaName: string, location: string) => analyticsService.trackCTAClick(ctaName, location),
    trackButtonClick: (buttonName: string, destination?: string) => analyticsService.trackButtonClick(buttonName, destination),
    trackDownload: (resourceName: string, resourceType: string) => analyticsService.trackDownload(resourceName, resourceType),
    trackVideoPlay: (videoName: string, videoUrl?: string) => analyticsService.trackVideoPlay(videoName, videoUrl),
    trackVideoProgress: (videoName: string, progress: number) => analyticsService.trackVideoProgress(videoName, progress),
    trackScrollDepth: (depth: number) => analyticsService.trackScrollDepth(depth),
    trackOutboundLink: (url: string, linkText?: string) => analyticsService.trackOutboundLink(url, linkText),
    trackSearch: (searchTerm: string, results?: number) => analyticsService.trackSearch(searchTerm, results),
    trackNavigation: (item: string, destination: string) => analyticsService.trackNavigation(item, destination),
    trackPricingInteraction: (plan: string, action: 'view' | 'select' | 'compare') => analyticsService.trackPricingInteraction(plan, action),
    setUserProperties: (properties: Record<string, any>) => analyticsService.setUserProperties(properties),
    identifyUser: (userId: string, email?: string) => analyticsService.identifyUser(userId, email),
  };
}
