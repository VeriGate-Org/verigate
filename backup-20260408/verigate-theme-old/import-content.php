<?php
/**
 * VeriGate Content Import Script
 *
 * Run ONCE after WordPress installation and theme activation.
 * Visit: http://www.verigate.co.za/wp-content/themes/verigate/import-content.php
 *
 * This script:
 * 1. Creates all pages from the static HTML files
 * 2. Creates blog posts
 * 3. Configures WordPress settings (static front page, permalinks)
 */

// Bootstrap WordPress
require_once dirname(__FILE__) . '/../../../wp-load.php';

// Only admins can run this
if (!current_user_can('manage_options')) {
    die('Access denied. You must be logged in as an administrator.');
}

// Prevent running twice
if (get_option('verigate_import_done')) {
    die('Import has already been run. Delete the "verigate_import_done" option to re-run.');
}

header('Content-Type: text/html; charset=utf-8');
echo '<html><head><title>VeriGate Import</title><style>body{font-family:monospace;background:#0f172a;color:#e2e8f0;padding:2rem}h1{color:#dc2626}.ok{color:#16a34a}.warn{color:#d97706}.err{color:#ef4444}</style></head><body>';
echo '<h1>VeriGate Content Import</h1><pre>';

// Static HTML files are bundled in the theme's static-html/ directory
$static_dir = dirname(__FILE__) . '/static-html';

$theme_uri = get_template_directory_uri();

// ── Page definitions ─────────────────────────────────────────────
// Format: 'static_file' => ['title', 'slug', 'parent_slug']
// parent_slug = '' for top-level pages

