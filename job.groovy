//job.groovy
import jenkins.model.Jenkins
import hudson.model.FreeStyleProject
import hudson.tasks.Shell
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.GitSCM
import hudson.triggers.SCMTrigger


jobName = 'MNT-CD-module9-extcreated-job'
if (args.length != 1 ) {
	println "No argument found"
}
def BRANCH_NAME=args[0]

jenkins = Jenkins.instance

if(jenkins.items.find{it.name == jobName})
	jenkins.items.find{it.name == jobName}.delete()

job = jenkins.createProject(FreeStyleProject, jobName)

gitTrigger = new SCMTrigger("*/30 * * * *")
job.scm = new GitSCM("https://github.com/MNT-Lab/groovy-scripts.git")
job.scm.branches = [new BranchSpec("*/${args[0]}")]

job.addTrigger(gitTrigger);
job.createTransientActions();

job.save()
