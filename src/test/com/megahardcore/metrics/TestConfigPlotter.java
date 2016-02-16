package com.megahardcore.metrics;


import com.megahardcore.service.MockConfigNode;
import com.megahardcore.metrics.ConfigPlotter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Diemex
 */
public class TestConfigPlotter
{
    @Test
    public void testLastPart()
    {
        assertEquals("test04", ConfigPlotter.getLastPart(MockConfigNode.BOOL_TRUE));
        assertEquals("test 01", ConfigPlotter.getLastPart(MockConfigNode.BOOL_FALSE));
    }
}
