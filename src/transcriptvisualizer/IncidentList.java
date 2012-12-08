package transcriptvisualizer;

import java.util.LinkedList;
import java.util.List;

/**
 * IncidentList class for the TranVis tool.
 * @author Sybil Ehrensberger
 * @version 0.2
 */
public class IncidentList {

    private float maxLength;
    private float minLength;
    private float avgLength;
    private float totalTime;
    public int noElementsTime;
    private boolean validTimes;
    private boolean calculated;

    public List<Incident> elements;
    public IncidentType group;
    

    public IncidentList(IncidentType main) {
        group = main;
        elements = new LinkedList<Incident>();
        calculated = false;

    }

    public void add(Incident i) {
        elements.add(i);
        if (i.validTimes) {
            validTimes = true;
        }

    }

    public int noElements() {
        return elements.size();
    }

    public IncidentList sublist(IncidentType revType) {
        IncidentList sublist = new IncidentList(group);
        for (Incident i : elements) {
            if (i.revisionType == revType) {
                sublist.add(i);
            }
        }
        
        return sublist;
    }
    
    /**
     * Calculate all of the relevant statistics pertaining to this IncidentList and
     * save them in the appropriate fields.
     */
    public void getStats() {

        calculated = true;
        maxLength = -1;
        totalTime = -1;
        avgLength = -1;
        noElementsTime = 0;

        if (!validTimes) {
            minLength = -1;
        } else {
            minLength = elements.get(0).length();
            totalTime = 0;
            for (Incident i : elements) {
                if (i.validTimes) {
                    minLength = Math.min(minLength, i.length());
                    maxLength = Math.max(maxLength, i.length());
                    totalTime += i.length();
                    noElementsTime++;
                }
            }
            avgLength = totalTime / noElementsTime;
        }

    }

    /**
     * Return the longest time span of this tag.
     * @return the longest time span of this tag.
     */
    public int getMaxLength() {
        if (!calculated) getStats();
        return (int)maxLength;
    }

    /**
     * Return the shortest time span of this tag
     * @return the shortest time span of this tag
     */
    public int getMinLength() {
        if (!calculated) getStats();
        return (int)minLength;
    }

    /**
     * Return the average time span for this tag
     * @return the average time span
     */
    public float getAvgLength() {
        if (!calculated) getStats();
        return avgLength;
    }

    /**
     * Return the total amount of time of this tag
     * @return the total amount of time
     */
    public int getTotalTime() {
        if (!calculated) getStats();
        return (int)totalTime;
    }
    
    public static IncidentList addLists(IncidentList l1, IncidentList l2) {
        IncidentList new_list = new IncidentList(IncidentType.UNDEFINED);
        new_list.elements.addAll(l1.elements);
        new_list.elements.addAll(l2.elements);
        new_list.validTimes = l1.validTimes || l2.validTimes;
        
        return new_list;
    }

}
