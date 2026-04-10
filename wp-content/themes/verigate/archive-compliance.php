<?php
/**
 * Archive — Compliance Hub
 *
 * Full-featured archive with split hero, trust signals, certification badges,
 * customer logos, key regulations, stats counter, and CTA.
 *
 * @package VeriGate
 */

get_header();

$theme = verigate_get_category_theme( 'compliance' );
?>

<main class="flex-1 pt-16">

    <!-- ─── Hero (split layout) ─── -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?> relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">

                <!-- Left — Copy + CTAs -->
                <div class="animate-on-scroll fade-up">
                    <?php verigate_badge( 'Compliance', 'compliance' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-foreground mt-4">
                        Regulatory Compliance
                    </h1>
                    <p class="text-xl text-muted-foreground mb-8 max-w-xl">
                        Stay compliant with South African regulations including POPIA, FICA, and industry-specific requirements
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
                    <?php get_template_part( 'template-parts/illustrations/compliance-flow' ); ?>
                </div>

            </div>
        </div>
    </section>

    <!-- ─── Trust Bar ─── -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- ─── Certification Badges ─── -->
    <?php
    $cert_base = VERIGATE_URI . '/assets/img/logos/certifications/';

    $certifications = array(
        array( 'name' => 'POPIA Compliant',    'file' => 'popia.svg',      'status' => 'Fully Compliant' ),
        array( 'name' => 'ISO 27001 Certified', 'file' => 'iso-27001.svg', 'status' => 'Certified' ),
        array( 'name' => 'SOC 2 Compliant',    'file' => 'soc2.svg',       'status' => 'Type II' ),
        array( 'name' => 'FICA Compliant',     'file' => 'fica.svg',       'status' => 'Fully Compliant' ),
    );
    ?>
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-10 animate-on-scroll fade-up">
                <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-3">Certified &amp; Compliant</h2>
                <p class="text-muted-foreground max-w-2xl mx-auto">Our platform meets the highest security and regulatory standards required for processing sensitive personal data in South Africa</p>
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $certifications as $cert ) : ?>
                    <div class="flex flex-col items-center justify-center p-6 bg-card border border-border rounded-lg hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 group">
                        <img
                            src="<?php echo esc_url( $cert_base . $cert['file'] ); ?>"
                            alt="<?php echo esc_attr( $cert['name'] ); ?>"
                            class="h-12 w-auto max-w-[120px] object-contain mb-4 opacity-70 group-hover:opacity-100 transition-opacity duration-200"
                            loading="lazy"
                        >
                        <span class="font-semibold text-sm text-foreground text-center"><?php echo esc_html( $cert['name'] ); ?></span>
                        <span class="text-xs text-muted-foreground text-center mt-1"><?php echo esc_html( $cert['status'] ); ?></span>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- ─── Customer Logos (compact) ─── -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- ─── Items Grid ─── -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Compliance Solutions</h2>
                <p class="text-lg text-muted-foreground">Explore our full suite of compliance verification services</p>
            </div>

            <?php if ( have_posts() ) : ?>
                <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                    <?php while ( have_posts() ) : the_post(); ?>
                        <?php get_template_part( 'template-parts/cards/feature-card', null, array(
                            'icon'        => get_field( 'icon' ) ?: 'shield-check',
                            'title'       => get_the_title(),
                            'description' => get_field( 'subtitle' ) ?: get_the_excerpt(),
                            'href'        => get_the_permalink(),
                            'category'    => 'compliance',
                        ) ); ?>
                    <?php endwhile; ?>
                </div>
            <?php else : ?>
                <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
            <?php endif; ?>
        </div>
    </section>

    <!-- ─── Key Regulations ─── -->
    <section class="py-20 px-4 <?php echo esc_attr( $theme['section_tint'] ); ?>">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Key Regulations</h2>
                <p class="text-lg text-muted-foreground">South African legislation that impacts how organisations handle personal data and financial compliance</p>
            </div>

            <div class="grid md:grid-cols-3 gap-8 stagger-list">

                <?php
                $regulations = array(
                    array(
                        'icon'        => 'shield',
                        'title'       => 'POPIA',
                        'description' => 'Protection of Personal Information Act — Mandatory for all South African organisations processing personal data',
                        'href'        => home_url( '/compliance/popia/' ),
                    ),
                    array(
                        'icon'        => 'user-check',
                        'title'       => 'FICA',
                        'description' => 'Financial Intelligence Centre Act — KYC/CDD requirements for financial institutions',
                        'href'        => home_url( '/compliance/fica/' ),
                    ),
                    array(
                        'icon'        => 'award',
                        'title'       => 'B-BBEE',
                        'description' => 'Broad-Based Black Economic Empowerment — Compliance verification for B-BBEE certificates',
                        'href'        => home_url( '/compliance/b-bbee/' ),
                    ),
                );

                foreach ( $regulations as $reg ) : ?>
                    <div class="bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-accent/5 border border-border/50 rounded-lg p-6 space-y-4 <?php echo esc_attr( $theme['card_hover_border'] ); ?> transition-all duration-200 hover:shadow-xl <?php echo esc_attr( $theme['card_hover_shadow'] ); ?> hover:-translate-y-1 group">
                        <div class="w-12 h-12 rounded-lg <?php echo esc_attr( $theme['icon_bg'] ); ?> flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-200">
                            <?php verigate_icon( $reg['icon'], 'w-6 h-6 ' . $theme['icon_color'], 24 ); ?>
                        </div>
                        <h3 class="text-xl font-semibold text-foreground"><?php echo esc_html( $reg['title'] ); ?></h3>
                        <p class="text-muted-foreground leading-relaxed"><?php echo esc_html( $reg['description'] ); ?></p>
                        <a href="<?php echo esc_url( $reg['href'] ); ?>" class="inline-flex items-center gap-1.5 text-sm font-medium <?php echo esc_attr( $theme['accent_text'] ); ?> group-hover:gap-2.5 transition-all duration-200">
                            Learn more <?php verigate_icon( 'arrow-right', 'w-4 h-4', 16 ); ?>
                        </a>
                    </div>
                <?php endforeach; ?>

            </div>
        </div>
    </section>

    <!-- ─── Stats Counter ─── -->
    <?php get_template_part( 'template-parts/sections/stats-counter' ); ?>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'The POPIA compliance features alone make VeriGate worth it. We\'ve eliminated manual compliance tracking and reduced our risk exposure significantly.',
        'name'    => 'Lindiwe Nkosi',
        'role'    => 'Compliance Manager',
        'company' => 'Discovery',
        'photo'   => 'https://images.unsplash.com/photo-1611432579402-7037e3e2c1e4?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'discovery.svg',
    ) ); ?>

    <!-- ─── CTA ─── -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'compliance' ) ); ?>

</main>

<?php get_footer();
