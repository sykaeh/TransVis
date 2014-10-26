package tranvis;

import org.xml.sax.Attributes;

/**
 * Created by ehrensbe on 25/10/14.
 */
public class SourceText extends BaseIncident {

    public SourceText(Transcript t, Attributes atts) {
        super(t, atts);

        group = IncidentType.SOURCETEXT;
        subgroup = IncidentType.NOSUBGROUP;

    }
}
