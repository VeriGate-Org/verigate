<?php
/**
 * Template Name: Homepage
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1">

    <?php get_template_part( 'template-parts/hero/hero-split', null, array(
        'title'          => 'Enterprise Verification Platform',
        'subtitle_html'  => '<span class="block mt-2 bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-500 bg-clip-text text-transparent">Trusted Background Screening for South Africa</span>',
        'description'    => 'Comprehensive criminal checks, qualification verification, employment history, and identity validation for modern South African businesses',
        'primary_label'  => 'Get Started',
        'primary_url'    => home_url( '/request-demo/' ),
        'secondary_label'=> 'View Platform',
        'secondary_url'  => home_url( '/platform/' ),
        'illustration'   => 'shield-verification',
    ) ); ?>

    <?php get_template_part( 'template-parts/sections/trust-indicators' ); ?>

    <!-- Social Proof Stat Row -->
    <section class="py-6 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="flex flex-wrap items-center justify-center gap-6 md:gap-10 text-sm text-muted-foreground animate-on-scroll fade-up">
                <div class="flex items-center gap-2">
                    <?php verigate_icon( 'users', 'w-4 h-4 text-accent', 16 ); ?>
                    <span><strong class="text-foreground">200+</strong> SA Clients</span>
                </div>
                <div class="flex items-center gap-2">
                    <?php verigate_icon( 'circle-check', 'w-4 h-4 text-accent', 16 ); ?>
                    <span><strong class="text-foreground">50,000+</strong> Verifications</span>
                </div>
                <div class="flex items-center gap-2">
                    <?php verigate_icon( 'target', 'w-4 h-4 text-accent', 16 ); ?>
                    <span><strong class="text-foreground">99.2%</strong> Accuracy</span>
                </div>
                <div class="flex items-center gap-2">
                    <?php verigate_icon( 'zap', 'w-4 h-4 text-accent', 16 ); ?>
                    <span><strong class="text-foreground">24hr</strong> Turnaround</span>
                </div>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/customer-logos' ); ?>

    <!-- Video Section -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-10 animate-on-scroll fade-up">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-secondary text-secondary-foreground mb-4">
                    <?php verigate_icon( 'zap', 'w-3 h-3 mr-1', 12 ); ?>
                    See It in Action
                </span>
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Watch How VeriGate Works</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">See how South African organisations use VeriGate to streamline background screening and stay compliant.</p>
            </div>
            <div class="animate-on-scroll fade-up">
                <div class="relative w-full rounded-xl overflow-hidden shadow-2xl border border-border aspect-video">
                    <iframe
                        src="https://www.youtube.com/embed/rYkb4d8l7i8"
                        title="VeriGate - Enterprise Background Screening Platform"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen
                        class="absolute inset-0 w-full h-full"
                        loading="lazy"
                    ></iframe>
                </div>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/features' ); ?>

    <!-- Process Pipeline Illustration -->
    <section class="py-16 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="animate-on-scroll fade-up">
                    <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">End-to-End Verification Pipeline</h2>
                    <p class="text-lg text-muted-foreground mb-6">From candidate submission to verified report — our platform handles the entire screening lifecycle with full audit trails and POPIA compliance built in.</p>
                    <div class="space-y-3">
                        <?php
                        $pipeline_points = array(
                            'Automated consent & data capture',
                            'Multi-source verification in parallel',
                            'QA review with compliance checks',
                            'Instant report delivery with audit trail',
                        );
                        foreach ( $pipeline_points as $point ) :
                        ?>
                            <div class="flex items-start gap-3">
                                <?php verigate_icon( 'circle-check', 'w-5 h-5 text-accent shrink-0 mt-0.5', 20 ); ?>
                                <span class="text-muted-foreground"><?php echo esc_html( $point ); ?></span>
                            </div>
                        <?php endforeach; ?>
                    </div>
                </div>
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/process-pipeline' ); ?>
                </div>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/data-sources' ); ?>

    <!-- Certification Badges -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-8">
                <h2 class="text-2xl font-bold text-foreground mb-2">Certified &amp; Compliant</h2>
                <p class="text-muted-foreground">Meeting the highest standards for data security and regulatory compliance</p>
            </div>
            <?php
            $cert_base = VERIGATE_URI . '/assets/img/logos/certifications/';
            $certs = array(
                array( 'name' => 'POPIA Compliant', 'desc' => 'Protection of Personal Information Act', 'badge' => 'popia.svg' ),
                array( 'name' => 'ISO 27001', 'desc' => 'Information Security Management', 'badge' => 'iso-27001.svg' ),
                array( 'name' => 'SOC 2 Type II', 'desc' => 'Service Organisation Controls', 'badge' => 'soc2.svg' ),
                array( 'name' => 'FICA Compliant', 'desc' => 'Financial Intelligence Centre Act', 'badge' => 'fica.svg' ),
            );
            ?>
            <div class="flex flex-wrap justify-center gap-6">
                <?php foreach ( $certs as $cert ) : ?>
                    <div class="flex items-center gap-4 p-4 border border-border rounded-lg bg-card hover:shadow-lg transition-shadow">
                        <img
                            src="<?php echo esc_url( $cert_base . $cert['badge'] ); ?>"
                            alt="<?php echo esc_attr( $cert['name'] ); ?>"
                            class="w-12 h-12 object-contain flex-shrink-0"
                            loading="lazy"
                        >
                        <div>
                            <p class="font-semibold text-sm"><?php echo esc_html( $cert['name'] ); ?></p>
                            <p class="text-xs text-muted-foreground"><?php echo esc_html( $cert['desc'] ); ?></p>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Enterprise-Grade Security -->
    <section class="py-16 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/security-lock' ); ?>
                </div>
                <div class="animate-on-scroll fade-up">
                    <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Enterprise-Grade Security</h2>
                    <p class="text-lg text-muted-foreground mb-6">Your data is protected by bank-grade security infrastructure. We invest heavily in security so you can trust us with your most sensitive verification data.</p>
                    <div class="space-y-3">
                        <?php
                        $security_points = array(
                            'AES-256 encryption at rest & TLS 1.3 in transit',
                            'ISO 27001 & SOC 2 Type II certified',
                            'POPIA-compliant data handling',
                            'Regular penetration testing & security audits',
                        );
                        foreach ( $security_points as $point ) :
                        ?>
                            <div class="flex items-start gap-3">
                                <?php verigate_icon( 'circle-check', 'w-5 h-5 text-accent shrink-0 mt-0.5', 20 ); ?>
                                <span class="text-muted-foreground"><?php echo esc_html( $point ); ?></span>
                            </div>
                        <?php endforeach; ?>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/stats-counter' ); ?>

    <!-- How It Works -->
    <section class="py-24 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-16 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-foreground">How VeriGate Works</h2>
                <p class="text-lg text-muted-foreground">Simple, secure, and thorough verification in four steps</p>
            </div>

            <?php
            $steps = [
                [ 'icon' => 'upload', 'step' => '01', 'title' => 'Submit Request', 'desc' => 'Upload candidate details and consent forms through our secure dashboard or API. Select the verification checks required.' ],
                [ 'icon' => 'scan-search', 'step' => '02', 'title' => 'Processing & Verification', 'desc' => 'Our team cross-references information with DHA, SAPS, SAQA, credit bureaus, and previous employers across South Africa.' ],
                [ 'icon' => 'shield-check', 'step' => '03', 'title' => 'Quality Assurance', 'desc' => 'Every result undergoes rigorous QA review by our compliance team to ensure accuracy and regulatory adherence.' ],
                [ 'icon' => 'file-text', 'step' => '04', 'title' => 'Report Delivered', 'desc' => 'Receive comprehensive, POPIA-compliant verification reports with clear findings, risk flags, and audit trails.' ],
            ];
            $count = count( $steps );
            ?>
            <div class="flex flex-col lg:flex-row items-stretch gap-0 stagger-list">
                <?php foreach ( $steps as $idx => $step ) : ?>
                    <div class="flex flex-col lg:flex-row items-center flex-1">
                        <!-- Step card -->
                        <div class="flex flex-col items-center text-center flex-1 px-4">
                            <div class="relative mb-4">
                                <div class="w-16 h-16 rounded-full bg-gradient-hero flex items-center justify-center text-primary-foreground shadow-lg">
                                    <?php verigate_icon( $step['icon'], 'w-7 h-7', 28 ); ?>
                                </div>
                                <div class="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-accent text-primary-foreground text-xs font-bold flex items-center justify-center border-2 border-background">
                                    <?php echo esc_html( $step['step'] ); ?>
                                </div>
                            </div>
                            <h3 class="text-lg font-semibold text-foreground mb-2"><?php echo esc_html( $step['title'] ); ?></h3>
                            <p class="text-sm text-muted-foreground leading-relaxed max-w-[220px]"><?php echo esc_html( $step['desc'] ); ?></p>
                        </div>

                        <?php if ( $idx < $count - 1 ) : ?>
                            <!-- Horizontal arrow — desktop -->
                            <div class="hidden lg:flex items-center justify-center flex-shrink-0 px-2">
                                <div class="w-8 h-0.5 bg-border"></div>
                                <?php verigate_icon( 'arrow-right', 'w-4 h-4 text-accent -ml-1', 16 ); ?>
                            </div>
                            <!-- Vertical arrow — mobile -->
                            <div class="flex lg:hidden items-center justify-center py-4">
                                <?php verigate_icon( 'arrow-down', 'w-5 h-5 text-accent', 20 ); ?>
                            </div>
                        <?php endif; ?>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/testimonials' ); ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'homepage' ) ); ?>

</main>

<?php get_footer();
