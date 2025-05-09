## Overview
The Permissive package is a powerful tree-based task management system designed for creating complex bot scripts with conditional logic and state management. It provides a structured way to organize bot behavior through a hierarchy of nodes, conditions, and actions.

## Core Components

### 1. Tree Structure
The package uses a tree-based architecture with the following key components:

#### TreeNode
- Base abstract class for all nodes in the tree
- Provides core functionality for tree traversal
- Contains description and validation tracking
- Key methods:
  - `execute()`: Performs the node's action
  - `validate()`: Determines if the node should proceed to success or failure
  - `successNode()`: Returns the next node on success
  - `failureNode()`: Returns the next node on failure
  - `traverse()`: Handles tree navigation logic

#### Branch
- Non-leaf node that manages decision logic
- Contains success and failure paths
- Uses Interlocks to determine which path to take
- Can have dynamic node resolution through Callable interfaces

#### LeafNode
- Terminal node that performs actual actions
- Contains executable logic through Callable or Runnable
- No child nodes

### 2. Conditional System

#### Permissive
- Basic conditional unit
- Contains:
  - Name: Identifier for the condition
  - Predicate: Supplier<Boolean> that evaluates the condition
  - Result tracking through EvaluationResult

#### Interlock
- Groups multiple Permissive conditions
- All conditions must be true for the Interlock to be active
- Tracks which condition failed first
- Used by Branch nodes for decision making

### 3. Interactive Components

#### InteractiveLeaf
- Special LeafNode for handling game interactions
- Supports:
  - Target selection
  - Option text/index based interactions
  - Success action callbacks

## Implementation Example

Here's a breakdown of the example implementation provided:

### 1. Script Structure
```java
@Info(name = "Permissive Script", description = "Example Permissive Script", author = "Sudo", version = "1.0")
public class ExampleScript extends BwuScript {
    // Script state and configuration
    public Tree tree = Tree.NORMAL;
    public String treeOption = "Chop down";
    
    // Constructor defines states
    public ExampleScript() {
        super(new FiremakingState("Firemaking", this),
            new WoodcuttingState("Woodcutting", this));
    }
}
```

### 2. State Implementation
Each state is implemented as a collection of Branches and Leaves:

```java
public class WoodcuttingState extends BwuScript.State {
    // Leaf nodes for actions
    private LeafNode isChoppingTreeLeaf = new LeafNode(...);
    private InteractiveLeaf<SceneObject> chopTreeLeaf = new InteractiveLeaf<>(...);
    
    // Branch nodes for decision making
    private Branch isTreeNearby = new Branch(script, "isTreeNearby", 
        noTreeNearbyLeaf, 
        chopTreeLeaf,
        new Interlock("IsTreeNearby",
            new Permissive("isTreeNearby", () -> {
                // Condition logic
            })
        )
    );
}
```

## Best Practices

1. **State Organization**
   - Create separate state classes for different bot activities
   - Use descriptive names for nodes and conditions
   - Keep conditions atomic and focused

2. **Node Structure**
   - Use Branches for decision points
   - Use LeafNodes for actual actions
   - Use InteractiveLeaf for game interactions

3. **Condition Management**
   - Group related conditions in Interlocks
   - Use meaningful names for Permissive conditions
   - Keep conditions simple and focused

4. **Error Handling**
   - Implement proper error handling in conditions
   - Use failure paths appropriately
   - Track and log condition results

## Example Usage

Here's a simple example of creating a basic task:

```java
// Create leaf nodes for actions
LeafNode performAction = new LeafNode(script, "performAction", () -> {
    // Action logic here
    return true;
});

// Create conditions
Permissive condition = new Permissive("checkCondition", () -> {
    // Condition logic here
    return true;
});

// Create branch with condition
Branch taskBranch = new Branch(script, "taskBranch",
    performAction,  // Success path
    idleNode,      // Failure path
    new Interlock("taskInterlock", condition)
);
```

## Common Patterns

1. **State Transitions**
```java
private LeafNode toNextStateLeaf = new LeafNode(script, "toNextState", () -> {
    script.setCurrentState("NextState");
    return true;
});
```

2. **Condition Checking**
```java
new Interlock("CheckInventory",
    new Permissive("inventoryFull", () -> Backpack.isFull())
);
```

3. **Interactive Tasks**
```java
InteractiveLeaf<SceneObject> interactLeaf = new InteractiveLeaf<>(script, "interact", 
    () -> script.delayUntil(() -> condition, timeout));
interactLeaf.setTarget(target);
interactLeaf.setOptionText("Interact");
```