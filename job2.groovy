def git = "MNT-Lab/groovy-scripts"
def repo = "yshchanouski"

job("MNT-CD-module9-extcreated-job") {
    parameters {
        stringParam('BRANCH_NAME', repo)
    }
    scm {
	git {
		remote {
		        github(git, '$BRANCH_NAME')
		}
		extensions {
			cleanBeforeCheckout()
		}
	}
    }
    triggers {
        scm('H/5 * * * *')
    }
    steps {
	shell('echo "Hello"')
    }
    
}

