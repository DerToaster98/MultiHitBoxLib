package de.dertoaster.multihitboxlib.entity;

import java.util.Optional;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class MHLibPartEntity<T extends Entity> extends PartEntity<T> {

	private final SubPartConfig config;
	private EntityDimensions baseSize = EntityDimensions.fixed(1, 1);
	public static final EntityDimensions FALLBACK_SIZE = EntityDimensions.fixed(1, 1);

	protected int newPosRotationIncrements;
	protected double interpTargetX;
	protected double interpTargetY;
	protected double interpTargetZ;
	protected double interpTargetYaw;
	protected double interpTargetPitch;
	public float renderYawOffset;
	public float prevRenderYawOffset;

	public int deathTime;
	public int hurtTime;
	
	private boolean enabled = true;

	private Optional<Tuple<Float, Float>> currentSizeModifier = Optional.empty();

	public MHLibPartEntity(T parent, final SubPartConfig properties) {
		super(parent);
		this.config = properties;
		this.baseSize = EntityDimensions.scalable((float) this.config.baseSize().x, (float) this.config.baseSize().y);
	}
	
	public SubPartConfig getConfig() {
		return this.config;
	}

	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
		interpTargetX = x;
		interpTargetY = y;
		interpTargetZ = z;
		interpTargetYaw = yaw;
		interpTargetPitch = pitch;
		newPosRotationIncrements = posRotationIncrements;
	}

	@Override
	public void tick() {
		updateLastPos();
		super.tick();
		if (this.newPosRotationIncrements > 0) {
			double d0 = this.getX() + (this.interpTargetX - this.getX()) / (double) this.newPosRotationIncrements;
			double d2 = this.getY() + (this.interpTargetY - this.getY()) / (double) this.newPosRotationIncrements;
			double d4 = this.getZ() + (this.interpTargetZ - this.getZ()) / (double) this.newPosRotationIncrements;
			double d6 = Mth.wrapDegrees(this.interpTargetYaw - (double) this.getYRot());
			this.setYRot((float) ((double) this.getYRot() + d6 / (double) this.newPosRotationIncrements));
			this.setXRot((float) ((double) this.getXRot() + (this.interpTargetPitch - (double) this.getXRot()) / (double) this.newPosRotationIncrements));
			--this.newPosRotationIncrements;
			this.setPos(d0, d2, d4);
			this.setRot(this.getYRot(), this.getXRot());
		}

		while (this.getYRot() - yRotO < -180F)
			yRotO -= 360F;
		while (this.getYRot() - yRotO >= 180F)
			yRotO += 360F;

		while (renderYawOffset - prevRenderYawOffset < -180F)
			prevRenderYawOffset -= 360F;
		while (renderYawOffset - prevRenderYawOffset >= 180F)
			prevRenderYawOffset += 360F;

		while (this.getXRot() - xRotO < -180F)
			xRotO -= 360F;
		while (this.getXRot() - xRotO >= 180F)
			xRotO += 360F;
	}
	
	public SPacketUpdateMultipart.PartDataHolder writeData() {
		return new SPacketUpdateMultipart.PartDataHolder(
				this.getX(),
				this.getY(),
				this.getZ(),
				this.getYRot(),
				this.getXRot(),
				this.baseSize.width,
				this.baseSize.height,
				this.baseSize.fixed,
				getEntityData().isDirty(),
				getEntityData().isDirty() ? getEntityData().packDirty() : null);

	}

	public void readData(SPacketUpdateMultipart.PartDataHolder data) {
		Vec3 vec = new Vec3(data.x(), data.y(), data.z());
		this.setPositionAndRotationDirect(vec.x(), vec.y(), vec.z(), data.yRot(), data.xRot(), 3);
		final float w = data.width();
		final float h = data.height();
		this.baseSize = (data.fixed() ? EntityDimensions.fixed(w, h) : EntityDimensions.scalable(w, h));
		this.refreshDimensions();
		if (data.dirty())
			getEntityData().assignValues(data.data());
	}

	public final void updateLastPos() {
		this.setPos(getX(), getY(), getZ());
		yRotO = this.getYRot();
		xRotO = this.getXRot();
		tickCount++;
	}

	@Override
	public void setPos(double pX, double pY, double pZ) {
		super.setPosRaw(pX, pY, pZ);
		this.setOldPosAndRot();
		
		this.setBoundingBox(this.getDimensions(Pose.STANDING).makeBoundingBox(pX, pY, pZ));
	}

	public Vec3 getConfigPositionOffset() {
		return this.config.basePosition();
	}
	
	public String getConfigName() {
		return this.config.name();
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

	public boolean hasCustomRenderer() {
		return false;
	}

	@Override
	public boolean isInvisible() {
		// Return true, otherwise the hitbox renders twice for whatever reason
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return this.config.collidable();
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (!this.config.canReceiveDamage()) {
			return false;
		}
		
		if(this.isInvulnerableTo(pSource)) {
			return false;
		}
		
		pAmount *= this.config.damageModifier();
		if (this.getParent() instanceof IMultipartEntity<?> ime && ime != null) {
			return ((IMultipartEntity) ime).hurt(this, pSource, pAmount);
		} else {
			return super.hurt(pSource, pAmount);
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose pPose) {
		if (this.baseSize == null) {
			return FALLBACK_SIZE;
		}
		if (this.currentSizeModifier != null && this.currentSizeModifier.isPresent()) {
			return this.baseSize.scale(this.currentSizeModifier.get().getA(), this.currentSizeModifier.get().getB());
		}
		return this.baseSize;
	}

	public boolean is(Entity pEntity) {
		return this == pEntity || this.getParent() == pEntity;
	}

	@Override
	public boolean isPickable() {
		return this.config.collidable();
	}

	public void setHidden(boolean hidden) {
		this.enabled = !hidden;
	}
	
}
