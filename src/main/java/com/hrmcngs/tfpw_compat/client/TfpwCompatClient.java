package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;
import com.hrmcngs.tfpw_compat.entity.ModEntities;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * クライアント専用の登録 (エンティティレンダラ)。MOD バス購読。
 *
 * <p>Ice and Fire + uranus がロードされていれば、IaF の本物のドラゴンモデル
 * ({@link IafDragonRenderer}) で描画する。無ければ自前の箱モデル
 * ({@link ElementDragonRenderer}) にフォールバックする。
 */
@Mod.EventBusSubscriber(modid = TfpwCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TfpwCompatClient {
    private TfpwCompatClient() {}

    /** IaF のドラゴンモデルを使えるか (uranus の TabulaModel が必要)。 */
    private static boolean canUseIafModel() {
        return ModList.get().isLoaded("iceandfire") && ModList.get().isLoaded("uranus");
    }

    /** 属性ドラゴンのモデル ( LayerDefinition ) を登録する ( フォールバック用の箱モデル )。 */
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ElementDragonModel.LAYER, ElementDragonModel::createBodyLayer);
        event.registerLayerDefinition(ElementDragonEggModel.LAYER, ElementDragonEggModel::createLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        boolean iaf = canUseIafModel();
        TfpwCompat.LOGGER.info("[TFPW-Compat] dragon renderer: {}",
                iaf ? "Ice and Fire model (tabula)" : "fallback box model");

        register(event, ModEntities.CORROSION_DRAGON, iaf);
        register(event, ModEntities.MIASMA_DRAGON, iaf);
        register(event, ModEntities.SOUL_DRAGON, iaf);
        register(event, ModEntities.SOUL_FIRE_DRAGON, iaf);
        register(event, ModEntities.WIND_DRAGON, iaf);
        register(event, ModEntities.WATER_DRAGON, iaf);
        register(event, ModEntities.DARK_DRAGON, iaf);
        register(event, ModEntities.HOLY_DRAGON, iaf);

        // 属性ドラゴンの卵 ( バニラの Dragon Egg ブロックモデルで描画 )
        event.registerEntityRenderer(ModEntities.ELEMENT_DRAGON_EGG.get(), ElementDragonEggRenderer::new);
    }

    /**
     * iaf=true なら IaF モデル、false なら箱モデルで登録する。
     * IafDragonRenderer は uranus のクラスを参照するため、iaf=false のときは
     * クラスロードすら起きないよう分岐の内側でのみ参照する。
     */
    private static <T extends ElementDragonEntity> void register(
            EntityRenderersEvent.RegisterRenderers event,
            RegistryObject<EntityType<T>> type, boolean iaf) {
        if (iaf) {
            event.registerEntityRenderer(type.get(), IafDragonRenderer::new);
        } else {
            event.registerEntityRenderer(type.get(), ElementDragonRenderer::new);
        }
    }
}
