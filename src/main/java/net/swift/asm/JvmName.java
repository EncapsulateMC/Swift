package net.swift.asm;

public class JvmName {
    private String name;

    private JvmName(String name) {
        this.name = name;
    }

    public JvmName inverse() {
        return new JvmName(name.replace('.', '/'));
    }

    public String getName() {
        return name;
    }

    public static JvmName from(String jvmName) {
        return new JvmName(jvmName.replace('.', '/'));
    }

}
