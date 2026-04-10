<?php
/**
 * VeriGate WordPress Configuration
 *
 * IMPORTANT: Fill in the database credentials below after creating the
 * database via cPanel on the shared hosting server.
 */

// ** Database settings ** //
define('WP_CACHE', true);
define( 'WPCACHEHOME', '/usr/www/users/veriggsrth/wp-content/plugins/wp-super-cache/' );
define( 'DB_NAME', 'veriggsrth_wp' );
define( 'DB_USER', 'veriggsrth_admin' );
define( 'DB_PASSWORD', '16b79t1RkO2293' );
define( 'DB_HOST', 'sql47.jnb1.host-h.net' );

/** Database charset */
define( 'DB_CHARSET', 'utf8mb4' );

/** Database collate type */
define( 'DB_COLLATE', '' );

/**
 * Authentication unique keys and salts.
 * Generated from https://api.wordpress.org/secret-key/1.1/salt/
 */
define('AUTH_KEY',         'S5noxBQ9U32tA}rrscg&wtG3xGbL_<<g%3w+Y,P^Vk6--faV`Qeo~<C>/li$nb!$');
define('SECURE_AUTH_KEY',  'q<HA2|??-mC]gkY6O#MWe]eF5X_XQs07HxEzC-}#9>JWX#p^g);!:!bs%)3a_Rwc');
define('LOGGED_IN_KEY',    'm}P.[69-Ypoaju&Kc/)#?d>2Ow*Dt-hW|I$UV0-J2c{{yulMC >Bm20%!^QCAIug');
define('NONCE_KEY',        'blQ/gTcmN5[Pb{+27xUA`I# %xmITEg6H]&^$ar20}:B6)/J-OFI7l;`Mt2-)!_D');
define('AUTH_SALT',        'j[[Ap@Tw@K/_[gV;-ssSqj(8+ZpVmHK#5?=MFos?xoQdDgEZo13E8NDac/=c&JnN');
define('SECURE_AUTH_SALT', 'g6_V1{==Fd6+S,N),]UjoX.Z)za!fVf}+D =/m^2h$M}(fv;K5%n5+lQ >*7FyI[');
define('LOGGED_IN_SALT',   'O8;+B|JOi_Z F]R2,PupujyeMz@a-}ZX{dP`&}_sPo9HBWAwQqqIPZ%s|6jzl<<V');
define('NONCE_SALT',       'QGil9-uC-o6^jZdz8#rR?.S.Fm:ScXu`^~6*p1OK^-q5Op0@5%gBrzOI9+kFgH-P');

/** Database table prefix */
$table_prefix = 'vg_';

/** Disable file editing from admin (security) */
define( 'DISALLOW_FILE_EDIT', true );

/** Memory limit */
define( 'WP_MEMORY_LIMIT', '256M' );

/** Disable WordPress auto-updates (manage manually) */
define( 'WP_AUTO_UPDATE_CORE', false );

/** Debugging — set to true during development, false in production */
define( 'WP_DEBUG', false );

/* That's all, stop editing! Happy publishing. */

/** Absolute path to the WordPress directory. */
if ( ! defined( 'ABSPATH' ) ) {
    define( 'ABSPATH', __DIR__ . '/' );
}

/** Sets up WordPress vars and included files. */
require_once ABSPATH . 'wp-settings.php';
