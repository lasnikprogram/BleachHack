/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package bleach.hack.util.render;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class WorldRenderUtils {

	private static final MinecraftClient mc = MinecraftClient.getInstance();
	private static Field shaderLightField;

	/**
	 * Draws a Text string in the world.
	 *  
	 * @return The used MatrixStack for further use
	 */
	public static MatrixStack drawText(String str, double x, double y, double z, double scale) {
		MatrixStack matrix = matrixFrom(x, y, z);

		Camera camera = mc.gameRenderer.getCamera();
		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		matrix.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);
		
		int halfWidth = mc.textRenderer.getWidth(str) / 2;
		
        int opacity = (int) (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
		
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

		mc.textRenderer.draw(str, -halfWidth, 0f, 553648127, false, matrix.peek().getModel(), immediate, true, opacity, 0xf000f0);
		immediate.draw();
        mc.textRenderer.draw(str, -halfWidth, 0f, -1, false, matrix.peek().getModel(), immediate, true, 0, 0xf000f0);
        immediate.draw();

		RenderSystem.disableBlend();

		return matrix;
	}

	/**
	 * Draws a 2D gui items somewhere in the world.
	 *  
	 * @return The used MatrixStack for further use
	 */
	public static MatrixStack drawGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		MatrixStack matrix = matrixFrom(x, y, z);

		Camera camera = mc.gameRenderer.getCamera();
		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

		matrix.scale((float) scale, (float) scale, 0.001f);
		matrix.translate(offX, offY, 0);

		if (item.isEmpty())
			return matrix;

		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f));

		//mc.getBufferBuilders().getEntityVertexConsumers().draw();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		Vec3f[] currentLight = getCurrentLight();
		DiffuseLighting.disableGuiDepthLighting();

		mc.getBufferBuilders().getOutlineVertexConsumers().setColor(255, 255, 255, 255);

		mc.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
				OverlayTexture.DEFAULT_UV, matrix, mc.getBufferBuilders().getOutlineVertexConsumers(), 0);

		mc.getBufferBuilders().getOutlineVertexConsumers().draw();

		RenderSystem.setShaderLights(currentLight[0], currentLight[1]);
		RenderSystem.disableBlend();

		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180f));

		return matrix;
	}

	public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrix = new MatrixStack();

		Camera camera = mc.gameRenderer.getCamera();
		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

		matrix.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrix;
	}
	
	public static Vec3f[] getCurrentLight() {
		if (shaderLightField == null) {
			shaderLightField = FieldUtils.getField(RenderSystem.class, "shaderLightDirections", true);
		}
		
		try {
			return (Vec3f[]) shaderLightField.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
