package com.project.qa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Evgeny.Gurinovich on 09.06.2016.
 */
public class Randomizer {
    private static final int STR_LENGTH = 6;
    private static final String ALPHABET = "qwertyuiopasdfghjklzxcvbnmQWERYUIOPASDFGHJLZXCVBNM1234567890";
    private static Logger LOGGER = LoggerFactory.getLogger(Randomizer.class);

    public static String getRandomSequence(){
        String result = "";
        Random r = new Random();
        for (int i = 0; i < STR_LENGTH; i++) {
            result += ALPHABET.charAt(r.nextInt(ALPHABET.length()));
        }
        return result;
    }

    public static <T> T getRandomValueFromList(List<T> list){
        if(list == null || list.size() == 0){
            throw new RuntimeException("Gotten list is empty!");
        }
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public static <T> List<T> getRandomValuesFromList(List<T> list, int amount) {
        if(list == null || list.size() == 0){
            throw new RuntimeException("Gotten list is empty!");
        } else if (amount >= list.size()) {
            throw new RuntimeException("Amount of requested values should be less than list size");
        }
        List<T> resultList = new ArrayList<>();
        Random random = new Random();
        int iteratorIndex = 0;
        while (iteratorIndex < amount) {
            T value = list.get(random.nextInt(list.size()));
            if (!resultList.contains(value)) {
                resultList.add(value);
                iteratorIndex++;
            }
        }
        return resultList;
    }

    public static <T, N> Map<T, N> getRandomValueFromMapList(List<Map<T, N>> list){
        if(list == null || list.size() == 0){
            throw new RuntimeException("Getting data from db: there is no data matching to condition set");
        }
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public static Integer generateRandomNumber(int start, int end){
        Integer result;
        if (start == end) {
            result = start;
        } else {
            Random random = new Random();
            result =  random.nextInt(end - start) + start;
        }
        LOGGER.info("Generated number is: " + result);
        return result;
    }

    public static String generateAutomationName() {
        return getRandomSequence() + "Automation";
    }
}
