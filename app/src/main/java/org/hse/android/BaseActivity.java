package org.hse.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        View button = findViewById(R.id.button_students);
        View button2 = findViewById(R.id.button_teachers);

        button.setOnClickListener(v -> clickButtonStudents());
        button2.setOnClickListener(v -> clickButtonTeachers());

        // Запуск DemoActivity
        // Intent intent = new Intent(this, DemoActivity.class);
        // startActivity(intent);
    }

    private void clickButtonStudents() {
//        Toast.makeText(this, R.string.button_students_reaction, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    private void clickButtonTeachers() {
//        Toast.makeText(this, R.string.button_teachers_reaction, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }
}