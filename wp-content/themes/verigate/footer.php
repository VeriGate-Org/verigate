<footer class="bg-primary text-primary-foreground py-12 px-4">
    <div class="container mx-auto max-w-6xl">

        <!-- Top: Brand + Newsletter -->
        <div class="grid md:grid-cols-2 gap-8 mb-12">
            <div class="space-y-4">
                <?php
                $footer_logo_id = get_theme_mod( 'custom_logo' );
                $footer_logo    = $footer_logo_id ? wp_get_attachment_image_url( $footer_logo_id, 'full' ) : '';
                ?>
                <?php if ( $footer_logo ) : ?>
                    <img src="<?php echo esc_url( $footer_logo ); ?>" alt="<?php bloginfo( 'name' ); ?>" class="h-12 w-auto brightness-0 invert">
                <?php else : ?>
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/verigate-logo-white.svg' ); ?>" alt="<?php bloginfo( 'name' ); ?>" class="h-12 w-auto">
                <?php endif; ?>

                <p class="text-primary-foreground/80 text-sm max-w-md">
                    Enterprise-grade background screening and verification platform trusted by South Africa's leading organisations.
                </p>

                <div class="flex gap-4 pt-2">
                    <?php
                    $linkedin = get_theme_mod( 'verigate_social_linkedin', '' );
                    $twitter  = get_theme_mod( 'verigate_social_twitter', '' );
                    ?>
                    <?php if ( $linkedin ) : ?>
                        <a href="<?php echo esc_url( $linkedin ); ?>" target="_blank" rel="noopener noreferrer" class="hover:text-accent transition-colors" aria-label="LinkedIn">
                            <?php verigate_icon( 'linkedin', 'w-5 h-5', 20 ); ?>
                        </a>
                    <?php endif; ?>
                    <?php if ( $twitter ) : ?>
                        <a href="<?php echo esc_url( $twitter ); ?>" target="_blank" rel="noopener noreferrer" class="hover:text-accent transition-colors" aria-label="Twitter">
                            <?php verigate_icon( 'twitter', 'w-5 h-5', 20 ); ?>
                        </a>
                    <?php endif; ?>
                </div>
            </div>

            <div>
                <h4 class="font-semibold mb-3 text-sm">Stay Updated</h4>
                <?php
                // CF7 newsletter form shortcode — set form ID in VeriGate Settings.
                if ( function_exists( 'wpcf7_contact_form' ) ) {
                    $newsletter_id = get_option( 'verigate_newsletter_form_id', '' );
                    if ( $newsletter_id ) {
                        echo do_shortcode( '[contact-form-7 id="' . intval( $newsletter_id ) . '" html_class="newsletter-form"]' );
                    }
                }
                ?>
            </div>
        </div>

        <!-- 5-column link grid -->
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-8 mb-12">
            <div>
                <h3 class="font-semibold mb-4">Products</h3>
                <ul class="space-y-2 text-sm text-primary-foreground/80">
                    <li><a href="<?php echo get_post_type_archive_link( 'verification' ); ?>" class="hover:text-accent transition-colors">Verification Types</a></li>
                    <li><a href="<?php echo get_post_type_archive_link( 'compliance' ); ?>" class="hover:text-accent transition-colors">Compliance</a></li>
                    <li><a href="<?php echo get_post_type_archive_link( 'fraud' ); ?>" class="hover:text-accent transition-colors">Fraud Prevention</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/platform/' ) ); ?>" class="hover:text-accent transition-colors">Platform</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/integrations/' ) ); ?>" class="hover:text-accent transition-colors">Integrations</a></li>
                </ul>
            </div>

            <div>
                <h3 class="font-semibold mb-4">Solutions</h3>
                <ul class="space-y-2 text-sm text-primary-foreground/80">
                    <li><a href="<?php echo esc_url( home_url( '/solutions/banking/' ) ); ?>" class="hover:text-accent transition-colors">Banking</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/solutions/fintech/' ) ); ?>" class="hover:text-accent transition-colors">Fintech</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/solutions/cryptocurrency/' ) ); ?>" class="hover:text-accent transition-colors">Cryptocurrency</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/solutions/gaming/' ) ); ?>" class="hover:text-accent transition-colors">Gaming</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/solutions/gig-economy/' ) ); ?>" class="hover:text-accent transition-colors">Gig Economy</a></li>
                    <li><a href="<?php echo get_post_type_archive_link( 'industry' ); ?>" class="hover:text-accent transition-colors text-accent">View All &rarr;</a></li>
                </ul>
            </div>

            <div>
                <h3 class="font-semibold mb-4">Company</h3>
                <ul class="space-y-2 text-sm text-primary-foreground/80">
                    <li><a href="<?php echo esc_url( home_url( '/about/' ) ); ?>" class="hover:text-accent transition-colors">About</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/careers/' ) ); ?>" class="hover:text-accent transition-colors">Careers</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/partner-program/' ) ); ?>" class="hover:text-accent transition-colors">Partners</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/events/' ) ); ?>" class="hover:text-accent transition-colors">Events</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="hover:text-accent transition-colors">Contact</a></li>
                </ul>
            </div>

            <div>
                <h3 class="font-semibold mb-4">Resources</h3>
                <ul class="space-y-2 text-sm text-primary-foreground/80">
                    <li><a href="<?php echo esc_url( home_url( '/blog/' ) ); ?>" class="hover:text-accent transition-colors">Blog</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/resources/' ) ); ?>" class="hover:text-accent transition-colors">Library</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/faqs/' ) ); ?>" class="hover:text-accent transition-colors">FAQs</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/technical-support/' ) ); ?>" class="hover:text-accent transition-colors">Support</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/roi-calculator/' ) ); ?>" class="hover:text-accent transition-colors">ROI Calculator</a></li>
                </ul>
            </div>

            <div>
                <h3 class="font-semibold mb-4">Legal</h3>
                <ul class="space-y-2 text-sm text-primary-foreground/80">
                    <li><a href="<?php echo esc_url( home_url( '/privacy/' ) ); ?>" class="hover:text-accent transition-colors">Privacy Policy</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/terms/' ) ); ?>" class="hover:text-accent transition-colors">Terms of Service</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/cookie-policy/' ) ); ?>" class="hover:text-accent transition-colors">Cookie Policy</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/south-africa/' ) ); ?>" class="hover:text-accent transition-colors">South Africa</a></li>
                    <li><a href="<?php echo esc_url( home_url( '/supported-documents/' ) ); ?>" class="hover:text-accent transition-colors">Supported Docs</a></li>
                </ul>
            </div>
        </div>

        <!-- Bottom Bar -->
        <div class="pt-8 border-t border-primary-foreground/20 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-primary-foreground/80">
            <p>&copy; <?php echo date( 'Y' ); ?> VeriGate (Pty) Ltd. All rights reserved. Built by <a href="https://arthmatic.co.za" target="_blank" rel="noopener noreferrer" class="hover:text-accent transition-colors">Arthmatic</a></p>
            <div class="flex gap-6">
                <a href="<?php echo esc_url( home_url( '/privacy/' ) ); ?>" class="hover:text-accent transition-colors">Privacy</a>
                <a href="<?php echo esc_url( home_url( '/terms/' ) ); ?>" class="hover:text-accent transition-colors">Terms</a>
                <a href="<?php echo esc_url( home_url( '/cookie-policy/' ) ); ?>" class="hover:text-accent transition-colors">Cookies</a>
            </div>
        </div>

    </div>
