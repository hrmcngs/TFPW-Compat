package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 属性ドラゴンのレンダラ。自前の {@link ElementDragonModel} を使い、
 * テクスチャは属性別 ({@code textures/entity/element_dragon/<element>.png})。
 */
public class ElementDragonRenderer<T extends ElementDragonEntity> extends MobRenderer<T, ElementDragonModel<T>> {

    private static final float SCALE = 1.6f;

    public ElementDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new ElementDragonModel<>(context.bakeLayer(ElementDragonModel.LAYER)), 1.0f);
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float s = SCALE * entity.getScale();
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        // フォールバック ( IaF 不在時。 実際は IaF 必須なので未到達 )。中立グレー肌を使う。
        return new ResourceLocation(TfpwCompat.MOD_ID,
                "textures/entity/element_dragon/stage_" + entity.getStage() + ".png");
    }
}
