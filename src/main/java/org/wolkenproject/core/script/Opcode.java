package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaCallable;
import org.wolkenproject.exceptions.MochaException;

public class Opcode {
    private String          name;
    private String          desc;
    private String          usage;
    private int             identifier;
    private int             numArgs;
    private MochaCallable   callable;

    public Opcode(String name, String desc, String usage, int identifier, int numArgs, MochaCallable callable) {
        this.name = name;
        this.desc = desc;
        this.usage= usage;
        this.numArgs= numArgs;
        this.callable= callable;
    }

    public void execute(Scope scope) throws MochaException {
        callable.call(scope);
    }

    public Opcode makeCopy() {
        return new Opcode(name, desc, usage, identifier, numArgs, callable);
    }

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    public String getName() {
        return name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsage() {
        return usage;
    }
}
