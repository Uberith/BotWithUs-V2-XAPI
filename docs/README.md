# XAPI Framework - Current State and Future Vision

## Why I Built PermissiveScript

When designing the XAPI framework, I wanted to solve several problems I kept seeing in bot development:

1. **Scripts Were Hard to Maintain**
   - Everyone had their own way of structuring code
   - Taking over someone else's script was a nightmare
   - Debugging was more guesswork than science

2. **Development Was Inefficient**
   - Lots of copy-pasted code between scripts
   - Common patterns were reimplemented differently
   - No standard way to handle common bot scenarios

3. **Quality Was Inconsistent**
   - Some scripts were rock solid, others fragile
   - Error handling was often an afterthought
   - Testing was difficult and often skipped

## What We Have Now

### The Core: PermissiveScript
I built PermissiveScript as a tree-based system because it maps perfectly to how bots actually work - they're just decision trees. Every tick, we're asking:
- Should I bank?
- Should I eat?
- Can I mine this rock?
- Is my inventory full?

The tree structure handles this naturally:
```
Root
├── Should Bank?
│   ├── Yes → Run Banking Logic
│   └── No → Continue
├── Should Eat?
│   ├── Yes → Eat Food
│   └── No → Continue
└── Main Activity
```

### Key Components I'm Proud Of

1. **TreeNode System**
   - Base class that handles all the traversal logic
   - Clean separation between decision-making and actions
   - Built-in tracking of what decisions were made

2. **Branch & Leaf Pattern**
   - Branches handle the decision-making
   - Leaves do the actual work
   - Super clear what each piece of code does

3. **Permissive & Interlock**
   - Groups conditions together logically
   - Makes complex decision-making readable
   - Easy to debug when things go wrong

## Where I Want to Take This

### 1. Make Debugging Amazing
Right now debugging is better than it was, but I want it to be incredible:
- Visual representation of the decision tree in real-time
- 3D overlays showing exactly what the bot is targeting
- Ability to replay a bug report exactly as it happened

### 2. True AIO Capabilities
I want to enable scripts to work together seamlessly:
- Scripts report what items they can produce
- Scheduler can chain scripts together automatically
- Example: User wants 1000 magic longbows
  1. WoodcuttingScript gets the logs
  2. FletchingScript makes the bows
  3. All coordinated automatically

### 3. Better Data Integration
We're sitting on a goldmine of data that could make our scripts smarter:
- Hoover integration for GE prices and trades
- Track all drops and XP gains
- Build heatmaps of player activity
- Know which drops belong to which player (huge for ironmen)

### 4. Professional-Grade UI
I want our UIs to look as good as modern web apps:
- Consistent, beautiful design across all scripts
- Smooth animations and transitions
- Professional feel that matches our technical quality
- (Thinking of having Akisame lead this)

### 5. Community Tools
Make it easier for community devs to build great scripts:
- Automated testing of community scripts
- Security scanning to protect users
- Better compilation error feedback
- Automated updates from GitHub to SDN

### 6. Better Monitoring
Give users better insight into their bots:
- Discord integration for status updates
- Real-time performance metrics
- Multi-account overview
- Alerts for important events

## Why These Changes Matter

1. **For Users**
   - More reliable scripts
   - Better looking UIs
   - Smarter automation
   - More transparency into what's happening

2. **For Developers**
   - Easier to build complex scripts
   - Better tools for debugging
   - Clear standards to follow
   - More automated testing

3. **For Us**
   - Easier to maintain scripts
   - Better user satisfaction
   - More professional product
   - Stronger community involvement

## What I Need

1. **Team Support**
   - UI/UX specialist (thinking Akisame)
   - Current dev team time allocation
   - Community manager involvement

2. **Infrastructure**
   - Some additional CI/CD setup
   - Testing environments
   - Monitoring systems

## Timeline (Rough)

### Short Term (1-2 months)
- Lock down PermissiveScript as our standard
- Start basic UI improvements
- Begin work on debugging tools

### Medium Term (3-6 months)
- Hoover integration
- Initial script orchestration
- Community CI/CD pipeline

### Long Term (6+ months)
- Full visual debugging
- Complete AIO system
- Advanced analytics

## Risks and Challenges

1. **Technical**
   - Some features need game client changes
   - Performance impact needs careful monitoring
   - Security is critical with community scripts

2. **Community**
   - Need to convince devs to adopt standards
   - Training and documentation needed
   - Migration of existing scripts

Let me know what parts you want me to prioritize or if you see any major issues I've missed.

# BotWithUs XAPI Framework - Strategic Overview & Roadmap

## Executive Summary
The XAPI framework represents our next-generation bot development platform, designed to standardize and streamline our script development process. This document outlines our current architecture, planned improvements, and strategic initiatives to enhance our competitive advantage in the market.

## Current State & Technical Foundation

### Core Architecture: PermissiveScript
Our flagship architecture, PermissiveScript, provides a robust foundation for script development with several key business advantages:

1. **Reduced Development Time**
   - Standardized components reduce boilerplate code
   - Reusable modules across different scripts
   - Clear patterns for rapid development

