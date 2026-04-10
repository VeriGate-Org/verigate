<?php
/**
 * Archive — Industry Solutions Hub
 *
 * @package VeriGate
 */

get_header();
$theme = verigate_get_category_theme( 'industry' );
?>

<main class="flex-1 pt-16">

    <!-- 1. Hero — Split with illustration -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="space-y-6 animate-on-scroll fade-up">
                    <?php verigate_badge( 'Industry Solutions', 'industry' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground mt-4">
                        Solutions by Industry
                    </h1>
                    <p class="text-xl text-muted-foreground max-w-2xl">
                        Tailored verification and compliance solutions for every sector of the South African economy
                    </p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            Request Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="<?php echo esc_url( home_url( '/roi-calculator/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            ROI Calculator
                        </a>
                    </div>
                </div>
                <div class="hidden lg:flex items-center justify-center">
                    <?php get_template_part( 'template-parts/illustrations/industry-network' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- 2. Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- 3. Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- 4. Items Grid (WP Loop) -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">All Industries</h2>
                <p class="text-lg text-muted-foreground">Find the right verification solution for your sector</p>
            </div>
            <?php if ( have_posts() ) : ?>
                <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                    <?php while ( have_posts() ) : the_post(); ?>
                        <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                            'icon'        => get_field( 'icon' ) ?: 'globe',
                            'title'       => get_the_title(),
                            'description' => get_field( 'subtitle' ) ?: get_the_excerpt(),
                            'href'        => get_the_permalink(),
                            'category'    => 'industry',
                        ) ); ?>
                    <?php endwhile; ?>
                </div>
            <?php else : ?>
                <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
            <?php endif; ?>
        </div>
    </section>

    <!-- 5. Why Industry-Specific Solutions -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Why Industry-Specific Solutions</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">Purpose-built verification for every sector</p>
            </div>

            <?php
            $benefits = array(
                array(
                    'icon'  => 'target',
                    'title' => 'Tailored Checks',
                    'desc'  => 'Verification packages designed for your industry\'s specific risk profile',
                ),
                array(
                    'icon'  => 'shield-check',
                    'title' => 'Regulatory Expertise',
                    'desc'  => 'Deep knowledge of sector-specific compliance requirements',
                ),
                array(
                    'icon'  => 'zap',
                    'title' => 'Faster Turnaround',
                    'desc'  => 'Industry-optimised workflows for faster results',
                ),
                array(
                    'icon'  => 'bar-chart-3',
                    'title' => 'Proven Results',
                    'desc'  => 'Measurable ROI across every industry we serve',
                ),
            );
            ?>
            <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $benefits as $benefit ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center hover:shadow-lg transition-shadow">
                        <div class="mx-auto w-14 h-14 rounded-lg <?php echo esc_attr( $theme['icon_bg'] ); ?> flex items-center justify-center mb-4">
                            <?php verigate_icon( $benefit['icon'], 'w-7 h-7 ' . $theme['icon_color'], 28 ); ?>
                        </div>
                        <h3 class="text-lg font-bold text-foreground mb-2"><?php echo esc_html( $benefit['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $benefit['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Stats Counter -->
    <?php get_template_part( 'template-parts/sections/stats-counter' ); ?>

    <!-- 6. Testimonials -->
    <?php get_template_part( 'template-parts/sections/testimonials' ); ?>

    <!-- 7. CTA -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'industry' ) ); ?>

</main>

<?php get_footer();
