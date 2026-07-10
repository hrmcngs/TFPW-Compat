package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 属性ドラゴンのレンダラ。
 *
 * <p><b>プレースホルダ</b>: 専用のドラゴンモデルはアート資産が必要なため、当面はバニラの
 * Phantom モデル (飛行生物) を流用する。テクスチャは属性別 (無ければ Minecraft が欠落表示、
 * クラッシュはしない)。本物のドラゴンモデル/テクスチャは差し替え TODO。
 */
public class ElementDragonRenderer<T extends ElementDragonEntity> extends MobRenderer<T, PhantomModel<T>> {

    public ElementDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new PhantomModel<>(context.bakeLayer(ModelLayers.PHANTOM)), 0.75f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(TfpwCompat.MOD_ID,
                "textures/entity/element_dragon/" + entity.getElementName() + ".png");
    }
}