$pages = array(
    // Top-level pages (index.html is handled by front-page.php, skip it)
    'about.html'            => array('About Us', 'about', ''),
    'analytics.html'        => array('Verification Analytics', 'analytics', ''),
    'blog.html'             => array('Blog', 'blog', ''),
    'careers.html'          => array('Careers', 'careers', ''),
    'compare-plans.html'    => array('Compare Plans', 'compare-plans', ''),
    'contact.html'          => array('Contact Us', 'contact', ''),
    'cookie-policy.html'    => array('Cookie Policy', 'cookie-policy', ''),
    'events.html'           => array('Events', 'events', ''),
    'faqs.html'             => array('FAQs', 'faqs', ''),
    'integrations.html'     => array('Integrations & API', 'integrations', ''),
    'partner-program.html'  => array('Partner Program', 'partner-program', ''),
    'platform.html'         => array('Platform Features', 'platform', ''),
    'pricing.html'          => array('Pricing', 'pricing', ''),
    'privacy.html'          => array('Privacy Policy', 'privacy', ''),
    'request-demo.html'     => array('Request a Demo', 'request-demo', ''),
    'resources.html'        => array('Resource Library', 'resources', ''),
    'roi-calculator.html'   => array('ROI Calculator', 'roi-calculator', ''),
    'south-africa.html'     => array('South Africa', 'south-africa', ''),
    'supported-documents.html' => array('Supported Documents', 'supported-documents', ''),
    'technical-support.html'   => array('Technical Support', 'technical-support', ''),
    'terms.html'            => array('Terms of Service', 'terms', ''),

    // Verification Types (parent: verification-types)
    'verification-types/index.html'              => array('Verification Types', 'verification-types', ''),
    'verification-types/criminal.html'           => array('Criminal Record Checks', 'criminal-record-checks', 'verification-types'),
    'verification-types/identity.html'           => array('Identity Verification', 'identity-verification', 'verification-types'),
    'verification-types/qualification.html'      => array('Qualification Checks', 'qualification-checks', 'verification-types'),
    'verification-types/employment.html'         => array('Employment History', 'employment-history', 'verification-types'),
    'verification-types/credit.html'             => array('Credit Screening', 'credit-screening', 'verification-types'),
    'verification-types/face-verification.html'  => array('Face Verification', 'face-verification', 'verification-types'),
    'verification-types/document-verification.html' => array('Document Verification', 'document-verification', 'verification-types'),
    'verification-types/business-verification.html' => array('Business Verification', 'business-verification', 'verification-types'),
    'verification-types/address-verification.html'  => array('Address Verification', 'address-verification', 'verification-types'),
    'verification-types/eidv.html'               => array('Electronic Identity Verification (eIDV)', 'eidv', 'verification-types'),
    'verification-types/age-verification.html'   => array('Age Verification', 'age-verification', 'verification-types'),
    'verification-types/behavioral-biometrics.html' => array('Behavioral Biometrics', 'behavioral-biometrics', 'verification-types'),
    'verification-types/consent-verification.html'  => array('Consent Verification', 'consent-verification', 'verification-types'),
    'verification-types/e-signature.html'        => array('Electronic Signatures', 'e-signature', 'verification-types'),
    'verification-types/nfc-verification.html'   => array('NFC Verification', 'nfc-verification', 'verification-types'),
    'verification-types/video-identification.html' => array('Video Identification', 'video-identification', 'verification-types'),

    // Compliance (parent: compliance)
    'compliance/index.html'              => array('Compliance Solutions', 'compliance', ''),
    'compliance/kyc.html'                => array('Know Your Customer (KYC)', 'kyc', 'compliance'),
    'compliance/kyb.html'                => array('Know Your Business (KYB)', 'kyb', 'compliance'),
    'compliance/kyi.html'                => array('Know Your Investor (KYI)', 'kyi', 'compliance'),
    'compliance/aml-screening.html'      => array('AML Screening', 'aml-screening', 'compliance'),
    'compliance/popia-compliance.html'   => array('POPIA Compliance', 'popia-compliance', 'compliance'),
    'compliance/iso-27001.html'          => array('ISO 27001 Compliance', 'iso-27001', 'compliance'),
    'compliance/soc2.html'               => array('SOC 2 Compliance', 'soc2', 'compliance'),
    'compliance/adverse-media.html'      => array('Adverse Media Screening', 'adverse-media', 'compliance'),
    'compliance/transaction-screening.html' => array('Transaction Screening', 'transaction-screening', 'compliance'),

    // Fraud Prevention (parent: fraud-prevention)
    'fraud-prevention/index.html'           => array('Fraud Prevention', 'fraud-prevention', ''),
    'fraud-prevention/identity-fraud.html'  => array('Identity Fraud Detection', 'identity-fraud', 'fraud-prevention'),
    'fraud-prevention/document-fraud.html'  => array('Document Fraud Detection', 'document-fraud', 'fraud-prevention'),
    'fraud-prevention/deepfake-detection.html' => array('Deepfake Detection', 'deepfake-detection', 'fraud-prevention'),
    'fraud-prevention/synthetic-identity.html' => array('Synthetic Identity Detection', 'synthetic-identity', 'fraud-prevention'),
    'fraud-prevention/device-intelligence.html' => array('Device Intelligence', 'device-intelligence', 'fraud-prevention'),

    // Solutions (parent: solutions — we'll create a parent page)
    'solutions/banking.html'        => array('Banking Solutions', 'banking', 'solutions'),
    'solutions/fintech.html'        => array('Fintech Solutions', 'fintech', 'solutions'),
    'solutions/crypto.html'         => array('Cryptocurrency Solutions', 'cryptocurrency', 'solutions'),
    'solutions/forex.html'          => array('Forex & Trading Solutions', 'forex-trading', 'solutions'),
    'solutions/gaming.html'         => array('Gaming & iGaming Solutions', 'gaming', 'solutions'),
    'solutions/gig-economy.html'    => array('Gig Economy Solutions', 'gig-economy', 'solutions'),
    'solutions/marketplaces.html'   => array('Marketplace Solutions', 'marketplaces', 'solutions'),
    'solutions/social-networks.html' => array('Social Network Solutions', 'social-networks', 'solutions'),
);

/**
 * Extract body content from a static HTML file.
 * Gets everything between </nav> (end of last nav) and <footer (start of footer).
 */
function extract_body_content($html) {
    // Find the end of the navbar (last </nav>)
    $nav_end = strrpos($html, '</nav>');
    if ($nav_end === false) return '';
    $content_start = $nav_end + 6; // strlen('</nav>')

    // Find the start of the footer
    $footer_start = strpos($html, '<footer', $content_start);
    if ($footer_start === false) {
        // Try finding the script tag as fallback
        $footer_start = strpos($html, '<script', $content_start);
    }
    if ($footer_start === false) return '';

    $content = substr($html, $content_start, $footer_start - $content_start);
    return trim($content);
}

