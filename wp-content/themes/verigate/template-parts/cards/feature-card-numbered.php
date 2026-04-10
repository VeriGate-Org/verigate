<?php
/**
 * Feature Card — Numbered Step
 *
 * @package VeriGate
 */

$number   = $args['number'] ?? 1;
$title    = $args['title'] ?? '';
$desc     = $args['description'] ?? '';
$category = $args['category'] ?? 'company';
$theme    = verigate_get_category_theme( $category );
?>

<div class="relative p-6 border border-border rounded-lg bg-card hover:shadow-md transition-all duration-200">
    <div class="w-10 h-10 rounded-full <?php echo esc_attr( $theme['accent_bg'] ); ?> flex items-center justify-center mb-4">
        <span class="text-sm font-bold <?php echo esc_attr( $theme['accent_text'] ); ?>"><?php echo esc_html( $number ); ?></span>
    </div>
    <h4 class="font-semibold text-foreground mb-2"><?php echo esc_html( $title ); ?></h4>
    <p class="text-sm text-muted-foreground leading-relaxed"><?php echo esc_html( $desc ); ?></p>
</div>
