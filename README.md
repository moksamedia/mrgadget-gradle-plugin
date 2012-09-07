The MrGadget Gradle plugin is a wrapper for the MrGadget project that makes it really, really easy to use MrGadget in Gradle build scripts. MrGadget loves to do 4 things: 1) upload files to a remote server via SFTP, 2) upload files to a remote server via SCP, 3) execute commands on a remote server, and 4) execute SUDO commands on a remote server.

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


