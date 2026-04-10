<?php
/**
 * 404 Page
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">
    <div class="container mx-auto max-w-6xl py-24 px-4 text-center">
        <div class="max-w-lg mx-auto space-y-6">
            <div class="w-24 h-24 mx-auto rounded-full bg-accent/10 flex items-center justify-center">
                <?php verigate_icon( 'shield', 'w-12 h-12 text-accent', 48 ); ?>
            </div>

            <h1 class="text-4xl md:text-5xl font-bold text-foreground">Page Not Found</h1>

            <p class="text-lg text-muted-foreground">
                The page you're looking for doesn't exist or has been moved.
            </p>

            <div class="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                <a href="<?php echo esc_url( home_url( '/' ) ); ?>" class="inline-flex items-center justify-center px-6 py-3 text-sm font-semibold rounded-lg bg-accent text-white hover:bg-accent/90 shadow-md transition-all duration-200">
                    <?php verigate_icon( 'arrow-right', 'w-4 h-4 mr-2 rotate-180', 16 ); ?>
                    Back to Home
                </a>
                <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-6 py-3 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all duration-200">
                    Contact Support
                </a>
            </div>
        </div>
    </div>
</main>

<?php get_footer();
