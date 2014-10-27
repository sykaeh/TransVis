package transvis;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;

/**
 * Class for all pause incidents.
 *
 * The following tags belong to this class:
 *  - {@literal <incident type="pause"></incident>}
 *  - {@literal <incident type="ILPause"></incident>}
 */
public class Pause extends BaseIncident {

    /**
     * List of the IncidentTypes of all subgroups of the pause incidents
     */
    public final static List<IncidentType> SUBGROUPS = Arrays.asList(
            IncidentType.P_SIMPLE, IncidentType.P_CONSULTS, IncidentType.P_READSTASK, IncidentType.P_READSST,
            IncidentType.P_READSTT, IncidentType.P_READSSTTT, IncidentType.P_UNCLEAR);

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Pause(Transcript t, Attributes atts) {
        super(t, atts);

        classify();
    }

    private void classify() {

        group = IncidentType.PAUSE;

        if (i_type.equals("pause")) {
            subgroup = IncidentType.P_SIMPLE;

        } else {

            switch (i_subtype) {
                case "readstask":
                    subgroup = IncidentType.P_READSTASK;
                    break;
                case "consults":
                    subgroup = IncidentType.P_CONSULTS;
                    break;
                case "readsst":
                    subgroup = IncidentType.P_READSST;
                    break;
                case "readstt":
                    subgroup = IncidentType.P_READSTT;
                    break;
                case "readsst+tt":
                    subgroup = IncidentType.P_READSSTTT;
                    break;
                case "unclear":
                default:
                    subgroup = IncidentType.P_UNCLEAR;
                    break;

            }
        }

    }
}
