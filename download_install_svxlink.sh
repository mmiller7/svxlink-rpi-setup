#!/bin/bash

#this is the step that will be performed on next execution
#for clean start, this should be updateRasbian
currentStep=updateRaspbian

case $currentStep in
  updateRaspbian)
    echo 'Phase 1 of 3: Updating Raspbian'
    sleep 1
    apt-get update &&
    apt-get -y upgrade &&
    sed -i 's/currentStep=updateRaspbian/currentStep=updatePi/' $0
    reboot
    ;;

  updatePi)
    echo 'Phase 2 of 3: Updating Raspberry Pi'
    sleep 1
    apt-get -y install git-core ca-certificates rpi-update &&
    echo y | rpi-update &&
    sed -i 's/currentStep=updatePi/currentStep=buildAndInstall/' $0
    reboot
    ;;

  buildAndInstall)
    echo 'Phase 3 of 3: Installing and Configuring dependencies'
    sleep 1
    apt-get -y install subversion libsigc++-2.0-dev g++ make libsigc++-1.2-dev libgsm1-dev libpopt-dev tcl8.5-dev libgcrypt-dev libspeex-dev libasound2-dev alsa-utils cmake &&
    useradd -r -s /sbin/nologin -M svxlink &&
    adduser svxlink audio &&
    
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
    cmake -DUSE_QT=OFF -DCMAKE_INSTALL_PREFIX=/usr -DSYSCONF_INSTALL_DIR=/etc -DLOCAL_STATE_DIR=/var .. &&
    make &&
    make install &&
    ldconfig &&
    echo 'Done!'
    sed -i 's/currentStep=buildAndInstall/currentStep=alreadyDone/' $0
    ;;

  alreadyDone)
    echo 'Already done!  To start over, modify script.'
    ;;

  *)
    echo ERROR
    ;;
esac




