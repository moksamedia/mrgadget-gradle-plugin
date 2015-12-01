# Description

The MrGadget Gradle plugin is a wrapper for the MrGadget project that makes it really, really easy to use MrGadget in Gradle build scripts. MrGadget loves to do 4 things: 1) upload files to a remote server via SFTP, 2) upload files to a remote server via SCP, 3) execute commands on a remote server, and 4) execute SUDO commands on a remote server.

Passwords can be passed in as parameters. If they are not, MrGadget will use the System.console to prompt the user for the required passwords, and will ask if they should be stored in the system preferences. The stored passwords are encrypted, using either an auto-generated key or a passed in encryption key. DISCLAIMER: Although the system is safer than storing passwords in plain text in a config or properties file, an interested hacker could certainly defeat the security, SO USE AT YOUR OWN RISK. 

# Installation

The 'mrgadget-plugin.gradle' file can be applied directly:

	apply from: 'https://raw.github.com/moksamedia/mrgadget-gradle-plugin/master/mrgadget-plugin.gradle'
	

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

		// REQUIRED (but could be set as params to method calls as well)

		user = 'someUser'  // the username used to log into the server
		host = '123.23.1.15'  // the ip address of host of the remote server
	
		// OPTIONAL
	
		prefsEncryptionKey = "some-random-chars"// string key used to encrypt any stored passwords
		password = "mypass" // can directly pass in the password, if you like
		sudoPassword = "mySudoPass" // if sudo password is different than login password, can pass it in also
		privateKey = "/some/path/to/key.pem/ // can use private key auth instead of password auth

		logProgressGranularity = 10 // use this to set the frequency, in percentage, that MrGadget log.info's file transfer
								    // progress (less = more reporting, value should be an integer between 0 and 100)
								
		leaveSessionOpen = false // set this to true if you're gonna execute a few commands in a row, but don't forget to close session
		
		promptToSavePass = true  // set to false and MrGadget won't ask you if you'd like to save your password
		
		strictHostKeyChecking = true  // set to false if you want to bypass strict host key checking
		
		showProgressDialog = true  // set to false to suppress showing the Swing progress dialog box during file transfers (useful
								   // for slow connections or BIG files, but not worth it for quick transfers)
		
		preserveTimeStamp = false  // set to true if you want MrGadget to set the timestamp of the remote file to the modified time of
						 		   // the local file

		// These can be used to change the names of the injected execution methods, in case they 
		// happen to conflict with something

		execRemoteMethodName = "execRemote"
		execRemoteSudoMethodName = "execRemoteSudo"
		copyToRemoteSCPMethodName = "copyToRemoteSCP"
		copyToRemoteSFTPMethodName = "copyToRemoteSFTP"
		
	}
	
# Example Usage

	/*
	 * Simplest case. User and host are passed in with command. Sesson will be closed upon
	 * completion. MrGadget will prompt for password.
	 */

	task doit << {

		execRemote(user:'myusername', host:'www.howdy.com', command:'ls -al')

	}


	/*
	 * Slightly less simple case. User and host are set in configuration block. They will
	 * be used for all commands in script unless changed or overridden.
	 */

	mrgadget {
		user: 'myusername'
		host: 'www.howdy.com'
	}

	task doit2 << {
	
			execRemote(command:'ls -al')
		
			// can override settings made in config block (for this call only)
			execRemote(user:'adifferentuser', host:'newhost.org', command:'rm somefile.txt') 
	
	}


	/*
	 * Executing multple commands without reconnecting.
	 */
	
	mrgadget {
		user = 'andrewhughes'
		host = 'www.thepathis.com'
		strictHostKeyChecking = false
		showProgressDialog = false  // don't show the progress dialog box
	}

	task deployRemote << {
	
		initMrGadget(leaveSessionOpen:true)

		logger.info "SENDING FILE"
		copyToRemoteSFTP(localFile:"$rootDir/build/libs/myproj.war", remoteFile:"/webapps/myproj_released.war")
	
		logger.info "SETTING OWNER OF WAR"
		execRemoteSudo("chown tomcat6:tomcat6 /webapps/myproj_released.war")
	
		logger.info "SETTING PERMISSION OF WAR"
		execRemoteSudo("chmod 775 /webapps/myproj_released.war")
	
		logger.info "RESTARTING TOMCAT"
		execRemoteSudo("service tomcat6 restart")
			
		logger.info "FINISHED"
		closeMrGadgetSession()
	
	}
		
