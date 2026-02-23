/**
 * Live Chat Widget Component
 * Supports multiple chat providers: Tawk.to, Intercom, Crisp
 * Provides unified interface with offline fallback
 */

import { useEffect, useState } from 'react';
import { X, MessageCircle, Send } from 'lucide-react';

// Extend Window interface for chat providers
declare global {
  interface Window {
    Tawk_API?: any;
    Tawk_LoadStart?: Date;
    Intercom?: (...args: any[]) => void;
    intercomSettings?: any;
    $crisp?: any;
    CRISP_WEBSITE_ID?: string;
  }
}

export type ChatProvider = 'tawk' | 'intercom' | 'crisp' | 'custom';

interface LiveChatProps {
  provider?: ChatProvider;
  offlineForm?: boolean;
}

/**
 * Tawk.to Integration
 */
function initializeTawk(propertyId: string, widgetId: string): void {
  if (window.Tawk_API) return;

  window.Tawk_API = window.Tawk_API || {};
  window.Tawk_LoadStart = new Date();

  const script = document.createElement('script');
  script.async = true;
  script.src = `https://embed.tawk.to/${propertyId}/${widgetId}`;
  script.charset = 'UTF-8';
  script.setAttribute('crossorigin', '*');
  
  const firstScript = document.getElementsByTagName('script')[0];
  firstScript.parentNode?.insertBefore(script, firstScript);

  console.log('✅ Tawk.to chat initialized');
}

/**
 * Intercom Integration
 */
function initializeIntercom(appId: string): void {
  if (window.Intercom) return;

  (function() {
    const w = window as any;
    const ic = w.Intercom;
    
    if (typeof ic === 'function') {
      ic('reattach_activator');
      ic('update', w.intercomSettings);
    } else {
      const d = document;
      const i: any = function() {
        i.c(arguments);
      };
      i.q = [];
      i.c = function(args: any) {
        i.q.push(args);
      };
      w.Intercom = i;
      
      const l = function() {
        const s = d.createElement('script');
        s.type = 'text/javascript';
        s.async = true;
        s.src = `https://widget.intercom.io/widget/${appId}`;
        const x = d.getElementsByTagName('script')[0];
        x.parentNode?.insertBefore(s, x);
      };
      
      if (document.readyState === 'complete') {
        l();
      } else if (w.attachEvent) {
        w.attachEvent('onload', l);
      } else {
        w.addEventListener('load', l, false);
      }
    }
  })();

  window.intercomSettings = {
    app_id: appId,
    alignment: 'right',
    horizontal_padding: 20,
    vertical_padding: 20,
  };

  window.Intercom!('boot', { app_id: appId });
  console.log('✅ Intercom chat initialized');
}

/**
 * Crisp Integration
 */
function initializeCrisp(websiteId: string): void {
  if (window.$crisp) return;

  window.$crisp = [];
  window.CRISP_WEBSITE_ID = websiteId;

  const script = document.createElement('script');
  script.src = 'https://client.crisp.chat/l.js';
  script.async = true;
  
  document.getElementsByTagName('head')[0].appendChild(script);
  console.log('✅ Crisp chat initialized');
}

/**
 * Custom Chat Widget (Offline Fallback)
 */
function CustomChatWidget({ onClose }: { onClose: () => void }) {
  const [message, setMessage] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      // Send offline message (implement API endpoint)
      console.log('Offline message:', { name, email, message });
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setIsSubmitted(true);
      setTimeout(() => {
        onClose();
      }, 2000);
    } catch (error) {
      console.error('Failed to send message:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed bottom-4 right-4 z-50 w-80 sm:w-96 bg-white rounded-lg shadow-2xl border border-gray-200 flex flex-col max-h-[600px]">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-700 text-white p-4 rounded-t-lg flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
            <MessageCircle className="w-5 h-5" />
          </div>
          <div>
            <h3 className="font-semibold">VeriGate Support</h3>
            <p className="text-xs text-purple-100">We're here to help</p>
          </div>
        </div>
        <button
          onClick={onClose}
          className="hover:bg-white/20 rounded-full p-1 transition-colors"
          aria-label="Close chat"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Body */}
      <div className="flex-1 p-4 overflow-y-auto">
        {isSubmitted ? (
          <div className="text-center py-8">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h4 className="font-semibold text-lg mb-2">Message Sent!</h4>
            <p className="text-gray-600 text-sm">
              We'll get back to you as soon as possible.
            </p>
          </div>
        ) : (
          <div>
            <div className="bg-purple-50 rounded-lg p-3 mb-4">
              <p className="text-sm text-gray-700">
                👋 Hi there! Our team is currently offline. Leave us a message and we'll get back to you soon.
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3">
              <div>
                <label htmlFor="chat-name" className="block text-sm font-medium text-gray-700 mb-1">
                  Name *
                </label>
                <input
                  id="chat-name"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent text-sm"
                  placeholder="John Doe"
                />
              </div>

              <div>
                <label htmlFor="chat-email" className="block text-sm font-medium text-gray-700 mb-1">
                  Email *
                </label>
                <input
                  id="chat-email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent text-sm"
                  placeholder="john@company.com"
                />
              </div>

              <div>
                <label htmlFor="chat-message" className="block text-sm font-medium text-gray-700 mb-1">
                  Message *
                </label>
                <textarea
                  id="chat-message"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  required
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent text-sm resize-none"
                  placeholder="How can we help you?"
                />
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-purple-600 hover:bg-purple-700 text-white py-2 px-4 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {isSubmitting ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Sending...
                  </>
                ) : (
                  <>
                    <Send className="w-4 h-4" />
                    Send Message
                  </>
                )}
              </button>
            </form>
          </div>
        )}
      </div>

      {/* Footer */}
      <div className="p-3 border-t border-gray-200 bg-gray-50 rounded-b-lg">
        <p className="text-xs text-gray-500 text-center">
          Typical reply time: Within 24 hours
        </p>
      </div>
    </div>
  );
}

