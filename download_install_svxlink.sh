#!/bin/bash
#https://github.com/mmiller7/svxlink-rpi-setup

#this is the step that will be performed on next execution
#for clean start, this should be updateRasbian
currentStep=updateRaspbian

if [ "$(whoami)" != "root" ]; then
	echo "ERROR: This script be run as root!"
	exit 1
fi

#get path to script for sed later
scriptPath="`pwd`/`basename $0`"

case $currentStep in
  updateRaspbian)
    echo 'Phase 1 of 3: Updating Raspbian'
    sleep 1
    apt-get update &&
    apt-get -y upgrade &&
    sed -i 's/currentStep=updateRaspbian/currentStep=updatePi/' $scriptPath
    echo 'You *MUST* reboot now.'
    echo 'Then re-run script to continue with setup phase 2.'
    #reboot
    ;;

  updatePi)
    echo 'Phase 2 of 3: Updating Raspberry Pi'
    sleep 1
    apt-get -y install git-core ca-certificates rpi-update &&
    echo y | rpi-update &&
    sed -i 's/currentStep=updatePi/currentStep=buildAndInstall/' $scriptPath
    echo 'You *MUST* reboot now.'
    echo 'Then re-run script to continue with setup phase 3.'
    #reboot
    ;;

  buildAndInstall)
    echo 'Phase 3 of 3: Installing and Configuring dependencies'
    sleep 1
    apt-get -y install subversion libsigc++-2.0-dev g++ make libsigc++-1.2-dev libgsm1-dev libpopt-dev tcl8.5-dev libgcrypt-dev libspeex-dev libasound2-dev alsa-utils cmake &&
    useradd -r -s /sbin/nologin -M svxlink &&
    adduser svxlink audio &&
    adduser svxlink gpio &&
    
    echo 'Downloading svxlink from GitHub' &&
    sleep 1 &&
    mkdir svxlink_github &&
    cd svxlink_github &&
    wget 'https://github.com/sm0svx/svxlink/archive/master.tar.gz' &&
    tar xf master.tar.gz &&
    
    echo 'Building svxlink' &&
    sleep 1 &&
    cd svxlink-master/src &&
    mkdir build &&
    cd build &&
    # by this point we should be relative path svxlink-master/src/build
    cmake -DUSE_QT=OFF -DCMAKE_INSTALL_PREFIX=/usr -DSYSCONF_INSTALL_DIR=/etc -DLOCAL_STATE_DIR=/var .. &&
    make &&
    make install &&
    ldconfig &&
    cd ../.. &&
    # by this point we should be relative path svxlin-master
    wget 'https://github.com/sm0svx/svxlink-sounds-en_US-heather/releases/download/14.08/svxlink-sounds-en_US-heather-16k-13.12.tar.bz2' &&
    tar -xf svxlink-sounds-en_US-heather-16k-13.12.tar.bz2 &&
    mv en_US-heather-16k /usr/share/svxlink/sounds/ &&
    ln -s /usr/share/svxlink/sounds/en_US-heather-16k /usr/share/svxlink/sounds/en_US &&

    echo 'Copying init-script' &&
    cp distributions/debian/etc/init.d/svxlink /etc/init.d/svxlink &&
    chmod 755 /etc/init.d/svxlink &&
    update-rc.d svxlink defaults &&
    echo 'Done!' &&
    echo '' &&
    echo 'Next steps:' &&
    echo '* Configure /etc/svxlink/svxlink.conf' &&
    echo '* Create /etc/defaults/svxlink (see svxlink.conf for variables for GPIO startup)' &&
    echo '* Configure /etc/svxlink/svxlink.d/ModuleEchoLink.conf' &&

    sed -i 's/currentStep=buildAndInstall/currentStep=alreadyDone/' $scriptPath
    ;;

  alreadyDone)
    echo 'Already done!  To start over, modify script.'
    ;;

  *)
    echo ERROR
    ;;
esac
