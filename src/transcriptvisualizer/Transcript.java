package transcriptvisualizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    public int startTransProcess;
    public int endTransProcess;
    /* transProcessComplete in recording tag */
    public boolean complete;
    
    /* concurrentVisibilitySTTT in recording tag */
    public boolean concurrentVisibility;
    
    /* first write tag  (in real time) */
    public int startDrafting;
    /* startRevision in recording tag (in real time) */
    public int startRevision;

    // OLD STUFF
    
    /* The total length of the process. */
    public int lengthProcess;
    /* The total length of the time span we're looking at. */
    public int lengthAdjustment;

    /* Adjustment time in real time*/
    public int startAdjustment;
    /* last time to show in real time */
    public int endAdjustment;
    /* startTransProcess in recording tag (in real time) */
    public int startProcess;

    /* endTransProcess in recording tag (in real time) */
    public int endProcess;
    public String timespan;
    


    private Document doc;
    private Element rootElement;
    
    private String[] revisiontypes;
    List<IncidentList> interrupts = new LinkedList<IncidentList>();
    List<IncidentList> consults = new LinkedList<IncidentList>();
    List<IncidentList> consults2 = new LinkedList<IncidentList>();
    List<IncidentList> revisions = new LinkedList<IncidentList>();
    List<IncidentList> productions = new LinkedList<IncidentList>(); // writes_short, writes_long, writes_typo, accepts
    List<IncidentList> pauses = new LinkedList<IncidentList>(); // IL pauses and "normal" pauses
    IncidentList typos;
    IncidentList sourcetext;

    /**
     * Public constructor for a Transcript.
     * @param fxmlFile the file to be parsed.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Transcript(File fxmlFile)
            throws ParserConfigurationException, SAXException, IOException {

        initializeTagList();
        revisiontypes = new String[]{"deletes", "inserts", "pastes",
            "moves to", "undoes", "autocorrects"};

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fxmlFile);
        doc.getDocumentElement().normalize();

    }

    private void parseFileName() {
        
    }
    
    /**
     * Initializes all of the tags.
     */
    private void initializeTagList() {

        String[] searcheng = new String[]{"google", "yahoo", "bing"};
        String[] encyclopedias = new String[]{"wikipedia", "britannica", "encyclopedia"};
        String[] dictionaries = new String[]{"dict.cc", "leo", "pons", "collins", "colins",
            "larousse", "duden", "reverso", "thefreedictionary", "langenscheidt",
            "langenscheit", "linguee", "linguee.com"};
        String[] portals = new String[]{"term-minator", "admin", "Europa",
            "Canada", "ourlanguages", "usa"};
        String[] termbanks = new String[]{"iate.europa.eu",
            "web4.zhaw.ch/terminologie/online", "EUR-lex", "Eurovoc",
            "franceterme"};

        String t = "interrupts";
        interrupts.add(new IncidentList("Private mail", t, "privatemail"));
        interrupts.add(new IncidentList("Job mail", t, "jobmail"));
        interrupts.add(new IncidentList("Internet", t, "internet"));
        interrupts.add(new IncidentList("Task", t, "task"));
        interrupts.add(new IncidentList("Workflow", t, "workflow"));
        interrupts.add(new IncidentList("Break", t, "break"));

        String c = "consults";
        consults.add(new IncidentList("Search engines", c, "Search engines", searcheng));
        consults.add(new IncidentList("Online encyclopedias", c, "Online encyclopedias", encyclopedias));
        consults.add(new IncidentList("Online Dictionaries", c, "Online Dictionaries", dictionaries));
        consults.add(new IncidentList("Portals", c, "Portals", portals));
        consults2.add(new IncidentList("Termbanks", c, "Termbanks", termbanks));
        consults2.add(new IncidentList("Workflow context", c, "Workflow context", new String[]{"workflowcontext"}));
        consults2.add(new IncidentList("Workflow style guide", c, "Workflow style guide", new String[]{"workflowstyleguide"}));
        consults2.add(new IncidentList("Workflow glossary", c, "Workflow glossary", new String[]{"workflowglossary"}));
        consults2.add(new IncidentList("Workflow parallel text", c, "Workflow parallel text", new String[]{"workflowparalleltext"}));
        consults2.add(new IncidentList("Concordance", c, "Concordance", new String[]{"concordance"}));
        consults.add(new IncidentList("Other Resources", c, "Other Resources", new String[]{}));

        revisions.add(new IncidentList("deletes", "deletes", "revision"));
        revisions.add(new IncidentList("deletes", "deletes", "revision2"));
        revisions.add(new IncidentList("inserts", "inserts", "revision"));
        revisions.add(new IncidentList("inserts", "inserts", "revision2"));
        revisions.add(new IncidentList("pastes", "pastes", "revision"));
        revisions.add(new IncidentList("pastes", "pastes", "revision2"));
        revisions.add(new IncidentList("moves to", "moves to", "revision"));
        revisions.add(new IncidentList("moves to", "moves to", "revision2"));
        revisions.add(new IncidentList("undoes", "undoes", "revision"));
        revisions.add(new IncidentList("undoes", "undoes", "revision2"));

        productions.add(new IncidentList("Accepts", "accepts", "match")); // 3
        productions.add(new IncidentList("Writes (with typo)", "writes", "with typo")); // 2
        productions.add(new IncidentList("Writes (with time)", "writes", "with time"));  // 1
        productions.add(new IncidentList("Writes (no time)", "writes", "no time")); // 0

        typos = new IncidentList("Typos", "*", "Typo");
        sourcetext = new IncidentList("ST", "*", "ST");

        String p = "ILpause";
        pauses.add(new IncidentList("No screen activity", "pause", "Simple"));
        pauses.add(new IncidentList("Looks at resource", p, "Consults"));
        pauses.add(new IncidentList("Looks at task", p, "ReadsTask"));
        pauses.add(new IncidentList("Looks at ST", p, "ReadsST"));
        pauses.add(new IncidentList("Looks at TT", p, "ReadsTT"));
        pauses.add(new IncidentList("Looks at ST+TT", p, "ReadsST+TT"));
        pauses.add(new IncidentList("Focus unclear", p, "Unclear"));

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
            error = error.concat("Missing document-Tag.\n");
        }

        if (rootElement.hasAttribute("name")) {
            name = rootElement.getAttribute("name");
        } else {
            error = error.concat("Missing name attribute in document tag.\n");
        }

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
                complete = recording.getAttribute("transProcessComplete").trim().equalsIgnoreCase("yes");
            } else {
                error = error.concat("Missing transProcessComplete attribute in recording tag.\n");
            }
            if (recording.hasAttribute("concurrentVisibilitySTTT")) {
                concurrentVisibility = recording.getAttribute("concurrentVisibilitySTTT").trim().equalsIgnoreCase("yes");
            } else {
                error = error.concat("Missing concurrentVisibilitySTTT attribute in recording tag.\n");
            }
            if (recording.hasAttribute("direction")) {
                direction = recording.getAttribute("direction").trim();
            } else {
                error = error.concat("Missing direction attribute in recording tag.\n");
            }
        }

        startDrafting = endProcess;
        // Find startDrafting (i.e. first write occurence)
        NodeList incidentList = doc.getElementsByTagName("incident");
        for (int i = 0; i < incidentList.getLength(); i++) {
            Element e = (Element) incidentList.item(i);
            if (e.hasAttribute("type") && e.getAttribute("type").equalsIgnoreCase("writes")) {
                if (e.hasAttribute("start")) {
                    int time = convertToSeconds(e.getAttribute("start"));
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
    private int convertToSeconds(String timestring) {

        String[] times = timestring.trim().split(":");
        int time = Integer.parseInt(times[0]) * 360
                + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2]);

        return time;
    }

    /**
     * Converts a time (String) of the format HH:MM:SS to seconds and adjusts
     * it to the adjustment start time.
     * @param timestring a String representing a time (HH:MM:SS)
     * @return an int representing the time in seconds
     */
    public int convertToReal(String timestring) {
        int time = convertToSeconds(timestring);
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

                } else {
                    handleIncident(e);
                }
            }
        }
    }

    private void handleIncident(Element e) {
        int start = 0;
        int end = 0;

        if (e.hasAttribute("start")) {
            start = convertToReal(e.getAttribute("start")); // adjusted times
        } else {
            start = 0;
        }

        if (e.hasAttribute("end")) {
            end = convertToReal(e.getAttribute("end")); // adjusted times
        }
        if (end == 0 || end < start) {
            end = start;
        }

        Integer[] times = {start, end};
        int length = end - start;

        if (start >= 0 && end <= lengthAdjustment && e.hasAttribute("type")) {
            String type = e.getAttribute("type").trim();
            if (type.equalsIgnoreCase("consults")) {
                handleConsults(e, times, length);
            } else if (type.equalsIgnoreCase("interrupts")) {
                handleInterrupts(e, times, length);
            } else if (Arrays.asList(revisiontypes).contains(type)) {
                handleRevisions(e, times);
            } else if (type.equalsIgnoreCase("pause") || type.equalsIgnoreCase("ILpause")) {
                handlePauses(e, times, length);
            } else if (type.equalsIgnoreCase("writes") || type.equalsIgnoreCase("accepts")) {
                handleWrite(e, times, length);
            }
        }
    }

    private void handle_two_step_write(Element e, Element possible_write) {

        int start = 0;
        int end = 0;

        if (e.hasAttribute("start")) {
            start = convertToReal(e.getAttribute("start")); // adjusted times
        } else {
            start = 0;
        }

        if (possible_write.hasAttribute("end")) {
            end = convertToReal(possible_write.getAttribute("end"));
        } else if (possible_write.hasAttribute("start")) { // if the write does not have an end-tag, use the start tag
            end = convertToReal(possible_write.getAttribute("start"));
        }
        if (end == 0 || end < start) {
            end = start;
        }

        Integer[] times = {start, end};
        int length = end - start;

        productions.get(2).times.add(times);
        productions.get(2).lengths.add(length);

    }

    private void handleConsults(Element e, Integer[] times, int l) {

        String source = e.getAttribute("src").toLowerCase();

        if (source.contains("collins") || source.contains("colins")) {
            source = "collins";
        }
        if (source.contains("google")) {
            source = "google";
        }
        if (source.contains("leo")) {
            source = "leo";
        }
        if (source.contains("wikipedia")) {
            source = "wikipedia";
        }

        boolean classified = false;

        for (IncidentList t : consults) {
            for (String item : t.src) {
                if (source.contains(item)) {
                    classified = true;
                    t.times.add(times);
                    if (l > 0) {
                        t.lengths.add(l);
                    }

                }
            }
        }

        for (IncidentList t : consults2) {
            for (String item : t.src) {
                if (source.contains(item)) {
                    classified = true;
                    t.times.add(times);
                    if (l > 0) {
                        t.lengths.add(l);
                    }

                }
            }
        }

        if (!classified) {
            IncidentList o = consults.get(consults.size() - 1); // get the Other Resources IncidentList
            o.times.add(times);
            if (l > 0) {
                o.lengths.add(l);
            }
        }

    }

    private void handleRevisions(Element e, Integer[] times) {

        int length = times[1] - times[0];

        String type = e.getAttribute("type");

        String subtype = "revision";
        if (e.hasAttribute("subtype")) {
            subtype = e.getAttribute("subtype");
        }

        if (subtype.equalsIgnoreCase("typo") || type.equalsIgnoreCase("autocorrects")) {
            typos.times.add(times);
            if (length > 0) {
                typos.lengths.add(length);
            }
        } else if (subtype.equalsIgnoreCase("ST")) {
            sourcetext.times.add(times);
            if (length > 0) {
                sourcetext.lengths.add(length);
            }
        } else {

            for (IncidentList t : revisions) {
                if (t.type.equalsIgnoreCase(type) && t.subtype.equalsIgnoreCase(subtype)) {
                    t.times.add(times);
                    if (length > 0) {
                        t.lengths.add(length);
                    }
                }
            }
        }
    }

    private void handleInterrupts(Element e, Integer[] times, int l) {

        String attr = e.getAttribute("subtype");

        for (int i = 0; i < interrupts.size(); i++) {
            if (attr.equalsIgnoreCase(interrupts.get(i).subtype)) {
                interrupts.get(i).times.add(times);
                if (l > 0) {
                    interrupts.get(i).lengths.add(l);
                }
            }
        }

    }

    private void handlePauses(Element e, Integer[] times, int l) {

        String attr = e.getAttribute("type");

        if (attr.equalsIgnoreCase("pause")) {
            for (IncidentList t : pauses) {
                if (t.type.equalsIgnoreCase("pause")) {
                    t.times.add(times);
                    if (l > 0) {
                        t.lengths.add(l);
                    }
                }
            }
        }

        if (attr.equalsIgnoreCase("ILpause") && e.hasAttribute("subtype")) {
            String type = e.getAttribute("subtype");
            for (IncidentList t : pauses) {
                if (t.subtype.equalsIgnoreCase(type)) {
                    t.times.add(times);
                    if (l > 0) {
                        t.lengths.add(l);
                    }
                }
            }
        }
    }

    private void handleWrite(Element e, Integer[] times, int length) {

        if (e.getAttribute("type").equalsIgnoreCase("accepts")
                && e.hasAttribute("subtype")
                && e.getAttribute("subtype").equalsIgnoreCase("match")) {
            productions.get(3).times.add(times);
            productions.get(3).lengths.add(length);
        }


        if (e.getAttribute("type").equalsIgnoreCase("writes")) {
            if (length > 0) {
                productions.get(1).times.add(times);
                productions.get(1).lengths.add(length);

            } else {
                productions.get(0).times.add(times);
                productions.get(0).lengths.add(length);
            }
        }

    }
}
