<?php
/**
 * Stats Counter — Animated numbers section
 *
 * @package VeriGate
 */

$stats = get_posts( array(
    'post_type'      => 'stat',
    'posts_per_page' => 6,
    'orderby'        => 'menu_order',
    'order'          => 'ASC',
) );

// Fallback to defaults if no CPT entries exist.
if ( empty( $stats ) ) {
    $default_stats = array(
        array( 'value' => '200+', 'label' => 'SA Clients', 'icon' => 'users' ),
        array( 'value' => '50000+', 'label' => 'Verifications Completed', 'icon' => 'circle-check' ),
        array( 'value' => '99.2%', 'label' => 'Accuracy Rate', 'icon' => 'target' ),
        array( 'value' => '24', 'label' => 'Hour Turnaround', 'icon' => 'zap' ),
        array( 'value' => '9', 'label' => 'Verification Types', 'icon' => 'activity' ),
        array( 'value' => '99.9%', 'label' => 'Uptime SLA', 'icon' => 'globe' ),
    );
} else {
    $default_stats = array();
    foreach ( $stats as $s ) {
        $default_stats[] = array(
            'value' => get_field( 'value', $s->ID ) ?: $s->post_title,
            'label' => get_field( 'label', $s->ID ) ?: '',
            'icon'  => get_field( 'icon', $s->ID ) ?: 'circle-check',
        );
    }
}
?>

<section class="py-20 bg-primary text-primary-foreground" data-counter-section>
    <div class="container mx-auto max-w-6xl px-4">
        <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl font-bold mb-4">Trusted at Scale</h2>
            <p class="text-primary-foreground/80 text-lg">Numbers that speak to our reliability and global reach</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-8">
            <?php foreach ( $default_stats as $i => $stat ) : ?>
                <div class="text-center group" data-counter-item style="opacity: 0; transform: translateY(20px); transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1) <?php echo $i * 0.1; ?>s;">
                    <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-accent/10 flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-200">
                        <?php verigate_icon( $stat['icon'], 'w-8 h-8 text-accent', 32 ); ?>
                    </div>
                    <div class="text-4xl md:text-5xl font-bold mb-2 text-accent" data-counter="<?php echo esc_attr( $stat['value'] ); ?>">
                        <?php echo esc_html( $stat['value'] ); ?>
                    </div>
                    <div class="text-sm md:text-base text-primary-foreground/80">
                        <?php echo esc_html( $stat['label'] ); ?>
                    </div>
                </div>
            <?php endforeach; ?>
        </div>

        <div class="mt-16 text-center">
            <p class="text-primary-foreground/60 text-sm">
                Join thousands of companies worldwide trusting VeriGate with their identity verification needs
            </p>
        </div>
    </div>
</section>
