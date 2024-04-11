package com.taskhub.project.core.board.helper;

import java.util.List;
import java.util.Set;

public class CommonFunction {
    public static boolean isTheSameList(List list1, List list2) {
        var set1 = Set.of(list1);
        var set2 = Set.of(list2);

        return set1.equals(set2);
    }


    public static boolean isSameOrder(List list1, List list2) {
        var minLength = Math.min(list1.size(), list2.size());

        list1 = list1.subList(0, minLength);
        list2 = list2.subList(0, minLength);

        return list1.equals(list2);
    }
}
