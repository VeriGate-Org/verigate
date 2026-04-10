<?php
/**
 * Single — Verification Type Detail
 *
 * @package VeriGate
 */

get_header();

$theme          = verigate_get_category_theme( 'verification' );
$subtitle       = get_field( 'subtitle' );
$badge_text     = get_field( 'badge_text' ) ?: 'Verification Type';
$icon           = get_field( 'icon' ) ?: 'shield-check';
$turnaround     = get_field( 'turnaround_time' );
$description    = get_field( 'description' );
$how_it_works   = get_field( 'how_it_works' );
$features       = get_field( 'features' );
$use_cases      = get_field( 'use_cases' );
$data_sources   = get_field( 'data_sources' );
$related_types  = get_field( 'related_types' );
?>

<main class="flex-1 pt-16">

    <!-- Hero -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( $badge_text, 'verification' ); ?>

                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        <?php the_title(); ?>
                        <?php if ( $subtitle ) : ?>
                            <span class="block mt-2 text-primary"><?php echo esc_html( $subtitle ); ?></span>
                        <?php endif; ?>
                    </h1>

                    <p class="text-xl text-muted-foreground mb-8 max-w-2xl">
                        <?php echo esc_html( $description ?: get_the_excerpt() ); ?>
                    </p>

                    <?php if ( $turnaround ) : ?>
                        <div class="inline-flex items-center gap-2 <?php echo esc_attr( $theme['accent_bg'] ); ?> border <?php echo esc_attr( $theme['badge_border'] ); ?> rounded-full px-5 py-2.5 mb-8">
                            <?php verigate_icon( 'clock', 'w-5 h-5 ' . $theme['accent_text'], 20 ); ?>
                            <span class="text-sm font-semibold text-foreground">Typical Turnaround:</span>
                            <span class="text-sm font-bold <?php echo esc_attr( $theme['accent_text'] ); ?>"><?php echo esc_html( $turnaround ); ?></span>
                        </div>
                    <?php endif; ?>

                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            View Pricing
                        </a>
                    </div>
                </div>

                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/shield-verification' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- How It Works -->
    <?php if ( $how_it_works ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">How It Works</h2>
                <p class="text-lg text-muted-foreground">A simple, streamlined process from request to verified result</p>
            </div>

            <div class="grid md:grid-cols-2 lg:grid-cols-<?php echo min( count( $how_it_works ), 4 ); ?> gap-6 stagger-list">
                <?php foreach ( $how_it_works as $i => $step ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-numbered', null, array(
                        'number'      => $step['step'] ?? ( $i + 1 ),
                        'title'       => $step['title'],
                        'description' => $step['description'],
                        'category'    => 'verification',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <!-- Features -->
    <?php if ( $features ) : ?>
    <section class="py-20 px-4 <?php echo esc_attr( $theme['section_tint'] ); ?>">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Key Features</h2>
            </div>

            <div class="grid md:grid-cols-2 gap-8 stagger-list">
                <?php foreach ( $features as $feature ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 space-y-4">
                        <h3 class="text-xl font-semibold text-foreground"><?php echo esc_html( $feature['title'] ); ?></h3>
                        <p class="text-muted-foreground"><?php echo esc_html( $feature['description'] ); ?></p>
                        <?php if ( ! empty( $feature['items'] ) ) : ?>
                            <ul class="space-y-2">
                                <?php foreach ( $feature['items'] as $item ) : ?>
                                    <li class="flex items-start gap-2 text-sm text-muted-foreground">
                                        <?php verigate_icon( 'check', 'w-4 h-4 text-category-verification flex-shrink-0 mt-0.5', 16 ); ?>
                                        <?php echo esc_html( is_array( $item ) ? $item['item'] : $item ); ?>
                                    </li>
                                <?php endforeach; ?>
                            </ul>
                        <?php endif; ?>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <!-- Use Cases -->
    <?php if ( $use_cases ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Common Use Cases</h2>
            </div>

            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $use_cases as $uc ) : ?>
                    <div class="p-6 border border-border rounded-lg bg-card hover:shadow-md transition-all">
                        <h4 class="font-semibold text-foreground mb-2"><?php echo esc_html( $uc['title'] ); ?></h4>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $uc['description'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <!-- Related Types -->
    <?php
    $related_posts = $related_types ? verigate_resolve_related( $related_types, 'verification' ) : [];
    if ( $related_posts ) : ?>
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl font-bold mb-4 text-foreground">Related Verification Types</h2>
            </div>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $related_posts as $related ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                        'icon'        => get_field( 'icon', $related->ID ) ?: 'shield-check',
                        'title'       => get_the_title( $related ),
                        'description' => get_field( 'subtitle', $related->ID ) ?: get_the_excerpt( $related ),
                        'href'        => get_the_permalink( $related ),
                        'category'    => 'verification',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'verification' ) ); ?>

</main>

<?php get_footer();
