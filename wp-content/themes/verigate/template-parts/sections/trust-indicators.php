<?php
/**
 * Trust Indicators Section — Stats cards grid
 *
 * @package VeriGate
 */

$stats = array(
    array( 'icon' => 'users', 'value' => '50,000+', 'label' => 'Verifications Completed' ),
    array( 'icon' => 'trending-up', 'value' => '99.2%', 'label' => 'Accuracy Rate' ),
    array( 'icon' => 'clock', 'value' => '24hr', 'label' => 'Turnaround Time' ),
    array( 'icon' => 'award', 'value' => '200+', 'label' => 'Clients Nationwide' ),
);
?>

<section class="py-24 px-4 bg-secondary/30">
    <div class="container mx-auto max-w-6xl">
        <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">
                Trusted by South Africa's Leading Organisations
            </h2>
            <p class="text-lg text-muted-foreground">
                Join hundreds of organisations across South Africa who rely on VeriGate for secure, compliant background screening
            </p>
        </div>

        <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
            <?php foreach ( $stats as $stat ) : ?>
                <div class="bg-card/50 border border-border/50 rounded-lg p-8 text-center space-y-3 hover:shadow-md transition-all duration-200 backdrop-blur-sm">
                    <div class="w-12 h-12 rounded-full bg-accent/10 flex items-center justify-center mx-auto">
                        <?php verigate_icon( $stat['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                    </div>
                    <div class="text-4xl font-bold text-foreground"><?php echo esc_html( $stat['value'] ); ?></div>
                    <div class="text-sm text-muted-foreground font-medium"><?php echo esc_html( $stat['label'] ); ?></div>
                </div>
            <?php endforeach; ?>
        </div>
    </div>
</section>
