package com.sybil_ehrensberger.transvis;

import org.xml.sax.SAXException;

public class TranscriptParseError extends SAXException {

    public TranscriptParseError(String msg) {
        super(msg);
    }
}
