/**
 * Email Service Integration
 * Handles email sending for contact forms, demo requests, and newsletters
 * Supports multiple providers: SendGrid, AWS SES, or custom SMTP
 */

export interface EmailData {
  to: string | string[];
  from: string;
  subject: string;
  text?: string;
  html?: string;
  cc?: string[];
  bcc?: string[];
  replyTo?: string;
}

export interface ContactEmailData {
  name: string;
  email: string;
  company?: string;
  phone?: string;
  message: string;
  consent: boolean;
}

export interface DemoRequestEmailData {
  firstName: string;
  lastName: string;
  email: string;
  company: string;
  jobTitle?: string;
  phone?: string;
  companySize: string;
  useCase: string;
  message?: string;
  consent: boolean;
}

export interface NewsletterEmailData {
  email: string;
  consent: boolean;
  source?: string;
}

/**
 * Email Templates
 */
const EMAIL_TEMPLATES = {
  // Contact form auto-responder
  contactAutoResponder: (name: string) => ({
    subject: 'Thank you for contacting VeriGate',
    html: `
      <!DOCTYPE html>
      <html>
      <head>
        <style>
          body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
          .container { max-width: 600px; margin: 0 auto; padding: 20px; }
          .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
          .content { padding: 30px; background: #f9f9f9; }
          .footer { padding: 20px; text-align: center; color: #666; font-size: 12px; }
          .button { display: inline-block; padding: 12px 24px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>VeriGate</h1>
          </div>
          <div class="content">
            <h2>Thank you for reaching out!</h2>
            <p>Hi ${name},</p>
            <p>We've received your message and our team will get back to you within 24 hours.</p>
            <p>In the meantime, feel free to explore our resources:</p>
            <a href="https://verigate.co.za/resources" class="button">Browse Resources</a>
            <p>If you have urgent questions, you can also reach us via:</p>
            <ul>
              <li>Email: support@verigate.com</li>
              <li>Phone: +1 (555) 123-4567</li>
              <li>Live Chat: Available on our website</li>
            </ul>
          </div>
          <div class="footer">
            <p>&copy; 2026 VeriGate. All rights reserved.</p>
            <p>Enterprise-Grade Identity Verification</p>
          </div>
        </div>
      </body>
      </html>
    `,
    text: `Hi ${name},\n\nThank you for contacting VeriGate. We've received your message and will respond within 24 hours.\n\nBest regards,\nThe VeriGate Team`
  }),

  // Demo request auto-responder
  demoAutoResponder: (firstName: string) => ({
    subject: 'Your VeriGate Demo Request - Next Steps',
    html: `
      <!DOCTYPE html>
      <html>
      <head>
        <style>
          body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
          .container { max-width: 600px; margin: 0 auto; padding: 20px; }
          .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
          .content { padding: 30px; background: #f9f9f9; }
          .footer { padding: 20px; text-align: center; color: #666; font-size: 12px; }
          .button { display: inline-block; padding: 12px 24px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
          .checklist { background: white; padding: 20px; border-left: 4px solid #667eea; margin: 20px 0; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>🎉 Demo Request Received!</h1>
          </div>
          <div class="content">
            <h2>Welcome, ${firstName}!</h2>
            <p>Thank you for requesting a demo of VeriGate. Our sales team will contact you shortly to schedule your personalized demonstration.</p>
            
            <div class="checklist">
              <h3>What happens next?</h3>
              <ol>
                <li>Our team will review your request (usually within 2 hours)</li>
                <li>We'll send you a calendar invite with available time slots</li>
                <li>Before the demo, we'll send you a brief questionnaire</li>
                <li>We'll prepare a customized demo based on your use case</li>
              </ol>
            </div>

            <p>To make the most of your demo, here are some resources:</p>
            <a href="https://verigate.co.za/integrations" class="button">View Documentation</a>
            
            <p><strong>Typical demo agenda:</strong></p>
            <ul>
              <li>Product overview (10 minutes)</li>
              <li>Live demonstration (20 minutes)</li>
              <li>Your specific use case walkthrough (15 minutes)</li>
              <li>Q&A and next steps (15 minutes)</li>
            </ul>
          </div>
          <div class="footer">
            <p>&copy; 2026 VeriGate. All rights reserved.</p>
            <p>Questions? Reply to this email or call us at +1 (555) 123-4567</p>
          </div>
        </div>
      </body>
      </html>
    `,
    text: `Hi ${firstName},\n\nThank you for requesting a VeriGate demo! Our sales team will contact you within 2 hours to schedule your personalized demonstration.\n\nBest regards,\nThe VeriGate Sales Team`
  }),

  // Newsletter welcome
  newsletterWelcome: () => ({
    subject: 'Welcome to VeriGate Insights',
    html: `
      <!DOCTYPE html>
      <html>
      <head>
        <style>
          body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
          .container { max-width: 600px; margin: 0 auto; padding: 20px; }
          .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
          .content { padding: 30px; background: #f9f9f9; }
          .footer { padding: 20px; text-align: center; color: #666; font-size: 12px; }
          .unsubscribe { margin-top: 20px; font-size: 11px; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>📧 Welcome to VeriGate Insights!</h1>
          </div>
          <div class="content">
            <h2>You're all set!</h2>
            <p>Thank you for subscribing to VeriGate Insights. You'll now receive:</p>
            <ul>
              <li>Industry trends and compliance updates</li>
              <li>Product announcements and feature releases</li>
              <li>Best practices and case studies</li>
              <li>Exclusive webinar invitations</li>
            </ul>
            <p>We publish new content weekly, so stay tuned!</p>
          </div>
          <div class="footer">
            <p>&copy; 2026 VeriGate. All rights reserved.</p>
            <div class="unsubscribe">
              <a href="{{unsubscribe_link}}">Unsubscribe</a> | <a href="{{preferences_link}}">Update Preferences</a>
            </div>
          </div>
        </div>
      </body>
      </html>
    `,
    text: `Welcome to VeriGate Insights!\n\nThank you for subscribing. You'll receive industry updates, product news, and exclusive content weekly.\n\nTo unsubscribe, visit: {{unsubscribe_link}}`
  }),

  // Internal notification for contact form
  contactNotification: (data: ContactEmailData) => ({
    subject: `New Contact Form Submission - ${data.name}`,
    html: `
      <!DOCTYPE html>
      <html>
      <body style="font-family: Arial, sans-serif;">
        <h2>New Contact Form Submission</h2>
        <table style="width: 100%; border-collapse: collapse;">
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Name:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.name}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Email:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.email}</td>
          </tr>
          ${data.company ? `
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Company:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.company}</td>
          </tr>
          ` : ''}
          ${data.phone ? `
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Phone:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.phone}</td>
          </tr>
          ` : ''}
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Message:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.message.replace(/\n/g, '<br>')}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Consent:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.consent ? 'Yes' : 'No'}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Submitted:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${new Date().toLocaleString()}</td>
          </tr>
        </table>
      </body>
      </html>
    `,
    text: `New Contact Form Submission\n\nName: ${data.name}\nEmail: ${data.email}\n${data.company ? `Company: ${data.company}\n` : ''}${data.phone ? `Phone: ${data.phone}\n` : ''}Message: ${data.message}\nSubmitted: ${new Date().toLocaleString()}`
  }),

  // Internal notification for demo request
  demoNotification: (data: DemoRequestEmailData) => ({
    subject: `🎯 New Demo Request - ${data.company}`,
    html: `
      <!DOCTYPE html>
      <html>
      <body style="font-family: Arial, sans-serif;">
        <h2>🎯 New Demo Request</h2>
        <table style="width: 100%; border-collapse: collapse;">
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Name:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.firstName} ${data.lastName}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Email:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.email}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Company:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.company}</td>
          </tr>
          ${data.jobTitle ? `
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Job Title:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.jobTitle}</td>
          </tr>
          ` : ''}
          ${data.phone ? `
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Phone:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.phone}</td>
          </tr>
          ` : ''}
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Company Size:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.companySize}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Use Case:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.useCase}</td>
          </tr>
          ${data.message ? `
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Additional Info:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${data.message.replace(/\n/g, '<br>')}</td>
          </tr>
          ` : ''}
          <tr>
            <td style="padding: 8px; border: 1px solid #ddd;"><strong>Submitted:</strong></td>
            <td style="padding: 8px; border: 1px solid #ddd;">${new Date().toLocaleString()}</td>
          </tr>
        </table>
        <p style="margin-top: 20px; padding: 15px; background: #fff3cd; border-left: 4px solid #ffc107;">
          <strong>⏰ Action Required:</strong> Please reach out to this lead within 2 hours.
        </p>
      </body>
      </html>
    `,
    text: `New Demo Request\n\nName: ${data.firstName} ${data.lastName}\nEmail: ${data.email}\nCompany: ${data.company}\n${data.jobTitle ? `Job Title: ${data.jobTitle}\n` : ''}${data.phone ? `Phone: ${data.phone}\n` : ''}Company Size: ${data.companySize}\nUse Case: ${data.useCase}\n${data.message ? `Additional Info: ${data.message}\n` : ''}Submitted: ${new Date().toLocaleString()}\n\nACTION REQUIRED: Please reach out within 2 hours.`
  })
};

