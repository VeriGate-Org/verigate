<?php
/**
 * Archive — Verification Types Hub
 *
 * Full-featured archive with split hero, trust signals, data sources,
 * how-it-works steps, stats counter, and CTA.
 *
 * @package VeriGate
 */

get_header();

$theme = verigate_get_category_theme( 'verification' );
?>

<main class="flex-1 pt-16">

    <!-- ─── Hero (split layout) ─── -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">

                <!-- Left — Copy + CTAs -->
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( 'Verification Types', 'verification' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        Background Screening &amp; Verification
                    </h1>
                    <p class="text-xl text-muted-foreground mb-8 max-w-xl">
                        Comprehensive verification services powered by direct integrations with South Africa's authoritative data sources
                    </p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all duration-200">
                            Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all duration-200">
                            View Pricing
                        </a>
                    </div>
                </div>

                <!-- Right — Illustration (hidden on mobile) -->
                <div class="hidden lg:flex items-center justify-center animate-on-scroll fade-up" style="animation-delay: .15s;">
                    <?php get_template_part( 'template-parts/illustrations/shield-verification' ); ?>
                </div>

            </div>
        </div>
    </section>

    <!-- ─── Trust Bar ─── -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- ─── Customer Logos (compact) ─── -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- ─── Items Grid ─── -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Explore All Verification Types</h2>
                <p class="text-lg text-muted-foreground">Browse our complete range of background screening services</p>
            </div>

            <?php if ( have_posts() ) : ?>
                <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                    <?php while ( have_posts() ) : the_post();
                        $subtitle = get_field( 'subtitle' );
                        $icon     = get_field( 'icon' ) ?: 'shield-check';
                    ?>
                        <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                            'icon'        => $icon,
                            'title'       => get_the_title(),
                            'description' => $subtitle ?: get_the_excerpt(),
                            'href'        => get_the_permalink(),
                            'category'    => 'verification',
                        ) ); ?>
                    <?php endwhile; ?>
                </div>
            <?php else : ?>
                <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
            <?php endif; ?>
        </div>
    </section>

    <!-- ─── Data Sources ─── -->
    <?php get_template_part( 'template-parts/sections/data-sources' ); ?>

    <!-- ─── How It Works (3-step) ─── -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">How It Works</h2>
                <p class="text-lg text-muted-foreground">Three simple steps from request to verified results</p>
            </div>

            <div class="grid md:grid-cols-3 gap-8 stagger-list">
                <?php
                $steps = array(
                    array(
                        'number'      => 1,
                        'title'       => 'Submit Request',
                        'description' => 'Upload candidate details via our dashboard, API, or bulk import. We accept ID numbers, names, and supporting documents.',
                    ),
                    array(
                        'number'      => 2,
                        'title'       => 'We Verify',
                        'description' => 'Our platform queries authoritative South African data sources in real time, cross-referencing multiple databases for accuracy.',
                    ),
                    array(
                        'number'      => 3,
                        'title'       => 'Get Results',
                        'description' => 'Receive a comprehensive, audit-ready verification report delivered to your dashboard, email, or integrated ATS.',
                    ),
                );

                foreach ( $steps as $step ) :
                    get_template_part( 'template-parts/cards/feature-card-numbered', null, array(
                        'number'      => $step['number'],
                        'title'       => $step['title'],
                        'description' => $step['description'],
                        'category'    => 'verification',
                    ) );
                endforeach;
                ?>
            </div>
        </div>
    </section>

    <!-- ─── Stats Counter ─── -->
    <?php get_template_part( 'template-parts/sections/stats-counter' ); ?>

    <!-- ─── CTA ─── -->
    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'VeriGate has transformed our hiring process. What used to take weeks now takes hours. The accuracy and compliance features give us complete confidence in every verification.',
        'name'    => 'Sipho Molefe',
        'role'    => 'Head of HR',
        'company' => 'Standard Bank',
        'photo'   => 'https://images.unsplash.com/photo-1586232902955-df204f34b36e?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'standard-bank.png',
    ) ); ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'verification' ) ); ?>

</main>

<?php get_footer();
