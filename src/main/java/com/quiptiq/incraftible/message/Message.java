package com.quiptiq.incraftible.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

/**
 * Player message that can be customised.
 */
public enum Message {
    PLAYER_MESSAGE_DISALLOWED(Message.CONFIG_PREFIX + "disallowed");

    private static final Map<String, Message> CONFIG_MESSAGES;

    /**
     * Provides names for materials.
     */
    private static final MaterialNamer namer = MaterialNamer.getInstance();

    static {
        HashMap<String, Message> messages = new HashMap<String, Message>();
        for (Message message : Message.values()) {
            messages.put(message.configNode, message);
        }
        CONFIG_MESSAGES = Collections.unmodifiableMap(messages);
    }

    private static final String CONFIG_PREFIX = "messages.";

    private final String configNode;

    private String message = "";

    private Message(String configNode) {
        this.configNode = configNode;
    }

    public void overrideMessage(String newMessage) {
        message = newMessage;
    }

    public Message valueFor(String configNode) {
        return CONFIG_MESSAGES.get(configNode);
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns the configuration node for this message.
     *
     * @return Configuration node.
     */
    public String getConfigNode() {
        return configNode;
    }

    /**
     * Prepares the configured message with the specified arguments, if any.
     *
     * @param args
     *            Message arguments.
     * @return String containing the prepared message.
     */
    public String prepareMessage(Object... args) {
        Object[] preparedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Material) {
                preparedArgs[i] = namer.getName((Material)args[i]);
            } else {
                preparedArgs[i] = args[i];
            }
        }
        return String.format(getMessage(), preparedArgs);
    }
}
