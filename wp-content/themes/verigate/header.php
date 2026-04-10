<!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo( 'charset' ); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class( 'bg-background text-foreground antialiased' ); ?>>
<?php wp_body_open(); ?>

<div class="min-h-screen flex flex-col">

<!-- Navigation -->
<nav class="fixed top-0 left-0 right-0 z-50 bg-background/95 backdrop-blur-sm border-b border-border shadow-sm" id="site-navigation" aria-label="<?php esc_attr_e( 'Primary Navigation', 'verigate' ); ?>">
    <div class="container mx-auto px-4">
        <div class="flex items-center justify-between h-16">

            <!-- Logo -->
            <a href="<?php echo esc_url( home_url( '/' ) ); ?>" class="flex items-center">
                <?php if ( has_custom_logo() ) : ?>
                    <?php
                    $logo_id  = get_theme_mod( 'custom_logo' );
                    $logo_url = wp_get_attachment_image_url( $logo_id, 'full' );
                    ?>
                    <img src="<?php echo esc_url( $logo_url ); ?>" alt="<?php bloginfo( 'name' ); ?>" class="h-8 md:h-10 w-auto">
                <?php else : ?>
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/verigate-logo.svg' ); ?>" alt="<?php bloginfo( 'name' ); ?>" class="h-8 md:h-10 w-auto">
                <?php endif; ?>
            </a>

            <!-- Desktop Navigation -->
            <div class="hidden lg:flex items-center gap-1" id="desktop-nav">

                <!-- Products -->
                <div class="relative" data-mega-trigger>
                    <button class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5" aria-expanded="false" data-dropdown="products">
                        Products
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="absolute top-full left-0 mt-1 w-[720px] bg-background border border-border rounded-lg shadow-xl p-6 hidden z-50" data-mega-dropdown="products">
                        <div class="grid grid-cols-3 gap-6">
                            <div>
                                <h3 class="text-xs font-semibold text-muted-foreground uppercase mb-3">Verification Types</h3>
                                <div class="space-y-1">
                                    <?php
                                    wp_nav_menu( array(
                                        'theme_location' => 'products',
                                        'container'      => false,
                                        'items_wrap'     => '%3$s',
                                        'depth'          => 1,
                                        'fallback_cb'    => 'verigate_products_fallback',
                                    ) );
                                    ?>
                                </div>
                            </div>
                            <div>
                                <h3 class="text-xs font-semibold text-muted-foreground uppercase mb-3">Compliance</h3>
                                <div class="space-y-1">
                                    <?php
                                    $compliance_posts = get_posts( array( 'post_type' => 'compliance', 'posts_per_page' => 6, 'orderby' => 'menu_order', 'order' => 'ASC' ) );
                                    foreach ( $compliance_posts as $p ) :
                                    ?>
                                        <a href="<?php echo get_permalink( $p ); ?>" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"><?php echo esc_html( $p->post_title ); ?></a>
                                    <?php endforeach; ?>
                                    <a href="<?php echo get_post_type_archive_link( 'compliance' ); ?>" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm text-accent font-medium">View All &rarr;</a>
                                </div>
                            </div>
                            <div>
                                <h3 class="text-xs font-semibold text-muted-foreground uppercase mb-3">Fraud Prevention</h3>
                                <div class="space-y-1">
                                    <?php
                                    $fraud_posts = get_posts( array( 'post_type' => 'fraud', 'posts_per_page' => 6, 'orderby' => 'menu_order', 'order' => 'ASC' ) );
                                    foreach ( $fraud_posts as $p ) :
                                    ?>
                                        <a href="<?php echo get_permalink( $p ); ?>" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"><?php echo esc_html( $p->post_title ); ?></a>
                                    <?php endforeach; ?>
                                    <a href="<?php echo get_post_type_archive_link( 'fraud' ); ?>" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm text-accent font-medium">View All &rarr;</a>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4 pt-4 border-t border-border flex gap-6">
                            <?php
                            $platform_page = get_page_by_path( 'platform' );
                            if ( $platform_page ) :
                            ?>
                                <a href="<?php echo get_permalink( $platform_page ); ?>" class="text-sm font-medium text-accent hover:underline">Platform Overview &rarr;</a>
                            <?php endif; ?>
                            <?php
                            $integrations_page = get_page_by_path( 'integrations' );
                            if ( $integrations_page ) :
                            ?>
                                <a href="<?php echo get_permalink( $integrations_page ); ?>" class="text-sm font-medium text-accent hover:underline">Integrations &rarr;</a>
                            <?php endif; ?>
                        </div>
                    </div>
                </div>

                <!-- Solutions -->
                <div class="relative" data-mega-trigger>
                    <button class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5" aria-expanded="false" data-dropdown="solutions">
                        Solutions
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="absolute top-full left-0 mt-1 w-[420px] bg-background border border-border rounded-lg shadow-xl p-4 hidden z-50" data-mega-dropdown="solutions">
                        <h3 class="text-xs font-semibold text-muted-foreground uppercase mb-3 px-3">Industries</h3>
                        <div class="grid grid-cols-2 gap-1">
                            <?php
                            $industry_posts = get_posts( array( 'post_type' => 'industry', 'posts_per_page' => 12, 'orderby' => 'menu_order', 'order' => 'ASC' ) );
                            foreach ( $industry_posts as $p ) :
                            ?>
                                <a href="<?php echo get_permalink( $p ); ?>" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium"><?php echo esc_html( $p->post_title ); ?></a>
                            <?php endforeach; ?>
                        </div>
                        <div class="mt-3 pt-3 border-t border-border">
                            <a href="<?php echo get_post_type_archive_link( 'industry' ); ?>" class="block px-3 py-2 text-sm text-accent font-medium hover:underline">View All Solutions &rarr;</a>
                        </div>
                    </div>
                </div>

                <!-- Resources -->
                <div class="relative" data-mega-trigger>
                    <button class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5" aria-expanded="false" data-dropdown="resources">
                        Resources
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="absolute top-full left-0 mt-1 w-[380px] bg-background border border-border rounded-lg shadow-xl p-4 hidden z-50" data-mega-dropdown="resources">
                        <div class="space-y-1">
                            <?php
                            $resources_items = array(
                                array( 'title' => 'Blog', 'desc' => 'Industry insights and updates', 'url' => get_permalink( get_option( 'page_for_posts' ) ) ?: home_url( '/blog/' ) ),
                                array( 'title' => 'Resource Library', 'desc' => 'Guides, whitepapers, and e-books', 'url' => home_url( '/resources/' ) ),
                                array( 'title' => 'FAQs', 'desc' => 'Frequently asked questions', 'url' => home_url( '/faqs/' ) ),
                                array( 'title' => 'Technical Support', 'desc' => 'Help and support resources', 'url' => home_url( '/technical-support/' ) ),
                                array( 'title' => 'Supported Documents', 'desc' => 'Document types we verify', 'url' => home_url( '/supported-documents/' ) ),
                                array( 'title' => 'ROI Calculator', 'desc' => 'Calculate your savings', 'url' => home_url( '/roi-calculator/' ) ),
                            );
                            foreach ( $resources_items as $item ) :
                            ?>
                                <a href="<?php echo esc_url( $item['url'] ); ?>" class="block p-3 rounded-md hover:bg-accent/5 transition-colors">
                                    <div class="font-medium text-sm mb-1"><?php echo esc_html( $item['title'] ); ?></div>
                                    <div class="text-xs text-muted-foreground"><?php echo esc_html( $item['desc'] ); ?></div>
                                </a>
                            <?php endforeach; ?>
                        </div>
                    </div>
                </div>

                <!-- Company -->
                <div class="relative" data-mega-trigger>
                    <button class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5" aria-expanded="false" data-dropdown="company">
                        Company
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="absolute top-full right-0 mt-1 w-[300px] bg-background border border-border rounded-lg shadow-xl p-4 hidden z-50" data-mega-dropdown="company">
                        <div class="space-y-1">
                            <?php
                            $company_items = array(
                                array( 'title' => 'About', 'desc' => 'Our mission and team', 'url' => home_url( '/about/' ) ),
                                array( 'title' => 'Careers', 'desc' => 'Join our team', 'url' => home_url( '/careers/' ) ),
                                array( 'title' => 'Partner Program', 'desc' => 'Become a partner', 'url' => home_url( '/partner-program/' ) ),
                                array( 'title' => 'Events', 'desc' => 'Upcoming events and webinars', 'url' => home_url( '/events/' ) ),
                                array( 'title' => 'Contact', 'desc' => 'Get in touch', 'url' => home_url( '/contact/' ) ),
                            );
                            foreach ( $company_items as $item ) :
                            ?>
                                <a href="<?php echo esc_url( $item['url'] ); ?>" class="block p-3 rounded-md hover:bg-accent/5 transition-colors">
                                    <div class="font-medium text-sm mb-1"><?php echo esc_html( $item['title'] ); ?></div>
                                    <div class="text-xs text-muted-foreground"><?php echo esc_html( $item['desc'] ); ?></div>
                                </a>
                            <?php endforeach; ?>
                        </div>
                    </div>
                </div>

                <!-- Pricing -->
                <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5">
                    Pricing
                </a>

                <!-- CTA Button -->
                <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="ml-4 inline-flex items-center justify-center px-5 py-2.5 text-sm font-semibold rounded-lg bg-accent text-white hover:bg-accent/90 shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105">
                    Get Started
                </a>
            </div>

            <!-- Mobile Menu Button -->
            <button class="lg:hidden p-2 rounded-md hover:bg-accent/10 transition-colors" id="mobile-menu-toggle" aria-label="Toggle menu" aria-expanded="false">
                <svg class="w-6 h-6 text-foreground menu-open-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><line x1="4" x2="20" y1="12" y2="12"/><line x1="4" x2="20" y1="6" y2="6"/><line x1="4" x2="20" y1="18" y2="18"/></svg>
                <svg class="w-6 h-6 text-foreground menu-close-icon hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>

        </div>

        <!-- Mobile Menu -->
        <div class="lg:hidden hidden py-4 border-t border-border max-h-[80vh] overflow-y-auto" id="mobile-menu">
            <div class="flex flex-col gap-1">

                <!-- Mobile: Products -->
                <div data-mobile-accordion>
                    <button class="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground" data-mobile-toggle>
                        Products
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="pl-4 pb-2 space-y-1 hidden" data-mobile-panel>
                        <p class="text-xs font-semibold text-muted-foreground uppercase px-2 pt-1">Verification Types</p>
                        <?php
                        $ver_posts = get_posts( array( 'post_type' => 'verification', 'posts_per_page' => 8, 'orderby' => 'menu_order', 'order' => 'ASC' ) );
                        foreach ( $ver_posts as $p ) :
                        ?>
                            <a href="<?php echo get_permalink( $p ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $p->post_title ); ?></a>
                        <?php endforeach; ?>
                        <a href="<?php echo get_post_type_archive_link( 'verification' ); ?>" class="block px-2 py-2 text-sm text-accent font-medium">View All &rarr;</a>

                        <p class="text-xs font-semibold text-muted-foreground uppercase px-2 pt-3">Compliance</p>
                        <?php foreach ( $compliance_posts ?? array() as $p ) : ?>
                            <a href="<?php echo get_permalink( $p ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $p->post_title ); ?></a>
                        <?php endforeach; ?>
                        <a href="<?php echo get_post_type_archive_link( 'compliance' ); ?>" class="block px-2 py-2 text-sm text-accent font-medium">View All &rarr;</a>

                        <p class="text-xs font-semibold text-muted-foreground uppercase px-2 pt-3">Fraud Prevention</p>
                        <?php foreach ( $fraud_posts ?? array() as $p ) : ?>
                            <a href="<?php echo get_permalink( $p ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $p->post_title ); ?></a>
                        <?php endforeach; ?>
                        <a href="<?php echo get_post_type_archive_link( 'fraud' ); ?>" class="block px-2 py-2 text-sm text-accent font-medium">View All &rarr;</a>
                    </div>
                </div>

                <!-- Mobile: Solutions -->
                <div data-mobile-accordion>
                    <button class="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground" data-mobile-toggle>
                        Solutions
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="pl-4 pb-2 space-y-1 hidden" data-mobile-panel>
                        <?php foreach ( $industry_posts ?? array() as $p ) : ?>
                            <a href="<?php echo get_permalink( $p ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $p->post_title ); ?></a>
                        <?php endforeach; ?>
                        <a href="<?php echo get_post_type_archive_link( 'industry' ); ?>" class="block px-2 py-2 text-sm text-accent font-medium">View All Solutions &rarr;</a>
                    </div>
                </div>

                <!-- Mobile: Resources -->
                <div data-mobile-accordion>
                    <button class="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground" data-mobile-toggle>
                        Resources
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="pl-4 pb-2 space-y-1 hidden" data-mobile-panel>
                        <?php foreach ( $resources_items ?? array() as $item ) : ?>
                            <a href="<?php echo esc_url( $item['url'] ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $item['title'] ); ?></a>
                        <?php endforeach; ?>
                    </div>
                </div>

                <!-- Mobile: Company -->
                <div data-mobile-accordion>
                    <button class="flex items-center justify-between w-full px-2 py-3 text-sm font-semibold text-foreground" data-mobile-toggle>
                        Company
                        <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                    </button>
                    <div class="pl-4 pb-2 space-y-1 hidden" data-mobile-panel>
                        <?php foreach ( $company_items ?? array() as $item ) : ?>
                            <a href="<?php echo esc_url( $item['url'] ); ?>" class="block px-2 py-2 text-sm text-foreground hover:text-accent"><?php echo esc_html( $item['title'] ); ?></a>
                        <?php endforeach; ?>
                    </div>
                </div>

                <!-- Mobile: Pricing -->
                <a href="<?php echo esc_url( home_url( '/pricing/' ) ); ?>" class="px-2 py-3 text-sm font-semibold text-foreground hover:text-accent transition-colors">
                    Pricing
                </a>

                <!-- Mobile: CTA -->
                <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="w-full mt-4 inline-flex items-center justify-center px-5 py-2.5 text-sm font-semibold rounded-lg bg-accent text-white hover:bg-accent/90 shadow-md transition-all duration-200">
                    Get Started
                </a>
            </div>
        </div>

    </div>
</nav>
