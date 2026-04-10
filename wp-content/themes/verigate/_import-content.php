<?php
/**
 * VeriGate Content Import Script
 * Imports all CPT data from JSON + assigns page templates.
 * Run via: https://verigate.co.za/_import-content.php?token=VG_IMPORT_2026_SECURE
 * DELETE THIS FILE IMMEDIATELY AFTER USE.
 */

// Security check
if ( ! isset( $_GET['token'] ) || $_GET['token'] !== 'VG_IMPORT_2026_SECURE' ) {
    http_response_code( 403 );
    die( 'Forbidden' );
}

// Load WordPress
define( 'SHORTINIT', false );
require_once dirname( __FILE__ ) . '/wp-load.php';

// Show errors for debugging
error_reporting( E_ALL );
ini_set( 'display_errors', 1 );
set_time_limit( 300 );

header( 'Content-Type: text/plain; charset=utf-8' );

$theme_dir  = get_template_directory();
$data_dir   = $theme_dir . '/_import-data';
$results    = [];

// ─────────────────────────────────────────────
// Helper: Create or update a CPT post
// ─────────────────────────────────────────────
function vg_create_post( $post_type, $title, $slug, $description = '', $status = 'publish' ) {
    // Check if post already exists
    $existing = get_page_by_path( $slug, OBJECT, $post_type );
    if ( $existing ) {
        return $existing->ID;
    }

    $post_id = wp_insert_post( [
        'post_type'    => $post_type,
        'post_title'   => $title,
        'post_name'    => $slug,
        'post_content' => $description,
        'post_status'  => $status,
    ] );

    return $post_id;
}

// ─────────────────────────────────────────────
// Helper: Store ACF-style repeater field data
// ACF stores repeaters as:
//   {field_name}       = count
//   {field_name}_0_{subfield} = value
//   {field_name}_0_{subfield}_0_{subsubfield} = value (nested)
// ─────────────────────────────────────────────
function vg_set_repeater( $post_id, $field_name, $rows, $sub_fields ) {
    // Delete existing repeater data
    global $wpdb;
    $wpdb->query( $wpdb->prepare(
        "DELETE FROM {$wpdb->postmeta} WHERE post_id = %d AND meta_key LIKE %s",
        $post_id,
        $field_name . '%'
    ) );

    update_post_meta( $post_id, $field_name, count( $rows ) );

    foreach ( $rows as $i => $row ) {
        foreach ( $sub_fields as $sf ) {
            if ( isset( $row[ $sf ] ) ) {
                $val = $row[ $sf ];
                $meta_key = "{$field_name}_{$i}_{$sf}";

                if ( is_array( $val ) && ! empty( $val ) ) {
                    // Check if it's a nested repeater (array of assoc arrays)
                    if ( isset( $val[0] ) && is_array( $val[0] ) ) {
                        // Nested repeater
                        $nested_keys = array_keys( $val[0] );
                        update_post_meta( $post_id, $meta_key, count( $val ) );
                        foreach ( $val as $j => $nested_row ) {
                            foreach ( $nested_keys as $nk ) {
                                if ( isset( $nested_row[ $nk ] ) ) {
                                    update_post_meta( $post_id, "{$meta_key}_{$j}_{$nk}", $nested_row[ $nk ] );
                                }
                            }
                        }
                    } elseif ( isset( $val[0] ) && is_string( $val[0] ) ) {
                        // Array of strings — store as a nested repeater with single 'item' field
                        update_post_meta( $post_id, $meta_key, count( $val ) );
                        foreach ( $val as $j => $item ) {
                            update_post_meta( $post_id, "{$meta_key}_{$j}_item", $item );
                        }
                    } else {
                        update_post_meta( $post_id, $meta_key, $val );
                    }
                } else {
                    update_post_meta( $post_id, $meta_key, $val );
                }
            }
        }
    }
}

function vg_set_simple_repeater( $post_id, $field_name, $rows ) {
    if ( empty( $rows ) ) return;
    $sub_fields = array_keys( $rows[0] );
    vg_set_repeater( $post_id, $field_name, $rows, $sub_fields );
}

