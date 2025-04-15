package org.hse.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        View button_students = findViewById(R.id.button_students);
        View button_teachers = findViewById(R.id.button_teachers);
        View button_settings = findViewById(R.id.button_settings);

        button_students.setOnClickListener(v -> clickButtonStudents());
        button_teachers.setOnClickListener(v -> clickButtonTeachers());
        button_settings.setOnClickListener(v -> clickButtonSettings());
    }

    // Кнопка с расписанием студентов
    private void clickButtonStudents() {
//        Toast.makeText(this, R.string.button_students_reaction, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    // Кнопка с расписанием учителей
    private void clickButtonTeachers() {
//        Toast.makeText(this, R.string.button_teachers_reaction, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }

    // Кнопка с настройками
    private void clickButtonSettings() {
//        Toast.makeText(this, R.string.button_teachers_reaction, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}