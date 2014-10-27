package transvis;

import org.xml.sax.Attributes;

/**
 * Class for all typo incidents
 */
public class Typo extends BaseIncident {

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Typo(Transcript t, Attributes atts) {
        super(t, atts);

        group = IncidentType.TYPOS;
        subgroup = IncidentType.NOSUBGROUP;
    }
}
