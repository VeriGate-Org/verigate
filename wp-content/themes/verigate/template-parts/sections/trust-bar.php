<?php
/**
 * Trust Bar — Compact certification badges & key stats
 *
 * @package VeriGate
 */

$certifications = array(
    array( 'name' => 'POPIA Compliant',    'file' => 'popia.svg' ),
    array( 'name' => 'ISO 27001 Certified', 'file' => 'iso-27001.svg' ),
    array( 'name' => 'SOC 2 Compliant',    'file' => 'soc2.svg' ),
    array( 'name' => 'FICA Compliant',     'file' => 'fica.svg' ),
);

$stats = array(
    array( 'value' => '200+',  'label' => 'Clients' ),
    array( 'value' => '99.2%', 'label' => 'Accuracy' ),
    array( 'value' => '24hr',  'label' => 'Turnaround' ),
);

$cert_base = VERIGATE_URI . '/assets/img/logos/certifications/';
?>

<section class="py-8 bg-secondary/30 animate-on-scroll fade-up">
    <div class="container mx-auto max-w-6xl px-4">
        <div class="flex flex-col md:flex-row items-center justify-between gap-8">

            <!-- Certification Badges -->
            <div class="flex flex-wrap items-center justify-center md:justify-start gap-6">
                <?php foreach ( $certifications as $cert ) : ?>
                    <img
                        src="<?php echo esc_url( $cert_base . $cert['file'] ); ?>"
                        alt="<?php echo esc_attr( $cert['name'] ); ?>"
                        class="h-8 w-auto opacity-60 hover:opacity-100 transition-opacity duration-300"
                        loading="lazy"
                    >
                <?php endforeach; ?>
            </div>

            <!-- Key Stats -->
            <div class="flex items-center justify-center md:justify-end gap-6">
                <?php foreach ( $stats as $i => $stat ) : ?>
                    <?php if ( $i > 0 ) : ?>
                        <div class="w-px h-8 border-l border-border"></div>
                    <?php endif; ?>
                    <div class="text-center">
                        <span class="text-lg font-bold text-accent"><?php echo esc_html( $stat['value'] ); ?></span>
                        <span class="text-sm text-muted-foreground ml-1"><?php echo esc_html( $stat['label'] ); ?></span>
                    </div>
                <?php endforeach; ?>
            </div>

        </div>
    </div>
</section>
