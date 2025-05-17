package de.itsjxsper.server.utlis;

import de.itsjxsper.server.Main;

public class PrefixUtil {

    public static String getPrefix() {
        return Main.getInstance().getConfig().getString("prefix");
    }
}
