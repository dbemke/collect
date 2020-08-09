/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import org.javarosa.core.model.RangeQuestion;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.views.TrackingTouchSlider;
import org.odk.collect.android.widgets.utilities.RangeWidgetUtils;

import java.math.BigDecimal;

@SuppressLint("ViewConstructor")
public class RangeIntegerWidget extends QuestionWidget implements Slider.OnChangeListener {
    TrackingTouchSlider slider;
    TextView currentValue;

    private int defaultThumbRadius;

    public RangeIntegerWidget(Context context, QuestionDetails prompt) {
        super(context, prompt);
    }

    @Override
    protected View onCreateAnswerView(Context context, FormEntryPrompt prompt, int answerFontSize) {
        RangeQuestion rangeQuestion = (RangeQuestion) getFormEntryPrompt().getQuestion();
        RangeWidgetUtils.RangeWidgetLayoutElements layoutElements = RangeWidgetUtils.setUpLayoutElements(context, prompt);

        slider = layoutElements.getSlider();
        currentValue = layoutElements.getCurrentValue();
        defaultThumbRadius = slider.getThumbRadius();

        BigDecimal actualValue = RangeWidgetUtils.setUpSlider(prompt, rangeQuestion, slider, true);
        if (actualValue == null) {
            slider.setThumbRadius(0);
        } else {
            setUpActualValueLabel(actualValue);
        }

        if (slider.isEnabled()) {
            slider.addOnChangeListener(this);
        }
        return layoutElements.getAnswerView();
    }

    @Override
    public IAnswerData getAnswer() {
        String stringAnswer = currentValue.getText().toString();
        return stringAnswer.isEmpty() ? null : new IntegerData(Integer.parseInt(stringAnswer));
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
    }

    @Override
    public boolean suppressFlingGesture(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return slider.isTrackingTouch();
    }

    @Override
    public void clearAnswer() {
        slider.setThumbRadius(0);
        currentValue.setText("");
        widgetValueChanged();
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        BigDecimal actualValue = RangeWidgetUtils.getActualValue(getFormEntryPrompt(), slider, value);
        setUpActualValueLabel(actualValue);
        widgetValueChanged();
    }

    private void setUpActualValueLabel(BigDecimal actualValue) {
        slider.setThumbRadius(defaultThumbRadius);
        currentValue.setText(String.valueOf(actualValue.intValue()));
    }
}