package net.acomputerdog.BlazeLoader.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.objectweb.asm.Opcodes.*;

public class BLAccessTransformer implements IClassTransformer {
    private Map<String, List<AccessModifier>> modifiers = new HashMap<String, List<AccessModifier>>();
    public static InputStream AT_SOURCE_OVERRIDE = null;

    protected BLAccessTransformer(String atFile) throws IOException {
        readRules(atFile);
    }

    public BLAccessTransformer() throws IOException {
        this("/res/bl_at.cfg".replaceAll("/", File.separator));
    }

    private void readRules(String rulesPath) throws IOException {
        InputStream stream = null;
        BufferedReader reader = null;
        if (AT_SOURCE_OVERRIDE == null) {
            URL rulesFile;
            File file = new File(rulesPath);

            if (file.exists() || (file = new File(System.getProperty("user.dir"), rulesPath)).exists()) {
                rulesFile = file.toURI().toURL();
            } else {
                rulesFile = getClass().getResource(rulesPath);
            }
            if (rulesFile == null) {
                throw new FileNotFoundException("Rules file could not be located!");
            }
            stream = rulesFile.openStream();
        } else {
            stream = AT_SOURCE_OVERRIDE;
        }
        reader = new BufferedReader(new InputStreamReader(stream));

        try {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                String[] sections = line.split("#");
                String[] parts = sections[0].split(" ");

                if (parts.length > 2) {
                    throw new IllegalArgumentException("Malformed Line: " + line);
                }

                AccessModifier m = new AccessModifier();
                m.setAccessMode(parts[0].trim());
                String[] description = parts[1].trim().split("\\.");

                if (description.length == 1) {
                    m.changeClassVisibility = true;
                } else {
                    String name = description[1].trim();
                    if (!name.equals("*")) {
                        int index = name.indexOf('(');

                        if (index > 0) {
                            m.description = name.substring(index);
                            m.name = name.substring(0, index);
                        } else {
                            m.name = name;
                        }
                    }
                }

                String className = description[0];
                addToMap(className, m);
            }
        } finally {
            reader.close();
        }

