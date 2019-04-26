package com.stereo.study.bytecode;

//import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import javassist.*;
import javassist.util.HotSwapper;

import java.io.IOException;

/**
 * Created by liuj-ai on 2019/4/26.
 */
public class Standard {

    public void doSomething() {
        System.out.println("doSomething");
    }


    public static void main(String[] args) {
        Standard standard = new Standard();
        standard.doSomething();
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass clazz = pool.get("com.stereo.study.bytecode.Standard");
            CtMethod cm = clazz.getDeclaredMethod("doSomething");
            cm.insertAt(1, "{System.out.println(\"hello HotSwapper.\");}");
            HotSwapper swap = null;
//            try {
//                swap = new HotSwapper(8000);
//            } catch (IllegalConnectorArgumentsException e) {
//                e.printStackTrace();
//            }
            swap.reload("com.stereo.study.bytecode.Standard", clazz.toBytecode());
            standard.doSomething();
        } catch (CannotCompileException | IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
