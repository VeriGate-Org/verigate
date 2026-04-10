<?php
/**
 * CTA Section — All Variants
 *
 * Usage: get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'homepage' ) );
 *
 * @package VeriGate
 */

$variant = $args['variant'] ?? 'homepage';

$variants = array(
    'homepage' => array(
        'title'           => 'Ready to Streamline Your Verification Process?',
        'description'     => 'Join 200+ South African organisations who trust VeriGate for secure, compliant background screening.',
        'primary_label'   => 'Request a Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'View Pricing',
        'secondary_url'   => home_url( '/pricing/' ),
    ),
    'verification' => array(
        'title'           => 'Start Screening with VeriGate',
        'description'     => 'Fast, accurate verification checks powered by direct integrations with South Africa\'s authoritative data sources.',
        'primary_label'   => 'Get Started',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'Compare Plans',
        'secondary_url'   => home_url( '/compare-plans/' ),
    ),
    'compliance' => array(
        'title'           => 'Ensure Full Compliance',
        'description'     => 'Stay ahead of South African regulatory requirements with VeriGate\'s automated compliance checks. POPIA, FICA, and sector-specific regulations covered.',
        'primary_label'   => 'Request Compliance Review',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'View Resources',
        'secondary_url'   => home_url( '/resources/' ),
    ),
    'fraud' => array(
        'title'           => 'Protect Your Organisation',
        'description'     => 'Defend against fraud with VeriGate\'s multi-layered detection and prevention platform. Built for South African threats, POPIA compliant.',
        'primary_label'   => 'Schedule Security Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'View Resources',
        'secondary_url'   => home_url( '/resources/' ),
    ),
    'industry' => array(
        'title'           => 'Built for Your Industry',
        'description'     => 'Join leading South African organisations using VeriGate for compliant, fast verification. Trusted by 200+ enterprises across the country.',
        'primary_label'   => 'Schedule Industry Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'ROI Calculator',
        'secondary_url'   => home_url( '/roi-calculator/' ),
    ),
    'blog' => array(
        'title'           => 'Stay Updated',
        'description'     => 'Subscribe for the latest insights on background screening, compliance, and verification in South Africa.',
        'primary_label'   => 'Subscribe',
        'primary_url'     => '#',
        'show_email'      => true,
    ),
    'careers' => array(
        'title'           => "Don't See a Role That Fits?",
        'description'     => 'We\'re always looking for talented people who share our passion for building secure, compliant verification solutions for South Africa.',
        'primary_label'   => 'Send Your CV',
        'primary_url'     => 'mailto:careers@verigate.co.za?subject=General Application',
        'secondary_label' => 'Set Up Job Alert',
        'secondary_url'   => 'mailto:careers@verigate.co.za?subject=Job Alert Request',
        'is_mailto'       => true,
    ),
    'pricing' => array(
        'title'           => 'Ready to Get Started?',
        'description'     => 'Join 200+ South African organisations already using VeriGate for background screening.',
        'primary_label'   => 'Request a Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'Contact Sales',
        'secondary_url'   => home_url( '/contact/' ),
    ),
    'platform' => array(
        'title'           => 'See It in Action',
        'description'     => 'Book a personalised demo and see how VeriGate can streamline your background screening process.',
        'primary_label'   => 'Request a Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'Technical Support',
        'secondary_url'   => home_url( '/technical-support/' ),
    ),
    'company' => array(
        'title'           => 'Ready to Get Started?',
        'description'     => 'Join 200+ South African organisations who trust VeriGate for secure, POPIA-compliant background screening and verification.',
        'primary_label'   => 'Request a Demo',
        'primary_url'     => home_url( '/request-demo/' ),
        'secondary_label' => 'View Pricing',
        'secondary_url'   => home_url( '/pricing/' ),
    ),
);

$content = $variants[ $variant ] ?? $variants['homepage'];
$show_email = ! empty( $content['show_email'] );
$is_mailto  = ! empty( $content['is_mailto'] );
?>

<section class="py-24 px-4 bg-gradient-hero relative overflow-hidden">
    <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>

    <div class="container mx-auto max-w-6xl relative z-10">
        <div class="<?php echo $show_email ? 'max-w-2xl mx-auto text-center' : 'max-w-2xl'; ?> space-y-8 animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl lg:text-5xl font-bold text-primary-foreground">
                <?php echo esc_html( $content['title'] ); ?>
            </h2>
            <p class="text-xl text-primary-foreground/90">
                <?php echo esc_html( $content['description'] ); ?>
            </p>

            <?php if ( $show_email ) : ?>
                <?php
                // Render CF7 newsletter form if available.
                $newsletter_id = get_option( 'verigate_newsletter_form_id', '' );
                if ( $newsletter_id && function_exists( 'wpcf7_contact_form' ) ) {
                    echo do_shortcode( '[contact-form-7 id="' . intval( $newsletter_id ) . '" html_class="newsletter-form"]' );
                }
                ?>
            <?php else : ?>
                <div class="flex flex-col sm:flex-row gap-4 items-start pt-4">
                    <?php if ( $is_mailto ) : ?>
                        <a href="<?php echo esc_url( $content['primary_url'] ); ?>" class="inline-flex items-center justify-center min-w-[200px] px-8 py-3.5 text-base font-semibold rounded-lg bg-primary-foreground text-primary hover:bg-primary-foreground/90 shadow-lg hover:scale-105 transition-transform duration-200">
                            <?php if ( $variant === 'careers' ) : ?>
                                <?php verigate_icon( 'mail', 'w-4 h-4 mr-2', 16 ); ?>
                            <?php endif; ?>
                            <?php echo esc_html( $content['primary_label'] ); ?>
                        </a>
                    <?php else : ?>
                        <a href="<?php echo esc_url( $content['primary_url'] ); ?>" class="inline-flex items-center justify-center min-w-[200px] px-8 py-3.5 text-base font-semibold rounded-lg bg-primary-foreground text-primary hover:bg-primary-foreground/90 shadow-lg hover:scale-105 transition-transform duration-200">
                            <?php echo esc_html( $content['primary_label'] ); ?>
                            <?php verigate_icon( 'arrow-right', 'w-5 h-5 ml-2', 20 ); ?>
                        </a>
                    <?php endif; ?>

                    <?php if ( ! empty( $content['secondary_label'] ) && ! empty( $content['secondary_url'] ) ) : ?>
                        <a href="<?php echo esc_url( $content['secondary_url'] ); ?>" class="inline-flex items-center justify-center min-w-[200px] px-8 py-3.5 text-base font-semibold rounded-lg border-2 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground/10 hover:scale-105 transition-transform duration-200">
                            <?php if ( $variant === 'careers' && $is_mailto ) : ?>
                                <?php verigate_icon( 'bell', 'w-4 h-4 mr-2', 16 ); ?>
                            <?php endif; ?>
                            <?php echo esc_html( $content['secondary_label'] ); ?>
                        </a>
                    <?php endif; ?>
                </div>
            <?php endif; ?>
        </div>
    </div>
</section>
