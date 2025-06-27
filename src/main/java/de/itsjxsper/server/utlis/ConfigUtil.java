package de.itsjxsper.server.utlis;

import de.itsjxsper.server.Main;

public class ConfigUtil {

    public static String getString(String Path) {
        return Main.getInstance().getConfig().getString(Path, PrefixUtil.getPrefix() +"<red>No Value</red>");
    }

    public static boolean getBoolean(String Path) {
        return Main.getInstance().getConfig().getBoolean(Path, false);
    }

    public static Object set(String Path, Object value) {
        Main.getInstance().getConfig().set(Path, value);
        Main.getInstance().saveConfig();
        return value;
    }
}
