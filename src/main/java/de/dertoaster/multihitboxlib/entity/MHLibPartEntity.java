package de.dertoaster.multihitboxlib.entity;

import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;

public class MHLibPartEntity<T extends Entity> extends PartEntity<T> {

	public MHLibPartEntity(T parent, final SubPartConfig properties) {
		super(parent);
	}

	@Override
	protected void defineSynchedData() {
		
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag pCompound) {
		
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag pCompound) {
		
	}

}
