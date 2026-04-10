<?php
/**
 * Testimonials Section — Carousel
 *
 * Pulls from 'testimonial' CPT with ACF fields. Falls back to hardcoded
 * data (including Unsplash photos) when CPT entries lack images.
 *
 * @package VeriGate
 */

/* ── Hardcoded fallback data (matches React source) ────────────────── */
$fallback_testimonials = array(
    array(
        'quote'       => 'VeriGate has transformed our hiring process. What used to take weeks now takes hours. The accuracy and compliance features give us complete confidence in every verification.',
        'author_name' => 'Sipho Molefe',
        'author_role' => 'Head of HR',
        'company'     => 'Standard Bank',
        'rating'      => 5,
        'metrics'     => '67% faster screening turnaround',
        'industry'    => 'Banking',
        'verified'    => true,
        'photo_url'   => 'https://images.unsplash.com/photo-1586232902955-df204f34b36e?w=200&h=200&fit=crop&crop=face',
        'logo_file'   => 'standard-bank.png',
    ),
    array(
        'quote'       => 'The POPIA compliance features alone make VeriGate worth it. We\'ve eliminated manual compliance tracking and reduced our risk exposure significantly.',
        'author_name' => 'Lindiwe Nkosi',
        'author_role' => 'Compliance Manager',
        'company'     => 'Discovery',
        'rating'      => 5,
        'metrics'     => '100% POPIA compliance achieved',
        'industry'    => 'Insurance',
        'verified'    => true,
        'photo_url'   => 'https://images.unsplash.com/photo-1611432579402-7037e3e2c1e4?w=200&h=200&fit=crop&crop=face',
        'logo_file'   => 'discovery.svg',
    ),
    array(
        'quote'       => 'We process over 500 verifications a month through VeriGate. The bulk upload feature and API integration with our ATS has been a game-changer for our recruitment team.',
        'author_name' => 'Johan van der Merwe',
        'author_role' => 'Talent Acquisition Director',
        'company'     => 'Vodacom',
        'rating'      => 5,
        'metrics'     => '500+ monthly verifications processed',
        'industry'    => 'Telecommunications',
        'verified'    => true,
        'photo_url'   => 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop&crop=face',
        'logo_file'   => 'vodacom.svg',
    ),
    array(
        'quote'       => 'The real-time dashboard and automated reporting have given our compliance team complete visibility. VeriGate\'s accuracy rate speaks for itself.',
        'author_name' => 'Kavitha Pillay',
        'author_role' => 'Risk & Compliance Lead',
        'company'     => 'Old Mutual',
        'rating'      => 5,
        'metrics'     => '99.2% verification accuracy',
        'industry'    => 'Financial Services',
        'verified'    => true,
        'photo_url'   => 'https://images.unsplash.com/photo-1619981970074-d0b06ef1ed83?w=200&h=200&fit=crop&crop=face',
        'logo_file'   => 'old-mutual.svg',
    ),
    array(
        'quote'       => 'Switching from manual background checks to VeriGate was the best decision we made. The integration with our existing HR systems was seamless.',
        'author_name' => 'Kabelo Mabena',
        'author_role' => 'People Operations Manager',
        'company'     => 'Capitec',
        'rating'      => 5,
        'metrics'     => '3x faster onboarding process',
        'industry'    => 'Banking',
        'verified'    => true,
        'photo_url'   => 'https://images.unsplash.com/photo-1617244147299-5ef406921c35?w=200&h=200&fit=crop&crop=face',
        'logo_file'   => 'capitec.svg',
    ),
);

$logo_base = VERIGATE_URI . '/assets/img/logos/';

/* ── Try CPT first, fall back to hardcoded ──────────────────────────── */
$cpt_testimonials = get_posts( array(
    'post_type'      => 'testimonial',
    'posts_per_page' => 10,
    'orderby'        => 'menu_order',
    'order'          => 'ASC',
) );

$items = array();

