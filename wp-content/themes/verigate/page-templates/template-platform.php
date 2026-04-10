<?php
/**
 * Template Name: Platform
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero — 2-column with illustration -->
    <section class="relative pt-16 pb-20 overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-50"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="space-y-6 animate-on-scroll fade-up">
                    <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-secondary text-secondary-foreground">
                        VeriGate Platform
                    </span>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                        One Platform for All Your
                        <span class="block text-accent mt-2">Background Screening Needs</span>
                    </h1>
                    <p class="text-xl text-muted-foreground max-w-3xl">
                        Six powerful modules working together to deliver fast, accurate, and POPIA-compliant background verification for South African organisations. From single checks to enterprise-scale batch processing.
                    </p>
                    <div class="flex gap-4 flex-wrap">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            Request a Demo
                        </a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            View Pricing
                        </a>
                    </div>
                </div>
                <div class="hidden lg:flex items-center justify-center">
                    <?php get_template_part( 'template-parts/illustrations/platform-dashboard' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Platform Stats -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-8 animate-on-scroll fade-up">
                <?php
                $platform_stats = [
                    [ 'value' => '6', 'label' => 'Core Modules' ],
                    [ 'value' => '200+', 'label' => 'Active Clients' ],
                    [ 'value' => '99.2%', 'label' => 'Accuracy Rate' ],
                    [ 'value' => '99.9%', 'label' => 'Uptime SLA' ],
                ];
                foreach ( $platform_stats as $stat ) :
                ?>
                    <div class="text-center">
                        <div class="text-4xl md:text-5xl font-bold text-accent mb-2"><?php echo esc_html( $stat['value'] ); ?></div>
                        <div class="text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- Platform Modules Grid -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Platform Modules</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">Six integrated modules that cover every aspect of the background screening lifecycle</p>
            </div>

            <?php
            $modules = [
                [
                    'icon'        => 'layout-dashboard',
                    'title'       => 'Dashboard',
                    'description' => 'Central hub for managing all verifications, tracking progress, and viewing analytics across your organisation.',
                    'features'    => [
                        'Real-time verification status overview with live counters',
                        'Team activity feed and user-level audit logs',
                        'Customisable widgets and role-based views',
                        'Exportable reports and scheduled email summaries',
                    ],
                ],
                [
                    'icon'        => 'code',
                    'title'       => 'API Gateway',
                    'description' => 'Production-ready RESTful API with OAuth 2.0 authentication, webhooks, rate limiting, and SDKs for major languages.',
                    'features'    => [
                        'OAuth 2.0 and API key authentication with scoped permissions',
                        'Real-time webhooks with automatic retry and signature verification',
                        'Rate limiting with configurable thresholds per endpoint',
                        'Official SDKs for Python, Node.js, Java, C#, PHP, and Ruby',
                    ],
                ],
                [
                    'icon'        => 'upload',
                    'title'       => 'Bulk Processing',
                    'description' => 'CSV upload and batch processing with async verification for high-volume clients processing thousands of checks.',
                    'features'    => [
                        'Drag-and-drop CSV upload with template validation',
                        'Batch processing up to 10,000 records per upload',
                        'Async verification with progress tracking and email notifications',
                        'Downloadable results in CSV, Excel, and PDF formats',
                    ],
                ],
                [
                    'icon'        => 'clipboard-list',
                    'title'       => 'Case Tracking',
                    'description' => 'Real-time verification status tracking, comprehensive audit trails, and quality assurance workflow management.',
                    'features'    => [
                        'Granular status updates from submission to completion',
                        'Full audit trail with timestamps and user attribution',
                        'QA review workflow with approval and rejection actions',
                        'Case notes, internal comments, and document attachments',
                    ],
                ],
                [
                    'icon'        => 'git-branch',
                    'title'       => 'Workflow Builder',
                    'description' => 'Create custom verification workflows with conditional logic, approval chains, and automated routing rules.',
                    'features'    => [
                        'Visual drag-and-drop workflow designer',
                        'Conditional logic based on verification results or risk scores',
                        'Multi-level approval chains with escalation rules',
                        'Reusable workflow templates for common screening scenarios',
                    ],
                ],
                [
                    'icon'        => 'shield-check',
                    'title'       => 'Compliance Engine',
                    'description' => 'Automated POPIA and FICA compliance, consent management, data retention policies, and regulatory reporting.',
                    'features'    => [
                        'Automated POPIA consent capture and record-keeping',
                        'FICA compliance checks with CDD/EDD workflows',
                        'Configurable data retention and automatic purging schedules',
                        'Regulatory audit reports with one-click export',
                    ],
                ],
            ];

            $bullet_icons = [ 'sparkles', 'circle-dot', 'arrow-right-circle', 'star' ];
            ?>
            <div class="grid md:grid-cols-2 gap-8 stagger-list">
                <?php foreach ( $modules as $module ) : ?>
                    <div class="bg-card border border-border rounded-lg hover:shadow-lg transition-shadow overflow-hidden">
                        <div class="p-6">
                            <div class="flex items-center gap-3 mb-3">
                                <div class="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                                    <?php verigate_icon( $module['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                                </div>
                                <h3 class="text-xl font-bold text-foreground"><?php echo esc_html( $module['title'] ); ?></h3>
                            </div>
                            <p class="text-base text-muted-foreground mb-4"><?php echo esc_html( $module['description'] ); ?></p>
                            <ul class="space-y-3">
                                <?php foreach ( $module['features'] as $idx => $feature ) : ?>
                                    <li class="flex items-start gap-2 text-sm">
                                        <?php verigate_icon( $bullet_icons[ $idx % 4 ], 'w-4 h-4 text-accent flex-shrink-0 mt-0.5', 16 ); ?>
                                        <span class="text-foreground"><?php echo esc_html( $feature ); ?></span>
                                    </li>
                                <?php endforeach; ?>
                            </ul>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Why Choose VeriGate -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Why Choose VeriGate</h2>
                <p class="text-lg text-muted-foreground">Built for South African compliance, designed for enterprise scale</p>
            </div>

            <?php
            $features = [
                [ 'icon' => 'zap', 'title' => 'Sub-24hr Turnaround', 'desc' => 'Most verifications completed within 24 hours, with priority processing available for urgent checks.' ],
                [ 'icon' => 'lock', 'title' => 'Bank-Grade Security', 'desc' => 'ISO 27001 and SOC 2 Type II certified with end-to-end AES-256 encryption and TLS 1.3.' ],
                [ 'icon' => 'bar-chart-3', 'title' => 'Real-Time Analytics', 'desc' => 'Live dashboards with volume trends, turnaround metrics, compliance scores, and team performance.' ],
                [ 'icon' => 'globe', 'title' => 'SA Data Sources', 'desc' => 'Direct integrations with DHA, SAPS, SAQA, CIPC, TransUnion, Experian, and XDS.' ],
            ];
            ?>
            <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $features as $f ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="mx-auto w-16 h-16 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                            <?php verigate_icon( $f['icon'], 'w-8 h-8 text-accent', 32 ); ?>
                        </div>
                        <h3 class="text-lg font-bold text-foreground mb-2"><?php echo esc_html( $f['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $f['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Integrations Section -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="grid md:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-6">Integrates with Your Existing Systems</h2>
                    <p class="text-lg text-muted-foreground mb-6">VeriGate connects seamlessly with the HR, payroll, and recruitment tools your team already uses. Our open API and pre-built connectors make integration straightforward.</p>
                    <div class="space-y-3 mb-8">
                        <?php
                        $integration_points = [
                            'HRIS and Payroll systems (SAP SuccessFactors, Sage, PaySpace)',
                            'Applicant Tracking Systems (BambooHR, Workday, Lever)',
                            'Custom ERP and CRM integrations via REST API',
                            'Zapier and Make (Integromat) for no-code workflows',
                        ];
                        foreach ( $integration_points as $point ) :
                        ?>
                            <div class="flex items-start gap-2 text-sm">
                                <?php verigate_icon( 'arrow-right-circle', 'w-4 h-4 text-accent flex-shrink-0 mt-0.5', 16 ); ?>
                                <span class="text-foreground"><?php echo esc_html( $point ); ?></span>
                            </div>
                        <?php endforeach; ?>
                    </div>
                    <a href="<?php echo esc_url( home_url( '/integrations/' ) ); ?>" class="inline-flex items-center justify-center px-6 py-2.5 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                        View All Integrations <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                    </a>
                </div>
                <div class="grid grid-cols-2 gap-4 stagger-list">
                    <?php
                    $integrations = [
                        [ 'name' => 'SAP SuccessFactors', 'logo' => 'sap.svg', 'category' => 'HRIS' ],
                        [ 'name' => 'BambooHR', 'logo' => 'bamboohr.svg', 'category' => 'ATS' ],
                        [ 'name' => 'Sage', 'logo' => 'sage.svg', 'category' => 'Payroll' ],
                        [ 'name' => 'PaySpace', 'logo' => 'payspace.svg', 'category' => 'Payroll' ],
                        [ 'name' => 'Zapier', 'logo' => 'zapier.svg', 'category' => 'Automation' ],
                        [ 'name' => 'Slack', 'logo' => 'slack.svg', 'category' => 'Communication' ],
                    ];
                    foreach ( $integrations as $int ) :
                    ?>
                        <div class="bg-card border border-border rounded-lg p-6 text-center">
                            <div class="flex flex-col items-center gap-3">
                                <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/integrations/' . $int['logo'] ); ?>"
                                     alt="<?php echo esc_attr( $int['name'] ); ?>"
                                     class="h-8 w-auto max-w-[120px] object-contain">
                                <div>
                                    <div class="font-semibold mb-1 text-sm text-foreground"><?php echo esc_html( $int['name'] ); ?></div>
                                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-secondary text-secondary-foreground"><?php echo esc_html( $int['category'] ); ?></span>
                                </div>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Testimonial Quote -->
    <section class="py-16 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl animate-on-scroll fade-up">
            <div class="text-center space-y-6">
                <!-- Stars -->
                <div class="flex items-center justify-center gap-1">
                    <?php verigate_stars( 5 ); ?>
                </div>

                <!-- Quote -->
                <blockquote class="text-xl md:text-2xl font-medium text-foreground leading-relaxed">
                    <?php verigate_icon( 'quote', 'w-8 h-8 text-accent/30 mx-auto mb-4', 32 ); ?>
                    &ldquo;The platform&rsquo;s six modules work seamlessly together. We went from manual spreadsheet tracking to a fully automated verification pipeline in just two weeks.&rdquo;
                </blockquote>

                <!-- Author -->
                <div class="flex items-center justify-center gap-4">
                    <img src="<?php echo esc_url( 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&crop=face' ); ?>"
                         alt="Johan van der Merwe"
                         class="w-12 h-12 rounded-full object-cover ring-2 ring-accent/20"
                         loading="lazy">
                    <div class="text-left">
                        <div class="font-semibold text-foreground">Johan van der Merwe</div>
                        <div class="text-sm text-muted-foreground">Talent Acquisition Director at Vodacom</div>
                    </div>
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/vodacom.svg' ); ?>"
                         alt="Vodacom"
                         class="h-6 w-auto opacity-50 ml-2 hidden sm:block"
                         loading="lazy">
                </div>
            </div>
        </div>
    </section>

    <!-- SA Data Sources -->
    <?php get_template_part( 'template-parts/sections/data-sources' ); ?>

    <!-- CTA Section -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="bg-card border-2 border-accent rounded-lg p-8 md:p-12 bg-gradient-to-br from-primary/5 to-accent/5 text-center animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">See the Full Platform in Action</h2>
                <p class="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">Book a personalised walkthrough of all six modules and see how VeriGate integrates with your existing systems.</p>
                <div class="flex flex-col sm:flex-row gap-4 justify-center">
                    <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all min-w-[200px]">
                        Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                    </a>
                    <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all min-w-[200px]">
                        Talk to an Engineer
                    </a>
                </div>
            </div>
        </div>
    </section>

</main>

<?php get_footer();
