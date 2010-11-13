#returns 0 if MySQLdb is installed
#returns 1 if MySQLdb and setuptools are not installed
#returns 2 if MySQLdb is not installed and setuptools are installed
import sys

try:
	import MySQLdb
except ImportError:
	try:
		from setuptools import setup
	except ImportError:
		sys.exit(1)

	sys.exit(2)


sys.exit(0)
