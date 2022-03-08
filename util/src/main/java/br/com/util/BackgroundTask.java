package br.com.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.FragmentActivity;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BackgroundTask {

    public static final int MESSAGE_PREEXECUTE = 123;
    public static final int MESSAGE_FINISH = 415024;
    public static final int MESSAGE_BROKEN = 415025;
    public static final int MESSAGE_STOP = 415026;
    public static final String TAG_HOOK = "HOOK";
    private static final Integer ID_ACTIVITY = 415027;
    private static final Integer ID_FRAGMENT_ACTIVITY = 415028;
    private static final Integer ID_FRAGMENT = 415029;
    private static final Integer ID_SUPPORT_FRAGMENT = 415030;
    private static AtomicInteger count = new AtomicInteger(0);
    private Holder holder = null;
    private Map<Integer, TaskDescription> taskMap = new ConcurrentHashMap();
    private Map<Integer, MessageListener> messageMap = new ConcurrentHashMap();
    private Map<Integer, FinishListener> postMap = new ConcurrentHashMap();
    private Map<Integer, BrokenListener> exceptionMap = new ConcurrentHashMap();
    private Map<Integer, PreExecuteListener> preExecuteMap = new ConcurrentHashMap();
    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);


    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

        public boolean handleMessage(@NonNull Message message) {

            Holder result;

            if (message.what == MESSAGE_PREEXECUTE) {

                Iterator i$ = BackgroundTask.this.preExecuteMap.values().iterator();

                while (i$.hasNext()) {
                    PreExecuteListener listenerx = (PreExecuteListener) i$.next();
                    listenerx.onPreExecute();
                }
                return true;
            }

            if (message.what == MESSAGE_FINISH && message.obj instanceof Holder) {
                result = (Holder) message.obj;
                BackgroundTask.this.taskMap.remove(result.id);
                BackgroundTask.this.messageMap.remove(result.id);
                BackgroundTask.this.exceptionMap.remove(result.id);
                FinishListener listener = (FinishListener) BackgroundTask.this.postMap.remove(result.id);
                if (listener != null) {
                    listener.onPostExecute(result.object);
                    BackgroundTask.this.preExecuteMap.remove(result.id);
                }

                BackgroundTask.getInstance().dispatchUnregister();

                return true;

            }

            if (message.what == MESSAGE_BROKEN && message.obj instanceof Holder) {
                result = (Holder) message.obj;
                BackgroundTask.this.taskMap.remove(result.id);
                BackgroundTask.this.messageMap.remove(result.id);
                BackgroundTask.this.postMap.remove(result.id);
                BrokenListener listenerxx = (BrokenListener) BackgroundTask.this.exceptionMap.remove(result.id);
                if (listenerxx != null) {
                    listenerxx.onException((Exception) result.object);
                    BackgroundTask.this.preExecuteMap.remove(result.id);
                }

                BackgroundTask.getInstance().dispatchUnregister();

                return true;
            }

            if (message.what == MESSAGE_STOP) {
                BackgroundTask.this.resetHolder();
                BackgroundTask.this.taskMap.clear();
                BackgroundTask.this.messageMap.clear();
                BackgroundTask.this.postMap.clear();
                BackgroundTask.this.exceptionMap.clear();

                return true;

            }

            // MESSAGE
            Iterator i$ = BackgroundTask.this.messageMap.values().iterator();

            while (i$.hasNext()) {
                MessageListener listenerx = (MessageListener) i$.next();
                listenerx.onProgressUpdate(message);
            }


            return true;
        }
    });

    public BackgroundTask() {
    }

    @MainThread
    public static Register with(@NonNull Activity activity) {
        getInstance().registerHookToContext(activity);
        return getInstance().buildRegister(activity);
    }

    @MainThread
    public static Register with(@NonNull FragmentActivity activity) {
        getInstance().registerHookToContext(activity);
        return getInstance().buildRegister(activity);
    }

    @MainThread
    public static Register with(@NonNull Fragment fragment) {
        getInstance().registerHookToContext(fragment);
        return getInstance().buildRegister(fragment);
    }

    @MainThread
    public static Register with(@NonNull androidx.fragment.app.Fragment fragment) {
        getInstance().registerHookToContext(fragment);
        return getInstance().buildRegister(fragment);
    }

    @WorkerThread
    public static void post(@NonNull Message message) {
        getInstance().handler.sendMessage(message);
    }

    private static BackgroundTask getInstance() {
        return SugarTaskHolder.INSTANCE;
    }

    private Register buildRegister(@NonNull Activity activity) {
        this.holder = new Holder(ID_ACTIVITY, activity);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull FragmentActivity activity) {
        this.holder = new Holder(ID_FRAGMENT_ACTIVITY, activity);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull Fragment fragment) {
        this.holder = new Holder(ID_FRAGMENT, fragment);
        return new Register(count.getAndIncrement());
    }

    private Register buildRegister(@NonNull androidx.fragment.app.Fragment fragment) {
        this.holder = new Holder(ID_SUPPORT_FRAGMENT, fragment);
        return new Register(count.getAndIncrement());
    }

    private Runnable buildRunnable(@NonNull final Integer id) {
        return new Runnable() {
            public void run() {

                Process.setThreadPriority(10);
                if (BackgroundTask.this.taskMap.containsKey(id)) {
                    Message message = Message.obtain();
                    try {
                        message.what = MESSAGE_FINISH;
                        message.obj = BackgroundTask.this.new Holder(id, ((TaskDescription) BackgroundTask.this.taskMap.get(id)).doInBackground());
                    } catch (Exception var3) {
                        message.what = MESSAGE_BROKEN;
                        message.obj = BackgroundTask.this.new Holder(id, var3);
                    }
                    BackgroundTask.post(message);
                }

            }
        };
    }

    private void registerHookToContext(@NonNull Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);

        if (hookFragment == null) {
            hookFragment = new HookFragment();
            manager.beginTransaction().add(hookFragment, TAG_HOOK).commitAllowingStateLoss();
        }

    }

    private void registerHookToContext(@NonNull FragmentActivity activity) {
        androidx.fragment.app.FragmentManager manager = activity.getSupportFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);

        if (hookSupportFragment == null) {
            hookSupportFragment = new HookSupportFragment();
            manager.beginTransaction().add(hookSupportFragment, TAG_HOOK).commitAllowingStateLoss();
        }

    }

    @TargetApi(17)
    private void registerHookToContext(@NonNull Fragment fragment) {
        FragmentManager manager = fragment.getChildFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);

        if (hookFragment == null) {
            hookFragment = new HookFragment();
            manager.beginTransaction().add(hookFragment, TAG_HOOK).commitAllowingStateLoss();
        }

    }

    private void registerHookToContext(@NonNull androidx.fragment.app.Fragment fragment) {
        androidx.fragment.app.FragmentManager manager = fragment.getChildFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);

        if (hookSupportFragment == null) {
            hookSupportFragment = new HookSupportFragment();
            manager.beginTransaction().add(hookSupportFragment, TAG_HOOK).commitAllowingStateLoss();
        }

    }

    private void dispatchUnregister() {

        if (this.holder != null && this.taskMap.size() <= 0) {

            if (this.holder.id.equals(ID_ACTIVITY) && this.holder.object instanceof Activity) {
                this.unregisterHookToContext((Activity) this.holder.object);

            } else if (this.holder.id.equals(ID_FRAGMENT_ACTIVITY) && this.holder.object instanceof FragmentActivity) {
                this.unregisterHookToContext((FragmentActivity) this.holder.object);

            } else if (this.holder.id.equals(ID_FRAGMENT) && this.holder.object instanceof Fragment) {
                this.unregisterHookToContext((Fragment) this.holder.object);

            } else if (this.holder.id.equals(ID_SUPPORT_FRAGMENT) && this.holder.object instanceof androidx.fragment.app.Fragment) {
                this.unregisterHookToContext((androidx.fragment.app.Fragment) this.holder.object);
            }

            this.resetHolder();
        }
    }

    private void unregisterHookToContext(@NonNull Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment != null) {
            hookFragment.postEnable = false;
            manager.beginTransaction().remove(hookFragment).commitAllowingStateLoss();
        }

    }

    private void unregisterHookToContext(@NonNull FragmentActivity activity) {
        androidx.fragment.app.FragmentManager manager = activity.getSupportFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment != null) {
            hookSupportFragment.postEnable = false;
            manager.beginTransaction().remove(hookSupportFragment).commitAllowingStateLoss();
        }

    }

    @TargetApi(17)
    private void unregisterHookToContext(@NonNull Fragment fragment) {
        FragmentManager manager = fragment.getChildFragmentManager();
        HookFragment hookFragment = (HookFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookFragment != null) {
            hookFragment.postEnable = false;
            manager.beginTransaction().remove(hookFragment).commitAllowingStateLoss();
        }

    }

    private void unregisterHookToContext(@NonNull androidx.fragment.app.Fragment fragment) {
        androidx.fragment.app.FragmentManager manager = fragment.getChildFragmentManager();
        HookSupportFragment hookSupportFragment = (HookSupportFragment) manager.findFragmentByTag(TAG_HOOK);
        if (hookSupportFragment != null) {
            hookSupportFragment.postEnable = false;
            manager.beginTransaction().remove(hookSupportFragment).commitAllowingStateLoss();
        }

    }

    private void resetHolder() {
        if (this.holder != null) {
            this.holder.id = 0;
            this.holder.object = null;
            this.holder = null;
        }
    }

    private static class SugarTaskHolder {
        public static final BackgroundTask INSTANCE = new BackgroundTask();

        private SugarTaskHolder() {
        }
    }

    public static class HookSupportFragment extends androidx.fragment.app.Fragment {
        protected boolean postEnable = true;

        public HookSupportFragment() {
        }

        @SuppressLint("WrongThread")
        public void onStop() {
            super.onStop();
            if (this.postEnable) {
                Message message = new Message();
                message.what = MESSAGE_STOP;
                BackgroundTask.post(message);
            }

        }
    }

    public static class HookFragment extends Fragment {
        protected boolean postEnable = true;

        public HookFragment() {
        }

        public void onStop() {
            super.onStop();
            if (this.postEnable) {
                Message message = new Message();
                message.what = MESSAGE_STOP;
                BackgroundTask.post(message);
            }

        }
    }

    private class Holder {
        private Integer id;
        private Object object;

        private Holder(@NonNull Integer id, @Nullable Object object) {
            this.id = id;
            this.object = object;
        }
    }

    public class Builder {
        private Integer id;

        private Builder(@NonNull Integer id) {
            this.id = id;
        }

        @MainThread
        public Builder onProgressUpdate(@NonNull MessageListener listener) {
            BackgroundTask.this.messageMap.put(this.id, listener);
            return this;
        }

        @MainThread
        public Builder onPostExecute(@NonNull FinishListener listener) {
            BackgroundTask.this.postMap.put(this.id, listener);
            return this;
        }

        @MainThread
        public Builder onException(@NonNull BrokenListener listener) {
            BackgroundTask.this.exceptionMap.put(this.id, listener);
            return this;
        }

        @SuppressLint("WrongThread")
        @MainThread
        public Builder onPreExecute(PreExecuteListener listener) {
            BackgroundTask.this.preExecuteMap.put(this.id, listener);

            Message message = Message.obtain();
            message.what = MESSAGE_PREEXECUTE;
            BackgroundTask.post(message);
            return this;
        }

        @MainThread
        public void execute() {
            BackgroundTask.this.executor.execute(BackgroundTask.this.buildRunnable(this.id));
        }
    }

    public class Register {
        private Integer id;

        private Register(@NonNull Integer id) {
            this.id = id;
        }

        @MainThread
        public Builder doInBackground(@NonNull TaskDescription description) {
            BackgroundTask.this.taskMap.put(this.id, description);
            return BackgroundTask.this.new Builder(this.id);
        }
    }

    public interface BrokenListener {
        void onException(@NonNull Exception var1);
    }

    public interface FinishListener {
        void onPostExecute(@Nullable Object var1);
    }

    public interface PreExecuteListener {
        void onPreExecute();
    }

    public interface MessageListener {
        void onProgressUpdate(@NonNull Message var1);
    }

    public interface TaskDescription {
        Object doInBackground();
    }
}

