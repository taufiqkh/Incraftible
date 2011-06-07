package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.ENTER_TO_CONTINUE;
import static com.quiptiq.nocraft.Message.USAGE;

import java.io.IOException;

/**
 * Command-line execution.
 *
 * @author Taufiq Hoven
 */
public final class Main {
    /**
     * Hides constructor.
     */
    private Main() {

    }

    /**
     * Default main method in case of accidentally executing the jar.
     *
     * @param args  Command-line arguments. These are ignored.
     */
    public static void main(String[] args) {
        System.out.println(USAGE);
        System.out.println(ENTER_TO_CONTINUE);
        try {
            System.in.read();
            System.in.skip(System.in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
