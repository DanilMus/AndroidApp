package org.hse.android;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DemoActivity extends AppCompatActivity {
    private TextView result;
    private EditText number;

    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        number = findViewById(R.id.number);
        View button = findViewById(R.id.button);
        View button2 = findViewById(R.id.button2);

        result = findViewById(R.id.result);

        button.setOnClickListener(v -> clickButton());
        button2.setOnClickListener(v -> clickButton2());
    }

    private void clickButton() {
        Integer count = validateNumber();
        if (count == null) {
            return;
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i + 1);
        }

        int sum = list.stream().mapToInt(Integer::intValue).sum();
        result.setText(String.format(Locale.getDefault(), getString(R.string.result_sum), sum));
    }

    private void clickButton2() {
        Integer count = validateNumber();
        if (count == null) {
            return;
        }

        if (count == 1) {
            result.setText(String.format(Locale.getDefault(), getString(R.string.result_zero)));
            return;
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 2; i <= count; i+=2) {
            list.add(i);
        }

        int sum = list.stream()
                .mapToInt(Integer::intValue)
                .reduce(1, (a, b) -> a * b);
        result.setText(String.format(Locale.getDefault(), getString(R.string.result_multiplicity), sum));
    }

    private Integer validateNumber() {
        String numberVal = number.getText().toString();

        if (numberVal.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_input, Toast.LENGTH_SHORT).show();
            numberVal = "0";
        }

        try {
            int num = Integer.parseInt(numberVal);

            if (num < MIN_NUMBER || num > MAX_NUMBER) {
                Toast.makeText(this, R.string.error_invalid_range, Toast.LENGTH_SHORT).show();
                return null;
            }

            return num;
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_format, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
