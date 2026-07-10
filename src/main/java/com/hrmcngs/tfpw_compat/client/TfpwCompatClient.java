package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ModEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * クライアント専用の登録 (エンティティレンダラ)。MOD バス購読。
 */
@Mod.EventBusSubscriber(modid = TfpwCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TfpwCompatClient {
    private TfpwCompatClient() {}

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CORROSION_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.MIASMA_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.SOUL_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.SOUL_FIRE_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.WIND_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.WATER_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.DARK_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.HOLY_DRAGON.get(), ElementDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.ERASURE_DRAGON.get(), ElementDragonRenderer::new);
    }
}
