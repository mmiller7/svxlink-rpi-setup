#!/usr/bin/python
# buttonAction.py
# Created by Matthew Miller
# 29APR2016

# This script will wait for a button to be pressed pulling the specified
# pin to "ground" and then it will execute the the specified command.
# The button is to be connected between GROUND and the pin-number specified
# in the command line parameter.
# Usage:   python buttonAction.py <pin_num> <command> [debounce_ms]
# Example: python buttonAction.py  27  "shutdown -h now"
# Example: python buttonAction.py  27  "shutdown -h now" 2000
# Note, you probably want to append "&" to send to background.


# Reference material for GPIO and Interrupts
# http://kampis-elektroecke.de/?page_id=3740
# http://raspi.tv/2013/how-to-use-interrupts-with-python-on-the-raspberry-pi-and-rpi-gpio
# https://pypi.python.org/pypi/RPi.GPIO

import RPi.GPIO as GPIO
import time
import os
import sys

# default debounce time
DEBOUNCE_MS=500

try:
    # Get GPIO pin number
    GPIO_PIN = int(sys.argv[1])

    # Get command to execute
    COMMAND = sys.argv[2]

    # Get optional debounce time
    if len(sys.argv) > 3:
        DEBOUNCE_MS=int(sys.argv[3]);
except:
    # print usage info
    print ("ERROR -- Invalid pin or command specified")
    print ("")
    print ("Usage:   python buttonAction.py <pin_num> <command> [debounce_ms]")
    print ("Example: python buttonAction.py  27  \"shutdown -h now\"")
    print ("Example: python buttonAction.py  27  \"shutdown -h now\" 2000")
    print ("")
    print ("Note, you probably want to append \"&\" to send to background.")
    print ("The default debounce time is {:d} milliseconds.".format(DEBOUNCE_MS))
    print ("")
    sys.exit()

# set up command to get milliseconds time
def millis():
    return int(round(time.time() * 1000))

# use the pin numbering from SoC in setup
GPIO.setmode(GPIO.BCM)  

# configure the requested GPIO pin as an input with pull-up resistor
GPIO.setup(GPIO_PIN, GPIO.IN, pull_up_down = GPIO.PUD_UP)  

# store flag to avoid multiple-execution
last_run_ms = millis()

# construct function to run specified code on button press
def Int_do_action(channel):
    # use the global variable
    global last_run_ms

    # check that it doesn't run twice in the same millisecond
    # this is a workaround for the add_event_detect sometimes
    # calling repeatedly if it is implemented for multiple
    # scripts or pins.
    if millis()-last_run_ms > 1:
        # run the command
        os.system(COMMAND)
        # update the last run time
        last_run_ms = millis()
   
# add interrupt on falling-edge (when pin goes low as button is pressed)
GPIO.add_event_detect(GPIO_PIN, GPIO.FALLING, callback = Int_do_action, bouncetime = DEBOUNCE_MS)

# print status
print("Initialized GPIO button: GPIO_PIN={:d}, COMMAND=`{:s}`, DEBOUNCE_MS={:d}".format(GPIO_PIN,COMMAND,DEBOUNCE_MS))

# do nothing while waiting for button to be pressed
while 1:
    time.sleep(1)
