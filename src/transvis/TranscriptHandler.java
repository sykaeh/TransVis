package transvis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Class to handle the XML parsing
 *
 * @author Sybil Ehrensberger
 *
 */
class TranscriptHandler extends DefaultHandler {

    private String content = null;
    private BaseIncident incident = null;
    private Transcript transcript;
    private Recording recording = null;

    /**
     * Public constructor
     *
     * @param t the current transcript
     */
    public TranscriptHandler(Transcript t) {
        super();
        transcript = t;
    }

    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {

        System.out.println("qname: " + qName);
        switch (qName) {
            case "document":
                transcript.setName(attributes.getValue("name"));
                break;
            case "incident":
                classifyIncident(attributes);
                break;
            case "recording":
                recording = new Recording(attributes);
                break;
            case "u":

                // TODO: handle utterances
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        switch (qName) {
            case "recording":
                transcript.addRecording(recording);
                break;

            case "incident":
                // TODO: handle content!
                if (incident == null)
                    System.out.println("ERROR:\t\tIncident is null...");
                else {
                    transcript.incidents.add(incident);
                    incident = null;
                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }


    private void classifyIncident(Attributes atts) {

        String incident_type = atts.getValue("type");
        String incident_subtype = atts.getValue("subtype");

        System.out.println("Classifying incident: " + incident_type + ", " + incident_subtype);

        if (incident_type == null)
            return;

        incident_type = incident_type.toLowerCase();

        switch (incident_type) {
            case "consults":
                incident = new Consultation(transcript, atts);
                break;
            case "pause":
            case "ilpause":
                incident = new Pause(transcript, atts);
                break;
            case "interrupts":
                incident = new Interruption(transcript, atts);
                break;

            case "writes":
                incident = new TargetText(transcript, atts);
                break;
            case "accepts":
                if (incident_subtype.equalsIgnoreCase("match"))
                    incident = new TargetText(transcript, atts);
                else
                    Transcript.error("Unclassified incident: " + incident_type + ", " + incident_subtype);
                break;

            case "autocorrects":
                incident = new Typo(transcript, atts);
                break;

            case "sic": // ignore all sics
                break;

            case "changes view":
            case "changes language setting":
            case "formats":
                incident = new Setting(transcript, atts);
                break;

            case "deletes":
            case "cuts":
            case "inserts":
            case "pastes":
            case "moves from":
            case "moves to":
            case "undoes":

                if (incident_subtype == null) {
                    Transcript.error("Unclassified incident: " + incident_type + ", no subtype");
                    return;
                }

                incident_subtype = incident_subtype.toLowerCase();

                switch (incident_subtype) {
                    case "st":
                        incident = new SourceText(transcript, atts);
                        break;
                    case "revision":
                    case "revision2":
                        incident = new Revision(transcript, atts);
                        break;
                    case "typo":
                        incident = new Typo(transcript, atts);
                        break;
                    default:
                        Transcript.error("Unclassified incident: " + incident_type + ", " + incident_subtype);
                        break;
                }

                break;

            default:
                Transcript.error("Unclassified incident: " + incident_type + ", " + incident_subtype);
                break;

        }

    }

}
