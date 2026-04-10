<?php
/**
 * Content — No Results
 *
 * @package VeriGate
 */
?>

<div class="text-center py-16 space-y-4">
    <div class="w-16 h-16 mx-auto rounded-full bg-muted flex items-center justify-center">
        <?php verigate_icon( 'file-search', 'w-8 h-8 text-muted-foreground', 32 ); ?>
    </div>
    <h2 class="text-2xl font-bold text-foreground">No Results Found</h2>
    <p class="text-muted-foreground max-w-md mx-auto">
        We couldn't find what you're looking for. Try searching with different terms or browse our main sections.
    </p>
    <div class="pt-4">
        <a href="<?php echo esc_url( home_url( '/' ) ); ?>" class="inline-flex items-center justify-center px-6 py-3 text-sm font-semibold rounded-lg bg-accent text-white hover:bg-accent/90 transition-all duration-200">
            Back to Home
        </a>
    </div>
</div>
