package de.dertoaster.multihitboxlib.api;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.dertoaster.multihitboxlib.util.BoneInformation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;

public interface IMultipartEntity<T extends Entity> {
	
	public boolean hurt(PartEntity<T> subPart, DamageSource source, float damage);
	
	@Nullable
	public UUID getMasterUUID();
	
	public default void processBoneInformation(final Map<String, BoneInformation> boneInformation) {
		// Does nothing by default
	}
	
	public boolean syncWithModel();

}
