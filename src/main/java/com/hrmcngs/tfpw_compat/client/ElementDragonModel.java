package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 属性ドラゴンのモデル (issue #199-A3)。
 *
 * <p>バニラの {@code EnderDragonModel} / {@code PhantomModel} は型引数が
 * {@code EnderDragon} / {@code Phantom} に縛られており自エンティティに使えないため、
 * 胴・首・頭・尾・翼・脚 を箱で自前に組む。テクスチャは属性ごと (64x64)。
 */
public class ElementDragonModel<T extends ElementDragonEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(TfpwCompat.MOD_ID, "element_dragon"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;

    public ElementDragonModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.tail = this.body.getChild("tail");
        this.leftWing = this.body.getChild("left_wing");
        this.rightWing = this.body.getChild("right_wing");
        this.frontLeftLeg = this.body.getChild("front_left_leg");
        this.frontRightLeg = this.body.getChild("front_right_leg");
        this.backLeftLeg = this.body.getChild("back_left_leg");
        this.backRightLeg = this.body.getChild("back_right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, -4.0f, -8.0f, 10.0f, 8.0f, 16.0f),
                PartPose.offset(0.0f, 14.0f, 0.0f));

        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.0f, -2.0f, -9.0f, 4.0f, 4.0f, 9.0f),
                PartPose.offset(0.0f, -2.0f, -8.0f));

        neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(26, 24).addBox(-3.0f, -3.0f, -7.0f, 6.0f, 6.0f, 7.0f)
                        // 角
                        .texOffs(0, 37).addBox(-3.0f, -5.0f, -3.0f, 1.0f, 2.0f, 1.0f)
                        .texOffs(0, 37).addBox(2.0f, -5.0f, -3.0f, 1.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 0.0f, -9.0f));

        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(0, 41).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 14.0f),
                PartPose.offset(0.0f, -1.0f, 8.0f));

        body.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(34, 0).addBox(0.0f, -0.5f, -5.0f, 16.0f, 1.0f, 10.0f),
                PartPose.offset(5.0f, -3.0f, 0.0f));

        body.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(34, 12).addBox(-16.0f, -0.5f, -5.0f, 16.0f, 1.0f, 10.0f),
                PartPose.offset(-5.0f, -3.0f, 0.0f));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(48, 24).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f);
        body.addOrReplaceChild("front_left_leg", leg, PartPose.offset(3.5f, 4.0f, -5.0f));
        body.addOrReplaceChild("front_right_leg", leg, PartPose.offset(-3.5f, 4.0f, -5.0f));
        body.addOrReplaceChild("back_left_leg", leg, PartPose.offset(3.5f, 4.0f, 5.0f));
        body.addOrReplaceChild("back_right_leg", leg, PartPose.offset(-3.5f, 4.0f, 5.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 頭は視線に追従
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);

        // 羽ばたき ( 地上でもゆっくり動かす )
        float flap = Mth.cos(ageInTicks * 0.3f) * 0.45f;
        this.leftWing.zRot = -flap;
        this.rightWing.zRot = flap;

        // 首と尾をゆっくり揺らす
        this.neck.xRot = Mth.cos(ageInTicks * 0.1f) * 0.05f;
        this.tail.yRot = Mth.cos(ageInTicks * 0.12f) * 0.15f;
        this.tail.xRot = 0.1f + Mth.cos(ageInTicks * 0.09f) * 0.05f;

        // 歩行に合わせて脚を振る
        float swing = Mth.cos(limbSwing * 0.6662f) * 1.2f * limbSwingAmount;
        this.frontLeftLeg.xRot = swing;
        this.backRightLeg.xRot = swing;
        this.frontRightLeg.xRot = -swing;
        this.backLeftLeg.xRot = -swing;
    }
}
