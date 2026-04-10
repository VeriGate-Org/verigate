import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { X, Cookie, Settings } from "lucide-react";

export function CookieConsent() {
  const [isVisible, setIsVisible] = useState(false);
  const [showPreferences, setShowPreferences] = useState(false);
  const [preferences, setPreferences] = useState({
    necessary: true, // Always required
    analytics: false,
    marketing: false,
    functional: false,
  });

  useEffect(() => {
    // Check if user has already made a choice
    const consent = localStorage.getItem("cookieConsent");
    if (!consent) {
      // Show banner after a short delay
      setTimeout(() => setIsVisible(true), 1000);
    } else {
      // Load saved preferences
      try {
        const savedPreferences = JSON.parse(consent);
        setPreferences(savedPreferences);
        // Initialize analytics if accepted
        if (savedPreferences.analytics) {
          initializeAnalytics();
        }
      } catch (error) {
        console.error("Error loading cookie preferences:", error);
      }
    }
  }, []);

  const initializeAnalytics = () => {
    // Initialize analytics service
    if (typeof window !== 'undefined') {
      import('../lib/analytics').then(({ analyticsService }) => {
        analyticsService.init({
          analytics: preferences.analytics,
          marketing: preferences.marketing,
        });
        console.log("✅ Analytics initialized with consent");
      });
    }
  };

  const acceptAll = () => {
    const allAccepted = {
      necessary: true,
      analytics: true,
      marketing: true,
      functional: true,
    };
    setPreferences(allAccepted);
    savePreferences(allAccepted);
    initializeAnalytics();
    setIsVisible(false);
  };

  const acceptNecessary = () => {
    const necessaryOnly = {
      necessary: true,
      analytics: false,
      marketing: false,
      functional: false,
    };
    setPreferences(necessaryOnly);
    savePreferences(necessaryOnly);
    setIsVisible(false);
  };

  const saveCustomPreferences = () => {
    savePreferences(preferences);
    if (preferences.analytics) {
      initializeAnalytics();
    }
    setShowPreferences(false);
    setIsVisible(false);
  };

  const savePreferences = (prefs: typeof preferences) => {
    localStorage.setItem("cookieConsent", JSON.stringify(prefs));
    localStorage.setItem("cookieConsentDate", new Date().toISOString());
  };

  const togglePreference = (key: keyof typeof preferences) => {
    if (key === "necessary") return; // Can't disable necessary cookies
    setPreferences((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));
  };

  if (!isVisible) return null;

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/20 backdrop-blur-sm z-40" />

      {/* Cookie Banner */}
      <div className="fixed bottom-0 left-0 right-0 z-50 p-4 animate-in slide-in-from-bottom duration-500">
        <Card className="max-w-3xl mx-auto border-border shadow-2xl">
          <CardContent className="p-6">
            {!showPreferences ? (
              // Main Banner
              <div>
                <div className="flex items-start gap-4">
                  <div className="p-3 rounded-lg bg-accent/10 flex-shrink-0">
                    <Cookie className="w-6 h-6 text-accent" />
                  </div>
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold mb-2">We Value Your Privacy</h3>
                    <p className="text-sm text-muted-foreground mb-4">
                      We use cookies to enhance your browsing experience, serve personalised content, and analyse our traffic in accordance with POPIA.
                      By clicking "Accept All", you consent to our use of cookies. You can customise your preferences or decline
                      non-essential cookies. Read our <a href="/cookie-policy" className="text-accent hover:underline">Cookie Policy</a> for more information.
                    </p>
                    <div className="flex flex-col sm:flex-row gap-3">
                      <Button onClick={acceptAll} size="sm">
                        Accept All Cookies
                      </Button>
                      <Button onClick={acceptNecessary} variant="outline" size="sm">
                        Necessary Only
                      </Button>
                      <Button
                        onClick={() => setShowPreferences(true)}
                        variant="ghost"
                        size="sm"
                      >
                        <Settings className="w-4 h-4 mr-2" />
                        Customize
                      </Button>
                      <a
                        href="/cookie-policy"
                        className="text-sm text-accent hover:underline self-center"
                      >
                        Cookie Policy
                      </a>
                    </div>
                  </div>
                  <button
                    onClick={acceptNecessary}
                    className="p-2 hover:bg-secondary rounded-lg transition-colors flex-shrink-0"
                    aria-label="Close banner"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>
              </div>
            ) : (
              // Preferences Panel
              <div>
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-lg font-semibold">Cookie Preferences</h3>
                  <button
                    onClick={() => setShowPreferences(false)}
                    className="p-2 hover:bg-secondary rounded-lg transition-colors"
                    aria-label="Close preferences"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>

                <div className="space-y-4 mb-6">
                  {/* Necessary Cookies */}
                  <div className="flex items-start justify-between p-4 border border-border rounded-lg bg-secondary/20">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="font-semibold">Necessary Cookies</h4>
                        <span className="text-xs px-2 py-1 bg-accent/10 text-accent rounded">Required</span>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        Essential for the website to function properly. These cannot be disabled.
                      </p>
                    </div>
                    <div className="ml-4">
                      <div className="w-11 h-6 bg-accent rounded-full flex items-center justify-end px-1">
                        <div className="w-4 h-4 bg-white rounded-full" />
                      </div>
                    </div>
                  </div>

                  {/* Analytics Cookies */}
                  <div className="flex items-start justify-between p-4 border border-border rounded-lg">
                    <div className="flex-1">
                      <h4 className="font-semibold mb-1">Analytics Cookies</h4>
                      <p className="text-sm text-muted-foreground">
                        Help us understand how visitors interact with our website to improve user experience.
                      </p>
                    </div>
                    <button
                      onClick={() => togglePreference("analytics")}
                      className="ml-4"
                      aria-label="Toggle analytics cookies"
                    >
                      <div
                        className={`w-11 h-6 rounded-full flex items-center px-1 transition-colors ${
                          preferences.analytics ? "bg-accent justify-end" : "bg-border justify-start"
                        }`}
                      >
                        <div className="w-4 h-4 bg-white rounded-full" />
                      </div>
                    </button>
                  </div>

                  {/* Marketing Cookies */}
                  <div className="flex items-start justify-between p-4 border border-border rounded-lg">
                    <div className="flex-1">
                      <h4 className="font-semibold mb-1">Marketing Cookies</h4>
                      <p className="text-sm text-muted-foreground">
                        Used to track visitors across websites to display relevant ads and campaigns.
                      </p>
                    </div>
                    <button
                      onClick={() => togglePreference("marketing")}
                      className="ml-4"
                      aria-label="Toggle marketing cookies"
                    >
                      <div
                        className={`w-11 h-6 rounded-full flex items-center px-1 transition-colors ${
                          preferences.marketing ? "bg-accent justify-end" : "bg-border justify-start"
                        }`}
                      >
                        <div className="w-4 h-4 bg-white rounded-full" />
                      </div>
                    </button>
                  </div>

                  {/* Functional Cookies */}
                  <div className="flex items-start justify-between p-4 border border-border rounded-lg">
                    <div className="flex-1">
                      <h4 className="font-semibold mb-1">Functional Cookies</h4>
                      <p className="text-sm text-muted-foreground">
                        Enable enhanced functionality and personalization, such as videos and live chat.
                      </p>
                    </div>
                    <button
                      onClick={() => togglePreference("functional")}
                      className="ml-4"
                      aria-label="Toggle functional cookies"
                    >
                      <div
                        className={`w-11 h-6 rounded-full flex items-center px-1 transition-colors ${
                          preferences.functional ? "bg-accent justify-end" : "bg-border justify-start"
                        }`}
                      >
                        <div className="w-4 h-4 bg-white rounded-full" />
                      </div>
                    </button>
                  </div>
                </div>

                <div className="flex flex-col sm:flex-row gap-3">
                  <Button onClick={saveCustomPreferences} className="flex-1">
                    Save Preferences
                  </Button>
                  <Button onClick={acceptAll} variant="outline" className="flex-1">
                    Accept All
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