/**
 * Fix image paths in content to use WordPress theme directory.
 * Converts relative paths like 'img/foo.jpg' or '../img/foo.jpg' to theme URI paths.
 */
function fix_image_paths($content, $theme_uri) {
    // Fix ../img/ references (from subdirectory pages)
    $content = str_replace('../img/', $theme_uri . '/img/', $content);
    // Fix img/ references (from root pages) — but not already-fixed ones
    $content = preg_replace('#(?<!\/)img/#', $theme_uri . '/img/', $content);
    // Fix ../css/ and ../js/ references
    $content = str_replace('../css/', $theme_uri . '/css/', $content);
    $content = str_replace('../js/', $theme_uri . '/js/', $content);
    return $content;
}

/**
 * Fix internal links to use WordPress permalinks.
 * Converts static .html links to WordPress slug-based URLs.
 */
function fix_internal_links($content) {
    $home = home_url('/');

    // Map of static file paths to WP slugs
    $link_map = array(
        // Root pages
        'index.html' => '',
        'about.html' => 'about/',
        'analytics.html' => 'analytics/',
        'blog.html' => 'blog/',
        'careers.html' => 'careers/',
        'compare-plans.html' => 'compare-plans/',
        'contact.html' => 'contact/',
        'cookie-policy.html' => 'cookie-policy/',
        'events.html' => 'events/',
        'faqs.html' => 'faqs/',
        'integrations.html' => 'integrations/',
        'partner-program.html' => 'partner-program/',
        'platform.html' => 'platform/',
        'pricing.html' => 'pricing/',
        'privacy.html' => 'privacy/',
        'request-demo.html' => 'request-demo/',
        'resources.html' => 'resources/',
        'roi-calculator.html' => 'roi-calculator/',
        'south-africa.html' => 'south-africa/',
        'supported-documents.html' => 'supported-documents/',
        'technical-support.html' => 'technical-support/',
        'terms.html' => 'terms/',

        // Verification Types
        'verification-types/index.html' => 'verification-types/',
        'verification-types/criminal.html' => 'verification-types/criminal-record-checks/',
        'verification-types/identity.html' => 'verification-types/identity-verification/',
        'verification-types/qualification.html' => 'verification-types/qualification-checks/',
        'verification-types/employment.html' => 'verification-types/employment-history/',
        'verification-types/credit.html' => 'verification-types/credit-screening/',
        'verification-types/face-verification.html' => 'verification-types/face-verification/',
        'verification-types/document-verification.html' => 'verification-types/document-verification/',
        'verification-types/business-verification.html' => 'verification-types/business-verification/',
        'verification-types/address-verification.html' => 'verification-types/address-verification/',
        'verification-types/eidv.html' => 'verification-types/eidv/',
        'verification-types/age-verification.html' => 'verification-types/age-verification/',
        'verification-types/behavioral-biometrics.html' => 'verification-types/behavioral-biometrics/',
        'verification-types/consent-verification.html' => 'verification-types/consent-verification/',
        'verification-types/e-signature.html' => 'verification-types/e-signature/',
        'verification-types/nfc-verification.html' => 'verification-types/nfc-verification/',
        'verification-types/video-identification.html' => 'verification-types/video-identification/',

        // Compliance
        'compliance/index.html' => 'compliance/',
        'compliance/kyc.html' => 'compliance/kyc/',
        'compliance/kyb.html' => 'compliance/kyb/',
        'compliance/kyi.html' => 'compliance/kyi/',
        'compliance/aml-screening.html' => 'compliance/aml-screening/',
        'compliance/popia-compliance.html' => 'compliance/popia-compliance/',
        'compliance/iso-27001.html' => 'compliance/iso-27001/',
        'compliance/soc2.html' => 'compliance/soc2/',
        'compliance/adverse-media.html' => 'compliance/adverse-media/',
        'compliance/transaction-screening.html' => 'compliance/transaction-screening/',

        // Fraud Prevention
        'fraud-prevention/index.html' => 'fraud-prevention/',
        'fraud-prevention/identity-fraud.html' => 'fraud-prevention/identity-fraud/',
        'fraud-prevention/document-fraud.html' => 'fraud-prevention/document-fraud/',
        'fraud-prevention/deepfake-detection.html' => 'fraud-prevention/deepfake-detection/',
        'fraud-prevention/synthetic-identity.html' => 'fraud-prevention/synthetic-identity/',
        'fraud-prevention/device-intelligence.html' => 'fraud-prevention/device-intelligence/',

        // Solutions
        'solutions/banking.html' => 'solutions/banking/',
        'solutions/fintech.html' => 'solutions/fintech/',
        'solutions/crypto.html' => 'solutions/cryptocurrency/',
        'solutions/forex.html' => 'solutions/forex-trading/',
        'solutions/gaming.html' => 'solutions/gaming/',
        'solutions/gig-economy.html' => 'solutions/gig-economy/',
        'solutions/marketplaces.html' => 'solutions/marketplaces/',
        'solutions/social-networks.html' => 'solutions/social-networks/',
    );

    foreach ($link_map as $static_path => $wp_slug) {
        // Replace href="../path" (from subdirectory pages)
        $content = str_replace('href="../' . $static_path . '"', 'href="' . $home . $wp_slug . '"', $content);
        // Replace href="path" (from root pages)
        $content = str_replace('href="' . $static_path . '"', 'href="' . $home . $wp_slug . '"', $content);
    }

    return $content;
}

