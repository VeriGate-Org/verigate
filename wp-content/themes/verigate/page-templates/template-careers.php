<?php
/**
 * Template Name: Careers
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero — Split Layout -->
    <section class="relative pt-16 pb-20 overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-50"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid lg:grid-cols-2 gap-12 items-center">
                <div class="space-y-6 animate-on-scroll fade-up">
                    <?php verigate_badge( 'Careers at VeriGate', 'company' ); ?>
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                        Join Our Team
                        <span class="block text-accent mt-2">Build South Africa's Most Trusted Verification Platform</span>
                    </h1>
                    <p class="text-xl text-muted-foreground max-w-xl">
                        Help us build South Africa's most trusted verification platform. We're looking for passionate people who care about security, compliance, and great technology.
                    </p>
                    <div class="flex gap-4 flex-wrap">
                        <a href="#open-positions" class="inline-flex items-center justify-center px-8 py-3.5 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            View Open Positions
                            <?php verigate_icon( 'arrow-down', 'w-4 h-4 ml-2', 16 ); ?>
                        </a>
                    </div>
                </div>
                <div class="hidden lg:flex items-center justify-center">
                    <?php get_template_part( 'template-parts/illustrations/team-collaboration' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Stats Bar -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-8 animate-on-scroll fade-up">
                <?php
                $career_stats = array(
                    array( 'value' => '40+', 'label' => 'Team Members' ),
                    array( 'value' => '5',   'label' => 'Departments' ),
                    array( 'value' => '3',   'label' => 'Office Locations' ),
                    array( 'value' => '2020', 'label' => 'Founded' ),
                );
                foreach ( $career_stats as $stat ) :
                ?>
                    <div class="text-center">
                        <div class="text-4xl md:text-5xl font-bold text-accent mb-2"><?php echo esc_html( $stat['value'] ); ?></div>
                        <div class="text-sm text-muted-foreground"><?php echo esc_html( $stat['label'] ); ?></div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Why Work at VeriGate -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="max-w-2xl mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold mb-4 text-foreground">Why Work at VeriGate?</h2>
                <p class="text-lg text-muted-foreground">We offer more than a job. We offer the chance to make a real difference in how South Africa verifies trust.</p>
            </div>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 stagger-list">
                <?php
                $perks = array(
                    array(
                        'icon'  => 'zap',
                        'title' => 'Meaningful Impact',
                        'desc'  => 'Help protect millions of South Africans from fraud and build trust in digital identity. Your work directly improves lives.',
                    ),
                    array(
                        'icon'  => 'users',
                        'title' => 'World-Class Team',
                        'desc'  => 'Work alongside talented engineers, product designers, and domain experts from across South Africa and beyond.',
                    ),
                    array(
                        'icon'  => 'globe',
                        'title' => 'Remote-Friendly',
                        'desc'  => 'Flexible working arrangements with offices in Cape Town and Johannesburg. Work where you do your best thinking.',
                    ),
                    array(
                        'icon'  => 'target',
                        'title' => 'Growth & Learning',
                        'desc'  => 'Clear career paths, generous learning budgets, conference attendance, and regular internal knowledge-sharing sessions.',
                    ),
                    array(
                        'icon'  => 'shield',
                        'title' => 'Mission-Driven Culture',
                        'desc'  => 'We\'re building critical infrastructure that makes South Africa safer and more trusted. Every team member matters.',
                    ),
                    array(
                        'icon'  => 'activity',
                        'title' => 'Modern Tech Stack',
                        'desc'  => 'Work with cutting-edge technology, best practices, and tools that empower you to ship great work every day.',
                    ),
                );
                foreach ( $perks as $perk ) :
                    get_template_part( 'template-parts/cards/feature-card', null, array(
                        'icon'        => $perk['icon'],
                        'title'       => $perk['title'],
                        'description' => $perk['desc'],
                        'category'    => 'company',
                    ) );
                endforeach;
                ?>
            </div>
        </div>
    </section>

    <!-- Life at VeriGate — Culture Section -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Life at VeriGate</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">A workplace where innovation thrives, collaboration is the norm, and every team member can make a difference.</p>
            </div>

            <div class="grid md:grid-cols-3 gap-6 stagger-list">
                <?php
                $culture_cards = array(
                    array(
                        'title'    => 'Team Collaboration',
                        'gradient' => 'bg-gradient-to-br from-primary to-accent',
                        'icon'     => 'users',
                        'desc'     => 'Cross-functional teams working together to solve complex verification challenges',
                    ),
                    array(
                        'title'    => 'Innovation Days',
                        'gradient' => 'bg-gradient-to-br from-accent to-primary',
                        'icon'     => 'zap',
                        'desc'     => 'Regular hackathons and innovation sprints to push the boundaries of verification technology',
                    ),
                    array(
                        'title'    => 'Community Impact',
                        'gradient' => 'bg-gradient-to-br from-primary/80 to-accent/80',
                        'icon'     => 'heart',
                        'desc'     => 'Giving back through mentorship programmes and community security initiatives',
                    ),
                );
                foreach ( $culture_cards as $card ) :
                ?>
                    <div class="<?php echo esc_attr( $card['gradient'] ); ?> rounded-xl h-64 flex items-end relative overflow-hidden group">
                        <!-- Icon overlay -->
                        <div class="absolute top-6 right-6 opacity-20 group-hover:opacity-30 transition-opacity duration-300">
                            <?php verigate_icon( $card['icon'], 'w-16 h-16 text-white', 64 ); ?>
                        </div>
                        <!-- Text overlay -->
                        <div class="relative z-10 p-6 w-full bg-gradient-to-t from-black/50 to-transparent backdrop-blur-sm rounded-b-xl">
                            <div class="flex items-center gap-2 mb-2">
                                <?php verigate_icon( $card['icon'], 'w-5 h-5 text-white', 20 ); ?>
                                <h3 class="text-lg font-bold text-white"><?php echo esc_html( $card['title'] ); ?></h3>
                            </div>
                            <p class="text-sm text-white/90"><?php echo esc_html( $card['desc'] ); ?></p>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Open Positions -->
    <section id="open-positions" class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Open Positions</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">Find your next opportunity. We're growing fast and looking for talented people across all departments.</p>
            </div>

            <?php
            $jobs = array(
                array(
                    'title'      => 'Senior Backend Engineer',
                    'department' => 'Engineering',
                    'location'   => 'Cape Town',
                    'type'       => 'Full-time',
                ),
                array(
                    'title'      => 'Product Designer',
                    'department' => 'Product',
                    'location'   => 'Remote',
                    'type'       => 'Full-time',
                ),
                array(
                    'title'      => 'Compliance Analyst',
                    'department' => 'Legal & Compliance',
                    'location'   => 'Johannesburg',
                    'type'       => 'Full-time',
                ),
                array(
                    'title'      => 'DevOps Engineer',
                    'department' => 'Engineering',
                    'location'   => 'Cape Town',
                    'type'       => 'Full-time',
                ),
                array(
                    'title'      => 'Business Development Manager',
                    'department' => 'Sales',
                    'location'   => 'Johannesburg',
                    'type'       => 'Full-time',
                ),
            );
            ?>

            <div class="space-y-4 stagger-list">
                <?php foreach ( $jobs as $job ) :
                    $subject = rawurlencode( 'Application: ' . $job['title'] );
                    $mailto  = 'mailto:careers@verigate.co.za?subject=' . $subject;
                ?>
                    <div class="bg-card border border-border rounded-lg p-6 hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 group">
                        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                            <div class="space-y-2">
                                <h3 class="text-lg font-bold text-foreground group-hover:text-accent transition-colors"><?php echo esc_html( $job['title'] ); ?></h3>
                                <div class="flex flex-wrap items-center gap-2">
                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-accent/10 text-accent">
                                        <?php verigate_icon( 'building-2', 'w-3 h-3 mr-1', 12 ); ?>
                                        <?php echo esc_html( $job['department'] ); ?>
                                    </span>
                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary text-secondary-foreground">
                                        <?php verigate_icon( 'map-pin', 'w-3 h-3 mr-1', 12 ); ?>
                                        <?php echo esc_html( $job['location'] ); ?>
                                    </span>
                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary text-secondary-foreground">
                                        <?php verigate_icon( 'clock', 'w-3 h-3 mr-1', 12 ); ?>
                                        <?php echo esc_html( $job['type'] ); ?>
                                    </span>
                                </div>
                            </div>
                            <a href="<?php echo esc_url( $mailto ); ?>" class="inline-flex items-center justify-center px-6 py-2.5 text-sm font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-sm transition-all shrink-0">
                                Apply
                                <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                            </a>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>

            <!-- General application prompt -->
            <div class="mt-10 text-center animate-on-scroll fade-up">
                <div class="bg-card border border-border rounded-lg p-6 inline-block">
                    <div class="flex items-center gap-3 text-muted-foreground">
                        <?php verigate_icon( 'mail', 'w-5 h-5 text-accent', 20 ); ?>
                        <p class="text-base">
                            Don't see a fit? Send your CV to
                            <a href="mailto:careers@verigate.co.za" class="text-accent font-semibold hover:underline">careers@verigate.co.za</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Meet Our Leadership -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-6xl">
            <div class="text-center mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Meet Our Leadership</h2>
                <p class="text-lg text-muted-foreground max-w-2xl mx-auto">Experienced leaders driving our vision to modernise background screening in South Africa.</p>
            </div>

            <?php
            $fallback_team = array(
                array(
                    'name'  => 'Thabo Ndlovu',
                    'role'  => 'Chief Executive Officer',
                    'photo' => 'https://images.unsplash.com/photo-1659444003277-6cb0a5ffc8bd?w=200&h=200&fit=crop&crop=face',
                ),
                array(
                    'name'  => 'Sarah van der Merwe',
                    'role'  => 'Chief Technology Officer',
                    'photo' => 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop&crop=face',
                ),
                array(
                    'name'  => 'James Motsepe',
                    'role'  => 'Chief Operating Officer',
                    'photo' => 'https://images.unsplash.com/photo-1698885765700-77c5a9b5cc8a?w=200&h=200&fit=crop&crop=face',
                ),
                array(
                    'name'  => 'Priya Naidoo',
                    'role'  => 'Head of Compliance',
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
                        'photo' => ( $photo && is_array( $photo ) ) ? ( $photo['sizes']['team-photo'] ?? $photo['url'] ) : ( $fb['photo'] ?? '' ),
                    );
                }
            } else {
                $team_items = $fallback_team;
            }
            ?>

            <div class="grid grid-cols-2 lg:grid-cols-4 gap-8 stagger-list">
                <?php foreach ( $team_items as $member ) : ?>
                    <div class="text-center group">
                        <?php if ( ! empty( $member['photo'] ) ) : ?>
                            <img src="<?php echo esc_url( $member['photo'] ); ?>"
                                 alt="<?php echo esc_attr( $member['name'] ); ?>"
                                 class="mx-auto w-24 h-24 rounded-full object-cover mb-4 ring-4 ring-primary/10 group-hover:ring-accent/30 transition-all duration-200"
                                 loading="lazy">
                        <?php else : ?>
                            <div class="mx-auto w-24 h-24 rounded-full bg-gradient-to-br from-primary to-accent mb-4 flex items-center justify-center text-2xl font-bold text-white">
                                <?php echo esc_html( verigate_initials( $member['name'] ) ); ?>
                            </div>
                        <?php endif; ?>
                        <h3 class="text-base font-bold text-foreground"><?php echo esc_html( $member['name'] ); ?></h3>
                        <p class="text-sm text-accent font-semibold"><?php echo esc_html( $member['role'] ); ?></p>
                    </div>
                <?php endforeach; ?>
            </div>

            <div class="text-center mt-8 animate-on-scroll fade-up">
                <a href="<?php echo esc_url( home_url( '/about/' ) ); ?>" class="inline-flex items-center justify-center px-6 py-2.5 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                    Learn More About Our Team
                    <?php verigate_icon( 'arrow-right', 'w-4 h-4 ml-2', 16 ); ?>
                </a>
            </div>
        </div>
    </section>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'Joining VeriGate was one of the best career decisions I\'ve made. The team is incredibly talented, the mission is meaningful, and there\'s real room to grow and make an impact.',
        'name'    => 'Sarah van der Merwe',
        'role'    => 'Chief Technology Officer',
        'company' => 'VeriGate',
        'photo'   => 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&h=80&fit=crop&crop=face',
        'logo'    => '',
    ) ); ?>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <!-- CTA -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'careers' ) ); ?>

</main>

<?php get_footer();
