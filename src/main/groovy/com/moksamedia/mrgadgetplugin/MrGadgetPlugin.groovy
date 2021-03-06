package com.moksamedia.mrgadgetplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.moksamedia.mrgadget.MrGadget


class MrGadgetPlugin implements Plugin<Project> {

	MrGadget mrg

	void apply(Project project) {

		project.extensions.mrgadget =  new MrGadgetPluginExtension()

		project.metaClass.initMrGadget = { def params = [:] ->
			def toUse = project.extensions.mrgadget.properties
			toUse.putAll(params)
			toUse.remove('metaClass') // not strictly necessary, but why not?
			toUse.remove('class')
			mrg?.closeSession()
			mrg = new MrGadget(toUse)
		}

		project.metaClass."$project.extensions.mrgadget.execRemoteMethodName" = { def params ->
						
			// if we pass in a string
			if (params instanceof String || params instanceof GString) {
				
				if (mrg==null) {
					initMrGadget() // init with params defined in extension
				}
				
				mrg.execRemote(params)
			}
			else {

				if (mrg==null) initMrGadget(params)
				else mrg.setParams(params)
				
				assert params.command != null
				
				mrg.execRemote(params.command)
				
			}
			
		}

		project.metaClass."$project.extensions.mrgadget.execRemoteSudoMethodName" = { def params ->
						
			if (params instanceof String || params instanceof GString) {
				if (mrg==null) {
					initMrGadget()
				}
				
				mrg.execRemoteSudo(params)
			}
			else {

				if (mrg==null) initMrGadget(params)
				else mrg.setParams(params)
				
				assert params.command != null
				
				mrg.execRemoteSudo(params.command)
				
			}
			
		}

		project.metaClass."$project.extensions.mrgadget.copyToRemoteSCPMethodName" = { def params = [:] ->
			if (mrg==null) initMrGadget(params)
			else mrg.setParams(params)
			mrg.copyToRemoteSCP([localFile:params.localFile, remoteFile:params.remoteFile])
		}

		project.metaClass."$project.extensions.mrgadget.copyToRemoteSFTPMethodName" = { def params = [:] ->
			if (mrg==null) initMrGadget(params)
			else mrg.setParams(params)
			mrg.copyToRemoteSFTP([localFile:params.localFile, remoteFile:params.remoteFile])
		}

		project.metaClass."$project.extensions.mrgadget.copyToRemoteRSYNCMethodName" = { def params = [:] ->
			if (mrg==null) initMrGadget(params)
			else mrg.setParams(params)
			mrg.copyToRemoteRSYNC(params)
		}
		
		project.metaClass.closeMrGadgetSession = {
			mrg?.closeSession()
		}

		project.metaClass.clearMrGadgetPasswords = {
			if (mrg==null) initMrGadget(clearAllPasswords:true)
			else mrg.clearAllPasswords()
		}

		project.metaClass.getMrGadget = {
			mrg
		}

	}

}

class MrGadgetPluginExtension {

	def String user
	def String host

	def String prefEncryptionKey = null
	def String password = null
	def String sudoPassword = null
	def String privateKey = null

	def int logProgressGranularity = 10

	def boolean leaveSessionOpen = false
	def boolean sudoPassDifferent = false
	def boolean promptToSavePass = true
	def boolean strictHostKeyChecking = true
	def boolean showProgressDialog = true
	def boolean preserveTimestamp = false

	def String execRemoteMethodName = "execRemote"
	def String execRemoteSudoMethodName = "execRemoteSudo"
	def String copyToRemoteSCPMethodName = "copyToRemoteSCP"
	def String copyToRemoteSFTPMethodName = "copyToRemoteSFTP"
	def String copyToRemoteRSYNCMethodName = "copyToRemoteRSYNC"
}