package tranvis;

import org.xml.sax.Attributes;

/**
 * Created by ehrensbe on 25/10/14.
 */
public class Typo extends BaseIncident {

    public Typo(Transcript t, Attributes atts) {
        super(t, atts);

        group = IncidentType.TYPOS;
        subgroup = IncidentType.NOSUBGROUP;
    }
}
