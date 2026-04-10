<?php
/**
 * SA Data Sources Section
 *
 * Shows the 8 South African government, bureau, and professional body
 * logos that VeriGate connects to directly.
 *
 * @package VeriGate
 */

$logo_base = VERIGATE_URI . '/assets/img/logos/sa-sources/';

$sources = array(
    array( 'name' => 'Department of Home Affairs', 'abbr' => 'DHA', 'file' => 'dha.svg', 'desc' => 'Identity & citizenship verification' ),
    array( 'name' => 'South African Police Service', 'abbr' => 'SAPS', 'file' => 'saps.svg', 'desc' => 'Criminal record checks' ),
    array( 'name' => 'SA Qualifications Authority', 'abbr' => 'SAQA', 'file' => 'saqa.svg', 'desc' => 'Qualification verification' ),
    array( 'name' => 'Companies & IP Commission', 'abbr' => 'CIPC', 'file' => 'cipc.svg', 'desc' => 'Directorship & company checks' ),
    array( 'name' => 'TransUnion', 'abbr' => 'TransUnion', 'file' => 'transunion.svg', 'desc' => 'Credit bureau checks' ),
    array( 'name' => 'Experian', 'abbr' => 'Experian', 'file' => 'experian.svg', 'desc' => 'Credit & fraud screening' ),
    array( 'name' => 'XDS', 'abbr' => 'XDS', 'file' => 'xds.png', 'desc' => 'Alternative credit data' ),
    array( 'name' => 'Health Professions Council', 'abbr' => 'HPCSA', 'file' => 'hpcsa.png', 'desc' => 'Professional registration' ),
);
?>

<section class="py-16 px-4 bg-secondary/30">
    <div class="container mx-auto max-w-6xl">
        <div class="text-center mb-12 animate-on-scroll fade-up">
            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-secondary text-secondary-foreground mb-4">
                <?php verigate_icon( 'globe', 'w-3 h-3 mr-1', 12 ); ?>
                Direct Connections
            </span>
            <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-3">Connected to South Africa's Key Data Sources</h2>
            <p class="text-muted-foreground max-w-2xl mx-auto">Our platform integrates directly with government databases, credit bureaus, and professional bodies for fast, accurate verification results.</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-6 stagger-list">
            <?php foreach ( $sources as $source ) : ?>
                <div class="flex flex-col items-center justify-center p-6 bg-card border border-border rounded-lg hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 group">
                    <img
                        src="<?php echo esc_url( $logo_base . $source['file'] ); ?>"
                        alt="<?php echo esc_attr( $source['name'] ); ?>"
                        class="h-12 w-auto max-w-[120px] object-contain mb-4 opacity-70 group-hover:opacity-100 transition-opacity duration-200"
                        loading="lazy"
                    >
                    <span class="font-semibold text-sm text-foreground text-center"><?php echo esc_html( $source['abbr'] ); ?></span>
                    <span class="text-xs text-muted-foreground text-center mt-1"><?php echo esc_html( $source['desc'] ); ?></span>
                </div>
            <?php endforeach; ?>
        </div>
    </div>
</section>
