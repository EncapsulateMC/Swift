package net.swift.asm;

public class MethodInjection {
    public final JvmName ownerClass;
    public final String methodName;
    public final String methodDesc;

    public MethodInjection(String ownerClass, String methodName, String methodDesc) {
        this.ownerClass = JvmName.from(ownerClass);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }
}
