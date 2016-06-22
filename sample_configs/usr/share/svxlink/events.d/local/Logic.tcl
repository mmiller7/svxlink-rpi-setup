###############################################################################
#
# Generic Logic event handlers
#
###############################################################################

#
# This is the namespace in which all functions and variables below will exist.
#
namespace eval Logic {

# Used to "lock" DTMF processing until "unlock" code is entered
variable dtmf_disabled 0;

#
# Executed when a DTMF command has been received
#   cmd - The command
#
# Return 1 to hide the command from further processing is SvxLink or
# return 0 to make SvxLink continue processing as normal.
#
# This function can be used to implement your own custom commands or to disable
# DTMF commands that you do not want users to execute.
proc dtmf_cmd_received {cmd} {
  #global active_module

  # Example: Ignore all commands starting with 3 in the EchoLink module
  #if {$active_module == "EchoLink"} {
  #  if {[string index $cmd 0] == "3"} {
  #    puts "Ignoring random connect command for module EchoLink: $cmd"
  #    return 1
  #  }
  #}

  # Handle the "force core command" mode where a command is forced to be
  # executed by the core command processor instead of by an active module.
  # The "force core command" mode is entered by prefixing a command by a star.
  #if {$active_module != "" && [string index $cmd 0] != "*"} {
  #  return 0
  #}
  #if {[string index $cmd 0] == "*"} {
  #  set cmd [string range $cmd 1 end]
  #}

  # Example: Custom command executed when DTMF 99 is received
  #if {$cmd == "99"} {
  #  puts "Executing external command"
  #  playMsg "Core" "online"

  #  exec ls &
  #  return 1
  #}


  variable dtmf_disabled

  # Enable DTMF Commands
  if {$dtmf_disabled && $cmd == "123"} {
    set dtmf_disabled 0;
    puts "Enabling DTMF Commands"
    playMsg "Core" "activating"
    return 1;
  }

  # Abort here if DTMF commands are disabled
  if {$dtmf_disabled} {
    puts "DTMF disabled - ignore: $cmd#"
    return 1;
  }

  # Disable DTMF Commands
  if {$cmd == "321"} {
    set dtmf_disabled 1;
    puts "Disabling DTMF Commands"
    playMsg "Core" "deactivating"
    return 1;
  }
  
  

 # say IP address for interface
  proc sayIP {iface} {
    set result [exec /usr/local/bin/getIP $iface]
    puts "$result"
    spellWord "$result"
    playSilence 500;
  }

  # internet test
  proc sayInternetStatus {} {
    if {[catch {exec ping -c 1 google.com} result] == 0} {
      puts "Internet Online Passed"
      playMsg "Core" "online"
    } else {
      puts "Internet Disconnected"
      #playMsg "MetarInfo" "not"
      #playMsg "Core" "online"
      #playSilence 500;
      playMsg "EchoLink" "link"
      playMsg "EchoLink" "disconnected"
    }
  }


  # Speak network IPs
  if {$cmd == "99"} {
    sayIP "eth0";
    sayIP "eth1";
    sayIP "wlan0";
    sayInternetStatus
    return 1
  }

  # Speak eth0 IP
  if {$cmd == "990"} {
    sayIP "eth0";
    return 1
  }

  # Speak eth1 IP
  if {$cmd == "991"} {
    sayIP "eth1";
    return 1
  }

  # Speak wlan0 IP
  if {$cmd == "992"} {
    sayIP "wlan0";
    return 1
  }

  # Say if online or offline
  if {$cmd == "999"} {
    sayInternetStatus
    return 1
  }

  return 0
}

# end of namespace
}

#
# This file has not been truncated
#
