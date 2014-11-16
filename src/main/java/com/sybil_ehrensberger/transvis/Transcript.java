package com.sybil_ehrensberger.transvis;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for each transcript and thus each individual process.
 *
 * @author Sybil Ehrensberger
 */
public class Transcript {

    /**
     * Participant ID
     */
    public String participant;
    /**
     * Group the participant belongs to
     */
    public String group;
    /**
     * Competence level (i.e. Beg, Pro)
     */
    public String competence;
    /**
     * Language version
     */
    public String version;
    /**
     * Name of the source text
     */
    public String sourcetextname;
    /**
     * Recording information added in the XML file
     */
    public Recording recording = null;
    /**
     * Adjustment time [in sec]
     */
    public int startAdjustment;
    /**
     * Total time of process (specified by start and end in recording tag)
     */
    public int totalTime;
    /**
     * Beginning of the drafting phase (first write incident)
     */
    public Double startDrafting = null;
    /**
     * Beginning of the revision phase (specified in recording tag)
     */
    public int startRevision;
    /**
     * Whether this transcript is from a workplace environment
     */
    public boolean workPlace;
    /**
     * Name of the selection (e.g. Complete Process, Orientation Phase, ...)
     */
    public String selection;
    /**
     * Beginning of the selection [in sec]
     */
    public double startSelection;
    /**
     * End of the selection [in sec]
     */
    public double endSelection;
    /**
     * Total duration of the selection [in sec]
     */
    public double durationSelection;
    /**
     * List of all found incidents
     */
    List<BaseIncident> incidents;
    /**
     * List of all incidents within the selection
     */
    List<BaseIncident> validIncidents;
    private String name;

    /**
     * Public constructor for a Transcript.
     *
     * @param fxmlFile the file to be parsed.
     * @throws ParserConfigurationException problem with the parser configuration
     * @throws SAXException                 problem with the xml parser
     * @throws IOException                  problem with reading the file
     */
    public Transcript(File fxmlFile) throws ParserConfigurationException, SAXException, IOException {

        incidents = new LinkedList<>();

        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        TranscriptHandler handler = new TranscriptHandler(this);
        parser.parse(fxmlFile, handler);

        if (name == null || name.isEmpty())
            throw new TranscriptParseError("Missing name attribute!");

        if (recording == null)
            throw new TranscriptParseError("Missing recording attribute!");

    }

    public Transcript() {
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

    /**
     * The name of the file to be analyzed (also specified in the name attribute)
     *
     * @return the name of the file to be analyzed
     */
    public String getName() {
        return name;
    }

    /**
     * Parse the name attribute of the document in to individual parts and throw a fatal error if is malformed.
     *
     * @param n the name attribute from the XML file
     * @throws TranscriptParseError if the transcript is invalid
     */
    public void setName(String n) throws TranscriptParseError {

        if (n == null)
            throw new TranscriptParseError(n + ": Invalid name attribute");

        String[] parts = n.split("_");
        if (parts.length >= 4) {
            participant = parts[0];
            group = participant.replaceAll("(.{4})(\\d)*", "$1");
            competence = parts[1];
            version = parts[2];
            sourcetextname = parts[3];
        } else
            throw new TranscriptParseError(n + ": Invalid name attribute. ");
        name = n;
    }

    /**
     * Add the recording information to the transcript and validate it. If it is not valid, throw a fatal error.
     *
     * @param r the recording information to be added
     * @throws TranscriptParseError if the transcript is invalid
     */
    public void addRecording(Recording r) throws TranscriptParseError {

        if (recording != null) {
            throw new TranscriptParseError(name + ": Multiple recording attributes found.");
        }

        r.validate(this);

        recording = r;

    }

    // TODO: Better error handling!
    public void error(String s) {

        Main.note(getName() + ": " + s);
        System.out.println("ERROR: " + s);
    }

    /**
     * Set the selection according to the given type and given start and end times. Filter the incidents accordingly
     * so only incidents within that time span will be used.
     *
     * @param type  number indicating which type was chosen in the GUI (0: partial, 1: complete, 2: orientation, 3: drafting, 4: revision)
     * @param start the start time in seconds for partial processes
     * @param end   the end time in seconds for partial processes
     */
    public void setSelection(int type, int start, int end) {

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

        System.out.println("Start selection: " + startSelection);
        System.out.println("End selection: " + endSelection);
        System.out.println("Total time selection: " + durationSelection);

        validIncidents = incidents.stream().filter(inc -> inc.valid()).collect(Collectors.toList());
    }

    /**
     * Adjust the times of each incident to the current selection. Only adjust if the boolean adjust is true.
     * <p>
     * Function to be used in the graphs so that an incident a the start of a phase (orientation, drafting, revision)
     * will always have start time 0. This way, processes with different phase lengths can be compared accurately.
     * Beware this function adjusts the times in place (i.e. each incident is modified).
     *
     * @param adjust whether to adjust the times or not
     */
    public void adjustTimesToSelection(boolean adjust) {

        if (adjust) {
            for (BaseIncident b : validIncidents) {
                b.start = b.start - startSelection;
                b.end = b.end - startSelection;
            }
        }

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

}

