package de.dertoaster.multihitboxlib.api.glibplus;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Tuple;
import org.joml.Vector3d;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import javax.annotation.Nullable;

public abstract class MHLibExtendedGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

	private Stack<Tuple<Vector3d, Vector3d>> scaleAndRotationStack = new ObjectArrayList<>();
	private Vector3d currentScaling = new Vector3d(1,1,1);
	private Vector3d currentRotation = new Vector3d(0,0,0);
	
	public MHLibExtendedGeoLayer(GeoRenderer<T> entityRendererIn) {
		super(entityRendererIn);
	}
	
	public void onPreRender() {
		this.scaleAndRotationStack = new ObjectArrayList<>();
		this.currentScaling = new Vector3d(1,1,1);
		this.currentRotation = new Vector3d(0,0,0);
	}
	
	public void onPostRender() {
		this.scaleAndRotationStack = null;
		this.currentScaling = null;
		this.currentRotation = null;
	}
	
	public void onRenderRecursivelyStart() {
		if (this.currentRotation != null && this.currentScaling != null) {
			scaleAndRotationStack.push(new Tuple<>(new Vector3d(this.currentScaling), new Vector3d(this.currentRotation)));
		}
	}
	
	public void onRenderRecursivelyEnd() {
		Tuple<Vector3d, Vector3d> tuple = this.scaleAndRotationStack.pop();
		this.currentScaling = tuple.getA();
		this.currentRotation = tuple.getB();
	}

	@Nullable
	public Vector3d getCurrentScalingEntry() {
		return this.currentScaling;
	}

	@Nullable
	public Vector3d getCurrentRotationEntry() {
		return this.currentRotation;
	}

}
