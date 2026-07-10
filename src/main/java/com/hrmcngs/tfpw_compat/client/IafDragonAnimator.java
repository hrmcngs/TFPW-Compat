package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;

import net.minecraft.util.Mth;

/**
 * Ice and Fire のドラゴン Tabula モデルを、自前の属性ドラゴン用に動かすアニメータ。
 *
 * <p>IaF 本来のアニメーション ({@code DragonAnimationsLibrary}) は {@code EntityDragonBase} の
 * 内部状態に依存するため使えない。ここでは首・頭・尾・脚・翼だけを簡易に動かす。
 *
 * <p><b>パーツ構造は実モデルで確認済み</b> ({@code firedragon_ground.tbl}):
 * <pre>
 *   armR1 / armL1  ← BodyUpper       … <b>翼の付け根</b> ( armR2 → fingerR*.* → membraneR* )
 *   ThighR / ThighL ← BodyLower      … 脚 ( → LegR / LegL )
 * </pre>
 * つまり {@code armX1} は脚ではなく翼。歩行スイングを当てないこと。
 *
 * <p><b>注意</b>: このクラスは uranus のクラスを直接参照するため、uranus が無い環境では
 * クラスロードに失敗する。{@link TfpwCompatClient} がロード判定してからのみ使用する。
 */
public class IafDragonAnimator<T extends ElementDragonEntity> implements ITabulaModelAnimator<T> {

    /** 尾のセグメント (根本→先端)。先に行くほど大きく揺れる。 */
    private static final String[] TAIL = {"Tail1", "Tail2", "Tail3", "Tail4", "Tail5"};
    /** 首のセグメント。 */
    private static final String[] NECK = {"Neck1", "Neck2", "Neck3"};

    @Override
    public void setRotationAngles(TabulaModel<T> model, T entity,
                                  float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scale) {
        final float deg = (float) Math.PI / 180f;
        boolean airborne = !entity.onGround();

        // 頭は視線に追従 ( 首 3 節で分担して自然に見せる )
        rotate(model, "Head", headPitch * deg * 0.5f, netHeadYaw * deg * 0.4f, 0f);
        for (String neck : NECK) {
            rotate(model, neck, headPitch * deg * 0.06f, netHeadYaw * deg * 0.2f, 0f);
        }

        // 尾: 先端ほど大きく左右に揺れる
        for (int i = 0; i < TAIL.length; i++) {
            float amp = 0.05f + i * 0.03f;
            rotate(model, TAIL[i], 0f, Mth.cos(ageInTicks * 0.1f - i * 0.4f) * amp, 0f);
        }

        // 顎を呼吸に合わせてわずかに開閉
        rotate(model, "Jaw", Math.abs(Mth.cos(ageInTicks * 0.07f)) * 0.08f, 0f, 0f);

        // 翼 ( armR1 / armL1 ): 空中では羽ばたき、地上では畳んだポーズのまま
        if (airborne) {
            float flap = Mth.cos(ageInTicks * 0.3f) * 0.5f;
            rotate(model, "armR1", 0f, 0f, flap);
            rotate(model, "armL1", 0f, 0f, -flap);
        } else {
            rotate(model, "armR1", 0f, 0f, 0f);
            rotate(model, "armL1", 0f, 0f, 0f);
        }

        // 脚 ( ThighR/L → LegR/L ): 地上の歩行に合わせて振る
        if (!airborne) {
            float swing = Mth.cos(limbSwing * 0.6f) * 0.9f * limbSwingAmount;
            rotate(model, "ThighL", swing, 0f, 0f);
            rotate(model, "ThighR", -swing, 0f, 0f);
            rotate(model, "LegL", -Math.abs(swing) * 0.5f, 0f, 0f);
            rotate(model, "LegR", -Math.abs(swing) * 0.5f, 0f, 0f);
        }
    }

    /** 既定ポーズからの相対回転を加える。パーツが無ければ無視 ( ポーズ .tbl 差異に強くする )。 */
    private static void rotate(TabulaModel<?> model, String cube, float x, float y, float z) {
        AdvancedModelBox box = model.getCube(cube);
        if (box == null) {
            return;
        }
        box.rotateAngleX = box.defaultRotationX + x;
        box.rotateAngleY = box.defaultRotationY + y;
        box.rotateAngleZ = box.defaultRotationZ + z;
    }
}
