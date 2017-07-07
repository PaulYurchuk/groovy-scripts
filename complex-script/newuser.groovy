import groovy.json.JsonOutput

// Create new admin user
//
def deploy = ['nx-deploy']
def Alex = security.addUser('AlexK', 'Alex', 'K', 'aliaksei_k@epam.com', true, 'ak123', deploy)
log.info('User jane.doe created')

