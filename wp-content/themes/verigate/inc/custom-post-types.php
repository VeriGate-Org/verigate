<?php
/**
 * Custom Post Types
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

add_action( 'init', 'verigate_register_cpts' );

function verigate_register_cpts() {

    // 1. Verification Types.
    register_post_type( 'verification', array(
        'labels' => array(
            'name'               => 'Verification Types',
            'singular_name'      => 'Verification Type',
            'add_new_item'       => 'Add New Verification Type',
            'edit_item'          => 'Edit Verification Type',
            'all_items'          => 'All Verification Types',
            'search_items'       => 'Search Verification Types',
            'not_found'          => 'No verification types found.',
        ),
        'public'       => true,
        'has_archive'  => true,
        'rewrite'      => array( 'slug' => 'verification-types', 'with_front' => false ),
        'menu_icon'    => 'dashicons-shield',
        'supports'     => array( 'title', 'editor', 'thumbnail', 'excerpt', 'revisions' ),
        'show_in_rest' => true,
    ) );

    // 2. Compliance Types.
    register_post_type( 'compliance', array(
        'labels' => array(
            'name'               => 'Compliance Types',
            'singular_name'      => 'Compliance Type',
            'add_new_item'       => 'Add New Compliance Type',
            'edit_item'          => 'Edit Compliance Type',
            'all_items'          => 'All Compliance Types',
            'search_items'       => 'Search Compliance Types',
            'not_found'          => 'No compliance types found.',
        ),
        'public'       => true,
        'has_archive'  => true,
        'rewrite'      => array( 'slug' => 'compliance', 'with_front' => false ),
        'menu_icon'    => 'dashicons-yes-alt',
        'supports'     => array( 'title', 'editor', 'thumbnail', 'excerpt', 'revisions' ),
        'show_in_rest' => true,
    ) );

    // 3. Fraud Prevention.
    register_post_type( 'fraud', array(
        'labels' => array(
            'name'               => 'Fraud Prevention',
            'singular_name'      => 'Fraud Prevention Type',
            'add_new_item'       => 'Add New Fraud Prevention Type',
            'edit_item'          => 'Edit Fraud Prevention Type',
            'all_items'          => 'All Fraud Prevention Types',
            'search_items'       => 'Search Fraud Prevention',
            'not_found'          => 'No fraud prevention types found.',
        ),
        'public'       => true,
        'has_archive'  => true,
        'rewrite'      => array( 'slug' => 'fraud-prevention', 'with_front' => false ),
        'menu_icon'    => 'dashicons-lock',
        'supports'     => array( 'title', 'editor', 'thumbnail', 'excerpt', 'revisions' ),
        'show_in_rest' => true,
    ) );

    // 4. Industry Solutions.
    register_post_type( 'industry', array(
        'labels' => array(
            'name'               => 'Industry Solutions',
            'singular_name'      => 'Industry Solution',
            'add_new_item'       => 'Add New Industry Solution',
            'edit_item'          => 'Edit Industry Solution',
            'all_items'          => 'All Industry Solutions',
            'search_items'       => 'Search Industry Solutions',
            'not_found'          => 'No industry solutions found.',
        ),
        'public'       => true,
        'has_archive'  => true,
        'rewrite'      => array( 'slug' => 'solutions', 'with_front' => false ),
        'menu_icon'    => 'dashicons-building',
        'supports'     => array( 'title', 'editor', 'thumbnail', 'excerpt', 'revisions' ),
        'show_in_rest' => true,
    ) );

    // 5. Testimonials (private).
    register_post_type( 'testimonial', array(
        'labels' => array(
            'name'               => 'Testimonials',
            'singular_name'      => 'Testimonial',
            'add_new_item'       => 'Add New Testimonial',
            'edit_item'          => 'Edit Testimonial',
            'all_items'          => 'All Testimonials',
            'not_found'          => 'No testimonials found.',
        ),
        'public'             => false,
        'show_ui'            => true,
        'show_in_menu'       => true,
        'has_archive'        => false,
        'publicly_queryable' => false,
        'rewrite'            => false,
        'menu_icon'          => 'dashicons-format-quote',
        'supports'           => array( 'title', 'revisions' ),
        'show_in_rest'       => true,
    ) );

    // 6. Team Members (private).
    register_post_type( 'team', array(
        'labels' => array(
            'name'               => 'Team Members',
            'singular_name'      => 'Team Member',
            'add_new_item'       => 'Add New Team Member',
            'edit_item'          => 'Edit Team Member',
            'all_items'          => 'All Team Members',
            'not_found'          => 'No team members found.',
        ),
        'public'             => false,
        'show_ui'            => true,
        'show_in_menu'       => true,
        'has_archive'        => false,
        'publicly_queryable' => false,
        'rewrite'            => false,
        'menu_icon'          => 'dashicons-groups',
        'supports'           => array( 'title', 'revisions' ),
        'show_in_rest'       => true,
    ) );

    // 7. Customer Logos (private).
    register_post_type( 'customer_logo', array(
        'labels' => array(
            'name'               => 'Customer Logos',
            'singular_name'      => 'Customer Logo',
            'add_new_item'       => 'Add New Customer Logo',
            'edit_item'          => 'Edit Customer Logo',
            'all_items'          => 'All Customer Logos',
            'not_found'          => 'No customer logos found.',
        ),
        'public'             => false,
        'show_ui'            => true,
        'show_in_menu'       => true,
        'has_archive'        => false,
        'publicly_queryable' => false,
        'rewrite'            => false,
        'menu_icon'          => 'dashicons-format-image',
        'supports'           => array( 'title', 'revisions' ),
        'show_in_rest'       => true,
    ) );

    // 8. Statistics (private).
    register_post_type( 'stat', array(
        'labels' => array(
            'name'               => 'Statistics',
            'singular_name'      => 'Statistic',
            'add_new_item'       => 'Add New Statistic',
            'edit_item'          => 'Edit Statistic',
            'all_items'          => 'All Statistics',
            'not_found'          => 'No statistics found.',
        ),
        'public'             => false,
        'show_ui'            => true,
        'show_in_menu'       => true,
        'has_archive'        => false,
        'publicly_queryable' => false,
        'rewrite'            => false,
        'menu_icon'          => 'dashicons-chart-bar',
        'supports'           => array( 'title', 'revisions' ),
        'show_in_rest'       => true,
    ) );
}

/**
 * Flush rewrite rules on theme activation.
 */
add_action( 'after_switch_theme', function () {
    verigate_register_cpts();
    flush_rewrite_rules();
} );
