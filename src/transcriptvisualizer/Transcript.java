package transcriptvisualizer;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sybil Ehrensberger
 */
public class Transcript {

    /* The name of the file to be analyzed. */
    public String name;
    
    
    public String participant;
    public String group;
    public String competence;
    public String version;
    public String sourcetextname;
    public String experiment;
    
    
    /* direction in recording tag */
    public String direction;
    /* startTransProcess in recording tag (in real time) */
    public float startProcess;
    /* endTransProcess in recording tag (in real time) */
    public float endProcess;
    /* transProcessComplete in recording tag */
    public String complete;
    /* startRevision in recording tag (in real time) */
    public float startRevision;
    /* KSLavailable in recording tag */
    public String kslavailable;
    /* ETavailable in recording tag */
    public String etavailable;
    /* concurrentVisibilitySTTT in recording tag */
    public String etquality;
    /* concurrentVisibilitySTTT in recording tag */
    public String concurrentVisibility;
    /* first write tag  (in real time) */
    public float startDrafting;
    
    
    // OLD STUFF
    /* The total length of the process. */
    public float lengthProcess;
    /* The total length of the time span we're looking at. */
    public float lengthAdjustment;

    /* Adjustment time in real time*/
    public float startAdjustment;
    /* last time to show in real time */
    public float endAdjustment;

    public String timespan;
    private Document doc;
    private Element rootElement;
    
    public boolean workPlace;
 

    protected Hashtable<IncidentType, IncidentList> incidentlists;
    protected IncidentList allIncidents;

    /**
     * Public constructor for a Transcript.
     * @param fxmlFile the file to be parsed.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Transcript(File fxmlFile)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fxmlFile);
        doc.getDocumentElement().normalize();
        
        incidentlists = new Hashtable();
        for (IncidentType t : IncidentType.values()) {
            incidentlists.put(t, new IncidentList(t));
        }
        allIncidents = new IncidentList(IncidentType.UNDEFINED);
    }

    private boolean parseFileName() {
        
        String[] parts = name.split("_");
        if (parts.length != 6) return false;
        participant = parts[0];
        group = participant.replaceAll("([A-Z]*)(\\d)*", "$1");
        competence = parts[1];
        version = parts[2];
        sourcetextname = parts[3];
        experiment = parts[4];
        return true;
        
    }


    /**
     * Check whether the document is well-formatted.
     * @return the string containing the error messages. If there are no errors
     * return the empty string.
     */
    public String check(int type, int start, int end) {

        String error = "";
        String message = "";
        rootElement = doc.getDocumentElement();
        if (!rootElement.getNodeName().equalsIgnoreCase("document")) {
            error = error.concat("Missing document tag.\n");
        }

        if (rootElement.hasAttribute("name")) {
            name = rootElement.getAttribute("name");
        } else {
            error = error.concat("Missing name attribute in document tag.\n");
        }
        if (!parseFileName()) message = message.concat("Invalid name attribute in document tag. \n");
        

        NodeList nl = doc.getElementsByTagName("recording");

        if (nl.getLength() != 1) {
            error = error.concat("No recording information available or multiple recording elements.\n");
        } else {
            Element recording = (Element) nl.item(0);
            if (recording.hasAttribute("startTransProcess")) {
                startProcess = convertToSeconds(recording.getAttribute("startTransProcess").trim());
            } else {
                startProcess = 0;
                error = error.concat("Missing startTransProcess attribute in recording tag.\n");
            }
            
            if (recording.hasAttribute("endTransProcess")) {
                endProcess = convertToSeconds(recording.getAttribute("endTransProcess").trim());
            } else {
                endProcess = 0;
                error = error.concat("Missing endTransProcess attribute in recording tag.\n");
            }

            if (recording.hasAttribute("startRevision")) {
                String attr = recording.getAttribute("startRevision").trim();
                if (attr.isEmpty()) {
                    //@TODO: Notify PERSON!!!
                    message = message.concat("No revision phase");
                    startRevision = endProcess;
                } else {
                    startRevision = convertToSeconds(attr);
                }
            } else {
                startRevision = 0;
                error = error.concat("Missing startRevision attribute in recording tag.\n");
            }
            if (recording.hasAttribute("transProcessComplete")) {
                complete = recording.getAttribute("transProcessComplete").trim();
            } else {
                message = message.concat("Missing transProcessComplete attribute in recording tag.\n");
            }
            if (recording.hasAttribute("KSLavailable")) {
                kslavailable = recording.getAttribute("KSLavailable").trim();
            } else {
                message = message.concat("Missing KSLavailable attribute in recording tag.\n");
            }
            if (recording.hasAttribute("ETavailable")) {
                etavailable = recording.getAttribute("ETavailable").trim();
            } else {
                message = message.concat("Missing ETavailable attribute in recording tag.\n");
            }
            if (recording.hasAttribute("ETquality")) {
                etquality = recording.getAttribute("ETquality").trim();
            } else {
                message = message.concat("Missing ETquality attribute in recording tag.\n");
            }
            
            
            if (recording.hasAttribute("direction")) {
                direction = recording.getAttribute("direction").trim();
            } else {
                message = message.concat("Missing direction attribute in recording tag.\n");
            }
            
            if (recording.hasAttribute("concurrentVisibilitySTTT")) {
                concurrentVisibility = recording.getAttribute("concurrentVisibilitySTTT").trim();
            } else {
                message = message.concat("Missing concurrentVisibilitySTTT attribute in recording tag.\n");
            }
        }

        startDrafting = endProcess;
        // Find startDrafting (i.e. first write occurence)
        NodeList incidentList = doc.getElementsByTagName("incident");
        for (int i = 0; i < incidentList.getLength(); i++) {
            Element e = (Element) incidentList.item(i);
            if (e.hasAttribute("type") && e.getAttribute("type").equalsIgnoreCase("writes")) {
                if (e.hasAttribute("start")) {
                    float time = convertToSeconds(e.getAttribute("start"));
                    if (time > startProcess && time < startDrafting) {
                        startDrafting = time - 1;
                    }
                }
            }
        }

        System.out.println("Start process: " + startProcess);
        System.out.println("Start drafting: " + startDrafting);
        System.out.println("Start revision: " + startRevision);
        System.out.println("End process: " + endProcess);

        if (type == 0) { // partial
            timespan = "Partial Process";
            startAdjustment = startProcess + start;
            endAdjustment = startProcess + end;
        } else if (type == 1) { // complete
            timespan = "Complete Process";
            startAdjustment = startProcess;
            endAdjustment = endProcess;
        } else if (type == 2) { // orientation
            timespan = "Orientation Phase";
            startAdjustment = startProcess;
            endAdjustment = startDrafting;
        } else if (type == 3) { // drafting
            timespan = "Drafting Phase";
            startAdjustment = startDrafting;
            endAdjustment = startRevision;
        } else if (type == 4) { // revision
            timespan = "Revision Phase";
            startAdjustment = startRevision;
            endAdjustment = endProcess;
        }

        if (endAdjustment > endProcess) {
            endAdjustment = endProcess;
        }
        lengthProcess = endProcess - startProcess;
        lengthAdjustment = endAdjustment - startAdjustment;
        return error;
    }

