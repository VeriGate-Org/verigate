// Blog post content database
// In a production app, this would come from a CMS or API

export const blogPostsContent: Record<string, any> = {
  "1": {
    title: "The Complete Guide to KYC Compliance in 2025",
    excerpt: "Everything you need to know about global KYC regulations, best practices, and how to implement compliant identity verification in your platform.",
    content: `Know Your Customer (KYC) compliance has evolved significantly in 2025. This guide covers global KYC regulations, implementation strategies, and best practices for maintaining compliance while optimizing user experience.`,
    author: { name: "Sarah Mitchell", role: "Chief Compliance Officer", avatar: "👩‍💼" },
    date: "2025-01-15",
    readTime: "12 min read",
    category: "Compliance",
    tags: ["KYC", "Compliance", "Regulations", "Best Practices"]
  },
  "2": {
    title: "AML Screening: Best Practices for Fintech Companies",
    excerpt: "Learn how to implement effective AML screening processes that balance compliance requirements with user experience.",
    content: `Anti-Money Laundering screening is critical for fintech platforms. This article explores implementation strategies, regulatory requirements across jurisdictions, and how to balance compliance with user experience using modern technology.`,
    author: { name: "Michael Chen", role: "Product Manager", avatar: "👨‍💻" },
    date: "2025-01-12",
    readTime: "8 min read",
    category: "Compliance",
    tags: ["AML", "Fintech", "Screening"]
  },
  "3": {
    title: "How to Integrate Identity Verification in 5 Minutes",
    excerpt: "A step-by-step tutorial showing how to add identity verification to your application using our JavaScript SDK.",
    content: `Get started with VeriGate's identity verification in just 5 minutes. This hands-on tutorial walks through SDK installation, configuration, and implementation with complete code examples for React and vanilla JavaScript applications.`,
    author: { name: "Alex Rodriguez", role: "Developer Advocate", avatar: "👨‍🔧" },
    date: "2025-01-10",
    readTime: "5 min read",
    category: "Technical",
    tags: ["Tutorial", "JavaScript", "Integration"]
  },
  "4": {
    title: "Document Fraud Detection with AI: Behind the Scenes",
    excerpt: "Explore the machine learning algorithms and techniques we use to detect fraudulent documents with 99.8% accuracy.",
    content: `Discover how VeriGate achieves 99.8% accuracy in fraud detection. We explore the computer vision, machine learning, and deep learning techniques that power our document verification system, including real-world case studies.`,
    author: { name: "Dr. Emily Watson", role: "Head of AI Research", avatar: "👩‍🔬" },
    date: "2025-01-08",
    readTime: "10 min read",
    category: "Technical",
    tags: ["AI", "Machine Learning", "Fraud Detection"]
  },
  "5": {
    title: "Biometric Authentication: Active vs Passive Liveness",
    excerpt: "Understanding the differences between active and passive liveness detection and when to use each approach.",
    content: `Liveness detection prevents biometric fraud. This comprehensive guide compares active and passive liveness detection, examining accuracy, user experience, implementation complexity, and optimal use cases for each approach.`,
    author: { name: "James Park", role: "Security Architect", avatar: "👨‍💼" },
    date: "2025-01-05",
    readTime: "7 min read",
    category: "Security",
    tags: ["Biometrics", "Liveness", "Security"]
  },
  "6": {
    title: "GDPR Compliance Checklist for Identity Verification",
    excerpt: "A comprehensive checklist to ensure your identity verification process complies with GDPR requirements.",
    content: `Navigate GDPR compliance for identity verification with this practical checklist. Covering data collection, processing, storage, user rights, cross-border transfers, and documentation requirements for audit readiness.`,
    author: { name: "Sarah Mitchell", role: "Chief Compliance Officer", avatar: "👩‍💼" },
    date: "2025-01-03",
    readTime: "9 min read",
    category: "Compliance",
    tags: ["GDPR", "Privacy", "Compliance"]
  },
  "7": {
    title: "Travel Rule Compliance for Crypto Exchanges",
    excerpt: "How crypto exchanges can implement the FATF Travel Rule while maintaining user privacy and security.",
    content: `The FATF Travel Rule requires crypto exchanges to share customer information for transactions over $1,000. Learn implementation strategies, technical solutions, privacy considerations, and global regulatory requirements.`,
    author: { name: "David Kim", role: "Crypto Compliance Lead", avatar: "👨‍💼" },
    date: "2025-01-01",
    readTime: "11 min read",
    category: "Compliance",
    tags: ["Crypto", "Travel Rule", "FATF"]
  },
  "8": {
    title: "Age Verification for Gaming Platforms: Complete Guide",
    excerpt: "Implementing compliant age verification for online gaming and gambling platforms across different jurisdictions.",
    content: `Age verification is crucial for gaming platforms. This guide covers regulatory requirements across jurisdictions, verification methods, implementation strategies, and how to balance compliance with user experience.`,
    author: { name: "Lisa Anderson", role: "Gaming Industry Expert", avatar: "👩‍💼" },
    date: "2024-12-28",
    readTime: "8 min read",
    category: "Industry",
    tags: ["Gaming", "Age Verification", "Compliance"]
  },
  "9": {
    title: "HIPAA-Compliant Patient Verification in Telehealth",
    excerpt: "Best practices for implementing patient identity verification in telehealth platforms while maintaining HIPAA compliance.",
    content: `Telehealth requires secure patient verification while maintaining HIPAA compliance. Explore authentication methods, privacy requirements, technical implementation, and best practices for healthcare platforms.`,
    author: { name: "Dr. Robert Martinez", role: "Healthcare Compliance Advisor", avatar: "👨‍⚕️" },
    date: "2024-12-25",
    readTime: "10 min read",
    category: "Industry",
    tags: ["Healthcare", "HIPAA", "Telehealth"]
  },
  "10": {
    title: "ROI of Automated Identity Verification",
    excerpt: "Calculate the return on investment from automating your identity verification process with real-world examples.",
    content: `Automated identity verification delivers measurable ROI. This analysis examines cost savings, fraud reduction, conversion rate improvements, and compliance benefits with real-world case studies and ROI calculation frameworks.`,
    author: { name: "Jennifer Lee", role: "Business Analyst", avatar: "👩‍💼" },
    date: "2024-12-22",
    readTime: "6 min read",
    category: "Industry",
    tags: ["ROI", "Business Case", "Automation"]
  },
  "11": {
    title: "Product Update: New Document Types and Coverage",
    excerpt: "We've added support for 500+ new document types across 25 countries. Here's what's new and improved.",
    content: `VeriGate expands global coverage with 500+ new document types across 25 countries. This update includes enhanced verification for Latin America, Southeast Asia, and Africa, plus improved processing speeds and accuracy.`,
    author: { name: "Michael Chen", role: "Product Manager", avatar: "👨‍💻" },
    date: "2024-12-20",
    readTime: "4 min read",
    category: "Product",
    tags: ["Product Update", "Documents", "Coverage"]
  }
};

export const defaultPost = {
  title: "Blog Post Coming Soon",
  excerpt: "This blog post is currently being written. Check back soon!",
  content: `# Coming Soon\n\nThis blog post is currently being written by our team. We're working hard to bring you quality content on identity verification, compliance, and security.\n\nIn the meantime, check out our other blog posts or explore our documentation to learn more about VeriGate's capabilities.\n\n---\n\n*Have a topic you'd like us to cover? Contact us with your suggestions!*`,
  author: { name: "VeriGate Team", role: "Content Team", avatar: "✍️" },
  date: new Date().toISOString().split('T')[0],
  readTime: "Coming soon",
  category: "General",
  tags: ["Coming Soon"]
};
