<?php
/**
 * Template Name: Partner Program
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background to-accent/5 relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4 text-center">
            <div class="animate-on-scroll fade-up space-y-6">
                <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                    VeriGate Partner Program
                </h1>
                <p class="text-xl text-muted-foreground max-w-2xl mx-auto">
                    Join South Africa's leading background screening partner ecosystem. Whether you build technology, resell solutions, or refer clients, there is a partnership model designed for you.
                </p>
                <div class="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                    <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                        Apply to Partner Program <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                    </a>
                    <a href="#partner-types" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                        Explore Partner Types
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Quick Stats -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-8 animate-on-scroll fade-up">
                <?php
                $stats = [
                    [ 'value' => '50+', 'label' => 'Active Partners' ],
                    [ 'value' => 'R5M+', 'label' => 'Partner Revenue (2025)' ],
                    [ 'value' => '3', 'label' => 'Partner Types' ],
                    [ 'value' => '200+', 'label' => 'Joint Clients' ],
                ];
                foreach ( $stats as $stat ) :
                ?>
                    <div class="text-center">
                        <div class="text-4xl md:text-5xl font-bold text-accent mb-2"><?php echo esc_html( $stat['value'] ); ?></div>
                        <div class="text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Partner Types -->
    <section id="partner-types" class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Choose Your Partnership Model</h2>
                <p class="text-lg text-muted-foreground">Three distinct programmes designed for different types of partners</p>
            </div>

            <div class="space-y-8 stagger-list">
                <?php
                $partner_types = [
                    [
                        'type'        => 'Technology Partners',
                        'icon'        => 'globe',
                        'description' => "Integrate VeriGate's verification API directly into your platform to offer background screening as a native feature for your customers.",
                        'ideal_for'   => 'HRIS providers, ATS platforms, payroll software, recruitment technology providers, and CRM systems operating in South Africa.',
                        'benefits'    => [
                            'Full API and SDK integration support with dedicated engineering resources',
                            'Co-branded documentation and joint marketing materials',
                            'Joint go-to-market campaigns and event sponsorships',
                            'Early access to new API endpoints, features, and beta programmes',
                            'Technical sandbox environment for development and testing',
                        ],
                    ],
                    [
                        'type'        => 'Reseller Partners',
                        'icon'        => 'users',
                        'description' => 'Sell VeriGate\'s background screening solutions to your client base under your brand or alongside your existing service offerings.',
                        'ideal_for'   => 'System integrators, IT consultancies, managed service providers, HR consulting firms, and value-added resellers across South Africa.',
                        'benefits'    => [
                            'Volume-based pricing with attractive margins',
                            'Dedicated partner manager and sales enablement resources',
                            'Deal registration programme with pipeline protection',
                            'White-label options for enterprise resellers',
                            'Quarterly business reviews and growth planning sessions',
                        ],
                    ],
                    [
                        'type'        => 'Referral Partners',
                        'icon'        => 'user-check',
                        'description' => 'Earn commission by referring clients to VeriGate. Ideal for professionals who advise South African businesses on HR, compliance, and risk.',
                        'ideal_for'   => 'Accountants, HR consultants, legal firms, B-BBEE advisors, industry associations, and business consultants across South Africa.',
                        'benefits'    => [
                            'Competitive referral commission on every closed deal',
                            'Simple referral process via the partner portal',
                            'No technical integration or sales quota required',
                            'Fast commission payouts via EFT within 30 days',
                            'Dedicated partner support contact for all queries',
                        ],
                    ],
                ];
                foreach ( $partner_types as $partner ) :
                ?>
                    <div class="bg-card border border-border rounded-lg hover:shadow-lg transition-shadow overflow-hidden">
                        <div class="p-6 border-b border-border">
                            <div class="flex items-center gap-3 mb-3">
                                <div class="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                                    <?php verigate_icon( $partner['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                                </div>
                                <h3 class="text-xl font-bold text-foreground"><?php echo esc_html( $partner['type'] ); ?></h3>
                            </div>
                            <p class="text-muted-foreground"><?php echo esc_html( $partner['description'] ); ?></p>
                        </div>
                        <div class="p-6">
                            <div class="grid md:grid-cols-2 gap-6">
                                <div>
                                    <h4 class="text-sm font-semibold text-foreground mb-3">Benefits:</h4>
                                    <ul class="space-y-2">
                                        <?php foreach ( $partner['benefits'] as $benefit ) : ?>
                                            <li class="flex items-start gap-2 text-sm">
                                                <?php verigate_icon( 'badge-check', 'w-4 h-4 text-accent flex-shrink-0 mt-0.5', 16 ); ?>
                                                <span class="text-muted-foreground"><?php echo esc_html( $benefit ); ?></span>
                                            </li>
                                        <?php endforeach; ?>
                                    </ul>
                                </div>
                                <div>
                                    <h4 class="text-sm font-semibold text-foreground mb-3">Ideal for:</h4>
                                    <p class="text-sm text-muted-foreground"><?php echo esc_html( $partner['ideal_for'] ); ?></p>
                                </div>
                            </div>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Partner Benefits -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Partner Benefits</h2>
                <p class="text-lg text-muted-foreground">Every VeriGate partner receives comprehensive support to grow their business</p>
            </div>

            <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php
                $benefits = [
                    [ 'icon' => 'credit-card', 'title' => 'Revenue Share', 'desc' => 'Earn competitive commissions and recurring revenue from every client you bring to VeriGate. Transparent payout structure with no hidden caps.' ],
                    [ 'icon' => 'bell', 'title' => 'Co-Marketing', 'desc' => "Joint webinars, case studies, blog features, and event sponsorships to grow your brand alongside ours in the South African market." ],
                    [ 'icon' => 'shield-check', 'title' => 'Dedicated Support', 'desc' => 'A named partner manager plus priority access to technical support, integration engineers, and product specialists.' ],
                    [ 'icon' => 'activity', 'title' => 'Partner Portal', 'desc' => 'A dedicated portal to track referrals, commissions, leads, marketing assets, and access technical documentation and training materials.' ],
                ];
                foreach ( $benefits as $b ) :
                ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="mx-auto w-14 h-14 rounded-full bg-accent/10 flex items-center justify-center mb-4">
                            <?php verigate_icon( $b['icon'], 'w-7 h-7 text-accent', 28 ); ?>
                        </div>
                        <h3 class="text-lg font-bold text-foreground mb-2"><?php echo esc_html( $b['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $b['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- How to Apply -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">How to Apply</h2>
                <p class="text-lg text-muted-foreground">Getting started is simple. Three steps to join the VeriGate partner program.</p>
            </div>

            <div class="grid md:grid-cols-3 gap-8 stagger-list">
                <?php
                $steps = [
                    [ 'num' => 1, 'title' => 'Apply', 'desc' => 'Complete the partner application form with details about your business, target market, and partnership goals. Applications typically take 5 minutes.' ],
                    [ 'num' => 2, 'title' => 'Review & Align', 'desc' => 'Our partner team will review your application within 5 business days. We will schedule a call to discuss alignment, mutual goals, and partnership terms.' ],
                    [ 'num' => 3, 'title' => 'Onboard & Launch', 'desc' => 'Once approved, you receive access to the partner portal, technical documentation, marketing materials, and a dedicated partner manager to help you get started.' ],
                ];
                foreach ( $steps as $step ) :
                ?>
                    <div class="text-center">
                        <div class="mx-auto w-16 h-16 rounded-full bg-accent text-white font-bold flex items-center justify-center text-2xl mb-4">
                            <?php echo esc_html( $step['num'] ); ?>
                        </div>
                        <h3 class="text-xl font-bold text-foreground mb-2"><?php echo esc_html( $step['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $step['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Trusted Platforms -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Trusted by Leading Platforms</h2>
                <p class="text-lg text-muted-foreground">We partner with established technology providers to deliver seamless verification experiences</p>
            </div>

            <div class="grid md:grid-cols-3 gap-6 stagger-list">
                <?php
                $platforms = [
                    [ 'name' => 'Zoho', 'logo' => 'zoho.svg', 'desc' => "VeriGate integrates with Zoho Recruit and Zoho People, enabling seamless background checks within Zoho's HR ecosystem." ],
                    [ 'name' => 'SAP SuccessFactors', 'logo' => 'sap.svg', 'desc' => 'Our SAP integration allows enterprise clients to trigger verifications directly from their SAP talent management workflows.' ],
                    [ 'name' => 'Sage', 'logo' => 'sage.svg', 'desc' => 'VeriGate connects with Sage 300 People and Sage Business Cloud Payroll for automated pre-employment screening.' ],
                ];
                foreach ( $platforms as $p ) :
                ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="mx-auto h-16 flex items-center justify-center mb-4">
                            <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/integrations/' . $p['logo'] ); ?>"
                                 alt="<?php echo esc_attr( $p['name'] ); ?>"
                                 class="h-12 w-auto max-w-[160px] object-contain">
                        </div>
                        <h3 class="text-xl font-bold text-foreground mb-2"><?php echo esc_html( $p['name'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $p['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- CTA -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl text-center">
            <div class="animate-on-scroll fade-up">
                <div class="bg-card border-2 border-accent rounded-lg p-8 md:p-12 bg-gradient-to-br from-primary/5 to-accent/5">
                    <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Ready to Partner with VeriGate?</h2>
                    <p class="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
                        Join 50+ partners growing their business with South Africa's leading verification platform. Apply today and our partner team will be in touch within 5 business days.
                    </p>
                    <div class="flex flex-col sm:flex-row gap-4 justify-center">
                        <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all min-w-[200px]">
                            Apply to Partner Program <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="mailto:partners@verigate.co.za" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all min-w-[200px]">
                            Email partners@verigate.co.za
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

</main>

<?php get_footer();