// ── Create a Solutions parent page (no static HTML, just a redirect page) ──
$solutions_page = wp_insert_post(array(
    'post_title'   => 'Solutions',
    'post_name'    => 'solutions',
    'post_content' => '<section class="hero hero--dark"><div class="container"><div class="hero__centered"><h1 class="hero__title">Industry Solutions</h1><p class="hero__subtitle">Tailored verification and compliance solutions for every industry.</p></div></div></section>',
    'post_status'  => 'publish',
    'post_type'    => 'page',
    'post_author'  => 1,
));
echo $solutions_page ? "<span class='ok'>[OK]</span> Created parent: Solutions (ID: $solutions_page)\n" : "<span class='err'>[ERR]</span> Failed to create Solutions parent page\n";

// ── Track parent page IDs ──
$parent_ids = array(
    'verification-types' => 0,
    'compliance' => 0,
    'fraud-prevention' => 0,
    'solutions' => $solutions_page,
);

// ── First pass: Create parent pages ──
$page_ids = array();
echo "\n--- Creating parent pages ---\n";
foreach ($pages as $file => $info) {
    list($title, $slug, $parent_slug) = $info;

    if ($parent_slug !== '') continue; // Skip child pages in first pass
    if ($slug === 'solutions') continue; // Already created above

    // Read static HTML
    $html_path = $static_dir . '/' . $file;
    $content = '';
    if (file_exists($html_path)) {
        $html = file_get_contents($html_path);
        $content = extract_body_content($html);
        $content = fix_image_paths($content, $theme_uri);
        $content = fix_internal_links($content);
    } else {
        echo "<span class='warn'>[WARN]</span> Static file not found: $file\n";
    }

    $page_id = wp_insert_post(array(
        'post_title'   => $title,
        'post_name'    => $slug,
        'post_content' => $content,
        'post_status'  => 'publish',
        'post_type'    => 'page',
        'post_author'  => 1,
    ));

    if ($page_id) {
        $page_ids[$slug] = $page_id;
        if (isset($parent_ids[$slug])) {
            $parent_ids[$slug] = $page_id;
        }
        echo "<span class='ok'>[OK]</span> Created: $title (slug: $slug, ID: $page_id)\n";
    } else {
        echo "<span class='err'>[ERR]</span> Failed: $title\n";
    }
}

