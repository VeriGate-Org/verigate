<?php
/**
 * ACF Configuration
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

/**
 * ACF Fallback — provide get_field() / have_rows() / the_row() / get_sub_field()
 * using plain post_meta when ACF Pro is not installed.
 * Data is stored the same way ACF stores repeaters:
 *   field_name          = row count
 *   field_name_0_sub    = value
 *   field_name_0_sub_0_nested = value
 */
if ( ! function_exists( 'get_field' ) ) {

    /**
     * Reconstruct a repeater array from flat post_meta.
     * ACF stores: field = count, field_0_sub = val, field_0_sub_0_nested = val
     * We rebuild: [ [sub => val, sub => [nested => val, ...]], ... ]
     */
    function _vg_rebuild_repeater( $post_id, $field_name, $count ) {
        global $wpdb;

        // Get ALL meta keys starting with this field prefix
        $prefix = $field_name . '_';
        $all_meta = $wpdb->get_results( $wpdb->prepare(
            "SELECT meta_key, meta_value FROM {$wpdb->postmeta} WHERE post_id = %d AND meta_key LIKE %s",
            $post_id,
            $wpdb->esc_like( $prefix ) . '%'
        ), OBJECT );

        // Build a key=>value lookup
        $meta_map = [];
        foreach ( $all_meta as $row ) {
            $meta_map[ $row->meta_key ] = $row->meta_value;
        }

        $rows = [];
        for ( $i = 0; $i < $count; $i++ ) {
            $row = [];
            $row_prefix = $prefix . $i . '_';

            // Find all sub-fields for this row
            foreach ( $meta_map as $key => $val ) {
                if ( strpos( $key, $row_prefix ) !== 0 ) continue;

                $sub_key = substr( $key, strlen( $row_prefix ) );

                // Skip if this is a nested repeater row (contains _N_ pattern after the sub-field name)
                // e.g., "items_0_item" means "items" is a nested repeater
                if ( preg_match( '/^\d+_/', $sub_key ) ) continue;

                // Check if this sub-field value is a number (potential nested repeater)
                if ( is_numeric( $val ) && intval( $val ) > 0 ) {
                    $nested_count = intval( $val );
                    $nested_prefix = $row_prefix . $sub_key . '_';

                    // Check if nested sub-fields exist
                    $has_nested = false;
                    foreach ( $meta_map as $nk => $nv ) {
                        if ( strpos( $nk, $nested_prefix . '0_' ) === 0 ) {
                            $has_nested = true;
                            break;
                        }
                    }

                    if ( $has_nested ) {
                        // Rebuild nested repeater
                        $nested_rows = [];
                        for ( $j = 0; $j < $nested_count; $j++ ) {
                            $nested_row_prefix = $nested_prefix . $j . '_';
                            $nested_row = [];
                            foreach ( $meta_map as $nk => $nv ) {
                                if ( strpos( $nk, $nested_row_prefix ) !== 0 ) continue;
                                $nested_sub = substr( $nk, strlen( $nested_row_prefix ) );
                                if ( strpos( $nested_sub, '_' ) === false ) {
                                    $nested_row[ $nested_sub ] = $nv;
                                }
                            }
                            if ( ! empty( $nested_row ) ) {
                                // If nested row has only one field called "item", flatten it
                                if ( count( $nested_row ) === 1 && isset( $nested_row['item'] ) ) {
                                    $nested_rows[] = $nested_row['item'];
                                } elseif ( count( $nested_row ) === 1 && isset( $nested_row['feature'] ) ) {
                                    $nested_rows[] = $nested_row['feature'];
                                } else {
                                    $nested_rows[] = $nested_row;
                                }
                            }
                        }
                        $row[ $sub_key ] = $nested_rows;
                        continue;
                    }
                }

                // Only include direct sub-fields (no further underscores indicating deeper nesting)
                if ( strpos( $sub_key, '_' ) === false ) {
                    $row[ $sub_key ] = $val;
                }
            }

            if ( ! empty( $row ) ) {
                $rows[] = $row;
            }
        }

        return $rows;
    }

    function get_field( $field_name, $post_id = null ) {
        if ( ! $post_id ) {
            $post_id = get_the_ID();
        }
        $val = get_post_meta( $post_id, $field_name, true );

        // If it's a number, check if it's a repeater count
        if ( is_numeric( $val ) && intval( $val ) > 0 && intval( $val ) == $val ) {
            global $wpdb;
            $test_key = $field_name . '_0_';
            $has_sub = $wpdb->get_var( $wpdb->prepare(
                "SELECT COUNT(*) FROM {$wpdb->postmeta} WHERE post_id = %d AND meta_key LIKE %s LIMIT 1",
                $post_id,
                $wpdb->esc_like( $test_key ) . '%'
            ) );

            if ( $has_sub ) {
                return _vg_rebuild_repeater( $post_id, $field_name, intval( $val ) );
            }
        }

        return $val;
    }

    // Repeater state for have_rows/the_row/get_sub_field pattern
    global $_vg_repeater_stack;
    $_vg_repeater_stack = [];

    function have_rows( $field_name, $post_id = null ) {
        global $_vg_repeater_stack;
        if ( ! $post_id ) {
            $post_id = get_the_ID();
        }

        $prefix = '';
        foreach ( $_vg_repeater_stack as $ctx ) {
            $prefix .= $ctx['field'] . '_' . $ctx['index'] . '_';
        }
        $full_key = $prefix . $field_name;

        $count = get_post_meta( $post_id, $full_key, true );
        if ( ! is_numeric( $count ) || intval( $count ) < 1 ) {
            return false;
        }

        $top = ! empty( $_vg_repeater_stack ) ? end( $_vg_repeater_stack ) : null;
        if ( $top && $top['field'] === $full_key ) {
            return $_vg_repeater_stack[ count( $_vg_repeater_stack ) - 1 ]['index'] < intval( $count );
        }

        $_vg_repeater_stack[] = [
            'field'   => $full_key,
            'count'   => intval( $count ),
            'index'   => -1,
            'post_id' => $post_id,
        ];

        return true;
    }

    function the_row() {
        global $_vg_repeater_stack;
        if ( empty( $_vg_repeater_stack ) ) return false;

        $idx = count( $_vg_repeater_stack ) - 1;
        $_vg_repeater_stack[ $idx ]['index']++;

        if ( $_vg_repeater_stack[ $idx ]['index'] >= $_vg_repeater_stack[ $idx ]['count'] ) {
            array_pop( $_vg_repeater_stack );
            return false;
        }
        return true;
    }

    function get_sub_field( $sub_field_name ) {
        global $_vg_repeater_stack;
        if ( empty( $_vg_repeater_stack ) ) return '';

        $top     = end( $_vg_repeater_stack );
        $meta_key = $top['field'] . '_' . $top['index'] . '_' . $sub_field_name;
        $post_id  = $top['post_id'];

        return get_post_meta( $post_id, $meta_key, true );
    }

    function the_sub_field( $sub_field_name ) {
        echo esc_html( get_sub_field( $sub_field_name ) );
    }

    function get_row_index() {
        global $_vg_repeater_stack;
        if ( empty( $_vg_repeater_stack ) ) return 0;
        $top = end( $_vg_repeater_stack );
        return $top['index'] + 1;
    }
}

/**
 * ACF-specific configuration — only runs when ACF Pro is active.
 */
if ( class_exists( 'ACF' ) ) {

    add_filter( 'acf/settings/save_json', function () {
        return VERIGATE_DIR . '/acf-json';
    } );

    add_filter( 'acf/settings/load_json', function ( $paths ) {
        $paths[] = VERIGATE_DIR . '/acf-json';
        return $paths;
    } );

    add_action( 'acf/init', function () {
        if ( ! function_exists( 'acf_add_options_page' ) ) {
            return;
        }

        acf_add_options_page( array(
            'page_title' => 'VeriGate Settings',
            'menu_title' => 'VeriGate Settings',
            'menu_slug'  => 'verigate-settings',
            'capability' => 'manage_options',
            'redirect'   => false,
            'icon_url'   => 'dashicons-admin-generic',
            'position'   => 2,
        ) );
    } );

}
