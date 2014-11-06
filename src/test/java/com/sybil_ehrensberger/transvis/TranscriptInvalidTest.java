package com.sybil_ehrensberger.transvis;

import org.junit.Test;

import java.io.File;
import java.net.URL;

public class TranscriptInvalidTest {

    @Test(expected = TranscriptParseError.class)
    public void testMissingName() throws Exception {
        URL url = this.getClass().getResource("/Test001_Level_DIR_missingName_Transcript.xml");
        Transcript t = new Transcript(new File(url.getFile()));
    }

    @Test(expected = TranscriptParseError.class)
    public void testMissingRecording() throws Exception {
        URL url = this.getClass().getResource("/Test001_Level_DIR_missingRecording_Transcript.xml");
        Transcript t = new Transcript(new File(url.getFile()));
    }

    @Test(expected = TranscriptParseError.class)
    public void testDoubleRecording() throws Exception {
        URL url = this.getClass().getResource("/Test001_Level_DIR_doubleRecording_Transcript.xml");
        Transcript t = new Transcript(new File(url.getFile()));
    }

}