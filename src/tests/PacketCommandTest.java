package tests;

import enums.PacketCommand;

public class PacketCommandTest {
    public static void main(String[] args) {
        System.out.println("Decoding: ");
        for (PacketCommand p : PacketCommand.values()) {
            System.out.println(p.toString() + ":");
            System.out.println("  Ord  = " + p.ordinal());
            System.out.println("  Code = " + String.format("0x%02X", p.code()));
            System.out.println("  Type = " + p.getType());
        }

        System.out.println("");

        System.out.println("Encoding");
        for (byte i = 0; i <= 0x15; i++) {
            System.out.println(String.format("0x%02X", i) + " = " + PacketCommand.fromByte(i));
        }
        System.out.println("1");

        for (int i = 0xA0; i <= 0xB5; i++) {
            System.out.println(String.format("0x%02X", (byte)i) + " = " + PacketCommand.fromByte((byte)i));
        }
        System.out.println("2");

        for (int i = 0xD0; i <= 0xD3; i++) {
            System.out.println(String.format("0x%02X", (byte)i) + " = " + PacketCommand.fromByte((byte)i));
        }
    }
}
