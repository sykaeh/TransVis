# TransVis - a translation process visualizer tool

TransVis is a program I developed to analyze transcripts of translation processes. The program takes XML documents and 
processes all of the so-called "incidents", i.e. pauses, consultations, deletions, insertions, writes, and visualizes 
them in several graphs so that they can be further analyzed. The program also calculates some descriptive statistics 
such as number of pauses or number of consultations and presents them in an Excel document. The program is written 
entirely in Java. It was developed for the Capturing Translation Processes project at the Zurich University of 
Applied Science (ZHAW).


## Getting started

TransVis is a self-contained application using the Java JDK 1.8. Download the latest packaged jar file from
downloads/

## External libraries used

- [Java Excel API](http://jexcelapi.sourceforge.net/) (`jxl.*`) LGPL v2
- [JFreeChart](http://www.jfree.org/jfreechart/) (`org.jfree.*`) LGPL
- Swing Framework (`org.jdesktop.application.*`, `javax.swing.*`)

## Authors

[Sybil Ehrensberger](http://sybil-ehrensberger.com)

## License

Copyright (c) 2014 Sybil Ehrensberger. See the LICENSE file for license rights and limitations (MIT).
