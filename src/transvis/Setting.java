package transvis;

import org.xml.sax.Attributes;

/**
 * Class for all settings incidents
 *
 * @author Sybil Ehrensberger
 */
public class Setting extends BaseIncident {

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Setting(Transcript t, Attributes atts) {

        super(t, atts);
        classify();

    }

    private void classify() {

        group = IncidentType.SETTING;
        switch (i_type) {
            case "changes view":
                subgroup = IncidentType.S_VIEWCHANGE;
                break;
            case "changes language setting":
                subgroup = IncidentType.S_LANGUAGE;
                break;
            case "formats":
                subgroup = IncidentType.S_FORMAT;
                break;
            default:
                subgroup = IncidentType.UNDEFINED;
                break;
        }

    }
}
