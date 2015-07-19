#!/bin/bash

echo 'Manual directions found at https://github.com/sm0svx/svxlink/wiki/LinkWithLocalMicAndSpeaker'
echo '  Note: this version has minor config value tweaks to my taste'

if [ "$(whoami)" != "root" ]; then
        echo "ERROR: This script be run as root!"
        exit 1
fi



echo 'Modifying /etc/svxlink/svxlink.conf:'
echo '  Adding MicSpkrLogic to [GLOBAL] LOGICS='
sed -i 's/LOGICS=/LOGICS=MicSpkrLogic,/' /etc/svxlink/svxlink.conf



echo '  Adding [MicSpkrLogic] config'
echo '

[MicSpkrLogic]
TYPE=Simplex
RX=RxMike
TX=TxSpkr
CALLSIGN=SM0SVX
EVENT_HANDLER=/usr/share/svxlink/events.tcl' >> /etc/svxlink/svxlink.conf



echo '  Adding link [MicSpkrLink] to connect SimplexLogic and MicSpkrLogic'
echo '

[MicSpkrLink]
CONNECT_LOGICS=SimplexLogic,MicSpkrLogic
DEFAULT_ACTIVE=1' >> /etc/svxlink/svxlink.conf



echo '  Defining [MicRx] for MicSpkrLogic'
echo '

[RxMike]
TYPE=Local
AUDIO_DEV=alsa:plughw:0
AUDIO_CHANNEL=1
SQL_DET=SERIAL
#SQL_DET=VOX
SERIAL_PORT=/dev/ttyS0
SERIAL_PIN=DCD:SET
PEAK_METER=1
SQL_START_DELAY=0
SQL_DELAY=0
SQL_HANGTIME=2000
#VOX_FILTER_DEPTH=20
VOX_FILTER_DEPTH=10
VOX_THRESH=1000' >> /etc/svxlink/svxlink.conf



echo '  Defining [TxSpkr] for MicSpkrLogic'
echo '

[TxSpkr]
TYPE=Local
#AUDIO_DEV=/dev/dsp
AUDIO_DEV=alsa:plughw:0
AUDIO_CHANNEL=1
PTT_TYPE=NONE
PTT_PORT=NONE' >> /etc/svxlink/svxlink.conf



echo 'Creating logic core TCL file...'
cp /usr/share/svxlink/events.d/SimplexLogic.tcl /usr/share/svxlink/events.d/MicSpkrLogic.tcl
sed -i 's/SimplexLogic/MicSpkrLogic/' /usr/share/svxlink/events.d/MicSpkrLogic.tcl

echo 'Done.'
echo 'Please review /etc/svxlink/svxlink.conf and adjust parameters to taste.'
