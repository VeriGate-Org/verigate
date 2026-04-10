<?php
/**
 * Mega Menu Walker
 *
 * Custom Walker_Nav_Menu for the VeriGate desktop mega-menu.
 * Adds data attributes so navigation.js can handle dropdowns.
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

class VeriGate_Mega_Menu_Walker extends Walker_Nav_Menu {

    /**
     * Track whether we're inside a mega-menu parent.
     */
    private $mega_parent = false;

    /**
     * Start a sub-level <ul>.
     */
    public function start_lvl( &$output, $depth = 0, $args = null ) {
        $indent = str_repeat( "\t", $depth );
        if ( 0 === $depth ) {
            $output .= "\n{$indent}<div class=\"mega-dropdown absolute top-full left-0 mt-1 bg-background border border-border rounded-lg shadow-xl p-6 hidden z-50\" data-mega-dropdown>\n";
            $output .= "{$indent}\t<ul class=\"mega-menu-grid\">\n";
        } else {
            $output .= "\n{$indent}<ul class=\"space-y-1\">\n";
        }
    }

    /**
     * End a sub-level.
     */
    public function end_lvl( &$output, $depth = 0, $args = null ) {
        $indent = str_repeat( "\t", $depth );
        if ( 0 === $depth ) {
            $output .= "{$indent}\t</ul>\n";
            $output .= "{$indent}</div>\n";
        } else {
            $output .= "{$indent}</ul>\n";
        }
    }

    /**
     * Start an <li>.
     */
    public function start_el( &$output, $item, $depth = 0, $args = null, $id = 0 ) {
        $classes = empty( $item->classes ) ? array() : (array) $item->classes;
        $has_children = in_array( 'menu-item-has-children', $classes, true );

        if ( 0 === $depth && $has_children ) {
            $output .= '<li class="relative" data-mega-trigger>';
            $output .= '<button class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-foreground hover:text-accent transition-colors rounded-md hover:bg-accent/5" aria-expanded="false">';
            $output .= esc_html( $item->title );
            $output .= ' <svg class="w-4 h-4 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>';
            $output .= '</button>';
        } elseif ( 0 === $depth ) {
            $active = verigate_is_menu_item_active( $item ) ? ' text-accent' : ' text-foreground hover:text-accent';
            $output .= '<li>';
            $output .= '<a href="' . esc_url( $item->url ) . '" class="px-4 py-2 text-sm font-medium transition-colors rounded-md hover:bg-accent/5' . $active . '">';
            $output .= esc_html( $item->title );
            $output .= '</a>';
        } else {
            // Sub-item.
            $desc = $item->description ? '<span class="block text-xs text-muted-foreground">' . esc_html( $item->description ) . '</span>' : '';
            $output .= '<li>';
            $output .= '<a href="' . esc_url( $item->url ) . '" class="block px-3 py-2 rounded-md hover:bg-accent/5 transition-colors text-sm font-medium">';
            $output .= esc_html( $item->title );
            $output .= $desc;
            $output .= '</a>';
        }
    }

    /**
     * End an <li>.
     */
    public function end_el( &$output, $item, $depth = 0, $args = null ) {
        $output .= "</li>\n";
    }
}

/**
 * Check if a menu item matches the current URL.
 */
function verigate_is_menu_item_active( $item ): bool {
    if ( ! isset( $item->url ) ) {
        return false;
    }
    $current = trailingslashit( home_url( $_SERVER['REQUEST_URI'] ?? '' ) );
    $link    = trailingslashit( $item->url );
    return $current === $link;
}
