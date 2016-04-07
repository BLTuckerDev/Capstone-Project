package dev.bltucker.nanodegreecapstone.data;

import java.util.Comparator;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.models.Story;

public class DescendingScoreStoryComparator implements Comparator<Story> {

    @Inject
    public DescendingScoreStoryComparator(){

    }

    @Override
    public int compare(Story lhs, Story rhs) {
        int leftHandScore = lhs != null ? (int) lhs.getScore() : 0;
        int rightHandScore = rhs != null ? (int) rhs.getScore() : 0;

        return rightHandScore - leftHandScore;
    }
}
