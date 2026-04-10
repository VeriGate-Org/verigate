<?php
/**
 * Customizer Settings
 *
 * Social links, analytics IDs, and other global options.
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

add_action( 'customize_register', 'verigate_customizer' );

function verigate_customizer( WP_Customize_Manager $wp_customize ) {

    // --- Section: Social Links ---
    $wp_customize->add_section( 'verigate_social', array(
        'title'    => __( 'Social Links', 'verigate' ),
        'priority' => 160,
    ) );

    $socials = array(
        'linkedin'  => 'LinkedIn URL',
        'twitter'   => 'Twitter / X URL',
        'facebook'  => 'Facebook URL',
        'instagram' => 'Instagram URL',
    );

    foreach ( $socials as $key => $label ) {
        $wp_customize->add_setting( "verigate_social_{$key}", array(
            'default'           => '',
            'sanitize_callback' => 'esc_url_raw',
        ) );
        $wp_customize->add_control( "verigate_social_{$key}", array(
            'label'   => $label,
            'section' => 'verigate_social',
            'type'    => 'url',
        ) );
    }

    // --- Section: Analytics ---
    $wp_customize->add_section( 'verigate_analytics', array(
        'title'    => __( 'Analytics', 'verigate' ),
        'priority' => 170,
    ) );

    $wp_customize->add_setting( 'verigate_ga_id', array(
        'default'           => '',
        'sanitize_callback' => 'sanitize_text_field',
    ) );
    $wp_customize->add_control( 'verigate_ga_id', array(
        'label'       => 'Google Analytics Measurement ID',
        'description' => 'e.g. G-XXXXXXXXXX',
        'section'     => 'verigate_analytics',
        'type'        => 'text',
    ) );

    $wp_customize->add_setting( 'verigate_gtm_id', array(
        'default'           => '',
        'sanitize_callback' => 'sanitize_text_field',
    ) );
    $wp_customize->add_control( 'verigate_gtm_id', array(
        'label'       => 'Google Tag Manager Container ID',
        'description' => 'e.g. GTM-XXXXXXX',
        'section'     => 'verigate_analytics',
        'type'        => 'text',
    ) );
}

/**
 * Output analytics scripts in <head>.
 */
add_action( 'wp_head', function () {
    $ga_id  = get_theme_mod( 'verigate_ga_id', '' );
    $gtm_id = get_theme_mod( 'verigate_gtm_id', '' );

    if ( $gtm_id ) {
        printf(
            "<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src='https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','%s');</script>\n",
            esc_js( $gtm_id )
        );
    } elseif ( $ga_id ) {
        printf(
            "<script async src=\"https://www.googletagmanager.com/gtag/js?id=%s\"></script>\n<script>window.dataLayer=window.dataLayer||[];function gtag(){dataLayer.push(arguments);}gtag('js',new Date());gtag('config','%s');</script>\n",
            esc_attr( $ga_id ),
            esc_js( $ga_id )
        );
    }
}, 1 );
