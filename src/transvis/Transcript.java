package transvis;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for each transcript and thus each individual process.
 *
 * @author Sybil Ehrensberger
 */
public class Transcript {

    /** The name of the file to be analyzed (also specified in the name attribute) */
    public String name;
    /** Participant ID */
    public String participant;
    /** Group the participant belongs to */
    public String group;
    /** Competence level (i.e. Beg, Pro) */
    public String competence;
    /** Language version */
    public String version;
    /** Name of the source text */
    public String sourcetextname;
    /** Recording information added in the XML file */
    public Recording recording = null;

    /** Adjustment time [in sec] */
    public int startAdjustment;
    /** Total time of process (specified by start & end in recording tag) */
    public int totalTime;
    /** Beginning of the drafting phase (first write incident) */
    public Integer startDrafting = null;
    /** Beginning of the revision phase (specified in recording tag) */
    public int startRevision;

    /** Whether this transcript is from a workplace environment */
    public boolean workPlace;

    /** Name of the selection (e.g. Complete Process, Orientation Phase, ...) */
    public String selection;
    /** Beginning of the selection [in sec] */
    public int startSelection;
    /** End of the selection [in sec] */
    public int endSelection;
    /** Total duration of the selection [in sec] */
    public int durationSelection;

    /** List of all found incidents */
    List<BaseIncident> incidents;

    /**
     * Public constructor for a Transcript.
     *
     * @param fxmlFile the file to be parsed.
     * @throws ParserConfigurationException problem with the parser configuration
     * @throws SAXException problem with the xml parser
     * @throws IOException problem with reading the file
     */
    public Transcript(File fxmlFile) throws ParserConfigurationException, SAXException, IOException {

        incidents = new LinkedList<>();

        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        TranscriptHandler handler = new TranscriptHandler(this);
        parser.parse(fxmlFile, handler);

    }

    /**
     * Parse the name attribute of the document in to individual parts and throw a fatal error if is malformed.
     *
     * @param n the name attribute from the XML file
     */
    public void setName(String n) {

        name = n;
        String[] parts = name.split("_");
        if (parts.length >= 4) {
            participant = parts[0];
            group = participant.replaceAll("(.{4})(\\d)*", "$1");
            competence = parts[1];
            version = parts[2];
            sourcetextname = parts[3];
        } else
            fatal("Invalid name attribute: " + name);
    }

    /**
     * Add the recording information to the transcript and validate it. If it is not valid, throw a fatal error.
     *
     * @param r the recording information to be added
     */
    public void addRecording(Recording r) {

        if (recording != null) {
            fatal("Multiple recording attributes");
        }

        try {
            r.validate(this);
        } catch (TranscriptError e) {
            fatal(e.getMessage());
        }

        recording = r;

    }

    // TODO: Better error handling!
    public static void fatal(String s) {
        System.out.println(s);
        System.exit(1);
    }

    // TODO: Better error handling!
    public static void error(String s) {
        System.out.println("ERROR: " + s);
    }

    /**
     * Set the selection according to the given type and given start and end times. Filter the incidents accordingly
     * so only incidents within that time span will be used.
     *
     * @param type number indicating which type was chosen in the GUI (0: partial, 1: complete, 2: orientation, 3: drafting, 4: revision)
     * @param start the start time in seconds for partial processes
     * @param end the end time in seconds for partial processes
     */
    public void setSelection(int type, int start, int end) {

        // TODO: This does not seem to do anything when showing the graph!
        System.out.println("Start process: " + 0);
        System.out.println("Start drafting: " + startDrafting);
        System.out.println("Start revision: " + startRevision);
        System.out.println("End process: " + totalTime);

        switch (type) {
            case 0: // partial
                selection = "Partial Process";
                startSelection = start;
                endSelection = end;
                break;

            case 2: // orientation
                selection = "Orientation Phase";
                startSelection = 0;
                endSelection = startDrafting;
                break;
            case 3: // drafting
                selection = "Drafting Phase";
                startSelection = startDrafting;
                endSelection = startRevision;
                break;
            case 4: // revision
                selection = "Revision Phase";
                startSelection = startRevision;
                endSelection = totalTime;
                break;
            case 1: // complete
            default:
                selection = "Complete Process";
                startSelection = 0;
                endSelection = totalTime;
                break;
        }

        if (endSelection > totalTime) {
            error("Selected end time is after the end of the process");
            endSelection = totalTime;
        }

        if (startSelection < 0) {
            error("Selected start time is negative");
            startSelection = 0;
        }
        durationSelection = endSelection - startSelection;
    }


    /**
     * Converts a time (String) of the format HH:MM:SS to seconds and adjusts
     * it to the adjustment start time.
     *
     * @param timestring a String representing a time (HH:MM:SS)
     * @return an int representing the time in seconds
     */
    public int adjustTime(String timestring) {
        int time = convertToSeconds(timestring);
        return adjustTime(time);
    }

    /**
     * Adjust the given time t to ignore the warm-up phase.
     *
     * @param t the time in seconds
     * @return the adjusted time in seconds
     */
    public int adjustTime(int t) {
        return t - startAdjustment;
    }

    /**
     * Converts the given timestring of the format HH:MM:SS to seconds
     * (an int).
     *
     * @param timestring the time (format HH:MM:SS)
     * @return the elapsed time in seconds
     */
    public static int convertToSeconds(String timestring) {

        String[] times = timestring.trim().split(":");
        int time = Integer.parseInt(times[0]) * 3600
                + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2]);

        return time;
    }

}

