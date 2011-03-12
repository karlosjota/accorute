#!/bin/bash
[ "$#" -eq "1" ] || exit 1;
function restart
{
    echo -n "Restarting the VM: "
    VBoxManage controlvm $1 poweroff &>/dev/null
    echo -n " ...stopped... "
    VBoxManage snapshot $1 restorecurrent &>/dev/null
    echo -n " ...restored... "
    VBoxManage startvm $1 --type headless &>/dev/null
    echo " ...started!"
}
function exit_gracefully
{
    echo -n "Closing the VM: "
    VBoxManage controlvm $1 poweroff &>/dev/null
    echo -n " ...stopped... "
    VBoxManage snapshot $1 restorecurrent &>/dev/null
    echo " ...restored!"
    exit 0
}
trap "restart $1" INT
trap "exit_gracefully $1" QUIT
while true; do
    sleep 60
done
