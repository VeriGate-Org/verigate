<?php
if ( ($_GET['token'] ?? '') !== 'vg2026bust' ) { http_response_code(403); exit('denied'); }
require_once dirname(__DIR__, 3) . '/wp-load.php';

$page = get_page_by_path('integrations');
if ( ! $page ) {
    echo 'Page not found: /integrations/';
    exit;
}

$current = get_post_meta( $page->ID, '_wp_page_template', true );
echo "Page ID: {$page->ID}, Current template: '{$current}'\n";

update_post_meta( $page->ID, '_wp_page_template', 'page-templates/template-integrations.php' );

$updated = get_post_meta( $page->ID, '_wp_page_template', true );
echo "Updated template: '{$updated}'\n";
echo "Done!";
