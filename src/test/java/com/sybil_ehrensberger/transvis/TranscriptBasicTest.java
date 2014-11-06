package com.sybil_ehrensberger.transvis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TranscriptBasicTest {

    Transcript testTranscript;

    @Before
    public void setUp() throws Exception {

        URL url = this.getClass().getResource("/Test001_Level_DIR_basic_Transcript.xml");
        testTranscript = new Transcript(new File(url.getFile()));
    }

    @After
    public void tearDown() throws Exception {

        testTranscript = null;
    }

    @Test
    public void testEssentials() throws Exception {

        assertEquals("Start time wrong or missing", 90, testTranscript.startAdjustment);
        assertEquals("Total time wrong or missing", 1230, testTranscript.totalTime);
        assertEquals("Start revision wrong or missing", 812, testTranscript.startRevision);
    }

    @Test
    public void testName() throws Exception {

        assertEquals("Test", testTranscript.group);
        assertEquals("Test001", testTranscript.participant);
        assertEquals("Level", testTranscript.competence);
        assertEquals("DIR", testTranscript.version);
        assertEquals("basic", testTranscript.sourcetextname);

    }


    @Test
    public void testRecording() throws Exception {

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