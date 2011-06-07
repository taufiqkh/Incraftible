NoCraft
=======

A [Minecraft](http://www.minecraft.net/) [Bukkit](http://bukkit.org/) plugin to prevent crafting of specified items. Configurable via the command line or config file.

Functionality is currently limited, as the plugin was only written in a day. It is expected that at some point Bukkit will include the required hooks to allow
more comprehensive permissions plugins (eg. WorldGuard) to fill the gap, and this plugin will be deprecated.

Configuration
-------------

Configuration is via a yml file in the plugins/NoCraft subdirectory of the minecraft server. There is currently only one option, `disallowed`, which specifies the
item ids of items that are not permitted to be crafted. For example, the following prevents the crafting of wooden picks and maps, respectively:
    disallowed:
    - 270
    - 258

Additionally, in-game commands can allow and disallow items. To disallow the crafting of maps:
    /nc disallow 258

To allow it again:
    /nc disallow 258

These commands can only be executed by an op. Also note that these commands are global, ie. not even an op can craft disallowed items, though they can of course allow them again.