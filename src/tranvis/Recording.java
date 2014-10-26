package tranvis;

import org.xml.sax.Attributes;

/**
 * Created by ehrensbe on 25/10/14.
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

    public void validate(Transcript t) throws TranscriptError {

        // TODO: what is a NO-GO? With what can we deal?
        if (startTransProcess.isEmpty())
            throw new TranscriptError("recording: missing start time");

        t.startAdjustment = Transcript.convertToSeconds(startTransProcess);

        if (endTransProcess.isEmpty())
            throw new TranscriptError("recording: missing end time");

        int end = Transcript.convertToSeconds(endTransProcess);
        if (end < t.startAdjustment)
            throw new TranscriptError("recording: end time before start time");
        t.totalTime = end - t.startAdjustment;

        if (startRevision.isEmpty())
            throw new TranscriptError("recording: missing startRevision");

        t.startRevision = t.adjustTime(startRevision);

    }
}
