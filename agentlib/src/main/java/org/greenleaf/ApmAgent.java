package org.greenleaf;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ApmAgent implements ClassFileTransformer {

    private static Logger logger = LoggerFactory.getLogger(ApmAgent.class);

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
                String codeStrBefore = "System.out.println(\"This code is inserted before constructor "+className+"\");";
                String codeStrAfter = "System.out.println(\"This code is inserted after constructor "+className+"\");";
                cc.insertBeforeBody((codeStrBefore));
                cc.insertAfter(codeStrAfter, true);
            }

            long startTime = System.currentTimeMillis();
            System.out.println("time cost " + (System.currentTimeMillis() - startTime));

            CtMethod[] methods = cls.getDeclaredMethods();
            for (CtMethod method : methods) {
                String codeStrBefore = "long startTime = System.currentTimeMillis();";
                String codeStrAfter = "System.out.println(\"time cost \" + (System.currentTimeMillis() - startTime));";
                System.out.println(codeStrBefore);
                System.out.println(codeStrAfter);
                method.insertBefore(codeStrBefore);
                method.insertAfter(codeStrAfter, true);
            }

            File file = new File(cls.getSimpleName() + ".class");
            try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(cls.toBytecode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cls.toBytecode();
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("premain exe args : {}" + agentArgs);
        inst.addTransformer(new ApmAgent());
    }
}