package org.igetwell.common.utils;

public class TestClassLoader {

    public static void main(String [] args){
        ClassLoader classLoader = new ClassLoader() {};
        try {
            Class cls = classLoader.loadClass(TestClassLoader.class.getName());
            System.err.println(classLoader.getParent());
            System.err.println(cls.getClassLoader());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
