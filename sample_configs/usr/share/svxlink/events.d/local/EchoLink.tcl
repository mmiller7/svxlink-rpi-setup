###############################################################################
#
# Overriden EchoLink module event handlers
#
###############################################################################

#
# This is the namespace in which all functions and variables below will exist.
# The name must match the configuration variable "NAME" in the
# [ModuleEchoLink] section in the configuration file. The name may be changed
# but it must be changed in both places.
#
namespace eval EchoLink {

#
# Executed when a transmission from an EchoLink station is starting
# or stopping
#
proc is_receiving {rx} {
#Don't make "beep" at the end of each transmission
#  if {$rx == 0} {
#    playTone 1000 100 100;
#  }
}

# end of namespace
}

#
# This file has not been truncated
#