/**
 * Email Service Configuration
 * This is a mock implementation. In production, integrate with:
 * - SendGrid API
 * - AWS SES
 * - Mailgun
 * - Or any other email service provider
 */

class EmailService {
  private apiKey: string;
  private fromEmail: string;
  private fromName: string;

  constructor() {
    // In production, load from environment variables
    this.apiKey = import.meta.env.VITE_EMAIL_API_KEY || 'mock-api-key';
    this.fromEmail = import.meta.env.VITE_FROM_EMAIL || 'noreply@verigate.co.za';
    this.fromName = import.meta.env.VITE_FROM_NAME || 'VeriGate';
  }

  /**
   * Send a generic email
   */
  async sendEmail(data: EmailData): Promise<boolean> {
    try {
      // Mock implementation - replace with actual API call
      console.log('📧 Sending email:', {
        to: data.to,
        from: data.from,
        subject: data.subject,
        // Don't log full content in production
      });

      // In production, use actual email service:
      /*
      const response = await fetch('https://api.sendgrid.com/v3/mail/send', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          personalizations: [{ to: [{ email: data.to }] }],
          from: { email: data.from },
          subject: data.subject,
          content: [
            { type: 'text/plain', value: data.text || '' },
            { type: 'text/html', value: data.html || '' },
          ],
        }),
      });

      return response.ok;
      */

      // Mock success
      return true;
    } catch (error) {
      console.error('❌ Email send error:', error);
      return false;
    }
  }

