package br.com.util.send;

import static androidx.core.content.FileProvider.getUriForFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Send {

 /*   public static void email(Activity context, Email email) throws Exception {

        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        //emailIntent.setType("plain/text");
        emailIntent.setType("text/plain");

        if (email.getEmails() == null) {
            throw new Exception("emails not found");
        }

        emailIntent.putExtra(Intent.EXTRA_EMAIL, email.getEmails());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email.getTitle() != null ? email.getTitle() : " ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, email.getMessage() != null ? email.getMessage() : " ");

        File filePath = new File(email.getPath() != null ? email.getPath() : " ");

        if (!filePath.exists()) {
            Log.e("Util", "file not found : " + email.getPath());

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
    }*/

    public static void email(Activity context, Email email) throws Exception {

        boolean hasAttachment = email.getPath() != null && new File(email.getPath()).exists();

        Intent emailIntent;

        if (hasAttachment) {
            emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        } else {
            emailIntent = new Intent(Intent.ACTION_SEND);
        }

        emailIntent.setType("text/plain"); // 👈 CORRETO

        if (email.getEmails() == null) {
            throw new Exception("emails not found");
        }

        emailIntent.putExtra(Intent.EXTRA_EMAIL, email.getEmails());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email.getTitle() != null ? email.getTitle() : " ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, email.getMessage() != null ? email.getMessage() : " ");

        if (hasAttachment) {

            File filePath = new File(email.getPath());
            ArrayList<Uri> files = new ArrayList<>();

            if (filePath.isFile()) {
                files.add(fileToUri(context, filePath));
            } else {
                for (File file : filePath.listFiles()) {
                    files.add(fileToUri(context, file));
                }
            }

            emailIntent.putExtra(Intent.EXTRA_STREAM, files);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
            if (builder.emails != null) {
                this.emails = builder.emails.toArray(new String[0]);
            }
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

            private List<String> emails;
            private String path;
            private String title;
            private String message;

            public Builder setEmails(String[] array) {
                if (array != null) {
                    emails = Arrays.asList(array);
                }
                return this;
            }

            public Builder setEmail(String email) {
                if (emails == null) {
                    emails = new ArrayList<>();
                }

                emails.add(email);
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
