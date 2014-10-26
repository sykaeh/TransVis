package tranvis;

import org.xml.sax.Attributes;

/**
 * Class for all source text incidents
 */
public class SourceText extends BaseIncident {

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public SourceText(Transcript t, Attributes atts) {
        super(t, atts);

        group = IncidentType.SOURCETEXT;
        subgroup = IncidentType.NOSUBGROUP;

    }
}
