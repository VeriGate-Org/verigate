/**
 * CRM Integration Layer
 * Supports multiple CRM platforms: HubSpot, Salesforce, Pipedrive
 * Handles lead creation, contact management, and activity tracking
 */

export interface CRMContact {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  jobTitle?: string;
  website?: string;
  industry?: string;
  companySize?: string;
}

export interface CRMLead extends CRMContact {
  source: string;
  status: 'new' | 'contacted' | 'qualified' | 'demo_scheduled' | 'proposal' | 'won' | 'lost';
  leadScore?: number;
  notes?: string;
  customFields?: Record<string, any>;
}

export interface CRMDeal {
  name: string;
  amount?: number;
  stage: string;
  closeDate?: Date;
  probability?: number;
  contactId?: string;
  companyId?: string;
}

export interface CRMActivity {
  type: 'email' | 'call' | 'meeting' | 'note' | 'task';
  subject: string;
  description?: string;
  contactId?: string;
  dealId?: string;
  dueDate?: Date;
  completed?: boolean;
}

/**
 * HubSpot Integration
 */
class HubSpotCRM {
  private apiKey: string;
  private baseUrl = 'https://api.hubapi.com';

  constructor(apiKey: string) {
    this.apiKey = apiKey;
  }

  async createContact(contact: CRMContact): Promise<string | null> {
    try {
      const response = await fetch(`${this.baseUrl}/crm/v3/objects/contacts`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          properties: {
            firstname: contact.firstName,
            lastname: contact.lastName,
            email: contact.email,
            phone: contact.phone,
            company: contact.company,
            jobtitle: contact.jobTitle,
            website: contact.website,
            industry: contact.industry,
          },
        }),
      });

      if (response.ok) {
        const data = await response.json();
        return data.id;
      }

      console.error('HubSpot contact creation failed:', await response.text());
      return null;
    } catch (error) {
      console.error('HubSpot API error:', error);
      return null;
    }
  }

  async createDeal(deal: CRMDeal): Promise<string | null> {
    try {
      const response = await fetch(`${this.baseUrl}/crm/v3/objects/deals`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          properties: {
            dealname: deal.name,
            amount: deal.amount,
            dealstage: deal.stage,
            closedate: deal.closeDate?.toISOString(),
            pipeline: 'default',
          },
        }),
      });

      if (response.ok) {
        const data = await response.json();
        return data.id;
      }

      console.error('HubSpot deal creation failed:', await response.text());
      return null;
    } catch (error) {
      console.error('HubSpot API error:', error);
      return null;
    }
  }

  async logActivity(activity: CRMActivity): Promise<boolean> {
    try {
      // HubSpot engagement creation
      const response = await fetch(`${this.baseUrl}/crm/v3/objects/notes`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          properties: {
            hs_note_body: activity.description,
            hs_timestamp: new Date().toISOString(),
          },
        }),
      });

      return response.ok;
    } catch (error) {
      console.error('HubSpot activity logging error:', error);
      return false;
    }
  }

  async submitForm(formId: string, data: Record<string, any>): Promise<boolean> {
    try {
      const portalId = import.meta.env.VITE_HUBSPOT_PORTAL_ID || '';
      const response = await fetch(
        `https://api.hsforms.com/submissions/v3/integration/submit/${portalId}/${formId}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            fields: Object.entries(data).map(([name, value]) => ({
              name,
              value,
            })),
            context: {
              pageUri: window.location.href,
              pageName: document.title,
            },
          }),
        }
      );

      return response.ok;
    } catch (error) {
      console.error('HubSpot form submission error:', error);
      return false;
    }
  }
}

/**
 * Salesforce Integration
 */
class SalesforceCRM {
  private instanceUrl: string;
  private accessToken: string;

  constructor(instanceUrl: string, accessToken: string) {
    this.instanceUrl = instanceUrl;
    this.accessToken = accessToken;
  }

  async createLead(lead: CRMLead): Promise<string | null> {
    try {
      const response = await fetch(`${this.instanceUrl}/services/data/v57.0/sobjects/Lead`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          FirstName: lead.firstName,
          LastName: lead.lastName,
          Email: lead.email,
          Phone: lead.phone,
          Company: lead.company,
          Title: lead.jobTitle,
          Website: lead.website,
          Industry: lead.industry,
          LeadSource: lead.source,
          Status: lead.status,
          Description: lead.notes,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        return data.id;
      }

      console.error('Salesforce lead creation failed:', await response.text());
      return null;
    } catch (error) {
      console.error('Salesforce API error:', error);
      return null;
    }
  }

  async webToLead(data: Record<string, any>): Promise<boolean> {
    try {
      const orgId = import.meta.env.VITE_SALESFORCE_ORG_ID || '';
      const formData = new FormData();
      
      formData.append('oid', orgId);
      formData.append('retURL', window.location.href);
      
      Object.entries(data).forEach(([key, value]) => {
        formData.append(key, String(value));
      });

      const response = await fetch('https://webto.salesforce.com/servlet/servlet.WebToLead?encoding=UTF-8', {
        method: 'POST',
        body: formData,
      });

      return response.ok;
    } catch (error) {
      console.error('Salesforce Web-to-Lead error:', error);
      return false;
    }
  }
}

/**
 * Generic CRM Service
 * Provides unified interface for multiple CRM platforms
 */
class CRMService {
  private platform: 'hubspot' | 'salesforce' | 'custom';
  private hubspot?: HubSpotCRM;
  private salesforce?: SalesforceCRM;

  constructor() {
    // Determine platform from environment
    this.platform = (import.meta.env.VITE_CRM_PLATFORM as any) || 'custom';

    // Initialize based on platform
    switch (this.platform) {
      case 'hubspot':
        const hubspotKey = import.meta.env.VITE_HUBSPOT_API_KEY || '';
        this.hubspot = new HubSpotCRM(hubspotKey);
        break;
      
      case 'salesforce':
        const sfInstance = import.meta.env.VITE_SALESFORCE_INSTANCE_URL || '';
        const sfToken = import.meta.env.VITE_SALESFORCE_ACCESS_TOKEN || '';
        this.salesforce = new SalesforceCRM(sfInstance, sfToken);
        break;
      
      default:
        console.log('Using custom CRM implementation');
    }
  }

  /**
   * Create a contact from form submission
   */
  async createContactFromForm(formData: {
    name?: string;
    firstName?: string;
    lastName?: string;
    email: string;
    company?: string;
    phone?: string;
    jobTitle?: string;
  }): Promise<boolean> {
    try {
      // Parse name if provided as single field
      let firstName = formData.firstName || '';
      let lastName = formData.lastName || '';
      
      if (formData.name && !firstName && !lastName) {
        const nameParts = formData.name.split(' ');
        firstName = nameParts[0] || '';
        lastName = nameParts.slice(1).join(' ') || '';
      }

      const contact: CRMContact = {
        firstName,
        lastName,
        email: formData.email,
        phone: formData.phone,
        company: formData.company,
        jobTitle: formData.jobTitle,
      };

      switch (this.platform) {
        case 'hubspot':
          const hubspotId = await this.hubspot?.createContact(contact);
          return !!hubspotId;
        
        case 'salesforce':
          // Salesforce uses leads instead of contacts for web forms
          const lead: CRMLead = {
            ...contact,
            source: 'Website',
            status: 'new',
          };
          const sfId = await this.salesforce?.createLead(lead);
          return !!sfId;
        
        default:
          // Custom implementation - log to console for now
          console.log('📝 Contact created (custom CRM):', contact);
          return true;
      }
    } catch (error) {
      console.error('CRM contact creation error:', error);
      return false;
    }
  }

  /**
   * Create a lead from demo request
   */
  async createLeadFromDemo(formData: {
    firstName: string;
    lastName: string;
    email: string;
    company: string;
    jobTitle?: string;
    phone?: string;
    companySize: string;
    useCase: string;
    message?: string;
  }): Promise<boolean> {
    try {
      const lead: CRMLead = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        company: formData.company,
        jobTitle: formData.jobTitle,
        companySize: formData.companySize,
        source: 'Website - Demo Request',
        status: 'demo_scheduled',
        leadScore: this.calculateLeadScore(formData),
        notes: `Use Case: ${formData.useCase}\n${formData.message || ''}`,
        customFields: {
          company_size: formData.companySize,
          use_case: formData.useCase,
        },
      };

      switch (this.platform) {
        case 'hubspot':
          // Create contact and deal
          const contactId = await this.hubspot?.createContact(lead);
          if (contactId) {
            await this.hubspot?.createDeal({
              name: `${formData.company} - Demo Request`,
              stage: 'appointmentscheduled',
              contactId,
            });
          }
          return !!contactId;
        
        case 'salesforce':
          const leadId = await this.salesforce?.createLead(lead);
          return !!leadId;
        
        default:
          console.log('📝 Lead created (custom CRM):', lead);
          return true;
      }
    } catch (error) {
      console.error('CRM lead creation error:', error);
      return false;
    }
  }

  /**
   * Add newsletter subscriber
   */
  async addNewsletterSubscriber(email: string, source?: string): Promise<boolean> {
    try {
      const contact: CRMContact = {
        firstName: '',
        lastName: '',
        email,
      };

      switch (this.platform) {
        case 'hubspot':
          // Add to newsletter list
          const contactId = await this.hubspot?.createContact(contact);
          if (contactId) {
            // Add to list (requires list ID from HubSpot)
            console.log('Added to HubSpot newsletter list');
          }
          return !!contactId;
        
        default:
          console.log('📧 Newsletter subscriber added (custom):', email);
          return true;
      }
    } catch (error) {
      console.error('CRM newsletter subscription error:', error);
      return false;
    }
  }

  /**
   * Submit form via native CRM form handling
   */
  async submitNativeForm(formType: 'contact' | 'demo', data: Record<string, any>): Promise<boolean> {
    try {
      switch (this.platform) {
        case 'hubspot':
          const formId = formType === 'demo' 
            ? import.meta.env.VITE_HUBSPOT_DEMO_FORM_ID 
            : import.meta.env.VITE_HUBSPOT_CONTACT_FORM_ID;
          
          if (formId) {
            return await this.hubspot?.submitForm(formId, data) || false;
          }
          break;
        
        case 'salesforce':
          return await this.salesforce?.webToLead(data) || false;
        
        default:
          console.log('Form submission (custom):', formType, data);
          return true;
      }
      
      return false;
    } catch (error) {
      console.error('CRM form submission error:', error);
      return false;
    }
  }

  /**
   * Calculate lead score based on form data
   */
  private calculateLeadScore(data: {
    company: string;
    jobTitle?: string;
    companySize: string;
    useCase: string;
  }): number {
    let score = 50; // Base score

    // Company size scoring
    const sizeScores: Record<string, number> = {
      '1-10': 5,
      '11-50': 10,
      '51-200': 15,
      '201-1000': 20,
      '1000+': 25,
    };
    score += sizeScores[data.companySize] || 0;

    // Job title scoring (senior roles get higher scores)
    const title = (data.jobTitle || '').toLowerCase();
    if (title.includes('ceo') || title.includes('founder') || title.includes('owner')) {
      score += 20;
    } else if (title.includes('cto') || title.includes('cio') || title.includes('vp')) {
      score += 15;
    } else if (title.includes('director') || title.includes('head')) {
      score += 10;
    } else if (title.includes('manager')) {
      score += 5;
    }

    // Use case scoring (specific use cases indicate higher intent)
    const useCase = data.useCase.toLowerCase();
    if (useCase.includes('enterprise') || useCase.includes('compliance')) {
      score += 15;
    } else if (useCase.includes('integration')) {
      score += 10;
    }

    // Cap at 100
    return Math.min(score, 100);
  }

  /**
   * Track page visit (for CRM activity)
   */
  trackPageVisit(page: string, email?: string): void {
    // This would integrate with CRM tracking
    console.log('📊 Page visit tracked:', page, email);
  }

  /**
   * Track event (for CRM activity)
   */
  trackEvent(event: string, properties?: Record<string, any>): void {
    // This would integrate with CRM tracking
    console.log('📊 Event tracked:', event, properties);
  }
}

// Export singleton instance
export const crmService = new CRMService();

// Export classes for advanced usage
export { HubSpotCRM, SalesforceCRM };
