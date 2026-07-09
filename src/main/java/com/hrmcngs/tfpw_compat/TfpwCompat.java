package com.hrmcngs.tfpw_compat;

import com.hrmcngs.tfpw_compat.compat.IceAndFireCompat;
import com.hrmcngs.tfpw_compat.compat.MekanismCompat;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * TFPW-Compat エントリポイント。
 *
 * the_four_primitives_and_weapons のアドオンとして、
 *   - Ice and Fire (ドラゴン) 連携   → {@link IceAndFireCompat}
 *   - Mekanism (発電) 連携           → {@link MekanismCompat}
 * を提供する。
 *
 * いずれの連携先も任意依存。相手 MOD が無ければ該当連携は no-op になり、
 * この MOD 自体は常にロードに成功する。
 */
@Mod(TfpwCompat.MOD_ID)
public final class TfpwCompat {
    public static final String MOD_ID = "tfpw_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TfpwCompat() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);

        // 連携先のロード状況をここで確認しておく (詳細な初期化は各 Compat 側で遅延実行)。
        IceAndFireCompat.init();
        MekanismCompat.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[TFPW-Compat] setup: iceandfire={}, mekanism={}",
                IceAndFireCompat.isLoaded(), MekanismCompat.isLoaded());
    }
}
