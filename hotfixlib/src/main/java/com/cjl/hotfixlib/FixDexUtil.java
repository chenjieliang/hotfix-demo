package com.cjl.hotfixlib;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.DocumentsContract;

import com.cjl.hotfixlib.utils.ArrayUtil;
import com.cjl.hotfixlib.utils.Constants;
import com.cjl.hotfixlib.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * @author chenjieliang
 */
public class FixDexUtil {

    //存在需要修改的dex集合
    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        loadedDex.clear();
    }

    public static String getDexPath(Context context, String dexName) {
        return new File(context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE), dexName).getAbsolutePath();
    }

    public static String getOptimizedDexPath(Context context) {
        return context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()
                + File.separator + Constants.DEX_OPTIMIZE_DIR;
    }


    public static synchronized void loadFixedDex(Context context) {
        if (context==null) {
            return;
        }
        // Dex 文件目录
        File fileDir = context.getDir(Constants.DEX_DIR,Context.MODE_PRIVATE);
        // 遍历私有文件中的所有文件
        File[] listFiles = fileDir.listFiles();
        for (File file : listFiles) {
            if (file.getName().endsWith(Constants.DEX_SUFFIX)
                    && !"classes.dex".equals(file.getName())) {
                //找到修改包文件，加入集合
                loadedDex.add(file);
            }
        }
        //模拟类加载器
        createDexClassLoader(context,fileDir);
    }

    private static void createDexClassLoader(Context context, File fileDir) {

        //临时解压目录
        String optimizeDir = getOptimizedDexPath(context);//fileDir.getAbsolutePath() + File.separator + Constants.DEX_OPTIMIZE_DIR;
        File fopt = new File(optimizeDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
        }

        for (File dex : loadedDex) {
            //初始化DexClassLoader(自有)
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(),
                    optimizeDir,null,context.getClassLoader());

            //每遍历一次需要修复的dex文件,就需要插队一次
            hotfix(classLoader,context);
        }
    }

    private static void hotfix(DexClassLoader classLoader, Context context) {

        //获取系统PathClassLocader
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //获取自有的dexElements数组
            Object selfDexElements = ReflectUtil.getDexElements(ReflectUtil.getPathList(classLoader));
            //获取系统的dexElements数组
            Object systemDexElements = ReflectUtil.getDexElements(ReflectUtil.getPathList(pathClassLoader));
            //合并成新的dexElements数组，并设置自有的优先级
            Object dexElements = ArrayUtil.combineArray(selfDexElements,systemDexElements);
            //获取系统的pathList对象
            Object systemPathList = ReflectUtil.getPathList(pathClassLoader);
            //重新赋值系统的pathList属性值 -- 修改的dexElements数组（新合成的）
            ReflectUtil.setField(systemPathList,systemPathList.getClass(),"dexElements",dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hotfixApk(final Context context, String apkPath, String dummyClassName ){
        //临时解压目录
        String optimizeDir = getOptimizedDexPath(context);
        File fopt = new File(optimizeDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
        }
        final DexClassLoader classLoader = new DexClassLoader(apkPath,
                optimizeDir,null,context.getClassLoader());
        try {
            classLoader.loadClass(dummyClassName);
            hotfix(classLoader,context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void call(ClassLoader cl,String className,String methodName,Object[] params) {
        Class c[]=null;
        if(params!=null){//存在
            int len = params.length;
            c = new Class[len];
            for(int i=0;i<len;++i){
                c[i] = params[i].getClass();
            }
        }
        Class myClasz = null;
        try {
            myClasz =
                    cl.loadClass(className);
            Object instance = myClasz.getConstructor().newInstance();
            myClasz.getDeclaredMethod(methodName,c).invoke(instance,params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
