package com.safitri.secretcalculator;
import android.content.Intent;
import android.provider.MediaStore;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MainActivity extends Activity {

    TextView display;
    double angkaPertama = 0;
    String operator = "";
    boolean angkaBaru = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        int[] angka = {
                R.id.b0, R.id.b1, R.id.b2, R.id.b3,
                R.id.b4, R.id.b5, R.id.b6, R.id.b7,
                R.id.b8, R.id.b9
        };

        for (int id : angka) {
            Button tombol = findViewById(id);
            tombol.setOnClickListener(v -> {
                String nilai = ((Button) v).getText().toString();

                if (angkaBaru || display.getText().toString().equals("0")) {
                    display.setText(nilai);
                    angkaBaru = false;
                } else {
                    display.append(nilai);
                }
            });
        }

        int[] operatorId = {
                R.id.plus, R.id.minus, R.id.mul, R.id.div
        };

        for (int id : operatorId) {
            findViewById(id).setOnClickListener(v -> {
                angkaPertama = Double.parseDouble(
                        display.getText().toString()
                );
                operator = ((Button) v).getText().toString();
                angkaBaru = true;
            });
        }

        findViewById(R.id.equals).setOnClickListener(v -> hitung());

        findViewById(R.id.clear).setOnClickListener(v -> {
            display.setText("0");
            angkaPertama = 0;
            operator = "";
            angkaBaru = true;
        });
    }

    private void hitung() {
        if (operator.isEmpty()) return;

        double angkaKedua = Double.parseDouble(
                display.getText().toString()
        );

        double hasil;

        switch (operator) {
            case "+":
                hasil = angkaPertama + angkaKedua;
                break;
            case "-":
                hasil = angkaPertama - angkaKedua;
                break;
            case "*":
                hasil = angkaPertama * angkaKedua;
                break;
            case "/":
                if (angkaKedua == 0) {
                    Toast.makeText(this, "Tidak bisa dibagi nol",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                hasil = angkaPertama / angkaKedua;
                break;
            default:
                return;
        }

        display.setText(
                hasil == (long) hasil
                        ? String.valueOf((long) hasil)
                        : String.valueOf(hasil)
        );


        if (hasil == 0) {
            bukaKamera();
        } else if (hasil == 3) {
            putarAudio();
        } else if (hasil == 9) {
            bukaGambar();
        } else if (hasil == 8) {
            bukaGPS();

        } else if (hasil == 1) {
            tampilkanSensor();
        }


        operator = "";
        angkaBaru = true;

    }


    private void bukaKamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this,
                    "Aplikasi kamera tidak ditemukan",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void bukaGambar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Pilih Gambar"));
    }

    private void putarAudio() {
        try {
            android.net.Uri suara =
                    RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_NOTIFICATION);

            Ringtone ringtone =
                    RingtoneManager.getRingtone(this, suara);

            if (ringtone != null) {
                ringtone.play();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Audio gagal diputar",
                    Toast.LENGTH_SHORT).show();
        }
    }


private void bukaGPS() {
    try {
        Uri lokasi = Uri.parse("geo:0,0?q=Lokasi saya");
        Intent intent = new Intent(Intent.ACTION_VIEW, lokasi);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aplikasi peta tidak tersedia",
                    Toast.LENGTH_SHORT).show();
        }
    } catch (Exception e) {
        Toast.makeText(this, "GPS tidak dapat dibuka",
                Toast.LENGTH_SHORT).show();
    }
}

    private void tampilkanSensor() {
        SensorManager manager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometer =
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope =
                manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        String pesan = "Accelerometer: "
                + (accelerometer != null ? "tersedia" : "tidak tersedia")
                + "\nGyroscope: "
                + (gyroscope != null ? "tersedia" : "tidak tersedia");

        new android.app.AlertDialog.Builder(this)
                .setTitle("Sensor Perangkat")
                .setMessage(pesan)
                .setPositiveButton("OK", null)
                .show();
    }
}