// ─────────────────────────────────────────────
// 1. VERIFICATION TYPES (16 entries)
// ─────────────────────────────────────────────
echo "=== VERIFICATION TYPES ===\n";
$vt_json = json_decode( file_get_contents( $data_dir . '/verificationTypes.json' ), true );
if ( ! $vt_json ) {
    echo "ERROR: Could not load verificationTypes.json\n";
} else {
    foreach ( $vt_json as $vt ) {
        $post_id = vg_create_post( 'verification', $vt['title'], $vt['slug'], $vt['description'] );
        if ( is_wp_error( $post_id ) ) {
            echo "  ERROR creating {$vt['slug']}: {$post_id->get_error_message()}\n";
            continue;
        }

        // Simple fields
        update_post_meta( $post_id, 'subtitle', $vt['subtitle'] ?? '' );
        update_post_meta( $post_id, 'badge_text', $vt['badgeText'] ?? $vt['title'] );
        update_post_meta( $post_id, 'icon', strtolower( $vt['icon'] ?? 'shield' ) );
        update_post_meta( $post_id, 'turnaround_time', $vt['turnaround'] ?? '' );
        update_post_meta( $post_id, 'description', $vt['description'] ?? '' );

        // Repeater: how_it_works
        if ( ! empty( $vt['howItWorks'] ) ) {
            vg_set_simple_repeater( $post_id, 'how_it_works', $vt['howItWorks'] );
        }

        // Repeater: features (nested — each feature has title, description, items[])
        if ( ! empty( $vt['features'] ) ) {
            vg_set_repeater( $post_id, 'features', $vt['features'], ['title', 'description', 'items'] );
        }

        // Repeater: use_cases
        if ( ! empty( $vt['useCases'] ) ) {
            vg_set_simple_repeater( $post_id, 'use_cases', $vt['useCases'] );
        }

        // Repeater: related_types (store as simple repeater with slug/title/description)
        if ( ! empty( $vt['relatedTypes'] ) ) {
            vg_set_simple_repeater( $post_id, 'related_types', $vt['relatedTypes'] );
        }

        echo "  ✓ {$vt['slug']} (ID: {$post_id})\n";
    }
    echo "  Total: " . count( $vt_json ) . " verification types\n\n";
}

// ─────────────────────────────────────────────
// 2. COMPLIANCE TYPES (9 entries)
// ─────────────────────────────────────────────
echo "=== COMPLIANCE TYPES ===\n";
$ct_json = json_decode( file_get_contents( $data_dir . '/compliance.json' ), true );
if ( ! $ct_json ) {
    echo "ERROR: Could not load compliance.json\n";
} else {
    foreach ( $ct_json as $ct ) {
        $post_id = vg_create_post( 'compliance', $ct['title'], $ct['slug'], $ct['description'] );
        if ( is_wp_error( $post_id ) ) {
            echo "  ERROR creating {$ct['slug']}: {$post_id->get_error_message()}\n";
            continue;
        }

        update_post_meta( $post_id, 'subtitle', $ct['subtitle'] ?? '' );
        update_post_meta( $post_id, 'badge_text', $ct['badgeText'] ?? $ct['title'] );
        update_post_meta( $post_id, 'icon', strtolower( $ct['icon'] ?? 'shield-check' ) );
        update_post_meta( $post_id, 'description', $ct['description'] ?? '' );

        // Repeater: requirements
        if ( ! empty( $ct['requirements'] ) ) {
            vg_set_simple_repeater( $post_id, 'requirements', $ct['requirements'] );
        }

        // Repeater: benefits
        if ( ! empty( $ct['benefits'] ) ) {
            vg_set_simple_repeater( $post_id, 'benefits', $ct['benefits'] );
        }

        // Repeater: process_steps
        if ( ! empty( $ct['process'] ) ) {
            vg_set_simple_repeater( $post_id, 'process_steps', $ct['process'] );
        }

        // Repeater: related_compliance
        if ( ! empty( $ct['relatedCompliance'] ) ) {
            vg_set_simple_repeater( $post_id, 'related_compliance', $ct['relatedCompliance'] );
        }

        echo "  ✓ {$ct['slug']} (ID: {$post_id})\n";
    }
    echo "  Total: " . count( $ct_json ) . " compliance types\n\n";
}

