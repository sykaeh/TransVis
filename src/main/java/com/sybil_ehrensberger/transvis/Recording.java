package com.sybil_ehrensberger.transvis;

import org.xml.sax.Attributes;

/**
 * Class representing the &lt;recording&gt; XML tag in the transcript
 *
 * @author Sybil Ehrensberger
 */
public class Recording {

    private String startTransProcess;
    private String endTransProcess;
    private String startRevision; /* startRevision in recording tag (in real time) */

    public String transProcessComplete; // transProcessComplete
    public String kslavailable; // KSLavailable
    public String etavailable; // ETavailable
    public String etquality; // ETquality
    public String direction; // direction
    public String concurrentVisibility; // concurrentVisibilitySTTT

    /**
     * Public constructor
     *
     * @param atts  Attributes associated with the recording tag
     */
    public Recording(Attributes atts) {

        startTransProcess = atts.getValue("startTransProcess").trim();
        endTransProcess = atts.getValue("endTransProcess").trim();
        startRevision = atts.getValue("startRevision").trim();

        transProcessComplete = atts.getValue("transProcessComplete").trim();
        kslavailable = atts.getValue("KSLavailable").trim();
        etavailable = atts.getValue("ETavailable").trim();
        etquality = atts.getValue("ETquality").trim();
        direction = atts.getValue("direction").trim();
        concurrentVisibility = atts.getValue("concurrentVisibilitySTTT").trim();

    }

    /**
     * Validate the recording tag to make sure all necessary attributes are present and valid.
     * Throw a TranscriptError if that is not the case. Set the start, end and start revision times.
     *
     * @param t the Transcript the recording belongs to
     * @throws TranscriptParseError if a necessary attribute is missing or invalid
     */
    public void validate(Transcript t) throws TranscriptParseError {

        if (startTransProcess.isEmpty())
            throw new TranscriptParseError(t.getName() + ": recording tag: missing start time");

        t.startAdjustment = Transcript.convertToSeconds(startTransProcess);

        if (endTransProcess.isEmpty())
            throw new TranscriptParseError(t.getName() + ": recording tag: missing end time");

        int end = Transcript.convertToSeconds(endTransProcess);
        if (end < t.startAdjustment)
            throw new TranscriptParseError(t.getName() + ": recording tag: end time before start time");
        t.totalTime = end - t.startAdjustment;

        if (startRevision.isEmpty())
            throw new TranscriptParseError(t.getName() + ": recording tag: missing startRevision");

        t.startRevision = t.adjustTime(startRevision);

    }
}