2. **Enhanced Maintainability**
   - Tree-based structure for predictable behavior
   - Clear separation of concerns
   - Standardized error handling

3. **Quality Assurance**
   - Built-in validation tracking
   - Comprehensive debugging capabilities
   - Structured testing approach

4. **Team Scalability**
   - Reduced onboarding time for new developers
   - Consistent development patterns
   - Shared best practices

## Strategic Initiatives & Roadmap

### 1. Revenue Enhancement
#### Grand Exchange Integration
- **Business Value**: Enhanced market analysis and trading capabilities
- **Features**:
  - Real-time GE transaction tracking
  - Comprehensive drop monitoring
  - Player trade analysis
  - Activity heatmaps for optimization
- **Impact**: Better informed pricing strategies and market predictions

#### Script Orchestration System
- **Business Value**: Enable true AIO (All-In-One) capabilities
- **Features**:
  - Automated resource chain management
  - Cross-script coordination
  - Production capability reporting
- **Impact**: Higher value proposition for customers through integrated solutions

### 2. Technical Innovation

#### Advanced Debugging & Monitoring
- **Business Value**: Reduced support costs and improved customer satisfaction
- **Features**:
  - Visual debugging system
  - 3D space visualization
  - Real-time performance monitoring
- **Impact**: Faster issue resolution and improved script reliability

#### PermissiveScript Enhancements
- **Business Value**: Improved bug tracking and script maintenance
- **Features**:
  - Node tree serialization
  - Bug replay capabilities
  - State reconstruction
- **Impact**: Reduced maintenance costs and improved customer support

### 3. User Experience & Community

#### UI/UX Standardization
- **Business Value**: Enhanced product perception and user satisfaction
- **Features**:
  - Web-quality UI standards
  - Consistent design language
  - Professional look and feel
- **Impact**: Increased user retention and market competitiveness

#### Community Integration
- **Business Value**: Improved community engagement and script quality
- **Features**:
  - Automated CI/CD pipeline
  - Security scanning
  - Quality assurance automation
- **Impact**: Reduced maintenance overhead and improved community trust

### 4. Monitoring & Analytics

#### Discord Integration
- **Business Value**: Enhanced user engagement and support
- **Features**:
  - Real-time activity monitoring
  - Performance analytics
  - Multi-account oversight
- **Impact**: Improved user satisfaction and reduced support overhead

## Implementation Timeline

### Phase 1 (Immediate Priority)
1. PermissiveScript standardization
2. Basic UI/UX improvements
3. Initial debugging tools

### Phase 2 (Medium Term)
1. Grand Exchange integration
2. Script orchestration foundation
3. Community CI/CD pipeline

### Phase 3 (Long Term)
1. Advanced visualization tools
2. Complete AIO capabilities
3. Comprehensive analytics system

## Resource Requirements

### Development Team
- Lead Architect (Existing)
- UI/UX Specialist (Potential new hire - Akisame?)
- Backend Developers (Existing team)
- Community Manager (Existing)

### Infrastructure
- Existing development environment
- Additional CI/CD infrastructure
- Testing environments

## Risk Assessment

### Technical Risks
1. Integration complexity with existing systems
2. Performance impact of new features
3. Security considerations for community scripts

### Mitigation Strategies
1. Phased implementation approach
2. Comprehensive testing protocol
3. Security-first development practices

## Success Metrics

### Technical Metrics
- Script development time reduction
- Bug report resolution time
- System performance metrics

### Business Metrics
- User satisfaction ratings
- Script adoption rates
- Support ticket volume
- Community engagement levels

## Next Steps

1. **Immediate Actions**
   - Formalize PermissiveScript as standard
   - Begin UI/UX improvement planning
   - Initiate debugging tools development

2. **Required Decisions**
   - Resource allocation approval
   - Priority confirmation
   - Timeline validation

3. **Key Stakeholder Input Needed**
   - UI/UX direction
   - Community incentive structure
   - Feature prioritization

## Technical Architecture Details

# BotWithUs XAPI Documentation

## Overview
The XAPI package is a comprehensive framework for developing RuneScape scripts and bots, with a focus on maintainability, structure, and developer experience. At its core is the PermissiveScript architecture, which provides a powerful and flexible way to organize bot logic through a tree-based task management system.

## Core Components

### 1. PermissiveScript Architecture
The PermissiveScript system is the recommended architecture for BotWithUs script development. It uses a tree-based structure to manage bot behavior and state.

#### Key Components:

##### TreeNode
- Base class for all nodes in the behavior tree
- Provides core traversal and execution logic
- Supports descriptive naming and validation tracking
- Methods:
  - `execute()`: Performs the node's action
  - `validate()`: Determines execution path
  - `successNode()`: Next node on success
  - `failureNode()`: Next node on failure
  - `traverse()`: Handles tree navigation

##### Branch
- Non-leaf decision nodes
- Uses Interlocks for conditional logic
- Supports both static and dynamic node resolution
- Flexible constructor options for various use cases