if ( ! empty( $cpt_testimonials ) ) {
    foreach ( $cpt_testimonials as $idx => $t ) {
        $photo = get_field( 'author_photo', $t->ID );
        $logo  = get_field( 'company_logo', $t->ID );
        $fb    = $fallback_testimonials[ $idx ] ?? null;

        $items[] = array(
            'quote'       => get_field( 'quote', $t->ID ) ?: ( $fb['quote'] ?? '' ),
            'author_name' => get_field( 'author_name', $t->ID ) ?: $t->post_title,
            'author_role' => get_field( 'author_role', $t->ID ) ?: ( $fb['author_role'] ?? '' ),
            'company'     => get_field( 'company', $t->ID ) ?: ( $fb['company'] ?? '' ),
            'rating'      => (int) ( get_field( 'rating', $t->ID ) ?: ( $fb['rating'] ?? 5 ) ),
            'metrics'     => get_field( 'metrics', $t->ID ) ?: ( $fb['metrics'] ?? '' ),
            'industry'    => get_field( 'industry', $t->ID ) ?: ( $fb['industry'] ?? '' ),
            'verified'    => get_field( 'verified', $t->ID ) ?: ( $fb['verified'] ?? false ),
            'photo_url'   => ( $photo && is_array( $photo ) ) ? ( $photo['sizes']['thumbnail'] ?? $photo['url'] ) : ( $fb['photo_url'] ?? '' ),
            'logo_url'    => ( $logo && is_array( $logo ) ) ? $logo['url'] : ( $fb ? $logo_base . $fb['logo_file'] : '' ),
            'linkedin'    => get_field( 'linkedin_url', $t->ID ) ?: '',
        );
    }
} else {
    // Pure fallback — no CPT entries
    foreach ( $fallback_testimonials as $fb ) {
        $items[] = array(
            'quote'       => $fb['quote'],
            'author_name' => $fb['author_name'],
            'author_role' => $fb['author_role'],
            'company'     => $fb['company'],
            'rating'      => $fb['rating'],
            'metrics'     => $fb['metrics'],
            'industry'    => $fb['industry'],
            'verified'    => $fb['verified'],
            'photo_url'   => $fb['photo_url'],
            'logo_url'    => $logo_base . $fb['logo_file'],
            'linkedin'    => '',
        );
    }
}

if ( empty( $items ) ) return;
?>

