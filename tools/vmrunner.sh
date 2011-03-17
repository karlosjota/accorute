#!/bin/bash
[ "$#" -eq "1" ] || exit 1;
function restart
{
    echo -n "[`date +%T`] "
    echo -n "Restarting the VM: "
    VBoxManage controlvm $1 poweroff &>>vmrunner.log
    echo -n " ...stopped... "
    VBoxManage snapshot $1 restorecurrent &>>vmrunner.log
    echo -n " ...restored... "
    VBoxManage startvm $1 --type headless &>>vmrunner.log
    echo " ...started!"
}
function exit_gracefully
{
    echo -n "[`date +%T`] "
    echo -n "Closing the VM: "
    VBoxManage controlvm $1 poweroff &>>vmrunner.log
    echo -n " ...stopped... "
    VBoxManage snapshot $1 restorecurrent &>>vmrunner.log
    echo " ...restored!"
    exit 0
}
trap "restart $1" INT
trap "exit_gracefully $1" QUIT
VBoxManage startvm $1 --type headless &>>vmrunner.log
echo " machine started!"
while true; do
    sleep 60
done
