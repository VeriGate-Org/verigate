<?php
/**
 * Template Name: Contact
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <!-- Hero — Split Layout -->
    <section class="relative pt-16 pb-20 overflow-hidden">
        <div class="absolute inset-0 bg-gradient-mesh opacity-50"></div>
        <div class="container mx-auto max-w-6xl relative z-10 px-4">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
                <!-- Left: Content -->
                <div class="space-y-6 animate-on-scroll fade-up">
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">Get in Touch</h1>
                    <p class="text-xl text-muted-foreground">Have questions about our verification services? We're here to help. Reach out to our team and we'll get back to you within one business day.</p>
                    <div class="flex flex-col sm:flex-row gap-4">
                        <a href="tel:+27215550123" class="inline-flex items-center justify-center px-6 py-3 text-base font-semibold rounded-lg bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg transition-all">
                            <?php verigate_icon( 'phone', 'w-4 h-4 mr-2', 16 ); ?>
                            Call Us
                        </a>
                        <a href="mailto:info@verigate.co.za" class="inline-flex items-center justify-center px-6 py-3 text-base font-semibold rounded-lg border border-border text-foreground hover:bg-accent/5 transition-all">
                            <?php verigate_icon( 'mail', 'w-4 h-4 mr-2', 16 ); ?>
                            Email Us
                        </a>
                    </div>
                </div>

                <!-- Right: Illustration -->
                <div class="hidden lg:flex items-center justify-center">
                    <?php get_template_part( 'template-parts/illustrations/south-africa-map' ); ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Trust Bar -->
    <?php get_template_part( 'template-parts/sections/trust-bar' ); ?>

    <!-- Contact Methods -->
    <section class="py-12 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <?php
            $methods = [
                [ 'icon' => 'mail', 'title' => 'Email', 'primary' => 'info@verigate.co.za', 'secondary' => 'General enquiries', 'href' => 'mailto:info@verigate.co.za' ],
                [ 'icon' => 'phone', 'title' => 'Phone', 'primary' => '+27 (0)21 555 0123', 'secondary' => 'Mon-Fri, 08:00-17:00 SAST', 'href' => 'tel:+27215550123' ],
                [ 'icon' => 'mail', 'title' => 'Support', 'primary' => 'support@verigate.co.za', 'secondary' => 'Technical support', 'href' => 'mailto:support@verigate.co.za' ],
                [ 'icon' => 'message-square', 'title' => 'Live Chat', 'primary' => 'Chat with us', 'secondary' => 'Mon-Fri, 08:00-17:00 SAST', 'href' => '#' ],
            ];
            ?>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 stagger-list">
                <?php foreach ( $methods as $m ) : ?>
                    <div class="bg-card border border-border rounded-lg p-6 hover:shadow-lg transition-shadow">
                        <div class="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center mb-4">
                            <?php verigate_icon( $m['icon'], 'w-6 h-6 text-accent', 24 ); ?>
                        </div>
                        <h3 class="text-lg font-bold text-foreground mb-1"><?php echo esc_html( $m['title'] ); ?></h3>
                        <p class="text-sm text-muted-foreground mb-3"><?php echo esc_html( $m['secondary'] ); ?></p>
                        <a href="<?php echo esc_url( $m['href'] ); ?>" class="text-foreground font-medium hover:text-accent transition-colors">
                            <?php echo esc_html( $m['primary'] ); ?>
                        </a>
                    </div>
                <?php endforeach; ?>
            </div>
        </div>
    </section>

    <!-- Contact Form Section -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Send Us a Message</h2>
                <p class="text-lg text-muted-foreground">Fill out the form below and our team will respond within one business day</p>
            </div>

            <div class="bg-card border border-border rounded-lg p-6">
                <div class="entry-content">
                    <?php
                    while ( have_posts() ) : the_post();
                        the_content();
                    endwhile;
                    ?>
                </div>
            </div>
        </div>
    </section>

    <!-- Trust Signals Near Form -->
    <section class="py-8 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="flex flex-col items-center gap-4 text-center">
                <div class="flex flex-wrap items-center justify-center gap-6">
                    <span class="text-sm font-medium text-muted-foreground">Your data is protected by</span>
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/popia.svg' ); ?>" alt="POPIA Compliant" class="h-8 opacity-60">
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/iso-27001.svg' ); ?>" alt="ISO 27001 Certified" class="h-8 opacity-60">
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/soc2.svg' ); ?>" alt="SOC 2 Type II" class="h-8 opacity-60">
                    <img src="<?php echo esc_url( VERIGATE_URI . '/assets/img/logos/certifications/fica.svg' ); ?>" alt="FICA Compliant" class="h-8 opacity-60">
                </div>
                <p class="text-xs text-muted-foreground">POPIA compliant &bull; ISO 27001 certified &bull; SOC 2 Type II</p>
            </div>
        </div>
    </section>

    <!-- Office Location with Map -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl">
            <div class="mb-12 animate-on-scroll fade-up">
                <h2 class="text-3xl md:text-4xl font-bold text-foreground mb-4">Our Office</h2>
            </div>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <!-- Office Address Card -->
                <div class="bg-card border border-border rounded-lg p-6">
                    <div class="flex items-start gap-4">
                        <div class="p-2 rounded-lg bg-accent/10">
                            <?php verigate_icon( 'map-pin', 'w-5 h-5 text-accent', 20 ); ?>
                        </div>
                        <div class="flex-1">
                            <h3 class="text-xl font-bold text-foreground mb-2">Cape Town</h3>
                            <div class="space-y-1 text-sm text-muted-foreground">
                                <p>4th Floor, The Terraces</p>
                                <p>34 Bree Street</p>
                                <p>Cape Town, 8001</p>
                                <p>South Africa</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Google Maps Embed -->
                <div class="rounded-lg border border-border overflow-hidden">
                    <iframe
                        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3310.8!2d18.42!3d-33.92!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMzPCsDU1JzEyLjAiUyAxOMKwMjUnMTIuMCJF!5e0!3m2!1sen!2sza!4v1"
                        class="h-[300px] w-full"
                        style="border:0;"
                        allowfullscreen=""
                        loading="lazy"
                        referrerpolicy="no-referrer-when-downgrade"
                        title="VeriGate Cape Town Office Location"
                    ></iframe>
                </div>
            </div>
        </div>
    </section>

    <!-- Business Hours & Enquiry Types -->
    <section class="py-20 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8 stagger-list">
                <!-- Business Hours -->
                <div class="bg-card border border-border rounded-lg p-6">
                    <div class="flex items-center gap-3 mb-4">
                        <?php verigate_icon( 'clock', 'w-6 h-6 text-accent', 24 ); ?>
                        <h3 class="text-lg font-bold text-foreground">Business Hours</h3>
                    </div>
                    <div class="space-y-2 text-muted-foreground">
                        <div class="flex justify-between">
                            <span>Monday - Friday:</span>
                            <span class="font-medium text-foreground">08:00 - 17:00 SAST</span>
                        </div>
                        <div class="flex justify-between">
                            <span>Saturday:</span>
                            <span class="font-medium text-foreground">Closed</span>
                        </div>
                        <div class="flex justify-between">
                            <span>Sunday:</span>
                            <span class="font-medium text-foreground">Closed</span>
                        </div>
                        <p class="text-sm pt-4 border-t mt-4">* Enterprise clients have access to 24/7 support via their dedicated account manager</p>
                    </div>
                </div>

                <!-- Enquiry Types -->
                <div class="bg-card border border-border rounded-lg p-6">
                    <div class="flex items-center gap-3 mb-4">
                        <?php verigate_icon( 'globe', 'w-6 h-6 text-accent', 24 ); ?>
                        <h3 class="text-lg font-bold text-foreground">Enquiry Types</h3>
                    </div>
                    <div class="space-y-4 text-muted-foreground">
                        <p>We handle a range of enquiries:</p>
                        <div class="space-y-2">
                            <?php
                            $enquiry_types = [ 'General Enquiry', 'Request a Demo', 'Pricing Information', 'Technical Support', 'Partnership Enquiry' ];
                            foreach ( $enquiry_types as $type ) :
                            ?>
                                <div class="flex items-center gap-2">
                                    <div class="w-2 h-2 rounded-full bg-accent"></div>
                                    <span><?php echo esc_html( $type ); ?></span>
                                </div>
                            <?php endforeach; ?>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Customer Logos -->
    <?php get_template_part( 'template-parts/sections/customer-logos-compact' ); ?>

    <?php get_template_part( 'template-parts/sections/testimonial-single', null, array(
        'quote'   => 'VeriGate\'s support team is incredibly responsive. Any time we\'ve had a question about our verification results, they\'ve come back within hours with clear, detailed answers.',
        'name'    => 'Kabelo Mabena',
        'role'    => 'People Operations Manager',
        'company' => 'Capitec',
        'photo'   => 'https://images.unsplash.com/photo-1617244147299-5ef406921c35?w=80&h=80&fit=crop&crop=face',
        'logo'    => 'capitec.svg',
    ) ); ?>

    <!-- FAQ Quick Links -->
    <section class="py-20 px-4 bg-secondary/30">
        <div class="container mx-auto max-w-4xl text-center animate-on-scroll fade-up">
            <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-4">Looking for Quick Answers?</h2>
            <p class="text-lg text-muted-foreground mb-8">Check out our frequently asked questions or browse our support resources</p>
            <div class="flex flex-col sm:flex-row gap-4 justify-center">
                <a href="<?php echo esc_url( home_url( '/faqs/' ) ); ?>" class="bg-card border border-border rounded-lg p-6 hover:shadow-lg transition-shadow text-left">
                    <h3 class="font-semibold text-foreground mb-2">FAQs</h3>
                    <p class="text-sm text-muted-foreground">Browse common questions and answers</p>
                </a>
                <a href="<?php echo esc_url( home_url( '/technical-support/' ) ); ?>" class="bg-card border border-border rounded-lg p-6 hover:shadow-lg transition-shadow text-left">
                    <h3 class="font-semibold text-foreground mb-2">Technical Support</h3>
                    <p class="text-sm text-muted-foreground">Access guides and documentation</p>
                </a>
            </div>
        </div>
    </section>

    <!-- CTA -->
    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'company' ) ); ?>

</main>

<?php get_footer();
