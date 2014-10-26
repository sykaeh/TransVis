package tranvis;

import org.xml.sax.Attributes;

/**
 * Class for all production incidents
 *
 * @author Sybil Ehrensberger
 */
public class Production extends BaseIncident {

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Production(Transcript t, Attributes atts) {
        super(t, atts);

        classify();
        setDraftingPhase();

    }

    private void setDraftingPhase() {

        if (start > transcript.startSelection)
            if (transcript.startDrafting == null || start < transcript.startDrafting)
                transcript.startDrafting = start - 1;
    }

    private void classify() {

        group = IncidentType.PRODUCTION;

        if (validTimes && length() >= 5) {
            subgroup = IncidentType.PR_WRITELONG;
        } else {
            subgroup = IncidentType.PR_WRITESHORT;
        }

        // TODO: Is accept a production or a separate group?
        if (i_type.equals("accepts") && i_subtype.equals("match")) {
            subgroup = IncidentType.MATCH;

        }
    }
}
