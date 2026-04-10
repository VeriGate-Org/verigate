<?php
/**
 * Theme Setup
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

add_action( 'after_setup_theme', 'verigate_setup' );

function verigate_setup() {
    // Let WordPress manage the <title> tag.
    add_theme_support( 'title-tag' );

    // Featured images.
    add_theme_support( 'post-thumbnails' );

    // HTML5 markup.
    add_theme_support( 'html5', array(
        'search-form',
        'comment-form',
        'comment-list',
        'gallery',
        'caption',
        'style',
        'script',
    ) );

    // Custom logo.
    add_theme_support( 'custom-logo', array(
        'height'      => 40,
        'width'       => 200,
        'flex-height' => true,
        'flex-width'  => true,
    ) );

    // Register navigation menus.
    register_nav_menus( array(
        'primary'         => __( 'Primary Navigation', 'verigate' ),
        'products'        => __( 'Products Mega Menu', 'verigate' ),
        'solutions'       => __( 'Solutions Menu', 'verigate' ),
        'resources'       => __( 'Resources Menu', 'verigate' ),
        'company'         => __( 'Company Menu', 'verigate' ),
        'footer-products' => __( 'Footer — Products', 'verigate' ),
        'footer-solutions'=> __( 'Footer — Solutions', 'verigate' ),
        'footer-company'  => __( 'Footer — Company', 'verigate' ),
        'footer-resources'=> __( 'Footer — Resources', 'verigate' ),
        'footer-legal'    => __( 'Footer — Legal', 'verigate' ),
    ) );

    // Custom image sizes.
    add_image_size( 'hero-bg', 1920, 1080, true );
    add_image_size( 'card-thumb', 600, 400, true );
    add_image_size( 'author-photo', 200, 200, true );
    add_image_size( 'company-logo', 320, 120, false );
    add_image_size( 'team-photo', 400, 400, true );
}

/**
 * Set custom excerpt length.
 */
add_filter( 'excerpt_length', function () {
    return 30;
} );

/**
 * Custom excerpt "more" string.
 */
add_filter( 'excerpt_more', function () {
    return '&hellip;';
} );

/**
 * Show all entries on CPT archive pages (no pagination).
 */
add_action( 'pre_get_posts', function ( $query ) {
    if ( is_admin() || ! $query->is_main_query() ) {
        return;
    }
    if ( $query->is_post_type_archive( [ 'verification', 'compliance', 'fraud', 'industry' ] ) ) {
        $query->set( 'posts_per_page', -1 );
    }
} );
