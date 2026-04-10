<?php
/**
 * Template Name: Pricing
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <?php get_template_part( 'template-parts/hero/hero-centered', null, array(
        'title'       => 'Simple, Transparent Pricing',
        'description' => 'Pay per verification with volume-based discounts. No hidden fees. All prices in ZAR.',
        'badge'       => 'Pricing',
        'category'    => 'company',
    ) ); ?>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- Pricing Cards -->
    <section class="py-12 px-4">
        <div class="container mx-auto max-w-6xl">
            <?php
            $plans = [
                [
                    'name'        => 'Starter',
                    'description' => 'For small businesses and startups getting started with background screening',
                    'icon'        => 'zap',
                    'price'       => 'R29',
                    'per'         => '/verification',
                    'volume'      => 'Up to 100 verifications/month',
                    'cta'         => 'Get Started',
                    'cta_url'     => home_url( '/request-demo/' ),
                    'highlight'   => false,
                    'popular'     => false,
                    'features'    => [
                        [ 'text' => 'Up to 100 verifications/month', 'included' => true ],
                        [ 'text' => 'Criminal record checks', 'included' => true ],
                        [ 'text' => 'Identity verification (DHA)', 'included' => true ],
                        [ 'text' => 'Email support', 'included' => true ],
                        [ 'text' => 'Dashboard access', 'included' => true ],
                        [ 'text' => 'Basic reporting', 'included' => true ],
                        [ 'text' => 'POPIA-compliant processing', 'included' => true ],
                        [ 'text' => 'Qualification verification', 'included' => false ],
                        [ 'text' => 'Credit screening', 'included' => false ],
                        [ 'text' => 'API access', 'included' => false ],
                        [ 'text' => 'Bulk upload', 'included' => false ],
                        [ 'text' => 'Dedicated account manager', 'included' => false ],
                    ],
                ],
                [
                    'name'        => 'Professional',
                    'description' => 'For growing companies with higher screening volumes and advanced needs',
                    'icon'        => 'building-2',
                    'price'       => 'R22',
                    'per'         => '/verification',
                    'volume'      => 'Up to 1,000 verifications/month',
                    'cta'         => 'Get Started',
                    'cta_url'     => home_url( '/request-demo/' ),
                    'highlight'   => true,
                    'popular'     => true,
                    'features'    => [
                        [ 'text' => 'Up to 1,000 verifications/month', 'included' => true ],
                        [ 'text' => 'All Starter verification types', 'included' => true ],
                        [ 'text' => 'Qualification verification (SAQA)', 'included' => true ],
                        [ 'text' => 'Employment history checks', 'included' => true ],
                        [ 'text' => 'Credit screening (TransUnion, Experian)', 'included' => true ],
                        [ 'text' => 'Priority email & phone support', 'included' => true ],
                        [ 'text' => 'Full API access with webhooks', 'included' => true ],
                        [ 'text' => 'Bulk upload processing', 'included' => true ],
                        [ 'text' => 'Advanced analytics & reporting', 'included' => true ],
                        [ 'text' => 'Custom branding', 'included' => true ],
                        [ 'text' => 'Dedicated account manager', 'included' => false ],
                        [ 'text' => 'Custom workflows', 'included' => false ],
                    ],
                ],
                [
                    'name'        => 'Enterprise',
                    'description' => 'Custom solutions for large organisations with complex screening requirements',
                    'icon'        => 'rocket',
                    'price'       => null,
                    'per'         => '',
                    'volume'      => 'Unlimited verifications',
                    'cta'         => 'Contact Sales',
                    'cta_url'     => home_url( '/contact/' ),
                    'highlight'   => false,
                    'popular'     => false,
                    'features'    => [
                        [ 'text' => 'Unlimited verifications', 'included' => true ],
                        [ 'text' => 'All Professional verification types', 'included' => true ],
                        [ 'text' => 'Business verification (CIPC)', 'included' => true ],
                        [ 'text' => 'International document checks', 'included' => true ],
                        [ 'text' => '24/7 premium support', 'included' => true ],
                        [ 'text' => 'Custom API integration', 'included' => true ],
                        [ 'text' => 'Enterprise analytics & BI export', 'included' => true ],
                        [ 'text' => 'White-label solution', 'included' => true ],
                        [ 'text' => 'Custom workflow builder', 'included' => true ],
                        [ 'text' => 'Dedicated account manager', 'included' => true ],
                        [ 'text' => 'SLA guarantee (99.9% uptime)', 'included' => true ],
                        [ 'text' => 'On-site training & onboarding', 'included' => true ],
                    ],
                ],
            ];
            ?>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <?php foreach ( $plans as $plan ) : ?>
                    <div class="relative flex flex-col bg-card rounded-lg border <?php echo $plan['highlight'] ? 'border-accent shadow-lg scale-105 lg:scale-110' : 'border-border'; ?>">
                        <?php if ( $plan['popular'] ) : ?>
                            <div class="absolute -top-4 left-1/2 -translate-x-1/2 bg-accent text-accent-foreground px-4 py-1 rounded-full text-sm font-semibold">
                                Most Popular
                            </div>
                        <?php endif; ?>

                        <!-- Header -->
                        <div class="text-center pb-8 px-6 pt-8">
                            <div class="mx-auto mb-4 w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                                <?php verigate_icon( $plan['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                            </div>
                            <h3 class="text-2xl font-bold text-foreground"><?php echo esc_html( $plan['name'] ); ?></h3>
                            <p class="text-sm mt-2 text-muted-foreground"><?php echo esc_html( $plan['description'] ); ?></p>
                        </div>

                        <!-- Price -->
                        <div class="flex-grow px-6">
                            <div class="text-center mb-8">
                                <?php if ( $plan['price'] ) : ?>
                                    <div class="flex items-baseline justify-center gap-2">
                                        <span class="text-4xl font-bold text-foreground"><?php echo esc_html( $plan['price'] ); ?></span>
                                        <span class="text-muted-foreground"><?php echo esc_html( $plan['per'] ); ?></span>
                                    </div>
                                    <p class="text-sm text-muted-foreground mt-2"><?php echo esc_html( $plan['volume'] ); ?></p>
                                <?php else : ?>
                                    <div class="text-3xl font-bold text-foreground">Custom</div>
                                    <p class="text-sm text-muted-foreground mt-2">Volume discounts available</p>
                                <?php endif; ?>
                            </div>

                            <!-- Features -->
                            <ul class="space-y-3">
                                <?php foreach ( $plan['features'] as $feature ) : ?>
                                    <li class="flex items-start gap-3 text-sm <?php echo $feature['included'] ? 'text-foreground' : 'text-muted-foreground'; ?>">
                                        <?php verigate_icon( 'check', 'w-5 h-5 flex-shrink-0 mt-0.5 ' . ( $feature['included'] ? 'text-accent' : 'text-muted-foreground/50' ), 20 ); ?>
                                        <span class="<?php echo ! $feature['included'] ? 'line-through' : ''; ?>"><?php echo esc_html( $feature['text'] ); ?></span>
                                    </li>
                                <?php endforeach; ?>
                            </ul>
                        </div>

                        <!-- CTA -->
                        <div class="pt-6 px-6 pb-6">
                            <a href="<?php echo esc_url( $plan['cta_url'] ); ?>" class="w-full inline-flex items-center justify-center px-6 py-3 text-base font-semibold rounded-lg transition-all <?php echo $plan['highlight'] ? 'bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg' : 'border border-border text-foreground hover:bg-accent/5'; ?>">
                                <?php echo esc_html( $plan['cta'] ); ?>
                                <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                            </a>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>

            <div class="text-center mt-8">
                <a href="<?php echo esc_url( home_url( '/compare-plans/' ) ); ?>" class="text-accent hover:underline font-medium">
                    View detailed plan comparison &rarr;
                </a>
            </div>
        </div>
    </section>

    <!-- Trust Indicators Strip -->
    <section class="py-8 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="flex flex-wrap items-center justify-center gap-8 md:gap-12">
                <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/popia.svg' ); ?>" alt="POPIA Compliant" class="h-10 opacity-60">
                <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/iso-27001.svg' ); ?>" alt="ISO 27001 Certified" class="h-10 opacity-60">
                <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/soc2.svg' ); ?>" alt="SOC 2 Type II" class="h-10 opacity-60">
                <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/fica.svg' ); ?>" alt="FICA Compliant" class="h-10 opacity-60">
            </div>
        </div>
    </section>

    <!-- Add-ons Section -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Optional Add-ons</h2>
                <p class="text-lg text-muted-foreground">Enhance your plan with additional features and services</p>
            </div>

            <?php
            $addons = [
                [ 'name' => 'Advanced AML Screening', 'desc' => 'Enhanced screening against SARB sanctions lists, PEPs, and global watchlists', 'price' => 'R8.50 per check' ],
                [ 'name' => 'Ongoing Monitoring', 'desc' => 'Continuous monitoring of verified individuals for criminal records and compliance changes', 'price' => 'R1,500/month' ],
                [ 'name' => 'Premium Support', 'desc' => '24/7 dedicated support with 2-hour response time SLA', 'price' => 'R7,500/month' ],
                [ 'name' => 'Custom Integration', 'desc' => 'Dedicated engineering support for ERP, HRIS, and custom system integrations', 'price' => 'Custom pricing' ],
            ];
            ?>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 stagger-list">
                <?php foreach ( $addons as $addon ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6">
                        <h3 class="text-lg font-bold text-foreground mb-1"><?php echo esc_html( $addon['name'] ); ?></h3>
                        <p class="text-sm text-muted-foreground mb-4"><?php echo esc_html( $addon['desc'] ); ?></p>
                        <div class="flex items-center justify-between">
                            <span class="text-2xl font-bold text-accent"><?php echo esc_html( $addon['price'] ); ?></span>
                            <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-4 py-2 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                                Enquire
                            </a>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Testimonial Quote -->
    <section class="py-16 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl text-center animate-on-scroll fade-up">
            <blockquote class="space-y-6">
                <div class="flex justify-center mb-4">
                    <?php verigate_stars( 5 ); ?>
                </div>
                <p class="text-xl md:text-2xl font-medium text-foreground leading-relaxed">
                    &ldquo;VeriGate&rsquo;s pricing is transparent and the per-check model means we only pay for what we use. The Professional plan paid for itself in the first month.&rdquo;
                </p>
                <footer class="flex flex-col items-center gap-3">
                    <img
                        src="<?php echo esc_url( 'https://images.unsplash.com/photo-1617244147299-5ef406921c35?w=80&h=80&fit=crop&crop=face' ); ?>"
                        alt="Kabelo Mabena"
                        class="w-14 h-14 rounded-full object-cover"
                        width="80"
                        height="80"
                        loading="lazy"
                    >
                    <div>
                        <cite class="not-italic font-semibold text-foreground">Kabelo Mabena</cite>
                        <p class="text-sm text-muted-foreground">People Operations Manager at Capitec</p>
                    </div>
                </footer>
            </blockquote>
        </div>
    </section>

    <!-- FAQ Section -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Pricing FAQ</h2>
                <p class="text-lg text-muted-foreground">Have questions? We've got answers.</p>
            </div>

            <?php
            $faqs = [
                [ 'q' => 'How does per-verification pricing work?', 'a' => 'You pay per verification check completed. The price depends on your plan tier: R29 (Starter), R22 (Professional), or custom rates for Enterprise. Each check type (criminal, qualification, credit, etc.) counts as one verification.' ],
                [ 'q' => 'What payment methods do you accept?', 'a' => 'We accept EFT/bank transfer, debit orders, credit cards (Visa, Mastercard), and can invoice for Enterprise customers. All prices are in ZAR and include VAT.' ],
                [ 'q' => 'Can I switch plans at any time?', 'a' => 'Yes, you can upgrade or downgrade your plan at any time. Changes take effect at the start of your next billing cycle. We\'ll prorate any billing differences.' ],
                [ 'q' => 'What happens if I exceed my monthly limit?', 'a' => 'Overages are billed at a slightly higher per-check rate. We\'ll notify you when you reach 80% of your limit so you can upgrade if needed.' ],
                [ 'q' => 'Is there a setup fee?', 'a' => 'No setup fees for Starter and Professional plans. Enterprise plans may include custom integration services priced separately.' ],
                [ 'q' => 'Are results POPIA-compliant?', 'a' => 'Yes, all verification processing is fully POPIA-compliant. We handle consent management, data minimisation, and secure data handling in accordance with the Protection of Personal Information Act.' ],
            ];
            ?>
            <div class="space-y-6 stagger-list">
                <?php foreach ( $faqs as $faq ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6">
                        <h3 class="text-lg font-bold text-foreground flex items-start gap-3 mb-3">
                            <?php verigate_icon( 'help-circle', 'w-5 h-5 text-accent flex-shrink-0 mt-0.5', 20 ); ?>
                            <span><?php echo esc_html( $faq['q'] ); ?></span>
                        </h3>
                        <p class="text-muted-foreground pl-8"><?php echo esc_html( $faq['a'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Money-Back Guarantee -->
    <section class="py-12 px-4">
        <div class="container mx-auto max-w-3xl text-center animate-on-scroll fade-up">
            <div class="flex flex-col items-center gap-4">
                <?php verigate_icon( 'shield-check', 'w-16 h-16 text-accent', 64 ); ?>
                <h2 class="text-2xl md:text-3xl font-bold text-foreground">Risk-Free</h2>
                <p class="text-lg text-muted-foreground max-w-xl">
                    Start with our Starter plan — no long-term contracts, no setup fees. Upgrade or downgrade at any time.
                </p>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="bg-card border-2 border-accent rounded-lg p-8 md:p-12 bg-gradient-to-br from-primary/5 to-accent/5 text-center animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Ready to Get Started?</h2>
                <p class="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">Join 200+ South African organisations already using VeriGate for background screening.</p>
                <div class="flex flex-col sm:flex-row gap-4 justify-center">
                    <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all min-w-[200px]">
                        Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                    </a>
                    <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all min-w-[200px]">
                        Contact Sales
                    </a>
                </div>
            </div>
        </div>
    </section>

</main>

<?php get_footer();
