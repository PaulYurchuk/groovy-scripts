def url = 'http://www.google.com/images/logo.gif'
def file = new File('google_logo.gif').newOutputStream()
file << new URL(url).openStream()
file.close()