package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * ドラゴンの<b>光る目</b>のオーバーレイレイヤー。
 *
 * <p>Ice and Fire の {@code LayerDragonEyes} と同じく {@link RenderType#eyes} ( 全輝度の発光 )
 * で目テクスチャを重ねる。テクスチャは属性色に着色した {@code eyes_&lt;element&gt;.png} ( 512x256、
 * UV はドラゴン肌と共通 )。ベースモデルの現在ポーズにそのまま描くので、姿勢が変わっても追従する。
 */
public class DragonEyesLayer<T extends ElementDragonEntity>
        extends RenderLayer<T, PosedTabulaModel<T>> {

    public DragonEyesLayer(RenderLayerParent<T, PosedTabulaModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        // 睡眠中は目を閉じている ( 発光させない )
        if (entity.isDragonSleeping()) {
            return;
        }
        ResourceLocation eyes = new ResourceLocation(TfpwCompat.MOD_ID,
                "textures/entity/element_dragon/eyes.png");
        VertexConsumer buffer = buffers.getBuffer(RenderType.eyes(eyes));
        // ベースモデル ( 現在ポーズ ) をそのまま全輝度で描画する。
        this.getParentModel().renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}