</footer>

</div><!-- .min-h-screen -->

<!-- Mobile Sticky CTA -->
<div class="vg-sticky-cta">
    <a href="<?php echo esc_url( home_url( '/request-demo/' ) ); ?>" class="inline-flex items-center justify-center px-4 py-2.5 text-sm font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 transition-all">
        Get a Demo
    </a>
    <a href="<?php echo esc_url( home_url( '/contact/' ) ); ?>" class="inline-flex items-center justify-center px-4 py-2.5 text-sm font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
        Contact Us
    </a>
</div>

<!-- Cookie Consent Banner (POPIA) -->
<div class="vg-cookie-banner" id="vg-cookie-banner" role="dialog" aria-label="Cookie consent">
    <div class="vg-cookie-banner__text">
        We use cookies to improve your experience and analyse site usage. By continuing to use this site, you agree to our
        <a href="<?php echo esc_url( home_url( '/cookie-policy/' ) ); ?>">Cookie Policy</a> and
        <a href="<?php echo esc_url( home_url( '/privacy/' ) ); ?>">Privacy Policy</a>.
    </div>
    <div class="vg-cookie-banner__actions">
        <button class="vg-cookie-banner__btn vg-cookie-banner__btn--accept" id="vg-cookie-accept">Accept</button>
        <button class="vg-cookie-banner__btn vg-cookie-banner__btn--decline" id="vg-cookie-decline">Decline</button>
    </div>
</div>

<script>
(function() {
    var banner = document.getElementById('vg-cookie-banner');
    if (!banner || localStorage.getItem('vg_cookie_consent')) return;
    setTimeout(function() { banner.classList.add('is-visible'); }, 1500);
    document.getElementById('vg-cookie-accept').addEventListener('click', function() {
        localStorage.setItem('vg_cookie_consent', 'accepted');
        banner.classList.remove('is-visible');
    });
    document.getElementById('vg-cookie-decline').addEventListener('click', function() {
        localStorage.setItem('vg_cookie_consent', 'declined');
        banner.classList.remove('is-visible');
    });
    // Add sticky CTA body class
    document.body.classList.add('has-sticky-cta');
})();
</script>

<?php wp_footer(); ?>
</body>
</html>
