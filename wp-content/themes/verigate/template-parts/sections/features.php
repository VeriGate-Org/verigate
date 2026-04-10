<?php
/**
 * Features Section — 6-card grid
 *
 * @package VeriGate
 */

$features = array(
    array( 'icon' => 'shield', 'title' => 'Criminal Checks', 'desc' => 'Comprehensive SAPS criminal record checks across all South African jurisdictions. Results within 24 hours for most cases.' ),
    array( 'icon' => 'graduation-cap', 'title' => 'Qualification Verification', 'desc' => 'Verify degrees, diplomas, and professional qualifications through SAQA, universities, and professional bodies like HPCSA.' ),
    array( 'icon' => 'user-check', 'title' => 'Employment History', 'desc' => 'Thorough employment history verification including dates, job titles, and reason for leaving with previous employers.' ),
    array( 'icon' => 'fingerprint', 'title' => 'Identity Validation', 'desc' => 'Validate South African IDs, passports, and work permits against Department of Home Affairs records.' ),
    array( 'icon' => 'credit-card', 'title' => 'Credit Screening', 'desc' => 'Credit bureau checks through TransUnion SA, Experian SA, and XDS for comprehensive financial risk assessment.' ),
    array( 'icon' => 'file-search', 'title' => 'Compliance Monitoring', 'desc' => 'Ongoing FICA, POPIA, and sector-specific compliance monitoring with automated alerts and audit trails.' ),
);
?>

<section class="py-24 px-4 bg-gradient-mesh">
    <div class="container mx-auto max-w-6xl">
        <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">Complete Verification Suite</h2>
            <p class="text-lg text-muted-foreground">Everything you need to screen candidates, verify identities, and ensure compliance in one powerful platform</p>
        </div>

        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
            <?php foreach ( $features as $feature ) : ?>
                <div class="bg-card border border-border/50 rounded-lg p-6 space-y-4 hover:border-accent/50 transition-all duration-200 hover:shadow-xl hover:shadow-cyan-500/10 hover:-translate-y-1 group">
                    <div class="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-200">
                        <?php verigate_icon( $feature['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                    </div>
                    <h3 class="text-xl font-semibold text-foreground"><?php echo esc_html( $feature['title'] ); ?></h3>
                    <p class="text-muted-foreground leading-relaxed"><?php echo esc_html( $feature['desc'] ); ?></p>
                </div>
            <?php endforeach; ?>
        </div>
    </div>
</section>
