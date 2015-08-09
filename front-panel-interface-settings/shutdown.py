# This script will wait for a button to be pressed and then shutdown
# the Raspberry Pi.
# The button is to be connected between GROUND and the pin-number
# specified by the command-line parameter.
# I recommend putting this script in /root/shutdown.py

# Add this line in /etc/rc.local:
# python /root/shutdown.py PIN_NUM &
# ex.: python /root/shutdown.py 27 &
# where "27" is replaced with your desired GPIO pin number.

# http://kampis-elektroecke.de/?page_id=3740
# http://raspi.tv/2013/how-to-use-interrupts-with-python-on-the-raspberry-pi-and-rpi-gpio
# https://pypi.python.org/pypi/RPi.GPIO

import RPi.GPIO as GPIO
import time
import os
import sys

# Prompt user for GPIO Pin
try:
    GPIO_PIN = sys.argv[1]
    GPIO_PIN = int(GPIO_PIN)
except:
    print ("shutdown.py: Invalid pin specified")
    sys.exit()

# we will use the pin numbering of the SoC, so our pin numbers in the code are 
# the same as the pin numbers on the gpio headers
GPIO.setmode(GPIO.BCM)  

# Pin GPIO_PIN will be input and will have his pull up resistor activated
# so we only need to connect a button to ground
GPIO.setup(GPIO_PIN, GPIO.IN, pull_up_down = GPIO.PUD_UP)  

# ISR: if our button is pressed, we will have a falling edge on GPIO_PIN
# this will trigger this interrupt:
def Int_shutdown(channel):  
   # shutdown our Raspberry Pi
   os.system("sudo shutdown -h now")
   
# Now we are programming pin GPIO_PIN as an interrupt input
# it will react on a falling edge and call our interrupt routine "Int_shutdown"
GPIO.add_event_detect(GPIO_PIN, GPIO.FALLING, callback = Int_shutdown, bouncetime = 2000)   

# do nothing while waiting for button to be pressed
while 1:
        time.sleep(1)