<section class="py-20 px-4 bg-secondary/30">
    <div class="container mx-auto max-w-6xl">

        <!-- Header -->
        <div class="max-w-2xl mb-12 animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">What Our Customers Say</h2>
            <p class="text-lg text-muted-foreground">Real feedback from companies using VeriGate</p>
        </div>

        <!-- Carousel -->
        <div data-carousel data-carousel-delay="6000" role="region" aria-label="Customer testimonials">

            <?php foreach ( $items as $i => $t ) :
                $initials = verigate_initials( $t['author_name'] );
            ?>
                <div class="<?php echo $i > 0 ? 'hidden' : ''; ?>" data-carousel-item aria-hidden="<?php echo $i > 0 ? 'true' : 'false'; ?>">
                    <div class="bg-card border border-border rounded-lg relative overflow-hidden">
                        <!-- Quote icon -->
                        <div class="absolute top-6 right-6 opacity-10">
                            <?php verigate_icon( 'quote', 'w-24 h-24 text-accent', 96 ); ?>
                        </div>

                        <div class="pt-12 pb-8 px-8 relative z-10">
                            <!-- Rating -->
                            <?php if ( $t['rating'] ) : ?>
                                <?php verigate_stars( $t['rating'] ); ?>
                            <?php endif; ?>

                            <!-- Quote -->
                            <blockquote class="text-xl md:text-2xl font-medium text-foreground mb-8 leading-relaxed mt-6">
                                &ldquo;<?php echo esc_html( $t['quote'] ); ?>&rdquo;
                            </blockquote>

                            <!-- Metrics -->
                            <?php if ( $t['metrics'] ) : ?>
                                <div class="mb-8">
                                    <span class="inline-flex items-center bg-accent/10 text-accent border border-accent px-4 py-2 rounded-full text-sm font-medium">
                                        <?php verigate_icon( 'trending-up', 'w-4 h-4 mr-2', 16 ); ?>
                                        <?php echo esc_html( $t['metrics'] ); ?>
                                    </span>
                                </div>
                            <?php endif; ?>

                            <!-- Author -->
                            <div class="flex items-center gap-4 pt-6 border-t border-border">
                                <?php if ( $t['photo_url'] ) : ?>
                                    <img src="<?php echo esc_url( $t['photo_url'] ); ?>" alt="<?php echo esc_attr( $t['author_name'] ); ?>" class="w-16 h-16 rounded-full object-cover flex-shrink-0 ring-2 ring-accent/20" loading="lazy">
                                <?php else : ?>
                                    <div class="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-xl flex-shrink-0">
                                        <?php echo esc_html( $initials ); ?>
                                    </div>
                                <?php endif; ?>

                                <div class="flex-1">
                                    <div class="flex items-center gap-2 mb-1">
                                        <h4 class="font-semibold text-foreground"><?php echo esc_html( $t['author_name'] ); ?></h4>
                                        <?php if ( $t['verified'] ) : ?>
                                            <?php verigate_icon( 'verified', 'w-4 h-4 text-accent', 16 ); ?>
                                        <?php endif; ?>
                                    </div>
                                    <p class="text-sm text-muted-foreground"><?php echo esc_html( $t['author_role'] ); ?> at <?php echo esc_html( $t['company'] ); ?></p>
                                    <div class="flex items-center gap-3 mt-2">
                                        <?php if ( $t['logo_url'] ) : ?>
                                            <img src="<?php echo esc_url( $t['logo_url'] ); ?>" alt="<?php echo esc_attr( $t['company'] ); ?>" class="h-5 w-auto max-w-[80px] object-contain opacity-60" loading="lazy">
                                        <?php endif; ?>
                                        <?php if ( $t['industry'] ) : ?>
                                            <span class="inline-flex items-center px-2 py-0.5 border border-border rounded-full text-xs text-muted-foreground"><?php echo esc_html( $t['industry'] ); ?></span>
                                        <?php endif; ?>
                                        <?php if ( $t['linkedin'] ) : ?>
                                            <a href="<?php echo esc_url( $t['linkedin'] ); ?>" target="_blank" rel="noopener noreferrer" class="text-accent hover:text-accent/80 transition-colors">
                                                <?php verigate_icon( 'linkedin', 'w-4 h-4', 16 ); ?>
                                            </a>
                                        <?php endif; ?>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <?php endforeach; ?>

            <!-- Navigation -->
            <div class="flex items-center justify-center gap-4 mt-8">
                <button data-carousel-prev class="inline-flex items-center justify-center w-10 h-10 rounded-md border border-border hover:bg-accent/5 transition-colors" aria-label="Previous testimonial">
                    <?php verigate_icon( 'chevron-left', 'w-4 h-4', 16 ); ?>
                </button>

                <div class="flex gap-2">
                    <?php foreach ( $items as $i => $t ) : ?>
                        <button data-carousel-dot class="<?php echo $i === 0 ? 'bg-accent w-8' : 'bg-border w-2'; ?> h-2 rounded-full transition-all" aria-label="Go to testimonial <?php echo $i + 1; ?>"></button>
                    <?php endforeach; ?>
                </div>

                <button data-carousel-next class="inline-flex items-center justify-center w-10 h-10 rounded-md border border-border hover:bg-accent/5 transition-colors" aria-label="Next testimonial">
                    <?php verigate_icon( 'chevron-right', 'w-4 h-4', 16 ); ?>
                </button>
            </div>
        </div>

    </div>
</section>
