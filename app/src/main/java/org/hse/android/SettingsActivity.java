package org.hse.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 2;

    // UI-элементы
    private ImageView photoImageView;
    private EditText nameEditText;
    private TextView lightSensorTextView;

    // Сенсоры
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    // Камера (новый способ — через ActivityResultLauncher)
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null && extras.containsKey("data")) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            photoImageView.setImageBitmap(imageBitmap);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Инициализация интерфейса
        photoImageView = findViewById(R.id.photoImageView);
        nameEditText = findViewById(R.id.nameEditText);
        lightSensorTextView = findViewById(R.id.lightSensorTextView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button saveButton = findViewById(R.id.saveButton);

        // Загружаем имя и фото
        loadNameFromPreferences();
        loadImageFromFile();

        // Кнопка "Сделать фото"
        takePhotoButton.setOnClickListener(v -> requestCameraPermission());

        // Кнопка "Сохранить"
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            saveNameToPreferences(name);  // сохраняем имя
            Toast.makeText(this, R.string.photo_saved, Toast.LENGTH_SHORT).show();

            // сохраняем фото
            Drawable drawable = photoImageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                saveImageToFile(bitmap);
            }
        });

        // Работа с сенсором освещенности
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            lightSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float light = event.values[0];
                    lightSensorTextView.setText(getString(R.string.current_light, light));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Можно не обрабатывать
                }
            };

            // Выводим список всех сенсоров
            TextView sensorListTextView = findViewById(R.id.sensorListTextView);
            StringBuilder sensorInfo = new StringBuilder("Доступные сенсоры:\n");
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

            for (Sensor sensor : sensorList) {
                sensorInfo.append("- ").append(sensor.getName()).append("\n");
            }
            sensorListTextView.setText(sensorInfo.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Подписка на сенсор при возвращении на экран
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отписка от сенсора при уходе
        if (lightSensor != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }
    }

    // Запрашиваем разрешение на использование камеры
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    // Открываем камеру
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    // Обработка результата запроса разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, R.string.photo_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Сохраняем имя пользователя
    private void saveNameToPreferences(String name) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        prefs.edit().putString("user_name", name).apply();
    }

    // Загружаем имя пользователя
    private void loadNameFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String savedName = prefs.getString("user_name", "");
        nameEditText.setText(savedName);
    }

    // Сохраняем изображение во внутреннюю память
    private void saveImageToFile(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), "user_photo.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при сохранении изображения", e);
        }
    }

    // Загружаем изображение из файла
    private void loadImageFromFile() {
        File file = new File(getFilesDir(), "user_photo.jpg");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            photoImageView.setImageBitmap(bitmap);
        }
    }
}
