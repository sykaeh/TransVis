package com.sybil_ehrensberger.transvis;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.List;

/**
 * Class for all consult incidents {@literal <incident type="consults"></incident>}
 */
public class Consultation extends BaseIncident {

    public String source;
    public String item;

    /**
     *
     */
    public final static List<IncidentType> SUBGROUP_GENERAL =
            Arrays.asList(IncidentType.C_SEARCHENG,
            IncidentType.C_ENCYCLOPEDIA, IncidentType.C_DICTIONARY,
            IncidentType.C_PORTALS, IncidentType.C_OTHER);

    /**
     *
     */
    public final static List<IncidentType> SUBGROUP_WORKPLACE =
            Arrays.asList(IncidentType.C_TERMBANKS, IncidentType.C_WFCONTEXT,
            IncidentType.C_WFSTYLEGUIDE, IncidentType.C_WFGLOSSARY,
                    IncidentType.C_WFPARALLELTEXT, IncidentType.C_CONCORDANCE);

    /**
     * Public constructor
     *
     * @param t the transcript this incident belongs to
     * @param atts attributes for this incident tag
     */
    public Consultation(Transcript t, Attributes atts) {
        super(t, atts);

        source = atts.getValue("src");
        item = atts.getValue("item");
        classify();
    }

    private void classify() {

        group = IncidentType.CONSULTATION;

        if (source.contains("collins") || source.contains("colins")) {
            source = "collins";
        }
        if (source.contains("google")) {
            source = "google";
        }
        if (source.contains("leo")) {
            source = "leo";
        }
        if (source.contains("wikipedia")) {
            source = "wikipedia";
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

        if (searcheng.contains(source)) {
            classified = true;
            subgroup = IncidentType.C_SEARCHENG;
        }
        if (encyclopedias.contains(source)) {
            classified = true;
            subgroup = IncidentType.C_ENCYCLOPEDIA;
        }
        if (dictionaries.contains(source)) {
            classified = true;
            subgroup = IncidentType.C_DICTIONARY;
        }
        if (portals.contains(source)) {
            classified = true;
            subgroup = IncidentType.C_PORTALS;
        }
        if (termbanks.contains(source)) {
            classified = true;
            subgroup = IncidentType.C_TERMBANKS;
        }

        if (source.contains("workflowcontext")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFCONTEXT;
        }
        if (source.contains("workflowstyleguide")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFSTYLEGUIDE;
        }
        if (source.contains("workflowglossary")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFGLOSSARY;
        }
        if (source.contains("workflowparalleltext")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_WFPARALLELTEXT;
        }
        if (source.contains("condordance")) {
            classified = true;
            transcript.workPlace = true;
            subgroup = IncidentType.C_CONCORDANCE;
        }

        if (!classified) {
            subgroup = IncidentType.C_OTHER;
        }


    }
}