/**
 * Main Live Chat Component
 */
export default function LiveChat({ provider = 'custom', offlineForm = true }: LiveChatProps) {
  const [showCustomChat, setShowCustomChat] = useState(false);
  const [showButton, setShowButton] = useState(true);

  useEffect(() => {
    // Get provider from environment or use prop
    const envProvider = (import.meta.env.VITE_CHAT_PROVIDER as ChatProvider) || provider;

    // Initialize based on provider
    switch (envProvider) {
      case 'tawk':
        const tawkPropertyId = import.meta.env.VITE_TAWK_PROPERTY_ID;
        const tawkWidgetId = import.meta.env.VITE_TAWK_WIDGET_ID;
        if (tawkPropertyId && tawkWidgetId) {
          initializeTawk(tawkPropertyId, tawkWidgetId);
          setShowButton(false); // Tawk.to provides its own button
        }
        break;

      case 'intercom':
        const intercomAppId = import.meta.env.VITE_INTERCOM_APP_ID;
        if (intercomAppId) {
          initializeIntercom(intercomAppId);
          setShowButton(false); // Intercom provides its own button
        }
        break;

      case 'crisp':
        const crispWebsiteId = import.meta.env.VITE_CRISP_WEBSITE_ID;
        if (crispWebsiteId) {
          initializeCrisp(crispWebsiteId);
          setShowButton(false); // Crisp provides its own button
        }
        break;

      case 'custom':
      default:
        // Use custom widget
        setShowButton(true);
        break;
    }
  }, [provider]);

  // Only show custom widget if enabled
  if (!offlineForm && provider === 'custom') {
    return null;
  }

  return (
    <>
      {/* Custom Chat Button */}
      {showButton && (
        <button
          onClick={() => setShowCustomChat(true)}
          className="fixed bottom-6 right-6 z-40 bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 text-white rounded-full p-4 shadow-lg transition-all duration-300 hover:scale-110 group"
          aria-label="Open chat"
        >
          <MessageCircle className="w-6 h-6" />
          {/* Notification badge (optional) */}
          <span className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse" />
          
          {/* Tooltip */}
          <span className="absolute right-full mr-3 top-1/2 -translate-y-1/2 bg-gray-900 text-white text-sm px-3 py-2 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap pointer-events-none">
            Chat with us
          </span>
        </button>
      )}

      {/* Custom Chat Widget */}
      {showCustomChat && (
        <CustomChatWidget onClose={() => setShowCustomChat(false)} />
      )}
    </>
  );
}

/**
 * Chat API Methods (for programmatic control)
 */
export const chatAPI = {
  /**
   * Open chat programmatically
   */
  open: () => {
    if (window.Tawk_API) {
      window.Tawk_API.maximize();
    } else if (window.Intercom) {
      window.Intercom('show');
    } else if (window.$crisp) {
      window.$crisp.push(['do', 'chat:open']);
    }
  },

  /**
   * Close chat programmatically
   */
  close: () => {
    if (window.Tawk_API) {
      window.Tawk_API.minimize();
    } else if (window.Intercom) {
      window.Intercom('hide');
    } else if (window.$crisp) {
      window.$crisp.push(['do', 'chat:close']);
    }
  },

  /**
   * Set visitor info
   */
  setVisitor: (name: string, email: string) => {
    if (window.Tawk_API) {
      window.Tawk_API.setAttributes({
        name: name,
        email: email,
      });
    } else if (window.Intercom) {
      window.Intercom('update', {
        name: name,
        email: email,
      });
    } else if (window.$crisp) {
      window.$crisp.push(['set', 'user:email', email]);
      window.$crisp.push(['set', 'user:nickname', name]);
    }
  },

  /**
   * Show specific message
   */
  showMessage: (message: string) => {
    if (window.Tawk_API) {
      window.Tawk_API.addEvent('bot_message', { message }, () => {
        window.Tawk_API.maximize();
      });
    } else if (window.Intercom) {
      window.Intercom('showNewMessage', message);
    } else if (window.$crisp) {
      window.$crisp.push(['do', 'message:show', ['text', message]]);
    }
  },
};
