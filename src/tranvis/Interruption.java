package tranvis;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ehrensbe on 25/10/14.
 */
public class Interruption extends BaseIncident {

    public final static List<IncidentType> SUBGROUPS = Arrays.asList(IncidentType.I_BREAK, IncidentType.I_WORKFLOW,
            IncidentType.I_TASK, IncidentType.I_INTERNET, IncidentType.I_JOBMAIL, IncidentType.I_PRIVATEMAIL);

    public Interruption(Transcript t, Attributes atts) {
        super(t, atts);
        classify();
    }

    private void classify() {

        group = IncidentType.INTERRUPTION;

        // TODO: Change to switch statement
        if (i_subtype.equals("privatemail")) {
            subgroup = IncidentType.I_PRIVATEMAIL;
        } else if (i_subtype.equals("jobmail")) {
            subgroup = IncidentType.I_JOBMAIL;
        } else if (i_subtype.equals("internet")) {
            subgroup = IncidentType.I_INTERNET;
        } else if (i_subtype.equals("task")) {
            subgroup = IncidentType.I_TASK;
        } else if (i_subtype.equals("workflow")) {
            subgroup = IncidentType.I_WORKFLOW;
        } else if (i_subtype.equals("break")) {
            subgroup = IncidentType.I_BREAK;
        } else if (i_subtype.equals("jobmail")) {
            subgroup = IncidentType.I_JOBMAIL;
        }
    }
}
