package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultsSummary {

    int fullyMatching;

    int collapsed;

    int partiallyMatching;

    int totalMatching;

    boolean estimatedCounts;

    int numRanks;

    int currStart;

    int currEnd;

    int prevStart;

    int nextStart;

    public int getFullyMatching() {
        return fullyMatching;
    }

    public void setFullyMatching(int fullyMatching) {
        this.fullyMatching = fullyMatching;
    }

    public int getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(int collapsed) {
        this.collapsed = collapsed;
    }

    public int getPartiallyMatching() {
        return partiallyMatching;
    }

    public void setPartiallyMatching(int partiallyMatching) {
        this.partiallyMatching = partiallyMatching;
    }

    public int getTotalMatching() {
        return totalMatching;
    }

    public void setTotalMatching(int totalMatching) {
        this.totalMatching = totalMatching;
    }

    public boolean isEstimatedCounts() {
        return estimatedCounts;
    }

    public void setEstimatedCounts(boolean estimatedCounts) {
        this.estimatedCounts = estimatedCounts;
    }

    public int getNumRanks() {
        return numRanks;
    }

    public void setNumRanks(int numRanks) {
        this.numRanks = numRanks;
    }

    public int getCurrStart() {
        return currStart;
    }

    public void setCurrStart(int currStart) {
        this.currStart = currStart;
    }

    public int getCurrEnd() {
        return currEnd;
    }

    public void setCurrEnd(int currEnd) {
        this.currEnd = currEnd;
    }

    public int getPrevStart() {
        return prevStart;
    }

    public void setPrevStart(int prevStart) {
        this.prevStart = prevStart;
    }

    public int getNextStart() {
        return nextStart;
    }

    public void setNextStart(int nextStart) {
        this.nextStart = nextStart;
    }
}