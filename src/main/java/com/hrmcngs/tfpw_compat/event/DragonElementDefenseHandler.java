package com.hrmcngs.tfpw_compat.event;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.compat.IceAndFireCompat;
import com.hrmcngs.tfpw_compat.compat.TfpwHostCompat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * issue #199-A4 の runtime 橋渡し。
 *
 * <p>Ice and Fire のドラゴン属性攻撃 (炎/氷/雷ブレス・噛みつき) を検出し、対象が本体
 * the_four_primitives_and_weapons の魔導書 (book スロット) で対応するカウンター属性を
 * 装備していればダメージを無効化する。
 *
 * <p>対応関係は本体 {@code ElementType.getCounterElement()} 準拠:
 * 火ドラゴン→WATER 本 / 氷ドラゴン→FIRE 本 / 雷ドラゴン→WIND 本 で無効化。
 *
 * <p>Ice and Fire も本体も未ロードなら no-op。FORGE イベントバスへ
 * {@link TfpwCompat} から登録する。
 */
public final class DragonElementDefenseHandler {

    private DragonElementDefenseHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        // どちらかが無ければ連携そのものが成立しない。
        if (!IceAndFireCompat.isLoaded() || !TfpwHostCompat.isLoaded()) return;

        String element = IceAndFireCompat.elementForDamageSource(event.getSource());
        if (element == null) return; // ドラゴン属性攻撃ではない

        LivingEntity target = event.getEntity();
        if (TfpwHostCompat.isElementNullifiedByBook(target, element)) {
            // カウンター属性の魔導書でブレスを完全無効化。
            event.setCanceled(true);
        }
    }
}
