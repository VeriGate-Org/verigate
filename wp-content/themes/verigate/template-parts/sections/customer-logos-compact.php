<?php
/**
 * Customer Logos Compact — Static logo strip for secondary pages
 *
 * @package VeriGate
 */

$logos = array(
    array( 'name' => 'Standard Bank', 'file' => 'standard-bank.png' ),
    array( 'name' => 'Discovery',     'file' => 'discovery.svg' ),
    array( 'name' => 'Vodacom',       'file' => 'vodacom.svg' ),
    array( 'name' => 'Old Mutual',    'file' => 'old-mutual.svg' ),
    array( 'name' => 'ABSA',          'file' => 'absa.png' ),
    array( 'name' => 'Nedbank',       'file' => 'nedbank.svg' ),
    array( 'name' => 'Capitec',       'file' => 'capitec.svg' ),
    array( 'name' => 'MTN',           'file' => 'mtn.svg' ),
);

$logo_base = VERIGATE_URI . '/assets/img/logos/';
?>

<section class="py-12 px-4 animate-on-scroll fade-up">
    <div class="container mx-auto max-w-6xl">

        <p class="text-sm text-muted-foreground text-center mb-6">
            Trusted by South Africa's leading organisations
        </p>

        <div class="flex flex-wrap items-center justify-center gap-8 md:gap-12">
            <?php foreach ( $logos as $logo ) : ?>
                <img
                    src="<?php echo esc_url( $logo_base . $logo['file'] ); ?>"
                    alt="<?php echo esc_attr( $logo['name'] ); ?>"
                    class="h-8 w-auto max-w-[100px] object-contain opacity-40 hover:opacity-70 grayscale hover:grayscale-0 transition-all duration-300"
                    loading="lazy"
                >
            <?php endforeach; ?>
        </div>

    </div>
</section>
