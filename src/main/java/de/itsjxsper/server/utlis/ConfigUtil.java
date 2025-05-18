package de.itsjxsper.server.utlis;

import de.itsjxsper.server.Main;

public class ConfigUtil {

    public static String getString(String Path) {
        return " "  + Main.getInstance().getConfig().getString(Path, PrefixUtil.getPrefix() +"<red>No Value</red>");
    }
}
