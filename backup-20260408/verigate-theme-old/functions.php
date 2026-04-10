<?php
/**
 * VeriGate Theme Functions
 */

// Theme setup
function verigate_setup() {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('html5', array('search-form', 'comment-form', 'comment-list', 'gallery', 'caption'));

    // Register navigation menus
    register_nav_menus(array(
        'footer-products'  => __('Footer Products', 'verigate'),
        'footer-solutions' => __('Footer Solutions', 'verigate'),
        'footer-company'   => __('Footer Company', 'verigate'),
        'footer-resources' => __('Footer Resources', 'verigate'),
        'footer-legal'     => __('Footer Legal', 'verigate'),
    ));
}
add_action('after_setup_theme', 'verigate_setup');

// Enqueue styles and scripts
function verigate_enqueue() {
    // Google Fonts
    wp_enqueue_style(
        'verigate-google-fonts',
        'https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=Manrope:wght@300;400;500;600;700;800&display=swap',
        array(),
        null
    );

    // Main stylesheet
    wp_enqueue_style('verigate-style', get_stylesheet_uri(), array('verigate-google-fonts'), '1.0.0');

    // Main JS
    wp_enqueue_script('verigate-main', get_template_directory_uri() . '/js/main.js', array(), '1.0.1', true);

    // Pass theme URL to JS for document library paths
    wp_localize_script('verigate-main', 'verigateConfig', array(
        'themeUrl' => get_template_directory_uri(),
    ));
}
add_action('wp_enqueue_scripts', 'verigate_enqueue');

// Disable comments site-wide
function verigate_disable_comments() {
    remove_post_type_support('post', 'comments');
    remove_post_type_support('page', 'comments');
}
add_action('init', 'verigate_disable_comments');

// Close comments on frontend
add_filter('comments_open', '__return_false', 20, 2);
add_filter('pings_open', '__return_false', 20, 2);

// Hide comments from admin menu
function verigate_remove_comments_menu() {
    remove_menu_page('edit-comments.php');
}
add_action('admin_menu', 'verigate_remove_comments_menu');

// Remove comments from admin bar
function verigate_remove_comments_admin_bar() {
    global $wp_admin_bar;
    $wp_admin_bar->remove_menu('comments');
}
add_action('wp_before_admin_bar_render', 'verigate_remove_comments_admin_bar');

// Allow unfiltered HTML in page content (needed for SVG icons etc.)
function verigate_allow_unfiltered_html($caps, $cap, $user_id) {
    if ($cap === 'unfiltered_html') {
        $caps = array('unfiltered_html' => true);
    }
    return $caps;
}
add_filter('map_meta_cap', 'verigate_allow_unfiltered_html', 1, 3);

// Remove WordPress emoji scripts (performance)
remove_action('wp_head', 'print_emoji_detection_script', 7);
remove_action('wp_print_styles', 'print_emoji_styles');

// Clean up wp_head
remove_action('wp_head', 'wp_generator');
remove_action('wp_head', 'wlwmanifest_link');
remove_action('wp_head', 'rsd_link');
