package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEggEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * 設置された属性ドラゴンの卵のレンダラ。
 *
 * <p>Ice and Fire の {@code DragonEggEntityRenderer} と同じ見た目 ( {@link ElementDragonEggModel} =
 * IaF DragonEggModel の再現 ) で、属性色に着色したテクスチャを貼る。
 */
public class ElementDragonEggRenderer extends EntityRenderer<ElementDragonEggEntity> {

    private final ElementDragonEggModel model;

    public ElementDragonEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ElementDragonEggModel(context.bakeLayer(ElementDragonEggModel.LAYER));
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(ElementDragonEggEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        // モデルは 24 基準 ( 下端 y=24 ) で組まれているので、 vanilla の生物と同じ変換で地面に立てる。
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0, -1.501, 0.0);
        VertexConsumer buffer = buffers.getBuffer(this.model.renderType(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElementDragonEggEntity entity) {
        return new ResourceLocation(TfpwCompat.MOD_ID,
                "textures/entity/element_dragon_egg/egg_" + entity.getElementName() + ".png");
    }
}
