package de.dertoaster.multihitboxlib.assetsynch.client;

import java.io.IOException;
import java.util.Optional;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import de.dertoaster.multihitboxlib.assetsynch.impl.TextureEnforcementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class TextureClientLogic {
	
	public static boolean receiveAndLoad(final TextureEnforcementManager manager, final ResourceLocation id, final byte[] data) {
		NativeImage ni = null;
		try {
			ni = NativeImage.read(data);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		TextureManager tm = Minecraft.getInstance().getTextureManager();
		AbstractTexture loadedInternally = tm.getTexture(id);
		if (loadedInternally instanceof DynamicTexture dt) {
			// If it is dynamic => easy, just reupload
			dt.getPixels().copyFrom(ni);
			dt.upload();
		} else {
			// Otherwise it gets icky...
			RenderSystem.assertOnRenderThreadOrInit();
			final int texId = TextureUtil.generateTextureId();
			
			Optional<Resource> textureBaseResource = Minecraft.getInstance().getResourceManager().getResource(id);
			
			Optional<TextureMetadataSection> textureBaseMeta;
			try {
				textureBaseMeta = textureBaseResource.isPresent() ? textureBaseResource.get().metadata().getSection(TextureMetadataSection.SERIALIZER) : Optional.empty();
			} catch (IOException e) {
				textureBaseMeta = Optional.empty();
				e.printStackTrace();
			}
			boolean blur = textureBaseMeta.isPresent() && textureBaseMeta.get().isBlur();
			boolean clamp = textureBaseMeta.isPresent() && textureBaseMeta.get().isClamp();
			
			TextureUtil.prepareImage(texId, 0, ni.getWidth(), ni.getHeight());
			ni.upload(0, 0, 0, 0, 0, ni.getWidth(), ni.getHeight(), blur, clamp, false, true);
		}
		
		return true;
	}

}
