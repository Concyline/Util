package br.com.util;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import br.com.util.send.Send;
import br.com.util.send.Send.Email;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Email email = new Email.Builder()
                .setTitle("Email para funcionario tal")
                .setMessage("Message 11:08")
                .setPath(Environment.getExternalStorageDirectory() + "/SC/cidades.rar")
                //.setPath(Environment.getExternalStorageDirectory() + "/SC")
                //.setEmail("concyline@hotmail.com")
                //.setEmail("siacsuporteandroid@gmail.com")
                .setEmails(new String[]{"concyline@hotmail.com","siacsuporteandroid@gmail.com"})
                .build();

        try {
            Send.email(this, email);

            //Send.file(this, Environment.getExternalStorageDirectory() + "/SC/cidades.rar");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}