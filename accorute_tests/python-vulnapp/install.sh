#!/bin/bash

if test $# -lt 1
then
        echo "Usage: $0 <Target directory to install web application in >"
        exit 1
fi

WEBAPP_DIR=${1%%/}

if ! test -d $WEBAPP_DIR
then
	echo "Specified directory $WEBAPP_DIR does not exist"
	exit 2
fi

echo Installing libraries...
echo Installing libraries... > install.log

python pkgtest.py
TEST_CODE=$?

if test $TEST_CODE != 0 
then
	cd deployment

	if test $TEST_CODE == 1
	then
		/bin/bash setuptools-0.6c6-py2.4.egg >> install.log
	fi
	
	tar -zxf MySQL-python-1.2.2.tar.gz
	cd MySQL-python-1.2.2
	python setup.py build >> install.log
	python setup.py install >> install.log
	cd ..
	cd ..
fi
echo Done!
echo Done! >> install.log


echo Copying files...
echo Copying files... >> install.log
cp .htaccess $WEBAPP_DIR
cp index* $WEBAPP_DIR
cp -r html $WEBAPP_DIR
cp -r resources $WEBAPP_DIR
echo Done!
echo Done! >> install.log

echo Performing configuration...
echo Performing configuration... >> install.log
mkdir $WEBAPP_DIR/log
mkdir $WEBAPP_DIR/home

sed "s|%INSTALL_DIR%|$WEBAPP_DIR|g" $WEBAPP_DIR/.htaccess > 1.tmp
mv 1.tmp $WEBAPP_DIR/.htaccess

sed "s|%INSTALL_DIR%|$WEBAPP_DIR|g" $WEBAPP_DIR/html/settings.py > 1.tmp
mv 1.tmp $WEBAPP_DIR/html/settings.py
echo Done!
echo Done! >> install.log

echo WEB APPLICATION INSTALLED SUCCESSFULLY
echo CONFIGURE DATABASE AND WEB APPLICATION WOULD BE OPERATIONAL

echo WEB APPLICATION INSTALLED SUCCESSFULLY >> install.log
echo CONFIGURE DATABASE AND WEB APPLICATION WOULD BE OPERATIONAL >> install.log