  /**
   * Handle contact form submission
   */
  async sendContactEmail(data: ContactEmailData): Promise<boolean> {
    try {
      // Send auto-responder to user
      const autoResponder = EMAIL_TEMPLATES.contactAutoResponder(data.name);
      await this.sendEmail({
        to: data.email,
        from: this.fromEmail,
        subject: autoResponder.subject,
        html: autoResponder.html,
        text: autoResponder.text,
      });

      // Send notification to sales team
      const notification = EMAIL_TEMPLATES.contactNotification(data);
      await this.sendEmail({
        to: import.meta.env.VITE_SALES_EMAIL || 'sales@verigate.co.za',
        from: this.fromEmail,
        subject: notification.subject,
        html: notification.html,
        text: notification.text,
        replyTo: data.email,
      });

      return true;
    } catch (error) {
      console.error('❌ Contact email error:', error);
      return false;
    }
  }

  /**
   * Handle demo request submission
   */
  async sendDemoRequestEmail(data: DemoRequestEmailData): Promise<boolean> {
    try {
      // Send auto-responder to user
      const autoResponder = EMAIL_TEMPLATES.demoAutoResponder(data.firstName);
      await this.sendEmail({
        to: data.email,
        from: this.fromEmail,
        subject: autoResponder.subject,
        html: autoResponder.html,
        text: autoResponder.text,
      });

      // Send notification to sales team
      const notification = EMAIL_TEMPLATES.demoNotification(data);
      await this.sendEmail({
        to: import.meta.env.VITE_SALES_EMAIL || 'sales@verigate.co.za',
        from: this.fromEmail,
        subject: notification.subject,
        html: notification.html,
        text: notification.text,
        replyTo: data.email,
      });

      return true;
    } catch (error) {
      console.error('❌ Demo request email error:', error);
      return false;
    }
  }

  /**
   * Handle newsletter subscription
   */
  async sendNewsletterWelcome(data: NewsletterEmailData): Promise<boolean> {
    try {
      const welcome = EMAIL_TEMPLATES.newsletterWelcome();
      await this.sendEmail({
        to: data.email,
        from: this.fromEmail,
        subject: welcome.subject,
        html: welcome.html,
        text: welcome.text,
      });

      // Also add to mailing list (e.g., Mailchimp, SendGrid Lists)
      console.log('📧 Adding to newsletter list:', data.email);

      return true;
    } catch (error) {
      console.error('❌ Newsletter email error:', error);
      return false;
    }
  }
}

// Export singleton instance
export const emailService = new EmailService();