// ─────────────────────────────────────────────
// 3. FRAUD PREVENTION (5 entries)
// ─────────────────────────────────────────────
echo "=== FRAUD PREVENTION ===\n";
$fp_json = json_decode( file_get_contents( $data_dir . '/fraudPrevention.json' ), true );
if ( ! $fp_json ) {
    echo "ERROR: Could not load fraudPrevention.json\n";
} else {
    foreach ( $fp_json as $fp ) {
        $post_id = vg_create_post( 'fraud', $fp['title'], $fp['slug'], $fp['description'] );
        if ( is_wp_error( $post_id ) ) {
            echo "  ERROR creating {$fp['slug']}: {$post_id->get_error_message()}\n";
            continue;
        }

        update_post_meta( $post_id, 'subtitle', $fp['subtitle'] ?? '' );
        update_post_meta( $post_id, 'badge_text', $fp['badgeText'] ?? $fp['title'] );
        update_post_meta( $post_id, 'icon', strtolower( $fp['icon'] ?? 'shield-alert' ) );
        update_post_meta( $post_id, 'description', $fp['description'] ?? '' );

        // Repeater: threats (title, description, severity)
        if ( ! empty( $fp['threats'] ) ) {
            vg_set_simple_repeater( $post_id, 'threat_indicators', $fp['threats'] );
        }

        // Repeater: detection_methods (title, description, items[])
        if ( ! empty( $fp['detectionMethods'] ) ) {
            vg_set_repeater( $post_id, 'detection_methods', $fp['detectionMethods'], ['title', 'description', 'items'] );
        }

        // Solutions as WYSIWYG/repeater
        if ( ! empty( $fp['solutions'] ) ) {
            // Store as repeater
            vg_set_simple_repeater( $post_id, 'solutions', $fp['solutions'] );
        }

        // Repeater: related_prevention
        if ( ! empty( $fp['relatedPrevention'] ) ) {
            vg_set_simple_repeater( $post_id, 'related_prevention', $fp['relatedPrevention'] );
        }

        echo "  ✓ {$fp['slug']} (ID: {$post_id})\n";
    }
    echo "  Total: " . count( $fp_json ) . " fraud prevention types\n\n";
}

// ─────────────────────────────────────────────
// 4. INDUSTRY SOLUTIONS (12 entries)
// ─────────────────────────────────────────────
echo "=== INDUSTRY SOLUTIONS ===\n";
$ind_json = json_decode( file_get_contents( $data_dir . '/industries.json' ), true );
if ( ! $ind_json ) {
    echo "ERROR: Could not load industries.json\n";
} else {
    foreach ( $ind_json as $ind ) {
        $post_id = vg_create_post( 'industry', $ind['title'], $ind['slug'], $ind['description'] );
        if ( is_wp_error( $post_id ) ) {
            echo "  ERROR creating {$ind['slug']}: {$post_id->get_error_message()}\n";
            continue;
        }

        update_post_meta( $post_id, 'subtitle', $ind['subtitle'] ?? '' );
        update_post_meta( $post_id, 'badge_text', $ind['badgeText'] ?? $ind['title'] );
        update_post_meta( $post_id, 'icon', strtolower( $ind['icon'] ?? 'landmark' ) );
        update_post_meta( $post_id, 'description', $ind['description'] ?? '' );

        // Repeater: metrics
        if ( ! empty( $ind['metrics'] ) ) {
            vg_set_simple_repeater( $post_id, 'metrics', $ind['metrics'] );
        }

        // Repeater: challenges (title, description, impact)
        if ( ! empty( $ind['challenges'] ) ) {
            vg_set_simple_repeater( $post_id, 'challenges', $ind['challenges'] );
        }

        // Repeater: solutions (title, description, features[], result)
        if ( ! empty( $ind['solutions'] ) ) {
            vg_set_repeater( $post_id, 'solutions', $ind['solutions'], ['title', 'description', 'features', 'result'] );
        }

        // Repeater: regulations
        if ( ! empty( $ind['regulations'] ) ) {
            vg_set_simple_repeater( $post_id, 'regulations', $ind['regulations'] );
        }

        // Repeater: use_cases (title, description, benefits[])
        if ( ! empty( $ind['useCases'] ) ) {
            vg_set_repeater( $post_id, 'use_cases', $ind['useCases'], ['title', 'description', 'benefits'] );
        }

        // ROI data as serialized array
        if ( ! empty( $ind['roi'] ) ) {
            update_post_meta( $post_id, 'roi_title', $ind['roi']['title'] ?? '' );
            if ( ! empty( $ind['roi']['stats'] ) ) {
                vg_set_simple_repeater( $post_id, 'roi_stats', $ind['roi']['stats'] );
            }
        }

        echo "  ✓ {$ind['slug']} (ID: {$post_id})\n";
    }
    echo "  Total: " . count( $ind_json ) . " industry solutions\n\n";
}

