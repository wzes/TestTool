package com.tongji.test.util;

import com.tongji.test.model.ItemResult;
import com.tongji.test.model.TotalResult;

import java.util.List;

/**
 * @author Create by xuantang
 * @date on 4/7/18
 */
public class StatisticUtils {

    /**
     *
     * @param itemResults
     * @return
     */
    public static TotalResult evaluate(List<ItemResult> itemResults) {
        if (itemResults == null || itemResults.size() == 0) {
            return new TotalResult();
        }
        TotalResult result = new TotalResult();
        result.setItemCount(itemResults.size());
        int correctCount = 0;
        for (ItemResult itemResult : itemResults) {
            if (itemResult.isCorrect()) {
                correctCount++;
            }
        }
        result.setItemCount(correctCount);
        result.setWrongCount(itemResults.size() - correctCount);
        result.setCorrectRate((double) correctCount / itemResults.size());
        return result;
    }
}
