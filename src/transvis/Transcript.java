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
 * @author Sybil Ehrensberger
 */
public class Transcript {

    /* The name of the file to be analyzed. */
    public String name; // name as specified in the name attribute of document


    public String participant;
    public String group;
    public String competence;
    public String version;
    public String sourcetextname;

    public Recording recording = null;


    /* Adjustment time [in sec] */
    public int startAdjustment;

    /* Total time of process (specified by start & end in recording tag) */
    public int totalTime;

    /* beginning of the drafting phase (first write incident) */
    public Integer startDrafting = null;

    /* beginning of the revision phase (specified in recording tag) */
    public int startRevision;


    public boolean workPlace;


    public String selection;
    public int startSelection; // beginning of the selected timespan
    public int endSelection; // end of the selected timespan
    public int durationSelection;

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

    public static void error(String s) {
        System.out.println("ERROR: " + s);
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
        return time - startAdjustment;
    }

    public int adjustTime(int t) {
        return t - startAdjustment;
    }
//
//    /**
//     * TODO: Check if parsing is being done properly...
//     */
//    public void parse() {
//
//        // Find all incident tags and handle them accordingly
//        NodeList incidentList = doc.getElementsByTagName("incident");
//        for (int i = 0; i < incidentList.getLength(); i++) {
//            if (incidentList.item(i).getNodeType() == Node.ELEMENT_NODE) {
//                Element e = (Element) incidentList.item(i);
//
//                if (e.hasAttribute("type") && e.getAttribute("type").equalsIgnoreCase("writes")) {
//                    Element possible_typo = (Element) incidentList.item(i + 1);
//                    Element possible_write = (Element) incidentList.item(i + 2);
//                    boolean typo = possible_typo != null && (possible_typo.hasAttribute("subtype") && possible_typo.getAttribute("subtype").equalsIgnoreCase("typo"))
//                            || (possible_typo.hasAttribute("type") && possible_typo.getAttribute("type").equalsIgnoreCase("autocorrects"));
//                    boolean writes2 = possible_write != null && possible_write.hasAttribute("type") && possible_write.getAttribute("type").equalsIgnoreCase("writes");
//                    if (typo && writes2) {
//                        handle_two_step_write(e, possible_write);
//                        handleIncident(possible_typo);
//                        i++;
//                        i++;
//
//                    } else {
//                        handleIncident(e);
//                    }
//
//                } else if (e.hasAttribute("type")) {
//                    handleIncident(e);
//                }
//            }
//        }
//    }
//
//    private void handleIncident(Element e) {
//
//        Incident i = new Incident(e, this);
//        if (i.start < 0 || i.end > lengthAdjustment) {
//            return;
//        }
//
//        IncidentList mainlist = (IncidentList)incidentlists.get(i.group);
//        mainlist.add(i);
//        IncidentList sublist = (IncidentList)incidentlists.get(i.subgroup);
//        sublist.add(i);
//        allIncidents.add(i);
//    }
//
//    TODO: What is this two step write?
//    private void handle_two_step_write(Element e, Element possible_write) {
//
//        Incident i = new Incident(e, this);
//        if (i.start < 0 || i.end > lengthAdjustment) {
//            return;
//        }
//
//        float end = 0;
//        if (possible_write.hasAttribute("end")) {
//            end = convertToReal(possible_write.getAttribute("end"));
//        } else if (possible_write.hasAttribute("start")) { // if the write does not have an end-tag, use the start tag
//            end = convertToReal(possible_write.getAttribute("start"));
//        }
//
//        if (i.validTimes) {
//
//            if (end == 0 || end < i.start) {
//                end = i.start;
//            }
//            i.end = end;
//            i.subgroup = IncidentType.PR_WRITETYPO;
//        }
//
//        IncidentList mainlist = (IncidentList)incidentlists.get(i.group);
//        mainlist.add(i);
//        IncidentList sublist = (IncidentList)incidentlists.get(i.subgroup);
//        sublist.add(i);
//        allIncidents.add(i);
//
//    }

}

