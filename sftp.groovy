@Grab(group='com.jcraft', module='jsch', version='0.1.46')
import com.jcraft.jsch.*
    
CliBuilder cli = new CliBuilder(
    usage: 'groovy sftp.groovy -a {ARTIFACT_NAME} -p {WORKSPACE}') 
cli.with {
  a longOpt: 'ARTIFACT_NMAE', args: 1, required: true, 'Artifact_name from job Jenkins'
  p longOpt: 'WORKSPACE', args: 1, required: true, 'WORKSPACE from job Jenkins'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def  ARTIFACT_NAME= options.a
def WORKSPACE= options.p

java.util.Properties config = new java.util.Properties()
config.put "StrictHostKeyChecking", "no"
JSch ssh = new JSch()
Session sess = ssh.getSession "root", "192.168.50.12", 22
sess.with {
    setConfig config
    setPassword "vagrant"
    connect()
    Channel chan = openChannel "sftp"
    chan.connect()
    ChannelSftp sftp = (ChannelSftp) chan;
    def sessionsFile = new File("${WORKSPACE}/${ARTIFACT_NAME}")
    sessionsFile.withInputStream { istream -> sftp.put(istream, "/home/artifact.tar.gz") }
    chan.disconnect()
    disconnect()
}
