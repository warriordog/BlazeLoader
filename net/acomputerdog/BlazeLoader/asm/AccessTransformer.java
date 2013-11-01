package net.acomputerdog.BlazeLoader.asm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@Beta(stable = false)
public class AccessTransformer implements IClassTransformer
{
	private Map<String, AccessModifier> modifiers = new HashMap<String, AccessModifier>();

	protected AccessTransformer() throws IOException
	{
		this("BL_AT.cfg");
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

		AccessModifier m = modifiers.get(name);

		if (m.changeClassVisibility)
		{
			cNode.access = getFixedAccess(cNode.access, m);
		}

		if (m.description.isEmpty())
		{
			for (FieldNode fNode : cNode.fields)
				if (fNode.name.equals(m.name))
					fNode.access = getFixedAccess(fNode.access, m);
		}
		else
		{
			for (MethodNode mNode : cNode.methods)
				if ((mNode.name.equals(m.name)) && (mNode.desc.equals(m.description)))
					mNode.access = getFixedAccess(mNode.access, m);
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
			case Opcodes.ACC_PRIVATE :
				fixedAccess |= targetAccess;
				break;
			case 0 :
				fixedAccess |= (targetAccess != Opcodes.ACC_PRIVATE ? targetAccess : 0);
				break;
			case Opcodes.ACC_PROTECTED :
				fixedAccess |= (targetAccess != Opcodes.ACC_PRIVATE && targetAccess != 0 ? targetAccess : Opcodes.ACC_PROTECTED);
				break;
			case Opcodes.ACC_PUBLIC :
				fixedAccess |= (targetAccess != Opcodes.ACC_PRIVATE && targetAccess != 0 && targetAccess != Opcodes.ACC_PROTECTED ? targetAccess : Opcodes.ACC_PUBLIC);
				break;
			default :
				throw new IllegalArgumentException("Invalid AccessMode: " + access);
		}

		if (m.changeFinal)
		{
			if (m.setFinal)
				fixedAccess |= Opcodes.ACC_FINAL;
			else
				fixedAccess |= ~Opcodes.ACC_FINAL;
		}

		m.newAccessMode = fixedAccess;
		return fixedAccess;
	}

	@SuppressWarnings("resource")
	private void readRules(String rules) throws IOException
	{
		File file = new File(rules);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		while (reader.readLine() != null)
		{
			String line = reader.readLine();

			if (line.startsWith("#") || line.isEmpty() || line == null)
				continue;

			String[] sections = line.split("#");
			String[] parts = sections[0].split(" ");

			if (parts.length > 2)
				throw new IllegalArgumentException("Malformed Line: " + line);

			AccessModifier m = new AccessModifier();
			m.setAccesMode(parts[0]);
			String[] descriptor = parts[1].split(".");

			if (descriptor.length == 0)
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

			modifiers.put(descriptor[0].replace('/', '.'), m);
		}

		reader.close();
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

	private void assertMatch(Matcher m, String s)
	{
		m.reset(s);
		illegalAssert(m.matches(), "Malformed Line: " + s);
	}

	private void illegalAssert(boolean b, String error)
	{
		if (!b)
			throw new IllegalArgumentException(error);
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
				accessMode = Opcodes.ACC_PUBLIC;
			else if (mode.startsWith("private"))
				accessMode = Opcodes.ACC_PRIVATE;
			else if (mode.startsWith("protected"))
				accessMode = Opcodes.ACC_PROTECTED;
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