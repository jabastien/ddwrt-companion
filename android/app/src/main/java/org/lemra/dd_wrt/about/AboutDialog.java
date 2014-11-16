/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Armel S.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.lemra.dd_wrt.about;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import org.lemra.dd_wrt.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutDialog extends Dialog {

    private static Context mContext = null;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
        super.setTitle("About");
    }

    public static String readRawTextFile(int id) {
        final InputStream inputStream = mContext.getResources().openRawResource(id);
        final InputStreamReader in = new InputStreamReader(inputStream);
        final BufferedReader buf = new BufferedReader(in);
        String line;
        final StringBuilder text = new StringBuilder();
        try {
            while ((line = buf.readLine()) != null)
                text.append(line);
        } catch (final IOException e) {
            try {
                inputStream.close();
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        return text.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);

        TextView tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.legal)));
        tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);

        tv = (TextView) findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);
    }

}