// ── Second pass: Create child pages ──
echo "\n--- Creating child pages ---\n";
foreach ($pages as $file => $info) {
    list($title, $slug, $parent_slug) = $info;

    if ($parent_slug === '') continue; // Skip parent pages

    $parent_id = isset($parent_ids[$parent_slug]) ? $parent_ids[$parent_slug] : 0;

    // Read static HTML
    $html_path = $static_dir . '/' . $file;
    $content = '';
    if (file_exists($html_path)) {
        $html = file_get_contents($html_path);
        $content = extract_body_content($html);
        $content = fix_image_paths($content, $theme_uri);
        $content = fix_internal_links($content);
    } else {
        echo "<span class='warn'>[WARN]</span> Static file not found: $file\n";
    }

    $page_id = wp_insert_post(array(
        'post_title'   => $title,
        'post_name'    => $slug,
        'post_content' => $content,
        'post_status'  => 'publish',
        'post_type'    => 'page',
        'post_parent'  => $parent_id,
        'post_author'  => 1,
    ));

    if ($page_id) {
        $page_ids[$slug] = $page_id;
        echo "<span class='ok'>[OK]</span> Created: $title (slug: $slug, parent: $parent_slug, ID: $page_id)\n";
    } else {
        echo "<span class='err'>[ERR]</span> Failed: $title\n";
    }
}

// ── Create blog posts ──
echo "\n--- Creating blog posts ---\n";

