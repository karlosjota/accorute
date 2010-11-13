#!/bin/bash
echo -n Retrieving build number from SVN...
REVISION=`svn info | grep Revision:`
R_NUMBER=${REVISION#Revision:}
R_NUMBER=${R_NUMBER## }
echo $REVISION 

if test -d Release
then
	echo Performing cleanup from previos interrupted build
	rm -rf Release
fi

mkdir Release


BASENAME=webapp-src-1.0.$R_NUMBER
SVN_ROOT=../../..
OUT_DIR=Release/$BASENAME 
DEP_DIR=$OUT_DIR/deployment

mkdir $OUT_DIR
mkdir $DEP_DIR


echo Copying sources 
cp -r html $OUT_DIR
cp -r resources $OUT_DIR
cp index* $OUT_DIR
cp .htaccess $OUT_DIR
cp install.sh $OUT_DIR
cp pkgtest.py $OUT_DIR
cp README.txt $OUT_DIR

echo Copying libraries
cp $SVN_ROOT/deployment/MySQL-python-1.2.2.tar.gz $DEP_DIR
cp $SVN_ROOT/deployment/setuptools-0.6c6-py2.4.egg $DEP_DIR

cd Release
find . -name '.svn' -exec rm -rf '{}' \; 1>/dev/null 2>/dev/null

echo Creating $BASENAME.tar.gz
tar -cf - $BASENAME | gzip > ../$BASENAME.tar.gz
cd ..
rm -rf Release

echo WEB APPLICATION DISTRIBUTIVE BUILD SUCCESSFULL
