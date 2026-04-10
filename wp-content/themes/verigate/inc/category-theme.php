<?php
/**
 * Category Theme Colour Helper
 *
 * Port of categoryTheme.ts — maps category keys to Tailwind class sets.
 *
 * @package VeriGate
 */

defined( 'ABSPATH' ) || exit;

/**
 * Get category theme classes.
 *
 * @param string $category One of: verification, compliance, fraud, industry, platform, company.
 * @return array Associative array of themed Tailwind classes.
 */
function verigate_get_category_theme( string $category = 'company' ): array {
    $themes = array(
        'verification' => array(
            'icon_bg'           => 'bg-category-verification/10',
            'icon_color'        => 'text-category-verification',
            'badge_bg'          => 'bg-category-verification/10',
            'badge_text'        => 'text-category-verification',
            'badge_border'      => 'border-category-verification/30',
            'card_hover_border' => 'hover:border-category-verification/50',
            'card_hover_shadow' => 'hover:shadow-blue-500/10',
            'hero_bg_to'        => 'to-category-verification/5',
            'section_tint'      => 'bg-blue-50/30',
            'accent_bg'         => 'bg-category-verification/10',
            'accent_text'       => 'text-category-verification',
        ),
        'compliance' => array(
            'icon_bg'           => 'bg-category-compliance/10',
            'icon_color'        => 'text-category-compliance',
            'badge_bg'          => 'bg-category-compliance/10',
            'badge_text'        => 'text-category-compliance',
            'badge_border'      => 'border-category-compliance/30',
            'card_hover_border' => 'hover:border-category-compliance/50',
            'card_hover_shadow' => 'hover:shadow-teal-500/10',
            'hero_bg_to'        => 'to-category-compliance/5',
            'section_tint'      => 'bg-teal-50/30',
            'accent_bg'         => 'bg-category-compliance/10',
            'accent_text'       => 'text-category-compliance',
        ),
        'fraud' => array(
            'icon_bg'           => 'bg-category-fraud/10',
            'icon_color'        => 'text-category-fraud',
            'badge_bg'          => 'bg-category-fraud/10',
            'badge_text'        => 'text-category-fraud',
            'badge_border'      => 'border-category-fraud/30',
            'card_hover_border' => 'hover:border-category-fraud/50',
            'card_hover_shadow' => 'hover:shadow-amber-500/10',
            'hero_bg_to'        => 'to-category-fraud/5',
            'section_tint'      => 'bg-amber-50/30',
            'accent_bg'         => 'bg-category-fraud/10',
            'accent_text'       => 'text-category-fraud',
        ),
        'industry' => array(
            'icon_bg'           => 'bg-category-industry/10',
            'icon_color'        => 'text-category-industry',
            'badge_bg'          => 'bg-category-industry/10',
            'badge_text'        => 'text-category-industry',
            'badge_border'      => 'border-category-industry/30',
            'card_hover_border' => 'hover:border-category-industry/50',
            'card_hover_shadow' => 'hover:shadow-green-500/10',
            'hero_bg_to'        => 'to-category-industry/5',
            'section_tint'      => 'bg-green-50/30',
            'accent_bg'         => 'bg-category-industry/10',
            'accent_text'       => 'text-category-industry',
        ),
        'platform' => array(
            'icon_bg'           => 'bg-category-platform/10',
            'icon_color'        => 'text-category-platform',
            'badge_bg'          => 'bg-category-platform/10',
            'badge_text'        => 'text-category-platform',
            'badge_border'      => 'border-category-platform/30',
            'card_hover_border' => 'hover:border-category-platform/50',
            'card_hover_shadow' => 'hover:shadow-purple-500/10',
            'hero_bg_to'        => 'to-category-platform/5',
            'section_tint'      => 'bg-purple-50/30',
            'accent_bg'         => 'bg-category-platform/10',
            'accent_text'       => 'text-category-platform',
        ),
        'company' => array(
            'icon_bg'           => 'bg-primary/10',
            'icon_color'        => 'text-primary',
            'badge_bg'          => 'bg-primary/10',
            'badge_text'        => 'text-primary',
            'badge_border'      => 'border-primary/30',
            'card_hover_border' => 'hover:border-primary/50',
            'card_hover_shadow' => 'hover:shadow-blue-900/10',
            'hero_bg_to'        => 'to-primary/5',
            'section_tint'      => 'bg-secondary/30',
            'accent_bg'         => 'bg-primary/10',
            'accent_text'       => 'text-primary',
        ),
    );

    return $themes[ $category ] ?? $themes['company'];
}
