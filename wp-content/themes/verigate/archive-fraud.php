<?php
/**
 * Archive — Fraud Prevention Hub
 *
 * @package VeriGate
 */

get_header();
$theme = verigate_get_category_theme( 'fraud' );
?>

<main class="flex-1 pt-16">

    <!-- 1. Hero — Split with illustration -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="space-y-6 animate-on-scroll fade-up">
                    <?php verigate_badge( 'Fraud Prevention', 'fraud' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground mt-4">
                        Fraud Prevention &amp; Detection
                    </h1>
                    <p class="text-xl text-muted-foreground max-w-2xl">
                        Multi-layered fraud detection and prevention built for the South African threat landscape
                    </p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            Schedule Security Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            View Pricing
                        </a>
                    </div>
                </div>
                <div class="hidden lg:flex items-center justify-center">
                    <?php get_template_part( 'template-parts/illustrations/fraud-detection' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- 2. Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- 3. Threat Landscape Overview -->
    <section class="py-16 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">South African Fraud Landscape</h2>
            </div>

            <?php
            $fraud_stats = array(
                array( 'value' => 'R3.2bn+', 'label' => 'Annual fraud losses in SA' ),
                array( 'value' => '40%',     'label' => 'Increase in identity fraud' ),
                array( 'value' => '67%',     'label' => 'Of fraud detected post-hire' ),
                array( 'value' => 'R250K+',  'label' => 'Average cost per fraud incident' ),
            );
            ?>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $fraud_stats as $stat ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="text-3xl md:text-4xl font-bold <?php echo esc_attr( $theme['accent_text'] ); ?> mb-2">
                            <?php echo esc_html( $stat['value'] ); ?>
                        </div>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- 4. Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- 5. Items Grid (WP Loop) -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Fraud Prevention Solutions</h2>
                <p class="text-lg text-muted-foreground">Explore our comprehensive fraud detection capabilities</p>
            </div>
            <?php if ( have_posts() ) : ?>
                <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                    <?php while ( have_posts() ) : the_post(); ?>
                        <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                            'icon'        => get_field( 'icon' ) ?: 'lock',
                            'title'       => get_the_title(),
                            'description' => get_field( 'subtitle' ) ?: get_the_excerpt(),
                            'href'        => get_the_permalink(),
                            'category'    => 'fraud',
                        ) ); ?>
                    <?php endwhile; ?>
                </div>
            <?php else : ?>
                <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
            <?php endif; ?>
        </div>
    </section>

    <!-- 6. How VeriGate Protects You — 3-step -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">How VeriGate Protects You</h2>
                <p class="text-lg text-muted-foreground">Three layers of defence against fraud in your hiring process</p>
            </div>

            <?php
            $protection_steps = array(
                array(
                    'number' => 1,
                    'icon'   => 'fingerprint',
                    'title'  => 'Identity Verification',
                    'desc'   => 'Multi-factor identity checks against DHA records',
                ),
                array(
                    'number' => 2,
                    'icon'   => 'scan-search',
                    'title'  => 'Document Analysis',
                    'desc'   => 'AI-powered document fraud detection and validation',
                ),
                array(
                    'number' => 3,
                    'icon'   => 'activity',
                    'title'  => 'Continuous Monitoring',
                    'desc'   => 'Ongoing monitoring for post-hire fraud signals',
                ),
            );
            ?>
            <div class="grid md:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $protection_steps as $step ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-numbered', null, array(
                        'number'      => $step['number'],
                        'title'       => $step['title'],
                        'description' => $step['desc'],
                        'category'    => 'fraud',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- 7. Security Badges Section -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="hidden lg:flex items-center justify-center animate-on-scroll fade-up">
                    <?php get_template_part( 'template-parts/illustrations/security-lock' ); ?>
                </div>
                <div class="space-y-6 animate-on-scroll fade-up">
                    <h2 class="text-3xl md:text-4xl font-bold text-foreground">Bank-Grade Security</h2>
                    <?php
                    $security_features = array(
                        'AES-256 encryption at rest',
                        'TLS 1.3 in transit',
                        'SOC 2 Type II certified',
                        'ISO 27001 certified',
                        'POPIA compliant data handling',
                    );
                    ?>
                    <ul class="space-y-4">
                        <?php foreach ( $security_features as $feature ) : ?>
                            <li class="flex items-center gap-3">
                                <?php verigate_icon( 'shield-check', 'w-5 h-5 text-accent flex-shrink-0', 20 ); ?>
                                <span class="text-foreground font-medium"><?php echo esc_html( $feature ); ?></span>
                            </li>
                        <?php endforeach; ?>
                    </ul>
                </div>
            </div>
        </div>
    </section>

    <!-- Stats Counter -->
    <?php get_template_part( 'template-parts/sections/stats-counter' ); ?>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'The real-time dashboard and automated reporting have given our compliance team complete visibility. VeriGate\'s accuracy rate speaks for itself.',
        'name'    => 'Kavitha Pillay',
        'role'    => 'Risk & Compliance Lead',
        'company' => 'Old Mutual',
        'photo'   => 'https://images.unsplash.com/photo-1619981970074-d0b06ef1ed83?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'old-mutual.svg',
    ) ); ?>

    <!-- 8. CTA -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'fraud' ) ); ?>

</main>

<?php get_footer();
