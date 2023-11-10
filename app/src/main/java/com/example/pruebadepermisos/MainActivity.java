package com.example.pruebadepermisos;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button boton;
    private int REQUEST_PERMISSION = 1234;

    File internalStorageDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boton = findViewById(R.id.button);
        boton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                gestionPermisos();
            }
        });



    }

    public void gestionPermisos(){
        if(checkPermission()){
            Toast.makeText(this,"Ya tienes permisos",Toast.LENGTH_SHORT).show();
            String pathSDCard = System.getenv("SECONDARY_STORAGE"); // LA VERDADERA RUTA A LA SD
            File rutaSD = new File(pathSDCard);
            Uri sdCardRoot = DocumentFile.fromFile(rutaSD).getUri();

            DocumentFile uriSDcard = DocumentFile.fromSingleUri(this, sdCardRoot);
            internalStorageDir = Environment.getExternalStorageDirectory();

            File[] files = internalStorageDir.listFiles();

            if(files != null){
                for( File file : files){
                    if(file.getName().endsWith("mp4")){
                        Log.d("NombreArchivo", file.getName());

                        //moverArchivo(folderDestino, file);
                        moverArchivo(uriSDcard, file);
                    }
                }
            }else{
                Log.i("validación", "Array files vacio");
            }
        }else{
            Toast.makeText(this,"Pidiendo permisos",Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }

    private boolean checkPermission() {
        Log.i("validación", "Check de permisos");
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(writePermission == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Tiene Escritura", Toast.LENGTH_SHORT).show();
            Log.i("validación", "Escritura");
        }else{
            Toast.makeText(this, "No Tiene Escritura", Toast.LENGTH_SHORT).show();
            Log.i("validación", "NO Escritura");
        }
        if(readPermission == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Tiene Lectura", Toast.LENGTH_SHORT).show();
            Log.i("validación", "Lectura");
        }else{
            Toast.makeText(this, "No Tiene Lectura", Toast.LENGTH_SHORT).show();
            Log.i("validación", "NO Lectura");
        }
        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        Log.i("requestPermission", "ok 1");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
        Log.i("requestPermission", "ok 2");
        Toast.makeText(this, "requesrPermission", Toast.LENGTH_SHORT).show();
    }

    private int moverArchivo(DocumentFile sdCardPath, File file){
        if(file == null){
            return 0;
        }

        try {
            //File destinationFile = new File(sdCardPath, file.getName());
            //DocumentFile newFile = sdCardPath.createFile("*/*", file.getName());

            // Crear streams de entrada y salida
            FileInputStream inputStream = new FileInputStream(file);
            FileOutputStream outputStream = new FileOutputStream(sdCardPath.getUri().getPath()+"/SD/"+file.getName());
            //FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(newFile.getUri());

            // Preparar un búfer para la transferencia
            byte[] buffer = new byte[1024];
            int length;

            // Realizar la copia del archivo
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Cerrar los streams después de la copia
            inputStream.close();
            outputStream.close();

            // Si llegamos aquí, la copia fue exitosa

            //borrar el archivo despues de copiarlo
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
            // Manejar cualquier error que pueda ocurrir durante la copia
        }
        return 1;
    }
}