package net.botwithus.xapi.script.permissive.interfaces;

/**
 * Represents a node in a tree structure. This is a common interface for
 * all types of nodes.
 */
public interface ITreeNode {

    /**
     * Defines the node to execute.
     */
    void execute() throws Exception;

    /**
     * Defines the node to execute upon success.
     * @return The TreeNode to execute upon success.
     */
    ITreeNode successNode();

    /**
     * Conditional argument used to determine use of successNode or failureNode
     * @return The boolean result of the conditional statement
     */
    boolean validate();

    /**
     * Defines the node to execute upon failure.
     * @return The TreeNode to execute upon failure.
     */
    ITreeNode failureNode();

    /**
     * Defines if a given TreeNode is a LeafNode or BranchNode
     */
    boolean isLeaf();
}
