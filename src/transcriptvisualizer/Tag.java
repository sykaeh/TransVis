package transcriptvisualizer;

import java.util.LinkedList;
import java.util.List;

/**
 * Tag class for the TranVis tool.
 * @author Sybil Ehrensberger
 * @version 0.2
 */
public class Tag {
    
    public List<Integer[]> times;
    
    public List<Integer> lengths;
    
    public int firstPart;
    
    public String type;
    
    public String name;
    
    public String subtype;
    
    public String[] src;
    
    private int maxLength;
    private int minLength;
    private float avgLength;
    private int totalTime;
    
    private int firstTime;
    private int lastTime;
    
    /**
     * Public constructor
     * @param t the type of the tag 
     * @param subt the subtype of the tag
     * @param sources list of sources (only for the 'consults' tag)
     */
    public Tag(String n, String t, String subt, String[] sources) {
        
        type = t;
        name = n;
        subtype = subt;
        src = sources;
        times = new LinkedList<Integer[]>();
        lengths = new LinkedList<Integer>();
        
    }
    
    /**
     * Public constructor
     * @param t type of tag
     * @param subt subtype of tag
     */
    public Tag(String n, String t, String subt) {
        
        type = t;
        name = n;
        subtype = subt;
        src = new String[] {};
        times = new LinkedList<Integer[]>();
        lengths = new LinkedList<Integer>();
        
    }
    
    /**
     * Calculate all of the relevant statistics pertaining to this Tag and
     * save them in the appropriate fields.
     */
    public void getStats() {
        
        maxLength = -1;
        totalTime = -1;
        avgLength = -1;
        
        if (lengths.isEmpty()) {
            minLength = -1;
        } else {
            minLength = lengths.get(0);
            totalTime = 0;
            for (int i=0; i < lengths.size(); i++) {
                int curr = lengths.get(i);
                if (curr < minLength) {
                    minLength = curr;
                }
                if (curr > maxLength) {
                    maxLength = curr;
                }
                totalTime += curr;
            }
            avgLength = totalTime / lengths.size();
        } 
        
        lastTime = 0;
        
        
        if (! times.isEmpty()) {
            firstTime = times.get(0)[0];
        } else {
            firstTime = -1;
            return;
        }
        
        for (int i=0; i < times.size(); i++) {
            Integer[] time = times.get(i);
            if (time[0] < firstTime) {
                firstTime = time[0];
            }
            if (time[1] > lastTime) {
                lastTime = time[1];
            }
        }
    }
        
    /**
     * Return the longest time span of this tag.
     * @return the longest time span of this tag.
     */
    public int getMaxLength() {
        return maxLength;
    }
    
    /**
     * Return the shortest time span of this tag
     * @return the shortest time span of this tag
     */
    public int getMinLength() {
        return minLength;
    }
    
    /**
     * Return the average time span for this tag
     * @return the average time span
     */
    public float getAvgLength() {
        return avgLength;
    }
    
    /**
     * Return the total amount of time of this tag
     * @return the total amount of time
     */
    public int getTotalTime() {
        return totalTime;
    }
    
    /**
     * Get the total amount of times this tag was found
     * @return the total amount of times this tag was found
     */
    public int getTotalNum() {
        return lengths.size();
    }
    
    /**
     * Return the total amount of times this tag was found in the first
     * period (specified by the statistics)
     * @return the total amount of times this tag was found in the first period
     */
    public int getFirstNum() {
        return firstPart;
    }
    
    /**
     * Return the time of the first occurrence of this tag
     * @return the time of the first tag
     */
    public int getFirstTime() {
        return firstTime;
    }
    
    /**
     * Return the time of the last occurrence of this tag
     * @return the time of the last tag
     */
    public int getLastTime() {
        return lastTime;
    }
}