// ─────────────────────────────────────────────
// 5. SOCIAL PROOF (testimonials, logos, stats)
// ─────────────────────────────────────────────
echo "=== SOCIAL PROOF ===\n";
$sp_json = json_decode( file_get_contents( $data_dir . '/social-proof.json' ), true );
if ( ! $sp_json ) {
    echo "ERROR: Could not load social-proof.json\n";
} else {
    // Testimonials
    if ( ! empty( $sp_json['testimonials'] ) ) {
        foreach ( $sp_json['testimonials'] as $t ) {
            $post_id = vg_create_post( 'testimonial', $t['company'] . ' - ' . $t['author'], sanitize_title( $t['company'] . '-' . $t['author'] ) );
            if ( is_wp_error( $post_id ) ) continue;

            update_post_meta( $post_id, 'quote', $t['quote'] );
            update_post_meta( $post_id, 'author_name', $t['author'] );
            update_post_meta( $post_id, 'author_role', $t['role'] );
            update_post_meta( $post_id, 'company', $t['company'] );
            update_post_meta( $post_id, 'rating', $t['rating'] );
            update_post_meta( $post_id, 'metrics', $t['metrics'] );
            update_post_meta( $post_id, 'industry', $t['industry'] );
            update_post_meta( $post_id, 'linkedin_url', $t['linkedin'] ?? '' );
            update_post_meta( $post_id, 'verified', $t['verified'] ? 1 : 0 );
            // Note: company_logo and author_photo would need media uploads — skipped for now

            echo "  ✓ Testimonial: {$t['author']} / {$t['company']} (ID: {$post_id})\n";
        }
    }

    // Customer Logos
    if ( ! empty( $sp_json['customerLogos'] ) ) {
        foreach ( $sp_json['customerLogos'] as $logo ) {
            $post_id = vg_create_post( 'customer_logo', $logo['name'], sanitize_title( $logo['name'] ) );
            if ( is_wp_error( $post_id ) ) continue;

            update_post_meta( $post_id, 'industry', $logo['industry'] );
            update_post_meta( $post_id, 'featured', $logo['featured'] ? 1 : 0 );
            // Logo image would need media upload — store the relative path for now
            update_post_meta( $post_id, 'logo_path', $logo['logo'] );

            echo "  ✓ Logo: {$logo['name']} (ID: {$post_id})\n";
        }
    }

    // Statistics
    if ( ! empty( $sp_json['statistics'] ) ) {
        foreach ( $sp_json['statistics'] as $stat ) {
            $post_id = vg_create_post( 'stat', $stat['label'], sanitize_title( $stat['label'] ) );
            if ( is_wp_error( $post_id ) ) continue;

            update_post_meta( $post_id, 'value', $stat['value'] );
            update_post_meta( $post_id, 'label', $stat['label'] );
            update_post_meta( $post_id, 'icon', $stat['icon'] );

            echo "  ✓ Stat: {$stat['label']} (ID: {$post_id})\n";
        }
    }
    echo "\n";
}

// ─────────────────────────────────────────────
// 6. ASSIGN PAGE TEMPLATES
// ─────────────────────────────────────────────
echo "=== PAGE TEMPLATES ===\n";

