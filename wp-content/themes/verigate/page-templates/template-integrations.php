<?php
/**
 * Template Name: Integrations
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <?php
    $bullet_icons = array( 'sparkles', 'circle-dot', 'arrow-right-circle', 'star' );
    $logo_base    = VERIGATE_URI . '/assets/img/logos/integrations/';

    $integration_categories = array(
        array(
            'category'     => 'HRIS & ATS Platforms',
            'icon'         => 'users',
            'description'  => 'Integrate verification directly into your HR and recruitment workflows',
            'integrations' => array(
                array( 'name' => 'Zoho People', 'logo' => 'zoho.svg', 'description' => 'Trigger verifications from Zoho People recruitment pipeline', 'popular' => true ),
                array( 'name' => 'SAP SuccessFactors', 'logo' => 'sap.svg', 'description' => 'Enterprise HRIS integration with automated screening', 'popular' => true ),
                array( 'name' => 'Sage HR', 'logo' => 'sage.svg', 'description' => 'SA-focused HR platform integration for employee onboarding' ),
                array( 'name' => 'BambooHR', 'logo' => 'bamboohr.svg', 'description' => 'Automated background checks from BambooHR candidate profiles' ),
                array( 'name' => 'PaySpace', 'logo' => 'payspace.svg', 'description' => 'South African payroll and HR integration' ),
            ),
        ),
        array(
            'category'     => 'REST API',
            'icon'         => 'code',
            'description'  => 'Build custom integrations with our comprehensive REST API',
            'integrations' => array(
                array( 'name' => 'REST API v2', 'description' => 'Full-featured API with OAuth 2.0 authentication and webhook support', 'popular' => true ),
                array( 'name' => 'Webhooks', 'description' => 'Real-time notifications for verification status changes', 'popular' => true ),
                array( 'name' => 'Batch API', 'description' => 'Submit and process bulk verifications programmatically' ),
                array( 'name' => 'SDKs', 'description' => 'Client libraries for JavaScript, Python, C#, Java, and PHP' ),
            ),
        ),
        array(
            'category'     => 'CRM Platforms',
            'icon'         => 'database',
            'description'  => 'Sync verification data with your customer relationship management system',
            'integrations' => array(
                array( 'name' => 'Zoho CRM', 'logo' => 'zoho.svg', 'description' => 'Two-way sync with verification status updates', 'popular' => true ),
                array( 'name' => 'Salesforce', 'logo' => 'salesforce.svg', 'description' => 'Custom object integration for compliance tracking' ),
                array( 'name' => 'HubSpot', 'logo' => 'hubspot.svg', 'description' => 'Contact and deal enrichment with verification data' ),
                array( 'name' => 'Microsoft Dynamics', 'logo' => 'microsoft.svg', 'description' => 'Enterprise CRM integration with custom fields' ),
            ),
        ),
        array(
            'category'     => 'Automation & Workflow',
            'icon'         => 'zap',
            'description'  => 'Automate verification workflows with popular automation tools',
            'integrations' => array(
                array( 'name' => 'Zapier', 'logo' => 'zapier.svg', 'description' => 'Connect VeriGate to 5,000+ apps with no-code automation', 'popular' => true ),
                array( 'name' => 'Make (Integromat)', 'logo' => 'make.svg', 'description' => 'Visual workflow automation for complex processes' ),
                array( 'name' => 'Microsoft Power Automate', 'logo' => 'microsoft.svg', 'description' => 'Enterprise workflow automation' ),
            ),
        ),
        array(
            'category'     => 'Communication',
            'icon'         => 'message-square',
            'description'  => 'Get verification updates in your team communication tools',
            'integrations' => array(
                array( 'name' => 'Slack', 'logo' => 'slack.svg', 'description' => 'Real-time verification notifications in Slack channels', 'popular' => true ),
                array( 'name' => 'Microsoft Teams', 'logo' => 'microsoft.svg', 'description' => 'Verification alerts and status updates in Teams' ),
                array( 'name' => 'Email (SMTP)', 'description' => 'Automated email notifications for completed verifications' ),
            ),
        ),
        array(
            'category'     => 'Analytics & Reporting',
            'icon'         => 'bar-chart-3',
            'description'  => 'Export verification data to your analytics and BI tools',
            'integrations' => array(
                array( 'name' => 'Power BI', 'logo' => 'power-bi.svg', 'description' => 'Verification dashboards and analytics reporting' ),
                array( 'name' => 'Google Sheets', 'logo' => 'google.svg', 'description' => 'Automatic export of verification results to spreadsheets' ),
                array( 'name' => 'CSV/PDF Export', 'description' => 'Download verification reports in standard formats' ),
            ),
        ),
    );

    $api_features = array(
        array( 'title' => 'OAuth 2.0 Authentication', 'description' => 'Secure token-based API access with refresh tokens' ),
        array( 'title' => 'Webhook Notifications', 'description' => 'Real-time callbacks for verification status changes' ),
        array( 'title' => 'Rate Limiting', 'description' => '100 req/s for Professional, custom limits for Enterprise' ),
        array( 'title' => 'SDKs & Libraries', 'description' => 'Official SDKs for JavaScript, Python, C#, Java, PHP' ),
    );
    ?>

    <!-- Hero Section -->
    <section class="relative pt-16 pb-12 overflow-hidden bg-gradient-to-br from-primary/5 via-background to-primary/10">
        <div class="container mx-auto max-w-6xl text-center px-4">
            <div class="animate-on-scroll fade-up space-y-6">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-secondary text-secondary-foreground">
                    Integrations &amp; API
                </span>
                <h1 class="text-4xl md:text-5xl font-bold text-foreground">
                    Integrations &amp; API
                </h1>
                <p class="text-xl text-muted-foreground max-w-3xl mx-auto">
                    Connect VeriGate with your HRIS, ATS, CRM, and custom systems. Our REST API and pre-built integrations make it easy to embed verification into any workflow.
                </p>

                <!-- Search Bar -->
                <div class="relative max-w-2xl mx-auto">
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-muted-foreground">
                        <?php verigate_icon( 'search', 'w-5 h-5', 20 ); ?>
                    </div>
                    <input
                        type="text"
                        id="integration-search"
                        placeholder="Search integrations..."
                        class="w-full pl-12 pr-4 py-4 text-lg rounded-lg border border-border bg-card text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent/50"
                    />
                </div>
            </div>
        </div>
    </section>

    <!-- API Code Example -->
    <section class="py-16 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="grid md:grid-cols-2 gap-8 items-start">
                <div class="animate-on-scroll fade-up">
                    <h2 class="text-3xl font-bold text-foreground mb-4">Powerful REST API</h2>
                    <p class="text-lg text-muted-foreground mb-6">
                        Submit verification requests, receive real-time webhook notifications, and download reports — all via our RESTful API.
                    </p>
                    <div class="space-y-4">
                        <?php foreach ( $api_features as $idx => $feature ) :
                            $b_icon = $bullet_icons[ $idx % count( $bullet_icons ) ];
                        ?>
                            <div class="flex items-start gap-3">
                                <?php verigate_icon( $b_icon, 'w-5 h-5 text-accent flex-shrink-0 mt-0.5', 20 ); ?>
                                <div>
                                    <p class="font-medium text-foreground"><?php echo esc_html( $feature['title'] ); ?></p>
                                    <p class="text-sm text-muted-foreground"><?php echo esc_html( $feature['description'] ); ?></p>
                                </div>
                            </div>
                        <?php endforeach; ?>
                    </div>
                </div>
                <!-- Terminal-style code block -->
                <div class="animate-on-scroll fade-up bg-[#0f172a] rounded-xl overflow-hidden shadow-2xl">
                    <div class="flex items-center gap-2 px-4 py-3 bg-[#1e293b] border-b border-white/10">
                        <span class="w-3 h-3 rounded-full bg-red-400"></span>
                        <span class="w-3 h-3 rounded-full bg-yellow-400"></span>
                        <span class="w-3 h-3 rounded-full bg-green-400"></span>
                        <span class="ml-2 text-xs text-white/40 font-mono">api-request.js</span>
                    </div>
                    <div class="p-6 overflow-x-auto">
                        <pre class="text-sm text-[#e2e8f0] font-mono whitespace-pre leading-relaxed"><span class="text-[#94a3b8]">// Submit a verification request</span>
<span class="text-[#c084fc]">const</span> response = <span class="text-[#c084fc]">await</span> <span class="text-[#38bdf8]">fetch</span>(<span class="text-[#86efac]">'https://api.verigate.co.za/v2/verifications'</span>, {
  <span class="text-[#e2e8f0]">method</span>: <span class="text-[#86efac]">'POST'</span>,
  <span class="text-[#e2e8f0]">headers</span>: {
    <span class="text-[#86efac]">'Authorization'</span>: <span class="text-[#86efac]">'Bearer YOUR_API_KEY'</span>,
    <span class="text-[#86efac]">'Content-Type'</span>: <span class="text-[#86efac]">'application/json'</span>,
  },
  <span class="text-[#e2e8f0]">body</span>: <span class="text-[#e2e8f0]">JSON</span>.<span class="text-[#38bdf8]">stringify</span>({
    <span class="text-[#e2e8f0]">candidate</span>: {
      <span class="text-[#e2e8f0]">firstName</span>: <span class="text-[#86efac]">'John'</span>,
      <span class="text-[#e2e8f0]">lastName</span>: <span class="text-[#86efac]">'Smith'</span>,
      <span class="text-[#e2e8f0]">idNumber</span>: <span class="text-[#86efac]">'8501015800089'</span>,
    },
    <span class="text-[#e2e8f0]">checks</span>: [<span class="text-[#86efac]">'criminal'</span>, <span class="text-[#86efac]">'identity'</span>, <span class="text-[#86efac]">'qualification'</span>],
    <span class="text-[#e2e8f0]">consentRef</span>: <span class="text-[#86efac]">'CONSENT-2026-001'</span>,
  }),
});

<span class="text-[#94a3b8]">// Response</span>
{
  <span class="text-[#86efac]">"id"</span>: <span class="text-[#86efac]">"ver_abc123"</span>,
  <span class="text-[#86efac]">"status"</span>: <span class="text-[#86efac]">"processing"</span>,
  <span class="text-[#86efac]">"checks"</span>: [
    { <span class="text-[#86efac]">"type"</span>: <span class="text-[#86efac]">"criminal"</span>, <span class="text-[#86efac]">"status"</span>: <span class="text-[#86efac]">"pending"</span>, <span class="text-[#86efac]">"eta"</span>: <span class="text-[#86efac]">"3-5 days"</span> },
    { <span class="text-[#86efac]">"type"</span>: <span class="text-[#86efac]">"identity"</span>, <span class="text-[#86efac]">"status"</span>: <span class="text-[#86efac]">"pending"</span>, <span class="text-[#86efac]">"eta"</span>: <span class="text-[#86efac]">"instant"</span> },
    { <span class="text-[#86efac]">"type"</span>: <span class="text-[#86efac]">"qualification"</span>, <span class="text-[#86efac]">"status"</span>: <span class="text-[#86efac]">"pending"</span>, <span class="text-[#86efac]">"eta"</span>: <span class="text-[#86efac]">"2-5 days"</span> }
  ]
}</pre>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Integration Categories -->
    <section class="py-16 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl font-bold text-foreground mb-4">Pre-built Integrations</h2>
                <p class="text-lg text-muted-foreground">
                    Connect VeriGate with your existing tools in minutes
                </p>
            </div>

            <div class="space-y-12" id="integration-list">
                <?php foreach ( $integration_categories as $cat_idx => $cat ) : ?>
                    <div class="integration-category" data-category="<?php echo esc_attr( strtolower( $cat['category'] ) ); ?>">
                        <div class="flex items-center gap-3 mb-6">
                            <div class="p-2 bg-primary/10 rounded-lg">
                                <?php verigate_icon( $cat['icon'], 'w-6 h-6 text-primary', 24 ); ?>
                            </div>
                            <div>
                                <h3 class="text-2xl font-bold text-foreground"><?php echo esc_html( $cat['category'] ); ?></h3>
                                <p class="text-sm text-muted-foreground"><?php echo esc_html( $cat['description'] ); ?></p>
                            </div>
                        </div>

                        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                            <?php foreach ( $cat['integrations'] as $int_idx => $integration ) :
                                $is_popular = ! empty( $integration['popular'] );
                                $has_logo   = ! empty( $integration['logo'] );
                            ?>
                                <div class="integration-card bg-card border border-border rounded-lg p-5 hover:shadow-lg hover:border-border/80 hover:-translate-y-0.5 transition-all duration-200"
                                     data-name="<?php echo esc_attr( strtolower( $integration['name'] ) ); ?>">
                                    <div class="flex items-start gap-3">
                                        <?php if ( $has_logo ) : ?>
                                            <img
                                                src="<?php echo esc_url( $logo_base . $integration['logo'] ); ?>"
                                                alt="<?php echo esc_attr( $integration['name'] ); ?>"
                                                class="w-10 h-10 object-contain flex-shrink-0"
                                                loading="lazy"
                                            >
                                        <?php else : ?>
                                            <div class="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                                                <?php verigate_icon( $cat['icon'], 'w-5 h-5 text-primary', 20 ); ?>
                                            </div>
                                        <?php endif; ?>
                                        <div class="flex-1 min-w-0">
                                            <div class="flex items-start justify-between gap-2">
                                                <h4 class="text-lg font-semibold text-foreground"><?php echo esc_html( $integration['name'] ); ?></h4>
                                                <?php if ( $is_popular ) : ?>
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary text-primary-foreground flex-shrink-0">Popular</span>
                                                <?php endif; ?>
                                            </div>
                                            <p class="text-sm text-muted-foreground mt-1"><?php echo esc_html( $integration['description'] ); ?></p>
                                        </div>
                                    </div>
                                </div>
                            <?php endforeach; ?>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'We process over 500 verifications a month through VeriGate. The bulk upload feature and API integration with our ATS has been a game-changer for our recruitment team.',
        'name'    => 'Johan van der Merwe',
        'role'    => 'Talent Acquisition Director',
        'company' => 'Vodacom',
        'photo'   => 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'vodacom.svg',
    ) ); ?>

    <!-- Custom Integration & Partner CTAs -->
    <section class="py-16 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="grid md:grid-cols-2 gap-8">
                <!-- Custom Integration -->
                <div class="bg-card border border-border rounded-lg overflow-hidden">
                    <div class="p-6">
                        <h3 class="text-xl font-bold text-foreground mb-2">Custom Integration</h3>
                        <p class="text-muted-foreground mb-6">
                            Need to integrate with a platform not listed here? Our team can build custom integrations for Enterprise clients.
                        </p>
                        <ul class="space-y-2 mb-6">
                            <?php
                            $custom_items = array( 'Custom API endpoints', 'ERP and HRIS connectors', 'Dedicated technical support', 'SLA guarantees' );
                            foreach ( $custom_items as $idx => $item ) :
                                $b_icon = $bullet_icons[ $idx % count( $bullet_icons ) ];
                            ?>
                                <li class="flex items-start gap-2 text-sm text-foreground">
                                    <?php verigate_icon( $b_icon, 'w-4 h-4 text-primary flex-shrink-0 mt-0.5', 16 ); ?>
                                    <span><?php echo esc_html( $item ); ?></span>
                                </li>
                            <?php endforeach; ?>
                        </ul>
                        <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center w-full px-6 py-3 text-sm font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 transition-all">
                            Request Custom Integration
                        </a>
                    </div>
                </div>

                <!-- Technology Partner -->
                <div class="bg-card border border-border rounded-lg overflow-hidden">
                    <div class="p-6">
                        <h3 class="text-xl font-bold text-foreground mb-2">Become a Technology Partner</h3>
                        <p class="text-muted-foreground mb-6">
                            Join our partner programme and integrate VeriGate into your platform.
                        </p>
                        <ul class="space-y-2 mb-6">
                            <?php
                            $partner_items = array( 'Co-marketing opportunities', 'Revenue sharing', 'Technical integration support', 'Partner portal access' );
                            foreach ( $partner_items as $idx => $item ) :
                                $b_icon = $bullet_icons[ $idx % count( $bullet_icons ) ];
                            ?>
                                <li class="flex items-start gap-2 text-sm text-foreground">
                                    <?php verigate_icon( $b_icon, 'w-4 h-4 text-primary flex-shrink-0 mt-0.5', 16 ); ?>
                                    <span><?php echo esc_html( $item ); ?></span>
                                </li>
                            <?php endforeach; ?>
                        </ul>
                        <a href="<?php echo esc_url( home_url( '/partner-program/' ) ); ?>" class="inline-flex items-center justify-center w-full px-6 py-3 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            Learn About Partnership
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- CTA -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'platform' ) ); ?>

</main>

<script>
(function() {
    var input = document.getElementById('integration-search');
    if (!input) return;
    input.addEventListener('input', function() {
        var q = this.value.toLowerCase().trim();
        var cats = document.querySelectorAll('.integration-category');
        cats.forEach(function(cat) {
            var cards = cat.querySelectorAll('.integration-card');
            var anyVisible = false;
            cards.forEach(function(card) {
                var name = card.getAttribute('data-name') || '';
                var text = card.textContent.toLowerCase();
                var match = !q || name.indexOf(q) !== -1 || text.indexOf(q) !== -1;
                card.style.display = match ? '' : 'none';
                if (match) anyVisible = true;
            });
            cat.style.display = anyVisible ? '' : 'none';
        });
    });
})();
</script>

<?php get_footer();
