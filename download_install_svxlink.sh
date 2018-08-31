#!/bin/bash
#https://github.com/mmiller7/svxlink-rpi-setup

set -e

if [ "$(whoami)" != "root" ]; then
	echo "ERROR: This script be run as root!"
	exit 1
fi

#get path to script for sed later
scriptPath="`pwd`/`basename $0`"

    echo 'Installing and Configuring dependencies'
    sleep 1
    apt-get -y install subversion libsigc++-2.0-dev g++ make libsigc++-1.2-dev libgsm1-dev libpopt-dev tcl8.5-dev libgcrypt-dev libspeex-dev libasound2-dev alsa-utils cmake default-jdk default-jre
    useradd -r -s /sbin/nologin -M svxlink
    adduser svxlink audio
    adduser svxlink gpio
    
    echo 'Compiling config tools'
    cd SvxlinkLocationUpdater
    javac configFileEditor/*.java
    cd ..
    
    echo 'Downloading svxlink from GitHub'
    sleep 1
    mkdir svxlink_github
    cd svxlink_github
    wget 'https://github.com/sm0svx/svxlink/archive/master.tar.gz'
    tar xf master.tar.gz
    
    echo 'Building svxlink'
    sleep 1
    cd svxlink-master/src
    mkdir build
    cd build
    # by this point we should be relative path svxlink-master/src/build
    cmake -DUSE_QT=OFF -DCMAKE_INSTALL_PREFIX=/usr -DSYSCONF_INSTALL_DIR=/etc -DLOCAL_STATE_DIR=/var ..
    make
    version=`date '+%Y%m%d'`
    echo "Attempting to build package svxlink version $version"
    checkinstall -D --pkgname svxlink --pkggroup svxlink --provides svxlink --pkgversion $version -y && (
	echo "Installing newly built package"
	dpkg -i svxlink_*.deb
    ) || (
	echo "Failed to build package for install, falling back to basic make-install"
	make install
    )
    ldconfig
    cd ../..
    # by this point we should be relative path svxlin-master
    
    echo 'Downloading and extracting sound files'
    wget 'https://github.com/sm0svx/svxlink-sounds-en_US-heather/releases/download/18.03.1/svxlink-sounds-en_US-heather-16k-18.03.1.tar.bz2'
    tar -xf svxlink-sounds-en_US-heather-16k-13.12.tar.bz2
    mv en_US-heather-16k /usr/share/svxlink/sounds/
    ln -s /usr/share/svxlink/sounds/en_US-heather-16k /usr/share/svxlink/sounds/en_US

    echo 'Copying init-script'
    cp distributions/debian/etc/init.d/svxlink /etc/init.d/svxlink
    chmod 755 /etc/init.d/svxlink
    update-rc.d svxlink defaults
    echo 'Done!'
    echo ''
    echo 'Next steps:'
    echo '* Configure /etc/svxlink/svxlink.conf'
    echo '* Create /etc/defaults/svxlink (see svxlink.conf for variables for GPIO startup)'
    echo '* Configure /etc/svxlink/svxlink.d/ModuleEchoLink.conf'

    sed -i 's/currentStep=alreadyDone/currentStep=alreadyDone/' $scriptPath

