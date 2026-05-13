package me.eternalhuman.packetboard;

import me.eternalhuman.packetboard.protocol.PacketIds;
import me.eternalhuman.packetboard.protocol.ProtocolConstants;
import me.eternalhuman.packetboard.util.version.MinecraftProtocolVersion;
import me.eternalhuman.packetboard.util.version.MinecraftVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PacketIdsTest {


    @Test
    public void testPacketIds() {
        assertEquals(0x44, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_12_2));
        assertEquals(0x47, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_13));
        assertEquals(0x47, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_13_2));
        assertEquals(0x47, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_13_1));

        assertEquals(0x4B, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_14));
        assertEquals(0x4B, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_14_1));
        assertEquals(0x4B, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_14_2));
        assertEquals(0x4B, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_14_3));
        assertEquals(0x4B, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_14_4));


        assertEquals(0x56, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_19_3));
        assertEquals(0x5C, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_1_20_2));
    }

    @Test
    public void testMinecraft26PacketIds() {
        assertEquals(0x6A, PacketIds.UPDATE_OBJECTIVES.getPacketId(ProtocolConstants.MINECRAFT_26_1_2));
        assertEquals(0x6D, PacketIds.UPDATE_TEAMS.getPacketId(ProtocolConstants.MINECRAFT_26_1_2));
        assertEquals(0x6E, PacketIds.UPDATE_SCORE.getPacketId(ProtocolConstants.MINECRAFT_26_1_2));
        assertEquals(0x4F, PacketIds.RESET_SCORE.getPacketId(ProtocolConstants.MINECRAFT_26_1_2));
        assertEquals(0x62, PacketIds.DISPLAY_OBJECTIVES.getPacketId(ProtocolConstants.MINECRAFT_26_1_2));
    }

    @Test
    public void testMinecraft26ProtocolVersions() {
        assertEquals(775, MinecraftProtocolVersion.getVersion(new MinecraftVersion(26, 1, 0)));
        assertEquals(775, MinecraftProtocolVersion.getVersion(new MinecraftVersion(26, 1, 1)));
        assertEquals(775, MinecraftProtocolVersion.getVersion(new MinecraftVersion(26, 1, 2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedVersion() {
        PacketIds.UPDATE_TEAMS.getPacketId(47); // 1.8
    }
}
