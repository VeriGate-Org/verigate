<?php
/**
 * Single Testimonial Quote — Compact reusable block
 *
 * Usage: get_template_part( 'template-parts/sections/testimonial-single', null, array(
 *     'quote'   => '...',
 *     'name'    => '...',
 *     'role'    => '...',
 *     'company' => '...',
 *     'photo'   => 'https://...',
 *     'logo'    => 'standard-bank.png',
 * ) );
 *
 * @package VeriGate
 */

$quote   = $args['quote'] ?? '';
$name    = $args['name'] ?? '';
$role    = $args['role'] ?? '';
$company = $args['company'] ?? '';
$photo   = $args['photo'] ?? '';
$logo    = $args['logo'] ?? '';

if ( ! $quote ) return;

$logo_base = VERIGATE_URI . '/assets/img/logos/';
?>

<section class="py-16 px-4 bg-secondary/30">
    <div class="container mx-auto max-w-4xl">
        <div class="text-center animate-on-scroll fade-up">

            <!-- Stars -->
            <div class="flex items-center justify-center gap-1 mb-6">
                <?php for ( $i = 0; $i < 5; $i++ ) : ?>
                    <?php verigate_icon( 'star', 'w-5 h-5 text-amber-400 fill-amber-400', 20 ); ?>
                <?php endfor; ?>
            </div>

            <!-- Quote -->
            <blockquote class="text-xl md:text-2xl font-medium text-foreground leading-relaxed mb-8 max-w-3xl mx-auto">
                &ldquo;<?php echo esc_html( $quote ); ?>&rdquo;
            </blockquote>

            <!-- Author -->
            <div class="flex items-center justify-center gap-4">
                <?php if ( $photo ) : ?>
                    <img src="<?php echo esc_url( $photo ); ?>"
                         alt="<?php echo esc_attr( $name ); ?>"
                         class="w-14 h-14 rounded-full object-cover ring-2 ring-accent/20"
                         loading="lazy">
                <?php endif; ?>
                <div class="text-left">
                    <div class="font-semibold text-foreground"><?php echo esc_html( $name ); ?></div>
                    <div class="text-sm text-muted-foreground"><?php echo esc_html( $role ); ?> at <?php echo esc_html( $company ); ?></div>
                </div>
                <?php if ( $logo ) : ?>
                    <img src="<?php echo esc_url( $logo_base . $logo ); ?>"
                         alt="<?php echo esc_attr( $company ); ?>"
                         class="h-6 w-auto max-w-[80px] object-contain opacity-50 ml-4 hidden sm:block"
                         loading="lazy">
                <?php endif; ?>
            </div>

        </div>
    </div>
</section>
