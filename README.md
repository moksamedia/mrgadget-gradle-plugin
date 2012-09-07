# Description

The MrGadget Gradle plugin is a wrapper for the MrGadget project that makes it really, really easy to use MrGadget in Gradle build scripts. MrGadget loves to do 4 things: 1) upload files to a remote server via SFTP, 2) upload files to a remote server via SCP, 3) execute commands on a remote server, and 4) execute SUDO commands on a remote server.

# Installation

The plugin can be used in a couple ways.

First, the 'mrgadget-plugin.gradle' file can be applied directly:

	apply from: 'https://raw.github.com/moksamedia/mrgadget-gradle-plugin/master/mrgadget-plugin.gradle'
	

Another way is to add the 'jar-with-dependencies' to your buildscript classpath:

	apply plugin: 'mrgadget'

	buildscript {
	
		repositories {
			mavenCentral()
			add(new org.apache.ivy.plugins.resolver.URLResolver()) {
				name = 'mrgadget-plugin'
				basePath = 'https://raw.github.com/moksamedia/mrgadget-gradle-plugin/master/repo'
				addArtifactPattern "${basePath}/[organization]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]"
			}
		}
    
	  dependencies {
		classpath 'com_moksamedia:mrgadget-plugin:0.1.0:jar-with-dependencies' // the fat jar 
	  }

	}

And yet a third way would be to add the 'light' jar to your buildscript classpath and add the other dependencies to your buildscript:

	apply plugin: 'mrgadget'

	buildscript {

		repositories {
			mavenCentral()
			add(new org.apache.ivy.plugins.resolver.URLResolver()) {
				name = 'mrgadget-plugin'
				basePath = 'https://raw.github.com/moksamedia/mrgadget-gradle-plugin/master/repo'
				addArtifactPattern "${basePath}/[organization]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]"
			}
		}

	  dependencies {
		compile 'com.jcraft:jsch:0.1.48'	// JSch is the SSH java package
		compile 'org.jasypt:jasypt-acegisecurity:1.9.0'	// for encrypting the stored passwords
		classpath 'com_moksamedia:mrgadget-plugin:0.1.0' // the light jar
	  }
	
	}

# Usage

The plugin injects a number of methods into your project.

* initMrGadget() : can be called to initialize MrGadget before uploading a file or executing a command. If any action is attempted before this method has been called, it will be called automatically.

* execRemote() : executes a non-sudo command on a remote server. The most important param is the command, as a string. As in execRemote(command:'rm someFile')

* execRemoteSudo() : executes a sudo command on a remote server. Again, use the 'command' param to pass in the command to be executed.

* copyToRemoteSCP() : copies a local file to a remote server using SCP. A full path to a local file (localFile) and remote file (remoteFile) must be specified, such as copyToRemoteSCP(localFile:'/path/to/some/file.jar', remoteFile:'path/on/remote/server/file_deployed.jar')

* copyToRemoteSFTP() : the same as copyToRemoteSCP but uses the SFTP protocol instead of the SCP protocol.

* closeMrGadgetSession() : if multiple commands are going to be executed on the same server successively, the session can be left open to avoid having to reconnect for each command by passing in the param leaveSessionOpen:true. If this is done, the session must be closed manually using this method once all actions have finished.

* clearMrGadgetPasswords() : MrGadget can optionally store passwords for user and host login and sudo in an encrypted format in the user preferences. USE THIS FEATURE AT YOUR OWN RISK. NO GUARANTEE IS MADE REGARDING ITS SECURITY OR SAFETY. This command will erase all encrypted passwords from the system preferences and reset the encryption key (if an auto-generated key was used).

* getMrGadget() : returns the MrGadget instance, if any, currently initialized.

# Configuration / Params