$blog_posts = array(
    array(
        'title' => 'The State of Background Verification in South Africa: 2026 Outlook',
        'slug'  => 'state-of-verification-2026',
        'date'  => '2026-01-15',
        'image' => 'blog/state-of-verification-2026.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>As we enter 2026, the landscape of background verification in South Africa continues to evolve rapidly. With increasing regulatory requirements, technological advancements, and a growing emphasis on trust and compliance, organisations are rethinking their approach to screening.</p>

<h2>Key Trends Shaping 2026</h2>
<p>The verification industry is being shaped by several major trends: the rise of digital identity verification, stricter POPIA enforcement, and the integration of AI-powered fraud detection. Companies that adapt to these changes will gain a significant competitive advantage in talent acquisition and risk management.</p>

<h2>Regulatory Landscape</h2>
<p>The Information Regulator has stepped up POPIA enforcement, issuing significant fines to organisations that fail to comply with data protection requirements during background checks. This has made compliance-first verification platforms essential rather than optional.</p>

<h2>Technology Integration</h2>
<p>API-first platforms like VeriGate are enabling seamless integration of verification services into existing HR workflows, reducing turnaround times from days to hours while maintaining accuracy and compliance.</p>
</div></div></section>',
    ),
    array(
        'title' => 'FICA Amendments 2026: What It Means for Your KYC Process',
        'slug'  => 'fica-amendments-2026',
        'date'  => '2026-02-01',
        'image' => 'blog/fica-amendments-2026.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>The Financial Intelligence Centre Act (FICA) amendments coming into effect in 2026 bring significant changes to how financial institutions and accountable institutions must conduct their Know Your Customer (KYC) processes.</p>

<h2>Key Changes</h2>
<p>The amendments introduce enhanced due diligence requirements for high-risk customers, expanded beneficial ownership transparency requirements, and stricter ongoing monitoring obligations. These changes align South Africa more closely with FATF recommendations.</p>

<h2>Impact on Businesses</h2>
<p>Organisations need to review and update their customer onboarding processes, invest in technology solutions that can handle the increased verification requirements, and ensure their staff are trained on the new compliance obligations.</p>

<h2>How VeriGate Helps</h2>
<p>Our platform has been updated to support all new FICA requirements, including enhanced beneficial ownership verification, automated risk scoring, and real-time sanctions screening that meets the new regulatory standards.</p>
</div></div></section>',
    ),
    array(
        'title' => 'POPIA and Background Screening: A Complete Compliance Guide',
        'slug'  => 'popia-background-screening',
        'date'  => '2026-02-10',
        'image' => 'blog/popia-background-screening.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>The Protection of Personal Information Act (POPIA) has fundamentally changed how organisations can collect, process, and store personal information during background screening processes. Understanding these requirements is essential for any employer conducting verification checks.</p>

<h2>Consent Requirements</h2>
<p>Under POPIA, explicit consent must be obtained before conducting any background check. This consent must be specific, informed, and freely given. Generic consent forms are no longer sufficient — candidates must understand exactly what checks will be conducted.</p>

<h2>Data Minimisation</h2>
<p>Only information that is directly relevant to the position should be collected and verified. Conducting credit checks for roles where financial responsibility is not a key requirement may violate POPIA principles.</p>

<h2>Data Retention</h2>
<p>Verification results should only be retained for as long as necessary. Organisations must have clear data retention policies and ensure that personal information is securely destroyed when no longer needed.</p>
</div></div></section>',
    ),
    array(
        'title' => 'Criminal Record Checks for Hiring: Best Practices for South African Employers',
        'slug'  => 'criminal-checks-hiring',
        'date'  => '2026-02-20',
        'image' => 'blog/criminal-checks-hiring.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>Criminal record checks are one of the most common verification types requested by South African employers. However, navigating the legal requirements and ethical considerations can be complex. Here\'s what you need to know.</p>

<h2>Legal Framework</h2>
<p>The Criminal Procedure Act and Labour Relations Act both provide guidance on when and how criminal record checks can be conducted. Not all positions require criminal checks, and the relevance of a criminal record to the specific role must be carefully considered.</p>

<h2>Best Practices</h2>
<p>Always obtain written consent, ensure the check is relevant to the position, consider the nature and age of any offences found, give candidates the opportunity to explain their records, and never make blanket policies that automatically disqualify candidates with any criminal record.</p>

<h2>Turnaround Times</h2>
<p>With VeriGate, criminal record checks through the SAPS Criminal Record Centre typically return within 5-10 business days, though urgent processing options are available for time-sensitive hiring decisions.</p>
</div></div></section>',
    ),
    array(
        'title' => 'How Facial Recognition is Transforming Customer Onboarding',
        'slug'  => 'facial-recognition-onboarding',
        'date'  => '2026-03-01',
        'image' => 'blog/facial-recognition-onboarding.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>Facial recognition technology is revolutionising how businesses verify customer identities during onboarding. With advances in liveness detection and AI-powered matching, remote identity verification is now as reliable as in-person checks.</p>

<h2>How It Works</h2>
<p>Modern face verification systems capture a live selfie, compare it against the photo on an identity document, and use liveness detection to ensure the person is physically present. This three-step process takes seconds but provides bank-grade identity assurance.</p>

<h2>Accuracy and Bias</h2>
<p>Leading platforms like VeriGate use algorithms specifically trained on diverse South African populations, ensuring high accuracy across all demographic groups. Regular bias audits and continuous model improvements maintain fairness and reliability.</p>

<h2>Regulatory Compliance</h2>
<p>Face verification for onboarding must comply with POPIA, FICA, and the upcoming AI governance framework. VeriGate ensures all biometric data is processed in compliance with these regulations, including secure storage and timely deletion.</p>
</div></div></section>',
    ),
    array(
        'title' => 'Bulk Verification Upload: Processing Thousands of Checks Efficiently',
        'slug'  => 'bulk-verification-upload',
        'date'  => '2026-03-10',
        'image' => 'blog/bulk-verification-upload.jpg',
        'content' => '<section class="section fade-in-up"><div class="container container--narrow"><div class="content-section">
<p>For organisations with high-volume verification needs — whether seasonal hiring, annual re-screening, or large-scale onboarding — bulk upload capabilities are essential. VeriGate\'s bulk verification feature lets you process thousands of checks from a single CSV upload.</p>

<h2>How Bulk Upload Works</h2>
<p>Download our CSV template, populate it with candidate details and required verification types, upload it through the VeriGate portal or API, and monitor progress in real-time via the dashboard. Results are delivered as they complete, with full reports available for download.</p>

<h2>Performance</h2>
<p>Our platform can handle uploads of up to 10,000 candidates per batch, with automatic queue management and parallel processing to ensure fast turnaround. Priority processing is available for time-sensitive bulk orders.</p>

<h2>Cost Efficiency</h2>
<p>Bulk verification comes with volume-based pricing discounts, making it significantly more cost-effective than processing individual checks. Contact our sales team for custom pricing on volumes exceeding 5,000 checks per month.</p>
</div></div></section>',
    ),
);

