# Rise Player

## Introduction

Rise Player is responsible for launching Viewer in Chrome, to display HTML content from Rise Vision. In addition, Player will run a local server on port 9449 that is used for communication with Viewer.

Rise Player works in conjunction with [Rise Vision](http://www.risevision.com), the [digital signage management application](http://rva.risevision.com/) that runs on [Google Cloud](https://cloud.google.com).

At this time Chrome is the only browser that this project and Rise Vision supports.

Built With

 - Java 1.7
 - Maven 3
 
## Development

### Local Development Environment Setup

Rise Player uses Maven as its build infrastructure, and it's easy to import on any IDE which supports it (e.g., Eclipse).

#### Running Rise Player

Rise Player is a jar and can be ran by right clicking on the file and running with Java Runtime.

Rise Vision Player requires a Display ID or Claim ID to connect the Display to the Rise Vision Platform.

1. From the [Rise Vision Platform](http://rva.risevision.com/) click on Displays
2. Select Add Display and give it a name.
3. Click save.
4. Copy the Display ID and enter it in the Rise Vision Player on startup.

The Display ID can also be changed in the the "RiseDisplayNetworkII.ini" within the application folder.


## Submitting Issues 
If you encounter problems or find defects we really want to hear about them. If you could take the time to add them as issues to this Repository it would be most appreciated. When reporting issues please use the following format where applicable:

**Reproduction Steps**

1. did this
2. then that
3. followed by this (screenshots / video captures always help)

**Expected Results**

What you expected to happen.

**Actual Results**

What actually happened. (screenshots / video captures always help)

## Contributing
All contributions are greatly appreciated and welcome! If you would first like to sound out your contribution ideas please post your thoughts to our [community](http://community.risevision.com), otherwise submit a pull request and we will do our best to incorporate it


## Resources
If you have any questions or problems please don't hesitate to join our lively and responsive community at http://community.risevision.com.

If you are looking for user documentation on Rise Vision please see http://www.risevision.com/help/users/

If you would like more information on developing applications for Rise Vision please visit http://www.risevision.com/help/developers/. 

**Facilitator**

[Francisco Vallarino](https://github.com/fjvallarino "Francisco Vallarino")
