How to build this plugin
------------------------

1) install the JIRA plugin development environment, as described at: 
		http://confluence.atlassian.com/display/DEVNET/How+to+Build+an+Atlassian+Plugin
	Don't forget to run 
		mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo
2) checkout the project source code if you haven't already
3) run 
		mvn eclipse:eclipse
	in the project root directory
4) import the project into Eclipse, or refresh the project if you had already loaded it
