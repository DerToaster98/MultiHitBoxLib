package de.dertoaster.multihitboxlib.api;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Queue;
import java.util.Optional;
import java.util.UUID;

// DO NOT IMPLEMENT THIS INTERFACE!!
public interface IMHLibFieldAccessor<T extends LivingEntity> {

    public default PartEntity<?>[] _mhlibAccess_getPartArray() {
        throw new NotImplementedException();
    }

    public default void _mhlibAccess_setPartArray(final PartEntity<?>[] value) {
        throw new NotImplementedException();
    }

    public default Queue<UUID> _mhlibAccess_getTrackerQueue() {
        throw new NotImplementedException();
    }

    public default int _mhlibAccess_getTicksSinceLastSynch() {
        throw new NotImplementedException();
    }

    public default void _mhlibAccess_setTicksSinceLastSynch(int value) {
        throw new NotImplementedException();
    }

    public default Map<String, MHLibPartEntity<T>> _mhlibAccess_getPartMap() {
        throw new NotImplementedException();
    }

    public default void _mhlibAccess_setPartMap(Map<String, MHLibPartEntity<T>> value) {
        throw new NotImplementedException();
    }

    public default Map<String, BoneInformation> _mhlibAccess_getSynchMap() {
        throw new NotImplementedException();
    }

    public default UUID _mhlibAccess_getMasterUUID() {
        throw new NotImplementedException();
    }

    public default void _mhlibAccess_setMasterUUID(UUID value) {
        throw new NotImplementedException();
    }

    public default Optional<CPacketBoneInformation.Builder> _mlibAccess_getBoneInfoBuilder() {
        throw new NotImplementedException();
    }

    public default void _mlibAccess_setBoneInfoBuilder(Optional<CPacketBoneInformation.Builder> value) {
        throw new NotImplementedException();
    }

}
