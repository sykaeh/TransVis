package transcriptvisualizer;

import org.w3c.dom.Element;

/**
 *
 * @author sehrensberger
 */
public class Incident {

    public IncidentType group;
    public IncidentType subgroup;
    public int start;
    public int end;
    public int length;
    public boolean validTimes;
    public String type;
    public String subtype;
    public String subsubtype;
    // only for IncidentType.CONSULT
    public String item;
    public String src;
    // only for IncidentType.REVISION
    public String before;
    public String after;
    private Element tag;

    public Incident(Element e, Transcript t) {

        tag = e;

        if (e.hasAttribute("start")) {
            start = t.convertToReal(e.getAttribute("start")); // adjusted times
        } else {
            validTimes = false;
        }

        if (e.hasAttribute("end")) {
            end = t.convertToReal(e.getAttribute("end")); // adjusted times
        } else if (validTimes) {
            end = start;
        }

        if (validTimes && end < start) {
            end = start;
        }

        if (e.hasAttribute("type")) {
            type = e.getAttribute("type").toLowerCase().trim();
        }

        if (e.hasAttribute("subtype")) {
            subtype = e.getAttribute("subtype").toLowerCase().trim();
        }

        if (e.hasAttribute("item")) {
            item = e.getAttribute("item").toLowerCase().trim();
        }

        if (e.hasAttribute("src")) {
            src = e.getAttribute("src").toLowerCase().trim();
        }

        if (e.hasAttribute("before")) {
            before = e.getAttribute("before").toLowerCase().trim();
        }

        if (e.hasAttribute("after")) {
            after = e.getAttribute("after").toLowerCase().trim();
        }

    }

    private void classify() {

        if (type.equals("consults")) {
            
        } else if (type.equals("interrupts")) {
            group = IncidentType.INTERRUPTION;
            // @TODO: classify subgroup
        } else if (type.equals("pause")) {
            group = IncidentType.PAUSE;
            subgroup = IncidentType.SIMPLE;
        } else if (type.equals("ilpause")) {
            group = IncidentType.PAUSE;
            if (subtype.equals("readstask")) {
                subgroup = IncidentType.READSTASK;
            }
            // @TODO: classify subgroup
        } else {
            group = IncidentType.UNDEFINED;
            subgroup = IncidentType.UNDEFINED;
        }
    }
}
