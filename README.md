Incraftible
===========

A [Minecraft](http://www.minecraft.net/) [Bukkit](http://bukkit.org/) plugin to prevent crafting of specified items. Configurable via config file.

Functionality is currently limited. It is expected that at some point Bukkit will include the required hooks to allow
more comprehensive permissions plugins (eg. WorldGuard) to fill the gap, and this plugin will be deprecated.

Configuration
-------------

Configuration of who can craft what is done via Bukkit permissions. All standard Bukkit materials are supported. For convenience, several permission nodes have
been created that control permissions on a number of objects. These nodes are:

* incraftible.craft.\*: Allows crafting of all items.
* incraftible.craft.tools.\*:  Allows crafting of all tools, including swords. These may be further subdivided into wood, stone, iron, gold and diamond.
* incraftible.craft.standard: Allows crafting of standard minecraft objects. That is, any object that you can craft on a vanilla server.
* incraftible.craft.armor.\*: Allows crafting of all armor. These may be further subdivided into leather, iron, gold and diamond.

Configuration of the plugin itself is via a yml file in the plugins/Incraftible subdirectory of the minecraft server. At the moment the only configurable option is
the message sent to the player when crafting fails.

Dependencies
------------
Incraftible requires CraftBukkit and the Spout plugin. Spout is necessary for the crafting hooks, which at the time of writing is not present in Bukkit.

Development
-----------
Incraftible has been purpose-built for a specific server. Pull requests may be accepted depending on the features/fixes introduced, the quality of the code and tests and how much time I have.

Compatibility
-------------

Incraftible has been tested with the following:

* CraftBukkit #1060
* Spout #121, #134

Planned Features
----------------
In-game commands are being investigated.

Dyes are currently all enabled or all disabled, as they all share the same Bukkit material. Permissions will probably be based on their colours, ie: black, red etc.

Item names are planned to be put into a resource bundle, partly so that I learn how to use resource bundles. 