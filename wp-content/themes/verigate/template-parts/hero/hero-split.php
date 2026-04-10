<?php
/**
 * Hero — Split Layout (Homepage)
 *
 * Displays a full-height hero with text on the left and illustration on the right.
 *
 * @package VeriGate
 */

$title         = $args['title'] ?? 'Enterprise Verification Platform';
$subtitle_html = $args['subtitle_html'] ?? '<span class="block mt-2 bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-500 bg-clip-text text-transparent">Trusted Background Screening for South Africa</span>';
$description   = $args['description'] ?? 'Comprehensive criminal checks, qualification verification, employment history, and identity validation for modern South African businesses';
$primary_label = $args['primary_label'] ?? 'Get Started';
$primary_url   = $args['primary_url'] ?? home_url( '/request-demo/' );
$secondary_label = $args['secondary_label'] ?? 'View Platform';
$secondary_url   = $args['secondary_url'] ?? home_url( '/platform/' );
$illustration    = $args['illustration'] ?? 'shield-verification';
?>

<section class="relative min-h-screen flex items-center justify-center overflow-hidden pt-16">
    <!-- Background gradient -->
    <div class="absolute inset-0 z-0 bg-gradient-hero"></div>

    <!-- Mesh overlay -->
    <div class="absolute inset-0 bg-gradient-mesh z-0 opacity-30"></div>

    <!-- Floating shapes -->
    <div class="absolute top-20 right-10 w-64 h-64 bg-cyan-400/20 rounded-full blur-3xl animate-float"></div>
    <div class="absolute bottom-20 left-10 w-96 h-96 bg-primary/20 rounded-full blur-3xl animate-float-delayed"></div>
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-80 h-80 bg-blue-400/10 rounded-full blur-3xl animate-pulse-slow"></div>

    <!-- Content -->
    <div class="container mx-auto max-w-6xl relative z-10 py-20 md:py-32">
        <div class="grid lg:grid-cols-2 gap-12 items-center">
            <div class="space-y-8 animate-on-scroll fade-up">
                <h1 class="text-4xl md:text-6xl lg:text-7xl font-bold text-primary-foreground leading-tight">
                    <?php echo wp_kses_post( $title ); ?>
                    <?php echo wp_kses_post( $subtitle_html ); ?>
                </h1>

                <p class="text-xl md:text-2xl text-primary-foreground/90 max-w-2xl">
                    <?php echo esc_html( $description ); ?>
                </p>

                <div class="flex flex-col sm:flex-row gap-4 items-start pt-4">
                    <a href="<?php echo esc_url( $primary_url ); ?>" class="inline-flex items-center justify-center min-w-[200px] px-8 py-3.5 text-base font-semibold rounded-lg bg-accent text-white hover:bg-accent/90 shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200">
                        <?php echo esc_html( $primary_label ); ?>
                    </a>
                    <a href="<?php echo esc_url( $secondary_url ); ?>" class="inline-flex items-center justify-center min-w-[200px] px-8 py-3.5 text-base font-semibold rounded-lg border-2 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground hover:text-primary transition-all duration-200">
                        <?php echo esc_html( $secondary_label ); ?>
                    </a>
                </div>

                <!-- Trust Indicators -->
                <div class="flex flex-wrap gap-8 pt-12">
                    <div class="flex items-center gap-2 text-primary-foreground/80">
                        <?php verigate_icon( 'shield-check', 'w-5 h-5 text-accent', 20 ); ?>
                        <span class="text-sm font-medium">POPIA Compliant</span>
                    </div>
                    <div class="flex items-center gap-2 text-primary-foreground/80">
                        <?php verigate_icon( 'award', 'w-5 h-5 text-accent', 20 ); ?>
                        <span class="text-sm font-medium">ISO 27001 Certified</span>
                    </div>
                    <div class="flex items-center gap-2 text-primary-foreground/80">
                        <?php verigate_icon( 'badge-check', 'w-5 h-5 text-accent', 20 ); ?>
                        <span class="text-sm font-medium">SOC 2 Type II</span>
                    </div>
                </div>
            </div>

            <!-- Illustration -->
            <div class="hidden lg:flex items-center justify-center">
                <?php get_template_part( 'template-parts/illustrations/' . $illustration ); ?>
            </div>
        </div>
    </div>

    <!-- Bottom gradient fade -->
    <div class="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-background to-transparent z-0"></div>
</section>
