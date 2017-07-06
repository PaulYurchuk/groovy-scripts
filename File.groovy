/**
 * Created by USER on 05.07.2017.
 */
//CliBuilder cli = new CliBuilder(
//        usage: 'groovy File.groovy -u {user} -p {password} -d {path to content} -n {repoName} [-h {nx3Url}]')
//cli.with {
//    u longOpt: 'username', args: 1, required: true, 'User with permissions to deploy to the target repo'
//   p longOpt: 'password', args: 1, required: true, 'Password for user'
//   r longOpt: 'repository', args: 1, required: true, 'Name of raw repository to deploy to, strict content validation is suggested to be turned off.'
//    d longOpt: 'directory', args: 1, required: true, 'Path of file to dploy'
//    h longOpt: 'host', args: 1, 'Nexus Repository Manager 3 host url (including port if necessary). Defaults to http://nexus/repository/Artifact_storage/'
//}
//def options = cli.parse(args)
//if (!options) {
//    return
//}


def command1 = "curl  -v  --user 'jenkinsnexus:jenkins' " +
        "--upload-file  helloworld-$BUILD_NUMBER.tar.gz   " +
        "http://nexus/repository/Artifact_storage/helloworld-$BUILD_NUMBER.tar.gz"
println command1
def proc1 = command1.execute()
proc1.waitFor()

println "return code: ${proc1.exitValue()}"
println "stderr: ${proc1.err.text}"
println "stdout: ${proc1.in.text}"


def command2 = '''/usr/bin/ssh -p vagrant vagrant@192.168.56.26 bash -s <<-SHELL

wget http://jenkinsnexus:jenkins@nexus/repository/Artifact_storage/$PRNUMBER
    tar -xzf $PRNUMBER
    rm $PRNUMBER
    cd /opt/jboss/wildfly-10.1.0.Final/standalone/deployments/
    if [ -f "helloworld.war" ]
    then
    mv helloworld.war /tmp/helloworld.war.old-$BUILD_NUMBER
    fi
    mv /root/helloworld.war /opt/jboss/wildfly-10.1.0.Final/standalone/deployments/helloworld.war
        SHELL '''
println command2
def proc2 = command2.execute()
proc1.waitFor()
println "return code: ${proc2.exitValue()}"
println "stderr: ${proc2.err.text}"
println "stdout: ${proc2.in.text}"
