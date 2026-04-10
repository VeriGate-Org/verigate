<?php
/**
 * Single — Fraud Prevention Detail
 *
 * @package VeriGate
 */

get_header();

$theme             = verigate_get_category_theme( 'fraud' );
$subtitle          = get_field( 'subtitle' );
$badge_text        = get_field( 'badge_text' ) ?: 'Fraud Prevention';
$description       = get_field( 'description' );
$threat_indicators = get_field( 'threat_indicators' );
$detection_methods = get_field( 'detection_methods' );
$integration_info  = get_field( 'integration_info' );
$related           = get_field( 'related_prevention' );
?>

<main class="flex-1 pt-16">

    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( $badge_text, 'fraud' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        <?php the_title(); ?>
                        <?php if ( $subtitle ) : ?><span class="block mt-2 text-primary"><?php echo esc_html( $subtitle ); ?></span><?php endif; ?>
                    </h1>
                    <p class="text-xl text-muted-foreground mb-8 max-w-2xl"><?php echo esc_html( $description ?: get_the_excerpt() ); ?></p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">Schedule Security Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?></a>
                    </div>
                </div>
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/fraud-detection' ); ?>
                </div>
            </div>
        </div>
    </section>

    <?php if ( $threat_indicators ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Threat Indicators</h2>
            <div class="grid md:grid-cols-2 gap-6 stagger-list">
                <?php foreach ( $threat_indicators as $ti ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-horizontal', null, array(
                        'icon'        => 'shield',
                        'title'       => $ti['title'],
                        'description' => $ti['description'],
                        'category'    => 'fraud',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $detection_methods ) : ?>
    <section class="py-20 px-4 <?php echo esc_attr( $theme['section_tint'] ); ?>">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Detection Methods</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $detection_methods as $i => $dm ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-numbered', null, array(
                        'number'      => $i + 1,
                        'title'       => $dm['title'],
                        'description' => $dm['description'],
                        'category'    => 'fraud',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $integration_info ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl prose prose-lg max-w-none">
            <h2 class="text-3xl md:text-4xl font-bold mb-8 text-foreground">Integration</h2>
            <?php echo wp_kses_post( $integration_info ); ?>
        </div>
    </section>
    <?php endif; ?>

    <?php
    $related_posts = $related ? verigate_resolve_related( $related, 'fraud' ) : [];
    if ( $related_posts ) : ?>
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl font-bold mb-12 text-foreground">Related Prevention</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $related_posts as $r ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                        'icon'        => get_field( 'icon', $r->ID ) ?: 'lock',
                        'title'       => get_the_title( $r ),
                        'description' => get_field( 'subtitle', $r->ID ) ?: get_the_excerpt( $r ),
                        'href'        => get_the_permalink( $r ),
                        'category'    => 'fraud',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'fraud' ) ); ?>
</main>

<?php get_footer();
