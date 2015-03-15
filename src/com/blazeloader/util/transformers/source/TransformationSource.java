package com.blazeloader.util.transformers.source;

import com.blazeloader.util.transformers.BLAccessTransformer;

public abstract class TransformationSource {
    public abstract void provideTransformations(BLAccessTransformer transformer);
}
