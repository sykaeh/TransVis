package transcriptvisualizer;

/**
 *
 * @author sehrensberger
 */
public enum IncidentType {
    
    // Main types
    CONSULTATION,
    INTERRUPTION,
    PAUSE,
    SOURCETEXT,     
    TYPOS,
    PRODUCTION,
    REVISION,
    UNDEFINED,
    
    // Subtypes interruption
    PRIVATEMAIL,    // <incident type="interrupts" subtype="privatemail">
    JOBMAIL,        // <incident type="interrupts" subtype="jobmail">
    INTERNET,       // <incident type="interrupts" subtype="internet">
    TASK,           // <incident type="interrupts" subtype="task">
    WORKFLOW,       // <incident type="interrupts" subtype="workflow">
    BREAK,          // <incident type="interrupts" subtype="break">
    
    // Subtypes pause
    SIMPLE,         // <incident type="pause">
    CONSULTS,       // <incident type="ILPause" subtype="consults">
    READSTASK,      // <incident type="ILPause" subtype="readsTask">
    READSST,        // <incident type="ILPause" subtype="readsST">
    READSTT,        // <incident type="ILPause" subtype="readsTT">
    READSSTTT,      // <incident type="ILPause" subtype="readsST+TT">
    UNCLEAR,        // <incident type="ILPause" subtype="unclear">
    
    // Subtypes typos
    REVISIONTYPOS,  // <incident type="(see revision)" subtype="typos">
    AUTOCORRECTS,   // <incident type="autocorrects" subtype="(anything)">
    
    // Subtype production
    
    // Subtypes revision
    DELETES1,       // <incident type="deletes" subtype="revision">
    INSERTS1,       // <incident type="inserts" subtype="revision">
    CUTS1,          // <incident type="cuts" subtype="revision">
    PASTES1,        // <incident type="pastes" subtype="revision">
    UNDOES1,        // <incident type="undoes" subtype="revision">
    MOVESFROM1,     // <incident type="moves from" subtype="revision">
    MOVESTO1,       // <incident type="moves to" subtype="revision">
    
    DELETES2,       // <incident type="deletes" subtype="revision2">
    INSERTS2,       // <incident type="inserts" subtype="revision2">
    CUTS2,          // <incident type="cuts" subtype="revision2">
    PASTES2,        // <incident type="pastes" subtype="revision2">
    UNDOES2,        // <incident type="undoes" subtype="revision2">
    MOVESFROM2,     // <incident type="moves from" subtype="revision2">
    MOVESTO2,       // <incident type="moves to" subtype="revision2">
     
}
