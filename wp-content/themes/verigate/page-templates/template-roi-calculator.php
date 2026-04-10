<?php
/**
 * Template Name: ROI Calculator
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero -->
    <section class="py-20 bg-gradient-to-br from-secondary via-background to-accent/5 relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4 text-center">
            <div class="animate-on-scroll fade-up space-y-6">
                <?php verigate_badge( 'Interactive Tool', 'company' ); ?>
                <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground mt-4">
                    ROI Calculator
                    <span class="block text-accent mt-2">Calculate Your Screening Savings</span>
                </h1>
                <p class="text-xl text-muted-foreground max-w-2xl mx-auto">
                    See how much your organisation could save by switching to VeriGate for background screening. Enter your current volumes and costs to get an instant estimate in ZAR.
                </p>
            </div>
        </div>
    </section>

    <!-- Calculator -->
    <section class="py-20 px-4" id="roi-calculator-app">
        <div class="container mx-auto max-w-6xl">
            <div class="grid lg:grid-cols-2 gap-8">

                <!-- Input Panel -->
                <div class="animate-on-scroll fade-up">
                    <div class="bg-card border border-border rounded-lg overflow-hidden">
                        <div class="p-6 border-b border-border">
                            <h2 class="text-xl font-bold text-foreground">Your Current Screening Costs</h2>
                            <p class="text-sm text-muted-foreground mt-1">Enter your current verification volumes and costs to calculate potential savings with VeriGate.</p>
                        </div>
                        <div class="p-6 space-y-8">

                            <!-- Monthly Volume Slider -->
                            <div class="space-y-3">
                                <div class="flex items-center justify-between">
                                    <label class="text-sm font-medium text-foreground" for="roi-volume">Monthly Verification Volume</label>
                                    <span class="text-lg font-bold text-accent" id="roi-volume-display">100</span>
                                </div>
                                <input type="range" id="roi-volume" min="10" max="5000" step="10" value="100"
                                    class="w-full h-2 bg-border rounded-lg appearance-none cursor-pointer accent-accent">
                                <div class="flex justify-between text-xs text-muted-foreground">
                                    <span>10</span>
                                    <span>5,000</span>
                                </div>
                                <div class="text-xs text-muted-foreground">
                                    Your pricing tier: <span class="font-semibold text-accent" id="roi-tier-label">Starter (R29/check)</span>
                                </div>
                            </div>

                            <!-- Current Cost -->
                            <div class="space-y-3">
                                <label class="text-sm font-medium text-foreground" for="roi-cost">Current Cost per Verification (ZAR)</label>
                                <div class="relative">
                                    <span class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground font-medium">R</span>
                                    <input type="number" id="roi-cost" min="1" max="500" value="45"
                                        class="w-full pl-8 pr-4 py-2.5 border border-border rounded-lg bg-background text-foreground focus:ring-2 focus:ring-accent/30 focus:border-accent outline-none transition-all">
                                </div>
                                <p class="text-xs text-muted-foreground">The average cost you currently pay per background check, including criminal, identity, and qualification verifications.</p>
                            </div>

                            <!-- Current Turnaround -->
                            <div class="space-y-3">
                                <label class="text-sm font-medium text-foreground" for="roi-turnaround">Current Average Turnaround (Days)</label>
                                <input type="number" id="roi-turnaround" min="1" max="30" value="5"
                                    class="w-full px-4 py-2.5 border border-border rounded-lg bg-background text-foreground focus:ring-2 focus:ring-accent/30 focus:border-accent outline-none transition-all">
                                <p class="text-xs text-muted-foreground">How many business days your current provider takes on average to return verification results.</p>
                            </div>

                        </div>
                    </div>
                </div>

                <!-- Results Panel -->
                <div class="animate-on-scroll fade-up space-y-6" style="animation-delay: 0.15s;">

                    <!-- Annual Savings Summary -->
                    <div class="bg-card border-2 border-accent rounded-lg p-6 text-center" id="roi-summary-card">
                        <p class="text-sm text-muted-foreground">Estimated Annual Savings</p>
                        <p class="text-5xl font-bold text-accent mt-2" id="roi-annual-savings">R19,200</p>
                        <div class="flex items-center justify-center gap-2 text-accent mt-3" id="roi-savings-pct-wrap">
                            <?php verigate_icon( 'trending-up', 'w-4 h-4', 16 ); ?>
                            <span class="text-sm font-medium" id="roi-savings-pct">36% cost reduction per check</span>
                        </div>
                        <p class="text-sm text-muted-foreground mt-2 hidden" id="roi-no-savings-msg">Your current rate is already competitive. Contact us for a custom Enterprise quote.</p>
                    </div>

                    <!-- Breakdown -->
                    <div class="bg-card border border-border rounded-lg overflow-hidden">
                        <div class="p-6 border-b border-border">
                            <h3 class="text-lg font-bold text-foreground">Results Breakdown</h3>
                        </div>
                        <div class="p-6 space-y-4">
                            <div class="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                                <div class="flex items-center gap-3">
                                    <?php verigate_icon( 'credit-card', 'w-5 h-5 text-accent flex-shrink-0', 20 ); ?>
                                    <div>
                                        <div class="font-semibold text-sm">Monthly Cost with VeriGate</div>
                                        <div class="text-xs text-muted-foreground" id="roi-monthly-detail">Based on 100 checks at R29/check</div>
                                    </div>
                                </div>
                                <div class="text-right">
                                    <div class="font-bold text-foreground" id="roi-monthly-vg-cost">R2,900</div>
                                    <div class="text-xs text-muted-foreground">per month</div>
                                </div>
                            </div>

                            <div class="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                                <div class="flex items-center gap-3">
                                    <?php verigate_icon( 'trending-up', 'w-5 h-5 text-accent flex-shrink-0', 20 ); ?>
                                    <div>
                                        <div class="font-semibold text-sm">Monthly Savings</div>
                                        <div class="text-xs text-muted-foreground">Difference vs your current cost</div>
                                    </div>
                                </div>
                                <div class="text-right">
                                    <div class="font-bold text-accent" id="roi-monthly-savings">R1,600</div>
                                    <div class="text-xs text-muted-foreground">per month</div>
                                </div>
                            </div>

                            <div class="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                                <div class="flex items-center gap-3">
                                    <?php verigate_icon( 'clock', 'w-5 h-5 text-accent flex-shrink-0', 20 ); ?>
                                    <div>
                                        <div class="font-semibold text-sm">Time Saved per Month</div>
                                        <div class="text-xs text-muted-foreground">Based on VeriGate 24hr avg turnaround</div>
                                    </div>
                                </div>
                                <div class="text-right">
                                    <div class="font-bold text-accent" id="roi-time-saved">400 days</div>
                                    <div class="text-xs text-muted-foreground">total across all checks</div>
                                </div>
                            </div>

                            <div class="flex items-center justify-between p-4 rounded-lg bg-secondary/50">
                                <div class="flex items-center gap-3">
                                    <?php verigate_icon( 'zap', 'w-5 h-5 text-accent flex-shrink-0', 20 ); ?>
                                    <div>
                                        <div class="font-semibold text-sm">Cost per Check Comparison</div>
                                        <div class="text-xs text-muted-foreground">Your current rate vs VeriGate</div>
                                    </div>
                                </div>
                                <div class="text-right">
                                    <span class="text-muted-foreground line-through mr-2" id="roi-old-rate">R45</span>
                                    <span class="font-bold text-accent" id="roi-new-rate">R29</span>
                                    <div class="text-xs text-muted-foreground">per verification</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Pricing Tiers -->
                    <div class="bg-card border border-border rounded-lg overflow-hidden">
                        <div class="p-6 border-b border-border">
                            <h3 class="text-lg font-bold text-foreground">VeriGate Pricing Tiers</h3>
                        </div>
                        <div class="p-6 space-y-3">
                            <div class="flex items-center justify-between p-3 rounded-lg text-sm" id="roi-tier-starter">
                                <div class="flex items-center gap-2">
                                    <span class="tier-check hidden"><?php verigate_icon( 'badge-check', 'w-4 h-4 text-accent', 16 ); ?></span>
                                    <div>
                                        <span class="font-semibold">Starter</span>
                                        <span class="text-muted-foreground ml-2">Up to 100 checks/month</span>
                                    </div>
                                </div>
                                <span class="font-bold tier-rate">R29/check</span>
                            </div>
                            <div class="flex items-center justify-between p-3 rounded-lg text-sm" id="roi-tier-pro">
                                <div class="flex items-center gap-2">
                                    <span class="tier-check hidden"><?php verigate_icon( 'badge-check', 'w-4 h-4 text-accent', 16 ); ?></span>
                                    <div>
                                        <span class="font-semibold">Professional</span>
                                        <span class="text-muted-foreground ml-2">101 – 1,000 checks/month</span>
                                    </div>
                                </div>
                                <span class="font-bold tier-rate">R22/check</span>
                            </div>
                            <div class="flex items-center justify-between p-3 rounded-lg text-sm" id="roi-tier-enterprise">
                                <div class="flex items-center gap-2">
                                    <span class="tier-check hidden"><?php verigate_icon( 'badge-check', 'w-4 h-4 text-accent', 16 ); ?></span>
                                    <div>
                                        <span class="font-semibold">Enterprise</span>
                                        <span class="text-muted-foreground ml-2">1,000+ checks/month</span>
                                    </div>
                                </div>
                                <span class="font-bold tier-rate">R18/check</span>
                            </div>
                        </div>
                    </div>

                    <!-- CTAs -->
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="flex-1 inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            Ready to Save? Request a Demo <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                        <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="flex-1 inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            View Full Pricing
                        </a>
                    </div>

                </div>
            </div>
        </div>
    </section>

    <!-- Why VeriGate -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Why Organisations Switch to VeriGate</h2>
                <p class="text-lg text-muted-foreground">Beyond cost savings, VeriGate delivers measurable improvements across your screening process.</p>
            </div>

            <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php
                $why_items = [
                    [ 'stat' => '24hr', 'label' => 'Average Turnaround', 'detail' => 'Most verifications completed within one business day via direct DHA, SAPS, and SAQA integrations.' ],
                    [ 'stat' => '99.2%', 'label' => 'Accuracy Rate', 'detail' => 'Verified against primary South African data sources for reliable, auditable results.' ],
                    [ 'stat' => '100%', 'label' => 'POPIA Compliant', 'detail' => 'Built-in consent management, data minimisation, and full POPIA compliance from day one.' ],
                    [ 'stat' => '200+', 'label' => 'SA Clients', 'detail' => 'Trusted by organisations across banking, insurance, telecoms, and professional services.' ],
                ];
                foreach ( $why_items as $item ) :
                ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="text-3xl font-bold text-accent mb-2"><?php echo esc_html( $item['stat'] ); ?></div>
                        <div class="font-semibold text-foreground mb-2"><?php echo esc_html( $item['label'] ); ?></div>
                        <p class="text-xs text-muted-foreground"><?php echo esc_html( $item['detail'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'Switching from manual background checks to VeriGate was the best decision we made. The integration with our existing HR systems was seamless and the cost savings are significant.',
        'name'    => 'Kabelo Mabena',
        'role'    => 'People Operations Manager',
        'company' => 'Capitec',
        'photo'   => 'https://images.unsplash.com/photo-1617244147299-5ef406921c35?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'capitec.svg',
    ) ); ?>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'company' ) ); ?>

</main>

<script>
(function() {
    var volumeEl      = document.getElementById('roi-volume');
    var costEl        = document.getElementById('roi-cost');
    var turnaroundEl  = document.getElementById('roi-turnaround');
    if (!volumeEl) return;

    function getRate(v) { return v <= 100 ? 29 : v <= 1000 ? 22 : 18; }
    function getTierLabel(v) {
        if (v <= 100) return 'Starter (R29/check)';
        if (v <= 1000) return 'Professional (R22/check)';
        return 'Enterprise (R18/check)';
    }
    function fmt(n) { return 'R' + Math.max(0, n).toLocaleString('en-ZA', { maximumFractionDigits: 0 }); }

    function update() {
        var vol  = parseInt(volumeEl.value) || 100;
        var cost = Math.max(1, parseInt(costEl.value) || 45);
        var turn = Math.max(1, parseInt(turnaroundEl.value) || 5);
        var rate = getRate(vol);

        var monthlyVG    = vol * rate;
        var monthlyCur   = vol * cost;
        var monthlySav   = monthlyCur - monthlyVG;
        var annualSav    = monthlySav * 12;
        var daysSaved    = Math.max(0, turn - 1) * vol;
        var pctReduction = cost > 0 ? Math.round(((cost - rate) / cost) * 100) : 0;

        document.getElementById('roi-volume-display').textContent = vol.toLocaleString();
        document.getElementById('roi-tier-label').textContent = getTierLabel(vol);
        document.getElementById('roi-annual-savings').textContent = fmt(annualSav);
        document.getElementById('roi-monthly-vg-cost').textContent = fmt(monthlyVG);
        document.getElementById('roi-monthly-detail').textContent = 'Based on ' + vol.toLocaleString() + ' checks at R' + rate + '/check';
        document.getElementById('roi-monthly-savings').textContent = fmt(monthlySav);
        document.getElementById('roi-time-saved').textContent = daysSaved.toLocaleString() + ' days';
        document.getElementById('roi-old-rate').textContent = 'R' + cost;
        document.getElementById('roi-new-rate').textContent = 'R' + rate;
        document.getElementById('roi-savings-pct').textContent = pctReduction + '% cost reduction per check';

        var hasSavings = annualSav > 0;
        document.getElementById('roi-savings-pct-wrap').classList.toggle('hidden', !hasSavings);
        document.getElementById('roi-no-savings-msg').classList.toggle('hidden', hasSavings);
        document.getElementById('roi-summary-card').classList.toggle('border-accent', hasSavings);
        document.getElementById('roi-summary-card').classList.toggle('border-border', !hasSavings);
        document.getElementById('roi-annual-savings').classList.toggle('text-accent', hasSavings);
        document.getElementById('roi-annual-savings').classList.toggle('text-foreground', !hasSavings);

        // Highlight active tier
        ['roi-tier-starter', 'roi-tier-pro', 'roi-tier-enterprise'].forEach(function(id) {
            var el = document.getElementById(id);
            el.classList.remove('bg-accent/10', 'ring-1', 'ring-accent/30', 'bg-secondary/30');
            el.querySelector('.tier-check').classList.add('hidden');
            el.querySelector('.tier-rate').classList.remove('text-accent');
            el.classList.add('bg-secondary/30');
        });
        var activeId = vol <= 100 ? 'roi-tier-starter' : vol <= 1000 ? 'roi-tier-pro' : 'roi-tier-enterprise';
        var activeEl = document.getElementById(activeId);
        activeEl.classList.remove('bg-secondary/30');
        activeEl.classList.add('bg-accent/10', 'ring-1', 'ring-accent/30');
        activeEl.querySelector('.tier-check').classList.remove('hidden');
        activeEl.querySelector('.tier-rate').classList.add('text-accent');
    }

    volumeEl.addEventListener('input', update);
    costEl.addEventListener('input', update);
    turnaroundEl.addEventListener('input', update);
    update();
})();
</script>

<?php get_footer();
