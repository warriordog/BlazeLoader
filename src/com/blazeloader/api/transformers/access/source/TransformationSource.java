package com.blazeloader.api.transformers.access.source;

import com.blazeloader.api.transformers.BLAccessTransformer;

public abstract class TransformationSource {
    public abstract void provideTransformations(BLAccessTransformer transformer);
}
