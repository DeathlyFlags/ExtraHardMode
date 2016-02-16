package com.megahardcore.module;


import com.megahardcore.config.messages.MessageNode;

/**
 * Holds all of MegaHardCore's player-tied data
 */
public class PlayerData
{
    /** Last message sent. */
    public MessageNode lastMessageSent = null;

    /** Last message timestamp. */
    public long lastMessageTimestamp = 0;

    /** Cached weight */
    public float cachedWeightStatus = -1.0F; //player can't have negative invetory....

    /** If player is in debugmode */
    public boolean inDebug = false;
}
