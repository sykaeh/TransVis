package transvis;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;

/**
 * Class for all revision incidents
 *
 * @author Sybil Ehrensberger
 */
public class Revision extends BaseIncident {

    public final static List<IncidentType> subgroups = Arrays.asList(IncidentType.R_DELETES, IncidentType.R_INSERTS,
            IncidentType.R_PASTES, IncidentType.R_MOVESTO, IncidentType.R_UNDOES);

    public String before;
    public String after;
    public String subsubtype;

    public IncidentType revisionType;

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Revision(Transcript t, Attributes atts) {
        super(t, atts);

        before = atts.getValue("before");
        after = atts.getValue("after");
        subsubtype = atts.getValue("subsubtype");

        classify();

    }

    private void classify() {

        group = IncidentType.REVISION;

        if (i_subtype.equals("revision")) {
            revisionType = IncidentType.R_REVISION;
        } else if (i_subtype.equals("revision2")) {
            revisionType = IncidentType.R_REVISION2;
        }

        if (i_type.contains("deletes")) {
            subgroup = IncidentType.R_DELETES;
        } else if (i_type.contains("inserts")) {
            subgroup = IncidentType.R_INSERTS;
        } else if (i_type.contains("pastes")) {
            subgroup = IncidentType.R_PASTES;
        } else if (i_type.contains("moves to")) {
            subgroup = IncidentType.R_MOVESTO;
        } else if (i_type.contains("undoes")) {
            subgroup = IncidentType.R_UNDOES;
        }

    }

}
