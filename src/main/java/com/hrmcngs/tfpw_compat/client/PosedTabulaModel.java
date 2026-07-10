package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

/**
 * Ice and Fire のドラゴン Tabula モデル群を包み、姿勢に応じて描画するモデルを切り替えるラッパ。
 *
 * <p>IaF は姿勢ごとに別の .tbl を持つ ({@code ground} / {@code hovering} / {@code flying})。
 * {@code MobRenderer} はモデルを 1 つしか持てないので、このラッパが内部で選ぶ。
 * 3 ポーズはパーツ名・パーツ数・テクスチャサイズが同一なので、アニメータもテクスチャも共通で使える。
 *
 * <p>{@link EntityModel} の状態 ({@code young}/{@code riding}/{@code attackTime}) は現在の
 * 内側モデルへ転送する。
 */
public class PosedTabulaModel<T extends ElementDragonEntity> extends EntityModel<T> {

    /** IaF のドラゴン姿勢。対応する .tbl が存在する。 */
    public enum Pose {
        GROUND, HOVERING, FLYING, SLEEPING
    }

    private final TabulaModel<T> ground;
    private final TabulaModel<T> hovering;
    private final TabulaModel<T> flying;
    private final TabulaModel<T> sleeping;

    private TabulaModel<T> current;

    public PosedTabulaModel(TabulaModel<T> ground, TabulaModel<T> hovering,
                            TabulaModel<T> flying, TabulaModel<T> sleeping) {
        super(RenderType::entityCutoutNoCull);
        this.ground = ground;
        this.hovering = hovering;
        this.flying = flying;
        this.sleeping = sleeping;
        this.current = ground;
    }

    /** 描画する姿勢を選ぶ ( 描画直前に呼ぶ )。 */
    public void setPose(Pose pose) {
        switch (pose) {
            case FLYING -> this.current = this.flying;
            case HOVERING -> this.current = this.hovering;
            case SLEEPING -> this.current = this.sleeping;
            default -> this.current = this.ground;
        }
    }

    private void syncState() {
        this.current.young = this.young;
        this.current.riding = this.riding;
        this.current.attackTime = this.attackTime;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        syncState();
        this.current.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        syncState();
        this.current.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float r, float g, float b, float alpha) {
        this.current.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, r, g, b, alpha);
    }
}
