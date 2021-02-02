# Citadel-Manager 1.0

This is a minecraft pvp event plugin.

It depends on WorldEdit, WorldGuard, Vault and LuckPerms.

A server Admin/Owner can set a region to have darkcitadel and throneroom flags and then activate the event through /activatecitadel.  That will start a timer where with a length that is configurable by the server owner.  After the timer is up the event will broadcast a message to the server saying the event has started.  The regions with the preciously mentioned flags will now also allow PVP and if any player dies inside of the citadel or throne room they lose money (also configurable by the server owner).  The goal is to be the sole player inside of the throne room for an amount of time specified inside of the config.  If 1 player manages to hold the throne room for that long they will be added to a group specified by the server owner in the config.  The server will then broadcast a message saying who claimed the throne and it will disable pvp in the regions with the custom flags.  The next timer for the event will then begin.  Every time an event starts the previous winner of the event has their ranked removed.

Commands:

/activatecitadel - Checks for the regions with darkcitadel and throneroom flags and if they exist, enables the first timer.\
/disablecitadel - Turns the plugin off (This requires a reload in order to re-enable).\
/forcecitadel - Automatically starts the event regardless of how much time is left before it is supposed to start.\
/stopcitadel - Stops the event if it is currently running and starts the timer over at default from config.\
/checkcitadel - Checks how much time is left before the next event in hours. If the event has already started it will say so.

Permissions:

citadelmanager.activate //Recommended for OP's/Owner's only\
citadelmanager.disable //Recommended for OP's/Owner's only\
citadelmanager.force //Recommended for Admins + only\
citadelmanager.stop //Recommended for Admins + only\
citadelmanager.check //Reccommended for everyone
