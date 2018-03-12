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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.engedu.anagrams.BuildConfig;
import com.google.engedu.anagrams.R;
import java.io.IOException;
import java.util.ArrayList;

public class AnagramsActivity extends AppCompatActivity {
    public static final String START_MESSAGE = "Find as many words as possible that can be formed by adding one letter to <big>%s</big> (but that do not contain the substring %s).";
    private ArrayList<String> anagrams;
    private String currentWord;
    private AnagramDictionary dictionary;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anagrams);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        try {
            this.dictionary = new AnagramDictionary(getAssets().open("words.txt"));
        } catch (IOException e) {
            Toast.makeText(this, "Could not load dictionary", 1).show();
        }
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setRawInputType(1);
        editText.setImeOptions(2);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                AnagramsActivity.this.processWord(editText);
                return true;
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Thanks for using - Play Again", 1).show();
    }

    private void processWord(EditText editText) {
        TextView resultView = (TextView) findViewById(R.id.resultView);
        String word = editText.getText().toString().trim().toLowerCase();
        if (word.length() != 0) {
            String color = "#cc0029";
            if (this.dictionary.isGoodWord(word, this.currentWord) && this.anagrams.contains(word)) {
                this.anagrams.remove(word);
                color = "#00aa29";
            } else {
                word = "X " + word;
            }
            resultView.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", new Object[]{color, word})));
            editText.setText(BuildConfig.FLAVOR);
            ((FloatingActionButton) findViewById(R.id.fab)).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_anagrams, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean defaultAction(View view) {
        TextView gameStatus = (TextView) findViewById(R.id.gameStatusView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView resultView = (TextView) findViewById(R.id.resultView);
        if (this.currentWord == null) {
            this.currentWord = this.dictionary.pickGoodStarterWord();
            this.anagrams = this.dictionary.getAnagramsWithOneMoreLetter(this.currentWord);
            gameStatus.setText(Html.fromHtml(String.format(START_MESSAGE, new Object[]{this.currentWord.toUpperCase(), this.currentWord})));
            //fab.setImageResource(R.drawable);
            fab.hide();
            resultView.setText(BuildConfig.FLAVOR);
            editText.setText(BuildConfig.FLAVOR);
            editText.setEnabled(true);
            editText.requestFocus();
            ((InputMethodManager) getSystemService("input_method")).showSoftInput(editText, 1);
        } else {
            editText.setText(this.currentWord);
            editText.setEnabled(false);
            //fab.setImageResource(17301540);
            this.currentWord = null;
            resultView.append(TextUtils.join("\n", this.anagrams));
            gameStatus.append(" Hit 'Play' to start again");
        }
        return true;
    }
}
