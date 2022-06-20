# Sample on how to get web content with Python. Currently pings a service that doesn't exist (this is from a previous project)
# but it shows how to do it and is good enough reference / future-adaptable code

import requests
import json

x = requests.get('http://192.168.4.29:8080/energy/diff/list:100,1')

stats = json.loads(x.content)

print(json.dumps(stats))
