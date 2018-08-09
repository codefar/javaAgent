package org.greenleaf;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ApmAgent implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.startsWith("java") || className.startsWith("sun")) {
            return null;
        }
        ClassPool pool = new ClassPool(true);
        pool.appendClassPath(new LoaderClassPath(loader));
        try {
            CtClass cls = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtConstructor[] ccs = cls.getDeclaredConstructors();
            for (CtConstructor cc : ccs) {
                //构造函数方法体开始时添加的代码
                String codeStrBefore = "System.out.println(\"This code is inserted before constructor "+className+"\");";
                //构造函数方法体结束前添加的代码
                String codeStrAfter = "System.out.println(\"This code is inserted after constructor "+className+"\");";
                cc.insertBeforeBody((codeStrBefore));
                cc.insertAfter(codeStrAfter, true);
            }

            CtMethod[] methods = cls.getDeclaredMethods();
            for (CtMethod method : methods) {
                String codeStrBefore = "System.out.println(\"This code is inserted before method "+method.getName()+"\");";
                String codeStrAfter = "System.out.println(\"This code is inserted after method "+method.getName()+"\");";
                method.insertBefore(codeStrBefore);
                method.insertAfter(codeStrAfter, true);
            }

            File file = new File(cls.getSimpleName() + ".class");
            System.out.println(file.getAbsolutePath());
            try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(cls.toBytecode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cls.toBytecode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("====premain exe args=" + agentArgs);
        inst.addTransformer(new ApmAgent());
    }
}