<?php
/**
 * Enqueue Styles & Scripts
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

add_action( 'wp_enqueue_scripts', 'verigate_enqueue' );

function verigate_enqueue() {
    // Google Fonts: Inter + Manrope.
    wp_enqueue_style(
        'verigate-fonts',
        'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Manrope:wght@600;700;800&display=swap',
        array(),
        null
    );

    // Compiled Tailwind CSS.
    wp_enqueue_style(
        'verigate-tailwind',
        VERIGATE_URI . '/assets/css/tailwind.css',
        array( 'verigate-fonts' ),
        VERIGATE_VERSION
    );

    // Scroll animations CSS.
    wp_enqueue_style(
        'verigate-animations',
        VERIGATE_URI . '/assets/css/animations.css',
        array( 'verigate-tailwind' ),
        VERIGATE_VERSION
    );

    // Contact Form 7 overrides.
    if ( class_exists( 'WPCF7' ) ) {
        wp_enqueue_style(
            'verigate-cf7',
            VERIGATE_URI . '/assets/css/cf7.css',
            array( 'verigate-tailwind' ),
            VERIGATE_VERSION
        );
    }

    // Navigation (mega menu + mobile).
    wp_enqueue_script(
        'verigate-navigation',
        VERIGATE_URI . '/assets/js/navigation.js',
        array(),
        VERIGATE_VERSION,
        true
    );

    // Scroll animations (IntersectionObserver).
    wp_enqueue_script(
        'verigate-animations',
        VERIGATE_URI . '/assets/js/animations.js',
        array(),
        VERIGATE_VERSION,
        true
    );

    // Testimonial / logo carousel.
    wp_enqueue_script(
        'verigate-carousel',
        VERIGATE_URI . '/assets/js/carousel.js',
        array(),
        VERIGATE_VERSION,
        true
    );

    // Stats counter animation.
    wp_enqueue_script(
        'verigate-stats-counter',
        VERIGATE_URI . '/assets/js/stats-counter.js',
        array(),
        VERIGATE_VERSION,
        true
    );
}

/**
 * Dequeue default WP block styles if not using Gutenberg.
 */
add_action( 'wp_enqueue_scripts', function () {
    wp_dequeue_style( 'wp-block-library' );
    wp_dequeue_style( 'wp-block-library-theme' );
    wp_dequeue_style( 'global-styles' );
}, 100 );
