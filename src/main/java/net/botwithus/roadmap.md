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
I built PermissiveScript as a tree-based system because it maps perfectly to how process automation actually work in the real world - they're just decision trees. Every tick, we're traversing the tree and ultimately making a decision on how to interact with the game.

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

### High-Level PermissiveScript Components

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
Right now debugging is about the same as V1 TreeScript, but I want it to be incredible:
- Visual representation of the decision tree in real-time, with the ability to pause the tree's state and resume it
- Game overlays in 3D space, showing exactly what the bot is targeting, interacting with, checking for, etc.
- Serialization of the PermissiveScript tree, granting the ability to replay a bug report exactly as it happened

### 2. True AIO Capabilities
I want to enable scripts to work together seamlessly:
- Scripts report what items they can produce
- Scheduler can chain scripts together automatically
- Example: User wants 1000 magic longbows
  1. WoodcuttingScript gets the logs
  2. FletchingScript makes the bows
  3. All coordinated automatically

### 3. Better Data Integration
We're sitting on a goldmine of data that could aid our scripts and future decisions:
- Hoover integration for GE prices and trades
- Track all drops and XP gains
- Build heatmaps of player activity

### 4. Professional-Grade UI
I want our UIs to look as good as modern web apps:
- Consistent, beautiful design across all scripts
- Smooth animations and transitions
- Professional feel that matches our technical quality
- (Thinking of having Akisame lead this)

### 5. Community Tools
Make it easier for community devs to build great scripts:
- CI/CD of community scripts
- LLM Security scanning prior to manual review to protect users
- Better compilation error feedback directly to the user
- Automated updates from GitHub to SDN on PR approval

### 6. Better Monitoring
Give users better insight into their bots:
- Discord integration for status updates
- Real-time performance metrics
- Multi-account overview
- Alerts for important events

### 7. Asks from other team members
- Javatar: 
    - Know which drops belong to which player (huge for ironmen)
    - Visual overlays. Highlight objects, draw on tiles, draw on the minimap, etc.
- Akisame:
    - Even better UI/UX for the UI. Design concepts -> Approved concepts -> Implementation
        Would like to be using similar best practices as modern web apps when applicable
- Coaez:
    - Maintaining v1 official scripts
    - DungWithUs
- Clarity:
    - ActionBar port from v1 and improvements
    - PvM Rotation API standard


## Why These Changes Matter

1. **For Users**
   - More reliable scripts
   - Better looking UIs
   - Smarter automation
   - More transparency into what's happening

2. **For Developers**
   - Easier to communicate ideas and concepts as majority of the community will be familiar with the PermissiveScript system
   - Better tools for debugging
   - Clear standards to follow
   - Bug reports will be standardized and easier to understand

3. **For Us**
   - Easier to maintain all scripts - Official scripts and community scripts
   - Better UX overall, leading to better user satisfaction
   - More professional product
   - Stronger community involvement

## What I Need

1. **Team Support**
   - UI/UX specialist (thinking Akisame)
   - Current dev team time allocation

2. **Infrastructure**
   - Discussion around community CI/CD setup.
   - Monitoring systems / Hoover integration into core api.

## Timeline

### Short Term
- Lock down PermissiveScript as our standard
- Start basic UI improvements
- Begin work on debugging tools

### Medium Term
- Hoover integration
- Initial script orchestration
- Community CI/CD pipeline

### Long Term
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
