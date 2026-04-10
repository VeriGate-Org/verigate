<?php
if ( ($_GET['token'] ?? '') !== 'vg2026bust' ) { http_response_code(403); exit('denied'); }
$dir = __DIR__;
$count = 0;
$it = new RecursiveIteratorIterator( new RecursiveDirectoryIterator( $dir ) );
foreach ( $it as $f ) {
    if ( $f->isFile() && preg_match('/\.(php|css)$/', $f->getFilename()) ) {
        touch( $f->getPathname() );
        $count++;
    }
}
echo "touched $count files";
