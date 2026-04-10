<?php
/**
 * Feature Card — Compact (List item)
 *
 * @package VeriGate
 */

$title    = $args['title'] ?? '';
$desc     = $args['description'] ?? '';
$category = $args['category'] ?? 'company';
$theme    = verigate_get_category_theme( $category );
?>

<div class="flex items-start gap-3 p-3">
    <div class="w-6 h-6 rounded-full <?php echo esc_attr( $theme['accent_bg'] ); ?> flex items-center justify-center flex-shrink-0 mt-0.5">
        <?php verigate_icon( 'check', 'w-3.5 h-3.5 ' . $theme['accent_text'], 14 ); ?>
    </div>
    <div>
        <h5 class="font-medium text-foreground text-sm"><?php echo esc_html( $title ); ?></h5>
        <?php if ( $desc ) : ?>
            <p class="text-xs text-muted-foreground mt-0.5"><?php echo esc_html( $desc ); ?></p>
        <?php endif; ?>
    </div>
</div>
