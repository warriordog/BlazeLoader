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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@Beta(stable = false)
public class AccessTransformer implements IClassTransformer
{
	private Map<String, List<AccessModifier>> modifiers = new HashMap<String, List<AccessModifier>>();
	private List<String> fullChangeClasses = new ArrayList<String>();

	public AccessTransformer() throws IOException
	{
		this("bl_at.cfg");
	}

	protected AccessTransformer(String rules) throws IOException
	{
		readRules(rules);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes == null)
			return null;

		if (!modifiers.containsKey(name))
			return bytes;

		ClassNode cNode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cNode, 0);

		if (fullChangeClasses.contains(name))
		{
			AccessModifier m = new AccessModifier();
			m.accessMode = ACC_PUBLIC;
			m.changeClassVisibility = true;
			addToMap(name, m);

			m = new AccessModifier();
			m.accessMode = ACC_PUBLIC;
			m.name = "*";
			addToMap(name, m);

			m = new AccessModifier();
			m.accessMode = ACC_PUBLIC;
			m.name = "*";
			m.description = "<dummy>";
			addToMap(name, m);
		}

		List<AccessModifier> mods = modifiers.get(name);

		for (AccessModifier m : mods)
		{

			if (m.changeClassVisibility)
			{
				cNode.access = getFixedAccess(cNode.access, m);
				continue;
			}

			if (m.description.isEmpty())
			{
				for (FieldNode fNode : cNode.fields)
					if (fNode.name.equals(m.name) || m.name.equals("*"))
					{
						fNode.access = getFixedAccess(fNode.access, m);

						if (!(m.name.equals("*")))
							break;
					}
			}
			else
			{
				for (MethodNode mNode : cNode.methods)
					if (((mNode.name.equals(m.name)) && (mNode.desc.equals(m.description))) || m.name.equals("*"))
					{
						mNode.access = getFixedAccess(mNode.access, m);

						if (!(m.name.equals("*")))
							break;
					}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cNode.accept(writer);

		return writer.toByteArray();
	}

	private int getFixedAccess(int access, AccessModifier m)
	{
		m.oldAccessMode = access;
		int targetAccess = m.accessMode;
		int fixedAccess = (access & ~7);

		switch (access & 7)
		{
			case ACC_PRIVATE :
				fixedAccess |= targetAccess;
				break;
			case 0 :
				fixedAccess |= (targetAccess != ACC_PRIVATE ? targetAccess : 0);
				break;
			case ACC_PROTECTED :
				fixedAccess |= (targetAccess != ACC_PRIVATE && targetAccess != 0 ? targetAccess : ACC_PROTECTED);
				break;
			case ACC_PUBLIC :
				fixedAccess |= (targetAccess != ACC_PRIVATE && targetAccess != 0 && targetAccess != ACC_PROTECTED ? targetAccess : ACC_PUBLIC);
				break;
			default :
				throw new IllegalArgumentException("Invalid AccessMode: " + access);
		}

		if (m.changeFinal)
		{
			if (m.setFinal)
				fixedAccess |= ACC_FINAL;
			else
				fixedAccess |= ~ACC_FINAL;
		}

		m.newAccessMode = fixedAccess;
		return fixedAccess;
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

				if (parts.length > 2)
					throw new IllegalArgumentException("Malformed Line: " + line);

				AccessModifier m = new AccessModifier();
				m.setAccesMode(parts[0]);
				String[] descriptor = parts[1].split("\\.");

				if (descriptor.length > 2)
					throw new IllegalArgumentException("Malformed Line: " + line);

				if (descriptor.length == 1)
					m.changeClassVisibility = true;
				else
				{
					String name = descriptor[1];
					int index = name.indexOf('(');

					if (index > 0)
					{
						m.name = name.substring(0, index);
						m.description = name.substring(index);
					}
					else
						m.name = name;
				}

				if (descriptor[1].equals("*"))
				{
					fullChangeClasses.add(descriptor[0].replace('/', '.'));
					System.out.println("*");
				}

				addToMap(descriptor[0].replace('/', '.'), m);
			}
		}
		finally
		{
			reader.close();
		}

		BlazeLoader.getLogger().logInfo("loaded " + countRules() + " access rules from: " + rules);
	}

	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: AccessTransformer <JarPath> <RuleFile> [RuleFile2] ...");
			System.exit(1);
		}

		boolean hasTransformer = false;
		AccessTransformer[] ats = new AccessTransformer[args.length - 1];

		for (int i = 1; i < ats.length; i++)
		{
			try
			{
				ats[i - 1] = new AccessTransformer(args[i]);
				hasTransformer = true;
			}
			catch (IOException e)
			{
				System.out.println("Could not read Transformer Rules: " + args[i]);
				e.printStackTrace();
			}
		}

		if (!hasTransformer)
		{
			System.out.println("Could not find a valid transformer to perform");
			System.exit(1);
		}

		File original = new File(args[0]);
		File temp = new File(args[0] + ".ATBackup");

		if (!original.exists() && !temp.exists())
		{
			System.out.println("Could not find target jar: " + original);
			System.exit(1);
		}

		if (!original.renameTo(temp))
		{
			System.out.println("Could not rename file: " + original + "->" + temp);
			System.exit(1);
		}

		try
		{
			processJar(temp, original, ats);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		if (!temp.delete())
			System.out.println("Could not delete temp file: " + temp);
	}

	private static void processJar(File input, File output, AccessTransformer[] transformers) throws IOException
	{
		ZipInputStream in = null;
		ZipOutputStream out = null;

		try
		{
			try
			{
				in = new ZipInputStream(new BufferedInputStream(new FileInputStream(input)));
			}
			catch (IOException e)
			{
				throw new FileNotFoundException("Could not open input file: " + e.getMessage());
			}

			try
			{
				out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(output)));
			}
			catch (FileNotFoundException e)
			{
				throw new FileNotFoundException("could not open output file: " + e.getMessage());
			}

			ZipEntry zip;

			while ((zip = in.getNextEntry()) != null)
			{
				if (zip.isDirectory())
				{
					out.putNextEntry(zip);
					continue;
				}

				byte[] data = new byte[4096];
				ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
				int length;

				do
				{
					length = in.read(data);

					if (length > 0)
						entryBuffer.write(data, 0, length);
				}
				while (length != -1);

				byte[] entryData = entryBuffer.toByteArray();
				String entryName = zip.getName();

				if (entryName.endsWith(".class") && !entryName.startsWith("."))
				{
					ClassNode cNode = new ClassNode();
					ClassReader reader = new ClassReader(entryData);
					reader.accept(cNode, 0);
					String name = cNode.name.replace('/', '.').replace('\\', '.');

					for (AccessTransformer at : transformers)
						entryData = at.transform(name, name, entryData);
				}

				ZipEntry newZip = new ZipEntry(entryName);
				out.putNextEntry(newZip);
				out.write(entryData);
			}
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{

				}
			}

			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{

				}
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
		public int accessMode = 0;
		public boolean setFinal = false;
		public boolean changeFinal = false;
		public boolean changeClassVisibility = false;

		private void setAccesMode(String mode)
		{
			if (mode.startsWith("public"))
				accessMode = ACC_PUBLIC;
			else if (mode.startsWith("private"))
				accessMode = ACC_PRIVATE;
			else if (mode.startsWith("protected"))
				accessMode = ACC_PROTECTED;
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