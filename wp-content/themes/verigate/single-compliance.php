<?php
/**
 * Single — Compliance Type Detail
 *
 * @package VeriGate
 */

get_header();

$theme         = verigate_get_category_theme( 'compliance' );
$subtitle      = get_field( 'subtitle' );
$badge_text    = get_field( 'badge_text' ) ?: 'Compliance';
$description   = get_field( 'description' );
$requirements  = get_field( 'requirements' );
$benefits      = get_field( 'benefits' );
$process_steps = get_field( 'process_steps' );
$related       = get_field( 'related_compliance' );
?>

<main class="flex-1 pt-16">

    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( $badge_text, 'compliance' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        <?php the_title(); ?>
                        <?php if ( $subtitle ) : ?><span class="block mt-2 text-primary"><?php echo esc_html( $subtitle ); ?></span><?php endif; ?>
                    </h1>
                    <p class="text-xl text-muted-foreground mb-8 max-w-2xl"><?php echo esc_html( $description ?: get_the_excerpt() ); ?></p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?></a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">View Pricing</a>
                    </div>
                </div>
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/compliance-flow' ); ?>
                </div>
            </div>
        </div>
    </section>

    <?php if ( $requirements ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Requirements</h2>
            <div class="grid md:grid-cols-2 gap-6 stagger-list">
                <?php foreach ( $requirements as $req ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-horizontal', null, array(
                        'icon'        => 'check',
                        'title'       => $req['title'],
                        'description' => $req['description'],
                        'category'    => 'compliance',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $benefits ) : ?>
    <section class="py-20 px-4 <?php echo esc_attr( $theme['section_tint'] ); ?>">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Benefits</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $benefits as $benefit ) : ?>
                    <div class="p-6 border border-border rounded-lg bg-card">
                        <h4 class="font-semibold text-foreground mb-2"><?php echo esc_html( $benefit['title'] ); ?></h4>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $benefit['description'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php if ( $process_steps ) : ?>
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl md:text-4xl font-bold mb-12 text-foreground animate-on-scroll fade-up">How It Works</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-<?php echo min( count( $process_steps ), 4 ); ?> gap-6 stagger-list">
                <?php foreach ( $process_steps as $i => $step ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card-numbered', null, array(
                        'number'      => $i + 1,
                        'title'       => $step['title'],
                        'description' => $step['description'],
                        'category'    => 'compliance',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php
    $related_posts = $related ? verigate_resolve_related( $related, 'compliance' ) : [];
    if ( $related_posts ) : ?>
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <h2 class="text-3xl font-bold mb-12 text-foreground animate-on-scroll fade-up">Related Compliance</h2>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php foreach ( $related_posts as $r ) : ?>
                    <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                        'icon'        => get_field( 'icon', $r->ID ) ?: 'shield-check',
                        'title'       => get_the_title( $r ),
                        'description' => get_field( 'subtitle', $r->ID ) ?: get_the_excerpt( $r ),
                        'href'        => get_the_permalink( $r ),
                        'category'    => 'compliance',
                    ) ); ?>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'compliance' ) ); ?>
</main>

<?php get_footer();