        System.out.println(String.format("Loaded %d access rules from %s", countRules(), rulesPath));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        if (!modifiers.containsKey(transformedName)) {
            return bytes;
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        List<AccessModifier> mods = modifiers.get(transformedName);

        for (AccessModifier m : mods) {
            if (m.changeClassVisibility) {
                classNode.access = getFixedAccess(classNode.access, m);
            } else {
                changeAccessFor(m, classNode, m.name, m.description);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }

    private void changeAccessFor(AccessModifier m, ClassNode classNode, String name, String description) {
        if (name.isEmpty() && description.isEmpty()) {
            for (MethodNode methodNode : classNode.methods) {
                methodNode.access = getFixedAccess(methodNode.access, m);
            }
            for (FieldNode fieldNode : classNode.fields) {
                fieldNode.access = getFixedAccess(fieldNode.access, m);
            }
        } else if (description.isEmpty()) {
            for (FieldNode fieldNode : classNode.fields) {
                if (fieldNode.name.equals(name)) {
                    fieldNode.access = getFixedAccess(fieldNode.access, m);
                }
            }
        } else {
            for (MethodNode methodNode : classNode.methods) {
                if ((methodNode.name.equals(name) && methodNode.desc.equals(description))) {
                    methodNode.access = getFixedAccess(methodNode.access, m);
                }
            }
        }
    }

    private int getFixedAccess(int access, AccessModifier target) {
        target.oldAccessMode = access;
        int t = target.targetAccessMode;
        int ret = (access & ~7);

        switch (access & 7) {
            case ACC_PRIVATE:
                ret |= t;
                break;
            case 0:
                ret |= (t != ACC_PRIVATE ? t : 0);
                break;
            case ACC_PROTECTED:
                ret |= (t != ACC_PRIVATE && t != 0 ? t : ACC_PROTECTED);
                break;
            case ACC_PUBLIC:
                ret |= (t != ACC_PRIVATE && t != 0 && t != ACC_PROTECTED ? t : ACC_PUBLIC);
                break;
            default:
                throw new IllegalArgumentException("Non-existent access mode!");
        }

        if (target.changeFinal) {
            if (target.setFinal) {
                ret |= ACC_FINAL;
            } else {
                ret &= ~ACC_FINAL;
            }
        }
        target.newAccessMode = ret;
        return ret;
    }

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Usage: AccessTransformer <JarPath> <RulesFile> [RulesFile2]...");
                System.exit(1);
            }

            boolean hasTransformer = false;
            BLAccessTransformer[] trans = new BLAccessTransformer[args.length - 1];

            for (int x = 1; x < args.length; x++) {
                try {
                    trans[x - 1] = new BLAccessTransformer(args[x]);
                    hasTransformer = true;
                } catch (IOException e) {
                    System.out.println("Could not read Transformer Map: " + args[x]);
                    e.printStackTrace();
                }
            }

            if (!hasTransformer) {
                System.out.println("Could not find a valid transformer to perform!");
                System.exit(1);
            }

            File orig = new File(args[0]);
            File temp = new File(args[0] + ".ATBackup");

            if (!orig.exists() && !temp.exists()) {
                System.out.println("Could not find target jar: " + orig);
                System.exit(1);
            }

            if (!orig.renameTo(temp)) {
                System.out.println("Could not rename file: " + orig + " -> " + temp);
                System.exit(1);
            }

            try {
                processJar(temp, orig, trans);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            if (!temp.delete()) {
                System.out.println("Could not delete temp file: " + temp);
            }
        } catch (Throwable t) {
            System.gc();
            System.out.println("Error occurred transforming access!");
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void processJar(File inFile, File outFile, BLAccessTransformer[] transformers) throws IOException {
        ZipInputStream inJar = null;
        ZipOutputStream outJar = null;

        try {
            try {
                inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }

            try {
                outJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }

            ZipEntry entry;

            while ((entry = inJar.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    outJar.putNextEntry(entry);
                    continue;
                }

                byte[] data = new byte[4096];
                ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

                int len;

                do {
                    len = inJar.read(data);

                    if (len > 0) {
                        entryBuffer.write(data, 0, len);
                    }
                } while (len != -1);

                byte[] entryData = entryBuffer.toByteArray();

                String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.startsWith(".")) {
                    ClassNode cls = new ClassNode();
                    ClassReader rdr = new ClassReader(entryData);
                    rdr.accept(cls, 0);
                    String name = cls.name.replace('/', '.').replace('\\', '.');

                    for (BLAccessTransformer trans : transformers) {
                        entryData = trans.transform(name, name, entryData);
                    }
                }

                ZipEntry newEntry = new ZipEntry(entryName);
                outJar.putNextEntry(newEntry);
                outJar.write(entryData);
            }
        } finally {
            if (outJar != null) {
                try {
                    outJar.close();
                } catch (IOException ignored) {
                }
            }

            if (inJar != null) {
                try {
                    inJar.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void addToMap(String name, AccessModifier m) {
        List<AccessModifier> mods;

        if (!modifiers.containsKey(name)) {
            mods = new ArrayList<AccessModifier>();
            mods.add(m);
            modifiers.put(name, mods);
        } else {
            mods = modifiers.get(name);
            mods.add(m);
            modifiers.put(name, mods);
        }
    }

    private int countRules() {
        int count = 0;

        for (String name : modifiers.keySet()) {
            count += modifiers.get(name).size();
        }

        return count;
    }

    private class AccessModifier {
        public String name = "";
        public String description = "";
        public int oldAccessMode = 0;
        public int newAccessMode = 0;
        public int targetAccessMode = 0;
        public boolean setFinal = false;
        public boolean changeFinal = false;
        public boolean changeClassVisibility = false;

        private void setAccessMode(String mode) {
            if (mode.startsWith("public")) {
                targetAccessMode = ACC_PUBLIC;
            } else if (mode.startsWith("private")) {
                targetAccessMode = ACC_PRIVATE;
            } else if (mode.startsWith("protected")) {
                targetAccessMode = ACC_PROTECTED;
            } else {
                throw new IllegalArgumentException("Malformed AccessMode: " + mode);
            }

            if (mode.endsWith("-f")) {
                changeFinal = true;
                setFinal = false;
            }

            if (mode.endsWith("+f")) {
                changeFinal = true;
                setFinal = false;
            }
        }
    }
}
