import requests
import json

x = requests.get('http://192.168.4.29:8080/energy/diff/list:100,1')

stats = json.loads(x.content)

print(json.dumps(stats))
