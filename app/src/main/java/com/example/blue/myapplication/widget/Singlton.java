package com.example.blue.myapplication.widget;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Singlton {
    private static Map<Object, Object> all = new ConcurrentHashMap(32);

    public Singlton() {
    }

    public static <T> T getInstance(Class<T> objCls) {
        return (T) getInstance(objCls, (InstanceFactory) null);
    }

    public static <T> T getInstance(Class<T> objCls, InstanceFactory<T> instanceFactory) {
        Object obj = all.get(objCls);
        if (obj == null) {
            synchronized (objCls) {
                obj = all.get(objCls);
                if (obj == null) {
                    try {
                        if (instanceFactory != null) {
                            obj = instanceFactory.createInstance();
                        } else {
                            obj = objCls.newInstance();
                        }

                        all.put(objCls, obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return (T) obj;
    }

    public static synchronized <T> void setInstance(T obj) {
//        assert obj != null;

        if (obj != null) {
            all.put(obj.getClass(), obj);
        }
    }

    public static synchronized <T> void removeInstance(Class<T> cls) {
        all.remove(cls);
    }

    public interface InstanceFactory<T> {
        T createInstance();
    }
}
