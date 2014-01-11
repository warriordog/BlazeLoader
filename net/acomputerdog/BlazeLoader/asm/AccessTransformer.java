package net.acomputerdog.BlazeLoader.asm;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class AccessTransformer implements IClassTransformer
{
	private Map<String, List<AccessModifier>> modifiers = new HashMap<String, List<AccessModifier>>();

	protected AccessTransformer(String atFile) throws IOException
	{

	}

	public AccessTransformer() throws IOException
	{
		this("bl_at.cfg");
	}

	private void readRules(String rules) throws IOException
	{
		File file = new File(rules);
		URL rulesFile;

		if (file.exists())
			rulesFile = file.toURI().toURL();
		else
			rulesFile = AccessTransformer.class.getClassLoader().getResource(rules);

		BufferedReader reader = new BufferedReader(new InputStreamReader(rulesFile.openStream()));

		try
		{
			String line;

			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);

				if (line.startsWith("#") || line.isEmpty() || line == null)
					continue;

				String[] sections = line.split("#");
				String[] parts = sections[0].split(" ");

				if (parts.length > 3)
					throw new IllegalArgumentException("Malformed Line: " + line);

				AccessModifier m = new AccessModifier();
				m.setAccesMode(parts[0]);

				if (parts.length == 2)
					m.changeClassVisibility = true;
				else
				{
					String nameReference = parts[2];
					int parenIdx = nameReference.indexOf('(');

					if (parenIdx > 0)
					{
						m.description = nameReference.substring(parenIdx);
						m.name = nameReference.substring(0, parenIdx);
					}
					else
						m.name = nameReference;
				}

				String className = parts[1].replace('/', '.');
				addToMap(className, m);
			}
		}
		finally
		{
			reader.close();
		}

		System.out.println(String.format("Loaded %d access rules from %s", countRules(), rules));
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes == null)
			return null;

		if (!modifiers.containsKey(transformedName))
			return bytes;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Collection<AccessModifier> mods = modifiers.get(transformedName);

		for (AccessModifier m : mods)
		{
			if (m.changeClassVisibility)
			{
				classNode.access = getFixedAccess(classNode.access, m);
				continue;
			}

			if (m.description.isEmpty())
			{
				for (FieldNode n : classNode.fields)
				{
					if (n.name.equals(m.name))
						n.access = getFixedAccess(n.access, m);
				}
			}
			else
			{
				for (MethodNode n : classNode.methods)
				{
					if ((n.name.equals(m.name) && n.desc.equals(m.description)))
						n.access = getFixedAccess(n.access, m);
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private int getFixedAccess(int access, AccessModifier target)
	{
		target.oldAccessMode = access;
		int t = target.targetAccessMode;
		int ret = (access & ~7);

		switch (access & 7)
		{
			case ACC_PRIVATE :
				ret |= t;
				break;
			case 0 :
				ret |= (t != ACC_PRIVATE ? t : 0);
				break;
			case ACC_PROTECTED :
				ret |= (t != ACC_PRIVATE && t != 0 ? t : ACC_PROTECTED);
				break;
			case ACC_PUBLIC :
				ret |= (t != ACC_PRIVATE && t != 0 && t != ACC_PROTECTED ? t : ACC_PUBLIC);
				break;
			default :
				throw new RuntimeException("That shouldn't of happend");
		}

		if (target.changeFinal)
		{
			if (target.setFinal)
				ret |= ACC_FINAL;
			else
				ret &= ~ACC_FINAL;
		}
		target.newAccessMode = ret;
		return ret;
	}

	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: AccessTransformer <JarPath> <RulesFile> [RulesFile2]...");
			System.exit(1);
		}

		boolean hasTransformer = false;
		AccessTransformer[] trans = new AccessTransformer[args.length - 1];

		for (int x = 1; x < args.length; x++)
		{
			try
			{
				trans[x - 1] = new AccessTransformer(args[x]);
				hasTransformer = true;
			}
			catch (IOException e)
			{
				System.out.println("Could not read Transformer Map: " + args[x]);
				e.printStackTrace();
			}
		}

		if (!hasTransformer)
		{
			System.out.println("Culd not find a valid transformer to perform");
			System.exit(1);
		}

		File orig = new File(args[0]);
		File temp = new File(args[0] + ".ATBackup");

		if (!orig.exists() && !temp.exists())
		{
			System.out.println("Could not find target jar: " + orig);
			System.exit(1);
		}

		if (!orig.renameTo(temp))
		{
			System.out.println("Could not rename file: " + orig + " -> " + temp);
			System.exit(1);
		}

		try
		{
			processJar(temp, orig, trans);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		if (!temp.delete())
			System.out.println("Could not delete temp file: " + temp);
	}

	private static void processJar(File inFile, File outFile, AccessTransformer[] transformers) throws IOException
	{
		ZipInputStream inJar = null;
		ZipOutputStream outJar = null;

		try
		{
			try
			{
				inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
			}
			catch (FileNotFoundException e)
			{
				throw new FileNotFoundException("Could not open input file: " + e.getMessage());
			}

			try
			{
				outJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
			}
			catch (FileNotFoundException e)
			{
				throw new FileNotFoundException("Could not open output file: " + e.getMessage());
			}

			ZipEntry entry;

			while ((entry = inJar.getNextEntry()) != null)
			{
				if (entry.isDirectory())
				{
					outJar.putNextEntry(entry);
					continue;
				}

				byte[] data = new byte[4096];
				ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

				int len;

				do
				{
					len = inJar.read(data);

					if (len > 0)
						entryBuffer.write(data, 0, len);
				}
				while (len != -1);

				byte[] entryData = entryBuffer.toByteArray();

				String entryName = entry.getName();

				if (entryName.endsWith(".class") && !entryName.startsWith("."))
				{
					ClassNode cls = new ClassNode();
					ClassReader rdr = new ClassReader(entryData);
					rdr.accept(cls, 0);
					String name = cls.name.replace('/', '.').replace('\\', '.');

					for (AccessTransformer trans : transformers)
						entryData = trans.transform(name, name, entryData);
				}

				ZipEntry newEntry = new ZipEntry(entryName);
				outJar.putNextEntry(newEntry);
				outJar.write(entryData);
			}
		}
		finally
		{
			if (outJar != null)
			{
				try
				{
					outJar.close();
				}
				catch (IOException e)
				{}
			}

			if (inJar != null)
			{
				try
				{
					inJar.close();
				}
				catch (IOException e)
				{}
			}
		}
	}

	private void addToMap(String name, AccessModifier m)
	{
		List<AccessModifier> mods;

		if (!modifiers.containsKey(name))
		{
			mods = new ArrayList<AccessModifier>();
			mods.add(m);
			modifiers.put(name, mods);
		}
		else
		{
			mods = modifiers.get(name);
			mods.add(m);
			modifiers.put(name, mods);
		}
	}

	private int countRules()
	{
		int count = 0;

		for (String name : modifiers.keySet())
			count += modifiers.get(name).size();

		return count;
	}

	private class AccessModifier
	{
		public String name = "";
		public String description = "";
		public int oldAccessMode = 0;
		public int newAccessMode = 0;
		public int targetAccessMode = 0;
		public boolean setFinal = false;
		public boolean changeFinal = false;
		public boolean changeClassVisibility = false;

		private void setAccesMode(String mode)
		{
			if (mode.startsWith("public"))
				targetAccessMode = ACC_PUBLIC;
			else if (mode.startsWith("private"))
				targetAccessMode = ACC_PRIVATE;
			else if (mode.startsWith("protected"))
				targetAccessMode = ACC_PROTECTED;
			else
				throw new IllegalArgumentException("Malformed AccessMode: " + mode);

			if (mode.endsWith("-f"))
			{
				changeFinal = true;
				setFinal = false;
			}

			if (mode.endsWith("+f"))
			{
				changeFinal = true;
				setFinal = false;
			}
		}
	}
}