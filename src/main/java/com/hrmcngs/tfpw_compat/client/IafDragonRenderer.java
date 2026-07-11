package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Ice and Fire の<b>本物のドラゴンモデル</b>で属性ドラゴンを描画するレンダラ。
 *
 * <p>IaF の {@code RenderDragonBase} は {@code MobRenderer<EntityDragonBase, ...>} に型が
 * 固定されていて流用できないが、uranus の {@code TabulaModel<T extends Entity>} は Entity 汎用
 * なので、モデル(形状)だけを借りて自前エンティティで使う。
 *
 * <p>テクスチャは IaF の {@code firedragon/gray_5.png} を<b>見本に生成した属性色版</b>
 * ({@code tfpw_compat:textures/entity/element_dragon/&lt;element&gt;.png}, 512x256)。
 * 元テクスチャは平均明度 17% と暗く、頂点色で乗算すると真っ黒になってしまうため、
 * 輝度を正規化してから着色している。
 *
 * <p>Ice and Fire / uranus が無い環境ではクラスロードに失敗するため、
 * {@link TfpwCompatClient} がロード判定してから使う ({@link ElementDragonRenderer} へフォールバック)。
 */
public class IafDragonRenderer<T extends ElementDragonEntity> extends MobRenderer<T, PosedTabulaModel<T>> {

    /**
     * IaF は姿勢ごとに別の .tbl を持つ ({@code assets/iceandfire/models/tabula/firedragon/*.tbl})。
     * 地上 / ホバリング / 飛行 の 3 ポーズを読み込み、状態に応じて切り替える。
     */
    private static final ResourceLocation MODEL_GROUND =
            new ResourceLocation("iceandfire", "firedragon/firedragon_ground");
    private static final ResourceLocation MODEL_HOVERING =
            new ResourceLocation("iceandfire", "firedragon/firedragon_hovering");
    private static final ResourceLocation MODEL_FLYING =
            new ResourceLocation("iceandfire", "firedragon/firedragon_flying");
    private static final ResourceLocation MODEL_SLEEPING =
            new ResourceLocation("iceandfire", "firedragon/firedragon_sleeping");

    /** 飛行ポーズに切り替える水平速度の閾値 ( これ未満はホバリング )。 */
    private static final double FLYING_SPEED_SQR = 0.01D;

    /**
     * モデルの実寸は 1.0 倍で 高 4.7 × 長 5.2 ブロック = <b>成体のドラゴン</b>。
     * ( 実測: firedragon_ground.tbl の bbox = 75.2px × 82.6px、 1px = 1/16 block )
     */
    private static final float SCALE = 1.0f;

    public IafDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new PosedTabulaModel<>(
                IafDragonRenderer.<T>loadModel(MODEL_GROUND),
                IafDragonRenderer.<T>loadModel(MODEL_HOVERING),
                IafDragonRenderer.<T>loadModel(MODEL_FLYING),
                IafDragonRenderer.<T>loadModel(MODEL_SLEEPING)), 1.5f);
        // 光る目 ( 本家 LayerDragonEyes 相当 )
        this.addLayer(new DragonEyesLayer<>(this));
    }

    private static <E extends ElementDragonEntity> TabulaModel<E> loadModel(ResourceLocation loc) {
        return TabulaModelHandlerHelper.getModel(loc, IafDragonAnimator::new);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        this.model.setPose(poseFor(entity));
        // 中立グレー肌に、この個体のカラーバリアント色を乗算 ( 本家式の色分け )。
        // グレーの明度中心 ~0.8 を補正して色を出す。
        int rgb = entity.getTintColor();
        this.model.setTint(
                Math.min(1.0f, ((rgb >> 16) & 0xFF) / 255.0f / 0.8f),
                Math.min(1.0f, ((rgb >> 8) & 0xFF) / 255.0f / 0.8f),
                Math.min(1.0f, (rgb & 0xFF) / 255.0f / 0.8f));
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    /** 睡眠 → SLEEPING、地上 → GROUND、空中で水平移動中 → FLYING、空中で停滞 → HOVERING。 */
    private static PosedTabulaModel.Pose poseFor(ElementDragonEntity entity) {
        if (entity.isDragonSleeping()) {
            return PosedTabulaModel.Pose.SLEEPING;
        }
        if (entity.onGround()) {
            return PosedTabulaModel.Pose.GROUND;
        }
        return entity.getDeltaMovement().horizontalDistanceSqr() > FLYING_SPEED_SQR
                ? PosedTabulaModel.Pose.FLYING
                : PosedTabulaModel.Pose.HOVERING;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float s = SCALE * entity.getScale(); // 成長段階でスケール
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        // 肌は中立グレー ( 色は個体バリアントで描画時に乗算 )。 睡眠 = sleeping、 通常 = 成長段階別。
        String path = entity.isDragonSleeping() ? "sleeping" : ("stage_" + entity.getStage());
        return new ResourceLocation(TfpwCompat.MOD_ID, "textures/entity/element_dragon/" + path + ".png");
    }
}
