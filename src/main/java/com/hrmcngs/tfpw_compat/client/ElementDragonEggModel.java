package com.hrmcngs.tfpw_compat.client;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEggEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * 設置された属性ドラゴンの卵のモデル。
 *
 * <p>Ice and Fire の {@code DragonEggModel} と<b>同じ箱寸法・階層・UV</b>を vanilla の
 * {@link EntityModel} で再現したもの ( uranus 非依存 )。テクスチャは 64x32。
 * 積層した 4 つの箱で先細りの卵形になる。
 */
public class ElementDragonEggModel extends EntityModel<ElementDragonEggEntity> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(TfpwCompat.MOD_ID, "element_dragon_egg"), "main");

    private final ModelPart egg1;

    public ElementDragonEggModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.egg1 = root.getChild("egg1");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // IaF DragonEggModel と同一: texW=64 texH=32
        PartDefinition egg1 = root.addOrReplaceChild("egg1",
                CubeListBuilder.create().texOffs(0, 12).addBox(-3.0f, -2.8f, -3.0f, 6.0f, 6.0f, 6.0f),
                PartPose.offset(0.0f, 19.6f, 0.0f));
        PartDefinition egg3 = egg1.addOrReplaceChild("egg3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -4.6f, -2.5f, 5.0f, 5.0f, 5.0f),
                PartPose.ZERO);
        egg1.addOrReplaceChild("egg2",
                CubeListBuilder.create().texOffs(22, 2).addBox(-2.5f, -0.6f, -2.5f, 5.0f, 5.0f, 5.0f),
                PartPose.ZERO);
        egg3.addOrReplaceChild("egg4",
                CubeListBuilder.create().texOffs(28, 16).addBox(-2.0f, -4.8f, -2.0f, 4.0f, 4.0f, 4.0f),
                PartPose.offset(0.0f, -0.9f, 0.0f));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(ElementDragonEggEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 卵は静止
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float r, float g, float b, float alpha) {
        this.egg1.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, alpha);
    }
}
