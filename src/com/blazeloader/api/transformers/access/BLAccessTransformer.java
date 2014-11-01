package com.blazeloader.api.transformers.access;

import com.blazeloader.api.transformers.access.map.DefaultTransformationMap;
import com.blazeloader.api.transformers.access.map.GlobalTransformationMap;
import com.blazeloader.api.transformers.access.map.TransformationMap;
import com.blazeloader.api.transformers.access.source.BLATSource;
import com.blazeloader.api.transformers.access.transformation.Transformation;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public class BLAccessTransformer implements IClassTransformer {
    private static BLAccessTransformer instance;

    private final TransformationMap transformations;
    private final GlobalTransformationMap globalTransformations;

    public BLAccessTransformer() {
        if (instance != null) {
            throw new IllegalStateException("Cannot create more than one BLAccessTransformer!");
        }
        instance = this;

        transformations = new DefaultTransformationMap();
        globalTransformations = new GlobalTransformationMap();

        try {
            new BLATSource(getClass().getResourceAsStream("/conf/bl_at.cfg")).provideTransformations(this);
        } catch (IOException e) {
            System.err.println("Failed to load bl_at.cfg!");
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        ClassNode classNode = new ClassNode(); //create a ClassNode to represent the class identified by these bytes
        new ClassReader(bytes).accept(classNode, 0); //read the class from the bytes

        boolean didTransform = false; //set to true if a transformation occurred

        for (Transformation trans : transformations.getTransformations(transformedName)) {
            didTransform |= trans.apply(classNode); //all registered transformations get a chance to apply, using a shared classNode.
        }
        for (Transformation trans : globalTransformations.getTransformations()) {
            didTransform |= trans.apply(classNode); //all registered transformations get a chance to apply, using a shared classNode.
        }

        if (didTransform) { //only recreate the class if it was actually transformed
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); //create a classWriter to output the class
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return bytes; //return original class if it was not transformed
    }

    public void addTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Transformation cannot be null!");
        }
        transformations.addTransformation(transformation.targetClass, transformation);
    }

    public void addGlobalTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Transformation cannot be null!");
        }
        globalTransformations.addTransformation(transformation.targetClass, transformation);
    }

    public static BLAccessTransformer instance() {
        return instance;
    }
}
