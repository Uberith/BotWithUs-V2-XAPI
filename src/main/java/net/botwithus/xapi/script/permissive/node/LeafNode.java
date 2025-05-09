package net.botwithus.xapi.script.permissive.node;

import net.botwithus.scripts.Script;

import java.util.concurrent.Callable;

public class LeafNode extends TreeNode {
    private Callable<Boolean> execute = null;
    private Runnable runnable = null;
    private boolean validate = false;

    public LeafNode(Script script) {
        super(script);
    }

    public LeafNode(Script script, String desc) {
        super(script, desc);
    }

    public LeafNode(Script script, String desc, String definedIn) {
        super(script, desc, definedIn);
    }

    public LeafNode(Script script, Callable<Boolean> execute) {
        super(script);
        this.execute = execute;
    }

    public LeafNode(Script script, String desc, Callable<Boolean> execute) {
        super(script, desc);
        this.execute = execute;
    }

    public LeafNode(Script script, String desc, String definedIn, Callable<Boolean> execute) {
        super(script, desc, definedIn);
        this.execute = execute;
    }

    public LeafNode(Script script, Runnable runnable) {
        super(script);
        this.runnable = runnable;
    }

    public LeafNode(Script script, String desc, Runnable runnable) {
        super(script, desc);
        this.runnable = runnable;
    }

    public LeafNode(Script script, String desc, String definedIn, Runnable runnable) {
        super(script, desc, definedIn);
        this.runnable = runnable;
    }

    @Override
    public void execute() {
        try {
            if (execute != null) {
                validate = execute.call();
            } else if (runnable != null) {
                runnable.run();
                validate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TreeNode successNode() {
        return null;
    }

    @Override
    public boolean validate() {
        return validate;
    }

    @Override
    public TreeNode failureNode() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
