<?php
/**
 * Single — Industry Solution Detail
 *
 * @package VeriGate
 */

get_header();

$theme      = verigate_get_category_theme( 'industry' );
$subtitle   = get_field( 'subtitle' );
$badge_text = get_field( 'badge_text' ) ?: 'Industry Solution';
$description = get_field( 'description' );
$challenges  = get_field( 'challenges' );
$solutions   = get_field( 'solutions' );
$regulations = get_field( 'regulations' );
$use_cases   = get_field( 'use_cases' );
$metrics     = get_field( 'metrics' );
$related     = get_field( 'related_industries' );
?>

<main class="flex-1 pt-16">

    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( $badge_text, 'industry' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        <?php the_title(); ?>
                        <?php if ( $subtitle ) : ?><span class="block mt-2 text-primary"><?php echo esc_html( $subtitle ); ?></span><?php endif; ?>
                    </h1>
                    <p class="text-xl text-muted-foreground mb-8 max-w-2xl"><?php echo esc_html( $description ?: get_the_excerpt() ); ?></p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">Schedule Industry Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?></a>
                        <a href="<?php echo esc_url( home_url( '/roi-calculator/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">ROI Calculator</a>
                    </div>
                </div>
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/industry-network' ); ?>
                </div>
            </div>
        </div>
    </section>

    <?php if ( $challenges ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Industry Challenges</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $challenges as $c ) : ?>
                    <div class="p-6 border border-border rounded-lg bg-card hover:shadow-md transition-all">
                        <h4 class="font-semibold text-foreground mb-2"><?php echo esc_html( $c['title'] ); ?></h4>
                        <p class="text-sm text-muted-foreground mb-3"><?php echo esc_html( $c['description'] ); ?></p>
                        <?php if ( ! empty( $c['impact'] ) ) : ?>
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-amber-50 text-amber-700 border border-amber-200">Impact: <?php echo esc_html( $c['impact'] ); ?></span>
                        <?php endif; ?>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $solutions ) : ?>
    <section class="py-20 px-4 <?php echo esc_attr( $theme['section_tint'] ); ?>">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Our Solutions</h2>
            <div class="space-y-8 stagger-list">
                <?php foreach ( $solutions as $sol ) : ?>
                    <div class="bg-card border border-border rounded-lg p-8">
                        <h3 class="text-xl font-semibold text-foreground mb-3"><?php echo esc_html( $sol['title'] ); ?></h3>
                        <p class="text-muted-foreground mb-4"><?php echo esc_html( $sol['description'] ); ?></p>
                        <?php if ( ! empty( $sol['features'] ) ) : ?>
                            <div class="grid md:grid-cols-2 gap-2 mb-4">
                                <?php foreach ( $sol['features'] as $feat ) : ?>
                                    <div class="flex items-center gap-2 text-sm">
                                        <?php verigate_icon( 'check', 'w-4 h-4 text-category-industry flex-shrink-0', 16 ); ?>
                                        <span class="text-muted-foreground"><?php echo esc_html( is_array( $feat ) ? $feat['feature'] : $feat ); ?></span>
                                    </div>
                                <?php endforeach; ?>
                            </div>
                        <?php endif; ?>
                        <?php if ( ! empty( $sol['result'] ) ) : ?>
                            <div class="mt-4 pt-4 border-t border-border">
                                <span class="text-sm font-medium <?php echo esc_attr( $theme['accent_text'] ); ?>">Result: <?php echo esc_html( $sol['result'] ); ?></span>
                            </div>
                        <?php endif; ?>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $metrics ) : ?>
    <section class="py-20 px-4 bg-primary text-primary-foreground">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 animate-on-scroll fade-up">Results &amp; Metrics</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-<?php echo min( count( $metrics ), 4 ); ?> gap-6 stagger-list">
                <?php foreach ( $metrics as $m ) : ?>
                    <div class="text-center p-6">
                        <div class="text-3xl font-bold text-accent mb-2"><?php echo esc_html( $m['improvement'] ); ?></div>
                        <div class="font-semibold mb-2"><?php echo esc_html( $m['metric'] ); ?></div>
                        <div class="text-sm text-primary-foreground/60">
                            <?php echo esc_html( $m['before'] ); ?> &rarr; <?php echo esc_html( $m['after'] ); ?>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php
    $related_posts = $related ? verigate_resolve_related( $related, 'industry' ) : [];
    if ( $related_posts ) : ?>
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Related Industries</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $related_posts as $r ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                        'icon'        => get_field( 'icon', $r->ID ) ?: 'globe',
                        'title'       => get_the_title( $r ),
                        'description' => get_field( 'subtitle', $r->ID ) ?: get_the_excerpt( $r ),
                        'href'        => get_the_permalink( $r ),
                        'category'    => 'industry',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'industry' ) ); ?>
</main>

<?php get_footer();
