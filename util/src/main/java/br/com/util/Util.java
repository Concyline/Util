package br.com.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import br.com.util.listeners.OnListnerAlertSimCancelar;
import br.com.util.listeners.OnListnerOk;

public class Util {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public static void abaixaTeclado(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void fadeIn(Context context, View button) {
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        button.startAnimation(in);
    }

    public static void load(Context context, View v) {
        Animation in = AnimationUtils.loadAnimation(context, R.anim.slide_in_top);
        v.startAnimation(in);
    }

    public static void rotate(Context context, View v) {
        Animation in = AnimationUtils.loadAnimation(context, R.anim.rotate);
        v.startAnimation(in);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void alertOk(Activity context, String mensagem) {
        alertOkInter(context, mensagem, null);
    }

    public static void alertOk(Activity context, String mensagem, final OnListnerOk onListnerOk) {
        alertOkInter(context, mensagem, onListnerOk);
    }

    private static void alertOkInter(final Activity context, String mensagem, @NonNull final OnListnerOk onListnerOk) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("Atenção")
                    .setMessage(mensagem)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (onListnerOk != null) {
                                onListnerOk.ok();
                            }

                        }
                    })
                    .setIcon(R.drawable.ic_error_outline_black_24dp
                    )
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alertSimCancelar(Context context, String mensagem, final OnListnerAlertSimCancelar simCancelar) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("Atenção")
                    .setMessage(mensagem)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            simCancelar.sim();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            simCancelar.cancelar();
                        }
                    })
                    .setIcon(R.drawable.ic_error_outline_black_24dp
                    )
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> getListGson(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(jsonArray, typeOfT);
    }

    public static <T> T getObjectGson(String jsonArray, Class<T> clazz) {
        return gson.fromJson(jsonArray, clazz);
    }

    public static String assetJsonFile (Context context, String filename ) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////




}
