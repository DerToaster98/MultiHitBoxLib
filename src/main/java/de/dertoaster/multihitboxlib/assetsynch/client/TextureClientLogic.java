package de.dertoaster.multihitboxlib.assetsynch.client;

import java.io.IOException;

import com.mojang.blaze3d.platform.NativeImage;

import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class TextureClientLogic {
	
	public static boolean receiveAndLoad(final AbstractAssetEnforcementManager manager, final ResourceLocation id, final byte[] data) throws IOException {
		NativeImage ni = null;
		try {
			ni = NativeImage.read(data);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		final Minecraft mc = Minecraft.getInstance();
		
		DynamicTexture dynTex = new DynamicTexture(ni);
		
		mc.getTextureManager().register(id, dynTex);
		return true;
		
		//Causes the client to freeze cause it waits for the submit call
//		AbstractTexture loadedInternally;
//
//		try {
//			loadedInternally = mc.submit(() -> mc.getTextureManager().getTexture(id)).get();
//		}
//		catch (InterruptedException | ExecutionException e) {
//			throw new IOException("Failed to load original texture: " + id, e);
//		}
//		if (loadedInternally instanceof DynamicTexture dt) {
//			// If it is dynamic => easy, just reupload
//			dt.getPixels().copyFrom(ni);
//			dt.upload();
//		} else {
//			// Otherwise it gets icky...
//			RenderSystem.assertOnRenderThreadOrInit();
//			final int texId = TextureUtil.generateTextureId();
//			
//			Optional<Resource> textureBaseResource = Minecraft.getInstance().getResourceManager().getResource(id);
//			
//			Optional<TextureMetadataSection> textureBaseMeta;
//			try {
//				textureBaseMeta = textureBaseResource.isPresent() ? textureBaseResource.get().metadata().getSection(TextureMetadataSection.SERIALIZER) : Optional.empty();
//			} catch (IOException e) {
//				textureBaseMeta = Optional.empty();
//				e.printStackTrace();
//			}
//			boolean blur = textureBaseMeta.isPresent() && textureBaseMeta.get().isBlur();
//			boolean clamp = textureBaseMeta.isPresent() && textureBaseMeta.get().isClamp();
//			
//			TextureUtil.prepareImage(texId, 0, ni.getWidth(), ni.getHeight());
//			ni.upload(0, 0, 0, 0, 0, ni.getWidth(), ni.getHeight(), blur, clamp, false, true);
//		}
//		
//		return true;
	}

}