foreach ($blog_posts as $post_data) {
    $post_id = wp_insert_post(array(
        'post_title'   => $post_data['title'],
        'post_name'    => $post_data['slug'],
        'post_content' => $post_data['content'],
        'post_status'  => 'publish',
        'post_type'    => 'post',
        'post_date'    => $post_data['date'] . ' 09:00:00',
        'post_author'  => 1,
    ));

    if ($post_id) {
        // Set featured image if it exists
        $img_path = get_template_directory() . '/img/' . $post_data['image'];
        if (file_exists($img_path)) {
            // Upload image to media library
            $upload_dir = wp_upload_dir();
            $filename = basename($post_data['image']);
            $dest = $upload_dir['path'] . '/' . $filename;
            copy($img_path, $dest);

            $filetype = wp_check_filetype($filename);
            $attachment = array(
                'guid'           => $upload_dir['url'] . '/' . $filename,
                'post_mime_type' => $filetype['type'],
                'post_title'     => sanitize_file_name($filename),
                'post_content'   => '',
                'post_status'    => 'inherit',
            );
            $attach_id = wp_insert_attachment($attachment, $dest, $post_id);
            if ($attach_id) {
                require_once(ABSPATH . 'wp-admin/includes/image.php');
                $attach_data = wp_generate_attachment_metadata($attach_id, $dest);
                wp_update_attachment_metadata($attach_id, $attach_data);
                set_post_thumbnail($post_id, $attach_id);
            }
        }
        echo "<span class='ok'>[OK]</span> Created post: {$post_data['title']} (ID: $post_id)\n";
    } else {
        echo "<span class='err'>[ERR]</span> Failed post: {$post_data['title']}\n";
    }
}

// ── Configure WordPress settings ──
echo "\n--- Configuring WordPress settings ---\n";

// Set static front page
$front_page = get_page_by_path('home');
if (!$front_page) {
    // Create a front page that uses front-page.php template
    $front_id = wp_insert_post(array(
        'post_title'   => 'Home',
        'post_name'    => 'home',
        'post_content' => '',
        'post_status'  => 'publish',
        'post_type'    => 'page',
        'post_author'  => 1,
    ));
} else {
    $front_id = $front_page->ID;
}

// Set blog page
$blog_page_id = isset($page_ids['blog']) ? $page_ids['blog'] : 0;

// Configure reading settings
update_option('show_on_front', 'page');
update_option('page_on_front', $front_id);
if ($blog_page_id) {
    update_option('page_for_posts', $blog_page_id);
}
echo "<span class='ok'>[OK]</span> Set static front page (ID: $front_id)\n";
echo "<span class='ok'>[OK]</span> Set blog page (ID: $blog_page_id)\n";

// Configure permalinks
global $wp_rewrite;
$wp_rewrite->set_permalink_structure('/%postname%/');
$wp_rewrite->flush_rules();
echo "<span class='ok'>[OK]</span> Set permalink structure to /%postname%/\n";

// Set timezone
update_option('timezone_string', 'Africa/Johannesburg');
echo "<span class='ok'>[OK]</span> Set timezone to Africa/Johannesburg\n";

// Set site title and tagline
update_option('blogname', 'VeriGate');
update_option('blogdescription', 'Enterprise-Grade Background Screening for South Africa');
echo "<span class='ok'>[OK]</span> Set site title and tagline\n";

// Delete default WordPress content
wp_delete_post(1, true); // Hello World post
wp_delete_post(2, true); // Sample page
wp_delete_comment(1, true); // Default comment
echo "<span class='ok'>[OK]</span> Deleted default WordPress content\n";

// Mark import as done
update_option('verigate_import_done', true);

echo "\n<span class='ok'>========================================</span>\n";
echo "<span class='ok'>Import complete!</span>\n";
echo "Pages created: " . count($page_ids) . "\n";
echo "Blog posts created: " . count($blog_posts) . "\n";
echo "\nNext steps:\n";
echo "1. Visit the site: " . home_url('/') . "\n";
echo "2. Check all pages load correctly\n";
echo "3. Enable SSL via cPanel\n";
echo "4. Update WordPress URLs to HTTPS\n";
echo '</pre></body></html>';
