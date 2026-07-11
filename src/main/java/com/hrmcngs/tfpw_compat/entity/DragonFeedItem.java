package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 属性ドラゴンの餌 ( 属性ごとに 1 種 )。対応属性のドラゴンに使うと回復 + 成長する
 * ({@link ElementDragonEntity#mobInteract})。
 */
public class DragonFeedItem extends Item {
    private final String element;

    public DragonFeedItem(String element, Properties properties) {
        super(properties);
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }

    /** stack が element 属性のドラゴン用の餌か。 */
    public static boolean isFeedFor(ItemStack stack, String element) {
        return stack.getItem() instanceof DragonFeedItem feed && feed.element.equals(element);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.tfpw_compat.dragon_feed"));
    }
}
