package de.dertoaster.multihitboxlib.api;

import org.joml.Vector3d;
import net.minecraft.util.Tuple;

public interface IMHLibExtendedRenderLayer {

	void pushToStack(Vector3d scaling, Vector3d rotation);
	
	Tuple<Vector3d, Vector3d> popStack();
	
	Vector3d getCurrentScaling();
	Vector3d getCurrentRotation();
	
	void applyCurrentValues(Vector3d scaling, Vector3d rotation);
	
	void resetStack();
	
	static final Vector3d DEFAULT_SCALING = new Vector3d(1, 1, 1);
	static final Vector3d DEFAULT_ROTATION = new Vector3d(0,0,0);
	
	default void resetCurrentValues() {
		applyCurrentValues(null, null);
	}
	
	default void onPostRender() {
		this.resetStack();
		this.resetCurrentValues();
	}
	
	default void onPreRender() {
		this.resetStack();
		this.applyCurrentValues(DEFAULT_SCALING, DEFAULT_ROTATION);
	}
	
	default void onRenderRecursivelyStart() {
		// Object needs to be cloned! Otherwise we will always modify the same thing
		final Vector3d currentRot = new Vector3d(this.getCurrentRotation());
		final Vector3d currentScale = new Vector3d(this.getCurrentScaling());
		if (currentRot != null && currentScale != null) {
			this.pushToStack(currentScale, currentRot);
		}
		
	}
	
	default void onRenderRecursivelyEnd() {
		Tuple<Vector3d, Vector3d> tuple = this.popStack();
		this.applyCurrentValues(tuple.getA(), tuple.getB());
	}
	
	
}
