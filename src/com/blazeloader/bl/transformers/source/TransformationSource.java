package com.blazeloader.bl.transformers.source;

import com.blazeloader.bl.transformers.BLAccessTransformer;

public abstract class TransformationSource {
    public abstract void provideTransformations(BLAccessTransformer transformer);
}
