<?php
/**
 * VeriGate Theme Functions
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

define( 'VERIGATE_VERSION', '1.0.0' );
define( 'VERIGATE_DIR', get_template_directory() );
define( 'VERIGATE_URI', get_template_directory_uri() );

// Theme setup: menus, supports, image sizes.
require_once VERIGATE_DIR . '/inc/theme-setup.php';

// Enqueue styles and scripts.
require_once VERIGATE_DIR . '/inc/enqueue-scripts.php';

// Register Custom Post Types.
require_once VERIGATE_DIR . '/inc/custom-post-types.php';

// ACF configuration.
require_once VERIGATE_DIR . '/inc/acf-config.php';

// Category theme colour helper.
require_once VERIGATE_DIR . '/inc/category-theme.php';

// Mega-menu Walker.
require_once VERIGATE_DIR . '/inc/walker-mega-menu.php';

// Customizer settings.
require_once VERIGATE_DIR . '/inc/customizer.php';

// Template tag helpers.
require_once VERIGATE_DIR . '/inc/template-tags.php';
