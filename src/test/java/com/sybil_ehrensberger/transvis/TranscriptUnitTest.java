package com.sybil_ehrensberger.transvis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class TranscriptUnitTest {

    Transcript testTranscript;

    @Before
    public void setUp() throws Exception {

        testTranscript = new Transcript();
    }

    @After
    public void tearDown() throws Exception {

        testTranscript = null;
    }


    @Test
    public void testSetName() throws Exception {

        testTranscript.setName("Test001_Level_DIR_basic_Transcript");

        assertEquals("Test", testTranscript.group);
        assertEquals("Test001", testTranscript.participant);
        assertEquals("Level", testTranscript.competence);
        assertEquals("DIR", testTranscript.version);
        assertEquals("basic", testTranscript.sourcetextname);

    }

    @Test(expected = TranscriptParseError.class)
    public void testSetNameInvalid() throws Exception {
        testTranscript.setName("");
    }

    @Test(expected = TranscriptParseError.class)
    public void testSetNameInvalid2() throws Exception {
        testTranscript.setName("not_enough_underscores");
    }

    @Test
    public void testSetSelection() throws Exception {

    }

    @Test
    public void testAdjustTimesToSelection() throws Exception {

    }

    @Test
    public void testAdjustTime() throws Exception {

    }

    @Test
    public void testAdjustTime1() throws Exception {

    }

    @Test
    public void testConvertToSeconds() throws Exception {

    }
}