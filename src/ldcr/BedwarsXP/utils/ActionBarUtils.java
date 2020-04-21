package ldcr.BedwarsXP.utils;

import ldcr.BedwarsXP.BedwarsXP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBarUtils {
    private static boolean USE_CHAT = false;
    private static boolean USE_1_7_NMS = false;
    private static boolean USE_1_8_NMS = false;
    private static boolean USE_1_11_API = false;

    private static Class<?> classCraftPlayer;
    private static Class<?> classPacketPlayOutChat;
    private static Class<?> classPacket;
    private static Class<?> classChatSerializer;
    private static Method methodSerializeMessage;
    private static Class<?> classIChatBaseComponent;
    private static Class<?> classChatComponentText;
    private static Method methodGetHandle = null;
    private static Field fieldPlayerConnection = null;
    private static Method methodSendPacket = null;

    public static void load() {
        String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName();
        NMS_VERSION = NMS_VERSION.substring(NMS_VERSION.lastIndexOf('.') + 1);

        if (NMS_VERSION.equalsIgnoreCase("v1_8_R1") || NMS_VERSION.equalsIgnoreCase("v1_7_")) {
            USE_1_7_NMS = true;
        } else {
            try {
                int ver = Integer.parseInt(NMS_VERSION.split("_")[1]);
                if (ver >= 11) {
                    USE_1_11_API = true;
                } else {
                    USE_1_8_NMS = true;
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                BedwarsXP.sendConsoleMessage("§cERROR: " + BedwarsXP.l18n("ERROR_UNSUPPORTED_VERSION_ACTIONBAR_MAY_NOT_WORK"));
                USE_CHAT = true;
            }
        }

        if (USE_1_7_NMS || USE_1_8_NMS) {
            try {
                classCraftPlayer = Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".entity.CraftPlayer");
                classPacketPlayOutChat = Class.forName("net.minecraft.server." + NMS_VERSION + ".PacketPlayOutChat");
                classIChatBaseComponent = Class.forName("net.minecraft.server." + NMS_VERSION + ".IChatBaseComponent");
                classPacket = Class.forName("net.minecraft.server." + NMS_VERSION + ".Packet");
                if (USE_1_7_NMS) {
                    classChatSerializer = Class.forName("net.minecraft.server." + NMS_VERSION + ".ChatSerializer");
                    methodSerializeMessage = classChatSerializer.getDeclaredMethod("a", String.class);
                } else {
                    classChatComponentText = Class.forName("net.minecraft.server." + NMS_VERSION + ".ChatComponentText");
                }
            } catch (ReflectiveOperationException e) {
                BedwarsXP.sendConsoleMessage("§cERROR: " + BedwarsXP.l18n("ERROR_UNSUPPORTED_VERSION_ACTIONBAR_MAY_NOT_WORK"));
                USE_1_7_NMS = false;
                USE_1_8_NMS = false;
                USE_CHAT = true;
            }
        }
    }

    public static void sendActionBar(Player player, String message) {
        if (USE_CHAT) {
            player.sendMessage(message);
        } else if (USE_1_11_API) {
            player.sendActionBar(message);
        } else if (USE_1_7_NMS || USE_1_8_NMS) {
            try {
                Object objectCraftPlayer = classCraftPlayer.cast(player);
                Object objectPacketChat;
                Object objectChatComponent;
                if (USE_1_7_NMS) {
                    objectChatComponent = classIChatBaseComponent.cast(methodSerializeMessage.invoke(classChatSerializer, "{\"text\": \"" + message + "\"}"));
                } else {
                    objectChatComponent = classChatComponentText.getConstructor(String.class).newInstance(message);
                }
                objectPacketChat = classPacketPlayOutChat.getConstructor(classIChatBaseComponent, byte.class).newInstance(objectChatComponent, (byte) 2);
                if (methodGetHandle == null)
                    methodGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
                Object objectEntityPlayer = methodGetHandle.invoke(objectCraftPlayer);
                if (fieldPlayerConnection == null)
                    fieldPlayerConnection = objectEntityPlayer.getClass().getDeclaredField("playerConnection");
                Object objectPlayerConnection = fieldPlayerConnection.get(objectEntityPlayer);
                if (methodSendPacket == null)
                    methodSendPacket = objectPlayerConnection.getClass().getDeclaredMethod("sendPacket", classPacket);
                methodSendPacket.invoke(objectPlayerConnection, objectPacketChat);
            } catch (ReflectiveOperationException e) { // Reflection exception caught -> dont retry, switch to backend
                e.printStackTrace();
                USE_1_7_NMS = false;
                USE_1_8_NMS = false;
                USE_CHAT = true;
                player.sendMessage(message);
            } catch (Exception e) { // send message to chat instead, retry actionbar later
                e.printStackTrace();
                player.sendMessage(message);
            }
        }
    }

}