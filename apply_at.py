import sys
import os
import subprocess
import shlex

mc_ver = '1.7.2'
mcp_dir = '..'
jars_dir = os.path.join(mcp_dir, 'jars')
libraries_dir = os.path.join(jars_dir, 'libraries')
versions_dir = os.path.join(jars_dir, 'versions', '%s' % mc_ver)

libs = '../jars/libraries/net/minecraft/launchwrapper/1.9/launchwrapper-1.9.jar:../jars/libraries/org/ow2/asm/asm-debug-all/4.1/asm-debug-all-4.1.jar'.replace('/', os.sep)
at = 'net/acomputerdog/BlazeLoader/asm/AccessTransformer'.replace('/', os.sep)
jar_target = '../jars/versions/"{mc_version}"/"{mc_version}".jar'.format(mc_version = mc_ver)

compile_cmd = 'javac -cp ' + libs + ' -d bin ' + at + '.java'
run_cmd = 'java -cp bin:' + libs + ' ' + at.replace(os.sep, '.') + ' ' + jar_target + ' bl_at.cfg'

def check_install():
	print '> Checking installation'
	
	if not os.path.isfile(os.path.join(versions_dir, '%s.jar' % mc_ver)):
		print '!!!! Minecraft %s jar not found!' % mc_ver
		return False
	
	if not os.path.isfile(os.path.join(versions_dir, '%s.json' % mc_ver)):
		print '!!!! Minecraft %s json not found!' % mc_ver
		return False
	
	if not os.path.isfile(os.path.join(libraries_dir, 'org', 'ow2', 'asm', 'asm-debug-all', '4.1', 'asm-debug-all-4.1.jar')):
		print '!!!! asm-debug-all-4.1?jar not found! get it at http://mvnrepository.com/artifact/org.ow2.asm/asm-debug-all/4.1'
		return False
	
	if not os.path.isfile(os.path.join(libraries_dir, 'net', 'minecraft', 'launchwrapper', '1.9', 'launchwrapper-1.9.jar')):
		print '!!!! launchwrapper-1.9 not found!'
		return False
	
	if not os.path.exists('bin') or not os.path.isdir('bin'):
		os.makedirs('bin')
	
	print '> Found all reqquired files'
	return True

def compile_at():
	print '> Compiling AccessTransformer'
	
	shlex.split(compile_cmd)
	process = subprocess.Popen(compile_cmd, shell=True, stdout=subprocess.PIPE)
	
	while True:
		nextline = process.stdout.readline()
		
		if nextline == '' and process.poll() != None:
			break
		
		print nextline
	
	print '> Compiled AccessTransformer'

def run_at():
	print '> Running AccessTransformer'
	
	shlex.split(run_cmd)
	process = subprocess.Popen(run_cmd, shell=True, stdout=subprocess.PIPE)
	
	while True:
		nextline = process.stdout.readline()
		
		if nextline == '' and process.poll() != None:
			break
		
		print nextline
	
	print '> AccessTransformer was successfully applied'

if not check_install():
	sys.exit(1)

compile_at()
run_at()