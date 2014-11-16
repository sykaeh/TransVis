package com.sybil_ehrensberger.transvis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Class to handle the XML parsing
 *
 * @author Sybil Ehrensberger
 */
class TranscriptHandler extends DefaultHandler {

    private BaseIncident incident = null;
    private Transcript transcript;
    private Recording recording = null;

    private BaseIncident firstWrite = null;
    private BaseIncident intermediateTypo = null;

    private String current_phase = "OP";
    private String last_time;

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

        switch (qName) {
            case "document":
                transcript.setName(attributes.getValue("name"));
                break;
            case "incident":

                System.out.println("Classifying incident: " + attributes.getValue("type") + ", " + attributes.getValue("subtype"));

                if (attributes.getValue("end") != null)
                    last_time = attributes.getValue("end");
                else if (attributes.getValue("start") != null)
                    last_time = attributes.getValue("start");

                // TODO: Check two-step write with unit tests!
                if (attributes.getValue("type").equalsIgnoreCase("writes") ||
                        attributes.getValue("type").equalsIgnoreCase("accepts")) {

                    if (firstWrite == null && intermediateTypo == null) {
                        // no previously stored write or typo
                        //System.out.println("first write I");
                        firstWrite = new TargetText(transcript, attributes);

                    } else if (firstWrite != null && intermediateTypo == null) {
                        // write without intermediate typo => regular write
                        incident = firstWrite;
                        //System.out.println("assigning write to incident");
                        firstWrite = new TargetText(transcript, attributes);

                    } else if (firstWrite == null && intermediateTypo != null) {

                        //System.out.println("first write III");
                        // typo without previous write
                        intermediateTypo = null;
                        firstWrite = new TargetText(transcript, attributes);

                    } else if (firstWrite != null && intermediateTypo != null) {

                        //System.out.println("true two-step write");
                        // we have a two-step write
                        BaseIncident secondWrite = new TargetText(transcript, attributes);
                        firstWrite.end = secondWrite.end;
                        firstWrite.subgroup = IncidentType.T_WRITETYPO;

                        incident = firstWrite;

                        intermediateTypo = null;
                        firstWrite = null;

                    }

                } else // if it is not a target text production
                    classifyIncident(attributes);
                break;
            case "recording":
                recording = new Recording(attributes);
                break;
            case "u":
                // ignore utterances
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
                if (incident == null)
                    System.out.println("ERROR:\t\tIncident is null...");
                else {
                    assignPhase(incident);
                    transcript.incidents.add(incident);

                    incident = null;
                }
                break;
        }
    }

    /**
     * Assign a phase to the given incident.
     *
     * @param b the incident in question
     */
    private void assignPhase(BaseIncident b) {

        if (transcript.startDrafting == null) {
            current_phase = "OP";
            b.phase = current_phase;
            return;
        }

        if (b.validTimes) {

            if (b.start < transcript.startDrafting) {
                current_phase = "OP";
            } else if (b.start > transcript.startDrafting && b.start < transcript.startRevision) {
                current_phase = "DP";
            } else {
                current_phase = "RP";
            }

        } else {
            b.guessTimes(last_time);
        }

        b.phase = current_phase;

    }

    /**
     * Classify the current incident based on the given attributes atts.
     *
     * @param atts the attributes to determine the classification
     */
    private void classifyIncident(Attributes atts) {

        boolean reset = true;
        String incident_type = atts.getValue("type");
        String incident_subtype = atts.getValue("subtype");

        if (incident_type == null) {
            unknown_incident(atts);
            reset();
            return;
        }

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

            case "autocorrects":
                incident = new Typo(transcript, atts);
                intermediateTypo = incident;
                reset = false;
                break;

            case "sic": // ignore all sics
                reset = false;
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
                    unknown_incident(atts);
                    reset();
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
                        intermediateTypo = incident;
                        reset = false;
                        break;
                    default:
                        unknown_incident(atts);
                        break;
                }

                break;

            default:
                unknown_incident(atts);
                break;

        }

        // reset two-step handles if we are not dealing with a typo
        if (reset) reset();

    }

    /**
     * Reset the saved first write and intermediate typos.
     */
    private void reset() {

        intermediateTypo = null;
        if (firstWrite != null) {
            assignPhase(firstWrite);
            transcript.incidents.add(firstWrite);
        }
        firstWrite = null;
    }

    /**
     * Send error message that the incident with the given attributes could not be classified.
     *
     * @param atts the attributes for the incident
     */
    private void unknown_incident(Attributes atts) {

        String start_time = atts.getValue("start");
        String error_text = "Unclassified incident";

        if (start_time != null)
            error_text += " (with start: " + start_time + ")";

        error_text += ": type=\"" + atts.getValue("type") + "\", ";

        String subtype = atts.getValue("subtype");
        if (subtype != null)
            error_text += "subtype=\"" + subtype + "\"";
        else
            error_text += "no subtype";

        transcript.error(error_text);
    }

}
