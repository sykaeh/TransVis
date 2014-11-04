package transvis;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;

/**
 * Class for all interruption incidents
 */
public class Interruption extends BaseIncident {

    /**
     * List of all IncidentTypes that belong to Interruptions
     */
    public final static List<IncidentType> SUBGROUPS =
            Arrays.asList(IncidentType.I_BREAK, IncidentType.I_WORKFLOW,
                          IncidentType.I_TASK, IncidentType.I_INTERNET,
                          IncidentType.I_JOBMAIL, IncidentType.I_PRIVATEMAIL);

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Interruption(Transcript t, Attributes atts) {
        super(t, atts);
        classify();
    }

    private void classify() {

        group = IncidentType.INTERRUPTION;
        switch (i_subtype) {
            case "privatemail":
                subgroup = IncidentType.I_PRIVATEMAIL; break;
            case "jobmail":
                subgroup = IncidentType.I_JOBMAIL; break;
            case "internet":
                subgroup = IncidentType.I_INTERNET; break;
            case "task":
                subgroup = IncidentType.I_TASK; break;
            case "workflow":
                subgroup = IncidentType.I_WORKFLOW; break;
            case "break":
                subgroup = IncidentType.I_BREAK; break;
            default:
                break;

        }
    }
}
