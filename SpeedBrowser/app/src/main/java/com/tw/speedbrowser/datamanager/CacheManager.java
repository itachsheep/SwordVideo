package com.tw.speedbrowser.datamanager;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class
CacheManager {

    private static Object toObject(byte[] data) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        } finally {
            closeInputStream(bais);
            closeInputStream(ois);
        }
        return null;
    }


    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(baos);
            closeOutputStream(oos);
        }
        return new byte[0];
    }


    public static <T> void delete(String key, T body) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);
        try {
            CacheDatabase.get().getCache().delete(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(String key) {
        asyncDelete(key);
    }

    public static void asyncDelete(String key) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().asyncDelete(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {
                    }, throwable -> {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void asyncSave(String key, T body) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);
        try {
            Disposable disposable = CacheDatabase
                    .get().getCache().asyncSave(cache)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {

                    }, throwable -> {

                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void save(String key, T body) {
        asyncSave(key, body);
    }

    public static Object getCache(String key) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null) {
                return toObject(cache.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void getAsyncCache(String key, DataCallback<T> callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cache -> {
                        if (cache != null && cache.data != null) {
                            if (toObject(cache.data) == null) {
                                callback.onResult(null);
                            } else {
                                callback.onResult((T) toObject(cache.data));
                            }
                        } else {
                            callback.onResult(null);
                        }
                    }, throwable -> callback.onResult(null));

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(null);
        }
    }


    public interface DataCallback<T> {
        void onResult(T t);
    }

    public static Boolean getBooleanCache(String key) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null && toObject(cache.data) != null) {
                return (Boolean) toObject(cache.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean getBooleanCache(String key, boolean defaultValue) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null) {
                return (Boolean) toObject(cache.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static void getAsyncBooleanCache(String key, BooleanCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cache -> {
                        if (cache != null && cache.data != null && toObject(cache.data) != null) {
                            callback.onResult((Boolean) toObject(cache.data));
                        } else {
                            callback.onResult(false);
                        }
                    }, throwable -> callback.onResult(false));

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(false);
        }
    }

    public static void getAsyncBooleanCache(String key, Boolean defaultValue, BooleanCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cache -> {
                        if (cache != null && cache.data != null && toObject(cache.data) != null) {
                            callback.onResult((Boolean) toObject(cache.data));
                        } else {
                            callback.onResult(defaultValue);
                        }
                    }, throwable -> callback.onResult(defaultValue));

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(defaultValue);
        }
    }

    public interface BooleanCallback {
        void onResult(Boolean result);
    }

    public static Integer getIntegerCache(String key) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cache != null && cache.data != null) {
            return (Integer) toObject(cache.data);
        }
        return -1;
    }

    public static Integer getIntegerCache(String key, Integer defaultInt) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null) {
                return (Integer) toObject(cache.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultInt;
    }

    public static void getAsyncIntegerCache(String key, IntegerCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Cache>() {
                        @Override
                        public void accept(Cache cache) throws Exception {
                            if (cache != null && cache.data != null && toObject(cache.data) != null) {
                                callback.onResult((Integer) toObject(cache.data));
                            } else {
                                callback.onResult(-1);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            callback.onResult(-1);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(-1);
        }
    }

    public static void getAsyncIntegerCache(String key, Integer defaultInt, IntegerCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Cache>() {
                        @Override
                        public void accept(Cache cache) throws Exception {
                            if (cache != null && cache.data != null && toObject(cache.data) != null) {
                                callback.onResult((Integer) toObject(cache.data));
                            } else {
                                callback.onResult(defaultInt);
                            }
                        }
                    }, throwable -> callback.onResult(defaultInt));

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(defaultInt);
        }
    }

    public interface IntegerCallback {
        void onResult(Integer integer);
    }


    public static String getStringCache(String key) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null) {
                return toObject(cache.data).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStringCache(String key, String defaultKey) {
        Cache cache = null;
        try {
            cache = CacheDatabase.get().getCache().getCache(key);
            if (cache != null && cache.data != null) {
                return toObject(cache.data).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultKey;
    }

    public static void getAsyncStringCache(String key, StringCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Cache>() {
                        @Override
                        public void accept(Cache cache) throws Exception {
                            if (cache != null && cache.data != null && toObject(cache.data) != null) {
                                callback.onResult(toObject(cache.data).toString());
                            } else {
                                callback.onResult("");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            callback.onResult("");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult("");
        }
    }

    public static void getAsyncStringCache(String key, String defaultKey, StringCallback callback) {
        try {
            Disposable disposable = CacheDatabase.get().getCache().getAsyncCache(key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Cache>() {
                        @Override
                        public void accept(Cache cache) throws Exception {
                            if (cache != null && cache.data != null && toObject(cache.data) != null) {
                                callback.onResult(toObject(cache.data).toString());
                            } else {
                                callback.onResult(defaultKey);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            callback.onResult(defaultKey);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(defaultKey);
        }
    }

    public interface StringCallback {
        void onResult(String result);
    }


    /**
     * 关闭流
     */
    private static void closeInputStream(InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }


    /**
     * 关闭流
     */
    private static void closeOutputStream(OutputStream stream) {
        try {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }


}
