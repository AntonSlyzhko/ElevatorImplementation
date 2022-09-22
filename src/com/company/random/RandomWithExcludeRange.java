package com.company.random;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class RandomWithExcludeRange extends Random {

    public RandomWithExcludeRange(){
        super();
    }

    public RandomWithExcludeRange(long seed){
        super(seed);
    }


    public int nextInt(int start, int end){
        return start + nextInt(end - start);
    }

    public int nextIntInRangeButExclude(int start, int end, int... excludes) {
        Set<Integer> excludesSet = Arrays.stream(excludes).boxed().collect(Collectors.toSet());
        int result;
        do{
            result = nextInt(start, end);
        } while (excludesSet.contains(result));
        return result;
    }
}
