package com.sybil_ehrensberger.transvis;

import org.xml.sax.Attributes;

/**
 * Class for all production incidents
 *
 * @author Sybil Ehrensberger
 */
public class TargetText extends BaseIncident {

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public TargetText(Transcript t, Attributes atts) {
        super(t, atts);

        classify();
        setDraftingPhase();

    }

    private void setDraftingPhase() {

        if (start > 0)
            if (transcript.startDrafting == null || start < transcript.startDrafting)
                transcript.startDrafting = start - 1;
    }

    private void classify() {

        group = IncidentType.TARGETTEXT;

        // TODO: Go over Target text incident classifications

        if (validTimes && length() >= 5) {
            subgroup = IncidentType.T_WRITELONG;
        } else {
            subgroup = IncidentType.T_WRITESHORT;
        }

        if (i_type.equals("accepts") && i_subtype.equals("match")) {
            subgroup = IncidentType.T_MATCH;

        }
    }
}
