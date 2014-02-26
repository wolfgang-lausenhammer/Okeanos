# Okeanos

Okeanos is an open source simulation platform aiming to simulate demand-response scenarios by utilizing game theoretic approaches. Furthermore, it is heavily based on independent agents making, their own decisions. Agents are the smalles unit of work in Okeanos and can range from household appliances over households over a group of households to a whole grid.

Visit also the project page on https://landsteiner.fh-salzburg.ac.at/ for other services related to Okeanos.

### Background
Wolfgang Lausenhammer develops this software as part of writing his Master's thesis. Being a graduate student at the University of Applied Sciences Salzburg, he currently spends his last semester at the Bowling Green State University fully dedicated to writing his Master's thesis.

### Build
A working Maven installation is required to build this software. To build it, head to the root folder of the project in a console and write
```
mvn package
```

To run it, either deploy the generated OSGi bundles in a container of choice or use the PAX plugin to automate starting and deploying of the artifacts like
```
mvn pax:provision
```
**NOTE**: to use ```pax:provision```, the artifacts to deploy need to be in a repository known to Maven. That is, use ```mvn install``` before attempting to provision the artifacts with the PAX plugin.

### Development in Eclipse
The PAX plugin can generate all necessary project files for Eclipse automatically by executing
```
mvn pax:eclipse
```
**NOTE**: newly added dependencies are not immediately known to Eclipse, for that reason, the command needs to be executed everytime a new dependency is added to one of the projects. 

### Documentation
Further documentation can be found in the docs folder

### Research Funding
The research is funded through scholarships to Wolfgang Lausenhammer by the Marshall Plan Foundation (http://marshallplan.at/) and the Josef Ressel Center for User-Centric Smart Grid Privacy, Security and Control (http://www.en-trust.at/).