// Map: page slug => template file
$page_templates = [
    'about'           => 'page-templates/template-about.php',
    'contact'         => 'page-templates/template-contact.php',
    'careers'         => 'page-templates/template-careers.php',
    'pricing'         => 'page-templates/template-pricing.php',
    'platform'        => 'page-templates/template-platform.php',
    'roi-calculator'  => 'page-templates/template-roi-calculator.php',
    'privacy'         => 'page-templates/template-legal.php',
    'terms'           => 'page-templates/template-legal.php',
    'cookie-policy'   => 'page-templates/template-legal.php',
];

// Create pages if they don't exist, assign templates
foreach ( $page_templates as $slug => $template ) {
    $page = get_page_by_path( $slug );
    if ( ! $page ) {
        // Create the page
        $title = ucwords( str_replace( '-', ' ', $slug ) );
        $page_id = wp_insert_post( [
            'post_type'   => 'page',
            'post_title'  => $title,
            'post_name'   => $slug,
            'post_status' => 'publish',
        ] );
        if ( is_wp_error( $page_id ) ) {
            echo "  ERROR creating page '{$slug}': {$page_id->get_error_message()}\n";
            continue;
        }
    } else {
        $page_id = $page->ID;
    }

    update_post_meta( $page_id, '_wp_page_template', $template );
    echo "  ✓ Page '{$slug}' (ID: {$page_id}) → {$template}\n";
}

// Also ensure the homepage has its template (from previous fix)
$front_page_id = get_option( 'page_on_front' );
if ( $front_page_id ) {
    update_post_meta( $front_page_id, '_wp_page_template', 'page-templates/template-homepage.php' );
    echo "  ✓ Homepage (ID: {$front_page_id}) → page-templates/template-homepage.php\n";
}

// ─────────────────────────────────────────────
// 7. CREATE ADDITIONAL PAGES (referenced in nav/footer)
// ─────────────────────────────────────────────
echo "\n=== ADDITIONAL PAGES ===\n";

$additional_pages = [
    'integrations'      => 'Integrations',
    'partner-program'   => 'Partner Program',
    'events'            => 'Events',
    'blog'              => 'Blog',
    'resources'         => 'Resources',
    'faqs'              => 'FAQs',
    'technical-support'  => 'Technical Support',
    'south-africa'      => 'South Africa',
    'supported-documents' => 'Supported Documents',
];

foreach ( $additional_pages as $slug => $title ) {
    $page = get_page_by_path( $slug );
    if ( ! $page ) {
        $page_id = wp_insert_post( [
            'post_type'   => 'page',
            'post_title'  => $title,
            'post_name'   => $slug,
            'post_status' => 'publish',
        ] );
        echo "  ✓ Created '{$title}' (ID: {$page_id})\n";
    } else {
        echo "  – '{$title}' already exists (ID: {$page->ID})\n";
    }
}

// ─────────────────────────────────────────────
// 8. SET READING SETTINGS
// ─────────────────────────────────────────────
echo "\n=== READING SETTINGS ===\n";

// Set blog page
$blog_page = get_page_by_path( 'blog' );
if ( $blog_page ) {
    update_option( 'page_for_posts', $blog_page->ID );
    echo "  ✓ Posts page set to 'Blog' (ID: {$blog_page->ID})\n";
}

// Flush rewrite rules
flush_rewrite_rules();
echo "  ✓ Rewrite rules flushed\n";

// ─────────────────────────────────────────────
// Summary
// ─────────────────────────────────────────────
echo "\n=== IMPORT COMPLETE ===\n";
echo "Verification Types: " . count( $vt_json ?? [] ) . "\n";
echo "Compliance Types: " . count( $ct_json ?? [] ) . "\n";
echo "Fraud Prevention: " . count( $fp_json ?? [] ) . "\n";
echo "Industry Solutions: " . count( $ind_json ?? [] ) . "\n";
echo "Testimonials: " . count( $sp_json['testimonials'] ?? [] ) . "\n";
echo "Customer Logos: " . count( $sp_json['customerLogos'] ?? [] ) . "\n";
echo "Statistics: " . count( $sp_json['statistics'] ?? [] ) . "\n";
echo "\nDELETE THIS FILE NOW!\n";
