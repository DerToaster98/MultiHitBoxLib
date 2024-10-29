package de.dertoaster.multihitboxlib.partentityimp;

import org.jetbrains.annotations.Nullable;

public interface IEntityInterface {
    boolean multipart$isMultipartEntity();

    @Nullable
    PartEntity<?>[] multipart$getParts();
}
