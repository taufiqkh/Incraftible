Incraftible
===========

A [Minecraft](http://www.minecraft.net/) [Bukkit](http://bukkit.org/) plugin to prevent crafting of specified items. Configurable via the command line or config file.

Functionality is currently limited, as the plugin was only written in a day. It is expected that at some point Bukkit will include the required hooks to allow
more comprehensive permissions plugins (eg. WorldGuard) to fill the gap, and this plugin will be deprecated.

Configuration
-------------

Configuration is via a yml file in the plugins/Incraftible subdirectory of the minecraft server. There is currently only one option, `disallowed`, which specifies the
item ids of items that are not permitted to be crafted. For example, the following prevents the crafting of wooden picks and maps, respectively:

    disallowed:
    - 270
    - 358

Dependencies
------------
Incraftible requires CraftBukkit and the Spout plugin.

Development
-----------
Incraftible has been purpose-built for a specific server. Pull requests may be accepted depending on the features/fixes introduced, the quality of the code and tests and how much time I have.

Compatibility
-------------

Incraftible has been tested with the following:

* CraftBukkit #1060
* Spout #121

Planned Features
----------------
In-game commands are planned to allow and disallow items. To disallow the crafting of maps:

    /ic disallow 358

To allow it again:

    /ic disallow 358

To list all currently disallowed items:

    /ic list

Note that these commands are NOT YET IMPLEMENTED.
