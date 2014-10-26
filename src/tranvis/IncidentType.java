package tranvis;

/**
 * @author sehrensberger
 */
public enum IncidentType {

    // Main types
    CONSULTATION("Consultations"),
    INTERRUPTION("Interruptions"),
    PAUSE("Pauses"),
    SOURCETEXT("Sourcetext"),
    TYPOS("Typos"),
    PRODUCTION("Production"),
    REVISION("Revisions"),
    UNDEFINED("Undefined"),
    NOSUBGROUP(""),

    // Subtypes consultations
    C_SEARCHENG("Search engines"),
    C_ENCYCLOPEDIA("Online encyclopedias"),
    C_DICTIONARY("Online dictionaries"),
    C_PORTALS("Portals"),
    C_TERMBANKS("Termbanks"),
    C_WFCONTEXT("Workflow context"),
    C_WFSTYLEGUIDE("Workflow style guide"),
    C_WFGLOSSARY("Workflow glossary"),
    C_WFPARALLELTEXT("Workflow parallel text"),
    C_CONCORDANCE("Concordance"),
    C_OTHER("Other Resources"),

    // Subtypes interruption
    I_PRIVATEMAIL("Private mail"), // <incident type="interrupts" subtype="privatemail">
    I_JOBMAIL("Job mail"),         // <incident type="interrupts" subtype="jobmail">
    I_INTERNET("Internet"),        // <incident type="interrupts" subtype="internet">
    I_TASK("Task"),                // <incident type="interrupts" subtype="task">
    I_WORKFLOW("Workflow"),        // <incident type="interrupts" subtype="workflow">
    I_BREAK("Break"),              // <incident type="interrupts" subtype="break">

    // Subtypes pause
    P_SIMPLE("No screen activity"),    // <incident type="pause">
    P_CONSULTS("Looks at resource"),   // <incident type="ILPause" subtype="consults">
    P_READSTASK("Looks at task"),      // <incident type="ILPause" subtype="readsTask">
    P_READSST("Looks at ST"),          // <incident type="ILPause" subtype="readsST">
    P_READSTT("Looks at TT"),          // <incident type="ILPause" subtype="readsTT">
    P_READSSTTT("Looks at ST+TT"),     // <incident type="ILPause" subtype="readsST+TT">
    P_UNCLEAR("Focus unclear"),        // <incident type="ILPause" subtype="unclear">


    // Subtype production
    PR_WRITESHORT("Writes no time or < 5 seconds"),    // <incident type="writes" >
    PR_WRITETYPO("Writes with a typo"),
    PR_WRITELONG("Writes ≥ 5 seconds"),

    MATCH("Match"),     // <incident type="accepts" subtype="match">

    // Subtypes revision
    R_DELETES("Deletes"),         // <incident type="deletes" subtype="(revision|revision2)">
    R_INSERTS("Inserts"),         // <incident type="inserts" subtype="(revision|revision2)">
    //R_CUTS ("Cuts"),               // <incident type="cuts" subtype="(revision|revision2)">
    R_PASTES("Pastes"),           // <incident type="pastes" subtype="(revision|revision2)">
    R_UNDOES("Undoes"),           // <incident type="undoes" subtype="(revision|revision2)">
    //R_MOVESFROM ("Moves from"),    // <incident type="moves from" subtype="(revision|revision2)">
    R_MOVESTO("Moves to"),        // <incident type="moves to" subtype="(revision|revision2)">

    R_REVISION("Revision"),        // <incident type="(revision)" subtype="revision">
    R_REVISION2("Revision 2");     // <incident type="(revision)" subtype="revision2">

    public final String descr;

    IncidentType(String descr) {
        this.descr = descr;
    }

}
