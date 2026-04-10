<?php
/**
 * Template Name: About
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero -->
    <section class="relative pt-16 pb-20 overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-50"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4 text-center">
            <div class="space-y-6 animate-on-scroll fade-up">
                <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                    Modernising Background Screening
                    <span class="block text-accent mt-2">for South Africa</span>
                </h1>
                <p class="text-xl text-muted-foreground max-w-2xl mx-auto">
                    We're on a mission to make background verification faster, more accurate, and fully compliant for South African organisations.
                </p>
            </div>
        </div>
    </section>

    <!-- Stats Bar -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-8 animate-on-scroll fade-up">
                <?php
                $stats = [
                    [ 'value' => '200+', 'label' => 'Clients Nationwide' ],
                    [ 'value' => '50,000+', 'label' => 'Verifications Completed' ],
                    [ 'value' => '99.2%', 'label' => 'Accuracy Rate' ],
                    [ 'value' => '24hr', 'label' => 'Average Turnaround' ],
                ];
                foreach ( $stats as $stat ) :
                ?>
                    <div class="text-center">
                        <div class="text-4xl md:text-5xl font-bold text-accent mb-2"><?php echo esc_html( $stat['value'] ); ?></div>
                        <div class="text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- Our Story -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Our Story</h2>
            </div>
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="prose prose-lg max-w-none text-muted-foreground space-y-6 animate-on-scroll fade-up">
                    <p>
                        VeriGate was founded in 2020 in Cape Town by a team of compliance professionals and technology experts who saw a critical gap in South Africa's background screening industry. Traditional verification processes were slow, manual, and fragmented — taking days or weeks to complete while businesses needed results in hours.
                    </p>
                    <p>
                        We built VeriGate to change that. Our cloud-based platform connects directly to South Africa's key data sources — the Department of Home Affairs, South African Police Service, South African Qualifications Authority, credit bureaus, and professional bodies — to deliver fast, accurate, and POPIA-compliant verification results.
                    </p>
                    <p>
                        Today, we serve over 200 organisations across banking, insurance, telecoms, healthcare, and professional services. We've processed more than 50,000 verifications with a 99.2% accuracy rate, helping our clients make confident hiring decisions while meeting their regulatory obligations.
                    </p>
                    <p>
                        As South Africa's regulatory landscape continues to evolve, we remain committed to staying ahead — ensuring our platform meets every compliance requirement so our clients can focus on what they do best.
                    </p>
                </div>
                <div class="hidden lg:flex justify-center animate-on-scroll fade-in">
                    <?php get_template_part( 'template-parts/illustrations/south-africa-map' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Core Values -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Our Core Values</h2>
                <p class="text-lg text-muted-foreground">The principles that guide everything we do</p>
            </div>

            <?php
            $values = [
                [ 'icon' => 'target', 'title' => 'Accuracy', 'desc' => 'We are committed to delivering verification results you can trust, with a 99.2% accuracy rate across all check types.' ],
                [ 'icon' => 'zap', 'title' => 'Speed', 'desc' => 'Fast turnaround without compromising quality. Most verifications completed within 24 hours.' ],
                [ 'icon' => 'shield', 'title' => 'Compliance', 'desc' => 'Full adherence to POPIA, FICA, and international standards including ISO 27001 and SOC 2 Type II.' ],
                [ 'icon' => 'heart', 'title' => 'Integrity', 'desc' => 'We operate with transparency and honesty, building trust through our actions and long-term client relationships.' ],
            ];
            ?>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $values as $val ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <div class="mx-auto w-16 h-16 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                            <?php verigate_icon( $val['icon'], 'w-8 h-8 text-accent', 32 ); ?>
                        </div>
                        <h3 class="text-xl font-bold text-foreground mb-2"><?php echo esc_html( $val['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground"><?php echo esc_html( $val['desc'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Timeline -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Our Journey</h2>
                <p class="text-lg text-muted-foreground">Key milestones in our growth story</p>
            </div>

            <?php
            $milestones = [
                [ 'year' => '2020', 'title' => 'Company Founded', 'desc' => 'VeriGate was founded in Cape Town with a mission to modernise background screening in South Africa.' ],
                [ 'year' => '2021', 'title' => 'Platform Launch', 'desc' => 'Launched our cloud-based verification platform with integrations to DHA, SAPS, and SAQA.' ],
                [ 'year' => '2022', 'title' => '100 Clients Milestone', 'desc' => 'Reached 100 active clients across banking, insurance, telecoms, and professional services.' ],
                [ 'year' => '2023', 'title' => 'API & Bulk Processing', 'desc' => 'Launched REST API and bulk upload capabilities, enabling enterprise-scale verification processing.' ],
                [ 'year' => '2024', 'title' => 'ISO 27001 Certification', 'desc' => 'Achieved ISO 27001 certification and SOC 2 Type II compliance, reinforcing our security commitment.' ],
                [ 'year' => '2025', 'title' => '200+ Clients, 50K+ Verifications', 'desc' => 'Surpassed 200 clients and 50,000 verifications processed with 99.2% accuracy rate.' ],
            ];
            $count = count( $milestones );
            ?>
            <div class="space-y-8 stagger-list">
                <?php foreach ( $milestones as $idx => $m ) : ?>
                    <div class="flex gap-6 items-start">
                        <div class="flex flex-col items-center">
                            <div class="w-16 h-16 rounded-full bg-accent text-accent-foreground font-bold flex items-center justify-center text-lg shrink-0">
                                <?php echo esc_html( $m['year'] ); ?>
                            </div>
                            <?php if ( $idx < $count - 1 ) : ?>
                                <div class="w-0.5 h-full min-h-[60px] bg-border mt-4"></div>
                            <?php endif; ?>
                        </div>
                        <div class="flex-1 bg-card border border-border rounded-lg p-6">
                            <h3 class="text-lg font-bold text-foreground mb-1"><?php echo esc_html( $m['title'] ); ?></h3>
                            <p class="text-muted-foreground"><?php echo esc_html( $m['desc'] ); ?></p>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Testimonial -->
    <section class="py-16 px-4 bg-card">
        <div class="container mx-auto max-w-3xl">
            <div class="text-center animate-on-scroll fade-up">
                <!-- Stars -->
                <div class="flex justify-center gap-1 mb-6">
                    <?php for ( $i = 0; $i < 5; $i++ ) : ?>
                        <?php verigate_icon( 'star', 'w-5 h-5 text-amber-400 fill-amber-400', 20 ); ?>
                    <?php endfor; ?>
                </div>

                <blockquote class="text-xl md:text-2xl font-medium text-foreground leading-relaxed mb-8">
                    &ldquo;VeriGate has been instrumental in helping us build a culture of trust and compliance. Their team truly understands the South African regulatory landscape.&rdquo;
                </blockquote>

                <div class="flex flex-col items-center gap-4">
                    <div class="flex items-center gap-4">
                        <img
                            src="<?php echo esc_url( 'https://images.unsplash.com/photo-1586232902955-df204f34b36e?w=80&h=80&fit=crop&crop=face' ); ?>"
                            alt="<?php echo esc_attr( 'Sipho Molefe' ); ?>"
                            class="w-14 h-14 rounded-full object-cover ring-2 ring-border"
                            loading="lazy"
                        >
                        <div class="text-left">
                            <p class="font-semibold text-foreground">Sipho Molefe</p>
                            <p class="text-sm text-muted-foreground">Head of HR at Standard Bank</p>
                        </div>
                    </div>
                    <img
                        src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/standard-bank.png' ); ?>"
                        alt="<?php echo esc_attr( 'Standard Bank' ); ?>"
                        class="h-8 object-contain opacity-60"
                        loading="lazy"
                    >
                </div>
            </div>
        </div>
    </section>

    <!-- Leadership Team -->
    <?php
    $fallback_team = array(
        array(
            'name'  => 'Thabo Ndlovu',
            'role'  => 'Chief Executive Officer',
            'bio'   => 'Former head of risk at a Big Four bank. 15+ years in compliance and financial services.',
            'photo' => 'https://images.unsplash.com/photo-1659444003277-6cb0a5ffc8bd?w=200&h=200&fit=crop&crop=face',
        ),
        array(
            'name'  => 'Sarah van der Merwe',
            'role'  => 'Chief Technology Officer',
            'bio'   => 'Software architect with 12 years building enterprise SaaS platforms. Previously at Amazon Web Services.',
            'photo' => 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop&crop=face',
        ),
        array(
            'name'  => 'James Motsepe',
            'role'  => 'Chief Operating Officer',
            'bio'   => 'Operations leader specialising in scaling verification services across Africa.',
            'photo' => 'https://images.unsplash.com/photo-1698885765700-77c5a9b5cc8a?w=200&h=200&fit=crop&crop=face',
        ),
        array(
            'name'  => 'Priya Naidoo',
            'role'  => 'Head of Compliance',
            'bio'   => 'Certified information privacy professional. Expert in POPIA, FICA, and international data protection law.',
            'photo' => 'https://images.unsplash.com/photo-1659355894139-ca46ea6fa67a?w=200&h=200&fit=crop&crop=face',
        ),
    );

    $cpt_team = get_posts( array(
        'post_type'      => 'team',
        'posts_per_page' => 10,
        'orderby'        => 'menu_order',
        'order'          => 'ASC',
    ) );

    $team_items = array();

    if ( ! empty( $cpt_team ) ) {
        foreach ( $cpt_team as $idx => $member ) {
            $photo = get_field( 'photo', $member->ID );
            $fb    = $fallback_team[ $idx ] ?? null;
            $team_items[] = array(
                'name'  => $member->post_title,
                'role'  => get_field( 'role', $member->ID ) ?: ( $fb['role'] ?? '' ),
                'bio'   => get_field( 'bio', $member->ID ) ?: ( $fb['bio'] ?? '' ),
                'photo' => ( $photo && is_array( $photo ) ) ? ( $photo['sizes']['team-photo'] ?? $photo['url'] ) : ( $fb['photo'] ?? '' ),
            );
        }
    } else {
        $team_items = $fallback_team;
    }

    if ( ! empty( $team_items ) ) :
    ?>
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Leadership Team</h2>
                <p class="text-lg text-muted-foreground">Meet the experienced leaders driving our vision forward</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 stagger-list">
                <?php foreach ( $team_items as $member ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 text-center">
                        <?php if ( ! empty( $member['photo'] ) ) : ?>
                            <img src="<?php echo esc_url( $member['photo'] ); ?>"
                                 alt="<?php echo esc_attr( $member['name'] ); ?>"
                                 class="mx-auto w-32 h-32 rounded-full object-cover mb-4 ring-4 ring-primary/10"
                                 loading="lazy">
                        <?php else : ?>
                            <div class="mx-auto w-32 h-32 rounded-full bg-gradient-to-br from-primary to-accent mb-4 flex items-center justify-center text-4xl font-bold text-white">
                                <?php echo esc_html( verigate_initials( $member['name'] ) ); ?>
                            </div>
                        <?php endif; ?>
                        <h3 class="text-lg font-bold text-foreground"><?php echo esc_html( $member['name'] ); ?></h3>
                        <p class="text-sm font-semibold text-accent mb-2"><?php echo esc_html( $member['role'] ); ?></p>
                        <?php if ( ! empty( $member['bio'] ) ) : ?>
                            <p class="text-sm text-muted-foreground"><?php echo esc_html( $member['bio'] ); ?></p>
                        <?php endif; ?>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>
    <?php endif; ?>

    <!-- Certifications -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Certifications &amp; Compliance</h2>
                <p class="text-lg text-muted-foreground">Meeting the highest standards for data security and regulatory compliance</p>
            </div>

            <?php
            $cert_base = VERIGATE_URI . '/assets/img/logos/certifications/';
            $certs = [
                [ 'name' => 'POPIA', 'status' => 'Compliant', 'badge' => 'popia.svg' ],
                [ 'name' => 'ISO 27001', 'status' => 'Certified', 'badge' => 'iso-27001.svg' ],
                [ 'name' => 'SOC 2 Type II', 'status' => 'Compliant', 'badge' => 'soc2.svg' ],
                [ 'name' => 'FICA', 'status' => 'Compliant', 'badge' => 'fica.svg' ],
            ];
            ?>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-8 stagger-list">
                <?php foreach ( $certs as $cert ) : ?>
                    <div class="flex flex-col items-center justify-center p-6 border border-border rounded-lg bg-card hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200">
                        <img
                            src="<?php echo esc_url( $cert_base . $cert['badge'] ); ?>"
                            alt="<?php echo esc_attr( $cert['name'] ); ?> certification badge"
                            class="w-20 h-20 object-contain mb-4"
                            loading="lazy"
                        >
                        <span class="font-semibold text-center text-foreground"><?php echo esc_html( $cert['name'] ); ?></span>
                        <span class="text-xs text-muted-foreground mt-2"><?php echo esc_html( $cert['status'] ); ?></span>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- CTA -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl text-center animate-on-scroll fade-up">
            <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Join Our Team</h2>
            <p class="text-lg text-muted-foreground mb-8">We're always looking for talented individuals who share our passion for building secure, compliant verification solutions for South Africa.</p>
            <div class="flex flex-col sm:flex-row gap-4 justify-center">
                <a href="<?php echo esc_url( home_url( '/careers/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                    View Open Positions
                </a>
                <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                    Get in Touch
                </a>
            </div>
        </div>
    </section>

</main>

<?php get_footer();
