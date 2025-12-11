package br.com.util;

import android.os.Bundle;
import android.os.Environment;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import br.com.util.send.Send;
import br.com.util.send.Send.Email;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            String assetJsonFile = Util.assetJsonFile(this, "uf.json");

            List<UF> lUF = Util.getListGson(assetJsonFile, UF.class);
            UF uf = Util.getObjectGson("{\"nome\": \"Acre\", \"sigla\": \"AC\"}", UF.class);


            String value = Str.format("SELECT * FROM CIDADES C WHERE C.NOME LIKE '{0}%' AND C.UF == '{1}'", "INHU", "GO");

            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Email email = new Email.Builder()
                .setTitle("Email para funcionario tal")
                .setMessage("Message 11:08")
                .setPath(Environment.getExternalStorageDirectory() + "/SC/cidades.rar")
                //.setPath(Environment.getExternalStorageDirectory() + "/SC")
                //.setEmail("concyline@hotmail.com")
                //.setEmail("siacsuporteandroid@gmail.com")
                .setEmails(new String[]{"concyline@hotmail.com", "siacsuporteandroid@gmail.com"})
                .build();

        try {
            Send.email(this, email);

            //Send.file(this, Environment.getExternalStorageDirectory() + "/SC/cidades.rar");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class UF{
        public String nome;
        public String sigla;
    }
}