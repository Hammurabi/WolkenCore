# import requests to make our lives easier
import requests

# define a 'connection' class to hold all the connection information
class rpc_connection:
    def __init__(self):
        self.ip      = 'localhost'
        self.port    = '12560'
        self.token   = ''
    def sendRequest(request, arguments):
        url = self.ip + ":" + self.port + "/" + query
        return toJson(requests.get(url, allow_redirects=True))