##### LeafNode
- Terminal nodes that perform actions
- Supports both Callable and Runnable implementations
- Includes validation tracking
- Can contain game interaction logic

##### InteractiveLeaf
- Specialized LeafNode for game interactions
- Supports target selection
- Handles option-based interactions
- Includes success callbacks

#### Conditional System

##### Permissive
- Basic conditional unit
- Contains:
  - Name: Identifier
  - Predicate: Boolean supplier
  - Result tracking

##### Interlock
- Groups multiple Permissive conditions
- Requires all conditions to be true
- Tracks first failing condition
- Used in Branch nodes for decision making

### 2. Script Development

#### BwuScript
- Base class extending PermissiveScript
- Provides:
  - State management
  - Graphics context
  - Statistics tracking
  - Timing utilities

#### Best Practices
1. State Organization
   - Separate states for different activities
   - Clear naming conventions
   - Focused state responsibilities

2. Node Structure
   - Use Branches for decision points
   - LeafNodes for atomic actions
   - InteractiveLeaf for game interactions

3. Condition Management
   - Group related conditions in Interlocks
   - Keep conditions atomic and focused
   - Meaningful naming

4. Error Handling
   - Implement proper error handling
   - Use appropriate failure paths
   - Track and log condition results

## Future Roadmap

### 1. Grand Exchange Integration
- Direct integration with Hoover for:
  - GE transactions
  - Mob and boss drops
  - Skilling XP tracking
  - Item conversions
  - Player trades
  - Player coordinate tracking for heatmaps

### 2. PermissiveScript Enhancements
- Serialization of node trees
  - Support for bug report replay
  - Integration with OpenGL backbuffer screenshots
  - State reconstruction capabilities

### 3. Script Orchestration
- Script production reporting system
  - Define item production capabilities
  - Integration with site Scheduler
  - Support for AIO operations
  - Example: Gold bar production chain
    1. MineWithUs for ore
    2. SmithWithUs for smelting

### 4. Game Mechanics Integration
- GroundItem ownership detection
  - Packet-based implementation
  - Ironman mode support
  - Drop tracking improvements

### 5. Standardization Initiatives
- PermissiveScript as official architecture
  - Optional but recommended
  - Potential incentive system
  - Improved script maintenance
  - Better community support

### 6. UI/UX Improvements
- ImGui Official Script Standards
  - Web-dev quality UI
  - Consistent design language
  - Enhanced user experience
  - Dedicated front-end development

### 7. Community Integration
- CI/CD Pipeline for Community Scripts
  - Automated repository management
  - Security vulnerability scanning
  - Compilation verification
  - Automated SDN updates
  - User notification system

### 8. Development Tools
- Visual Debugging System
  - 3D space object highlighting
  - Tile overlays
  - Minimap integration
  - Development debugging tools

### 9. Monitoring and Reporting
- Discord Integration
  - 5-minute activity summaries
  - Per-account statistics
  - XP/hr tracking
  - Kill/hr monitoring
  - Multi-account overview

## Benefits of PermissiveScript

### 1. Structured Development
- Clear separation of concerns
- Predictable behavior flow
- Easy to maintain and modify
- Reduced cognitive load

### 2. Flexible Architecture
- Support for complex decision trees
- Easy state management
- Conditional branching
- Dynamic node resolution

### 3. Debugging and Maintenance
- Clear execution paths
- Built-in validation tracking
- Easy to trace issues
- Support for logging and monitoring

### 4. Community Benefits
- Standardized approach
- Easier script handover
- Shared best practices
- Reduced learning curve

### 5. Future-Proofing
- Extensible architecture
- Support for new features
- Easy integration points
- Scalable design

## Getting Started

### Basic Script Structure
```java
@Info(name = "Example Script", description = "Example Script", author = "Author", version = "1.0")
public class ExampleScript extends BwuScript {
    public ExampleScript() {
        super(
            new StateOne("State One", this),
            new StateTwo("State Two", this)
        );
    }

    @Override
    public Branch getRootNode() {
        return currentState.getRootNode();
    }
}
```

### State Implementation
```java
public class ExampleState extends State {
    private final Branch rootNode;

    public ExampleState(String name, Script script) {
        super(name, script);
        
        // Create leaf nodes
        LeafNode actionNode = new LeafNode(script, "Action", () -> {
            // Action logic
            return true;
        });

        // Create conditions
        Permissive condition = new Permissive("Condition", () -> {
            // Condition logic
            return true;
        });

        // Create branch
        rootNode = new Branch(script, "Root",
            actionNode,
            idleNode,
            new Interlock("MainInterlock", condition)
        );
    }

    @Override
    public Branch getRootNode() {
        return rootNode;
    }
}
```

## Contributing
We welcome contributions to improve and extend the XAPI framework. Please follow these guidelines:
1. Follow the established coding standards
2. Include appropriate documentation
3. Add unit tests for new features
4. Submit detailed pull requests
5. Engage with the community for feedback

## Support
For support and questions:
1. Check the official documentation
2. Join our Discord community
3. Submit issues through the appropriate channels
4. Engage with other developers 