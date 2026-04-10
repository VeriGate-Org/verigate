<?php
/**
 * Customer Logos Section — Infinite Scrolling Marquee
 *
 * @package VeriGate
 */

$title    = $args['title'] ?? "Trusted by South Africa's Leading Organisations";
$subtitle = $args['subtitle'] ?? 'Join 200+ companies using VeriGate for background screening and verification';

$logos = array(
    array( 'name' => 'Standard Bank', 'file' => 'standard-bank.png' ),
    array( 'name' => 'Discovery',     'file' => 'discovery.svg' ),
    array( 'name' => 'Vodacom',       'file' => 'vodacom.svg' ),
    array( 'name' => 'Old Mutual',    'file' => 'old-mutual.svg' ),
    array( 'name' => 'ABSA',          'file' => 'absa.png' ),
    array( 'name' => 'Nedbank',       'file' => 'nedbank.svg' ),
    array( 'name' => 'Sanlam',        'file' => 'sanlam.svg' ),
    array( 'name' => 'Sasol',         'file' => 'sasol.png' ),
    array( 'name' => 'MTN',           'file' => 'mtn.svg' ),
    array( 'name' => 'Capitec',       'file' => 'capitec.svg' ),
);

$logo_base = VERIGATE_URI . '/assets/img/logos/';
?>

<section class="py-16 px-4">
    <div class="container mx-auto max-w-6xl">
        <div class="max-w-2xl mb-12 animate-on-scroll fade-up">
            <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-3"><?php echo esc_html( $title ); ?></h2>
            <p class="text-muted-foreground"><?php echo esc_html( $subtitle ); ?></p>
        </div>

        <!-- Scrolling Marquee -->
        <div class="logo-marquee">
            <div class="logo-marquee__track">
                <?php
                // Render logos twice for seamless loop
                for ( $loop = 0; $loop < 2; $loop++ ) :
                    foreach ( $logos as $logo ) :
                ?>
                    <div class="flex items-center justify-center flex-shrink-0 px-6">
                        <img
                            src="<?php echo esc_url( $logo_base . $logo['file'] ); ?>"
                            alt="<?php echo esc_attr( $logo['name'] ); ?>"
                            class="h-10 w-auto max-w-[160px] object-contain opacity-60 hover:opacity-100 transition-opacity duration-300"
                            loading="lazy"
                        >
                    </div>
                <?php
                    endforeach;
                endfor;
                ?>
            </div>
        </div>
    </div>
</section>
