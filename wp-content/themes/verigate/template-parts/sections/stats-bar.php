<?php
/**
 * Stats Bar — Compact horizontal stats
 *
 * @package VeriGate
 */

$stats = $args['stats'] ?? array(
    array( 'value' => '50,000+', 'label' => 'Verifications Completed' ),
    array( 'value' => '99.2%', 'label' => 'Accuracy Rate' ),
    array( 'value' => '24hr', 'label' => 'Turnaround Time' ),
    array( 'value' => '200+', 'label' => 'Clients Nationwide' ),
);
$class = $args['class'] ?? '';
?>

<div class="flex flex-wrap items-center justify-center gap-x-8 gap-y-4 py-6 <?php echo esc_attr( $class ); ?>">
    <?php foreach ( $stats as $i => $stat ) : ?>
        <div class="flex items-center gap-x-8">
            <div class="text-center">
                <div class="text-2xl md:text-3xl font-bold text-accent"><?php echo esc_html( $stat['value'] ); ?></div>
                <div class="text-xs md:text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></div>
            </div>
            <?php if ( $i < count( $stats ) - 1 ) : ?>
                <div class="hidden sm:block w-px h-10 bg-border"></div>
            <?php endif; ?>
        </div>
    <?php endforeach; ?>
</div>
