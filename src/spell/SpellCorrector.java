package spell;

import java.io.IOException;
import java.io.File;
import java.util.*;

public class SpellCorrector implements ISpellCorrector {
    private Trie dictionary;

    public SpellCorrector() {
        this.dictionary = new Trie();

    }

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        File file = new File(dictionaryFileName);
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()) {
            String nextWord = scanner.next();
            dictionary.add(nextWord);
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        ArrayList<String> editDistanceOneWords = new ArrayList<>();
        ArrayList<String> editDistanceTwoWords = new ArrayList<>();
        inputWord = inputWord.toLowerCase();

        // check if the word is already in the dictionary
        if(dictionary.find(inputWord) != null) {
            return inputWord;
        }

        // generate edit distance 1 words inputWord
        generateEditDistanceWords(editDistanceOneWords, inputWord);

        // find most similar word and return it
        String mostSimilarWord = findMostSimilarWord(editDistanceOneWords);
        if(mostSimilarWord != null) {
            return mostSimilarWord;
        }

        // if there is no matching word form editDistanceOne words, try editDistanceTwo words
        for(int i = 0; i < editDistanceOneWords.size(); i++) {
            generateEditDistanceWords(editDistanceTwoWords, editDistanceOneWords.get(i));
        }

        // find most similar word and return it
        mostSimilarWord = findMostSimilarWord(editDistanceTwoWords);
        if(mostSimilarWord != null) {
            return mostSimilarWord;
        } else {    // there are no matches
            return null;
        }
    }

    /**
     * Populates an ArrayList that is passed in with all edit distance words given an inputWord
     * @param editDistanceWords the array that is populated with all words
     * @param inputWord the word from which edit distance words are populated
     */
    private void generateEditDistanceWords(ArrayList<String> editDistanceWords, String inputWord) {
        StringBuilder wordBuilder = new StringBuilder();

        // Deletion - n words (“bird” - “ird”, “brd”, “bid”, and “bir”)
        wordBuilder.setLength(0);
        for(int letterToRemove = 0; letterToRemove < inputWord.length(); letterToRemove++) {
            for(int letterIndex = 0; letterIndex < inputWord.length(); letterIndex++) {
                if(letterToRemove != letterIndex) {
                    wordBuilder.append(inputWord.charAt(letterIndex));
                }
            }
            editDistanceWords.add(wordBuilder.toString());
            wordBuilder.setLength(0);
        }

        // Transposition - n-1 words (“house” - “ohuse”, “huose”, “hosue”)
        wordBuilder.setLength(0);
        for(int swapIndex = 0; swapIndex < inputWord.length() - 1; swapIndex++) {
            for(int letterIndex = 0; letterIndex < inputWord.length(); letterIndex++) {
                if(letterIndex == swapIndex) {
                    wordBuilder.append(inputWord.charAt(swapIndex + 1));
                } else if(letterIndex == swapIndex + 1) {
                    wordBuilder.append(inputWord.charAt(swapIndex));
                } else {
                    wordBuilder.append(inputWord.charAt(letterIndex));
                }
            }
            editDistanceWords.add(wordBuilder.toString());
            wordBuilder.setLength(0);
        }

        // Alteration - 25n words (“top” - “aop”, “bop”, …, “zop”, “tap”, “tbp”, …, “tzp”, “toa”, “tob”, …, and “toz”)
        wordBuilder.setLength(0);
        for(int alterIndex = 0; alterIndex < inputWord.length(); alterIndex++) {
            wordBuilder.append(inputWord);
            for(int letterIndex = 0; letterIndex < 26; letterIndex++) {
                if(inputWord.charAt(alterIndex) != (char)('a' + letterIndex)) {
                    wordBuilder.setCharAt(alterIndex, (char)('a' + letterIndex));

                    editDistanceWords.add(wordBuilder.toString());
                }
            }
            wordBuilder.setLength(0);
        }

        // Insertion - 26(n+1) words (“ask” - “aask”, “bask”, “cask”, … “zask”, “aask”, “absk”, “acsk”, … “azsk”, “asak”, “asbk”, “asck”, … “aszk”, “aska”, “askb”, “askc”, … “askz”
        wordBuilder.setLength(0);
        for(int insertIndex = 0; insertIndex <= inputWord.length(); insertIndex++) {
            for(int letterIndex = 0; letterIndex < 26; letterIndex++) {
                wordBuilder.append(inputWord);
                wordBuilder.insert(insertIndex, (char)('a' + letterIndex));

                editDistanceWords.add(wordBuilder.toString());
                wordBuilder.setLength(0);
            }
        }
    }


    private String findMostSimilarWord(ArrayList<String> editDistanceWords) {
        HashMap<String, Integer> matchedWords = new HashMap<>();
        ArrayList<String> highestCountWords = new ArrayList<>();

        // map all matched words to their count
        for(int i = 0; i < editDistanceWords.size(); i++) {
            if(dictionary.find(editDistanceWords.get(i)) != null) {
                matchedWords.put(editDistanceWords.get(i), dictionary.find(editDistanceWords.get(i)).getValue());
            }
        }

        // there are no matched words to begin with, return null
        if(matchedWords.size() == 0) {
            return null;
        }

        // add all words that tie for the highest count
        Integer maxValue = Collections.max(matchedWords.values());
        for(Map.Entry<String, Integer> entry : matchedWords.entrySet()) {   // FIXME TAKE NOTES ON THIS
            if(entry.getValue().equals(maxValue)) {
                highestCountWords.add(entry.getKey());
            }
        }

        // if there is 1 word - it wins
        if(highestCountWords.size() == 1) {
            return highestCountWords.get(0);
        }

        // if there are greater than 1 word, return alphabetically the highest one
        if(highestCountWords.size() > 1) {
            return Collections.min(highestCountWords);
        }

        // else there are no matching words
        return null;
    }
}
