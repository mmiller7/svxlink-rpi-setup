#!/bin/bash
# blink_led_status.sh
# Created by Matthew Miller
# 08AUG2015
# Polls status and blinks codes.

# The button is to be connected between GROUND and the pin-number
# specified by the command-line parameter.
# I recommend putting this script in /root/blink_led_status.sh

# Add this line in /etc/rc.local:
# /root/blink_led_status.sh PIN_NUM &
# ex.: /root/blink_led_status.sh 18 &
# where "18" is replaced with your desired GPIO pin number.

# Blink this GPIO LED to send status
# Uses 1st parameter by default
BLINK_PIN=$1

# Delay seconds between iterations
BLINK_DELAY_OFFTIME=1

# seconds on for "long blink" (fail)
BLINK_DELAY_LONG=0.5

# Seconds on for "short blink" (pass)
BLINK_DELAY_SHORT=0.15



# Setup
if [ "$BLINK_PIN" == "" ]; then
  echo "blink_led_status.sh: ERROR - GPIO pin not specified!"
  exit 1
fi
echo ${BLINK_PIN} > /sys/class/gpio/export
echo out > /sys/class/gpio/gpio${BLINK_PIN}/direction
echo "Flashing run indicator on GPIO ${BLINK_PIN}"

blinkOnce()
{
  BLINK_DELAY=$1
  echo 1 > /sys/class/gpio/gpio${BLINK_PIN}/value
  sleep $BLINK_DELAY
  echo 0 > /sys/class/gpio/gpio${BLINK_PIN}/value
  sleep $BLINK_DELAY
}

# Performs a test and blinks per exit result
doTest()
{
  cmd=$1
  $cmd &> /dev/null && blinkOnce $BLINK_DELAY_SHORT || blinkOnce $BLINK_DELAY_LONG
}

# Test loop
while true
do
  # Internet status blink
  #ping -c 1 -s 0 google.com > /dev/null && blinkOnce $BLINK_DELAY_SHORT || blinkOnce $BLINK_DELAY_LONG
  doTest "ping -c 1 -s 0 google.com"

  # Svxlink status blink
  #pgrep svxlink > /dev/null && blinkOnce $BLINK_DELAY_SHORT || blinkOnce $BLINK_DELAY_LONG
  doTest "pgrep svxlink"

  # Wait to restart loop
  sleep $BLINK_DELAY_OFFTIME
done
     
