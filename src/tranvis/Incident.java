package tranvis;

import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author sehrensberger
 */
public class Incident {

    public IncidentType group;
    public IncidentType subgroup;
    public IncidentType revisionType;
    public float start;
    public float end;
    
    public boolean validTimes;
    public String type;
    public String subtype;
    
    private Element tag;
    private Transcript transcript;
    
    // only for IncidentType.CONSULT
    public String item;
    public String src;
    
    // only for IncidentType.REVISION
    public String before;
    public String after;
    public String subsubtype;
    

    public Incident(Element e, Transcript t) {

        tag = e;
        transcript = t;

        start = 0;
        end = 0;
        validTimes = true;
        
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
        } else {
            subtype = "";
        }

        if (e.hasAttribute("item")) {
            item = e.getAttribute("item").toLowerCase().trim();
        } else {
            item = "";
        }

        if (e.hasAttribute("src")) {
            src = e.getAttribute("src").toLowerCase().trim();
        } else {
            src = "";
        }

        if (e.hasAttribute("before")) {
            before = e.getAttribute("before").toLowerCase().trim();
        } else {
            before = "";
        }

        if (e.hasAttribute("after")) {
            after = e.getAttribute("after").toLowerCase().trim();
        } else {
            after = "";
        }
        
        if (e.hasAttribute("subsubtype")) {
            subsubtype = e.getAttribute("subsubtype").toLowerCase().trim();
        } else {
            subsubtype = "";
        }
        
        classify();

    }

    public float length() {
        return end - start;
    }
    
    
    private void classify() {

        group = IncidentType.UNDEFINED;
        subgroup = IncidentType.UNDEFINED;
        revisionType = IncidentType.UNDEFINED;
        
        if (type.equals("consults")) {
            group = IncidentType.CONSULTATION;
            classifyConsults();

        } else if (type.equals("interrupts")) {
            group = IncidentType.INTERRUPTION;

            if (subtype.equals("privatemail")) {
                subgroup = IncidentType.I_PRIVATEMAIL;
            } else if (subtype.equals("jobmail")) {
                subgroup = IncidentType.I_JOBMAIL;
            } else if (subtype.equals("internet")) {
                subgroup = IncidentType.I_INTERNET;
            } else if (subtype.equals("task")) {
                subgroup = IncidentType.I_TASK;
            } else if (subtype.equals("workflow")) {
                subgroup = IncidentType.I_WORKFLOW;
            } else if (subtype.equals("break")) {
                subgroup = IncidentType.I_BREAK;
            } else if (subtype.equals("jobmail")) {
                subgroup = IncidentType.I_JOBMAIL;
            }

        } else if (type.equals("pause")) {
            group = IncidentType.PAUSE;
            subgroup = IncidentType.P_SIMPLE;
        } else if (type.equals("ilpause")) {
            group = IncidentType.PAUSE;
            if (subtype.equals("readstask")) {
                subgroup = IncidentType.P_READSTASK;
            } else if (subtype.equals("consults")) {
                subgroup = IncidentType.P_CONSULTS;
            } else if (subtype.equals("readsst")) {
                subgroup = IncidentType.P_READSST;
            } else if (subtype.equals("readstt")) {
                subgroup = IncidentType.P_READSTT;
            } else if (subtype.equals("readsst+tt")) {
                subgroup = IncidentType.P_READSSTTT;
            } else if (subtype.equals("unclear")) {
                subgroup = IncidentType.P_UNCLEAR;
            }

        } else if (subtype.equals("st")) {
            group = IncidentType.SOURCETEXT;
            subgroup = IncidentType.NOSUBGROUP;
            
        } else if (type.equals("autocorrects") || subtype.equals("typo")) {
            group = IncidentType.TYPOS;
            subgroup = IncidentType.NOSUBGROUP;

        } else if (type.equals("writes")) {
            group = IncidentType.PRODUCTION;
            if (validTimes && length() >= 5) {
                subgroup = IncidentType.PR_WRITELONG;
            } else {
                subgroup = IncidentType.PR_WRITESHORT;
            }
        } else if (type.equals("accepts") && subtype.equals("match")) {
            group = IncidentType.MATCH;
            subgroup = IncidentType.NOSUBGROUP;
            
        } else if (subtype.equals("revision")) {
            group = IncidentType.REVISION;
            revisionType = IncidentType.R_REVISION;
            classifyRevisions();
        } else if (subtype.equals("revision2")) {
            group = IncidentType.REVISION;
            revisionType = IncidentType.R_REVISION2;
            classifyRevisions();
        }
    }

    private void classifyConsults() {

        if (src.contains("collins") || src.contains("colins")) {
            src = "collins";
        }
        if (src.contains("google")) {
            src = "google";
        }
        if (src.contains("leo")) {
            src = "leo";
        }
        if (src.contains("wikipedia")) {
            src = "wikipedia";
        }

        List<String> searcheng = Arrays.asList(
                "google", "yahoo", "bing");
        List<String> encyclopedias = Arrays.asList(
                "wikipedia", "britannica", "encyclopedia");
        List<String> dictionaries = Arrays.asList(
                "dict.cc", "leo", "pons", "collins", "colins", "larousse", 
                "duden", "reverso", "thefreedictionary", "langenscheidt",
                "langenscheit", "linguee", "linguee.com");
        List<String> portals = Arrays.asList(
                "term-minator", "admin", "europa",
                "canada", "ourlanguages", "usa");
        List<String> termbanks = Arrays.asList(
                "iate.europa.eu", "web4.zhaw.ch/terminologie/online", 
                "eur-lex", "eurovoc", "franceterme");

        boolean classified = false;

        if (searcheng.contains(src)) {
            classified = true;
            subgroup = IncidentType.C_SEARCHENG;
        }
        if (encyclopedias.contains(src)) {
            classified = true;
            subgroup = IncidentType.C_ENCYCLOPEDIA;
        }
        if (dictionaries.contains(src)) {
            classified = true;
            subgroup = IncidentType.C_DICTIONARY;
        }
        if (portals.contains(src)) {
            classified = true;
            subgroup = IncidentType.C_PORTALS;
        }
        if (termbanks.contains(src)) {
            classified = true;
            subgroup = IncidentType.C_TERMBANKS;
        }

        if (src.contains("workflowcontext")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFCONTEXT;
        }
        if (src.contains("workflowstyleguide")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFSTYLEGUIDE;
        }
        if (src.contains("workflowglossary")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFGLOSSARY;
        }
        if (src.contains("workflowparalleltext")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFPARALLELTEXT;
        }
        if (src.contains("condordance")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_CONCORDANCE;
        }

        if (!classified) {
            subgroup = IncidentType.C_OTHER;
        }


    }

    private void classifyRevisions() {
        
        if (type.contains("deletes")) {
            subgroup = IncidentType.R_DELETES;
        } else if (type.contains("inserts")) {
            subgroup = IncidentType.R_INSERTS;
        } else if (type.contains("pastes")) {
            subgroup = IncidentType.R_PASTES;
        } else if (type.contains("moves to")) {
            subgroup = IncidentType.R_MOVESTO;
        } else if (type.contains("undoes")) {
            subgroup = IncidentType.R_UNDOES;  
        }
        
    }
}
