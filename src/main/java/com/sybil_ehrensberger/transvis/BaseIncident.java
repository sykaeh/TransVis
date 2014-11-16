package com.sybil_ehrensberger.transvis;

import org.xml.sax.Attributes;

/**
 * Base class for all incidents.
 *
 * @author Sybil Ehrensberger
 */
public class BaseIncident {

    /**
     * Start time [in sec] of the incident
     */
    public double start;

    /**
     * End time [in sec] of the incident
     */
    public double end;

    public IncidentType group;
    public IncidentType subgroup;

    public Transcript transcript;

    public String i_type;
    public String i_subtype;
    public String phase;

    public Boolean validTimes = true;

    /**
     * Public constructor
     *
     * @param t    the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public BaseIncident(Transcript t, Attributes atts) {
        transcript = t;

        i_type = atts.getValue("type").toLowerCase();
        i_subtype = atts.getValue("subtype");
        if (i_subtype != null)
            i_subtype = i_subtype.toLowerCase();
        deal_with_times(atts.getValue("start"), atts.getValue("end"));
    }

    /**
     * Calculate the length [in sec] of the incident and return it
     *
     * @return the length of the incident
     */
    public double length() {
        return end - start;
    }

    private void deal_with_times(String s_start, String s_end) {

        try {
            start = transcript.adjustTime(Transcript.convertToSeconds(s_start));
        } catch (NumberFormatException | NullPointerException e) {
            validTimes = false;
            if (s_start == null)
                transcript.error("Missing start time for " + i_type);
            else
                transcript.error("Invalid start time format for " + i_type + " (" + s_start + "): " + e.getMessage());
        }

        try {
            end = transcript.adjustTime(Transcript.convertToSeconds(s_end));
        } catch (NumberFormatException | NullPointerException e) {
            //Transcript.error("Invalid end time format (" + s_end + "): " + e.getMessage());
            end = start;
        }

        if (validTimes && end < start) {
            transcript.error("End time (" + s_end + ") before start time (" + s_end + ")");
            end = start;
        }


    }

    /**
     * Check whether the incident is within the chosen selection. Return true if it is, else false.
     *
     * @return True if the incident it within the chosen selection
     */
    public boolean valid() {

        if (start < 0) { // in the warm up phase, ignore completely
            group = IncidentType.WARMUP;
            return false;
        } else if (start < transcript.startSelection) {
            return false;
        } else if (end > transcript.endSelection) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Assign the last_time as start and end times if there is no start and end time.
     *
     * @param last_time the timestring to set start and end to
     */
    public void guessTimes(String last_time) {

        start = transcript.adjustTime(last_time) + 0.1;
        end = start;
    }


}
