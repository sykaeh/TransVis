package com.sybil_ehrensberger.transvis;

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

    private BaseIncident firstWrite = null;
    private BaseIncident intermediateTypo = null;

    private String current_phase = "OP";

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
                try {
                    transcript.setName(attributes.getValue("name"));
                } catch (TranscriptError transcriptError) {
                    GeneralView.fatalError(transcriptError.getMessage());
                    transcriptError.printStackTrace();
                }
                break;
            case "incident":

                // TODO: Check two-step write with unit tests!
                if (attributes.getValue("type").equalsIgnoreCase("writes") ||
                        attributes.getValue("type").equalsIgnoreCase("accepts")) {

                    if (firstWrite == null && intermediateTypo == null) {
                        // no previously stored write or typo
                        System.out.println("first write I");
                        firstWrite = new TargetText(transcript, attributes);
                    } else if (firstWrite != null && intermediateTypo == null) {
                        // write without intermediate typo => regular write
                        incident = firstWrite;
                        System.out.println("assigning write to incident");
                        firstWrite = new TargetText(transcript, attributes);

                    } else if (firstWrite == null && intermediateTypo != null) {

                        System.out.println("first write III");
                        // typo without previous write
                        intermediateTypo = null;
                        firstWrite = new TargetText(transcript, attributes);

                    } else if (firstWrite != null && intermediateTypo != null) {

                        System.out.println("true two-step write");
                        // we have a two-step write
                        BaseIncident secondWrite = new TargetText(transcript, attributes);
                        firstWrite.end = secondWrite.end;

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
                try {
                    transcript.addRecording(recording);
                } catch (TranscriptError transcriptError) {
                    GeneralView.fatalError(transcriptError.getMessage());
                    transcriptError.printStackTrace();
                }
                break;

            case "incident":
                if (incident == null)
                    System.out.println("ERROR:\t\tIncident is null...");
                else {
                    System.out.println(incident.group);
                    assignPhase(incident);
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

        }

        b.phase = current_phase;
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
                    transcript.error("Unclassified incident: " + incident_type + ", no subtype");
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
                        transcript.error("Unclassified incident: " + incident_type + ", " + incident_subtype);
                        break;
                }

                break;

            default:
                transcript.error("Unclassified incident: " + incident_type + ", " + incident_subtype);
                break;

        }

        // reset two-step handles if we are not dealing with a typo
        if (incident == null || incident.group != IncidentType.TYPOS) {
            intermediateTypo = null;
            if (firstWrite != null) {
                assignPhase(firstWrite);
                transcript.incidents.add(firstWrite);
            }
            firstWrite = null;
        }

    }

}
