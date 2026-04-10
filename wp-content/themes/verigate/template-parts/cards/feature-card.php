<?php
/**
 * Feature Card — Standard
 *
 * @package VeriGate
 */

$icon     = $args['icon'] ?? 'shield-check';
$title    = $args['title'] ?? '';
$desc     = $args['description'] ?? '';
$href     = $args['href'] ?? '';
$category = $args['category'] ?? 'company';
$theme    = verigate_get_category_theme( $category );
?>

<div class="bg-gradient-to-br from-card to-card/50 hover:from-card hover:to-accent/5 border border-border/50 rounded-lg p-6 space-y-4 <?php echo esc_attr( $theme['card_hover_border'] ); ?> transition-all duration-200 hover:shadow-xl <?php echo esc_attr( $theme['card_hover_shadow'] ); ?> hover:-translate-y-1 group">
    <div class="w-12 h-12 rounded-lg <?php echo esc_attr( $theme['icon_bg'] ); ?> flex items-center justify-center group-hover:bg-accent/20 group-hover:scale-110 transition-all duration-200">
        <?php verigate_icon( $icon, 'w-6 h-6 ' . $theme['icon_color'], 24 ); ?>
    </div>

    <?php if ( $href ) : ?>
        <h3 class="text-xl font-semibold text-foreground flex items-center justify-between">
            <a href="<?php echo esc_url( $href ); ?>" class="hover:text-accent transition-colors">
                <?php echo esc_html( $title ); ?>
            </a>
            <?php verigate_icon( 'chevron-right', 'w-5 h-5 text-muted-foreground group-hover:text-accent group-hover:translate-x-1 transition-all duration-200', 20 ); ?>
        </h3>
    <?php else : ?>
        <h3 class="text-xl font-semibold text-foreground"><?php echo esc_html( $title ); ?></h3>
    <?php endif; ?>

    <p class="text-muted-foreground leading-relaxed"><?php echo esc_html( $desc ); ?></p>

    <?php if ( $href ) : ?>
        <a href="<?php echo esc_url( $href ); ?>" class="inline-flex items-center gap-1.5 text-sm font-medium <?php echo esc_attr( $theme['accent_text'] ); ?> group-hover:gap-2.5 transition-all duration-200">
            Learn More <?php verigate_icon( 'arrow-right', 'w-4 h-4', 16 ); ?>
        </a>
    <?php endif; ?>
</div>
