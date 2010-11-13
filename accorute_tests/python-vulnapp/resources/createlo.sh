#!/bin/bash
THIS_USER=`whoami`

if test $THIS_USER != 'root' 
then
	echo "This script should be run with root priveleges. Quitting..."
	exit 1
fi

if test -d log || test -f file.img 
then 
	echo "File file.img and log directory already exists. Quitting..."
	exit 1
else
	dd if=/dev/zero of=file.img bs=1k count=10000
	losetup /dev/loop/0 file.img
	mke2fs -c /dev/loop/0 10000
	mkdir log
	mount -t ext2 /dev/loop/0 log
	echo "Device file created and mounted successfully."
fi
