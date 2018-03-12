/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 8;
    private Random random = new Random();
    private ArrayList<String> wordList;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private HashSet<String> wordSet;

    public AnagramDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader (wordListStream));
        this.lettersToWord = new HashMap();
        this.wordSet = new HashSet();
        this.wordList = new ArrayList();
        while (true) {
            String line = in.readLine();
            if (line != null) {
                String word = line.trim();
                this.wordSet.add(word);
                this.wordList.add(word);
                String sortedWord = sortLetters(word);
                ArrayList<String> words;
                if (this.lettersToWord.containsKey(sortedWord)) {
                    words = (ArrayList) this.lettersToWord.get(sortedWord);
                    words.add(word);
                    this.lettersToWord.put(sortedWord, words);
                } else {
                    words = new ArrayList();
                    words.add(word);
                    this.lettersToWord.put(sortedWord, words);
                }
            } else {
                return;
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        if (!word.contains(base) && this.wordSet.contains(word)) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList();
        for (char i = 'a'; i <= 'z'; i = (char) (i + 1)) {
            String sortedWord = sortLetters(word + i);
            if (this.lettersToWord.containsKey(sortedWord)) {
                ArrayList<String> words = (ArrayList) this.lettersToWord.get(sortedWord);
                for (int w = 0; w < words.size(); w++) {
                    String wrd = (String) words.get(w);
                    if (isGoodWord(wrd, word)) {
                        result.add(wrd);
                    }
                }
            }
        }
        return result;
    }


    public String pickGoodStarterWord() {
        String pickedWord;
        while (true) {
            pickedWord = (String) this.wordList.get(this.random.nextInt(this.wordList.size()));
            Log.v("pickGoodStarterWord", pickedWord);
            if (pickedWord.length() >= DEFAULT_WORD_LENGTH && getAnagramsWithOneMoreLetter(pickedWord).size() >= MIN_NUM_ANAGRAMS) {
                return pickedWord;
            }
        }
    }

    public String sortLetters(String word) {
        char[] characters = word.toCharArray();
        Arrays.sort(characters);
        return new String(characters);
    }
}
