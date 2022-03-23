package br.com.util.send;

import static androidx.core.content.FileProvider.getUriForFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class Send {

    public static void email(Activity context, Email email) throws Exception {

        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");

        if (email.getEmails() == null) {
            throw new Exception("emails not found");
        }

        emailIntent.putExtra(Intent.EXTRA_EMAIL, email.getEmails());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email.getTitle() != null ? email.getTitle() : " ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, email.getMessage() != null ? email.getMessage() : " ");

        File filePath = new File(email.getPath() != null ? email.getPath() : " ");

        if (!filePath.exists()) {
            Log.e("Util", "FILE NOT FOUND : " + email.getPath());

        } else {

            ArrayList<Uri> files = new ArrayList<Uri>();

            if (filePath.isFile()) {
                files.add(fileToUri(context, filePath));
            } else {
                for (File file : filePath.listFiles()) {
                    files.add(fileToUri(context, file));
                }
            }

            emailIntent.putExtra(Intent.EXTRA_STREAM, files);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        context.startActivity(Intent.createChooser(emailIntent, "Sending email..."));


    }

    private static Uri fileToUri(Activity context, File filePath) {
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, context.getPackageName() + ".provider", filePath);
        } else {
            uri = Uri.fromFile(filePath);
        }

        return uri;
    }


    public static void file(Activity context, String path) throws Exception {

        File file = new File(path);

        if (!file.exists()) {
            throw new Exception("file not found");
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriForFile(context, context.getPackageName() + ".provider", file);

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("*/*");
        context.startActivity(intent);
    }

    public static class Email {

        private String[] emails;
        private String path;
        private String title;
        private String message;

        public Email(Builder builder) {
            this.emails = builder.emails;
            this.path = builder.path;
            this.title = builder.title;
            this.message = builder.message;
        }

        public String[] getEmails() {
            return emails;
        }

        public String getPath() {
            return path;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public static class Builder {

            private String[] emails;
            private String path;
            private String title;
            private String message;

            public Builder setEmails(String[] emails) {
                this.emails = emails;
                return this;
            }

            public Builder setPath(String path) {
                this.path = path;
                return this;
            }

            public Builder setTitle(String title) {
                this.title = title;
                return this;
            }

            public Builder setMessage(String message) {
                this.message = message;
                return this;
            }


            public Email build() {
                return new Email(this);
            }

        }
    }


}
