# Description

The MrGadget Gradle plugin is a wrapper for the MrGadget project that makes it really, really easy to use MrGadget in Gradle build scripts. MrGadget loves to do 4 things: 1) upload files to a remote server via SFTP, 2) upload files to a remote server via SCP, 3) execute commands on a remote server, and 4) execute SUDO commands on a remote server.

Passwords can be passed in as parameters. If they are not, MrGadget will use the System.console to prompt the user for the required passwords, and will ask if they should be stored in the system preferences. The stored passwords are encrypted, using either an auto-generated key or a passed in encryption key. DISCLAIMER: Although the system is safer than storing passwords in plain text in a config or properties file, an interested hacker could certainly defeat the security, SO USE AT YOUR OWN RISK. 

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

* initMrGadget() : can be called to initialize MrGadget before uploading a file or executing a command. If any action is attempted before this method has been called, it will be called automatically. Both user and host must be set before any action can be executed. For example, you could say: initMrGadget(user:'someUser', host:'www.awesome.com'); execRemote('ls'). Or: execRemote(user:'someUser', host:'www.awesome.com', command:'ls') But if you tried the execRemote('ls') call without setting a user and host, bad things will happen.

* execRemote() : executes a non-sudo command on a remote server. The most important param is the command, as a string. As in execRemote(command:'rm someFile')

* execRemoteSudo() : executes a sudo command on a remote server. Again, use the 'command' param to pass in the command to be executed.

* copyToRemoteSCP() : copies a local file to a remote server using SCP. A full path to a local file (localFile) and remote file (remoteFile) must be specified, such as copyToRemoteSCP(localFile:'/path/to/some/file.jar', remoteFile:'path/on/remote/server/file_deployed.jar')

* copyToRemoteSFTP() : the same as copyToRemoteSCP but uses the SFTP protocol instead of the SCP protocol.

* closeMrGadgetSession() : if multiple commands are going to be executed on the same server successively, the session can be left open to avoid having to reconnect for each command by passing in the param leaveSessionOpen:true. If this is done, the session must be closed manually using this method once all actions have finished.

* clearMrGadgetPasswords() : MrGadget can optionally store passwords for user and host login and sudo in an encrypted format in the user preferences. USE THIS FEATURE AT YOUR OWN RISK. NO GUARANTEE IS MADE REGARDING ITS SECURITY OR SAFETY. This command will erase all encrypted passwords from the system preferences and reset the encryption key (if an auto-generated key was used).

* getMrGadget() : returns the MrGadget instance, if any, currently initialized.

# Configuration / Params

MrGadget can be configured via an extension block in your buildscript. These values are passed in to the MrGadget instance on the execution of every command; however, any values explicitly sent in with the method invocation will override values set in the extension block.

mrgadget {

	user = 'someUser'  // the username used to log into the server
	host = '123.23.1.15'  // the ip address of host of the remote server
	
	prefsEncryptionKey = "some-random-chars"// optional: string key used to encrypt any stored passwords
	password = "mypass" // optional: can directly pass in the password, if you like
	sudoPassword = "mySudoPass" // optional: // if sudo password is different than login password, can pass it in also

	logProgressGranularity = 20 // optional: use this to set the frequency, in percentage, that MrGadget log.info's file transfer progress (less = more reporting, value should be an integer between 0 and 100)
}