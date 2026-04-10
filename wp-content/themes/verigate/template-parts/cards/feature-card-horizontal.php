<?php
/**
 * Feature Card — Horizontal Layout
 *
 * @package VeriGate
 */

$icon     = $args['icon'] ?? 'shield-check';
$title    = $args['title'] ?? '';
$desc     = $args['description'] ?? '';
$category = $args['category'] ?? 'company';
$theme    = verigate_get_category_theme( $category );
?>

<div class="flex gap-4 p-4 rounded-lg hover:bg-accent/5 transition-colors group">
    <div class="w-10 h-10 rounded-lg <?php echo esc_attr( $theme['icon_bg'] ); ?> flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-all duration-200">
        <?php verigate_icon( $icon, 'w-5 h-5 ' . $theme['icon_color'], 20 ); ?>
    </div>
    <div>
        <h4 class="font-semibold text-foreground mb-1"><?php echo esc_html( $title ); ?></h4>
        <p class="text-sm text-muted-foreground leading-relaxed"><?php echo esc_html( $desc ); ?></p>
    </div>
</div>
