package com.eerussianguy.firmalife.te;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSmallPlant extends ModelBase
{
    private final ModelRenderer bone;
    private final ModelRenderer bone2;

    public ModelSmallPlant() {
        textureWidth = 64;
        textureHeight = 64;

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone, 0.0F, -0.7854F, 0.0F);
        bone.cubeList.add(new ModelBox(bone, 0, 16, 0.0F, -16.0F, -8.0F, 0, 16, 16, 0.0F, false));

        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(bone2, 0.0F, 0.7854F, 0.0F);
        bone2.cubeList.add(new ModelBox(bone2, 0, 0, 0.0F, -16.0F, -8.0F, 0, 16, 16, 0.0F, false));
    }

    public void render(float f5) {
        bone.render(f5);
        bone2.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

