############################################################################
I System reuirements
############################################################################
1. python version 2.3 and later
2. mysql server version 3 and later
3. zlib1g-dev package
4. libmysqlclient15-dev package
5. gcc compiler
6. LOCAL_HOME environment variable should be set to point to directory for local installation. Unless installation system wide and is run by root.

############################################################################
II How to Install web application
############################################################################
1. run ./install.sh <target dir>
Note: <target dir> should already exist. Otherwise, first issue `mkdir <target dir>`

If script succeedes the following message will be displayed:
	WEB APPLICATION INSTALLED SUCCESSFULLY
	CONFIGURE DATABASE AND WEB APPLICATION WOULD BE OPERATIONAL
otherwise sent install.log file to technical support

2. configure database: 
	cd resources
	mysql -u root -p < createdb.sql 	#run database building script from Mysql server root user 

NOTE: if mysql root user password is not set then -p option should be omitted

NOTE: database name and username of web application user can be changed in createdb.sql. If so, corresponding settings should be applied to html/settings.py file

NOTE: run `mysql -u root -p < createdb-full.sql` to create database with contents. Initial users are petand:qwerty and ddk:123456


Enjoy!