    /**
     * Converts a time in a given String of the format HH:MM:SS to seconds 
     * (an int).
     * 
     */
    private float convertToSeconds(String timestring) {

        String[] times = timestring.trim().split(":");
        float time = Integer.parseInt(times[0]) * 360
                + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2]);

        return time;
    }

    /**
     * Converts a time (String) of the format HH:MM:SS to seconds and adjusts
     * it to the adjustment start time.
     * @param timestring a String representing a time (HH:MM:SS)
     * @return an int representing the time in seconds
     */
    public float convertToReal(String timestring) {
        float time = convertToSeconds(timestring);
        return time - startAdjustment;
    }

    /**
     * Parses to whole document.
     */
    public void parse() {

        // Find all incident tags and handle them accordingly
        NodeList incidentList = doc.getElementsByTagName("incident");
        for (int i = 0; i < incidentList.getLength(); i++) {
            if (incidentList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) incidentList.item(i);
                
                if (e.hasAttribute("type") && e.getAttribute("type").equalsIgnoreCase("writes")) {
                    Element possible_typo = (Element) incidentList.item(i + 1);
                    Element possible_write = (Element) incidentList.item(i + 2);
                    boolean typo = possible_typo != null && (possible_typo.hasAttribute("subtype") && possible_typo.getAttribute("subtype").equalsIgnoreCase("typo"))
                            || (possible_typo.hasAttribute("type") && possible_typo.getAttribute("type").equalsIgnoreCase("autocorrects"));
                    boolean writes2 = possible_write != null && possible_write.hasAttribute("type") && possible_write.getAttribute("type").equalsIgnoreCase("writes");
                    if (typo && writes2) {
                        handle_two_step_write(e, possible_write);
                        handleIncident(possible_typo);
                        i++;
                        i++;

                    } else {
                        handleIncident(e);
                    }

                } else if (e.hasAttribute("type")) {
                    handleIncident(e);
                }
            }
        }
    }

    private void handleIncident(Element e) {

        Incident i = new Incident(e, this);
        if (i.start < 0 || i.end > lengthAdjustment) {
            return;
        }
        
        IncidentList mainlist = (IncidentList)incidentlists.get(i.group);
        mainlist.add(i);
        IncidentList sublist = (IncidentList)incidentlists.get(i.subgroup);
        sublist.add(i);
        allIncidents.add(i);
    }

    private void handle_two_step_write(Element e, Element possible_write) {

        Incident i = new Incident(e, this);

        float end = 0;
        if (possible_write.hasAttribute("end")) {
            end = convertToReal(possible_write.getAttribute("end"));
        } else if (possible_write.hasAttribute("start")) { // if the write does not have an end-tag, use the start tag
            end = convertToReal(possible_write.getAttribute("start"));
        }

        if (i.validTimes) {

            if (end == 0 || end < i.start) {
                end = i.start;
            }
            i.end = end;
            i.subgroup = IncidentType.PR_WRITETYPO;
        }

        IncidentList mainlist = (IncidentList)incidentlists.get(i.group);
        mainlist.add(i);
        IncidentList sublist = (IncidentList)incidentlists.get(i.subgroup);
        sublist.add(i);
        allIncidents.add(i);

    }

}
